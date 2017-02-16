package com.creativetrends.simple.app.lock;

import android.support.v7.app.ActionBarActivity;

import com.creativetrends.simple.app.interfaces.LifeCycleInterface;



public class PinActionBarActivity extends ActionBarActivity {
    private static LifeCycleInterface mLifeCycleListener;

    public static void setListener(LifeCycleInterface listener) {
        if (mLifeCycleListener != null) {
            mLifeCycleListener = null;
        }
        mLifeCycleListener = listener;
    }

    public static void clearListeners() {
        mLifeCycleListener = null;
    }

    public static boolean hasListeners() {
        return (mLifeCycleListener != null);
    }

    @Override
    protected void onResume() {
        if (mLifeCycleListener != null) {
            mLifeCycleListener.onActivityResumed(PinActionBarActivity.this);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (mLifeCycleListener != null) {
            mLifeCycleListener.onActivityPaused(PinActionBarActivity.this);
        }
        super.onPause();
    }
}
