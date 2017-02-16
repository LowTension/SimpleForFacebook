package com.creativetrends.simple.app.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.services.NetworkConnection;
import com.creativetrends.simple.app.utils.PreferencesUtility;
import com.creativetrends.simple.app.utils.ThemeUtils;
import com.creativetrends.simple.app.webview.FloatingWebView;

import java.io.File;
import java.io.IOException;

// Created by Jorell on 6/18/2016.
public class SharerActivity extends AppCompatActivity {
    public static Uri galleryImageUri = null;
    public Context context;
    public SwipeRefreshLayout swipeRefreshLayout;
    int themePusher = 0;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI;

    // the same for Android 5.0 methods only
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private WebView webView;
    ProgressDialog sharedialog;
    private static final int REQUEST_LOCATION = 2;
    boolean mCreatingActivity = true;
    @SuppressWarnings({"deprecation", "unused", "typo"})
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (!mCreatingActivity) {
            setUpWindow();
            ThemeUtils.setSettingsTheme(this, this);
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_float);
        }else{
            super.onCreate(savedInstanceState);
            setUpWindow();
            ThemeUtils.setSettingsTheme(this, this);
            setContentView(R.layout.activity_float);
        }


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_float);
        swipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.white));
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(ThemeUtils.getColorPrimary(this));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
                if (!NetworkConnection.isConnected(SimpleApp.getContextOfApplication()))
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

        Uri url = getIntent().getData();

        webView = (WebView) findViewById(R.id.text_box);
        webView.getSettings().setJavaScriptEnabled(true);
        if (preferences.getBoolean("allow_location", false)) {
            webView.getSettings().setGeolocationEnabled(true);
            webView.getSettings().setGeolocationDatabasePath(getFilesDir().getPath());
        } else {
            webView.getSettings().setGeolocationEnabled(false);
        }
        //noinspection deprecation
        webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.loadUrl(url.toString());
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

        sharedialog = new ProgressDialog(SharerActivity.this);
        sharedialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                if (webView != null) {
                    webView.destroy();
                    webView.removeAllViews();
                }
                finish();
            }
        });


        sharedialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                webView.loadUrl("");
                webView.stopLoading();
                webView.clearHistory();
                webView.clearCache(true);
                System.gc();
            }
        });


        webView.setWebViewClient(new FloatingWebView() {


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                try {
                    swipeRefreshLayout.setRefreshing(false);
                    swipeRefreshLayout.setEnabled(false);
                    ThemeUtils.backgoundColorStyle(SharerActivity.this, view);
                    if(!sharedialog.isShowing()){
                        sharedialog = ProgressDialog.show(SharerActivity.this, null, getResources().getString(R.string.context_share_image_progress));
                        setUpWindow();

                       }
                    if (url.contains("sharer")) {
                        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
                        if (url.contains("mobile.facebook.com")) {
                            url = url.replace("mobile.facebook.com", "www.facebook.com");
                        } else if (url.contains("m.facebook.com")) {
                            url = url.replace("m.facebook.com", "www.facebook.com");
                        }
                    } else if (url.equals("https://www.facebook.com/")) {
                        webView.getSettings().setUserAgentString("Mozilla/5.0 (iPad; CPU OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1");
                        webView.getSettings().setLoadWithOverviewMode(true);
                        webView.getSettings().setUseWideViewPort(true);
                    } else {
                        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36");
                    }
                    ThemeUtils.pageFinished(view, url);

                } catch (Exception e) {
                    Log.e("onLoadResourceError", "" + e.getMessage());
                    e.printStackTrace();
                }
                themePusher = 0;

            }


            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                try {
                    if (themePusher < 5) {
                        ThemeUtils.facebookTheme(SharerActivity.this, view);

                    }
                    if (themePusher == 10) {
                        ThemeUtils.facebookTheme(SharerActivity.this, view);
                    }
                    if (view.getProgress() > 50 && themePusher < 3 && view.getUrl() != null) {
                        if ((view.getUrl().startsWith("https://www.facebook.com/") || view.getUrl().startsWith("https://web.facebook.com/")) && !view.getUrl().contains("sharer.php")) {
                            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.head.appendChild(node); } addStyleString('#pagelet_bluebar, #leftCol, li._1tm3:nth-child(2), #rightCol, ._5pcb, ._4-u2.mvm._495i._4-u8 { display: none !important; }');addStyleString('li._1tm3:nth-child(2), ._5pcb, ._4-u2.mvm._495i._4-u8 { display: none !important; }');addStyleString('#contentCol { margin: auto !important; }');addStyleString('#globalContainer, #contentArea, ._59s7  { width: auto !important; }');");
                            ThemeUtils.facebookTheme(SharerActivity.this, view);
                            if (themePusher == 2) {
                                sharedialog.dismiss();
                                showWindow();
                                if (galleryImageUri != null) {
                                    view.loadUrl("javascript:document.querySelector('input[type=\"file\"]').click();");
                                }
                            }
                        }
                        themePusher += -10;
                    }
                } catch (Exception ignored) {

                }
            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeRefreshLayout.setRefreshing(false);
                swipeRefreshLayout.setEnabled(false);
                try {
                    if (url.contains("?pageload=composer")) {
                        if (url.contains("checkin")) {
                        view.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_location%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22https%3A%2F%2Fmobile.facebook.com%2F%3Fpageload%3Dcomposer_checkin%22%7D%7D)()");
                            requestLocationPermission();
                        }else {
                            view.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_overview%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22https%3A%2F%2Fmobile.facebook.com%2F%3Fpageload%3Dcomposer%22%7D%7D)()");
                        }
                        if (url.contains("?pageload=composer")) {
                        if (url.contains("photo")) {
                            view.loadUrl("javascript:(function()%7Btry%7Bdocument.querySelector('button%5Bname%3D%22view_photo%22%5D').click()%7Dcatch(_)%7Bwindow.location.href%3D%22https%3A%2F%2Fmobile.facebook.com%2F%3Fpageload%3Dcomposer_photo%22%7D%7D)()");
                        }
                        if (url.contains("photos")) {
                                view.loadUrl("javascript:document.querySelector('input[type=\"file\"]').click();");
                        }

                      }
                    } else if (!url.contains("home.php") || url.contains("sharer.php")) {
                        if (sharedialog.isShowing()) {
                            sharedialog.dismiss();
                            showWindow();
                        }
                        if (url.contains("sharer.php")) {
                            view.loadUrl("javascript:function addStyleString(str) { var node = document.createElement('style'); node.innerHTML = str; document.head.appendChild(node); } addStyleString('._5e9y { width: auto !important; }');addStyleString('.uiSelectorRight .uiSelectorMenuWrapper { left:auto; right:auto; }');addStyleString('._5l58 ._5h_u ._3_tq { bottom:auto; }');addStyleString('textarea{ height: auto; resize:  vertical; }');");
                        } else if (url.startsWith("https://www.facebook.com") || url.startsWith("https://web.facebook.com")) {
                            view.loadUrl("javascript:function removeElement(id) { var node = document.getElementById(id); node.parentNode.removeChild(node); } removeElement('pagelet_bluebar');removeElement('leftCol');removeElement('rightCol');");
                            if (galleryImageUri != null) {
                                view.loadUrl("javascript:document.querySelector('input[type=\"file\"]').click();");
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e("onLoadResourceError", "" + e.getMessage());
                    e.printStackTrace();
                }
            }


            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //noinspection StatementWithEmptyBody
                if (view.getUrl() != null && (view.getUrl().contains("composer") || url.contains("dialog/return"))) {

                }
                assert url != null;
                if (url.contains("/home.php")) {
                    MainActivity.webView.loadUrl("https://mobile.facebook.com");
                    finish();
                }
                if (url.contains("dialog/return")) {
                    finish();
                }
                if (url.contains("/home.php?s") && !url.contains("/home.php?sk=")) {
                    Toast.makeText(getBaseContext(),R.string.success, Toast.LENGTH_LONG ).show();
                    finish();
                    MainActivity.webView.loadUrl("https://mobile.facebook.com");
                }
                return false;
            }


        });


        webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                //if(!sharedialog.isShowing()){
                    //sharedialog = ProgressDialog.show(SharerActivity.this, null, getResources().getString(R.string.com_facebook_loading));
                    //setUpWindow();

                //}
                //if (progress == 100&& sharedialog.isShowing()) {
                    //sharedialog.dismiss();

                //}
            }

            @Override
            public void
            onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                try {
                    if (title != null && title.contains("https://www.facebook.com/dialog/return")) {
                        finish();
                        MainActivity.webView.loadUrl("https://mobile.facebook.com");
                    }
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback);
                callback.invoke(origin, true, false);
            }


            public void onCloseWindow(WebView window) {
                window.destroy();
                window.removeAllViews();
                super.onCloseWindow(window);
                finish();
            }


            // for Lollipop, all in one
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (mFilePathCallback != null) {
                    mFilePathCallback.onReceiveValue(null);
                }
                    mFilePathCallback = filePathCallback;

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                    } catch (IOException ignored) {

                    }
                    if (photoFile != null) {
                        mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    } else {
                        takePictureIntent = null;
                    }
                }

                Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                contentSelectionIntent.setType("image/*");

                if (galleryImageUri != null) {
                    ValueCallback access$1000 = mFilePathCallback;
                    Object obj = new Uri[FILECHOOSER_RESULTCODE];
                    //obj[0] = galleryImageUri;
                    mFilePathCallback.onReceiveValue(null);
                    mFilePathCallback = null;
                    galleryImageUri = null;
                    return true;
                }

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

            @SuppressWarnings("ResultOfMethodCallIgnored")
            private File createImageFile() throws IOException {
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Folio");
                if (!imageStorageDir.exists()) {
                    imageStorageDir.mkdirs();
                }
                imageStorageDir = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                return imageStorageDir;
            }


            @SuppressWarnings("ResultOfMethodCallIgnored")
            void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                if (galleryImageUri != null) {
                    mUploadMessage.onReceiveValue(galleryImageUri);
                    mUploadMessage = null;
                    galleryImageUri = null;
                    return;
                }
                try {
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Folio");
                    if (!imageStorageDir.exists()) {
                        imageStorageDir.mkdirs();
                    }
                    File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    mCapturedImageURI = Uri.fromFile(file);
                    final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
                    Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                    i.addCategory(Intent.CATEGORY_OPENABLE);
                    i.setType("image/*");
                    Intent chooserIntent = Intent.createChooser(i, getString(R.string.image_chooser));
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});
                    startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
                } catch (Exception ignored) {

                }

            }


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
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onStart(){
        super.onStart();
        PreferencesUtility.putString("needs_lock", "false");
    }


    @Override
    public void onBackPressed() {
            super.onBackPressed();
            overridePendingTransition(R.anim.activity_slide_up, R.anim.activity_slide_down);
            sharedialog.dismiss();
        }





    @Override
    protected void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
            webView.pauseTimers();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        webView.resumeTimers();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.removeAllViews();
            webView.destroy();
            webView = null;
            System.gc();
            sharedialog.dismiss();
        }
    }


    public void setUpWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        getWindow().setAttributes(params);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        if (height > width) {
            getWindow().setLayout(-0, -0);
        } else {
            getWindow().setLayout(-0, -0);
        }
        {

        }
    }


    public void showWindow() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        getWindow().setAttributes(params);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        if (height > width) {
            getWindow().setLayout((int) (width * .9), (int) (height * 0.9));
        } else {
            getWindow().setLayout((int) (width * .9), (int) (height * 0.9));
        }
        {

        }
    }

    private void requestLocationPermission() {
        String[] permissions = new String[] { Manifest.permission.ACCESS_FINE_LOCATION };
        if (!hasLocationPermission()) {
            Log.i("Location:", "needed");
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION);
        } else {
            Log.i("Location:", "granted");
        }
    }

    private boolean hasLocationPermission() {
        String locationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
        int hasPermission = ContextCompat.checkSelfPermission(this, locationPermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    webView.reload();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
