package com.creativetrends.simple.app.utils;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.webkit.WebView;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.activities.MainActivity;
import com.creativetrends.simple.app.webview.WebViewScroll;
import com.github.clans.fab.FloatingActionMenu;


public class Listener implements WebViewScroll.Listener {
    private static SharedPreferences preferences;
    private final WebViewScroll mWebView;
    private final FloatingActionMenu FAB;
    private final int mScrollThreshold;
    public Listener(MainActivity activity, WebView view) {
        mWebView = (WebViewScroll) view;
        FAB = (FloatingActionMenu) activity.findViewById(R.id.fab);
        preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        mScrollThreshold = activity.getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }


    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }


    @Override
    public void onScrollChange(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (preferences.getBoolean("lock_toolbar", false) && Math.abs(oldScrollY - scrollY) > mScrollThreshold) {
            if (scrollY > oldScrollY) {
                FAB.showMenuButton(true);
                FAB.close(true);
            } else if (scrollY < oldScrollY) {
                FAB.showMenuButton(true);
                FAB.close(true);
            }
        } else {
            if (Math.abs(oldScrollY - scrollY) > mScrollThreshold) {
                if (scrollY > oldScrollY) {
                    FAB.close(true);
                } else if (scrollY < oldScrollY) {
                    FAB.close(true);
                }

                }
            }

        }
    }
