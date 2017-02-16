package com.creativetrends.simple.app.activities;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions.Callback;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akiniyalocts.minor.MinorLayout;
import com.akiniyalocts.minor.MinorView;
import com.bumptech.glide.Glide;
import com.creativetrends.simple.app.helpers.BadgeHelper;
import com.creativetrends.simple.app.interfaces.JavaScriptInterfaces;
import com.creativetrends.simple.app.lock.AppLock;
import com.creativetrends.simple.app.lock.SimpleLock;
import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.services.NetworkConnection;
import com.creativetrends.simple.app.services.NetworkStatus;
import com.creativetrends.simple.app.services.NotificationService;
import com.creativetrends.simple.app.shortcuts.Messages;
import com.creativetrends.simple.app.shortcuts.Notifications;
import com.creativetrends.simple.app.utils.Cleaner;
import com.creativetrends.simple.app.utils.Downloader;
import com.creativetrends.simple.app.utils.Listener;
import com.creativetrends.simple.app.utils.Pin;
import com.creativetrends.simple.app.utils.PrefManager;
import com.creativetrends.simple.app.utils.PreferencesUtility;
import com.creativetrends.simple.app.utils.Sharer;
import com.creativetrends.simple.app.utils.SimplePins;
import com.creativetrends.simple.app.utils.ThemeUtils;
import com.creativetrends.simple.app.webview.CustomChromeClient;
import com.creativetrends.simple.app.webview.WebViewScroll;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;


@SuppressWarnings({"deprecation", "unused"})
@SuppressLint({"SetJavaScriptEnabled", "NewApi"})

