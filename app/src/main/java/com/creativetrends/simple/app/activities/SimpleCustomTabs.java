package com.creativetrends.simple.app.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.URLUtil;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.services.NetworkConnection;
import com.creativetrends.simple.app.services.NetworkStatus;
import com.creativetrends.simple.app.utils.AdRemoval;
import com.creativetrends.simple.app.utils.Pin;
import com.creativetrends.simple.app.utils.PreferencesUtility;
import com.creativetrends.simple.app.utils.Sharer;
import com.creativetrends.simple.app.utils.SimpleListener;
import com.creativetrends.simple.app.utils.StaticUtils;
import com.creativetrends.simple.app.utils.ThemeUtils;
import com.creativetrends.simple.app.webview.CustomChromeClient;
import com.creativetrends.simple.app.webview.WebViewScroll;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static android.os.Build.VERSION_CODES.M;
import static com.creativetrends.simple.app.activities.MainActivity.pins_adapt;


//Created by Jorell on 3/15/2016.

@SuppressWarnings("ALL")
public class SimpleCustomTabs extends AppCompatActivity {
    private static final String TAG = SimpleCustomTabs.class.getSimpleName();
    private static final int REQUEST_STORAGE = 1;
    private static final int REQUEST_LOCATION = 2;
    private static final int ID_CONTEXT_MENU_SAVE_IMAGE = 2562617;
    private static final int ID_CONTEXT_MENU_SHARE_IMAGE = 2562618;
    private static final int ID_CONTEXT_MENU_COPY_IMAGE = 2562619;
    public static Bitmap favoriteIcon;
    private static SharedPreferences preferences;
    public SwipeRefreshLayout swipeRefreshLayout;
    View mCustomView;
    FrameLayout customViewContainer, menuHolder;
    private ScrollView menuScroll;
    private CardView overflowMenu;
    private String mPendingImageUrlToSave, appDirectoryName;
    private Toolbar toolbar;
    private WebViewScroll webView;
    TextView toolbarTitle, copy, open, fav;


