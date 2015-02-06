
package com.sleepyduck.fingerboardtrainer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.os.Vibrator;

import java.util.Arrays;
import java.util.Calendar;

public class TimerService extends Service {
    public static final String TAG = "fingerboardtrainer";

    private MainActivity mActivity;

    private TimerThread mTimerThread;

    private MediaPlayer mBeepMidPlayer;

    private MediaPlayer mBeepHighPlayer;

    private MediaPlayer mBeepVerryHighPlayer;

    @Override
    public IBinder onBind(Intent intent) {
        return new TimerBinder(this);
    }

    @Override
    public void onCreate() {
        mBeepMidPlayer = MediaPlayer.create(this, R.raw.beep_mid);
        mBeepHighPlayer = MediaPlayer.create(this, R.raw.beep_hig);
        mBeepVerryHighPlayer = MediaPlayer.create(this, R.raw.beep_very_high);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mTimerThread != null) {
            mTimerThread.mRunning = false;
        }
        mBeepMidPlayer.release();
        mBeepHighPlayer.release();
        mBeepVerryHighPlayer.release();
        super.onDestroy();
        mActivity = null;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mActivity = null;
        return super.onUnbind(intent);
    }

    public void setMainActivity(MainActivity activity) {
        mActivity = activity;
    }

    public void startTimer(final int hangTime, final int pauseTime, final int repetitions,
            final int restTime, final int totalRepetitions, final Notification notification) {
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
                        notifyHang(notification);
                        for (int time = hangTime; time > 0 && mRunning; --time) {
                            setText("Round " + (repetitions - rep) + "/" + repetitions + " : Set "
                                    + (totalRepetitions - totalRep) + "/" + totalRepetitions
                                    + "\nHang " + time + "s");
                            sleepFor(1000);
                        }
                        if (mRunning) {
                            notifyPause(notification);
                        }
                        if (rep > 0) {
                            for (int time = pauseTime; time > 0 && mRunning; --time) {
                                setText("Round " + (repetitions - rep) + "/" + repetitions
                                        + " : Set " + (totalRepetitions - totalRep) + "/"
                                        + totalRepetitions + "\nPause " + time + "s");
                                sleepFor(1000);
                            }
                        }
                    }
                    if (totalRep > 0) {
                        Calendar cal = Calendar.getInstance();
                        for (int time = restTime; time > 0 && mRunning; --time) {
                            cal.setTimeInMillis(time * 1000);
                            setText("Round " + (repetitions - rep - 1) + "/" + repetitions
                                    + " : Set " + (totalRepetitions - totalRep) + "/"
                                    + totalRepetitions + "\nRest " + cal.get(Calendar.MINUTE)
                                    + "m " + cal.get(Calendar.SECOND) + "s");
                            sleepFor(1000);
                            if (time == 4 && mRunning) {
                                notifyWarn(notification);
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

    private void notifyHang(Notification notification) {
        switch (notification) {
            case VIBRATE:
                Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(800);
                break;
            case SOUND:
                playBeep(mBeepHighPlayer, 800);
                break;
            default:
                break;
        }
    }

    private void notifyPause(Notification notification) {
        switch (notification) {
            case VIBRATE:
                Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(new long[] {
                        0, 200, 200, 200, 200, 200
                }, -1);
                break;
            case SOUND:
                playBeep(mBeepVerryHighPlayer, 200, 200, 200, 200, 200);
                break;
            default:
                break;
        }
    }

    private void notifyWarn(Notification notification) {
        switch (notification) {
            case VIBRATE:
                Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(new long[] {
                        0, 400, 600, 400, 600, 400
                }, -1);
                break;
            case SOUND:
                playBeep(mBeepMidPlayer, 400, 600, 400, 600, 400);
                break;
            default:
                break;
        }
    }

    private void playBeep(final MediaPlayer player, final long... lengths) {
        if (lengths.length % 2 != 1) {
            throw new ArrayIndexOutOfBoundsException();
        }
        player.start();
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(lengths[0]);
                } catch (InterruptedException ignore) {
                } finally {
                    player.pause();
                    player.seekTo(0);
                    if (lengths.length > 2) {
                        new Thread() {
                            public void run() {
                                try {
                                    Thread.sleep(lengths[1]);
                                } catch (InterruptedException ignore) {
                                } finally {
                                    playBeep(player, Arrays.copyOfRange(lengths, 2, lengths.length));
                                }
                            }
                        }.start();
                    }
                }
            }
        }.start();
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
