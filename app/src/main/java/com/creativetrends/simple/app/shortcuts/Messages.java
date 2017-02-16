package com.creativetrends.simple.app.shortcuts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.creativetrends.simple.app.activities.MessagesActivity;

/** Created by Creative Trends Apps on 12/6/2016.*/

public class Messages extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent messages;
        super.onCreate(savedInstanceState);
        messages = new Intent(Messages.this, MessagesActivity.class);
        messages.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(messages);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
