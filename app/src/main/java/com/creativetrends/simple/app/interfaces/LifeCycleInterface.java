package com.creativetrends.simple.app.interfaces;

import android.app.Activity;


public interface LifeCycleInterface {


    void onActivityResumed(Activity activity);

    void onActivityPaused(Activity activity);
}
