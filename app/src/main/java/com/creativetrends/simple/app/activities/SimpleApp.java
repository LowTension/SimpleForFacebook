// Copyright (C) 2014-2015 Jorell Rutledge/Creative Trends.
//This file is originally apart of Folio for Facebook.
//Copyright notice must remain here if you're using any part of this code.
//Some code taken from Tinfoil for Facebook
//Some code taken from Facebook Lite

package com.creativetrends.simple.app.activities;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import com.facebook.FacebookSdk;


public class SimpleApp extends Application {
    @SuppressLint("StaticFieldLeak")
    public static Context mContext;

    public static Context getContextOfApplication() {

        return mContext;
    }

    @Override
    public void onCreate() {
        mContext = getApplicationContext();
        FacebookSdk.sdkInitialize(getApplicationContext());
        super.onCreate();
        }

    }