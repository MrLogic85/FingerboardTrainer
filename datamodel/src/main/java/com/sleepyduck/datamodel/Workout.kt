package com.sleepyduck.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Workout(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val icon: Int = WorkoutIcon.GIRL_EASY_CLIMB.id,
    val lastWorkout: Long? = null,
    val timesRun: Int = 0,
    var workoutData: List<WorkoutElement>? = null
) : Parcelable