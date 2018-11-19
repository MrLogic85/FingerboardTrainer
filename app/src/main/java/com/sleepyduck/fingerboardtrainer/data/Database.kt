package com.sleepyduck.fingerboardtrainer.data

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.sleepyduck.datamodel.Workout

data class User(val name: String = "")

private const val USERS = "users"
private const val WORKOUTS = "workouts"

class Database {
    private val firestore = FirebaseFirestore.getInstance()

    init {
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
    }


    fun getOrCreateWorkoutData(
            user: FirebaseUser,
            defaultWorkouts: () -> List<Workout>,
            result: (List<Workout>) -> Unit) {

        val userDocument = firestore.collection(USERS).document(user.uid)

        val createUser = { workouts: List<Workout> ->
            workouts.forEach { workout ->
                userDocument
                        .collection(WORKOUTS)
                        .document(workout.id)
                        .set(workout)
            }
        }

        userDocument
                .get()
                .addOnSuccessListener { userResult ->
                    when {
                        userResult.exists() -> {
                            userDocument
                                    .collection(WORKOUTS)
                                    .get()
                                    .addOnSuccessListener { querySnapshot ->
                                        result(querySnapshot.toObjects(Workout::class.java))
                                    }
                        }

                        else -> {
                            userDocument.set(User(user.displayName ?: ""))
                            val workouts = defaultWorkouts()
                            createUser(workouts)
                            result(workouts)
                        }
                    }
                }
    }
}