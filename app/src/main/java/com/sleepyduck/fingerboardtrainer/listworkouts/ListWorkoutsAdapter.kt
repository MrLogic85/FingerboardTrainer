package com.sleepyduck.fingerboardtrainer.listworkouts

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.datamodel.Workout
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.workoutui.toIconRes
import kotlinx.android.synthetic.main.list_item_workout.view.*
import java.util.*

class ListWorkoutsAdapter(
        workouts: List<Workout>,
        private val itemClickListener: (Workout) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var workouts: List<Workout> = workouts
        set(value) {
            val diffResult = DiffUtil.calculateDiff(Diff(field, value))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            WorkoutListItemViewHolder(parent, R.layout.list_item_workout)

    override fun getItemCount() = workouts.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val workout = workouts[position]
        holder.itemView.run {
            icon.setImageResource(workout.icon.toIconRes())
            workoutTitle.text = workout.title
            workoutDescription.text = workout.description

            val lastWorkout = workout.lastWorkout
            when (lastWorkout) {
                null -> timeSinceWorkoutLabel.setText(R.string.last_workout_never)
                else -> (Date().time - lastWorkout).toReadableString(context)
            }

            setOnClickListener {
                itemClickListener(workout)
            }
        }
    }

}

private fun Long.toReadableString(context: Context): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = this
    val year: Int = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return when {
        year > 0 -> context.getString(R.string.last_workout_year, year, month, day)
        month > 0 -> context.getString(R.string.last_workout_month, month, day)
        day > 0 -> context.getString(R.string.last_workout_day, day)
        else -> context.getString(R.string.last_workout_today)
    }
}

private class Diff(val oldList: List<Workout>, val newList: List<Workout>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int) = oldList[oldPos].id == newList[newPos].id

    override fun areContentsTheSame(oldPos: Int, newPos: Int) = oldList[oldPos] == newList[newPos]

}