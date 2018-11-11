package com.sleepyduck.fingerboardtrainer

import android.app.Application
import com.google.firebase.FirebaseApp
import timber.log.Timber
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.FirebaseFirestore





class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        FirebaseApp.initializeApp(this)

    }
}
