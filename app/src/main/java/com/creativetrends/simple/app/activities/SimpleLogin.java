package com.creativetrends.simple.app.activities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.utils.PrefManager;
import com.creativetrends.simple.app.utils.PreferencesUtility;
import com.creativetrends.simple.app.utils.ThemeUtils;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;
import java.util.Calendar;


public class SimpleLogin extends AppCompatActivity {
    public static CallbackManager callbackmanager;
    private PrefManager prefManager;
    ImageView logo;
    TextView terms, policy;
    CardView login;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setSettingsTheme(this, this);
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        setContentView(R.layout.activity_login_screen);
        login = (CardView) findViewById(R.id.custom_facebook_button);
        logo = (ImageView) findViewById(R.id.launcher_icon);
        terms =(TextView) findViewById(R.id.terms);
        policy = (TextView) findViewById(R.id.policy);
        texFade();

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder terms = new AlertDialog.Builder(SimpleLogin.this);
                terms.setTitle(getResources().getString(R.string.terms_settings));
                terms.setMessage(getResources().getString(R.string.eula_string));
                terms.setPositiveButton(R.string.accept, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                terms.show();
            }
        });

        policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder terms = new AlertDialog.Builder(SimpleLogin.this);
                terms.setTitle("Privacy Policy");
                //noinspection deprecation
                terms.setMessage(Html.fromHtml(getString(R.string.policy_about)));
                terms.setPositiveButton(R.string.accept, new AlertDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });
                terms.show();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFblogin();
            }
        });
        setScreenElements();
    }

    //code for screen elements taken from ML manager
    @SuppressLint("SetTextI18n")
    private void setScreenElements() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        TextView appNameVersion = (TextView) findViewById(R.id.version);
        assert appNameVersion != null;
        appNameVersion.setText(getResources().getString(R.string.version) + " " + PreferencesUtility.getAppVersionName(getApplicationContext()));
        TextView copyright = (TextView) findViewById(R.id.copyright_text);
        copyright.setText(getResources().getString(R.string.copy_right) + year + " " + getResources().getString(R.string.creative_trends) + ".");
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
    }


    @TargetApi(19)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    private void onFblogin() {
        callbackmanager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email"));
        LoginManager.getInstance().registerCallback(callbackmanager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                launchHomeScreen();
            }

            @Override
            public void onCancel() {
                prefManager.setFirstTimeLaunch(true);

            }

            @Override
            public void onError(FacebookException error) {
                prefManager.setFirstTimeLaunch(true);
                Toast.makeText(SimpleLogin.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        });
        LoginManager.getInstance().setLoginBehavior(LoginBehavior.WEB_ONLY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }

    private void launchHomeScreen() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(SimpleLogin.this, MainActivity.class));
        finish();
    }

    private void texFade() {
        Animation grow = AnimationUtils.loadAnimation(this, R.anim.jump_from_down);
        grow.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
            }
            public void onAnimationEnd(Animation animation) {
            }
            public void onAnimationRepeat(Animation animation) {
            }
        });
        logo.startAnimation(grow);
        login.startAnimation(grow);
    }

}