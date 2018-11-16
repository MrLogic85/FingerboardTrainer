package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.listui.ListUIAdapter
import kotlinx.android.synthetic.main.action_layout.view.*

class ItemWorkoutPause(
    workout: WorkoutElement,
    adapter: ListUIAdapter
) : ItemWorkout(workout, adapter) {

    override val backgroundColor = R.color.materialLight_Blue

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.icon.setImageResource(R.drawable.icon_climb_2)
        holder.itemView.actionGroup.visibility = View.GONE
    }
}