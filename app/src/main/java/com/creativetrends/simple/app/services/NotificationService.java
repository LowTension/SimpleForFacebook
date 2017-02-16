package com.creativetrends.simple.app.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.WearableExtender;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.activities.MainActivity;
import com.creativetrends.simple.app.activities.MessagesActivity;
import com.creativetrends.simple.app.activities.SimpleApp;
import com.creativetrends.simple.app.utils.Cleaner;
import com.creativetrends.simple.app.utils.ThemeUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Date;

public class NotificationService extends Service {


    private static final String BASE_URL = "https://mobile.facebook.com";
    private static final String NOTIFICATIONS_URL = "https://m.facebook.com/notifications.php";
    private static final String MESSAGES_URL = "https://m.facebook.com/messages";
    private static final String MESSAGES_URL_BACKUP = "https://mobile.facebook.com/messages";
    private static final String NOTIFICATION_OLD_MESSAGE_URL = "https://m.facebook.com/messages#";
    private static final int MAX_RETRY = 3;
    private static final int JSOUP_TIMEOUT = 10000;
    private static final String TAG;
    private static Runnable runnable;
    private static String userAgent;
    boolean isConnectedMobile;
    static {
        TAG = NotificationService.class.getSimpleName();
    }



    private static Boolean syncMessages = false;
    private static Boolean syncNotifications = false;

    private final HandlerThread handlerThread;
    private final Handler handler;
    private volatile boolean shouldContinue = true;
    private SharedPreferences preferences;
    static long time = new Date().getTime();
    static String tmpStr = String.valueOf(time);
    static String last4Str = tmpStr.substring(tmpStr.length() -1);
    static int notificationId = Integer.valueOf(last4Str);


