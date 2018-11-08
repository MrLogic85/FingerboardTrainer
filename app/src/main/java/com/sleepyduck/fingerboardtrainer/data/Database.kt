package com.sleepyduck.fingerboardtrainer.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

data class Workout(val data: String)

fun initlializeDatabase(context: Context) {
    FirebaseApp.initializeApp(context)
}

fun workouts(): LiveData<Workout> {
    val workout = mapOf("title" to "Workout",
            "description" to "Workout description",
            "icon" to 3)

    val db = FirebaseFirestore.getInstance()

    db.collection("users")

    FirebaseFirestore.getInstance()
            .collection("workouts")
            .add(workout)
            .addOnCompleteListener {
                Timber.d("Wrote Hello, World!: %b", it.isSuccessful)
            }

    return MutableLiveData<Workout>()
}