package com.creativetrends.simple.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.creativetrends.simple.app.R;

public class NavigationDrawer extends PreferenceFragment {
    private static final String LOG_TAG = "NavigationDrawer Items";
    private SharedPreferences.OnSharedPreferenceChangeListener myPrefListner;
    private SharedPreferences preferences;
    public boolean mListStyled;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.navigation_preferences);
        Context context = getActivity().getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);


        myPrefListner = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                preferences.edit().putString("changed", "true").apply();
                Log.i(LOG_TAG, "Applying changes needed");
                switch (key) {
                    default:
                        break;


                }
                Log.v("SharedPreferenceChange", key + " changed in NotificationsSettingsFragment");
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.navigation_pref);
        preferences.registerOnSharedPreferenceChangeListener(myPrefListner);
    }


    @Override
    public void onStop() {
        super.onStop();
        preferences.unregisterOnSharedPreferenceChangeListener(myPrefListner);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mListStyled) {
            View rootView = getView();
            if (rootView != null) {
                ListView list = (ListView) rootView.findViewById(android.R.id.list);
                list.setPadding(0, 0, 0, 0);
                list.setDivider(null);
                mListStyled = true;
            }
        }

    }

}