    public NotificationService() {
        handlerThread = new HandlerThread("Handler Thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public static void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager)
                SimpleApp.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    public static void clearMessages() {
        NotificationManager notificationManager = (NotificationManager)
                SimpleApp.getContextOfApplication().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeUtils.setNotificationColor(this);
        isConnectedMobile = NetworkConnection.isConnectedMobile(getApplicationContext());
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        runnable = new HandlerRunnable();
        if (isConnectedMobile) {
            handler.postDelayed(runnable, 15000);
        }else {
            handler.postDelayed(runnable, 3000);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!(syncMessages || syncNotifications)) {
            runnable = new HandlerRunnable();
            handler.post(runnable);
        }
        return START_STICKY;
    }

    public boolean stopService(Intent name) {
        syncNotifications = false;
        syncMessages = false;
        return super.stopService(name);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        syncMessages = false;
        syncNotifications = false;
        synchronized (handler) {
            shouldContinue = false;
            handler.notify();
        }

        handler.removeCallbacksAndMessages(null);
        handlerThread.quit();
    }

    @SuppressWarnings("deprecation")
    private void syncCookies() {
        if (Build.VERSION.SDK_INT < 21) {
            CookieSyncManager.createInstance(getApplicationContext());
            CookieSyncManager.getInstance().sync();
        }
    }

    private void restartItself() {
        final Context context = SimpleApp.getContextOfApplication();
        final Intent intent = new Intent(context, NotificationService.class);
        context.stopService(intent);
        context.startService(intent);
    }

    @SuppressWarnings("deprecation")
    private void notifier(String title, String url, boolean isMessage, Bitmap picture) {
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        final String contentTitle;
        if (isMessage)
            contentTitle = getString(R.string.app_name_pro);
        else
            contentTitle = getString(R.string.app_name_pro);


        Log.i(TAG, "Start notification - isMessage: " + isMessage);

        Intent actionIntent = new Intent(this, MainActivity.class);
        actionIntent.putExtra("start_url", "https://m.facebook.com/notifications");
        actionIntent.setAction("NOTIFICATION_URL_ACTION");
        PendingIntent actionPendingIntent = PendingIntent.getActivity(this, notificationId, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent messageIntent = new Intent(this, MainActivity.class);
        messageIntent.putExtra("start_url", "https://m.facebook.com/messages");
        messageIntent.setAction("NOTIFICATION_URL_ACTION");
        PendingIntent messagePendingIntent = PendingIntent.getActivity(this, 1, messageIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.drawable.ic_notify_tabs, getString(R.string.app_name), actionPendingIntent)
                .build();

        NotificationCompat.Action message = new NotificationCompat.Action.Builder(R.drawable.ic_mess_tabs, getString(R.string.app_name), messagePendingIntent)
                .build();


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setColor(ThemeUtils.getColorPrimary(this))
                .setContentTitle(contentTitle)
                .setContentText(title)
                .setTicker(title)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true);


        if (!isMessage && picture != null){
            mBuilder.setLargeIcon(NotificationService.getCircleBitmap(picture));
        }else{
            mBuilder.setLargeIcon(bm);
        }





        String ringtoneKey = "ringtone";
        if (isMessage)
            ringtoneKey = "ringtone_msg";

        Uri ringtoneUri = Uri.parse(preferences.getString(ringtoneKey, "content://settings/system/notification_sound"));
        mBuilder.setSound(ringtoneUri);


        if (preferences.getBoolean("vibrate", false))
            mBuilder.setVibrate(new long[]{500, 500});
        else
            mBuilder.setVibrate(new long[]{0L});


        if (preferences.getBoolean("led_light", false)) {
            Resources resources = getResources(), systemResources = Resources.getSystem();
            mBuilder.setLights(Color.CYAN,
                    resources.getInteger(systemResources.getIdentifier("config_defaultNotificationLedOn", "integer", "android")),
                    resources.getInteger(systemResources.getIdentifier("config_defaultNotificationLedOff", "integer", "android")));

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            mBuilder.setPriority(Notification.PRIORITY_HIGH);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (isMessage) {
            Intent intent = new Intent(this, MessagesActivity.class);
            intent.putExtra("start_url", url);
            intent.setAction("NOTIFICATION_URL_ACTION");
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.extend(new WearableExtender().addAction(message));
            mBuilder.setOngoing(false);
            mBuilder.setSmallIcon(R.drawable.ic_mess_tabs);
            mBuilder.setOnlyAlertOnce(true);
            Notification note = mBuilder.build();
            mNotificationManager.notify(1, note);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("start_url", "https://mobile.facebook.com/notifications");
            intent.setAction("NOTIFICATION_URL_ACTION");
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(), notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.extend(new WearableExtender().addAction(action));
            mBuilder.setOngoing(false);
            mBuilder.setSmallIcon(R.drawable.ic_notify_tabs);
            Notification note = mBuilder.build();
            mNotificationManager.notify(notificationId, note);


            if (preferences.getBoolean("led_light", false))
                note.flags |= Notification.FLAG_SHOW_LIGHTS;
        }

    }


    private class HandlerRunnable implements Runnable {

        public void run() {
            int timeInterval;
            try {
                if (isConnectedMobile && preferences.getBoolean("data_reduce", false)) {
                    timeInterval = Integer.parseInt(preferences.getString("interval_pref_twitter", "10800000"));
                } else {
                    timeInterval = Integer.parseInt(preferences.getString("interval_pref", "1800000"));
                }
                Log.i(TAG, "Time interval: " + (timeInterval / 1000) + " seconds");


                final long now = System.currentTimeMillis();
                final long sinceLastCheck = now - preferences.getLong("last_check", now);
                final boolean ntfLastStatus = preferences.getBoolean("ntf_last_status", false);
                final boolean msgLastStatus = preferences.getBoolean("msg_last_status", false);

                if ((sinceLastCheck < timeInterval) && ntfLastStatus && msgLastStatus) {
                    final long waitTime = timeInterval - sinceLastCheck;
                    if (waitTime >= 10000) {
                        Log.i(TAG, "I'm going to wait. Resuming in: " + (waitTime / 10000) + " seconds");

                        synchronized (handler) {
                            try {
                                handler.wait(waitTime);
                            } catch (InterruptedException ex) {
                                Log.i(TAG, "Thread interrupted");
                            } finally {
                                Log.i(TAG, "Lock is now released");
                            }
                        }

                    }
                }


                if (shouldContinue) {

                    if (NetworkConnection.isConnected(getApplicationContext())) {
                        Log.i(TAG, "Internet connection active. Starting AsyncTask...");
                        String connectionType = "Wi-Fi";
                        if (NetworkConnection.isConnectedMobile(getApplicationContext()))
                            connectionType = "Mobile";
                        Log.i(TAG, "Connection Type: " + connectionType);
                        userAgent = preferences.getString("webview_user_agent", System.getProperty("http.agent"));
                        Log.i(TAG, "User Agent: " + userAgent);

                        if (preferences.getBoolean("notifications_activated", false) &&!syncNotifications) {
                            new CheckNotificationsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                        }
                        if (preferences.getBoolean("messages_activated", false)&&!syncMessages) {
                            new CheckMessagesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
                        }
                        preferences.edit().putLong("last_check", System.currentTimeMillis()).apply();
                    } else
                        Log.i(TAG, "No internet connection. Skip checking.");


                    handler.postDelayed(runnable, timeInterval);
                } else
                    Log.i(TAG, "Notified to stop running. Exiting...");

            } catch (RuntimeException re) {
                Log.i(TAG, "RuntimeException caught");
                restartItself();
            }
        }

    }



    /**
     * Notifications checker task: it checks Facebook notifications only.
     */
    private class CheckNotificationsTask extends AsyncTask<Void, Void, Element> {

        boolean syncProblemOccurred = false;

        private Element getElement(String connectUrl) {
            try {
                return Jsoup.connect(connectUrl)
                        .userAgent(userAgent).timeout(JSOUP_TIMEOUT)
                        .cookie("https://mobile.facebook.com", CookieManager.getInstance().getCookie("https://mobile.facebook.com"))
                        .get()
                        .select("div.touchable-notification")
                        .not("a._19no")
                        .not("a.button")
                        .first();
            } catch (IllegalArgumentException ex) {
                Log.i("CheckNotificationsTask", "Cookie sync problem occurred");
                if (!syncProblemOccurred) {

                    syncProblemOccurred = true;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected Element doInBackground(Void... params) {
            Element result = null;
            int tries = 0;

            syncCookies();

            while (tries++ < MAX_RETRY && result == null) {
                Log.i("CheckNotificationsTask", "doInBackground: Processing... Trial: " + tries);
                Log.i("CheckNotificationsTask", "Trying: " + NOTIFICATIONS_URL);
                Element notification = getElement(NOTIFICATIONS_URL);
                if (notification != null)
                    result = notification;
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Element result) {
            syncNotifications = false;
            try {
                if (result == null)
                    return;
                if (result.text() == null)
                    return;

                final String time = result.select("span.mfss.fcg").text();
                final String text = result.text().replace(time, "");
                final String pictureStyle = result.select("i.img.l.profpic").attr("style");

                if (!preferences.getBoolean("activity_visible", false)){
                    if (!preferences.getString("last_notification_text", "").equals(text)) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground (Void[] params){
                                Bitmap picture = Cleaner.getBitmapFromURL(Cleaner.extractUrl(pictureStyle));
                                notifier(text, BASE_URL + result.attr("href"), false, picture);
                                return null;
                            }
                        }.execute();

                    }
                }


                preferences.edit().putString("last_notification_text", text).apply();

                preferences.edit().putBoolean("ntf_last_status", true).apply();
                Log.i("CheckNotificationsTask", "onPostExecute: Aight biatch ;)");
            } catch (Exception ex) {
                // save this check status
                preferences.edit().putBoolean("ntf_last_status", false).apply();
                Log.i("CheckNotificationsTask", "onPostExecute: Failure");
            }
        }

    }

    /**
     * Messages checker task: it checks new messages only.
     */
    /** Messages checker task: it checks new messages only. */
    private class CheckMessagesTask extends AsyncTask<Void, Void, String> {

        boolean syncProblemOccurred = false;

        private String getNumber(String connectUrl) {
            try {
                Elements message = Jsoup.connect(connectUrl)
                        .userAgent(userAgent)
                        .timeout(JSOUP_TIMEOUT)
                        .cookie("https://m.facebook.com", CookieManager.getInstance().getCookie("https://m.facebook.com"))
                        .get()
                        .select("div#viewport").select("div#page").select("div._129-")
                        .select("#messages_jewel").select("span._59tg");

                return message.html();
            } catch (IllegalArgumentException ex) {
                Log.i("CheckMessagesTask", "Cookie sync problem occurred");
                if (!syncProblemOccurred) {
                    syncProblemOccurred = true;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return "failure";
        }

        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            int tries = 0;

            // sync cookies to get the right data
            syncCookies();

            while (tries++ < MAX_RETRY && result == null) {
                Log.i("CheckMessagesTask", "doInBackground: Processing... Trial: " + tries);

                // try to generate rss feed address
                Log.i("CheckMsgTask:getNumber", "Trying: " + MESSAGES_URL);
                String number = getNumber(MESSAGES_URL);
                if (!number.matches("^[+-]?\\d+$")) {
                    Log.i("CheckMsgTask:getNumber", "Trying: " + MESSAGES_URL_BACKUP);
                    number = getNumber(MESSAGES_URL_BACKUP);
                }
                if (number.matches("^[+-]?\\d+$"))
                    result = number;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // parse a number of unread messages
                int newMessages = Integer.parseInt(result);

                if (!preferences.getBoolean("activity_visible", false) || preferences.getBoolean("notifications_everywhere", true)) {
                    if (newMessages == 1)
                        notifier(getString(R.string.you_have_one_message), NOTIFICATION_OLD_MESSAGE_URL, true, null);
                    else if (newMessages > 1)
                        notifier(String.format(getString(R.string.you_have_n_messages), newMessages), NOTIFICATION_OLD_MESSAGE_URL, true, null);
                }

                // save this check status
                preferences.edit().putBoolean("msg_last_status", true).apply();
                Log.i("CheckMessagesTask", "onPostExecute: Aight biatch ;)");
            } catch (NumberFormatException ex) {
                // save this check status
                preferences.edit().putBoolean("msg_last_status", false).apply();
                Log.i("CheckMessagesTask", "onPostExecute: Failure");
            }
        }

    }


    static private Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.WHITE;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }
}