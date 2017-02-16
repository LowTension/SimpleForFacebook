package com.creativetrends.simple.app.utils;// Created by Creative Trends Apps on 8/29/2016.

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.webkit.WebView;

import com.creativetrends.simple.app.activities.MainActivity;
import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.services.NetworkConnection;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ThemeUtils {
    public WebView webview;

    @SuppressWarnings("ConstantConditions")
    public static void setAppColor(Context context, Activity activity) {
        try{
            boolean FacebookBlue = PreferencesUtility.getInstance(context).getTheme().equals("facebookblue");
            boolean MaterialPink = PreferencesUtility.getInstance(context).getTheme().equals("pinkdark");
            boolean DeepPurple = PreferencesUtility.getInstance(context).getTheme().equals("deeppurpledark");
            boolean NightMode = PreferencesUtility.getInstance(context).getTheme().equals("darktheme");
            boolean DeepOrange = PreferencesUtility.getInstance(context).getTheme().equals("deeporangedark");
            boolean Falcon = PreferencesUtility.getInstance(context).getTheme().equals("falcondark");
            boolean DarkGreen = PreferencesUtility.getInstance(context).getTheme().equals("greendark");
            boolean LightGreen = PreferencesUtility.getInstance(context).getTheme().equals("lightgreendark");
            boolean Amber = PreferencesUtility.getInstance(context).getTheme().equals("amberdark");
            boolean Red = PreferencesUtility.getInstance(context).getTheme().equals("reddark");
            boolean JorellBlue = PreferencesUtility.getInstance(context).getTheme().equals("googlebluedark");
            boolean Cyan = PreferencesUtility.getInstance(context).getTheme().equals("cyandark");
            boolean MaterialDark = PreferencesUtility.getInstance(context).getTheme().equals("bluegreydark");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean("auto_night", false) && isNightTime(activity)) {
                context.setTheme(R.style.NightMode);
            } else {
                if (NightMode)
                    context.setTheme(R.style.NightMode);
                if (FacebookBlue)
                    context.setTheme(R.style.FacebookBlue);
                if (MaterialPink)
                    context.setTheme(R.style.MaterialPink);
                if (DeepPurple)
                    context.setTheme(R.style.DeepPurple);
                if (DeepOrange)
                    context.setTheme(R.style.DeepOrange);
                if (Falcon)
                    context.setTheme(R.style.Falcon);
                if (DarkGreen)
                    context.setTheme(R.style.DarkGreen);
                if (LightGreen)
                    context.setTheme(R.style.LightGreen);
                if (Amber)
                    context.setTheme(R.style.Amber);
                if (Red)
                    context.setTheme(R.style.Red);
                if (JorellBlue)
                    context.setTheme(R.style.JorellBlue);
                if (Cyan)
                    context.setTheme(R.style.Cyan);
                if (MaterialDark)
                    context.setTheme(R.style.MaterialDark);
            }
        } catch (Exception ignored) {

        }
    }


    @SuppressWarnings("ConstantConditions")
    public static void setSettingsTheme(Context context, Activity activity) {
        try{
            boolean FacebookBlue = PreferencesUtility.getInstance(context).getTheme().equals("facebookblue");
            boolean MaterialPink = PreferencesUtility.getInstance(context).getTheme().equals("pinkdark");
            boolean DeepPurple = PreferencesUtility.getInstance(context).getTheme().equals("deeppurpledark");
            boolean NightMode = PreferencesUtility.getInstance(context).getTheme().equals("darktheme");
            boolean DeepOrange = PreferencesUtility.getInstance(context).getTheme().equals("deeporangedark");
            boolean Falcon = PreferencesUtility.getInstance(context).getTheme().equals("falcondark");
            boolean DarkGreen = PreferencesUtility.getInstance(context).getTheme().equals("greendark");
            boolean LightGreen = PreferencesUtility.getInstance(context).getTheme().equals("lightgreendark");
            boolean Amber = PreferencesUtility.getInstance(context).getTheme().equals("amberdark");
            boolean Red = PreferencesUtility.getInstance(context).getTheme().equals("reddark");
            boolean JorellBlue = PreferencesUtility.getInstance(context).getTheme().equals("googlebluedark");
            boolean Cyan = PreferencesUtility.getInstance(context).getTheme().equals("cyandark");
            boolean MaterialDark = PreferencesUtility.getInstance(context).getTheme().equals("bluegreydark");
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean("auto_night", false) && isNightTime(activity)) {
                context.setTheme(R.style.NightModeSettings);
            } else {
                if (NightMode)
                    context.setTheme(R.style.NightModeSettings);
                if (FacebookBlue)
                    context.setTheme(R.style.FacebookBlueSettings);
                if (MaterialPink)
                    context.setTheme(R.style.MaterialPinkSettings);
                if (DeepPurple)
                    context.setTheme(R.style.DeepPurpleSettings);
                if (DeepOrange)
                    context.setTheme(R.style.DeepOrangeSettings);
                if (Falcon)
                    context.setTheme(R.style.FalconSettings);
                if (DarkGreen)
                    context.setTheme(R.style.DarkGreenSettings);
                if (LightGreen)
                    context.setTheme(R.style.LightGreenSettings);
                if (Amber)
                    context.setTheme(R.style.AmberSettings);
                if (Red)
                    context.setTheme(R.style.RedSettings);
                if (JorellBlue)
                    context.setTheme(R.style.JorellBlueSettings);
                if (Cyan)
                    context.setTheme(R.style.CyanSettings);
                if (MaterialDark)
                    context.setTheme(R.style.MaterialDarkSettings);
            }
        } catch (Exception ignored) {

        }
    }


    public static void setNotificationColor(Context context) {
        boolean FacebookBlue = PreferencesUtility.getInstance(context).getTheme().equals("facebookblue");
        boolean MaterialPink = PreferencesUtility.getInstance(context).getTheme().equals("pinkdark");
        boolean DeepPurple = PreferencesUtility.getInstance(context).getTheme().equals("deeppurpledark");
        boolean NightMode = PreferencesUtility.getInstance(context).getTheme().equals("darktheme");
        boolean DeepOrange = PreferencesUtility.getInstance(context).getTheme().equals("deeporangedark");
        boolean Falcon = PreferencesUtility.getInstance(context).getTheme().equals("falcondark");
        boolean DarkGreen = PreferencesUtility.getInstance(context).getTheme().equals("greendark");
        boolean LightGreen = PreferencesUtility.getInstance(context).getTheme().equals("lightgreendark");
        boolean Amber = PreferencesUtility.getInstance(context).getTheme().equals("amberdark");
        boolean Red = PreferencesUtility.getInstance(context).getTheme().equals("reddark");
        boolean JorellBlue = PreferencesUtility.getInstance(context).getTheme().equals("googlebluedark");
        boolean Cyan = PreferencesUtility.getInstance(context).getTheme().equals("cyandark");
        boolean MaterialDark = PreferencesUtility.getInstance(context).getTheme().equals("bluegreydark");
        if (NightMode)
            context.setTheme(R.style.NightModeSettings);
        if (FacebookBlue)
            context.setTheme(R.style.FacebookBlueSettings);

        if (MaterialPink)
            context.setTheme(R.style.MaterialPinkSettings);
        if (DeepPurple)
            context.setTheme(R.style.DeepPurpleSettings);
        if (DeepOrange)
            context.setTheme(R.style.DeepOrangeSettings);
        if (Falcon)
            context.setTheme(R.style.FalconSettings);
        if (DarkGreen)
            context.setTheme(R.style.DarkGreenSettings);
        if (LightGreen)
            context.setTheme(R.style.LightGreenSettings);
        if (Amber)
            context.setTheme(R.style.AmberSettings);
        if (Red)
            context.setTheme(R.style.RedSettings);
        if (JorellBlue)
            context.setTheme(R.style.JorellBlueSettings);
        if (Cyan)
            context.setTheme(R.style.CyanSettings);
        if (MaterialDark)
            context.setTheme(R.style.MaterialDarkSettings);
    }






    public static int getColorPrimary(Context context) {
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorPrimary;
        } else {
            colorAttr = context.getResources().getIdentifier("colorPrimary", "attr", context.getPackageName());
        }
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.data;
    }

    public static int getColorPrimaryDark(Context context) {
        try {
            int colorAttr;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorAttr = android.R.attr.colorPrimaryDark;
            } else {

                colorAttr = context.getResources().getIdentifier("colorPrimaryDark", "attr", context.getPackageName());
            }
            TypedValue outValue = new TypedValue();
            context.getTheme().resolveAttribute(colorAttr, outValue, true);
            return outValue.data;
        } catch (Exception ignored) {

        }
        TypedValue outValue = new TypedValue();
        return outValue.data;
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void facebookMobile(Activity activity, WebView view) {
        try {
            InputStream inputStream = activity.getAssets().open("facebookmobile.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void amoledTheme(Activity activity, WebView view) {
        try {
            InputStream inputStream = activity.getAssets().open("amoled.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void materiallight(Activity activity, WebView view) {
        try {
            InputStream inputStream = activity.getAssets().open("materiallight.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void materialdark(Activity activity, WebView view) {
        try {
            InputStream inputStream = activity.getAssets().open("materialdark.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void removeheader(Activity activity, WebView view) {
        try {
            InputStream inputStream = activity.getAssets().open("removeheader.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void showheader(Activity activity, WebView view) {
        try {
            InputStream inputStream = activity.getAssets().open("showheader.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void roundImages(Activity activity, WebView view) {
        try {
            InputStream inputStream = activity.getAssets().open("roundimages.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void pageStarted(Activity activity, WebView view) {
        try {
            boolean isConnectedMobile = NetworkConnection.isConnectedMobile(activity.getApplicationContext());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
            StringBuilder CSS = new StringBuilder("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.head.appendChild(node); } ");
            CSS.append("addStyleString('._129-{ margin-top: -46px; }');");
            if (preferences.getBoolean("select", false)) {
                CSS.append("addStyleString('._5msj{ display: none; }');addStyleString('._5rgr{ -webkit-user-select: initial; }');");
            } else {
                CSS.append("addStyleString('._5msj{ display: block; }');addStyleString('._5rgr{ -webkit-user-select: initial; }');");
            }
            if (preferences.getBoolean("news_edit", false)) {
                CSS.append("addStyleString('#mbasic_inline_feed_composer{ display: none; }');");
            } else {
                CSS.append("addStyleString('#mbasic_inline_feed_composer{ display: block; }');");
            }
            if (preferences.getBoolean("hide_people", false)) {
                CSS.append("article._55wo._5rgr._5gh8._35au{ display: none; }');");
            } else {
                CSS.append("addStyleString('article[data-ft*=\"ei\":\"\"]{ display: block  !important; }');addStyleString('._55wo._5rgr._5gh8._5gh8._35au, ._2dr, ._d2r { display: block !important; }');");
            }
            if (preferences.getBoolean("hide_ads", false)) {
                CSS.append("addStyleString('article[data-ft*=ei]{display: none !important;}');");
            } else {
                CSS.append("addStyleString('article[data-ft*=ei]{display: block !important;}');");
            }

            roundImages(activity, view);

            if (isConnectedMobile && preferences.getBoolean("no_images", false)) {
                view.getSettings().setLoadsImagesAutomatically(false);
                view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); " + "node.innerHTML = str; document.body.appendChild(node); } " + "addStyleString('.img, ._5sgg, ._-_a, .widePic, .profile-icon{ display: none; }');");
            } else {
                view.getSettings().setLoadsImagesAutomatically(true);
            }
            CSS.append("addStyleString('[data-sigil*=m-promo-jewel-simple_navigation_header]{ display: none; }');");
            CSS.append("addStyleString('._46e0 { display: none; }');addStyleString('._5xjd { display: none; }');addStyleString('#toggleHeader, .h.i#simple_navigation_header, .i.j#simple_navigation_header { display: none; }');addStyleString('simple_navigation_header._4o57 { display:inline; }');");
            view.loadUrl(CSS.toString());

        } catch (Exception ignored) {

        }
    }

    public static void pageFinished(WebView view, String url) {
        try {
            StringBuilder CSS = new StringBuilder("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.body.appendChild(node); } ");
            if (url != null) {
                if (url.contains("sharer") || url.contains("/composer/") || url.contains("throwback") || url.contains("edit") || url.contains("cover") || url.contains("reposition") || url.contains("%2Fedit%2")) {
                    CSS.append("addStyleString('._129-{ margin-top: -1px; }');");
                    MainActivity.swipeRefreshLayout.setEnabled(false);
                } else {
                        CSS.append("addStyleString('._129-{ margin-top: -46px; }');");
                        MainActivity.swipeRefreshLayout.setEnabled(true);
                }
                if (url.contains("messages")) {
                    CSS.append("addStyleString('[data-sigil*=m-promo-jewel-simple_navigation_header]{ display: none; }');");
                }

                CSS.append("addStyleString('._46e0 { display: none; }');addStyleString('._5xjd { display: none; }');addStyleString('#toggleHeader, .h.i#simple_navigation_header, .i.j#simple_navigation_header { display: none; }');");
            } else {
                    CSS.append("addStyleString('._129-{ margin-top: -46px; }');");
            }

            view.loadUrl(CSS.toString());
        }catch(Exception ignored){

        }

    }




    public static void backgoundColorStyle(Activity activity, WebView view) {
        final boolean facebookMobileColor = PreferencesUtility.getInstance(activity).getFreeTheme().equals("facebooktheme");
        final boolean amoledTheme = PreferencesUtility.getInstance(activity).getFreeTheme().equals("darktheme");
        final boolean materialDarkTheme = PreferencesUtility.getInstance(activity).getFreeTheme().equals("draculatheme");
        final boolean materiallightTheme = PreferencesUtility.getInstance(activity).getFreeTheme().equals("materialtheme");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (preferences.getBoolean("auto_night", false) && isNightTime(activity)) {
            view.setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
        } else {
            if (materiallightTheme) {
                view.setBackgroundColor(ContextCompat.getColor(activity, R.color.defaultcolor));
            }
            if (amoledTheme) {
                view.setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
            }
            if (materialDarkTheme) {
                view.setBackgroundColor(ContextCompat.getColor(activity, R.color.darcula));
            }
            if (facebookMobileColor) {
                view.setBackgroundColor(ContextCompat.getColor(activity, R.color.defaultcolor));
            }
        }
    }


    @SuppressWarnings("ConstantConditions")
    public static void fontSize(WebView view, Activity activity) {
        try{
            boolean xxsmallfont = PreferencesUtility.getInstance(activity).getFont().equals("50");
            boolean xsmallfont = PreferencesUtility.getInstance(activity).getFont().equals("75");
            boolean smallerfont = PreferencesUtility.getInstance(activity).getFont().equals("85");
            boolean smallfont = PreferencesUtility.getInstance(activity).getFont().equals("90");
            boolean defaultfont = PreferencesUtility.getInstance(activity).getFont().equals("100");
            boolean mediumfont = PreferencesUtility.getInstance(activity).getFont().equals("105");
            boolean largefont = PreferencesUtility.getInstance(activity).getFont().equals("115");
            boolean xlfont = PreferencesUtility.getInstance(activity).getFont().equals("125");
            boolean xxlfont = PreferencesUtility.getInstance(activity).getFont().equals("175");

            if (xxsmallfont) {
                view.getSettings().setTextZoom(50);
            }

            if (xsmallfont) {
                view.getSettings().setTextZoom(75);
            }

            if (smallerfont) {
                view.getSettings().setTextZoom(85);
            }

            if (smallfont) {
                view.getSettings().setTextZoom(90);
            }

            if (defaultfont) {
                view.getSettings().setTextZoom(100);
            }

            if (mediumfont) {
                view.getSettings().setTextZoom(105);
            }
            if (largefont) {
                view.getSettings().setTextZoom(110);
            }
            if (xlfont) {
                view.getSettings().setTextZoom(120);
            }
            if (xxlfont) {
                view.getSettings().setTextZoom(150);
            }
        } catch (Exception ignored) {

        }
    }


    public static void facebookTheme(Activity activity, WebView view) {
        final boolean facebookmobiletheme = PreferencesUtility.getInstance(activity).getFreeTheme().equals("facebooktheme");
        final boolean blackthemetheme = PreferencesUtility.getInstance(activity).getFreeTheme().equals("darktheme");
        final boolean materialdarktheme = PreferencesUtility.getInstance(activity).getFreeTheme().equals("draculatheme");
        final boolean materiallighttheme = PreferencesUtility.getInstance(activity).getFreeTheme().equals("materialtheme");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (preferences.getBoolean("auto_night", false) && isNightTime(activity)) {
            amoledTheme(activity, view);
            view.setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
        } else {

            if (materiallighttheme) {
                materiallight(activity, view);
                view.setBackgroundColor(ContextCompat.getColor(activity, R.color.defaultcolor));
            }

            if (blackthemetheme) {
                amoledTheme(activity, view);
                view.setBackgroundColor(ContextCompat.getColor(activity, R.color.black));
            }

            if (materialdarktheme) {
                materialdark(activity, view);
                view.setBackgroundColor(ContextCompat.getColor(activity, R.color.darcula));
            }

            if (facebookmobiletheme) {
                facebookMobile(activity, view);
                view.setBackgroundColor(ContextCompat.getColor(activity, R.color.defaultcolor));
            }

        }
    }


    private static String startNightTime() {
        return PreferencesUtility.getString("startTime", "1830");
    }

    private static String startNightDayTime() {
        return PreferencesUtility.getString("startTime", "1730");
    }

    private static String endNightTime() {
        return PreferencesUtility.getString("endTime", "0700");
    }

    private static String endNDayightTime() {
        return PreferencesUtility.getString("endTime", "0630");
    }


    public static boolean isNightTime(Activity activity) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        if (!preferences.getBoolean("auto_night", false)) {
            return false;
        }
        try {
            int startTime;
            int endTime;
            String nightStart = startNightTime();
            String daylight = startNightDayTime();
            String endDaynight = endNDayightTime();
            String nightEnd = endNightTime();
            Calendar calNow = Calendar.getInstance();
            TimeZone isDS = TimeZone.getDefault();
            //noinspection WrongConstant
            calNow.set(Calendar.PM, Calendar.AM);
            String timeNow = new SimpleDateFormat("HHmm", Locale.getDefault()).format(calNow.getTime());
            if (isDS.useDaylightTime()){
                startTime = Integer.parseInt(daylight);
            }else {
                startTime = Integer.parseInt(nightStart);
            }
            if (isDS.useDaylightTime()){
                endTime = Integer.parseInt(endDaynight);
            }else {
                endTime = Integer.parseInt(nightEnd);
            }
            int nowTime = Integer.parseInt(timeNow);
            if (startTime > endTime) {
                return nowTime > startTime || nowTime < endTime;
            } else return !(startTime >= endTime || nowTime < startTime || nowTime > endTime);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void isKeyBoardShowing(Context context) {
        try{
            final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            MainActivity.webView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    Rect r = new Rect();
                    MainActivity.webView.getWindowVisibleDisplayFrame(r);
                    int screenHeight = MainActivity.webView.getRootView().getHeight();
                    int keypadHeight = screenHeight - r.bottom;
                    if (keypadHeight > screenHeight * 0.15) {
                        MainActivity.swipeRefreshLayout.setEnabled(false);
                        try {
                            if (MainActivity.webView.getUrl().contains("cover")) {
                                MainActivity.swipeRefreshLayout.setEnabled(false);
                            }
                        }catch(Exception ignored){

                        }
                        MainActivity.tabs.setVisibility(View.INVISIBLE);
                        if (preferences.getBoolean("show_fab", false)) {
                            MainActivity.FAB.setVisibility(View.INVISIBLE);
                        }

                    } else {
                        MainActivity.tabs.setVisibility(View.VISIBLE);
                        MainActivity.swipeRefreshLayout.setEnabled(true);
                        if (preferences.getBoolean("show_fab", false)) {
                            MainActivity.FAB.setVisibility(View.VISIBLE);
                        }

                    }
                }
            });
        } catch (Exception ignored) {

        }
    }
}
