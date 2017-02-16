package com.creativetrends.simple.app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.creativetrends.simple.app.activities.SimpleApp;


public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context = SimpleApp.getContextOfApplication();
        Intent startIntent = new Intent(context, NotificationService.class);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.getBoolean("notifications_activated", false) || preferences.getBoolean("messages_activated", false)) {
            context.startService(startIntent);
            Log.d("NotificationReceiver", "Notifications started");
        }else {
            context.stopService(startIntent);
            Log.d("PollReceiver", "Notifications canceled");
        }
    }
}
