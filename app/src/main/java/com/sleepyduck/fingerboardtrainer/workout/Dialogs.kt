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
import com.sleepyduck.workoutui.toEditableString
import com.sleepyduck.workoutui.toIconRes
import kotlinx.android.synthetic.main.dialog_edit_workout.view.*

@SuppressLint("InflateParams")
fun MaterialDialog.setWorkout(workout: WorkoutElement): MaterialDialog {

    val view = LayoutInflater.from(windowContext)
        .inflate(R.layout.dialog_edit_workout, null)
    view.icon.setImageResource(workout.icon.toIconRes())
    view.name.setText(workout.name)
    view.say.setText(workout.say)
    view.time.setText(workout.timeMillis.toEditableString())
    view.count.setText("${workout.repeat ?: 0}")

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