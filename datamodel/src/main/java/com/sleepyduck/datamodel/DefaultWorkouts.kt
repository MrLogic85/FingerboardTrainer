package com.sleepyduck.datamodel

fun createDefaultWorkouts() = listOf(
    Workout(
        title = "Easy Workout",
        description = "Shot quick workout",
        icon = WorkoutIcon.GIRL_EASY_CLIMB.id,
        workoutData = listOf(
            WorkoutElement(
                type = WorkoutElementType.REPEAT.id,
                name = "Repeat",
                repeat = 3,
                workouts = listOf(
                    WorkoutElement(
                        type = WorkoutElementType.REPEAT.id,
                        name = "Repeat",
                        repeat = 3,
                        workouts = listOf(
                            WorkoutElement(
                                type = WorkoutElementType.SAY.id,
                                name = "Hang",
                                say = "Start",
                                timeMillis = 10000L
                            ),
                            WorkoutElement(
                                type = WorkoutElementType.SAY.id,
                                name = "Pause",
                                say = "Stop",
                                timeMillis = 10000L
                            )
                        )
                    ),
                    WorkoutElement(
                        type = WorkoutElementType.PAUSE.id,
                        name = "Rest",
                        timeMillis = 300000L
                    )
                )
            )
        )
    ),
    Workout(
        title = "Hard Workout",
        description = "Long hangs with short breaks",
        icon = WorkoutIcon.GIRL_HARD_CLIMB.id,
        workoutData = listOf(
            WorkoutElement(
                type = WorkoutElementType.SAY.id,
                say = "No"
            )
        )
    )
)