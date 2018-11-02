
package com.sleepyduck.fingerboardtrainer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.sleepyduck.fingerboardtrainer.MainLayout.LayoutState;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import se.sleepyduckstudio.billing.BillingManager;
import se.sleepyduckstudio.logger.Log;

public class MainActivity extends Activity {
    private static final long AD_PAUSE_TIME = 300000;
    private static final int DONATE_REQUEST_CODE = 65486332;
    public static final int REQUEST_TTS = 544357324;

    private Notification meNotification;
    private TextView mTextView;
    private Button mStartButton;
    private Button mPauseButton;
    private Button mHangTimeButton;
    private Button mPauseTimeButton;
    private Button mRepetitionsButton;
    private Button mRestTimeButton;
    private Button mTotalRepetitionsButton;
    private AdView mAdView;
    private Handler mHandler;
    private boolean mRunning;
    private boolean mDonateAsAction;
    private boolean mHasRatedApp;
    private Intent mServiceIntent;
    private MainLayout mMainLayout;
    private long mAdTimer = -1;
    private List<WorkoutData> mWorkoutDataList;
    private List<String> mNavMenuListItems;
    private ListView mNavMenu;
    private AlertDialog mDonateDialog;

    private InterstitialAd mInterstitialAd;

    private BillingManager mBillingManager;

    private TimerBinder mBinder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            se.sleepyduckstudio.logger.Log.d("onServiceDisconnected");
            mBinder = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            se.sleepyduckstudio.logger.Log.d("onServiceConnected");
            mBinder = (TimerBinder) service;
            mBinder.getService().setMainActivity(MainActivity.this);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBillingManager = BillingManager.Factory.getInstance(this, this::activateInAppPurchases);

        mServiceIntent = new Intent(this, TimerService.class);
        startService(mServiceIntent);

        mMainLayout = findViewById(R.id.main_layout);
        mTextView = findViewById(R.id.textView);
        mStartButton = findViewById(R.id.start_button);
        mPauseButton = findViewById(R.id.pause_button);
        mHangTimeButton = findViewById(R.id.hang_time);
        mPauseTimeButton = findViewById(R.id.pause_time);
        mRepetitionsButton = findViewById(R.id.repetitions);
        mRestTimeButton = findViewById(R.id.rest_time);
        mTotalRepetitionsButton = findViewById(R.id.total_repetitions);
        mAdView = findViewById(R.id.adView);

        setupActionBar();
        setupNavMenu();
        load();