    private ImageView secure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setSettingsTheme(this, this);
        setContentView(R.layout.activity_browser);
        overflowmenu();
        appDirectoryName = getString(R.string.app_name_pro).replace(" ", " ");
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        customViewContainer = (FrameLayout) findViewById(R.id.fullscreen_custom_content);
        secure = (ImageView) findViewById(R.id.lockButton);
        toolbar = (Toolbar) findViewById(R.id.browser_toolbar);
        toolbarTitle = (TextView) findViewById(R.id.toolbarTitle);
        copy = (TextView) findViewById(R.id.simple_copy_link);
        open = (TextView) findViewById(R.id.simple_open_link);
        fav = (TextView) findViewById(R.id.simple_fav_link);
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        ResolveInfo ri = getPackageManager().resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
        String pn = ri.loadLabel(getPackageManager()).toString();
        if(pn.contains("Android")){
        open.setText(getResources().getString(R.string.open_with) +"..." );
        }else {
            open.setText(getResources().getString(R.string.open_with) + " " + pn);
        }
        setSupportActionBar(toolbar);

        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }

            AdRemoval.init(this);

        Uri url = getIntent().getData();


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.simple_swipe);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.white));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ThemeUtils.getColorPrimary(this));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try{
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
                } catch (Exception ignored) {

                }
            }
        });



        webView = (WebViewScroll) findViewById(R.id.simple_webview);

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
        webView.setVerticalScrollBarEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
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
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.setListener(this, new SimpleListener(this, webView));
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        try {
            webView.loadUrl(url.toString());
        } catch (Exception ignored) {

        }


        webView.setWebViewClient(new WebViewClient() {
            private Map<String, Boolean> loadedUrls = new HashMap<>();

            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                    try {
                        boolean ad;
                        if (!loadedUrls.containsKey(url)) {
                            ad = AdRemoval.isAd(url);
                            loadedUrls.put(url, ad);
                        } else {
                            ad = loadedUrls.get(url);
                        }
                        return ad ? AdRemoval.createEmptyResource() :
                                super.shouldInterceptRequest(view, url);
                    } catch (Exception ignored) {

                    }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                try {
                    if (url.startsWith("market:") || url.startsWith("https://m.youtube.com")
                            || url.startsWith("https://play.google.com") || url.startsWith("magnet:")
                            || url.startsWith("mailto:") || url.startsWith("intent:")
                            || url.startsWith("https://mail.google.com") || url.startsWith("https://plus.google.com")
                            || url.startsWith("geo:") || url.startsWith("google.streetview:")) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        finish();
                        return true;

                    } else if ((url.contains("http://") || url.contains("https://"))) {
                        return false;
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Log.e("shouldOverrideUrlLoad", "No Activity to handle action", e);
                        e.printStackTrace();
                    }
                    return true;

                } catch (Exception ignored) {
                    return true;
                }
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try {
                    swipeRefreshLayout.setRefreshing(true);
                    swipeRefreshLayout.setEnabled(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, 2000);
                    ImageView goForward = (ImageView) findViewById(R.id.simple_go_forward);
                    if (webView.canGoForward()) {
                        goForward.setImageDrawable(getResources().getDrawable(R.drawable.ic_go_forward));
                    } else {
                        goForward.setImageDrawable(getResources().getDrawable(R.drawable.ic_go_forward_light));
                    }
                    ((TextView) findViewById(R.id.toolbarSub)).setText(url);
                    if ((url.contains("https://"))) {
                        secure.setVisibility(View.VISIBLE);
                    } else {
                        secure.setVisibility(View.GONE);
                    }
                    ImageView browserrefresh = (ImageView) findViewById(R.id.simple_refresh);
                    ImageView browserstop = (ImageView) findViewById(R.id.simple_stop);

                    browserstop.setVisibility(View.VISIBLE);
                    if (browserrefresh.getVisibility() == View.VISIBLE) {
                        browserrefresh.setVisibility(View.GONE);
                    }


                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                try {
                    swipeRefreshLayout.setRefreshing(false);
                    ImageView browserstop = (ImageView) findViewById(R.id.simple_stop);
                    browserstop.setVisibility(View.GONE);
                    ImageView browserrefresh = (ImageView) findViewById(R.id.simple_refresh);
                    browserrefresh.setVisibility(View.VISIBLE);

                } catch (Exception ignored) {

                }
            }
        });

        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, String userAgent, final String contentDisposition, final String mimeType, long contentLength) {
                final String filename1 = URLUtil.guessFileName(url, contentDisposition, mimeType);

                Snackbar snackbar = Snackbar.make(webView, "Download " + filename1 + "?", Snackbar.LENGTH_INDEFINITE);
                snackbar.setActionTextColor(Color.parseColor("#1e88e5"));
                snackbar.setAction("DOWNLOAD", new View.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT >= M) {
                            if (ActivityCompat.checkSelfPermission(SimpleCustomTabs.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(SimpleCustomTabs.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
                            } else {
                                try {
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                                    String filename = URLUtil.guessFileName(url, contentDisposition, mimeType);
                                    String path = preferences.getString("picture_save", Environment.getExternalStorageState() + appDirectoryName);
                                    File folio = new File(path);
                                    if(!folio.exists()){
                                        //noinspection ResultOfMethodCallIgnored
                                        folio.mkdir();
                                    }
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                                    request.setAllowedOverRoaming(false);

                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    if(preferences.getBoolean("custom_pictures", false)){
                                        try{
                                            request.setDestinationUri(Uri.parse("file://" + path + File.separator + filename));
                                        } catch (Exception exc) {
                                            Toast.makeText(SimpleCustomTabs.this, exc.toString(), Toast.LENGTH_LONG).show();
                                        }
                                    }else {
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, filename);
                                    }
                                    request.setVisibleInDownloadsUi(true);

                                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                    dm.enqueue(request);

                                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    intent.setType("*/*");


                                    Snackbar.make(webView, R.string.fragment_main_downloading, Snackbar.LENGTH_LONG).show();
                                } catch (Exception exc) {
                                    Snackbar.make(webView, exc.toString(), Snackbar.LENGTH_LONG).show();
                                }
                            }
                        } else {
                            try {
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                                String filename = URLUtil.guessFileName(url, contentDisposition, mimeType);
                                String path = preferences.getString("picture_save", Environment.getExternalStorageState() + appDirectoryName);
                                File folio = new File(path);
                                if(!folio.exists()){
                                    //noinspection ResultOfMethodCallIgnored
                                    folio.mkdir();
                                }
                                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                                request.setAllowedOverRoaming(false);

                                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                if(preferences.getBoolean("custom_pictures", false)){
                                    try{
                                        request.setDestinationUri(Uri.parse("file://" + path + File.separator + filename));
                                    } catch (Exception exc) {
                                        Toast.makeText(SimpleCustomTabs.this, exc.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }else {
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, filename);
                                }
                                request.setVisibleInDownloadsUi(true);

                                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                                dm.enqueue(request);

                                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                                intent.addCategory(Intent.CATEGORY_OPENABLE);
                                intent.setType("*/*");

                                Snackbar.make(webView, R.string.fragment_main_downloading, Snackbar.LENGTH_LONG).show();
                            } catch (Exception exc) {
                                Snackbar.make(webView, exc.toString(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    }
                });
                snackbar.show();
            }
        });


        webView.setWebChromeClient(new CustomChromeClient(this) {

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }

            @Override
            public void
            onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                try {
                    toolbarTitle.setText(title);
                    texFade();
                } catch (Exception ignored) {

                }

            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
                try {
                    favoriteIcon = icon;
                    if (icon != null && StaticUtils.isLollipop()) {
                        Palette.from(icon).generate(new Palette.PaletteAsyncListener() {
                            @Override
                            public void onGenerated(Palette palette) {
                                Palette.Swatch vibrant = palette.getVibrantSwatch();
                                if (vibrant != null) {
                                    setColor(palette.getVibrantColor(ContextCompat.getColor(SimpleCustomTabs.this, R.color.md_blue_grey_500)));
                                } else {
                                    setColor(palette.getMutedColor(ContextCompat.getColor(SimpleCustomTabs.this, R.color.md_blue_grey_500)));

                                }
                            }
                        });
                    }
                } catch (Exception ignored) {

                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browser_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                try{
                finish();
                } catch (Exception ignored) {

                }
                return true;
            case R.id.simple_share:
                try{
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Share current page");
                    i.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                    startActivity(Intent.createChooser(i, getString(R.string.share_action)));
                } catch (Exception ignored) {

                }
                return true;
            case R.id.simple_overflow:
                try{
                showMenu();
                } catch (Exception ignored) {

                }
                return true;


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
        try{
        if (overflowMenu.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
        } catch (Exception ignored) {

        }
    }



    @Override
    public void onBackPressed() {
        try{
        if (overflowMenu.getVisibility() == View.VISIBLE) {
            hideMenu();
        }
        if(MainActivity.webView.canGoBack()){
           MainActivity.webView.goBack();
        }
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
            try {
                webView.clearCache(true);
                webView.clearHistory();
            } catch (Exception ignored) {

            }
        }
        } catch (Exception ignored) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try{
        if (webView != null) {
            unregisterForContextMenu(webView);
            webView.onPause();
            webView.pauseTimers();
        }
        } catch (Exception ignored) {

        }

    }

    private void hideMenu() {
        Animation fade = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        fade.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                overflowMenu.setVisibility(View.GONE);
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        overflowMenu.startAnimation(fade);
        menuHolder.setClickable(false);
        menuHolder.setFocusable(false);
        menuHolder.setSoundEffectsEnabled(false);
    }

    private void showMenu() {
        menuScroll.setScrollY(0);
        Animation grow = AnimationUtils.loadAnimation(this, R.anim.translate_from_top);
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        grow.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                overflowMenu.setVisibility(View.VISIBLE);
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        in.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        overflowMenu.startAnimation(grow);
        copy.startAnimation(grow);
        open.startAnimation(grow);
        fav.startAnimation(grow);
        menuHolder.setClickable(true);
        menuHolder.setFocusable(true);
        overflowMenu.setSoundEffectsEnabled(false);
        menuHolder.setSoundEffectsEnabled(false);
    }

    @Override
    protected void onStart(){
        super.onStart();
        PreferencesUtility.putString("needs_lock", "false");
    }

    @Override
    protected void onResume() {
        super.onResume();
        try{
        webView.onResume();
        webView.resumeTimers();
        registerForContextMenu(webView);
        PreferencesUtility.putString("needs_lock", "false");
        } catch (Exception ignored) {

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();
            webView.destroy();
            webView = null;
            PreferencesUtility.putString("needs_lock", "false");
            if(MainActivity.webView.canGoBack()){
                MainActivity.webView.goBack();
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


    private boolean hasStoragePermission() {
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
                    Snackbar.make(webView, R.string.permission_denied, Snackbar.LENGTH_LONG).show();
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
                try{
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, mPendingImageUrlToSave);
                    startActivity(Intent.createChooser(share, getString(R.string.context_share_image)));
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case ID_CONTEXT_MENU_COPY_IMAGE:
                try{
                    ClipboardManager clipboard = (ClipboardManager) SimpleCustomTabs.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newUri(this.getContentResolver(), "URI", Uri.parse(mPendingImageUrlToSave));
                    clipboard.setPrimaryClip(clip);
                    Snackbar.make(webView, R.string.content_copy_link_done, Snackbar.LENGTH_LONG).show();
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

            // default image extension
            String imgExtension = ".jpg";

            if (imageUrl.contains(".gif"))
                imgExtension = ".gif";
            else if (imageUrl.contains(".png"))
                imgExtension = ".png";

            String file = "IMG_" + System.currentTimeMillis() + imgExtension;
            String path = preferences.getString("picture_save", Environment.getExternalStorageState() + appDirectoryName);
            File folio = new File(path);
            if(!folio.exists()){
                //noinspection ResultOfMethodCallIgnored
                folio.mkdir();
            }
            DownloadManager dm = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(imageUrl);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            if(preferences.getBoolean("custom_pictures", false)){
                try{
                    request.setDestinationUri(Uri.parse("file://" + path + File.separator + file));
                } catch (Exception exc) {
                    Toast.makeText(SimpleCustomTabs.this, exc.toString(), Toast.LENGTH_LONG).show();
                }
            }else {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, file);
            }
                    request.setTitle(file).setDescription(getString(R.string.save_img))
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



    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.menu_holder:
                        hideMenu();
                    return;
                case R.id.simple_go_forward:
                        ImageView goForward = (ImageView) findViewById(R.id.simple_go_forward);
                        if (webView.canGoForward()) {
                            goForward.setImageDrawable(getResources().getDrawable(R.drawable.ic_go_forward));
                            webView.goForward();
                            hideMenu();
                        } else {
                            goForward.setImageDrawable(getResources().getDrawable(R.drawable.ic_go_forward_light));
                        }
                    return;
                case R.id.simple_info:
                        hideMenu();
                        AlertDialog.Builder info = new AlertDialog.Builder(SimpleCustomTabs.this);
                        info.setTitle(webView.getUrl());
                        if (webView.getUrl().contains("https://")) {
                            info.setMessage(getResources().getString(R.string.private_info));
                        } else {
                            info.setMessage(getResources().getString(R.string.none_private_info));
                        }
                        info.setPositiveButton("OKAY", new AlertDialog.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });
                        info.setNeutralButton(null, null);
                        info.show();
                    return;
                case R.id.simple_refresh:
                    webView.reload();

                    return;

                case R.id.simple_copy_link:
                        ClipboardManager clipboard = (ClipboardManager) SimpleCustomTabs.this.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newUri(getContentResolver(), "URI", Uri.parse(webView.getUrl()));
                        clipboard.setPrimaryClip(clip);
                        Snackbar.make(webView, R.string.content_copy_link_done, Snackbar.LENGTH_LONG).show();

                    return;

                case R.id.simple_open_link:
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(webView.getUrl()));
                        startActivity(intent);
                    return;

                case R.id.simple_stop:
                        hideMenu();
                        webView.stopLoading();

                    return;

                case R.id.simple_fav_link:
                        hideMenu();
                        Pin bookmark = new Pin();
                        bookmark.setTitle(webView.getTitle());
                        bookmark.setUrl(webView.getUrl());
                        pins_adapt.addItem(bookmark);
                        Toast.makeText(getBaseContext(), "Added: " + webView.getTitle() + " to favorites.", Toast.LENGTH_LONG).show();

                    return;

                default:
                        hideMenu();
                    return;

            }
        }

    };

    private void overflowmenu() {
        overflowMenu = (CardView) findViewById(R.id.main_menu);
        menuScroll = (ScrollView) findViewById(R.id.scroller);
        menuHolder = (FrameLayout) findViewById(R.id.menu_holder);
        menuHolder.setOnClickListener(onClickListener);
        menuHolder.setClickable(false);
        menuHolder.setFocusable(false);
        findViewById(R.id.simple_go_forward).setOnClickListener(onClickListener);
        findViewById(R.id.simple_info).setOnClickListener(onClickListener);
        findViewById(R.id.simple_refresh).setOnClickListener(onClickListener);
        findViewById(R.id.simple_copy_link).setOnClickListener(onClickListener);
        findViewById(R.id.simple_open_link).setOnClickListener(onClickListener);
        findViewById(R.id.simple_stop).setOnClickListener(onClickListener);
        findViewById(R.id.simple_fav_link).setOnClickListener(onClickListener);


    }


    private void setColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getWindow().getStatusBarColor(), StaticUtils.darkColor(color));
            colorAnimation.setDuration(100);
            colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor((int) animator.getAnimatedValue());
                    }
                }
            });
            colorAnimation.start();
        }

        int colorFrom = ContextCompat.getColor(this, R.color.md_blue_grey_500);
        Drawable backgroundFrom = toolbar.getBackground();
        if (backgroundFrom instanceof ColorDrawable) colorFrom = ((ColorDrawable) backgroundFrom).getColor();
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, color);
        colorAnimation.setDuration(100);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                toolbar.setBackgroundColor((int) animator.getAnimatedValue());
                swipeRefreshLayout.setProgressBackgroundColorSchemeColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();

    }


    private void texFade() {
        Animation grow = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        grow.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        toolbarTitle.startAnimation(grow);
    }




}