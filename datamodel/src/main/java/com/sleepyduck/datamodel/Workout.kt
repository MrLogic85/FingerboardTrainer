package com.sleepyduck.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Workout(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val icon: Int = WorkoutIcon.GIRL_EASY_CLIMB.id,
    val lastWorkout: Long = 0,
    val timesRun: Int = 0,
    var workoutData: List<WorkoutElement> = listOf()
) : Parcelable