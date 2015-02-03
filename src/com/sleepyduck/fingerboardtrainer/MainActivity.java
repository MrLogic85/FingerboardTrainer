
package com.sleepyduck.fingerboardtrainer;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {
    public static final String TAG = "fingerboardtrainer";
    private TextView mTextView;

    private Button mStartButton;

    private int mHangTime;

    private int mRestTime;

    private int mRepetitions;

    private int mPauseTime;

    private int mTotalRepetitions;

    private EditText mHangTimeText;

    private EditText mPauseTimeText;

    private EditText mRepetitionsText;

    private EditText mRestTimeText;

    private EditText mTotalRepetitionsText;

    private Handler mHandler;

    private boolean mRunning;

    private Intent mServiceIntent;

    private TimerBinder mBinder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
            mBinder = null;
            mStartButton.setEnabled(false);
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mBinder = (TimerBinder)service;
            mBinder.getService().setMainActivity(MainActivity.this);
            mStartButton.setEnabled(true);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mServiceIntent = new Intent(this, TimerService.class);
        startService(mServiceIntent);

        mTextView = (TextView)findViewById(R.id.textView);
        mStartButton = (Button)findViewById(R.id.start_button);

        mHangTimeText = (EditText)findViewById(R.id.hang_time);
        mPauseTimeText = (EditText)findViewById(R.id.pause_time);
        mRepetitionsText = (EditText)findViewById(R.id.repetitions);
        mRestTimeText = (EditText)findViewById(R.id.rest_time);
        mTotalRepetitionsText = (EditText)findViewById(R.id.total_repetitions);
        if (savedInstanceState == null) {
            mHangTime = Integer.valueOf(mHangTimeText.getText().toString());
            mPauseTime = Integer.valueOf(mPauseTimeText.getText().toString());
            mRepetitions = Integer.valueOf(mRepetitionsText.getText().toString());
            mRestTime = Integer.valueOf(mRestTimeText.getText().toString());
            mTotalRepetitions = Integer.valueOf(mTotalRepetitionsText.getText().toString());
            load();
        }
        mHangTimeText.addTextChangedListener(new MyTextWatcher(R.id.hang_time));
        mPauseTimeText.addTextChangedListener(new MyTextWatcher(R.id.pause_time));
        mRepetitionsText.addTextChangedListener(new MyTextWatcher(R.id.repetitions));
        mRestTimeText.addTextChangedListener(new MyTextWatcher(R.id.rest_time));
        mTotalRepetitionsText.addTextChangedListener(new MyTextWatcher(R.id.total_repetitions));

        mHandler = new Handler();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        bindService(mServiceIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        unbindService(mServiceConnection);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        stopService(mServiceIntent);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.action_history) {
            startActivity(new Intent(this, HistoryActivity.class));
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    public void onStartClicked(View view) {
        if (!mRunning) {
            start();
        } else {
            stop();
        }
    }

    private void start() {
        Log.d(TAG, "start, mBinder = " + mBinder);
        if (mBinder != null) {
            mStartButton.setText(R.string.stop);
            mRunning = true;
            mBinder.getService().startTimer(mHangTime, mPauseTime, mRepetitions, mRestTime,
                    mTotalRepetitions);
        }
    }

    private void stop() {
        if (mBinder != null) {
            mStartButton.setText(R.string.start);
            mRunning = false;
            mBinder.getService().stopTimer();
            setText("");
        }
    }

    public void setText(final String text) {
        mHandler.post(new Runnable() {
            public void run() {
                mTextView.setText(text);
            }
        });
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
                        + repetitions + ", Rest " + restTime + "m, Total repetitions "
                        + totalRepetitions;
                history.add(log);
                editor.putStringSet("history", history);
                if (!editor.commit()) {
                    Toast.makeText(MainActivity.this, "Failed to save shared preferences",
                            Toast.LENGTH_LONG).show();
                }
                setText("Good job!");
            }
        });
    }

    private void save() {
        Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
        editor.putInt("hang_time", mHangTime);
        editor.putInt("pause_time", mPauseTime);
        editor.putInt("repetitions", mRepetitions);
        editor.putInt("rest_time", mRestTime);
        editor.putInt("total_repetitions", mTotalRepetitions);
        if (!editor.commit()) {
            Toast.makeText(this, "Failed to save shared preferences", Toast.LENGTH_LONG).show();
        }
    }

    private void load() {
        SharedPreferences prefs = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        mHangTime = prefs.getInt("hang_time", mHangTime);
        mPauseTime = prefs.getInt("pause_time", mPauseTime);
        mRepetitions = prefs.getInt("repetitions", mRepetitions);
        mRestTime = prefs.getInt("rest_time", mRestTime);
        mTotalRepetitions = prefs.getInt("total_repetitions", mTotalRepetitions);

        mHangTimeText.setText(String.valueOf(mHangTime));
        mPauseTimeText.setText(String.valueOf(mPauseTime));
        mRepetitionsText.setText(String.valueOf(mRepetitions));
        mRestTimeText.setText(String.valueOf(mRestTime));
        mTotalRepetitionsText.setText(String.valueOf(mTotalRepetitions));
    }

    private class MyTextWatcher implements TextWatcher {

        private int mId;

        public MyTextWatcher(int viewId) {
            mId = viewId;
        }

        public void afterTextChanged(Editable editable) {
            if (editable.length() > 0) {
                int val = Integer.valueOf(editable.toString());
                switch (mId) {
                    case R.id.hang_time:
                        mHangTime = val;
                        break;
                    case R.id.pause_time:
                        mPauseTime = val;
                        break;
                    case R.id.repetitions:
                        mRepetitions = val;
                        break;
                    case R.id.rest_time:
                        mRestTime = val;
                        break;
                    case R.id.total_repetitions:
                        mTotalRepetitions = val;
                        break;
                    default:
                }
                save();
            }
        }

        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

    }
}
