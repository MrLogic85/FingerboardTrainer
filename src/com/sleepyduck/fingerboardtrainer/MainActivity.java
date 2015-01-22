
package com.sleepyduck.fingerboardtrainer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends Activity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        mStartButton.setText(R.string.stop);
        mRunning = true;
        new Thread() {
            @Override
            public void run() {
                for (int time = 3; time > 0 && mRunning; --time) {
                    setText("Start in " + time + "s");
                    sleepFor(1000);
                }
                int totalRep = mTotalRepetitions;
                int rep;
                while ((totalRep--) > 0 && mRunning) {
                    rep = mRepetitions;
                    while ((rep--) > 0 && mRunning) {
                        vibrateHang();
                        for (int time = mHangTime; time > 0 && mRunning; --time) {
                            setText("Hang " + time + "s");
                            sleepFor(1000);
                        }
                        vibratePause();
                        if (rep > 0) {
                            for (int time = mPauseTime; time > 0 && mRunning; --time) {
                                setText("Pause " + time + "s");
                                sleepFor(1000);
                            }
                        }
                    }
                    if (totalRep > 0) {
                        Calendar cal = Calendar.getInstance();
                        for (int time = mRestTime * 60; time > 0 && mRunning; --time) {
                            cal.setTimeInMillis(time * 1000);
                            setText("Rest " + cal.get(Calendar.MINUTE) + "m "
                                    + cal.get(Calendar.SECOND) + "s");
                            sleepFor(1000);
                            if (time == 4) {
                                vibrateWarn();
                            }
                        }
                    }
                }
                if (mRunning) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            trainingCompleted();
                        }
                    });
                }
            }
        }.start();
    }

    private void stop() {
        mStartButton.setText(R.string.start);
        mRunning = false;
        mTextView.setText("");
    }

    protected void setText(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mTextView.setText(text);
            }
        });
    }

    private void vibrateHang() {
        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(800);
    }

    private void vibratePause() {
        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[] {
                0, 200, 200, 200, 200, 200
        }, -1);
    }

    private void vibrateWarn() {
        Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(new long[] {
                0, 400, 600, 400, 600, 400
        }, -1);
    }

    private void sleepFor(long millis) {
        long sleepTime = System.currentTimeMillis() + millis;
        while (sleepTime > System.currentTimeMillis()) {
            try {
                Thread.sleep(sleepTime - System.currentTimeMillis());
            } catch (InterruptedException ignore) {

            }
        }
    }

    private void trainingCompleted() {
        stop();
        SharedPreferences prefs = getSharedPreferences("shared_prefs", MODE_PRIVATE);
        Set<String> history = new HashSet<String>();
        history = prefs.getStringSet("history", history);
        Editor editor = prefs.edit();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        sb.append(cal.get(Calendar.YEAR)).append("-").append(cal.get(Calendar.MONTH) + 1)
                .append("-").append(cal.get(Calendar.DAY_OF_MONTH)).append(" ")
                .append(cal.get(Calendar.HOUR_OF_DAY)).append(":").append(cal.get(Calendar.MINUTE));
        sb.append(" - Hang/Pause " + mHangTime + "s/" + mPauseTime + "s, Repetitions "
                + mRepetitions);
        sb.append(", Rest " + mRestTime + "m, Total repetitions " + mTotalRepetitions);
        history.add(sb.toString());
        editor.putStringSet("history", history);
        editor.commit();
        setText("Training \"" + sb.toString() + "\" has been added to the history");
    }

    private void save() {
        Editor editor = getSharedPreferences("shared_prefs", MODE_PRIVATE).edit();
        editor.putInt("hang_time", mHangTime);
        editor.putInt("pause_time", mPauseTime);
        editor.putInt("repetitions", mRepetitions);
        editor.putInt("rest_time", mRestTime);
        editor.putInt("total_repetitions", mTotalRepetitions);
        editor.commit();
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

        @Override
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

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

    }
}
