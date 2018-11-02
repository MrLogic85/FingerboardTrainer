
package com.sleepyduck.fingerboardtrainer;

import android.os.Binder;

class TimerBinder extends Binder {

    private TimerService mService;

    TimerBinder(TimerService timerService) {
        mService = timerService;
    }

    TimerService getService() {
        return mService;
    }

}
