package com.sleepyduck.workoutui

import androidx.annotation.DrawableRes
import com.sleepyduck.datamodel.WorkoutIcon
import com.sleepyduck.datamodel.toIcon
import com.sleepyduck.resources.R

@DrawableRes
fun WorkoutIcon.iconRes() = when (this) {
    WorkoutIcon.GIRL_EASY_CLIMB -> R.drawable.icon_climb_1
    WorkoutIcon.GIRL_NORMAL_CLIMB -> R.drawable.icon_climb_2
    WorkoutIcon.GIRL_MEDIUM_CLIMB -> R.drawable.icon_climb_3
    WorkoutIcon.GIRL_HARD_CLIMB -> R.drawable.icon_climb_4
    WorkoutIcon.REPEAT -> R.drawable.icon_repeat
}

fun Int.toIconRes() = toIcon().iconRes()