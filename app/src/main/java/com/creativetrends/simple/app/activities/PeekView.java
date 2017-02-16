package com.creativetrends.simple.app.activities;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.services.NetworkConnection;
import com.creativetrends.simple.app.utils.Downloader;
import com.creativetrends.simple.app.webview.WebViewScroll;
import com.creativetrends.simple.app.utils.AdRemoval;
import com.creativetrends.simple.app.utils.ThemeUtils;
import com.creativetrends.simple.app.utils.Cleaner;
import com.creativetrends.simple.app.utils.PreferencesUtility;
import com.creativetrends.simple.app.utils.SimpleListener;
import com.creativetrends.simple.app.utils.StaticUtils;
import com.creativetrends.simple.app.webview.FloatingWebView;
import com.creativetrends.simple.app.webview.CustomChromeClient;
import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**Created by Creative Trends Apps on 10/16/2016.*/

public class PeekView extends AppCompatActivity {
    private static final int REQUEST_STORAGE = 1;
    private static final int ID_CONTEXT_MENU_SAVE_IMAGE = 2562617;
    private static final int ID_CONTEXT_MENU_SHARE_IMAGE = 2562618;
    private static final int ID_CONTEXT_MENU_COPY_IMAGE = 2562619;
    private static final int FILECHOOSER_RESULTCODE = 1;
    SharedPreferences preferences;
    private static String appDirectoryName;
    public SwipeRefreshLayout swipeRefreshLayout;
    private String url;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private WebViewScroll webView;
    FloatingActionButton downloadImage;
    Toolbar toolbar;
    int themePusher = 0;
    public int scrollPosition = 0;
    public static Bitmap favoriteIcon;
    static boolean isConnectedMobile;
    private DownloadManager mgr=null;
    private long lastDownload=-1L;

