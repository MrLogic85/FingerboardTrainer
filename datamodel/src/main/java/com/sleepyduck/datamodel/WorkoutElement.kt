package com.sleepyduck.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class WorkoutElement(
        val id: String = UUID.randomUUID().toString(),
        val type: Int = WorkoutElementType.PAUSE.id,
        val icon: Int = WorkoutIcon.GIRL_EASY_CLIMB.id,
        val name: String? = null,
        val repeat: Int? = null,
        val say: String? = null,
        val timeMillis: Long? = null,
        val workouts: List<WorkoutElement>? = null) : Parcelable