package com.creativetrends.simple.app.shortcuts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.creativetrends.simple.app.activities.MainActivity;

/**Created by Creative Trends Apps on 12/6/2016.*/

public class Notifications extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent notifications = new Intent(getApplicationContext(), MainActivity.class);
        notifications.putExtra("start_url", "https://mobile.facebook.com/notifications");
        notifications.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(notifications);
        finish();
    }
}
