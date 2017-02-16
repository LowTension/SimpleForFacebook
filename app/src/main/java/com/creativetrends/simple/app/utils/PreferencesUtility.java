package com.creativetrends.simple.app.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.creativetrends.simple.app.activities.SimpleApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public final class PreferencesUtility {
    private static final String THEME_PREFERENCE = "theme_preference";
    private static final String FONT_SIZE = "font_pref";
    private static final String FACEBOOK_THEMES = "theme_preference_fb";
    private static final String NEWS_FEED = "news_feed";
    private static SharedPreferences mPreferences;
    private static PreferencesUtility sInstance;


    public PreferencesUtility(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferencesUtility getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesUtility(context.getApplicationContext());
        }
        return sInstance;
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(SimpleApp.getContextOfApplication()).getBoolean(key, defValue);
    }

    public static String getString(String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(SimpleApp.getContextOfApplication()).getString(key, defValue);
    }

    public static void putString(String key, String value) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(SimpleApp.getContextOfApplication()).edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void remove(String key) {
        Editor editor = PreferenceManager.getDefaultSharedPreferences(SimpleApp.getContextOfApplication()).edit();
        editor.remove(key);
        editor.apply();
    }

    public static String getAppVersionName(Context context) {
        String res = "0.0.0.0.0.0";
        try {
            res = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }



    public String getTheme() {
        return mPreferences.getString(THEME_PREFERENCE, "");
    }


    public String getFont() {
        return mPreferences.getString(FONT_SIZE, "");
    }

    public String getFeed() {
        return mPreferences.getString(NEWS_FEED, "");
    }

    public String getFreeTheme() {
        return mPreferences.getString(FACEBOOK_THEMES, "");
    }


    public static ArrayList<Pin> getBookmarks() {
        String bookmarks = getString("simple_pins", "[]");
        ArrayList<Pin> pinsList = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(bookmarks);
            for (int i = 0; i < array.length(); i++) {
                JSONObject ob = array.getJSONObject(i);
                Pin bookmark = new Pin();
                bookmark.setTitle(ob.getString("title"));
                bookmark.setUrl(ob.getString("url"));
                pinsList.add(bookmark);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pinsList;
    }

    public static void saveBookmarks(ArrayList<Pin> pinsList) {
        JSONArray array = new JSONArray();
        Iterator it = pinsList.iterator();
        if (it.hasNext()) {
            do {
                Pin bookmark = (Pin) it.next();
                JSONObject ob = new JSONObject();
                try {
                    ob.put("title", bookmark.getTitle());
                    ob.put("url", bookmark.getUrl());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(ob);
            } while (it.hasNext());
        }
        putString("simple_pins", array.toString());
    }


}




