package com.creativetrends.simple.app.lock;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.creativetrends.simple.app.interfaces.KeyboardButtonClickedListener;
import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.utils.PreferencesUtility;
import com.creativetrends.simple.app.utils.ThemeUtils;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;


public abstract class AppLockActivity extends PinActivity implements KeyboardButtonClickedListener, View.OnClickListener, FingerprintUiHelper.Callback {

    public static final String TAG = AppLockActivity.class.getSimpleName();
    public static final String ACTION_CANCEL = TAG + ".actionCancelled";
    private static final int DEFAULT_PIN_LENGTH = 4;
    public static CallbackManager callbackmanager;
    protected TextView mStepTextView;
    protected TextView mForgotTextView;
    protected PinCodeRoundView mPinCodeRoundView;
    protected KeyboardView mKeyboardView;
    protected ImageView mFingerprintImageView;
    protected TextView mFingerprintTextView;
    protected LockManager mLockManager;
    protected FingerprintManager mFingerprintManager;
    protected FingerprintUiHelper mFingerprintUiHelper;
    protected int mType = AppLock.UNLOCK_PIN;
    protected int mAttempts = 1;
    protected String mPinCode;
    protected String mOldPinCode;
    String folioUser = null;
    private boolean isCodeSuccessful = false;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            preferences = PreferenceManager.getDefaultSharedPreferences(this);
            ThemeUtils.setSettingsTheme(this, this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
            }
            if (preferences.getBoolean("nav", false) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ThemeUtils.getColorPrimaryDark(this));
            }
            setContentView(getContentView());
            updateUserInfo();
            initLayout(getIntent());
        }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        PreferencesUtility.putString("needs_lock", "false");
        initLayout(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferencesUtility.putString("needs_lock", "false");
        initLayoutForFingerprint();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mFingerprintUiHelper != null) {
            mFingerprintUiHelper.stopListening();
        }
    }


    private void initLayout(Intent intent) {

        Bundle extras = intent.getExtras();
        if (extras != null) {
            mType = extras.getInt(AppLock.EXTRA_TYPE, AppLock.UNLOCK_PIN);
        }

        mLockManager = LockManager.getInstance();
        mPinCode = "";
        mOldPinCode = "";

        enableAppLockerIfDoesNotExist();
        mLockManager.getAppLock().setPinChallengeCancelled(false);

        mStepTextView = (TextView) this.findViewById(R.id.pin_code_step_textview);
        mPinCodeRoundView = (PinCodeRoundView) this.findViewById(R.id.pin_code_round_view);
        mPinCodeRoundView.setPinLength(this.getPinLength());
        mForgotTextView = (TextView) this.findViewById(R.id.pin_code_forgot_textview);
        mForgotTextView.setOnClickListener(this);
        mKeyboardView = (KeyboardView) this.findViewById(R.id.pin_code_keyboard_view);
        mKeyboardView.setKeyboardButtonClickedListener(this);

        mForgotTextView.setText(getForgotText());
        mForgotTextView.setVisibility(mLockManager.getAppLock().shouldShowForgot() ? View.VISIBLE : View.GONE);

        setStepText();
    }


    private void initLayoutForFingerprint() {
        mFingerprintImageView = (ImageView) this.findViewById(R.id.pin_code_fingerprint_imageview);
        mFingerprintTextView = (TextView) this.findViewById(R.id.pin_code_fingerprint_textview);
        if (mType == AppLock.UNLOCK_PIN && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mFingerprintManager = (FingerprintManager) getSystemService(Context.FINGERPRINT_SERVICE);
            mFingerprintUiHelper = new FingerprintUiHelper.FingerprintUiHelperBuilder(mFingerprintManager).build(mFingerprintImageView, mFingerprintTextView, this);
            try {
                if (mFingerprintManager.isHardwareDetected() && mFingerprintUiHelper.isFingerprintAuthAvailable()
                        && mLockManager.getAppLock().isFingerprintAuthEnabled()) {
                    mFingerprintImageView.setVisibility(View.VISIBLE);
                    mFingerprintTextView.setVisibility(View.VISIBLE);
                    mFingerprintUiHelper.startListening();
                } else {
                    mFingerprintImageView.setVisibility(View.GONE);
                    mFingerprintTextView.setVisibility(View.GONE);
                }
            } catch (SecurityException e) {
                Log.e(TAG, e.toString());
                mFingerprintImageView.setVisibility(View.GONE);
                mFingerprintTextView.setVisibility(View.GONE);
            }
        } else {
            mFingerprintImageView.setVisibility(View.GONE);
            mFingerprintTextView.setVisibility(View.GONE);
        }
    }


    @SuppressWarnings("unchecked")
    private void enableAppLockerIfDoesNotExist() {
        try {
            if (mLockManager.getAppLock() == null) {
                mLockManager.enableAppLock(this, getCustomAppLockActivityClass());
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    private void setStepText() {
        mStepTextView.setText(getStepText(mType));
    }


    public String getStepText(int reason) {
        String msg = null;
        switch (reason) {
            case AppLock.DISABLE_PINLOCK:
                msg = getString(R.string.pin_code_step_disable, this.getPinLength());
                break;
            case AppLock.ENABLE_PINLOCK:
                msg = getString(R.string.pin_code_step_create, this.getPinLength());
                break;
            case AppLock.CHANGE_PIN:
                msg = getString(R.string.pin_code_step_change, this.getPinLength());
                break;
            case AppLock.UNLOCK_PIN:
                msg = getString(R.string.pin_code_step_unlock, this.getPinLength());
                break;
            case AppLock.CONFIRM_PIN:
                msg = getString(R.string.pin_code_step_enable_confirm, this.getPinLength());
                break;
        }
        return msg;
    }

    public String getForgotText() {
        return getString(R.string.pin_code_forgot_text);
    }


    @Override
    public void finish() {
        super.finish();
        if (isCodeSuccessful) {
            PreferencesUtility.putString("needs_lock", "false");
            if (mLockManager != null) {
                AppLock appLock = mLockManager.getAppLock();
                if (appLock != null) {
                    appLock.setLastActiveMillis();

                }
            }
        }

    }


    @Override
    public void onKeyboardClick(KeyboardButtonEnum keyboardButtonEnum) {
        if (mPinCode.length() < this.getPinLength()) {
            int value = keyboardButtonEnum.getButtonValue();

            if (value == KeyboardButtonEnum.BUTTON_CLEAR.getButtonValue()) {
                if (!mPinCode.isEmpty()) {
                    setPinCode(mPinCode.substring(0, mPinCode.length() - 1));
                } else {
                    setPinCode("");
                }
            } else {
                setPinCode(mPinCode + value);
            }
        }
    }


    @Override
    public void onRippleAnimationEnd() {
        if (mPinCode.length() == this.getPinLength()) {
            onPinCodeInputed();
        }
    }


    protected void onPinCodeInputed() {
        switch (mType) {
            case AppLock.DISABLE_PINLOCK:
                if (mLockManager.getAppLock().checkPasscode(mPinCode)) {
                    setResult(RESULT_OK);
                    mLockManager.getAppLock().setPasscode(null);
                    onPinCodeSuccess();
                    finish();
                } else {
                    onPinCodeError();
                }
                break;
            case AppLock.ENABLE_PINLOCK:
                mOldPinCode = mPinCode;
                setPinCode("");
                mType = AppLock.CONFIRM_PIN;
                setStepText();
                break;
            case AppLock.CONFIRM_PIN:
                if (mPinCode.equals(mOldPinCode)) {
                    setResult(RESULT_OK);
                    mLockManager.getAppLock().setPasscode(mPinCode);
                    onPinCodeSuccess();
                    finish();
                } else {
                    mOldPinCode = "";
                    setPinCode("");
                    mType = AppLock.ENABLE_PINLOCK;
                    setStepText();
                    onPinCodeError();
                }
                break;
            case AppLock.CHANGE_PIN:
                if (mLockManager.getAppLock().checkPasscode(mPinCode)) {
                    mType = AppLock.ENABLE_PINLOCK;
                    setStepText();
                    setPinCode("");
                    onPinCodeSuccess();
                } else {
                    onPinCodeError();
                }
                break;
            case AppLock.UNLOCK_PIN:
                if (mLockManager.getAppLock().checkPasscode(mPinCode)) {
                    setResult(RESULT_OK);
                    onPinCodeSuccess();
                    finish();
                } else {
                    onPinCodeError();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getBackableTypes().contains(mType)) {
            if (AppLock.UNLOCK_PIN == getType()) {
                mLockManager.getAppLock().setPinChallengeCancelled(true);
                LocalBroadcastManager
                        .getInstance(this)
                        .sendBroadcast(new Intent().setAction(ACTION_CANCEL));
            }
            super.onBackPressed();
        }
    }

    @Override
    public void onAuthenticated() {
        Log.e(TAG, "Fingerprint READ!!!");
        setResult(RESULT_OK);
        onPinCodeSuccess();
        finish();
    }

    @Override
    public void onError() {
        Log.e(TAG, "Fingerprint READ ERROR!!!");
    }


    public List<Integer> getBackableTypes() {
        return Arrays.asList(AppLock.CHANGE_PIN, AppLock.DISABLE_PINLOCK);
    }


    public abstract void showForgotDialog();


    protected void onPinCodeError() {
        onPinFailure(mAttempts++);
        Thread thread = new Thread() {
            public void run() {
                mPinCode = "";
                mPinCodeRoundView.refresh(mPinCode.length());
                Animation animation = AnimationUtils.loadAnimation(AppLockActivity.this, R.anim.shake);
                mKeyboardView.startAnimation(animation);
            }
        };
        runOnUiThread(thread);
    }

    protected void onPinCodeSuccess() {
        isCodeSuccessful = true;
        onPinSuccess(mAttempts);
        mAttempts = 1;

    }


    public void setPinCode(String pinCode) {
        mPinCode = pinCode;
        mPinCodeRoundView.refresh(mPinCode.length());
    }


    public int getType() {
        return mType;
    }


    @Override
    public void onClick(View view) {
        showForgotDialog();
    }


    public abstract void onPinFailure(int attempts);


    public abstract void onPinSuccess(int attempts);


    public int getContentView() {
        return R.layout.activity_pin_code;
    }


    public int getPinLength() {
        return AppLockActivity.DEFAULT_PIN_LENGTH;
    }


    public Class<? extends AppLockActivity> getCustomAppLockActivityClass() {
        return this.getClass();
    }

    private void updateUserInfo() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String userID = object.getString("id");
                    folioUser = object.getString("link");
                    final TextView name = (TextView) findViewById(R.id.lock_name_new);
                    if (name != null) {
                        name.setText(getResources().getString(R.string.welcome) + ", " + (object.getString("first_name") + "!"));
                    }
                    final ImageView profilePic = (ImageView) findViewById(R.id.pin_facebook_image);
                    String picUrl = "https://graph.facebook.com/" + userID + "/picture?type=large";
                    Picasso.with(getApplicationContext()).load(picUrl).into(profilePic);
                } catch (NullPointerException ignored) {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,cover,link,first_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);
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
}
