package com.creativetrends.simple.app.utils;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.creativetrends.simple.app.R;

import java.io.File;


public final class Downloader extends Activity{


    public static void downloadFile(Context context, String url, String fileName) {
       SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String appDirectoryName = context.getString(R.string.app_name_pro).replace(" ", " ");
        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
            String path = preferences.getString("picture_save", Environment.getExternalStorageState() + appDirectoryName);
            File folio = new File(path);
            if(!folio.exists()){
                //noinspection ResultOfMethodCallIgnored
                folio.mkdir();
            }
            if(preferences.getBoolean("custom_pictures", false)){
                try{
                    request.setDestinationUri(Uri.parse("file://" + path + File.separator + fileName));
                } catch (Exception exc) {
                    Toast.makeText(context, exc.toString(), Toast.LENGTH_LONG).show();
                }
            }else {
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES + File.separator + appDirectoryName, fileName);
            }
            request.allowScanningByMediaScanner();

            // notify user when download is completed
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // start download
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } else {
            try {
                if (url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    context.startActivity(intent);
                }
            } catch (android.content.ActivityNotFoundException e) {
                // can't start activity
            }
        }
    }


    public static boolean isDownloadableFile(String url) {
        int index = url.indexOf("?");
        if (index > -1) {
            url = url.substring(0, index);
        }
        url = url.toLowerCase();

        for (String type : SimpleConfig.DOWNLOAD_FILE_TYPES) {
            if (url.endsWith(type)) return true;
        }

        return false;
    }


    public static boolean isImageFile(String url) {
        int index = url.indexOf("?");
        if (index > -1) {
            url = url.substring(0, index);
        }
        url = url.toLowerCase();

        for (String type : SimpleConfig.IMAGE_FILE_TYPES) {
            if (url.endsWith(type)) return true;
        }

        return false;
    }


    public static String getFileName(String url) {
        int index = url.indexOf("?");
        if (index > -1) {
            url = url.substring(0, index);
        }
        url = url.toLowerCase();

        index = url.lastIndexOf("/");
        if (index > -1) {
            return url.substring(index + 1, url.length());
        } else {
            return Long.toString(System.currentTimeMillis());
        }
    }
}
