package com.sleepyduck.datamodel

enum class WorkoutElementType(val id: Int) {
    REPEAT(1),
    PAUSE(2),
    SAY(3)
}

fun Int.toWorkoutElementType() = when (this) {
    1 -> WorkoutElementType.REPEAT
    2 -> WorkoutElementType.PAUSE
    3 -> WorkoutElementType.SAY
    else -> WorkoutElementType.PAUSE
}