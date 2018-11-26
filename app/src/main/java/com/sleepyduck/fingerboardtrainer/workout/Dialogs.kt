package com.sleepyduck.fingerboardtrainer.workout

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View.GONE
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.sleepyduck.datamodel.WorkoutElement
import com.sleepyduck.datamodel.WorkoutElementType
import com.sleepyduck.datamodel.toWorkoutElementType
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.workoutui.TimeStamp
import com.sleepyduck.workoutui.toIconRes
import com.sleepyduck.workoutui.toTimeStamp
import kotlinx.android.synthetic.main.dialog_edit_time.view.*
import kotlinx.android.synthetic.main.dialog_edit_workout.view.*

@SuppressLint("InflateParams")
fun MaterialDialog.editWorkout(
    workout: WorkoutElement,
    result: (WorkoutElement) -> Unit
): MaterialDialog {

    val view = LayoutInflater.from(windowContext)
        .inflate(R.layout.dialog_edit_workout, null)
    view.icon.setImageResource(workout.icon.toIconRes())
    view.name.setText(workout.name)
    view.say.setText(workout.say)
    view.count.setText("${workout.repeat}")

    val valueId = R.string.dialog_number_two_values_no_decimals
    val setTime = { timeStamp: TimeStamp ->
        view.timeHours.text = context.getString(valueId, timeStamp.hours)
        view.timeMinutes.text = context.getString(valueId, timeStamp.minutes)
        view.timeSeconds.text = context.getString(valueId, timeStamp.seconds)
    }
    setTime(workout.timeMillis.toTimeStamp())

    view.timeLayout.setOnClickListener {
        MaterialDialog(context)
            .title(res = R.string.dialog_title_edit_time)
            .pickTime(
                view.timeHours.text.toString().toInt(),
                view.timeMinutes.text.toString().toInt(),
                view.timeSeconds.text.toString().toInt(),
                setTime
            )
            .show()
    }

    positiveButton {
        result(
            workout.copy(
                name = view.name.text.toString(),
                say = view.say.text.toString(),
                repeat = view.count.text.toString().toInt(),
                timeMillis = view.timeHours.text.toString().toLong() * 360000L +
                        view.timeMinutes.text.toString().toLong() * 60000L +
                        view.timeSeconds.text.toString().toLong() * 1000L
            )
        )
    }

    when (workout.type.toWorkoutElementType()) {
        WorkoutElementType.REPEAT -> {
            title(res = R.string.dialog_title_repeat)
            view.sayGroup.visibility = GONE
            view.timeGroup.visibility = GONE
        }

        WorkoutElementType.PAUSE -> {
            title(res = R.string.dialog_title_pause)
            view.sayGroup.visibility = GONE
            view.countGroup.visibility = GONE
        }

        WorkoutElementType.SAY -> {
            title(res = R.string.dialog_title_say)
            view.countGroup.visibility = GONE
        }
    }

    return customView(view = view)
}

@SuppressLint("InflateParams")
fun MaterialDialog.pickTime(
    hours: Int,
    minutes: Int,
    seconds: Int,
    result: (TimeStamp) -> Unit
): MaterialDialog {

    val view = LayoutInflater.from(windowContext)
        .inflate(R.layout.dialog_edit_time, null)

    view.hoursPicker.apply {
        minValue = 0
        maxValue = 23
        value = hours
    }

    view.minutesPicker.apply {
        minValue = 0
        maxValue = 59
        value = minutes
    }

    view.secondsPicker.apply {
        minValue = 0
        maxValue = 59
        value = seconds
    }

    positiveButton {
        result(
            TimeStamp(
                view.hoursPicker.value,
                view.minutesPicker.value,
                view.secondsPicker.value
            )
        )
    }

    return customView(view = view)
}