        mHandler = new Handler();

        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();
    }

    private void setupActionBar() {
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupNavMenu() {
        mWorkoutDataList = new ArrayList<>();
        mNavMenuListItems = new ArrayList<>();
        ArrayAdapter<String> mNavMenuAdapter = new ArrayAdapter<>(this, R.layout.nav_menu_item, mNavMenuListItems);
        mNavMenu = findViewById(R.id.nav_menu);
        mNavMenu.setAdapter(mNavMenuAdapter);
        mNavMenu.setOnItemClickListener((parent, view, position, id) -> {
            selectWorkout(position);
            mMainLayout.setNavMenu(MainLayout.NavMenuState.CLOSED);
            save();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == DONATE_REQUEST_CODE) {
                mBillingManager.consumePurchase(this, data);
                mAdView.setVisibility(View.GONE);
                askForAppRating();
            } else if (requestCode == REQUEST_TTS) {
                TextToSpeechManager.setHasEngine(true);
            }
        } else {
            Log.d("MAinActivity.onActivityResult(): resultConde = " + resultCode);
        }
    }

    @Override
    protected void onResume() {
        Log.d("onResume");
        bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mAdView.resume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("onPause");
        unbindService(mServiceConnection);
        mAdView.pause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("onDestroy");
        stopService(mServiceIntent);
        mBillingManager.destroy(this);

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mRunning) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        } else if (mMainLayout.getNavMenuState() == MainLayout.NavMenuState.OPEN) {
            mMainLayout.setNavMenu(MainLayout.NavMenuState.CLOSED);
        } else {
            save();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mBillingManager != null && mBillingManager.getBillingItems().size() > 0) {
            if (mBillingManager.hasDonated()) {
                getMenuInflater().inflate(R.menu.main_has_donated, menu);
            } else if (mDonateAsAction) {
                getMenuInflater().inflate(R.menu.main_donate_as_action, menu);
            } else {
                getMenuInflater().inflate(R.menu.main_donate, menu);
            }
        } else {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_name:
                changeCurrentWorkoutName();
                return true;
            case R.id.delete_workout:
                deleteWorkout();
                break;
            case R.id.action_notification:
                showNotificationPicker();
                return true;
            case R.id.action_donate:
                donateMoney();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggleNavMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onAddWorkoutClicked(View view) {
        createNewWorkout();
    }

    private void createNewWorkout() {
        final WorkoutData data = new WorkoutData();
        data.mHangTime = Integer.valueOf(getString(R.string.initial_hang_time));
        data.mPauseTime = Integer.valueOf(getString(R.string.initial_pause_time));
        data.mRepetitions = Integer.valueOf(getString(R.string.initial_repetitions));
        data.mRestTime = Integer.valueOf(getString(R.string.initial_rest_time));
        data.mTotalRepetitions = Integer.valueOf(getString(R.string.initial_total_repetitions));
        data.mName = getString(R.string.initial_training_name) + " " + (mNavMenuListItems.size() + 1);

        final EditText et = new EditText(this);
        et.setText(data.mName);
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_change_name)
                .setView(et)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    data.mName = et.getText().toString();
                    mWorkoutDataList.add(data);
                    mNavMenuListItems.add(data.mName);
                    getActionBar().setTitle(data.mName);
                    selectWorkout(mNavMenuListItems.size() - 1);
                })
                .show();
    }

    private void deleteWorkout() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete)
                .setMessage(R.string.delete_workout_message)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> deleteWorkout(mNavMenu.getCheckedItemPosition()))
                .show();
    }

    private void deleteWorkout(int id) {
        if (mNavMenuListItems.size() <= 1) {
            resetWorkout(0);
            selectWorkout(0);
            changeCurrentWorkoutName();
        } else {
            mWorkoutDataList.remove(id);
            mNavMenuListItems.remove(id);
            selectWorkout(0);
        }
        save();
    }

    private void resetWorkout(int id) {
        WorkoutData data = mWorkoutDataList.get(id);
        data.mName = getString(R.string.initial_training_name) + " " + (id + 1);
        data.mHangTime = Integer.parseInt(getString(R.string.initial_hang_time));
        data.mPauseTime = Integer.parseInt(getString(R.string.initial_pause_time));
        data.mRepetitions = Integer.parseInt(getString(R.string.initial_repetitions));
        data.mRestTime = Integer.parseInt(getString(R.string.initial_rest_time));
        data.mTotalRepetitions = Integer.parseInt(getString(R.string.initial_total_repetitions));
        save();
    }

    private void selectWorkout(int id) {
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        WorkoutData data = mWorkoutDataList.get(id);
        mHangTimeButton.setText(sdf.format(data.mHangTime * 1000));
        mPauseTimeButton.setText(sdf.format(data.mPauseTime * 1000));
        mRepetitionsButton.setText(String.valueOf(data.mRepetitions));
        mRestTimeButton.setText(sdf.format(data.mRestTime * 1000));
        mTotalRepetitionsButton.setText(String.valueOf(data.mTotalRepetitions));
        getActionBar().setTitle(data.mName);
        mNavMenu.setItemChecked(id, true);
        save();
    }

    private void changeCurrentWorkoutName() {
        final EditText et = new EditText(this);
        final int currentWorkout = mNavMenu.getCheckedItemPosition();
        et.setText(mWorkoutDataList.get(currentWorkout).mName);
        et.setHint(mWorkoutDataList.get(currentWorkout).mName);
        new AlertDialog.Builder(this)
                .setView(et)
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel())
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    WorkoutData data = mWorkoutDataList.get(currentWorkout);
                    data.mName = et.getText().toString();
                    mNavMenuListItems.set(currentWorkout, data.mName);
                    getActionBar().setTitle(data.mName);
                    save();
                })
                .show();
    }

    public void activateInAppPurchases() {
        invalidateOptionsMenu();
        if (!mBillingManager.hasDonated()) {
            activateBannerAds();
        }
    }

    private void activateBannerAds() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("67B5ACD31F0A329A191C59D16C81E9D7")
                .build();
        mAdView.loadAd(adRequest);
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("67B5ACD31F0A329A191C59D16C81E9D7")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void showNotificationPicker() {
        final RadioGroup picker = (RadioGroup) View
                .inflate(this, R.layout.notification_picker, null);
        RadioButton rb = null;
        switch (meNotification) {
            case NONE:
                rb = picker.findViewById(R.id.mode_silent);
                break;
            case VOICE:
                rb = picker.findViewById(R.id.mode_voice);
                break;
            case VIBRATE:
                rb = picker.findViewById(R.id.mode_vibrate);
                break;
        }
        rb.setChecked(true);
        picker.setOnCheckedChangeListener((group, checkedId) -> {

            switch (checkedId) {
                case R.id.mode_silent:
                    meNotification = Notification.NONE;
                    break;
                case R.id.mode_voice:
                    meNotification = Notification.VOICE;
                    TextToSpeechManager.initializeTTSCheck(MainActivity.this);
                    break;
                case R.id.mode_vibrate:
                    meNotification = Notification.VIBRATE;
                    break;
            }
            save();
        });
        new AlertDialog.Builder(this).setView(picker).setTitle(R.string.action_notification)
                .setPositiveButton(android.R.string.ok, null).show();
    }

    private void donateMoney() {
        DonateLayout view = (DonateLayout) getLayoutInflater().inflate(R.layout.donate, null);
        view.setHasDonated(mBillingManager.hasDonated());
        view.setupDonationData(mBillingManager);
        mDonateDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(true)
                .show();
    }

    public void onDonateClicked(View view) {
        if (mDonateDialog != null) {
            mDonateDialog.dismiss();
            mDonateDialog = null;
        }

        String productId;
        switch (view.getId()) {
            case R.id.donate_small:
                productId = "donate_1";
                break;
            case R.id.donate_medium:
                productId = "donate_2";
                break;
            case R.id.donate_large:
                productId = "donate_3";
                break;
            case R.id.donate_repeat:
                productId = "donate_repeat";
                break;
            default:
                Toast.makeText(this, "Failed to process in app billing items", Toast.LENGTH_SHORT).show();
                return;
        }

        mBillingManager.purchase(this, productId, DONATE_REQUEST_CODE);
    }

    public void onStartClicked(View view) {
        if (!mRunning) {
            start();
        } else {
            stop();
        }
    }

    private void start() {
        if (mBinder != null) {
            WorkoutData data = mWorkoutDataList.get(mNavMenu.getCheckedItemPosition());
            mStartButton.setText(R.string.stop);
            mRunning = true;
            mBinder.getService().startTimer(data.mHangTime, data.mPauseTime, data.mRepetitions,
                    data.mRestTime, data.mTotalRepetitions, meNotification);
            mMainLayout.setLayoutState(LayoutState.RUNNING);
        }
    }

    public void onPauseClicked(View view) {
        if (mRunning) {
            if (mBinder != null) {
                if (!mBinder.getService().isPaused()) {
                    mBinder.getService().pauseTimer();
                    mPauseButton.setText(R.string.resume);
                } else {
                    mBinder.getService().resumeTimer();
                    mPauseButton.setText(R.string.pause);
                }
            }
        }
    }

    private void stop() {
        if (mBinder != null) {
            mStartButton.setText(R.string.start);
            mPauseButton.setText(R.string.pause);
            mRunning = false;
            mBinder.getService().stopTimer();
            setText("");
            mMainLayout.setLayoutState(LayoutState.NORMAL);
        }
        showAd(2000);
    }

    public void onCloseNavMenuClicked(View view) {
        hideNavMenu();
    }

    private void toggleNavMenu() {
        mMainLayout.setNavMenu(
                mMainLayout.getNavMenuState() == MainLayout.NavMenuState.OPEN
                        ? MainLayout.NavMenuState.CLOSED
                        : MainLayout.NavMenuState.OPEN);
    }

    private void hideNavMenu() {
        mMainLayout.setNavMenu(MainLayout.NavMenuState.CLOSED);
    }

    public void setText(final String text) {
        mHandler.post(() -> mTextView.setText(text));
    }

    private void showAd(int timeMillis) {
        if (mBillingManager != null && !mBillingManager.hasDonated()) {
            if (mAdTimer < System.currentTimeMillis()) {
                showDonate();
                mAdTimer = System.currentTimeMillis() + AD_PAUSE_TIME;
                mHandler.postDelayed(() -> {
                    if (!isFinishing()) {
                        if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            mAdTimer = -1;
                        }
                    }
                }, timeMillis);
            }
        }
    }

    private void showDonate() {
        mDonateAsAction = true;
        invalidateOptionsMenu();
    }

    public void onTrainingCompleted(final int hangTime, final int pauseTime, final int repetitions,
                                    final int restTime, final int totalRepetitions) {
        mHandler.post(() -> {
            stop();
            SharedPreferences prefs = getSharedPreferences("shared_prefs", MODE_PRIVATE);
            Set<String> history = new HashSet<>();
            history = new HashSet<>(prefs.getStringSet("history", history));
            Editor editor = prefs.edit();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale
                    .getDefault());
            String log = sdf.format(cal.getTime());
            log += " - Hang/Pause " + hangTime + "s/" + pauseTime + "s, Repetitions "
                    + repetitions + ", Rest " + (restTime / 60) + "m " + (restTime % 60)
                    + "s, Total repetitions " + totalRepetitions;
            history.add(log);
            editor.putStringSet("history", history);
            if (!editor.commit()) {
                // Toast.makeText(MainActivity.this,
                // "Failed to save shared preferences",
                // Toast.LENGTH_LONG).show();
            }
            setText("Good job!");
        });
        showAd(2000);
    }

    private void save() {
        Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();

        //Global prefs
        editor.putString("notification", meNotification.toString());
        editor.putBoolean("has_rated_app", mHasRatedApp);
        editor.putInt("save_version", 1);
        editor.putInt("current_workout", mNavMenu.getCheckedItemPosition());

        //Workout
        editor.putInt("workout_count", mWorkoutDataList.size());
        WorkoutData data;
        for (int i = 0; i < mWorkoutDataList.size(); ++i) {
            data = mWorkoutDataList.get(i);
            editor.putInt("hang_time_" + i, data.mHangTime);
            editor.putInt("pause_time_" + i, data.mPauseTime);
            editor.putInt("repetitions_" + i, data.mRepetitions);
            editor.putInt("rest_time_" + i, data.mRestTime);
            editor.putInt("total_repetitions_" + i, data.mTotalRepetitions);
            editor.putString("name_" + i, data.mName);
        }

        editor.commit();
    }

    private void load() {
        SharedPreferences prefs = getSharedPreferences("shared_prefs", MODE_PRIVATE);

        if (prefs.getInt("save_version", 0) < 1) {
            loadOld(prefs);
            return;
        }

        try {
            meNotification = Enum.valueOf(Notification.class,
                    prefs.getString("notification", Notification.VOICE.toString()));
        } catch (Exception e) {
            meNotification = Notification.VOICE;
        }
        mHasRatedApp = prefs.getBoolean("has_rated_app", false);
        int workoutCount = prefs.getInt("workout_count", 1);
        int currentWorkout = prefs.getInt("current_workout", 0);

        mWorkoutDataList.clear();
        mNavMenuListItems.clear();
        WorkoutData data;
        for (int i = 0; i < workoutCount; ++i) {
            data = new WorkoutData();
            data.mHangTime = prefs.getInt("hang_time_" + i,
                    Integer.valueOf(getString(R.string.initial_hang_time)));
            data.mPauseTime = prefs.getInt("pause_time_" + i,
                    Integer.valueOf(getString(R.string.initial_pause_time)));
            data.mRepetitions = prefs.getInt("repetitions_" + i,
                    Integer.valueOf(getString(R.string.initial_repetitions)));
            data.mRestTime = prefs.getInt("rest_time_" + i,
                    Integer.valueOf(getString(R.string.initial_rest_time)));
            data.mTotalRepetitions = prefs.getInt("total_repetitions_" + i,
                    Integer.valueOf(getString(R.string.initial_total_repetitions)));
            data.mName = prefs.getString("name_" + i, getString(R.string.initial_training_name) + " " + (i + 1));
            mWorkoutDataList.add(data);
            mNavMenuListItems.add(data.mName);
        }

        selectWorkout(currentWorkout);
    }

    private void loadOld(SharedPreferences prefs) {
        try {
            meNotification = Enum.valueOf(Notification.class,
                    prefs.getString("notification", Notification.VOICE.toString()));
        } catch (Exception e) {
            meNotification = Notification.VOICE;
        }
        mHasRatedApp = prefs.getBoolean("has_rated_app", false);

        int hangTime = prefs.getInt("hang_time",
                Integer.valueOf(getString(R.string.initial_hang_time)));
        int pauseTime = prefs.getInt("pause_time",
                Integer.valueOf(getString(R.string.initial_pause_time)));
        int repetitions = prefs.getInt("repetitions",
                Integer.valueOf(getString(R.string.initial_repetitions)));
        int restTime = prefs.getInt("rest_time",
                Integer.valueOf(getString(R.string.initial_rest_time)));
        int totalRepetitions = prefs.getInt("total_repetitions",
                Integer.valueOf(getString(R.string.initial_total_repetitions)));

        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        mHangTimeButton.setText(sdf.format(hangTime * 1000));
        mPauseTimeButton.setText(sdf.format(pauseTime * 1000));
        mRepetitionsButton.setText(String.valueOf(repetitions));
        mRestTimeButton.setText(sdf.format(restTime * 1000));
        mTotalRepetitionsButton.setText(String.valueOf(totalRepetitions));

        mNavMenu.setItemChecked(0, true);
        WorkoutData data = new WorkoutData();
        data.mHangTime = hangTime;
        data.mPauseTime = pauseTime;
        data.mRepetitions = repetitions;
        data.mRestTime = restTime;
        data.mTotalRepetitions = totalRepetitions;
        data.mName = getString(R.string.initial_training_name) + " 1";
        mWorkoutDataList.add(data);
        mNavMenuListItems.add(data.mName);
        getActionBar().setTitle(data.mName);
    }

    public void onChangeTimeClicked(final View view) {
        final View picker = View.inflate(this, R.layout.minutes_seconds_picker, null);
        final NumberPicker minutes = picker.findViewById(R.id.time_picker_minutes);
        final NumberPicker seconds = picker.findViewById(R.id.time_picker_seconds);
        minutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        seconds.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutes.setMaxValue(60);
        seconds.setMaxValue(59);
        int val = getValueFromViewId(view.getId());
        minutes.setValue(val / 60);
        seconds.setValue(val % 60);
        new AlertDialog.Builder(this).setView(picker).setTitle(getTitleFromViewId(view.getId()))
                .setNegativeButton(android.R.string.cancel, null).setCancelable(true)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> updateTime((Button) view, minutes.getValue() * 60 + seconds.getValue())).show();
    }

    public void onChangeValueClicked(final View view) {
        final NumberPicker picker = new NumberPicker(this);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setMaxValue(60);
        picker.setValue(getValueFromViewId(view.getId()));
        new AlertDialog.Builder(this).setView(picker).setTitle(getTitleFromViewId(view.getId()))
                .setNegativeButton(android.R.string.cancel, null).setCancelable(true)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> updateValue((Button) view, picker.getValue())).show();
    }

    private int getValueFromViewId(int id) {
        WorkoutData data = mWorkoutDataList.get(mNavMenu.getCheckedItemPosition());
        switch (id) {
            case R.id.hang_time:
                return data.mHangTime;
            case R.id.pause_time:
                return data.mPauseTime;
            case R.id.rest_time:
                return data.mRestTime;
            case R.id.repetitions:
                return data.mRepetitions;
            case R.id.total_repetitions:
                return data.mTotalRepetitions;
            default:
                break;
        }
        return 0;
    }

    private int getTitleFromViewId(int id) {
        switch (id) {
            case R.id.hang_time:
                return R.string.hang_time;
            case R.id.pause_time:
                return R.string.pause_time;
            case R.id.rest_time:
                return R.string.rest_time;
            case R.id.repetitions:
                return R.string.repetitions;
            case R.id.total_repetitions:
                return R.string.total_repetitions;
            default:
                break;
        }
        return 0;
    }

    private void updateTime(Button view, int seconds) {
        WorkoutData data = mWorkoutDataList.get(mNavMenu.getCheckedItemPosition());
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        view.setText(sdf.format(seconds * 1000));
        switch (view.getId()) {
            case R.id.hang_time:
                data.mHangTime = seconds;
                break;
            case R.id.pause_time:
                data.mPauseTime = seconds;
                break;
            case R.id.rest_time:
                data.mRestTime = seconds;
                break;
            default:
                break;
        }
        save();
    }

    private void updateValue(Button view, int value) {
        WorkoutData data = mWorkoutDataList.get(mNavMenu.getCheckedItemPosition());
        view.setText("" + value);
        switch (view.getId()) {
            case R.id.repetitions:
                data.mRepetitions = value;
                break;
            case R.id.total_repetitions:
                data.mTotalRepetitions = value;
                break;
            default:
                break;
        }
        save();
    }

    private void askForAppRating() {
        if (!mHasRatedApp) {
            final Uri uri = Uri.parse("market://details?id=" + getPackageName());
            final Intent rateAppIntent = new Intent(Intent.ACTION_VIEW, uri);
            if (getPackageManager().queryIntentActivities(rateAppIntent, 0).size() > 0) {
                new AlertDialog.Builder(this)
                        .setTitle(R.string.rate_app)
                        .setMessage(R.string.rate_app_text)
                        .setPositiveButton(android.R.string.ok,
                                (dialog, which) -> {
                                    mHasRatedApp = true;
                                    startActivity(rateAppIntent);
                                }).setNegativeButton(R.string.later, null).setCancelable(true)
                        .show();
            }
        }
    }
}
