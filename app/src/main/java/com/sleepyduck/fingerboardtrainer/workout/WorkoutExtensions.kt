package com.sleepyduck.fingerboardtrainer.workout

import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.fingerboardtrainer.data.WorkoutElementType
import com.sleepyduck.fingerboardtrainer.data.toWorkoutElementType
import com.sleepyduck.fingerboardtrainer.workout.adapteritem.ItemWorkoutPause
import com.sleepyduck.fingerboardtrainer.workout.adapteritem.ItemWorkoutRepeat
import com.sleepyduck.fingerboardtrainer.workout.adapteritem.ItemWorkoutSay
import com.sleepyduck.listui.ListUIAdapter
import com.sleepyduck.listui.ListUIAdapterItem

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