
package com.sleepyduck.fingerboardtrainer;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import java.util.Calendar;

public class TimerService extends Service {
    public static final String TAG = "fingerboardtrainer";

    private MainActivity mActivity;

    private TimerThread mTimerThread;

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return new TimerBinder(this);
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "TimerService.onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "TimerService.onDestroy");
        if (mTimerThread != null) {
            mTimerThread.mRunning = false;
        }
        super.onDestroy();
        mActivity = null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind");
        mActivity = null;
        return super.onUnbind(intent);
    }

    public void setMainActivity(MainActivity activity) {
        mActivity = activity;
    }

    public void startTimer(final int hangTime, final int pauseTime, final int repetitions,
            final int restTime, final int totalRepetitions) {
        mTimerThread = new TimerThread() {
            @Override
            public void run() {
                for (int time = 3; time > 0 && mRunning; --time) {
                    setText("Start in " + time + "s");
                    sleepFor(1000);
                }
                int totalRep = totalRepetitions;
                int rep;
                while ((totalRep--) > 0 && mRunning) {
                    rep = repetitions;
                    while ((rep--) > 0 && mRunning) {
                        vibrateHang();
                        for (int time = hangTime; time > 0 && mRunning; --time) {
                            setText("Hang " + time + "s");
                            sleepFor(1000);
                        }
                        vibratePause();
                        if (rep > 0) {
                            for (int time = pauseTime; time > 0 && mRunning; --time) {
                                setText("Pause " + time + "s");
                                sleepFor(1000);
                            }
                        }
                    }
                    if (totalRep > 0) {
                        Calendar cal = Calendar.getInstance();
                        for (int time = restTime * 60; time > 0 && mRunning; --time) {
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
                    trainingCompleted(hangTime, pauseTime, repetitions, restTime, totalRepetitions);
                }
            }
        };
        mTimerThread.start();
    }

    public void stopTimer() {
        if (mTimerThread != null) {
            mTimerThread.mRunning = false;
        }
    }

    protected void setText(String string) {
        if (mActivity != null) {
            mActivity.setText(string);
        }
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

    private void trainingCompleted(final int hangTime, final int pauseTime, final int repetitions,
            final int restTime, final int totalRepetitions) {
        if (mActivity != null) {
            mActivity.onTrainingCompleted(hangTime, pauseTime, repetitions, restTime,
                    totalRepetitions);
        }
    }

    private class TimerThread extends Thread {
        boolean mRunning = false;

        @Override
        public synchronized void start() {
            mRunning = true;
            super.start();
        }
    }
}
