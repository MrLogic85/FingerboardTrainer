package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.listui.ListUIAdapter
import kotlinx.android.synthetic.main.action_layout.view.*

class ItemWorkoutSay(
    workout: WorkoutElement,
    adapter: ListUIAdapter
) : ItemWorkout(workout, adapter) {

    override val backgroundColor = R.color.materialLight_Teal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.name.text = name
        holder.itemView.action.text = say
        holder.itemView.icon.setImageResource(R.drawable.icon_climb_1)
        holder.itemView.actionIcon.setImageResource(R.drawable.icon_say)
    }
}