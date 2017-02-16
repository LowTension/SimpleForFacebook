package com.creativetrends.simple.app.tile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.service.quicksettings.TileService;

import com.creativetrends.simple.app.activities.MainActivity;

/**Created by Creative Trends Apps on 10/12/2016.*/

@SuppressLint("NewApi")
public class STile extends TileService {


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onTileAdded() {
        super.onTileAdded();
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
    }

    @Override
    public void onClick() {
        super.onClick();
        startActivity(new Intent(this, MainActivity.class));
    }
}