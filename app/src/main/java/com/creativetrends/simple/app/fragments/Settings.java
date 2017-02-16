package com.creativetrends.simple.app.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.creativetrends.simple.app.activities.FilePickerActivity;
import com.creativetrends.simple.app.activities.SimpleApp;
import com.creativetrends.simple.app.lock.AppLock;
import com.creativetrends.simple.app.lock.SimpleLock;
import com.creativetrends.simple.app.preferences.SwitchPreferenceCompat;
import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.services.NetworkConnection;
import com.creativetrends.simple.app.services.NotificationService;
import com.creativetrends.simple.app.utils.PreferencesUtility;

import java.io.File;
import java.text.DecimalFormat;

import static android.app.Activity.RESULT_OK;


public class Settings extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private static final int REQUEST_LOCATION = 1;
    private static final int REQUEST_STORAGE = 2;
    private static final int REQUEST_SFINGER = 3;
    Context context;
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
    private SharedPreferences preferences;
    public boolean mListStyled;
    static boolean isConnectedMobile;
    Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
    SwitchPreferenceCompat ads, notifications, messages;
    Preference clear;
    static long size = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = SimpleApp.getContextOfApplication();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        addPreferencesFromResource(R.xml.preferences);
        isConnectedMobile = NetworkConnection.isConnectedMobile(SimpleApp.getContextOfApplication());
        ads = (SwitchPreferenceCompat) findPreference("hide_ads");
        notifications = (SwitchPreferenceCompat) findPreference("notifications_activated");
        messages = (SwitchPreferenceCompat) findPreference("messages_activated");
        Preference versionnumber = findPreference("version_number");
        Preference locker = findPreference("simple_locker");
        Preference picture_location = findPreference("custom_directory");
        Preference colored_nav = findPreference("nav");
        Preference links = findPreference("allow_inside");
        clear = findPreference("clear");
        versionnumber.setSummary(PreferencesUtility.getAppVersionName(context));

        if (preferences.getBoolean("notifications_activated", false)) {
            notifications.setSummary("General notifications turned on");
        }
        if (preferences.getBoolean("messages_activated", false)) {
            messages.setSummary("Message notifications turned on");
        } else {
            messages.setSummary("Enable to receive message notifications");
        }

        if (preferences.getString("custom_directory", "").equals("")) {
            String storage = "/storage/emulated/0/";
            picture_location.setSummary(storage + Environment.DIRECTORY_PICTURES + File.separator + getResources().getString(R.string.app_name_pro));
            } else {
                picture_location.setSummary("CustomDialog choice:" + " " + preferences.getString("custom_directory", ""));
            }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManagerCompat manager = FingerprintManagerCompat.from(getActivity().getApplicationContext());
            if (manager.isHardwareDetected()) {
                locker.setSummary(getActivity().getResources().getString(R.string.lock_text_new_fingerprint));
            } else {
                locker.setSummary(getActivity().getResources().getString(R.string.lock_text_new));
            }
        }


        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        ResolveInfo ri = context.getPackageManager().resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
        String pn = ri.loadLabel(context.getPackageManager()).toString();

        links.setSummary("Enable to open links in Simple. Disable to open links with" + " " + pn + ".");

        if (!isNavigationBarAvailable() && android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colored_nav.setEnabled(false);
            colored_nav.setSelectable(false);
            colored_nav.setSummary(getResources().getString(R.string.not_supported));
            Log.i("Hardware buttons", "disable this preference");
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && isNavigationBarAvailable()) {
                colored_nav.setEnabled(true);
                colored_nav.setSelectable(true);
                colored_nav.setSummary(getResources().getString(R.string.enable_color));
            }
        }

        ListPreference lp = (ListPreference) findPreference("interval_pref");
        String temp1 = getString(R.string.interval_pref_description).replace("%s", "");
        String temp2 = lp.getSummary().toString();
        if (temp1.equals(temp2))
            lp.setValueIndex(2);

        myPrefListner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                preferences.edit().putString("changed", "true").apply();
                Log.i("Settings", "Applying changes needed");
                final Intent notify = new Intent(context, NotificationService.class);
                switch (key) {

                    case "interval_pref":

                        if (prefs.getBoolean("notifications_activated", false) || prefs.getBoolean("messages_activated", false)) {
                            context.stopService(notify);
                            context.startService(notify);
                        }
                        break;

                    case "notifications_activated":
                        if (prefs.getBoolean("notifications_activated", false)) {
                            notifications.setSummary("General notifications turned on");
                        } else {
                            notifications.setSummary("Enable to receive general notifications");
                        }
                        if (prefs.getBoolean("notifications_activated", false) && preferences.getBoolean("messages_activated", false)) {
                            context.stopService(notify);
                            context.startService(notify);
                        } else //noinspection StatementWithEmptyBody
                            if (!prefs.getBoolean("notifications_activated", false) && preferences.getBoolean("messages_activated", false)) {
                                // ignore this case
                            } else if (prefs.getBoolean("notifications_activated", false) && !preferences.getBoolean("messages_activated", false)) {
                                context.startService(notify);
                            } else
                                context.stopService(notify);
                        break;

                    case "messages_activated":
                        if (prefs.getBoolean("messages_activated", false)) {
                            messages.setSummary("Message notifications turned on");
                        } else {
                            messages.setSummary("Enable to receive message notifications");
                        }
                        if (prefs.getBoolean("messages_activated", false) && preferences.getBoolean("notifications_activated", false)) {
                            context.stopService(notify);
                            context.startService(notify);
                        } else //noinspection StatementWithEmptyBody
                            if (!prefs.getBoolean("messages_activated", false) && preferences.getBoolean("notifications_activated", false)) {
                                // ignore this case
                            } else if (prefs.getBoolean("messages_activated", false) && !preferences.getBoolean("notifications_activated", false)) {
                                context.startService(notify);
                            } else
                                context.stopService(notify);
                        break;

                    case "hide_ads":


                        break;


                    case "simple_locker":
                        final FingerprintManagerCompat manager = FingerprintManagerCompat.from(getActivity().getApplicationContext());
                        if (prefs.getBoolean("simple_locker", false)) {
                            AlertDialog.Builder terms = new AlertDialog.Builder(getActivity());
                            terms.setTitle(getResources().getString(R.string.simple_lock));
                            terms.setMessage(getResources().getString(R.string.saved_pin_message));
                            terms.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if (manager.isHardwareDetected() && !hasfingerPermission()) {
                                        requestFingerPermission();
                                    } else {
                                        Intent intent = new Intent(getActivity(), SimpleLock.class);
                                        intent.putExtra(AppLock.EXTRA_TYPE, AppLock.ENABLE_PINLOCK);
                                        startActivity(intent);
                                    }

                                }
                            });
                            terms.show();
                        } else {
                            Intent intent = new Intent(getActivity(), SimpleLock.class);
                            intent.putExtra(AppLock.EXTRA_TYPE, AppLock.DISABLE_PINLOCK);
                            startActivity(intent);

                        }
                        break;

                    case "allow_location":
                        if (prefs.getBoolean("allow_location", false)) {
                            requestLocationPermission();
                        }

                        break;

                    case "peek_View":
                        if (prefs.getBoolean("peek_View", false)) {
                            requestStoragePermission();
                        }
                        break;


                    default:
                        break;

                    case "custom_pictures":
                        try {
                            if (prefs.getBoolean("custom_pictures", false)) {
                                requestStoragePermission();
                            }
                        } catch (Exception ignored) {

                        }


                        break;


                }

            }
        };


        Preference navigation = findPreference("drawer_items_pref");
        Preference terms = findPreference("terms");
        Preference rating = findPreference("rate_simple");
        navigation.setOnPreferenceClickListener(this);
        terms.setOnPreferenceClickListener(this);
        picture_location.setOnPreferenceClickListener(this);
        rating.setOnPreferenceClickListener(this);


    }


    @Override
    public boolean onPreferenceClick(Preference preference) {
        preferences.edit().putString("changed", "true").apply();
        String key = preference.getKey();

        switch (key) {

            case "rate_simple":
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + SimpleApp.getContextOfApplication().getPackageName())));
                break;


            case "drawer_items_pref":
                //noinspection ResourceType
                getFragmentManager().beginTransaction()
                        .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out)
                        .addToBackStack(null).replace(R.id.settings_frame,
                        new NavigationDrawer()).commit();
                break;


            case "terms":

                AlertDialog.Builder terms = new AlertDialog.Builder(getActivity());
                terms.setTitle(getResources().getString(R.string.terms_settings));
                terms.setMessage(getResources().getString(R.string.eula_string));
                terms.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

                terms.show();
                break;


            case "custom_directory":
                if (isSDPresent) {
                    Intent intent = new Intent(getActivity(), FilePickerActivity.class);
                    String storage = "/storage/";
                    intent.putExtra(FilePickerActivity.ARG_START_PATH, storage);
                    startActivityForResult(intent, 1);
                } else {
                    Intent intent = new Intent(getActivity(), FilePickerActivity.class);
                    intent.putExtra(FilePickerActivity.ARG_START_PATH, Environment.getExternalStorageDirectory().getPath());
                    startActivityForResult(intent, 1);

                }

                break;

        }

        return false;


    }


    @Override
    public void onStart() {
        super.onStart();
        View rootView = getView();
        if (rootView != null) {
            ListView list = (ListView) rootView.findViewById(android.R.id.list);
            list.setPadding(0, 0, 0, 0);
            list.setDivider(null);
            mListStyled = true;
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        initializeCache();
        preferences.registerOnSharedPreferenceChangeListener(myPrefListner);
        // update notification ringtone preference summary
        String ringtoneString = preferences.getString("ringtone", "content://settings/system/notification_sound");
        Uri ringtoneUri = Uri.parse(ringtoneString);
        String name;

        try {
            Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
            name = ringtone.getTitle(context);
        } catch (Exception ex) {
            ex.printStackTrace();
            name = "Default";
        }

        if ("".equals(ringtoneString))
            name = getString(R.string.silent);

        RingtonePreference rpn = (RingtonePreference) findPreference("ringtone");
        rpn.setSummary(name);

        // update message ringtone preference summary
        ringtoneString = preferences.getString("ringtone_msg", "content://settings/system/notification_sound");
        ringtoneUri = Uri.parse(ringtoneString);

        try {
            Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);
            name = ringtone.getTitle(context);
        } catch (Exception ex) {
            ex.printStackTrace();
            name = "Default";
        }

        if ("".equals(ringtoneString))
            name = getString(R.string.silent);

        RingtonePreference rpm = (RingtonePreference) findPreference("ringtone_msg");
        rpm.setSummary(name);
    }


    @Override
    public void onPause() {
        super.onPause();
        preferences.unregisterOnSharedPreferenceChangeListener(myPrefListner);
    }


    private void requestLocationPermission() {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int hasPermission = ContextCompat.checkSelfPermission(context, locationPermission);
        String[] permissions = new String[]{locationPermission};
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_LOCATION);
        } else {
            Log.i("", "");
        }

    }

    private void requestStoragePermission() {
        String locationPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(context, locationPermission);
        String[] permissions = new String[]{locationPermission};
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_STORAGE);
        } else {
            Log.i("", "");
        }

    }


    @TargetApi(Build.VERSION_CODES.M)
    private void requestFingerPermission() {
        String locationPermission = Manifest.permission.USE_FINGERPRINT;
        int hasPermission = ContextCompat.checkSelfPermission(context, locationPermission);
        String[] permissions = new String[]{locationPermission};
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_SFINGER);
        } else {
            Log.i("", "");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean hasfingerPermission() {
        String storagePermission = Manifest.permission.USE_FINGERPRINT;
        int hasPermission = ContextCompat.checkSelfPermission(getActivity(), storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Preference picture_location = findPreference("custom_directory");
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            picture_location.setSummary(filePath);
            preferences.edit().putString("custom_directory", filePath).apply();
            PreferencesUtility.putString("apply_changes", "");
        }
    }



    public boolean isNavigationBarAvailable() {
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        return (!(hasBackKey && hasHomeKey));
    }


    private void initializeCache() {
        size += getDirSize(getActivity().getCacheDir());
        size += getDirSize(getActivity().getExternalCacheDir());
        clear.setSummary(getResources().getString(R.string.current_cache_size) + ": " + readableFileSize(size));
    }

    public long getDirSize(File dir) {
        long size = 0;
        for (File file : dir.listFiles()) {
            if (file != null && file.isDirectory()) {
                size += getDirSize(file);
            } else if (file != null && file.isFile()) {
                size += file.length();
            }
        }
        return size;
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0 Bytes";
        final String[] units = new String[]{"Bytes", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


}