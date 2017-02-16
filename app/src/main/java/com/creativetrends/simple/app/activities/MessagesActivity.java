package com.creativetrends.simple.app.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.lock.SimpleLock;
import com.creativetrends.simple.app.lock.AppLock;
import com.creativetrends.simple.app.services.NetworkConnection;
import com.creativetrends.simple.app.services.NetworkStatus;
import com.creativetrends.simple.app.webview.WebViewScroll;
import com.creativetrends.simple.app.utils.ThemeUtils;
import com.creativetrends.simple.app.utils.PreferencesUtility;
import com.creativetrends.simple.app.utils.SimpleListener;
import com.creativetrends.simple.app.webview.CustomChromeClient;

public class MessagesActivity extends AppCompatActivity {
    public Context context;
    public Toolbar toolbar;
    private int cssInject = 0;
    private WebViewScroll webView;
    public SwipeRefreshLayout swipeRefreshLayout;
    String url;
    SharedPreferences preferences;

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        ThemeUtils.setSettingsTheme(this, this);
        setContentView(R.layout.activity_messages);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.simple_swipe);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.white));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ThemeUtils.getColorPrimary(this));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
                if (!NetworkConnection.isConnected(getApplicationContext()))
                    swipeRefreshLayout.setRefreshing(false);
                else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);

                        }

                    }, 2500);
                }
            }
        });


        webView = (WebViewScroll) findViewById(R.id.text_box);
        webView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setListener(this, new SimpleListener(this, webView));
        if (preferences.getBoolean("allow_location", false)) {
            webView.getSettings().setGeolocationEnabled(true);
            webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            webView.getSettings().setGeolocationEnabled(false);
        }
        if (NetworkStatus.getInstance(this).isOnline()) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        try {
            ThemeUtils.fontSize(webView, this);
        }catch(Exception ignored){

        }
        if (preferences.getBoolean("data_reduce", false)) {
            String defaultUserAgent = webView.getSettings().getUserAgentString();
            webView.getSettings().setUserAgentString(defaultUserAgent.replaceFirst("Android ([0-9]+(\\.[0-9]+)*)", "Android"));
        } else {
            webView.getSettings().setUserAgentString("Mozilla/5.0 (BB10; Kbd) AppleWebKit/537.10+ (KHTML, like Gecko) Version/10.1.0.4633 Mobile Safari/537.10+");
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
        webView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:


                    case MotionEvent.ACTION_UP:


                        if (!v.hasFocus())


                        {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        try{
            webView.loadUrl("https://m.facebook.com/messages#");
        } catch (Exception e) {
            e.printStackTrace();
        }

        webView.setWebViewClient(new WebViewClient() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try {
                    ThemeUtils.backgoundColorStyle(MessagesActivity.this, webView);
                    swipeRefreshLayout.setRefreshing(true);
                    swipeRefreshLayout.setEnabled(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, 1000);

                    ThemeUtils.pageFinished(view, url);

                } catch (NullPointerException ignored) {
                } catch (Exception e){
                    e.printStackTrace();

                }
                cssInject = 0;

            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                cssInject++;
                    try {
                        if (cssInject < 5) {
                            ThemeUtils.pageStarted(MessagesActivity.this, webView);
                            ThemeUtils.facebookTheme(MessagesActivity.this, webView);
                            ThemeUtils.removeheader(MessagesActivity.this, webView);
                        }
                        if (cssInject == 10) {
                            ThemeUtils.facebookTheme(MessagesActivity.this, webView);
                            ThemeUtils.removeheader(MessagesActivity.this, webView);
                            //swipeRefreshLayout.setRefreshing(false);
                        }

                    } catch (NullPointerException ignored) {
                    } catch (Exception e){
                        e.printStackTrace();

                    }
                }



            @SuppressLint("ResourceAsColor")
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                try {
                    ThemeUtils.pageFinished(view, url);
                    swipeRefreshLayout.setRefreshing(false);
                    if (url.contains("sharer") || url.contains("/composer/") || url.contains("throwback_share_source")) {
                        ThemeUtils.showheader(MessagesActivity.this, webView);
                        swipeRefreshLayout.setEnabled(false);
                    } else {
                        ThemeUtils.removeheader(MessagesActivity.this, webView);
                    }
                } catch (NullPointerException ignored) {
                } catch (Exception e){
                    e.printStackTrace();

                }

            }
        });


        webView.setWebChromeClient(new CustomChromeClient(this) {
            @Override
            public void
            onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                assert title != null;
                if (title.contains("https://m.facebook.com/") || title.contains("https://mobile.facebook.com/")) {
                    MessagesActivity.this.setTitle(R.string.messages);
                } else {
                    MessagesActivity.this.setTitle(title);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        webView.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.messages_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(preferences.getString("is_shortcut", "").equals("true")) {
                    finish();
                }else {
                    finish();

                }
                return true;

            case R.id.minimize:
                webView.loadUrl("https://m.facebook.com/buddylist.php");
                return false;


            default:
                return super.onOptionsItemSelected(item);


        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        try{
            webView.loadUrl(url);
        }catch(Exception ignored){

        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            try {
                ThemeUtils.pageFinished(webView, webView.getUrl());
                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshLayout.setEnabled(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);

            } catch (NullPointerException e) {
                Log.e("onBackPressed", "" + e.getMessage());
                e.printStackTrace();
            }
        } else {
            if(preferences.getString("is_shortcut", "").equals("true")){
                super.onBackPressed();
            }else{
                super.onBackPressed();

            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            unregisterForContextMenu(webView);
            webView.onPause();
            webView.pauseTimers();
            PreferencesUtility.putString("needs_lock", "true");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();
        registerForContextMenu(webView);
        try {
            ThemeUtils.backgoundColorStyle(MessagesActivity.this, webView);
            if (PreferencesUtility.getString("needs_lock", "").equals("true") && (preferences.getBoolean("folio_locker", false))) {
                Intent intent = new Intent(MessagesActivity.this, SimpleLock.class);
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();
            webView.destroy();
            webView = null;
            }
        }

    }




