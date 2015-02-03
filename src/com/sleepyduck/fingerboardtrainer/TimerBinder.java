package com.sleepyduck.fingerboardtrainer;

import android.os.Binder;

public class TimerBinder extends Binder {

    private TimerService mService;

    public TimerBinder(TimerService timerService) {
        mService = timerService;
    }

    public TimerService getService() {
        return mService;
    }

}
