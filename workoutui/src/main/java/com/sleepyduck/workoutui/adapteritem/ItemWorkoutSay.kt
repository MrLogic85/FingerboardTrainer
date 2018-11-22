package com.sleepyduck.workoutui.adapteritem

import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.datamodel.WorkoutElement
import com.sleepyduck.workoutui.ListUIAdapter
import com.sleepyduck.workoutui.R
import kotlinx.android.synthetic.main.action_layout.view.*

class ItemWorkoutSay(
    workout: WorkoutElement,
    adapter: ListUIAdapter,
    onItemClickListener: (ItemWorkout) -> Unit?
) : ItemWorkout(workout, adapter, onItemClickListener) {

    override val backgroundColor = R.color.materialLight_Teal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.name.text = name
        holder.itemView.action.text = say
        holder.itemView.actionIcon.setImageResource(R.drawable.icon_say)
    }
}