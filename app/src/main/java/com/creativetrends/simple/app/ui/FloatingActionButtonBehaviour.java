package com.creativetrends.simple.app.ui;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.util.AttributeSet;
import android.view.View;

import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

/**Created by Family on 10/8/2016.*/

public class FloatingActionButtonBehaviour extends CoordinatorLayout.Behavior {
    private float mTranslationY;

    public FloatingActionButtonBehaviour(Context context, AttributeSet attrs) {
    }

    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        if ((child instanceof FloatingActionMenu) && (dependency instanceof Snackbar.SnackbarLayout)) {
            updateTranslation(parent, child, dependency);
        }
        return false;
    }

    private void updateTranslation(CoordinatorLayout parent, View child, View dependency) {
        float translationY = getTranslationY(parent, child);
        if (translationY != this.mTranslationY) {
            ViewCompat.animate(child).cancel();
            if (Math.abs(translationY - this.mTranslationY) == ((float) dependency.getHeight())) {
                ViewCompat.animate(child).translationY(translationY).setListener(null);
            } else {
                ViewCompat.setTranslationY(child, translationY);
            }
            this.mTranslationY = translationY;
        }
    }

    private float getTranslationY(CoordinatorLayout parent, View child) {
        float minOffset = 0.0f;
        List dependencies = parent.getDependencies(child);
        int z = dependencies.size();
        for (int i = 0; i < z; i++) {
            View view = (View) dependencies.get(i);
            if ((view instanceof Snackbar.SnackbarLayout) && parent.doViewsOverlap(child, view)) {
                minOffset = Math.min(minOffset, ViewCompat.getTranslationY(view) - ((float) view.getHeight()));
            }
        }
        return minOffset;
    }
}