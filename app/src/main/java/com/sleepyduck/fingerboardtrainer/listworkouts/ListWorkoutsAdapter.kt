package com.sleepyduck.fingerboardtrainer.listworkouts

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.Workout
import kotlinx.android.synthetic.main.list_item_workout.view.*

class ListWorkoutsAdapter(workouts: List<Workout>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var workouts: List<Workout> = workouts
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            WorkoutListItemViewHolder(parent, R.layout.list_item_workout)

    override fun getItemCount() = workouts.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val workout = workouts[position]
        holder.itemView.workoutTitle.text = workout.title
    }

}