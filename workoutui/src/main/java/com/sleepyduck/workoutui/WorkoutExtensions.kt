package com.sleepyduck.workoutui

import com.sleepyduck.datamodel.WorkoutElement
import com.sleepyduck.datamodel.WorkoutElementType
import com.sleepyduck.datamodel.toWorkoutElementType
import com.sleepyduck.workoutui.adapteritem.ItemWorkout
import com.sleepyduck.workoutui.adapteritem.ItemWorkoutPause
import com.sleepyduck.workoutui.adapteritem.ItemWorkoutRepeat
import com.sleepyduck.workoutui.adapteritem.ItemWorkoutSay
import java.util.*

fun List<WorkoutElement>.toListUIAdapterItems(
    adapter: ListUIAdapter,
    onItemClickListener: (ItemWorkout) -> Unit?
): List<ListUIAdapterItem> = map { it.toListUIAdapterItem(adapter, onItemClickListener) }

fun WorkoutElement.toListUIAdapterItem(
    adapter: ListUIAdapter,
    onItemClickListener: (ItemWorkout) -> Unit?
): ItemWorkout {
    return when (type.toWorkoutElementType()) {
        WorkoutElementType.REPEAT -> ItemWorkoutRepeat(
            this,
            adapter,
            onItemClickListener
        )

        WorkoutElementType.SAY -> ItemWorkoutSay(
            this,
            adapter,
            onItemClickListener
        )

        WorkoutElementType.PAUSE -> ItemWorkoutPause(
            this,
            adapter,
            onItemClickListener
        )
    }
}

fun List<WorkoutElement>.replaceWhen(
    newElement: WorkoutElement,
    condition: (WorkoutElement) -> Boolean
): List<WorkoutElement> {
    return map {
        val element = if (condition(it)) newElement else it
        element.copy(
            workouts = element.workouts.replaceWhen(newElement, condition)
        )
    }
}

fun Long.toMillisString(): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    return when {
                this > 60000L -> "${calendar.get(Calendar.MINUTE)}m ${calendar.get(Calendar.SECOND)}s"
                else -> "${calendar.get(Calendar.SECOND)}s"
    }
}

data class TimeStamp(val hours: Int, val minutes: Int, val seconds: Int)

fun Long.toTimeStamp(): TimeStamp = TimeStamp(
    (this / 3600000L % 24).toInt(),
    (this / 60000L % 60).toInt(),
    (this / 1000L % 60).toInt()
)