// Copyright (C) 2014-2015 Jorell Rutledge/Creative Trends.
//This file is originally apart of Folio for Facebook.
//Copyright notice must remain here if you're using any part of this code.
//Some code taken from Tinfoil for Facebook
//Some code taken from Facebook Lite


package com.creativetrends.simple.app.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.creativetrends.simple.app.fragments.Settings;
import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.utils.ThemeUtils;

@TargetApi(23)
public class SettingsActivity extends AppCompatActivity {
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setSettingsTheme(this, this);
        setContentView(R.layout.activity_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        if (preferences.getBoolean("nav", false) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ThemeUtils.getColorPrimaryDark(this));
        }

        getFragmentManager().beginTransaction().replace(R.id.settings_frame,
                new Settings()).commit();
    }




    @Override
    protected void onStart() {
        super.onStart();
        preferences.edit().putString("apply_changes", "new").apply();
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    protected void onPause(){
        super.onPause();

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }


    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            changes();
            if (preferences.getBoolean("simple_locker", false)) {
                preferences.edit().putString("apply_changes", "true").apply();
            }
        } else
            getFragmentManager().popBackStack();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;


            default:
                return super.onOptionsItemSelected(item);




        }
    }

    private void changes() {
        if (preferences.getString("changed", "").equals("")) {
            finish();
        } else {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            intent.putExtra("apply_changes_to_app", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }
    }


}