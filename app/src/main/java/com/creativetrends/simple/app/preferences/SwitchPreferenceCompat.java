package com.creativetrends.simple.app.preferences;

import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;

import com.creativetrends.simple.app.R;


public class SwitchPreferenceCompat extends CheckBoxPreference {

    public SwitchPreferenceCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SwitchPreferenceCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SwitchPreferenceCompat(Context context) {
        super(context);
        init();
    }

    private void init() {
        setWidgetLayoutResource(R.layout.simple_switchpreference);
    }

}