    @SuppressWarnings({"deprecation", "unused", "typo"})
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        final boolean fbtheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("facebooktheme");
        final boolean blacktheme = PreferencesUtility.getInstance(this).getFreeTheme().equals("darktheme");
        final boolean dracula = PreferencesUtility.getInstance(this).getFreeTheme().equals("draculatheme");
        final boolean folio = PreferencesUtility.getInstance(this).getFreeTheme().equals("materialtheme");
        super.onCreate(savedInstanceState);
        showWindow();
        ThemeUtils.setSettingsTheme(this, this);
        isConnectedMobile = NetworkConnection.isConnectedMobile(SimpleApp.getContextOfApplication());
        setContentView(R.layout.activity_pickview);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        appDirectoryName = getString(R.string.app_name_pro).replace(" ", " ");
        downloadImage = (FloatingActionButton) findViewById(R.id.download_fab);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.GONE);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
        params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        mgr=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_float);
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

        AdRemoval.init(this);
        Uri url = getIntent().getData();
        webView = (WebViewScroll) findViewById(R.id.peek_webview);
        assert webView != null;
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
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        String content = null;
        webView.loadData(content, "text/html; charset=utf-8", null);
        webView.setListener(this, new SimpleListener(this, webView));
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }
        webView.setOnScrollChangedCallback(new WebViewScroll.OnScrollChangedCallback() {
            public void onScroll(int l, int t) {
                scrollPosition = t;
            }
        });
        webView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });



        try {

            webView.loadUrl(url.toString());
            if (webView.getUrl().contains("facebook")) {
                webView.getSettings().setUserAgentString("Mozilla/5.0 (BB10; Kbd) AppleWebKit/537.10+ (KHTML, like Gecko) Version/10.1.0.4633 Mobile Safari/537.10+");
            } else {
                webView.getSettings().setUserAgentString("");
            }
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        webView.setWebViewClient(new FloatingWebView() {


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



            @SuppressLint("NewApi")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(final WebView view, String url) {
                if (url != null)
                    url = Cleaner.cleanAndDecodeUrl(url);

                try {
                    assert url != null;
                    if ((url.contains("market://")
                            || url.contains("mailto:")
                            || url.contains("play.google")
                            || url.contains("tel:")
                            || url.contains("youtube")
                            || url.contains("vid:"))) {
                        view.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        finish();
                        return true;
                    }
                    if(url.contains("jpg")) {
                        if (Downloader.isImageFile(url) && hasStoragePermission()) {
                            Downloader.downloadFile(PeekView.this, url, Downloader.getFileName(url));
                            Toast.makeText(getBaseContext(), R.string.download_complete, Toast.LENGTH_LONG).show();
                            finish();
                            return true;
                        }

                    } else if ((url.contains("http://") || url.contains("https://"))) {
                        return false;
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


            @Override
            public void onPageStarted(WebView view, String url,  Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                try {
                    swipeRefreshLayout.setRefreshing(true);
                    swipeRefreshLayout.setEnabled(true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }, 900);

                    if (url.contains("facebook.com/")) {
                        if (preferences.getBoolean("auto_night", false) && ThemeUtils.isNightTime(PeekView.this)) {
                            injectAmoledCSS();
                            view.setBackgroundColor(ContextCompat.getColor(PeekView.this, R.color.black));
                        } else {
                            if (folio) {
                                injectMaterialCSS();
                                view.setBackgroundColor(ContextCompat.getColor(PeekView.this, R.color.defaultcolor));
                            }
                            if (blacktheme) {
                                injectAmoledCSS();
                                view.setBackgroundColor(ContextCompat.getColor(PeekView.this, R.color.black));
                            }
                            if (dracula) {
                                injectDraculaCSS();
                                view.setBackgroundColor(ContextCompat.getColor(PeekView.this, R.color.darcula));
                            }
                            if (fbtheme) {
                                injectDefaultCSS();
                                view.setBackgroundColor(ContextCompat.getColor(PeekView.this, R.color.defaultcolor));
                            }

                                injectRound();
                            if (preferences.getBoolean("hide_people", false)) {
                                injectHide();
                            }
                        }
                    }


                } catch (NullPointerException e) {
                    Log.e("onLoadResourceError", "" + e.getMessage());
                    e.printStackTrace();
                }
                themePusher = 0;

            }



            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                try {
                    if (view.getProgress() > 50 && themePusher < 3 && view.getUrl() != null) {
                        if (url.contains("/photo.php?fbid=")) {
                            downloadImage.setVisibility(View.VISIBLE);
                        }
                        if (url.contains("/photos/a.")) {
                            downloadImage.setVisibility(View.VISIBLE);
                        }
                        if (url.contains("/photo.php?fbid=")) {
                            downloadImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    webView.loadUrl("javascript:document.querySelector(\"a[href*='.jpg']\").click();");

                                }


                            });

                            if (url.contains("/photos/a.")) {
                                downloadImage.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (themePusher == 2) {
                                            webView.loadUrl("javascript:document.querySelector(\"a[href*='.jpg']\").click();");

                                        }
                                    }
                                });

                            }


                        }
                    }

                } catch (NullPointerException e) {
                    Log.e("onLoadResourceError", "" + e.getMessage());
                    e.printStackTrace();
                }
                themePusher += -10;
            }


            @SuppressLint("ResourceAsColor")
            @Override
            public void onPageFinished(WebView view, final String url) {
                super.onPageFinished(view, url);
                try {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
                    toolbar.setVisibility(View.VISIBLE);
                    //noinspection StatementWithEmptyBody
                    if (url.contains("facebook.com/")) {
                        if (preferences.getBoolean("auto_night", false) && ThemeUtils.isNightTime(PeekView.this)) {
                            injectAmoledCSS();
                            view.setBackgroundColor(ContextCompat.getColor(PeekView.this, R.color.black));
                        } else {
                            if (folio) {
                                injectMaterialCSS();
                                view.setBackgroundColor(ContextCompat.getColor(PeekView.this, R.color.defaultcolor));
                            }
                            if (blacktheme) {
                                injectAmoledCSS();
                                view.setBackgroundColor(ContextCompat.getColor(PeekView.this, R.color.black));
                            }
                            if (dracula) {
                                injectDraculaCSS();
                                view.setBackgroundColor(ContextCompat.getColor(PeekView.this, R.color.darcula));
                            }
                            if (fbtheme) {
                                injectDefaultCSS();
                                view.setBackgroundColor(ContextCompat.getColor(PeekView.this, R.color.defaultcolor));
                            }

                            injectRound();
                            if (preferences.getBoolean("hide_people", false)) {
                                injectHide();
                            }
                        }
                    }else {
                    }

                    if (url.contains("/photo.php?fbid=")) {
                        downloadImage.setVisibility(View.VISIBLE);
                    }
                    if (url.contains("/photos/a.")) {
                        downloadImage.setVisibility(View.VISIBLE);
                    }
                    if (url.contains("/photo.php?fbid=")) {
                        downloadImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(getBaseContext(), R.string.context_share_image_progress, Toast.LENGTH_SHORT).show();
                                webView.loadUrl("javascript:document.querySelector(\"a[href*='.jpg']\").click();");

                            }

                        });

                        if (url.contains("/photos/a.")) {
                            downloadImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Toast.makeText(getBaseContext(), R.string.context_share_image_progress, Toast.LENGTH_SHORT).show();
                                    webView.loadUrl("javascript:document.querySelector(\"a[href*='.jpg']\").click();");

                                }
                            });

                        }
                    }

                } catch (NullPointerException e) {
                    Log.e("onLoadResourceError", "" + e.getMessage());
                    e.printStackTrace();
                }

            }
        });


        webView.setWebChromeClient(new CustomChromeClient(this) {

            @Override
            public void
            onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                try {
                    PeekView.this.setTitle(webView.getTitle().replace("", ""));
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
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
                                    setColor(palette.getVibrantColor(ThemeUtils.getColorPrimary(PeekView.this)));
                                } else {
                                    setColor(palette.getMutedColor(ThemeUtils.getColorPrimary(PeekView.this)));

                                }
                            }
                        });
                    }
                } catch (Exception ignored) {

                }

            }



            // for Lollipop, all in one
            public boolean onShowFileChooser(
                    WebView webView, ValueCallback<Uri[]> filePathCallback,
                    FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

                    // create the file where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ignored) {

                    }


                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                Intent[] intentArray;
                if (takePictureIntent != null) {
                    intentArray = new Intent[]{takePictureIntent};
                } else {
                    intentArray = new Intent[0];
                }

                Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);

                startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);

                return true;
            }

            private File createImageFile() throws IOException {

                File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Folio");

                if (!imageStorageDir.exists()) {

                    //noinspection ResultOfMethodCallIgnored
                    imageStorageDir.mkdirs();
                }

                imageStorageDir = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                return imageStorageDir;
            }


            @SuppressWarnings("ResultOfMethodCallIgnored")

            void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType ) {
                mUploadMessage = uploadMsg;

                try {
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Folio");

                    if (!imageStorageDir.exists()) {

                        imageStorageDir.mkdirs();
                    }

                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                    mCapturedImageURI = Uri.fromFile(file); // save to the private variable

                    final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    // captureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");

                    Intent chooserIntent = Intent.createChooser(i, getString(R.string.image_chooser));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                } catch (Exception ignored) {

                }

            }

            // not needed but let's make it overloaded just in case
            // openFileChooser for Android < 3.0
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooser(uploadMsg, "");
            }

            // openFileChooser for other Android versions

            /** may not work on KitKat due to lack of implementation of openFileChooser() or onShowFileChooser()
             *  https://code.google.com/p/android/issues/detail?id=62220
             *  however newer versions of KitKat fixed it on some devices */
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooser(uploadMsg, acceptType);
            }

        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }

                Uri result = null;

                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception ignored) {

                }

                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }

            Uri[] results = null;

            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {

                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }

            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.quick_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.activity_slide_up, R.anim.activity_slide_down);
                return true;


            case R.id.quick_jump:
                if (scrollPosition > 10) {
                    scrollToTop();
                }else {
                    Snackbar.make(webView, R.string.aleady_at_top, Snackbar.LENGTH_SHORT).show();
                }
                return true;

            case R.id.quick_share:
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "");
                i.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                startActivity(Intent.createChooser(i, getString(R.string.share_action)));
                return true;


            default:
                return super.onOptionsItemSelected(item);


        }
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        PreferencesUtility.putString("needs_lock", "false");

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
            try {
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
            super.onBackPressed();
            PreferencesUtility.putString("needs_lock", "false");
            overridePendingTransition(R.anim.activity_slide_up, R.anim.activity_slide_down);

        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onStart() {
        super.onStart();
        //noinspection unused
        android.webkit.CookieSyncManager cS = CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().startSync();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            unregisterForContextMenu(webView);
            webView.onPause();
            webView.pauseTimers();
            PreferencesUtility.putString("needs_lock", "false");
            //noinspection deprecation
            CookieSyncManager.getInstance().sync();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();
        registerForContextMenu(webView);
        //noinspection deprecation
        CookieSyncManager.getInstance().stopSync();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();
            webView.destroy();
            webView = null;
            PreferencesUtility.putString("needs_lock", "false");
            try{
                if(onComplete!=null)
                    unregisterReceiver(onComplete);
            }catch(Exception ignored){

            }
        }
    }

    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        } else {
            if (url != null)
                saveImageToDisk(url, null, null);
        }
    }

    // check is storage permission granted
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
                    if (url != null)
                        saveImageToDisk(url, null, null);
                } else {
                    Toast.makeText(getBaseContext(), getString( R.string.permission_denied), Toast.LENGTH_SHORT).show();
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
                share.putExtra(Intent.EXTRA_TEXT, url);
                startActivity(Intent.createChooser(share, getString(R.string.context_share_image)));
                break;
            case ID_CONTEXT_MENU_COPY_IMAGE:
                ClipboardManager clipboard = (ClipboardManager) PeekView.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newUri(this.getContentResolver(), "URI", Uri.parse(url));
                clipboard.setPrimaryClip(clip);
                Snackbar.make(toolbar, R.string.content_copy_link_done, Snackbar.LENGTH_LONG).show();
        }
        return super.onContextItemSelected(item);
    }


    private void showLongPressedImageMenu(ContextMenu menu, String imageUrl) {
        url = imageUrl;
        menu.add(0, ID_CONTEXT_MENU_SAVE_IMAGE, 0, getString(R.string.save_img));
        menu.add(0, ID_CONTEXT_MENU_SHARE_IMAGE, 1, getString(R.string.context_share_image));
        menu.add(0, ID_CONTEXT_MENU_COPY_IMAGE, 2, getString(R.string.context_copy_image_link));
    }

    @SuppressWarnings("Range")
    private void saveImageToDisk(final String url, final String contentDisposition, final String mimeType) {
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
                    Toast.makeText(PeekView.this, exc.toString(), Toast.LENGTH_LONG).show();
                }
            }else {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, filename);
            }
            request.setVisibleInDownloadsUi(true);
            DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            lastDownload =dm.enqueue(request);
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            }
            if (intent != null) {
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            }
            if (intent != null) {
                intent.setType("*/*");
            }
            if(isConnectedMobile) {
                Toast.makeText(PeekView.this, getString(R.string.downloading_on_mobile), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception exc) {
            Toast.makeText(PeekView.this, exc.toString(), Toast.LENGTH_SHORT).show();
        }
    }



    public void showWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0.4f;
        getWindow().setAttributes(params);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        if (height > width) {
            getWindow().setLayout((int) (width * .9), (int) (height * .9));
        } else {
            getWindow().setLayout((int) (width * .9), (int) (height * .9));
        }


    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void injectDefaultCSS() {
        try {
            InputStream inputStream = getAssets().open("facebookmobile.css");
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


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void injectAmoledCSS() {
        try {
            InputStream inputStream = getAssets().open("amoled.css");
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


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void injectMaterialCSS() {
        try {
            InputStream inputStream = getAssets().open("materiallight.css");
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


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void injectDraculaCSS() {
        try {
            InputStream inputStream = getAssets().open("materialdark.css");
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

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void injectRound() {
        try {
            InputStream inputStream = getAssets().open("roundimages.css");
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




    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void injectHide() {
        try {
            InputStream inputStream = getAssets().open("hidepeople.css");
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

        int colorFrom = ThemeUtils.getColorPrimary(PeekView.this);
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

    public void queryStatus(View v) {
        Cursor c=mgr.query(new DownloadManager.Query().setFilterById(lastDownload));

        if (c==null) {
            Toast.makeText(this, "Download not found!", Toast.LENGTH_LONG).show();
        }
        else {
            c.moveToFirst();

            Log.d(getClass().getName(), "COLUMN_ID: "+
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_ID)));
            Log.d(getClass().getName(), "COLUMN_BYTES_DOWNLOADED_SO_FAR: "+
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
            Log.d(getClass().getName(), "COLUMN_LAST_MODIFIED_TIMESTAMP: "+
                    c.getLong(c.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)));
            Log.d(getClass().getName(), "COLUMN_LOCAL_URI: "+
                    c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
            Log.d(getClass().getName(), "COLUMN_STATUS: "+
                    c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS)));
            Log.d(getClass().getName(), "COLUMN_REASON: "+
                    c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON)));

            Toast.makeText(this, statusMessage(c), Toast.LENGTH_LONG).show();
        }
    }

    public void viewLog(View v) {
        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
    }

    private String statusMessage(Cursor c) {
        String msg="???";

        switch(c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
            case DownloadManager.STATUS_FAILED:
                msg="Download failed!";
                break;

            case DownloadManager.STATUS_PAUSED:
                msg="Download paused!";
                break;

            case DownloadManager.STATUS_PENDING:
                msg="Download pending!";
                break;

            case DownloadManager.STATUS_RUNNING:
                msg="Download in progress!";
                break;

            case DownloadManager.STATUS_SUCCESSFUL:
                msg="Download complete!";
                break;

            default:
                msg="Download is nowhere in sight";
                break;
        }

        return(msg);
    }
    BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            if(preferences.getBoolean("custom_pictures", false)){
                String path = preferences.getString("picture_save", Environment.getExternalStorageState() + appDirectoryName);
                Toast.makeText(ctxt, ctxt.getResources().getString(R.string.save_to) + " " + Uri.parse(path + File.separator), Toast.LENGTH_LONG).show();
            }else {
                Toast.makeText(ctxt, ctxt.getResources().getString(R.string.save_to) + " " + Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, Toast.LENGTH_LONG).show();
            }
        }
    };

}