package com.sleepyduck.workoutui.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.datamodel.WorkoutElement
import com.sleepyduck.workoutui.ListUIAdapter
import com.sleepyduck.workoutui.R
import kotlinx.android.synthetic.main.action_layout.view.*

class ItemWorkoutPause(
    workout: WorkoutElement,
    adapter: ListUIAdapter,
    onItemClickListener: (ItemWorkout) -> Unit?
) : ItemWorkout(workout, adapter, onItemClickListener) {

    override val backgroundColor = R.color.materialLight_Blue

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.actionGroup.visibility = View.GONE
    }
}