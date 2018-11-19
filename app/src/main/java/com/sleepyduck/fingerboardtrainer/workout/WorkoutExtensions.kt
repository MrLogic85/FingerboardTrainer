package com.sleepyduck.fingerboardtrainer.workout

import com.sleepyduck.datamodel.WorkoutElement
import com.sleepyduck.datamodel.WorkoutElementType
import com.sleepyduck.datamodel.toWorkoutElementType
import com.sleepyduck.fingerboardtrainer.workout.adapteritem.ItemWorkoutPause
import com.sleepyduck.fingerboardtrainer.workout.adapteritem.ItemWorkoutRepeat
import com.sleepyduck.fingerboardtrainer.workout.adapteritem.ItemWorkoutSay
import com.sleepyduck.workoutui.ListUIAdapter
import com.sleepyduck.workoutui.ListUIAdapterItem

fun List<WorkoutElement>.toListUIAdapterItems(adapter: ListUIAdapter): List<ListUIAdapterItem> =
    map { it.toListUIAdapterItem(adapter) }

fun WorkoutElement.toListUIAdapterItem(adapter: ListUIAdapter) =
    when (type.toWorkoutElementType()) {
        WorkoutElementType.REPEAT -> ItemWorkoutRepeat(
            this,
            adapter
        )
        WorkoutElementType.SAY -> ItemWorkoutSay(
            this,
            adapter
        )
        WorkoutElementType.PAUSE -> ItemWorkoutPause(
            this,
            adapter
        )
    }