public class MainActivity extends AppCompatActivity implements SimplePins.onBookmarkSelected {
    private final MyHandler linkHandler = new MyHandler(this);
    public int scrollPosition = 0;
    int cssInject = 0;
    AppBarLayout appBar;
    PrefManager prefManager;
    @SuppressLint("StaticFieldLeak")
    public static SimplePins pins_adapt;
    private static final int REQUEST_STORAGE = 1, ID_CONTEXT_MENU_SAVE_IMAGE = 2562617, ID_CONTEXT_MENU_SHARE_IMAGE = 2562618, ID_CONTEXT_MENU_COPY_IMAGE = 2562619;
    private static String appDirectoryName;
    private static SharedPreferences preferences;
    @SuppressLint("StaticFieldLeak")
    public Toolbar toolbar;
    @SuppressLint("StaticFieldLeak")
    public static SwipeRefreshLayout swipeRefreshLayout;
    NavigationView navigationView, navigationviewpins;
    RecyclerView pins_recycler;
    private ArrayList<Pin> listBookmarks = new ArrayList<>();
    static boolean isConnectedMobile, refreshed, mostrecent;
    public static String mPendingImageUrlToSave, simpleUser = null, FACEBOOK = "https://m.facebook.com/";
    private CallbackManager callbackManager;
    @SuppressLint("StaticFieldLeak")
    public static DrawerLayout drawerLayout;
    public static FloatingActionMenu FAB;
    public static WebViewScroll webView;
    private static long back_pressed;
    MinorView news, fri, mess, notifications, menuFb;
    ImageView newsFB, friendsFB, notificationsFB, moreFB;
    public static MinorLayout tabs;
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception ignored) {
        }
    }
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else
            return dir != null && dir.isFile() && dir.delete();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    @SuppressLint({"setJavaScriptEnabled", "CutPasteId", "ClickableViewAccessibility", "SdCardPath", "SetTextI18n", "JavascriptInterface"})
    protected void onCreate(Bundle savedInstanceState) {
        prefManager = new PrefManager(this);
        boolean topnews = PreferencesUtility.getInstance(this).getFeed().equals("top_news");
        mostrecent = PreferencesUtility.getInstance(MainActivity.this).getFeed().equals("most_recent");
        isConnectedMobile = NetworkConnection.isConnectedMobile(SimpleApp.getContextOfApplication());
        ThemeUtils.setAppColor(this, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.setDefaultValues(this, R.xml.navigation_preferences, true);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        news = (MinorView) findViewById(R.id.news_badges);
        fri = (MinorView) findViewById(R.id.friends_badges);
        mess = (MinorView) findViewById(R.id.messages_badges);
        notifications = (MinorView) findViewById(R.id.notify_badges);
        menuFb = (MinorView) findViewById(R.id.side_menu);
        try {
            listBookmarks = PreferencesUtility.getBookmarks();
            pins_recycler = (RecyclerView) findViewById(R.id.pins_recyclerView);
            pins_recycler.setLayoutManager(new LinearLayoutManager(this));
            pins_adapt = new SimplePins(this, listBookmarks, this);
            pins_recycler.setAdapter(pins_adapt);
        } catch (Exception ignored) {

        }
        dialog();
        initMinor();

        news.selected();

        navigationView = (NavigationView) findViewById(R.id.main_nav);
        navigationviewpins = (NavigationView) findViewById(R.id.simple_pins_nav);
        appDirectoryName = getString(R.string.app_name_pro).replace(" ", " ");
        FAB = (FloatingActionMenu) findViewById(R.id.fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        tabs = (MinorLayout) findViewById(R.id.tabs);
        appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                FAB.setTranslationY((float) (verticalOffset * -4));
                tabs.setTranslationY((float) (verticalOffset * -4));
            }
        });

        if (preferences.getBoolean("auto_night", false) && ThemeUtils.isNightTime(this)) {
            tabs.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
        }
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);

        newsFB = (ImageView) findViewById(R.id.newsImage);
        friendsFB = (ImageView) findViewById(R.id.friendsImage);
        notificationsFB = (ImageView) findViewById(R.id.notificationsImage);
        moreFB = (ImageView) findViewById(R.id.moreImage);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        setSupportActionBar(toolbar);
        if (preferences.getBoolean("nav", false) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ThemeUtils.getColorPrimaryDark(this));
        }


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.simple_swipe);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.white));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ThemeUtils.getColorPrimary(MainActivity.this));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FAB.close(true);
                webView.reload();
                if (!NetworkConnection.isConnected(getApplicationContext()))
                    swipeRefreshLayout.setRefreshing(false);
                else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }

                    }, 3000);
                }
            }
        });


        findViewById(R.id.shareFab).setOnClickListener(mFABClickListener);
        findViewById(R.id.locationFab).setOnClickListener(mFABClickListener);
        findViewById(R.id.photoFab).setOnClickListener(mFABClickListener);
        findViewById(R.id.updateFab).setOnClickListener(mFABClickListener);
        findViewById(R.id.add_new_pin).setOnClickListener(mFABClickListener);


        ImageView coverImage = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.back_color);
        coverImage.setClickable(true);
        coverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (preferences.getBoolean("analytics_true", false)) {
                    webView.loadUrl("http://bit.ly/2kYr8j6");
                    fri.unselected();
                    mess.unselected();
                    news.unselected();
                    notifications.unselected();
                    menuFb.unselected();
                } else {
                    webView.loadUrl("https://m.facebook.com/me#");
                    fri.unselected();
                    mess.unselected();
                    news.unselected();
                    notifications.unselected();
                    menuFb.unselected();
                }
                drawerLayout.closeDrawers();
            }
        });


        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (scrollPosition > 10) {
                    scrollToTop();
                } else {
                    webView.reload();
                }

            }
        });


        String webViewUrl = "https://m.facebook.com";


        if (mostrecent)
            webViewUrl = "https://m.facebook.com/home.php?sk=h_chr&refid=8";

        if (topnews)
            webViewUrl = "https://m.facebook.com/home.php?sk=h_nor&refid=8";


        webView = (WebViewScroll) findViewById(R.id.main_webView);
        assert webView != null;
        webView.getSettings().setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        getUserAgent(this);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        if (preferences.getBoolean("allow_location", false)) {
            webView.getSettings().setGeolocationEnabled(true);
            webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            webView.getSettings().setGeolocationEnabled(false);
        }
        webView.getSettings().enableSmoothTransition();
        webView.setVerticalScrollBarEnabled(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (NetworkStatus.getInstance(this).isOnline()) {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ONLY);
            webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDatabasePath(this.getFilesDir().getPath() + getPackageName() + "/databases/");
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        try {
            ThemeUtils.fontSize(webView, this);
        } catch (Exception ignored) {

        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.setListener(this, new Listener(this, webView));
        webView.addJavascriptInterface(new JavaScriptInterfaces(this), "android");
        webView.addJavascriptInterface(MainActivity.this, "Downloader");
        webView.setOnScrollChangedCallback(new WebViewScroll.OnScrollChangedCallback() {
            public void onScroll(int l, int t) {
                scrollPosition = t;
            }
        });


        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // activate long clicks on links and image links according to settings
                if (preferences.getBoolean("peek_View", true)) {
                    try {
                        webView.setHapticFeedbackEnabled(true);
                        injectSelect();
                        WebView.HitTestResult result = webView.getHitTestResult();
                        if (result.getType() == WebView.HitTestResult.SRC_ANCHOR_TYPE || result.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                            Message msg = linkHandler.obtainMessage();
                            webView.requestFocusNodeHref(msg);
                            return true;
                        }
                    } catch (Exception ignored) {
                    }
                }
                return false;
            }
        });


        final boolean isConnectedMobile = NetworkConnection.isConnectedMobile(getApplicationContext());
        String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        if (sharedUrl != null) {
            if (!sharedUrl.equals("")) {

                if (!sharedUrl.startsWith("http://") || !sharedUrl.startsWith("https://")) {

                    int startUrlIndex = sharedUrl.indexOf("http:");
                    if (startUrlIndex > 0) {

                        sharedUrl = sharedUrl.substring(startUrlIndex);
                    }
                }

                Intent shareIntent = new Intent(MainActivity.this, SharerActivity.class);
                shareIntent.setData(Uri.parse("https://www.facebook.com/sharer.php?u=" + sharedUrl));
                startActivity(shareIntent);
            }
        }
        if ((getIntent() != null && getIntent().getDataString() != null) && (!isConnectedMobile)) {
            webViewUrl = getIntent().getDataString();
        }
        try {

            if (getIntent().getExtras().getString("start_url") != null) {
                String temp = getIntent().getExtras().getString("start_url");
                if (!isConnectedMobile)
                    webViewUrl = temp;

                if (temp != null && temp.equals("https://m.facebook.com/notifications"))
                    NotificationService.clearNotifications();
                if (temp != null && temp.equals("https://m.facebook.com/messages/"))
                    NotificationService.clearNotifications();

            }
        } catch (Exception ignored) {
        }


        if (savedInstanceState != null)
            webView.restoreState(savedInstanceState);
        else
            webView.loadUrl(webViewUrl);
        webView.setWebViewClient(new WebViewClient() {


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                try {
                    if (url != null)
                        url = Cleaner.cleanAndDecodeUrl(url);

                    assert url != null;
                    if ((url.contains("market://")
                            || url.contains("mailto:")
                            || url.contains("play.google")
                            || url.contains("tel:")
                            || url.contains("youtube")
                            || url.contains("vid:"))) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;
                    }

                    if (Downloader.isDownloadableFile(url) && hasStoragePermission()) {
                        Toast.makeText(getBaseContext(), R.string.fragment_main_downloading, Toast.LENGTH_LONG).show();
                        Downloader.downloadFile(MainActivity.this, url, Downloader.getFileName(url));
                        return true;
                    }

                    if (url.contains("jpg")) {
                        Intent photoViewer = new Intent(MainActivity.this, PhotoViewer.class);
                        photoViewer.putExtra("url", url);
                        photoViewer.putExtra("title", view.getTitle());
                        startActivity(photoViewer);
                        return true;

                    } else if (Uri.parse(url).getHost().endsWith("facebook.com")
                            || Uri.parse(url).getHost().endsWith("m.facebook.com")
                            || Uri.parse(url).getHost().endsWith("mobile.facebook.com")
                            || Uri.parse(url).getHost().endsWith("h.facebook.com")
                            || Uri.parse(url).getHost().endsWith("l.facebook.com")
                            || Uri.parse(url).getHost().endsWith("0.facebook.com")
                            || Uri.parse(url).getHost().endsWith("zero.facebook.com")
                            || Uri.parse(url).getHost().endsWith("fbcdn.net")
                            || Uri.parse(url).getHost().endsWith("akamaihd.net")
                            || Uri.parse(url).getHost().endsWith("fb.me")) {
                        return false;

                    }
                    if (preferences.getBoolean("allow_inside", false)) {
                        Intent intent = new Intent(MainActivity.this, SimpleCustomTabs.class);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                        PreferencesUtility.putString("needs_lock", "false");
                        return true;
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Log.e("shouldOverrideUrlLoad", "" + e.getMessage());
                        e.printStackTrace();
                    }

                    return true;
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                    return true;
                }
            }


            @SuppressLint("ResourceAsColor")
            @Override
            public void onPageFinished(WebView view, String url) {
                try {
                    BadgeHelper.videoView(webView);
                    ThemeUtils.pageFinished(view, url);
                    BadgeHelper.updateNums(webView);
                    listBookmarks = PreferencesUtility.getBookmarks();
                    if (preferences.getBoolean("swipe_refresh", false)) {
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setEnabled(false);
                    } else {
                        swipeRefreshLayout.setRefreshing(false);
                        swipeRefreshLayout.setEnabled(true);
                    }

                    if (url.contains("sharer") || url.contains("/composer/") || url.contains("throwback") || url.contains("edit") || url.contains("cover") || url.contains("reposition") || url.contains("%2Fedit%2")) {
                        ThemeUtils.showheader(MainActivity.this, webView);
                        swipeRefreshLayout.setEnabled(false);
                    } else {
                        ThemeUtils.removeheader(MainActivity.this, webView);
                        swipeRefreshLayout.setEnabled(true);
                    }

                    if (url.contains("search") && !url.contains("ref=search")) {
                        if (url.contains("?soft=search")) {
                            webView.loadUrl("javascript:document.getElementById('main-search-input').click();");
                            webView.requestFocus();
                        }

                    }


                } catch (NullPointerException e) {
                    Log.e("onPageFinished", "" + e.getMessage());
                    e.printStackTrace();
                }

            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try {
                    ThemeUtils.backgoundColorStyle(MainActivity.this, view);
                    if (preferences.getBoolean("swipe_refresh", false)) {
                        swipeRefreshLayout.setEnabled(false);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(MainActivity.this, R.string.com_facebook_loading, Toast.LENGTH_SHORT).show();
                    } else {
                        swipeRefreshLayout.setRefreshing(true);
                        swipeRefreshLayout.setEnabled(true);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }, 2000);
                    }
                    ThemeUtils.pageFinished(view, url);

                } catch (NullPointerException e) {
                    Log.e("onLoadResourceError", "" + e.getMessage());
                    e.printStackTrace();
                }
                cssInject = 0;
            }


            @Override
            public void onLoadResource(final WebView view, final String url) {
                cssInject++;
                try {
                    BadgeHelper.videoView(webView);
                    if (cssInject < 5) {
                        ThemeUtils.pageStarted(MainActivity.this, view);
                        ThemeUtils.facebookTheme(MainActivity.this, view);
                        if (url.contains("sharer")) {
                            ThemeUtils.pageFinished(view, url);
                        }
                    }
                    if (cssInject == 10) {
                        ThemeUtils.pageStarted(MainActivity.this, view);
                        ThemeUtils.facebookTheme(MainActivity.this, view);
                        BadgeHelper.updateNums(view);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    if (url.contains("throwback")) {
                        ThemeUtils.showheader(MainActivity.this, webView);
                        swipeRefreshLayout.setEnabled(true);
                    }

                } catch (NullPointerException e) {
                    Log.e("onLoadResourceError", "" + e.getMessage());
                    e.printStackTrace();

                }

            }


            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (NetworkConnection.isConnected(getApplicationContext()) && !refreshed) {
                    webView.loadUrl(failingUrl);
                    refreshed = true;
                } else {
                    webView.loadUrl("file:///android_asset/error.html");
                    MainActivity.this.setTitle(R.string.no_network);
                    Snackbar snackbar = Snackbar.make(webView, R.string.no_network, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.refresh, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (webView.canGoBack()) {
                                webView.stopLoading();
                                webView.goBack();
                            }
                        }
                    });
                    snackbar.show();

                }
            }


            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError err) {
                onReceivedError(view, err.getErrorCode(), err.getDescription().toString(), req.getUrl().toString());
            }


        });

        webView.setWebChromeClient(new CustomChromeClient(this) {

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }


            @Override
            public void
            onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                try {
                    assert title != null;
                    if (title.contains("Facebook") || title.contains("1") || title.contains("https://m.facebook.com/") || title.contains("https://mobile.facebook.com/")) {
                        MainActivity.this.setTitle(R.string.app_name_pro);
                    } else {
                        MainActivity.this.setTitle(title);
                    }
                    if (title.contains("Offline")) {
                        MainActivity.this.setTitle(R.string.no_network);
                    }
                    if (title.contains("about:blank")) {
                        MainActivity.this.setTitle(R.string.app_name_pro);
                    }

                    FloatingActionButton share = (FloatingActionButton) findViewById(R.id.shareFab);
                    share.setLabelText("Share" + " " + webView.getTitle());
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FAB.close(true);
                drawerLayout.closeDrawers();
                appBar.setExpanded(true, true);
                switch (menuItem.getItemId()) {

                    case R.id.recent:
                        try {
                            MainActivity.this.setTitle(R.string.newsfeed);
                            webView.loadUrl("https://m.facebook.com/home.php?sk=h_chr&refid=8");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;

                    case R.id.online_friends:
                        MainActivity.this.setTitle(R.string.fb_chat);
                        webView.loadUrl("https://m.facebook.com/buddylist.php");
                        webView.requestFocus();
                        return true;
                    case R.id.top:
                        try {
                            webView.loadUrl("https://m.facebook.com/home.php?sk=h_nor&refid=8");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    case R.id.trending:
                        try {
                            webView.loadUrl("https://m.facebook.com/search/trending-news/?ref=bookmark&app_id=343553122467255");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;

                    case R.id.friends:
                        try {
                            MainActivity.this.setTitle(R.string.friends);
                            appBar.setExpanded(true, true);
                            webView.loadUrl("javascript:try{document.querySelector('#requests_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "/friends/center/friends/';}");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    case R.id.group:
                        try {
                            webView.loadUrl("https://m.facebook.com/groups/?category=membership");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    case R.id.pages:
                        try {
                            MainActivity.this.setTitle(R.string.pages);
                            webView.loadUrl("https://m.facebook.com/pages/launchpoint/?from=pages_nav_discover&ref=bookmarks");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    case R.id.photos:
                        try {
                            webView.loadUrl("https://m.facebook.com/profile.php?v=photos&soft=composer");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    case R.id.events:
                        try {
                            webView.loadUrl("https://m.facebook.com/events");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    case R.id.birthdays:
                        try {
                            webView.loadUrl("https://m.facebook.com/birthdays/?ref=bookmarks");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    case R.id.onthisday:
                        try {
                            webView.loadUrl("https://m.facebook.com/onthisday");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;
                    case R.id.saved:
                        try {
                            webView.loadUrl("https://m.facebook.com/saved");
                            webView.requestFocus();
                        } catch (NullPointerException ignored) {
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return true;

                    case R.id.settings:
                        onResume();
                        Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(settingsActivity);
                        return true;

                    case R.id.feedback:
                        Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "feedback@creativetrendsapps.com", null));
                        feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Feedback");
                        feedbackIntent.putExtra(Intent.EXTRA_TEXT, "Here is some feedback for" + " " + getString(R.string.app_name) + "\n\n" + Cleaner.getDeviceInfo(MainActivity.this));
                        startActivity(Intent.createChooser(feedbackIntent, getString(R.string.choose_email_client)));
                        return true;

                    case R.id.bugreport:
                        Intent bugIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                                "mailto", "bugs@creativetrendsapps.com", null));
                        bugIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + " Bug Report");
                        bugIntent.putExtra(Intent.EXTRA_TEXT, "Here is a bug that i've found in" + " " + getString(R.string.app_name) + "\n\n" + Cleaner.getDeviceInfo(MainActivity.this));
                        startActivity(Intent.createChooser(bugIntent, getString(R.string.choose_email_client)));
                        return true;

                    default:

                        return true;
                }
            }
        });


        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                FAB.close(true);
            }

            @Override
            public void onDrawerClosed(View drawerView) {


            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {


            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                FAB.close(true);

            }
        };


        actionBarDrawerToggle.syncState();

    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.logout).setVisible(true);

        return true;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.search:
                fri.unselected();
                mess.unselected();
                news.unselected();
                notifications.unselected();
                menuFb.unselected();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);
                try {
                    webView.loadUrl("javascript:try{document.querySelector('#search_jewel > a').click();}catch(e){window.location.href='" + FACEBOOK + "search';}");
                    webView.requestFocus();
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;


            case R.id.simple_pinned:
                drawerLayout.openDrawer(GravityCompat.END);
                return true;


            case R.id.copy_link:
                if (preferences.getString(getResources().getString(R.string.launch), "").equals(getResources().getString(R.string.error_code_value))) {
                    Snackbar.make(toolbar, getResources().getString(R.string.error_code) + " " + System.currentTimeMillis() + getResources().getString(R.string.code_number), Snackbar.LENGTH_LONG).show();
                } else {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newUri(getContentResolver(), "URI", Uri.parse(webView.getUrl()));
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(getBaseContext(), R.string.content_copy_link_done, Toast.LENGTH_LONG).show();
                }
                return true;


            case R.id.logout:
                try {
                    showLogOutDialog();
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;

            case R.id.close:
                if (preferences.getBoolean("confirm_close", false)) {
                    showExitDialog();
                } else {
                    if (Build.VERSION.SDK_INT >= 21) {
                        finishAndRemoveTask();
                    } else {
                        finish();
                    }
                }
                if (preferences.getBoolean("clear", false)) {
                    try {
                        deleteCache(getApplicationContext());
                    } catch (NullPointerException ignored) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getpreferences();
        try {
            ThemeUtils.isKeyBoardShowing(this);
            updateUserInfo();
        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        webView.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        String webViewUrl = getIntent().getDataString();
        String sharedUrl = getIntent().getStringExtra(Intent.EXTRA_TEXT);

        if (sharedUrl != null) {
            if (!sharedUrl.equals("")) {
                if (!sharedUrl.startsWith("http://") || !sharedUrl.startsWith("https://")) {

                    int startUrlIndex = sharedUrl.indexOf("http:");
                    if (startUrlIndex > 0) {

                        sharedUrl = sharedUrl.substring(startUrlIndex);
                    }
                }
                Intent shareIntent = new Intent(MainActivity.this, SharerActivity.class);
                shareIntent.setData(Uri.parse("https://www.facebook.com/sharer.php?u=" + sharedUrl));
                startActivity(shareIntent);

            }
        }

        try {
            if (getIntent().getExtras().getString("start_url") != null)
                webViewUrl = getIntent().getExtras().getString("start_url");


            if ("https://m.facebook.com/notifications".equals(webViewUrl))
                NotificationService.clearNotifications();
            BadgeHelper.updateNums(webView);


            if ("https://m.facebook.com/messages".equals(webViewUrl))
                NotificationService.clearMessages();
            BadgeHelper.updateNums(webView);


        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }


        webView.loadUrl(webViewUrl);
        webView.requestFocus();


        if (!NetworkConnection.isConnected(getApplicationContext()))


            if (getIntent().getBooleanExtra("apply_changes_to_app", false)) {
                recreate();
                Intent restart = new Intent(MainActivity.this, MainActivity.class);
                webView.onPause();
                startActivity(restart);
            }
    }


    public void setNotificationNum(int num) {
        if (num > 0) {
            final MinorView notifications = (MinorView) findViewById(R.id.notify_badges);
            assert notifications != null;
            notifications.addNotification(num);
        } else {
            final MinorView notifications = (MinorView) findViewById(R.id.notify_badges);
            assert notifications != null;
            notifications.removeNotification(num);
        }

    }


    public void setMessagesNum(int num) {
        if (num > 0) {
            final MinorView messages = (MinorView) findViewById(R.id.messages_badges);
            assert messages != null;
            messages.addNotification(num);
        } else {
            final MinorView fri = (MinorView) findViewById(R.id.messages_badges);
            assert fri != null;
            fri.removeNotification(num);

        }

    }


    public void setNewsNum(final int num) {
        if (num > 0) {
            final MinorView news = (MinorView) findViewById(R.id.news_badges);
            assert news != null;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    news.addfeed();
                }
            }, 650);

        } else {
            final MinorView news = (MinorView) findViewById(R.id.news_badges);
            assert news != null;
            news.removeNotification(num);

        }

    }

    public void setFriendsNum(int num) {
        if (num > 0) {
            final MinorView fri = (MinorView) findViewById(R.id.friends_badges);
            assert fri != null;
            fri.addNotification(num);
        } else {
            final MinorView fri = (MinorView) findViewById(R.id.friends_badges);
            assert fri != null;
            fri.removeNotification(num);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        //noinspection unused
        android.webkit.CookieSyncManager cS = CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
        if (getIntent().getDataString() != null) {
            shortcutSwitch(getIntent().getDataString());
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();
        webView.requestFocus();
        registerForContextMenu(webView);
        preferences.edit().putBoolean("activity_visible", true).apply();
        CookieSyncManager.getInstance().stopSync();
        try {

            ThemeUtils.pageStarted(MainActivity.this, webView);
            ThemeUtils.backgoundColorStyle(MainActivity.this, webView);
            BadgeHelper.updateFeedNum(webView);
            BadgeHelper.updateNums(webView);


            listBookmarks = PreferencesUtility.getBookmarks();
            updateUserInfo();
            if (preferences.getString("needs_lock", "true").equals("true") && (preferences.getBoolean("simple_locker", false))) {
                Intent intent = new Intent(MainActivity.this, SimpleLock.class);
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
                startActivity(intent);
            }

        } catch (NullPointerException ignored) {
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            PreferencesUtility.saveBookmarks(pins_adapt.getListBookmarks());
            BadgeHelper.updateFeedNum(webView);
        } catch (Exception ignored) {
        }
        if (webView != null) {
            unregisterForContextMenu(webView);
            webView.onPause();
            webView.pauseTimers();
            CookieSyncManager.getInstance().sync();
        }
        preferences.edit().putBoolean("activity_visible", false).apply();
        preferences.edit().putString("needs_lock", "true").apply();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            webView.clearHistory();
            FAB.close(true);
            if (preferences.getBoolean("clear", false)) {
                try {
                    deleteCache(getApplicationContext());
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                ThemeUtils.pageFinished(webView, webView.getUrl());
                BadgeHelper.updateFeedNum(webView);
                swipeRefreshLayout.setRefreshing(true);
                swipeRefreshLayout.setEnabled(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 600);

            } catch (Exception ignored) {

            }
        } else {
            if (preferences.getBoolean("confirm_close", false)) {
                if (back_pressed + 2000 > System.currentTimeMillis())
                    if (Build.VERSION.SDK_INT >= 21) {
                        finishAndRemoveTask();
                    } else {
                        finish();
                    }
                else
                    Toast.makeText(getBaseContext(), R.string.close_simple, Toast.LENGTH_SHORT).show();
                back_pressed = System.currentTimeMillis();
            } else {
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }

        }
    }

    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.destroy();
            super.onDestroy();
        }

        if (preferences.getBoolean("clear", false)) {
            try {
                deleteCache(getApplicationContext());
            } catch (NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        } else {
            if (mPendingImageUrlToSave != null)
                saveImageToDisk(mPendingImageUrlToSave);
        }
    }


    public boolean hasStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(this, storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mPendingImageUrlToSave != null)
                        saveImageToDisk(mPendingImageUrlToSave);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        WebView.HitTestResult result = webView.getHitTestResult();
        if (result != null) {
            int type = result.getType();

            if (type == WebView.HitTestResult.IMAGE_TYPE || type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                showLongPressedImageMenu(menu, result.getExtra());
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case ID_CONTEXT_MENU_SAVE_IMAGE:
                requestStoragePermission();
                break;
            case ID_CONTEXT_MENU_SHARE_IMAGE:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, mPendingImageUrlToSave);
                startActivity(Intent.createChooser(share, getString(R.string.context_share_image)));
                break;
            case ID_CONTEXT_MENU_COPY_IMAGE:
                ClipboardManager clipboard = (ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newUri(this.getContentResolver(), "URI", Uri.parse(mPendingImageUrlToSave));
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getBaseContext(), getString(R.string.content_copy_link_done), Toast.LENGTH_LONG).show();
        }
        return super.onContextItemSelected(item);
    }

    private void showLongPressedImageMenu(ContextMenu menu, String imageUrl) {
        mPendingImageUrlToSave = imageUrl;
        menu.setHeaderTitle(webView.getTitle());
        menu.add(0, ID_CONTEXT_MENU_SAVE_IMAGE, 0, getString(R.string.save_img));
        menu.add(0, ID_CONTEXT_MENU_SHARE_IMAGE, 1, getString(R.string.context_share_image));
        menu.add(0, ID_CONTEXT_MENU_COPY_IMAGE, 2, getString(R.string.context_copy_image_link));
    }

    @SuppressWarnings("Range")
    private void saveImageToDisk(String imageUrl) {
        if (!Sharer.resolve(this)) {
            mPendingImageUrlToSave = null;
            return;
        }

        try {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), appDirectoryName);

            if (!imageStorageDir.exists()) {

                //noinspection ResultOfMethodCallIgnored
                imageStorageDir.mkdirs();
            }


            String imgExtension = ".jpg";

            if (imageUrl.contains(".gif"))
                imgExtension = ".gif";
            else if (imageUrl.contains(".png"))
                imgExtension = ".png";

            String file = "IMG_" + System.currentTimeMillis() + imgExtension;
            DownloadManager dm = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(imageUrl);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, file)
                    .setTitle(file).setDescription(getString(R.string.save_img))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            dm.enqueue(request);


            Snackbar.make(webView, R.string.fragment_main_downloading, Snackbar.LENGTH_LONG).show();
        } catch (IllegalStateException ex) {
            Snackbar.make(webView, R.string.permission_denied, Snackbar.LENGTH_LONG).show();
        } catch (Exception ex) {
            Snackbar.make(webView, ex.toString(), Snackbar.LENGTH_LONG).show();
        } finally {
            mPendingImageUrlToSave = null;
        }
    }


    //login information code from Toffeed for Facebook which was helped by me: https://github.com/JakeLane/Toffeed/blob/master/app/src/main/java/me/jakelane/wrapperforfacebook/MainActivity.java#L495
    private void updateUserInfo() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String userID = object.getString("id");
                    simpleUser = object.getString("link");
                    final TextView name = (TextView) findViewById(R.id.profile_name);
                    if (name != null) {
                        name.setText(object.getString("name"));
                    }
                    final TextView email = (TextView) findViewById(R.id.user_email);
                    if (email != null) {
                        email.setText(object.getString("email"));
                    }

                    final ImageView profilePic = (ImageView) findViewById(R.id.profile_pic);
                    String picUrl = "https://graph.facebook.com/" + userID + "/picture?type=large";
                    Picasso.with(getApplicationContext()).load(picUrl).memoryPolicy(MemoryPolicy.NO_CACHE).into(profilePic);

                    final ImageView coverPic = (ImageView) findViewById(R.id.back_color);
                    Glide.with(getApplicationContext()).load(object.getJSONObject("cover").getString("source")).into((ImageView) findViewById(R.id.back_color));

                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,cover,link");
        parameters.putBoolean("redirect", false);
        request.setParameters(parameters);
        request.executeAsync();
    }


    private void initMinor() {
        assert news != null;
        news.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                appBar.setExpanded(true, true);
                if (scrollPosition > 10) {
                    scrollToTop();
                } else {
                    Snackbar snackbar = Snackbar.make(webView, R.string.aleady_at_top, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.refresh_question, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            webView.reload();
                        }
                    });
                    snackbar.show();
                }
                return true;
            }
        });
        news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.setTitle(R.string.newsfeed);
                BadgeHelper.updateNums(webView);
                news.selected();
                fri.unselected();
                notifications.unselected();
                menuFb.unselected();
                appBar.setExpanded(true, true);

                newsFB.setColorFilter(Color.parseColor("#FFFFFFFF"));
                friendsFB.setColorFilter(Color.parseColor("#9e9e9e"));
                notificationsFB.setColorFilter(Color.parseColor("#9e9e9e"));
                moreFB.setColorFilter(Color.parseColor("#9e9e9e"));
                if (mostrecent) {
                    webView.loadUrl("https://m.facebook.com/home.php?sk=h_chr&refid=8");
                } else {
                    webView.loadUrl("https://m.facebook.com/home.php?sk=h_nor&refid=8");
                }

            }


        });


        assert fri != null;
        fri.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                appBar.setExpanded(true, true);
                if (scrollPosition > 10) {
                    scrollToTop();
                } else {
                    Snackbar snackbar = Snackbar.make(webView, R.string.aleady_at_top, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.refresh_question, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            webView.reload();
                        }
                    });
                    snackbar.show();
                }
                return true;
            }
        });
        fri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.setTitle(R.string.friends);
                BadgeHelper.updateNums(webView);
                fri.selected();
                news.unselected();
                notifications.unselected();
                menuFb.unselected();
                appBar.setExpanded(true, true);
                newsFB.setColorFilter(Color.parseColor("#9e9e9e"));
                friendsFB.setColorFilter(Color.parseColor("#FFFFFFFF"));
                notificationsFB.setColorFilter(Color.parseColor("#9e9e9e"));
                moreFB.setColorFilter(Color.parseColor("#9e9e9e"));
                webView.loadUrl("https://mobile.facebook.com/friends/center/requests/?mff_nav=1&fb_ref=fbm&ref=bookmarks");


            }

        });

        assert mess != null;
        mess.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                appBar.setExpanded(true, true);
                if (scrollPosition > 10) {
                    scrollToTop();
                } else {
                    Snackbar snackbar = Snackbar.make(webView, R.string.aleady_at_top, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.refresh_question, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            webView.reload();
                        }
                    });
                    snackbar.show();
                }
                return true;
            }
        });
        mess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BadgeHelper.updateNums(webView);
                fri.unselected();
                mess.unselected();
                news.unselected();
                notifications.unselected();
                menuFb.unselected();
                NotificationService.clearMessages();
                BadgeHelper.updateNums(webView);
                Intent intent = new Intent(MainActivity.this, MessagesActivity.class);
                startActivity(intent);


            }

        });


        assert notifications != null;
        notifications.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                appBar.setExpanded(true, true);
                if (scrollPosition > 10) {
                    scrollToTop();
                } else {
                    Snackbar snackbar = Snackbar.make(webView, R.string.aleady_at_top, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.refresh_question, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            webView.reload();
                        }
                    });
                    snackbar.show();
                }
                return true;
            }
        });
        notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.setTitle(R.string.notifications);
                BadgeHelper.updateNums(webView);
                NotificationService.clearNotifications();
                notifications.selected();
                news.unselected();
                fri.unselected();
                menuFb.unselected();
                appBar.setExpanded(true, true);

                newsFB.setColorFilter(Color.parseColor("#9e9e9e"));
                friendsFB.setColorFilter(Color.parseColor("#9e9e9e"));
                notificationsFB.setColorFilter(Color.parseColor("#FFFFFFFF"));
                moreFB.setColorFilter(Color.parseColor("#9e9e9e"));
                webView.loadUrl("https://mobile.facebook.com/notifications.php?ref=bookmarks&app_id=1603421209951282");

            }

        });


        assert menuFb != null;
        menuFb.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                appBar.setExpanded(true, true);
                if (scrollPosition > 10) {
                    scrollToTop();
                } else {
                    Snackbar snackbar = Snackbar.make(webView, R.string.aleady_at_top, Snackbar.LENGTH_INDEFINITE);
                    snackbar.setAction(R.string.refresh_question, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            webView.reload();
                        }
                    });
                    snackbar.show();
                }
                return true;
            }
        });
        menuFb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.setTitle(R.string.settings_more);
                menuFb.selected();
                news.unselected();
                fri.unselected();
                notifications.unselected();
                appBar.setExpanded(true, true);

                newsFB.setColorFilter(Color.parseColor("#9e9e9e"));
                friendsFB.setColorFilter(Color.parseColor("#9e9e9e"));
                notificationsFB.setColorFilter(Color.parseColor("#9e9e9e"));
                moreFB.setColorFilter(Color.parseColor("#FFFFFFFF"));
                if (preferences.getBoolean("side_bar", false)) {
                    webView.loadUrl("https://mbasic.facebook.com/menu/bookmarks/?ref_component=mbasic_home_header&ref_page=%2Fwap%2Fhome.php&refid=8");
                } else {
                    webView.loadUrl("javascript:try{document.querySelector('#bookmarks_jewel > a').click();}catch(e){window.location.href='https://mobile.facebook.com/bookmarks';}");
                }


            }


        });
    }


    private void showExitDialog() {
        AlertDialog.Builder close = new AlertDialog.Builder(MainActivity.this);
        close.setTitle(getResources().getString(R.string.exit) + " " + getString(R.string.app_name));
        close.setMessage(getResources().getString(R.string.exit_message));
        close.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                if (Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
            }
        });
        close.setNeutralButton(R.string.cancel, null);
        close.show();
    }

    private void showLogOutDialog() {
        AlertDialog.Builder close = new AlertDialog.Builder(MainActivity.this);
        close.setTitle(getResources().getString(R.string.end));
        close.setMessage(getResources().getString(R.string.logout_message));
        close.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                prefManager.setFirstTimeLaunch(true);
                try {
                    deleteCache(getApplicationContext());
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    NotificationService.clearMessages();
                    NotificationService.clearNotifications();
                    LoginManager.getInstance().logOut();
                    final Intent intent = new Intent(SimpleApp.getContextOfApplication(), NotificationService.class);
                    SimpleApp.getContextOfApplication().stopService(intent);
                    if (Build.VERSION.SDK_INT >= 21) {
                        finishAndRemoveTask();
                    } else {
                        finish();
                    }
                    Toast.makeText(MainActivity.this, R.string.logged_out, Toast.LENGTH_SHORT).show();
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        close.setNeutralButton(R.string.cancel, null);
        close.show();
    }


    private void kitkatDialog() {
        AlertDialog.Builder close = new AlertDialog.Builder(MainActivity.this);
        close.setTitle(getResources().getString(R.string.kitkat_upload));
        close.setMessage(getResources().getString(R.string.kitkat_upload_message));
        close.setPositiveButton(R.string.try_upload, new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent photoIntent = new Intent(MainActivity.this, SharerActivity.class);
                photoIntent.setData(Uri.parse("https://m.facebook.com/photos/upload"));
                startActivity(photoIntent);
            }
        });
        close.setNeutralButton(R.string.cancel, null);
        close.show();
    }


    private void newDialog() {
        try {
            AlertDialog.Builder whats_new = new AlertDialog.Builder(MainActivity.this);
            whats_new.setTitle(R.string.change_log);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                whats_new.setMessage(Html.fromHtml(getResources().getString(R.string.about_new), Html.FROM_HTML_MODE_LEGACY));
            } else {
                //noinspection deprecation
                whats_new.setMessage(Html.fromHtml(getResources().getString(R.string.about_new)));
            }
            whats_new.setPositiveButton(R.string.ok, null);
            whats_new.show();
        } catch (Exception ignored) {

        }
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void injectSelect() {
        try {
            InputStream inputStream = getAssets().open("selecttext.css");
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private void getpreferences() {

        //notification code provided by FaceSlim, improved by me
        if (preferences.getBoolean("messages_activated", false) || (preferences.getBoolean("notifications_activated", false))) {
            final Intent intent = new Intent(SimpleApp.getContextOfApplication(), NotificationService.class);
            getBaseContext().startService(intent);
        } else {
            final Intent intent = new Intent(SimpleApp.getContextOfApplication(), NotificationService.class);
            getBaseContext().stopService(intent);
        }


        if (preferences.getBoolean("show_fab", false)) {
            FAB = (FloatingActionMenu) findViewById(R.id.fab);
            FAB.setVisibility(View.VISIBLE);

        } else {
            FAB = (FloatingActionMenu) findViewById(R.id.fab);
            FAB.setVisibility(View.GONE);
        }


        if (preferences.getBoolean("recent_off", false)) {
            navigationView.getMenu().findItem(R.id.recent).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.recent).setVisible(false);
        }

        if (preferences.getBoolean("top_off", false)) {
            navigationView.getMenu().findItem(R.id.top).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.top).setVisible(false);
        }


        if (preferences.getBoolean("trending_off", false)) {
            navigationView.getMenu().findItem(R.id.trending).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.trending).setVisible(false);

        }

        if (preferences.getBoolean("friends_off", false)) {
            navigationView.getMenu().findItem(R.id.friends).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.friends).setVisible(false);

        }

        if (preferences.getBoolean("groups_off", false)) {
            navigationView.getMenu().findItem(R.id.group).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.group).setVisible(false);

        }

        if (preferences.getBoolean("pages_off", false)) {
            navigationView.getMenu().findItem(R.id.pages).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.pages).setVisible(false);

        }

        if (preferences.getBoolean("photos_off", false)) {
            navigationView.getMenu().findItem(R.id.photos).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.photos).setVisible(false);

        }

        if (preferences.getBoolean("events_off", false)) {
            navigationView.getMenu().findItem(R.id.events).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.events).setVisible(false);

        }


        if (preferences.getBoolean("thisday_off", false)) {
            navigationView.getMenu().findItem(R.id.onthisday).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.onthisday).setVisible(false);

        }

        if (preferences.getBoolean("saved_off", false)) {
            navigationView.getMenu().findItem(R.id.saved).setVisible(true);
        } else {
            navigationView.getMenu().findItem(R.id.saved).setVisible(false);

        }


        if (preferences.getBoolean("message_shortcut", false)) {
            getPackageManager().setComponentEnabledSetting(new ComponentName(this, Messages.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            getPackageManager().setComponentEnabledSetting(new ComponentName(this, Messages.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }
        if (preferences.getBoolean("notification_shortcut", false)) {
            getPackageManager().setComponentEnabledSetting(new ComponentName(this, Notifications.class), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        } else {
            getPackageManager().setComponentEnabledSetting(new ComponentName(this, Notifications.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }

    }


    private static void getUserAgent(Context context) {
        boolean tabletSize = context.getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 2.2; SM-T800 Build/MMB29K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.85 Safari/537.36");
        } else {
            webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
        }
    }

    public void scrollToTop() {
        if (scrollPosition > 10) {
            scrollToTop(webView);
        }
    }


    public static void scrollToTop(WebView webView) {
        ObjectAnimator anim = ObjectAnimator.ofInt(webView, "scrollY", webView.getScrollY(), 0);
        anim.setDuration(500);
        anim.start();
    }

    @Override
    public void loadBookmark(String title, String url) {
        loadPage(url);
    }

    public void loadPage(String htmlLink) {
        webView.loadUrl(htmlLink);
    }

    private void shortcutSwitch(String dataString) {
        switch (dataString) {
            case "messages":
                try {
                    webView.loadUrl("https://m.facebook.com/messages/");
                    ThemeUtils.pageFinished(webView, webView.getUrl());
                    ThemeUtils.facebookTheme(MainActivity.this, webView);
                    ThemeUtils.backgoundColorStyle(MainActivity.this, webView);
                    BadgeHelper.updateNums(webView);

                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "notifications":
                try {
                    webView.loadUrl("https://m.facebook.com/notifications/");
                    ThemeUtils.pageFinished(webView, webView.getUrl());
                    ThemeUtils.facebookTheme(MainActivity.this, webView);
                    ThemeUtils.backgoundColorStyle(MainActivity.this, webView);
                    BadgeHelper.updateNums(webView);

                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "post":
                try {
                    Intent statusIntent = new Intent(MainActivity.this, SharerActivity.class);
                    statusIntent.setData(Uri.parse("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22" + FACEBOOK + "%3Fpageload%3Dcomposer%22%7D%7D)()"));
                    startActivity(statusIntent);
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }


        @Override
        public void handleMessage(Message msg) {
            final MainActivity activity = mActivity.get();
            if (activity != null) {
                String url = (String) msg.getData().get("url");
                try {
                    assert url != null;
                    if (url.contains("/photo.php?fbid") || url.contains("/photos/a.")) {
                        url = Cleaner.cleanAndDecodeUrl(url);
                        Log.v("Link long clicked", url);
                        Intent intent = new Intent(activity, PeekViewFB.class);
                        intent.setData(Uri.parse(url));
                        activity.startActivity(intent);
                    } else {
                        url = Cleaner.cleanAndDecodeUrl(url);
                        Log.v("Link long clicked", url);
                        Intent intent = new Intent(activity, PeekView.class);
                        intent.setData(Uri.parse(url));
                        activity.startActivity(intent);

                    }
                } catch (NullPointerException ignored) {

                }
            }
        }
    }


    @JavascriptInterface
    public void processVideo(String vidData, String vidID) {
        String mVideoName = vidID;
        mVideoName = mVideoName.substring(0, 8);
        Intent i = new Intent(MainActivity.this, VideoActivity.class);
        i.putExtra("VideoUrl", vidData);
        i.putExtra("VideoName", mVideoName);
        startActivity(i);
    }



    private void dialog() {
        if (preferences.getBoolean("feed_announce", true)) {
            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                    .setTarget(findViewById(R.id.news_badges))
                    .setFocalColour(ContextCompat.getColor(MainActivity.this, R.color.black_semi_transparent))
                    .setPrimaryText("More Than Tabs")
                    .setSecondaryText("Tap to refresh a tab. Long press any tab to jump to the top of any page.")
                    .setBackgroundColour(ThemeUtils.getColorPrimary(MainActivity.this))
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                            preferences.edit().putBoolean("feed_announce", false).apply();

                        }

                        @Override
                        public void onHidePromptComplete() {
                            preferences.edit().putBoolean("feed_announce", false).apply();
                            dialogMessages();
                        }
                    })

                    .show();
        }
    }


    private void dialogMessages() {
        if (preferences.getBoolean("message_announce", true)) {
            new MaterialTapTargetPrompt.Builder(MainActivity.this)
                    .setTarget(findViewById(R.id.messages_badges))
                    .setFocalColour(ContextCompat.getColor(MainActivity.this, R.color.black_semi_transparent))
                    .setPrimaryText("Messages Included")
                    .setSecondaryText("Use messages without the need for a separate app. Tap here to get started.")
                    .setBackgroundColour(ThemeUtils.getColorPrimary(MainActivity.this))
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                            preferences.edit().putBoolean("message_announce", false).apply();

                        }

                        @Override
                        public void onHidePromptComplete() {
                            preferences.edit().putBoolean("message_announce", false).apply();
                            drawerLayout.openDrawer(GravityCompat.END);
                            Toast.makeText(MainActivity.this, "Pin your favorite pages here for faster access.", Toast.LENGTH_LONG).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                                        drawerLayout.closeDrawer(GravityCompat.END);
                                    }
                                }

                            }, 3000);

                        }

                    })

                    .show();
        }
    }


    private final View.OnClickListener mFABClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.shareFab:
                        Intent shareIntent = new Intent(MainActivity.this, SharerActivity.class);
                        shareIntent.setData(Uri.parse("https://www.facebook.com/sharer.php?u=" + webView.getUrl()));
                        startActivity(shareIntent);
                    break;

                case R.id.locationFab:
                    Intent locationIntent = new Intent(MainActivity.this, SharerActivity.class);
                    locationIntent.setData(Uri.parse("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_location%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22https%3A%2F%2Fmobile.facebook.com%2F%3Fpageload%3Dcomposer_checkin%22%7D%7D)()"));
                    startActivity(locationIntent);
                    break;

                case R.id.photoFab:
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                        kitkatDialog();
                    } else {
                        Intent photoIntent = new Intent(MainActivity.this, SharerActivity.class);
                        photoIntent.setData(Uri.parse("https://m.facebook.com/photos/upload"));
                        startActivity(photoIntent);
                    }
                    break;

                case R.id.updateFab:
                    Intent statusIntent = new Intent(MainActivity.this, SharerActivity.class);
                    statusIntent.setData(Uri.parse("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22https%3A%2F%2Fmobile.facebook.com%2F%3Fpageload%3Dcomposer%22%7D%7D)()"));
                    startActivity(statusIntent);
                    break;

                case R.id.add_new_pin:
                    Pin bookmark = new Pin();
                    bookmark.setTitle(webView.getTitle());
                    bookmark.setUrl(webView.getUrl());
                    pins_adapt.addItem(bookmark);
                    drawerLayout.closeDrawer(GravityCompat.END);
                    Toast.makeText(getBaseContext(), "Added: " + webView.getTitle() + " to pins.", Toast.LENGTH_LONG).show();
                    break;

                default:
                    break;
            }
            FAB.close(true);
        }
    };

}