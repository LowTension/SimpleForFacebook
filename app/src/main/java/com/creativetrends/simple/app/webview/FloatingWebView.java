package com.creativetrends.simple.app.webview;

import android.annotation.TargetApi;
import android.content.Context;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.creativetrends.simple.app.activities.SimpleApp;
import com.creativetrends.simple.app.services.NetworkConnection;

// Created by Jorell on 3/13/2016.

public class FloatingWebView extends WebViewClient {

    private Context context = SimpleApp.getContextOfApplication();
    private boolean refreshed;

    @SuppressWarnings("deprecation")
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

        if (NetworkConnection.isConnected(context) && !refreshed) {
            view.loadUrl(failingUrl);
            refreshed = true;
        }
    }

    @TargetApi(android.os.Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
        onReceivedError(view, err.getErrorCode(), err.getDescription().toString(), req.getUrl().toString());
    }


}
