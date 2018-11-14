package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.fingerboardtrainer.data.WorkoutElementType
import com.sleepyduck.fingerboardtrainer.data.toWorkoutElementType
import com.sleepyduck.listui.ListUIAdapterItem

interface ListUIAdapterClickListener {
    fun onElementClicked(workoutElement: WorkoutElement, recyclerView: RecyclerView, position: Int)
}

fun List<WorkoutElement>.toListUIAdapterItems(
    selectedElement: WorkoutElement?,
    clickListener: ListUIAdapterClickListener
): List<ListUIAdapterItem> = map { it.toListUIAdapterItem(selectedElement, clickListener) }

fun WorkoutElement.toListUIAdapterItem(
    selectedElement: WorkoutElement?,
    clickListener: ListUIAdapterClickListener
) = when (type.toWorkoutElementType()) {
    WorkoutElementType.REPEAT -> ItemWorkoutRepeat(this, selectedElement, clickListener)
    WorkoutElementType.SAY -> ItemWorkoutSay(this, selectedElement, clickListener)
    WorkoutElementType.PAUSE -> ItemWorkoutPause(this, selectedElement, clickListener)
}