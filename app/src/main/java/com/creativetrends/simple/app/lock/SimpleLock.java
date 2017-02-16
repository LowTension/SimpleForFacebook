package com.creativetrends.simple.app.lock;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.utils.PreferencesUtility;

import uk.me.lewisdeane.ldialogs.BaseDialog;
import uk.me.lewisdeane.ldialogs.CustomDialog;

public class SimpleLock extends AppLockActivity {

    @SuppressWarnings("deprecation")
    @Override
    public void showForgotDialog() {
        Resources res = getResources();
        // Create the builder with required paramaters - Context, Title, Positive Text
        CustomDialog.Builder builder = new CustomDialog.Builder(this,
                res.getString(R.string.activity_dialog_title),
                res.getString(R.string.activity_dialog_accept));
        builder.content(res.getString(R.string.activity_dialog_content));
        builder.negativeText(res.getString(R.string.activity_dialog_decline));

        //Set theme
        builder.darkTheme(false);
        builder.typeface(Typeface.SANS_SERIF);
        builder.positiveColor(res.getColor(R.color.light_blue_500)); // int res, or int colorRes parameter versions available as well.
        builder.negativeColor(res.getColor(R.color.light_blue_500));
        builder.rightToLeft(false); // Enables right to left positioning for languages that may require so.
        builder.titleAlignment(BaseDialog.Alignment.CENTER);
        builder.buttonAlignment(BaseDialog.Alignment.CENTER);
        builder.setButtonStacking(false);

        //Set text sizes
        builder.titleTextSize((int) res.getDimension(R.dimen.activity_dialog_title_size));
        builder.contentTextSize((int) res.getDimension(R.dimen.activity_dialog_content_size));
        builder.positiveButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_positive_button_size));
        builder.negativeButtonTextSize((int) res.getDimension(R.dimen.activity_dialog_negative_button_size));

        //Build the dialog.
        CustomDialog customDialog = builder.build();
        customDialog.setCanceledOnTouchOutside(false);
        //noinspection ConstantConditions
        customDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                Intent intent = new Intent(SimpleLock.this, SimpleLock.class);
                intent.putExtra(AppLock.EXTRA_TYPE, AppLock.CHANGE_PIN);
                startActivity(intent);
            }

            @Override
            public void onCancelClick() {

            }
        });

        // Show the dialog.
        customDialog.show();
    }

    @Override
    protected void onResume() {
     super.onResume();
     PreferencesUtility.putString("needs_lock", "false");
    }

    @Override
    public void onPinFailure(int attempts) {
    }

    @Override
    public void onPinSuccess(int attempts) {
    PreferencesUtility.putString("needs_lock", "true");
    finish();


    }

    @Override
    public int getPinLength() {
        return super.getPinLength();
    }
}