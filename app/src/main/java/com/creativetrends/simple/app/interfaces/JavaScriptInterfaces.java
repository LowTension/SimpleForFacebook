package com.creativetrends.simple.app.interfaces;

import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.TextView;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.activities.MainActivity;
import com.creativetrends.simple.app.activities.VideoActivity;
import com.creativetrends.simple.app.helpers.BadgeHelper;
import com.creativetrends.simple.app.utils.PreferencesUtility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaScriptInterfaces {
    private final MainActivity mContext;


    public JavaScriptInterfaces(MainActivity c) {
        mContext = c;
    }


    @JavascriptInterface
    public void getNums(final String notifications, final String messages, final String requests) {
        final int notifications_int = BadgeHelper.isInteger(notifications) ? Integer.parseInt(notifications) : 0;
        final int messages_int = BadgeHelper.isInteger(messages) ? Integer.parseInt(messages) : 0;
        final int requests_int = BadgeHelper.isInteger(requests) ? Integer.parseInt(requests) : 0;
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContext.setNotificationNum(notifications_int);
                mContext.setMessagesNum(messages_int);
                mContext.setFriendsNum(requests_int);
            }
        });
    }

    @JavascriptInterface
    public void getFeedCount(final String number) {
        try {
            final int num = Integer.parseInt(number);
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setNewsNum(num);
                }
            });

        } catch (NumberFormatException e) {
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.setNewsNum(0);
                }
            });
        }
    }

    @JavascriptInterface
    public void processVideo(String vidData, String vidID) {
        String mVideoUrl = vidData;
        String mVideoName = vidID;
        mVideoName = mVideoName.substring(0, 8);
        Intent i = new Intent(mContext, VideoActivity.class);
        i.putExtra("VideoUrl", mVideoUrl);
        i.putExtra("VideoName", mVideoName);
        mContext.startActivity(i);
    }




    @JavascriptInterface
    public void getUserInfo(final String htmlElement) {
        if (htmlElement == null) {
            // If there is no result, we don't wanna run
            return;
        }
        // Name regex
        Pattern pattern = Pattern.compile("aria-label=\"(.[^\"]*)\"");
        Matcher matcher = pattern.matcher(htmlElement);

        String name = null;
        if (matcher.find()) {
            name = matcher.group(1);
        }

        if (name != null) {
            final String finalName = name;
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((TextView) mContext.findViewById(R.id.profile_name)).setText(finalName);
                        ((TextView) mContext.findViewById(R.id.user_email)).setText(mContext.getResources().getString(R.string.app_name) + " " + PreferencesUtility.getAppVersionName(mContext.getApplicationContext()));
                    } catch (NullPointerException e) {
                        Log.e("onLoadResourceError", "" + e.getMessage());
                        e.printStackTrace();
                    }

                }
            });


        }
    }
}
