
package com.sleepyduck.fingerboardtrainer;

import com.android.vending.billing.IInAppBillingService;
import com.purplebrain.adbuddiz.sdk.AdBuddiz;
import com.sleepyduck.fingerboardtrainer.MainLayout.LayoutState;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {
    private static final long AD_PAUSE_TIME = 300000;
    private static final int DONATE_REQUEST_CODE = 65486332;
    public static final int REQUEST_TTS = 544357324;

    private Notification meNotification;
    private TextView mTextView;
    private Button mStartButton;
    private int mHangTime;
    private int mRestTime;
    private int mRepetitions;
    private int mPauseTime;
    private int mTotalRepetitions;
    private Button mHangTimeButton;
    private Button mPauseTimeButton;
    private Button mRepetitionsButton;
    private Button mRestTimeButton;
    private Button mTotalRepetitionsButton;
    private Handler mHandler;
    private boolean mRunning;
    private boolean mDonateAsAction;
    private Intent mServiceIntent;
    private MainLayout mMainLayout;
    private long mAdTimer = -1;

    private TimerBinder mBinder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Log.d("onServiceDisconnected");
            mBinder = null;
            mStartButton.setEnabled(false);
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("onServiceConnected");
            mBinder = (TimerBinder)service;
            mBinder.getService().setMainActivity(MainActivity.this);
            mStartButton.setEnabled(true);
        }
    };

    private BillingManager mBillingManager;
    private IInAppBillingService mBillingService;

    private ServiceConnection mBillingServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBillingService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBillingService = IInAppBillingService.Stub.asInterface(service);
            mBillingManager = new BillingManager(MainActivity.this, mBillingService);
            mBillingManager.handleBillingConnectionOperations();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mServiceIntent = new Intent(this, TimerService.class);
        startService(mServiceIntent);

        Intent billingServiceIntent = new Intent(
                "com.android.vending.billing.InAppBillingService.BIND");
        billingServiceIntent.setPackage("com.android.vending");
        bindService(billingServiceIntent, mBillingServiceConn, Context.BIND_AUTO_CREATE);

        mMainLayout = (MainLayout)findViewById(R.id.main_layout);
        mTextView = (TextView)findViewById(R.id.textView);
        mStartButton = (Button)findViewById(R.id.start_button);
        mHangTimeButton = (Button)findViewById(R.id.hang_time);
        mPauseTimeButton = (Button)findViewById(R.id.pause_time);
        mRepetitionsButton = (Button)findViewById(R.id.repetitions);
        mRestTimeButton = (Button)findViewById(R.id.rest_time);
        mTotalRepetitionsButton = (Button)findViewById(R.id.total_repetitions);
        load();

        mHandler = new Handler();

        AdBuddiz.setPublisherKey("5d509af5-ad01-4751-8826-89bb007ea51e");
        AdBuddiz.cacheAds(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == DONATE_REQUEST_CODE) {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                try {
                    if (responseCode == 0) {
                        JSONObject jo = new JSONObject(purchaseData);
                        Log.d("JSON RESULT: " + jo.toString());
                        mBillingManager.setHasDonated(true);
                        invalidateOptionsMenu();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(this, "Failed to parse make donation.", Toast.LENGTH_LONG).show();
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
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("onPause");
        unbindService(mServiceConnection);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d("onDestroy");
        stopService(mServiceIntent);

        if (mBillingService != null) {
            unbindService(mBillingServiceConn);
        }

        super.onDestroy();
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
            case R.id.action_history:
                startActivity(new Intent(this, HistoryActivity.class));
                return true;
            case R.id.action_notification:
                showNotificationPicker();
                return true;
            case R.id.action_donate:
                donateMoney();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void activateInAppPurchases() {
        invalidateOptionsMenu();
    }

    private void showNotificationPicker() {
        final RadioGroup picker = (RadioGroup)View
                .inflate(this, R.layout.notification_picker, null);
        RadioButton rb = null;
        switch (meNotification) {
            case NONE:
                rb = (RadioButton)picker.findViewById(R.id.mode_silent);
                break;
            case SOUND:
                rb = (RadioButton)picker.findViewById(R.id.mode_sound);
                break;
            case VIBRATE:
                rb = (RadioButton)picker.findViewById(R.id.mode_vibrate);
                break;
        }
        rb.setChecked(true);
        picker.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.mode_silent:
                        meNotification = Notification.NONE;
                        break;
                    case R.id.mode_sound:
                        meNotification = Notification.SOUND;
                        TextToSpeechManager.initializeTTSCheck(MainActivity.this);
                        break;
                    case R.id.mode_vibrate:
                        meNotification = Notification.VIBRATE;
                        break;
                }
                save();
            }
        });
        new AlertDialog.Builder(this).setView(picker).setTitle(R.string.action_notification)
                .setPositiveButton(android.R.string.ok, null).show();
    }

    private void donateMoney() {
        try {
            Bundle buyIntentBundle = mBillingService.getBuyIntent(3, getPackageName(),
                    mBillingManager.getBillingItems().get(0).productId, "inapp", "");
            PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
            startIntentSenderForResult(pendingIntent.getIntentSender(), DONATE_REQUEST_CODE,
                    new Intent(), 0, 0, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (SendIntentException e) {
            e.printStackTrace();
        }
    }

    public void onStartClicked(View view) {
        if (!mRunning) {
            start();
        } else {
            stop();
        }
    }

    private void start() {
        Log.d("start, mBinder = " + mBinder);
        if (mBinder != null) {
            mStartButton.setText(R.string.stop);
            mRunning = true;
            mBinder.getService().startTimer(mHangTime, mPauseTime, mRepetitions, mRestTime,
                    mTotalRepetitions, meNotification);
            mMainLayout.setLayoutState(LayoutState.TEXT_FOCUS);
        }
    }

    private void stop() {
        if (mBinder != null) {
            mStartButton.setText(R.string.start);
            mRunning = false;
            mBinder.getService().stopTimer();
            setText("");
            mMainLayout.setLayoutState(LayoutState.NORMAL);
        }
        showAd(2000);
    }

    public void setText(final String text) {
        mHandler.post(new Runnable() {
            public void run() {
                mTextView.setText(text);
            }
        });
    }

    private void showAd(int timeMillis) {
        if (!mBillingManager.hasDonated()) {
            if (mAdTimer < System.currentTimeMillis()) {
                showDonate();
                mAdTimer = System.currentTimeMillis() + AD_PAUSE_TIME;
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (!isFinishing()) {
                            AdBuddiz.showAd(MainActivity.this);
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
        mHandler.post(new Runnable() {
            public void run() {
                stop();
                SharedPreferences prefs = getSharedPreferences("shared_prefs", MODE_PRIVATE);
                Set<String> history = new HashSet<String>();
                history = prefs.getStringSet("history", history);
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
            }
        });
        showAd(2000);
    }

    private void save() {
        Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
        editor.putString("notification", meNotification.toString());
        editor.putInt("hang_time", mHangTime);
        editor.putInt("pause_time", mPauseTime);
        editor.putInt("repetitions", mRepetitions);
        editor.putInt("rest_time", mRestTime);
        editor.putInt("total_repetitions", mTotalRepetitions);
        if (!editor.commit()) {
            // Toast.makeText(this, "Failed to save shared preferences",
            // Toast.LENGTH_LONG).show();
        }
    }

    private void load() {
        Log.d("pre load, mRestTime = " + mRestTime);
        SharedPreferences prefs = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        meNotification = Enum.valueOf(Notification.class,
                prefs.getString("notification", Notification.SOUND.toString()));
        mHangTime = prefs.getInt("hang_time",
                Integer.valueOf(getString(R.string.initial_hang_time)));
        mPauseTime = prefs.getInt("pause_time",
                Integer.valueOf(getString(R.string.initial_pause_time)));
        mRepetitions = prefs.getInt("repetitions",
                Integer.valueOf(getString(R.string.initial_repetitions)));
        mRestTime = prefs.getInt("rest_time",
                Integer.valueOf(getString(R.string.initial_rest_time)));
        mTotalRepetitions = prefs.getInt("total_repetitions",
                Integer.valueOf(getString(R.string.initial_total_repetitions)));

        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        mHangTimeButton.setText(sdf.format(mHangTime * 1000));
        mPauseTimeButton.setText(sdf.format(mPauseTime * 1000));
        mRepetitionsButton.setText(String.valueOf(mRepetitions));
        mRestTimeButton.setText(sdf.format(mRestTime * 1000));
        mTotalRepetitionsButton.setText(String.valueOf(mTotalRepetitions));

        Log.d("post load, mRestTime = " + mRestTime);
    }

    public void onChangeTimeClicked(final View view) {
        final View picker = View.inflate(this, R.layout.minutes_seconds_picker, null);
        final NumberPicker minutes = (NumberPicker)picker.findViewById(R.id.time_picker_minutes);
        final NumberPicker seconds = (NumberPicker)picker.findViewById(R.id.time_picker_seconds);
        minutes.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        seconds.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        minutes.setMaxValue(60);
        seconds.setMaxValue(59);
        int val = getValueFromViewId(view.getId());
        minutes.setValue(val / 60);
        seconds.setValue(val % 60);
        new AlertDialog.Builder(this).setView(picker).setTitle(getTitleFromViewId(view.getId()))
                .setNegativeButton(android.R.string.cancel, null).setCancelable(true)
                .setPositiveButton(android.R.string.yes, new OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        updateTime((Button)view, minutes.getValue() * 60 + seconds.getValue());
                    }
                }).show();
    }

    public void onChangeValueClicked(final View view) {
        final NumberPicker picker = new NumberPicker(this);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker.setMaxValue(60);
        picker.setValue(getValueFromViewId(view.getId()));
        new AlertDialog.Builder(this).setView(picker).setTitle(getTitleFromViewId(view.getId()))
                .setNegativeButton(android.R.string.cancel, null).setCancelable(true)
                .setPositiveButton(android.R.string.yes, new OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        updateValue((Button)view, picker.getValue());
                    }
                }).show();
    }

    private int getValueFromViewId(int id) {
        switch (id) {
            case R.id.hang_time:
                return mHangTime;
            case R.id.pause_time:
                return mPauseTime;
            case R.id.rest_time:
                return mRestTime;
            case R.id.repetitions:
                return mRepetitions;
            case R.id.total_repetitions:
                return mTotalRepetitions;
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
        SimpleDateFormat sdf = new SimpleDateFormat("mm:ss", Locale.getDefault());
        ((Button)view).setText(sdf.format(seconds * 1000));
        switch (view.getId()) {
            case R.id.hang_time:
                mHangTime = seconds;
                break;
            case R.id.pause_time:
                mPauseTime = seconds;
                break;
            case R.id.rest_time:
                mRestTime = seconds;
                break;
            default:
                break;
        }
        save();
    }

    private void updateValue(Button view, int value) {
        ((Button)view).setText("" + value);
        switch (view.getId()) {
            case R.id.repetitions:
                mRepetitions = value;
                break;
            case R.id.total_repetitions:
                mTotalRepetitions = value;
                break;
            default:
                break;
        }
        save();
    }
}
