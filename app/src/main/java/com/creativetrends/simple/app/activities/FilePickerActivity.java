package com.creativetrends.simple.app.activities;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.creativetrends.simple.app.R;
import com.creativetrends.simple.app.lock.CompositeFilter;
import com.creativetrends.simple.app.lock.PatternFilter;
import com.creativetrends.simple.app.ui.DirectoryFragment;
import com.creativetrends.simple.app.utils.ThemeUtils;
import com.creativetrends.simple.app.utils.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**Created by Creative Trends Apps on 24.10.15.*/
public class FilePickerActivity extends AppCompatActivity implements DirectoryFragment.FileClickListener {
    public static final String ARG_START_PATH = "arg_start_path";
    public static final String ARG_CURRENT_PATH = "arg_current_path";

    public static final String ARG_FILTER = "arg_filter";

    public static final String STATE_START_PATH = "state_start_path";
    private static final String STATE_CURRENT_PATH = "state_current_path";

    public static final String RESULT_FILE_PATH = "result_file_path";
    private static final int HANDLE_CLICK_DELAY = 150;

    public  static final String IS_DIRECTORY = "is_directory";

    private Toolbar mToolbar;
    private String mStartPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String mCurrentPath = mStartPath;
    Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);

    private CompositeFilter mFilter;
    EditText et;
    SharedPreferences preferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setSettingsTheme(this, this);
        setContentView(R.layout.activity_file_picker);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("nav", false) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(ThemeUtils.getColorPrimaryDark(this));
        }
        et = new EditText(FilePickerActivity.this);
        initArguments();
        initViews();
        initToolbar();


        if (savedInstanceState != null) {
            mStartPath = savedInstanceState.getString(STATE_START_PATH);
            mCurrentPath = savedInstanceState.getString(STATE_CURRENT_PATH);
        } else {
            initFragment();
        }
    }

    @SuppressWarnings("unchecked")
    private void initArguments() {
        if (getIntent().hasExtra(ARG_FILTER)) {
            Serializable filter = getIntent().getSerializableExtra(ARG_FILTER);

            if (filter instanceof Pattern) {
                ArrayList<FileFilter> filters = new ArrayList<>();
                filters.add(new PatternFilter((Pattern) filter, false));
                mFilter = new CompositeFilter(filters);
            } else {
                mFilter = (CompositeFilter) filter;
            }
        }

        if (getIntent().hasExtra(ARG_START_PATH)) {
            mStartPath = getIntent().getStringExtra(ARG_START_PATH);
            mCurrentPath = mStartPath;
        }

        if (getIntent().hasExtra(ARG_CURRENT_PATH)) {
            String currentPath = getIntent().getStringExtra(ARG_CURRENT_PATH);

            if (currentPath.startsWith(mStartPath)) {
                mCurrentPath = currentPath;
            }
        }
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setSoundEffectsEnabled(false);
        }

        try {
            Field f = mToolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);

            TextView textView = (TextView) f.get(mToolbar);
            textView.setEllipsize(TextUtils.TruncateAt.START);
        } catch (Exception ignored) {}
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
    }

    private void initFragment() {
        getFragmentManager().beginTransaction()
                .add(R.id.container, DirectoryFragment.getInstance(
                        mStartPath, mFilter))
                .commit();
    }

    private void addFragmentToBackStack(String path) {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, DirectoryFragment.getInstance(
                        path, mFilter))
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_picker, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.dir_create:
                createDialog();
                return true;


            case R.id.dir_select:
                setResultAndFinish(mCurrentPath, true);
                Toast.makeText(getApplicationContext(), "Changed to:" + " "+ mCurrentPath, Toast.LENGTH_SHORT).show();
                return true;


            default:
                return super.onOptionsItemSelected(item);


        }
    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
            mCurrentPath = FileUtils.cutLastSegmentOfPath(mCurrentPath);
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_CURRENT_PATH, mCurrentPath);
        outState.putString(STATE_START_PATH, mStartPath);
    }

    @Override
    public void onFileClicked(final File clickedFile) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleFileClicked(clickedFile);
            }
        }, HANDLE_CLICK_DELAY);
    }

    private void handleFileClicked(final File clickedFile) {
        if (clickedFile.isDirectory()) {
            addFragmentToBackStack(clickedFile.getPath());
            mCurrentPath = clickedFile.getPath();
        } else {
            setResultAndFinish(clickedFile.getPath(), false);
        }
    }

    private void setResultAndFinish(String filePath, boolean isDirectory) {
        Intent data = new Intent();
        data.putExtra(RESULT_FILE_PATH, filePath);
        data.putExtra(IS_DIRECTORY, isDirectory);
        setResult(RESULT_OK, data);
        finish();
    }


    private void createDialog() {
        try {
            AlertDialog.Builder createFile = new AlertDialog.Builder(FilePickerActivity.this);
            createFile.setTitle(R.string.create_directory);
            createFile.setView(et, 20,10,20,10);
            createFile.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    try {
                        String myPath;
                        if(isSDPresent && mCurrentPath.contains("emulated")){
                            myPath = mCurrentPath + File.separator + "0" + File.separator +et.getText().toString();
                        }else {
                            myPath = mCurrentPath + File.separator + et.getText().toString();
                        }
                        final File myDir = new File(myPath);
                        //noinspection ResultOfMethodCallIgnored
                        myDir.mkdirs();
                        setResultAndFinish(myPath, true);
                        Toast.makeText(getApplicationContext(), "Created new directory:" + " " + myPath + File.separator, Toast.LENGTH_LONG).show();
                         } catch (Exception ignored) {

                     }

                   }
            });
            createFile.setNegativeButton(R.string.cancel, null);

            createFile.show();
        } catch (Exception ignored) {

        }
    }

}