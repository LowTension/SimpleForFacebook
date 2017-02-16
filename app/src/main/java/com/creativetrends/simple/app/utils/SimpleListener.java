package com.creativetrends.simple.app.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.webkit.WebView;

import com.creativetrends.simple.app.webview.WebViewScroll;

/**Created by Creative Trends Apps on 10/17/2016.*/

public class SimpleListener implements WebViewScroll.Listener {
    private WebViewScroll mWebView;

    public SimpleListener(Activity activity, WebView view) {

        mWebView = (WebViewScroll) view;
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

    }

}