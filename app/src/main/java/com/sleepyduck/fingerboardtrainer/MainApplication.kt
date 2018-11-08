package com.sleepyduck.fingerboardtrainer

import android.app.Application
import com.sleepyduck.fingerboardtrainer.data.initlializeDatabase

import timber.log.Timber

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initlializeDatabase(this)
    }
}
