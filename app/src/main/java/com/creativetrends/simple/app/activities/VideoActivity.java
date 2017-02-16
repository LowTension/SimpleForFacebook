package com.creativetrends.simple.app.activities;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.services.NetworkConnection;
import com.creativetrends.simple.app.utils.ThemeUtils;

import java.io.File;

public class VideoActivity extends AppCompatActivity {
    private static final String mVideoName = "VideoName";
    private static final String mVideoUrl = "VideoUrl";
    private static final int REQUEST_STORAGE = 1;
    String VideoName;
    private String VideoURL;
    private VideoView mVideoViewPlayer;
    private static String appDirectoryName;
    private DownloadManager mgr=null;
    private long lastDownload=-1L;
    Toolbar toolbar;
    SharedPreferences preferences;
    static boolean isConnectedMobile;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isConnectedMobile = NetworkConnection.isConnectedMobile(SimpleApp.getContextOfApplication());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        ThemeUtils.setSettingsTheme(this, this);
        setContentView(R.layout.activity_video);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mgr=(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        appDirectoryName = getString(R.string.app_name_pro).replace(" ", " ");


        VideoURL = getIntent().getStringExtra(mVideoUrl);
        VideoName = getIntent().getStringExtra(mVideoName);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mVideoViewPlayer = (VideoView) findViewById(R.id.mVideoPlayer);



        try {
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(mVideoViewPlayer);
            Uri video = Uri.parse(VideoURL);
            mVideoViewPlayer.setMediaController(mediacontroller);
            mVideoViewPlayer.setVideoURI(video);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        mVideoViewPlayer.requestFocus();
        mVideoViewPlayer.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {

               mVideoViewPlayer.start();
            }
        });
        mVideoViewPlayer.setOnCompletionListener(new OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                try {
                    onBackPressed();
                } catch (Exception ignored) {

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.video_menu, menu);
        return true;
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //String[] storagePermission;
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.video_save:
                requestStoragePermission();
                return true;



            case R.id.photo_copy:
                ClipboardManager clipboard = (ClipboardManager) VideoActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.context_share_video), VideoURL);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getBaseContext(), R.string.content_copy_link_done, Toast.LENGTH_SHORT).show();
                return true;


            case R.id.photo_open:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    intent.setData(Uri.parse(VideoURL));
                    startActivity(intent);
                    finish();
                } catch (ActivityNotFoundException e) {

                    e.printStackTrace();
                }
                return true;


            default:
                return super.onOptionsItemSelected(item);

        }
    }


    private void requestStoragePermission() {
        String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!hasStoragePermission()) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_STORAGE);
        } else {
            if (VideoURL != null)
                saveVideoToDisk(VideoURL, null, null);
        }
    }


    private boolean hasStoragePermission() {
        String storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int hasPermission = ContextCompat.checkSelfPermission(this, storagePermission);
        return (hasPermission == PackageManager.PERMISSION_GRANTED);
    }


    private void saveVideoToDisk(final String url, final String contentDisposition, final String mimeType) {
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
                    Toast.makeText(VideoActivity.this, exc.toString(), Toast.LENGTH_LONG).show();
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
                Toast.makeText(VideoActivity.this, getString(R.string.downloading_on_mobile), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception exc) {
            Toast.makeText(VideoActivity.this, exc.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onDestroy() {
        super.onDestroy();
        System.gc();
        try{
            if(onComplete!=null)
                unregisterReceiver(onComplete);
        }catch(Exception ignored){

        }
    }

    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
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
