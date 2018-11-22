package com.sleepyduck.datamodel

// *** *** *** DO NOT CHANGE THE ID'S, THEY ARE HARD LINKED IN THE DATABASE TO THE ICON RESOURCE *** *** ***
enum class WorkoutIcon(val id: Int) {
    GIRL_EASY_CLIMB(1),
    GIRL_NORMAL_CLIMB(2),
    GIRL_MEDIUM_CLIMB(3),
    GIRL_HARD_CLIMB(4),

    REPEAT(5)
}

fun Int.toIcon() = when (this) {
    1 -> WorkoutIcon.GIRL_EASY_CLIMB
    2 -> WorkoutIcon.GIRL_NORMAL_CLIMB
    3 -> WorkoutIcon.GIRL_MEDIUM_CLIMB
    4 -> WorkoutIcon.GIRL_HARD_CLIMB
    5 -> WorkoutIcon.REPEAT

    else -> WorkoutIcon.GIRL_EASY_CLIMB
}