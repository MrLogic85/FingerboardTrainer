package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.fingerboardtrainer.toMillisString
import com.sleepyduck.listui.ActionItem
import kotlinx.android.synthetic.main.action_layout.view.*

data class ItemWorkoutSay(
    val workout: WorkoutElement,
    val selectedElement: WorkoutElement?,
    val recyclerView: RecyclerView,
    val clickListener: ListUIAdapterClickListener
) : ActionItem(workout.hashCode().toLong(), workout == selectedElement) {

    override val backgroundColor = R.color.materialLight_Teal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.name.text = workout.name
        holder.itemView.action.text = workout.say
        holder.itemView.icon.setImageResource(R.drawable.icon_climb_1)
        holder.itemView.actionIcon.setImageResource(R.drawable.icon_say)

        val time = workout.timeMillis ?: 0L
        holder.itemView.hint.visibility = if (time <= 0) View.GONE else View.VISIBLE
        holder.itemView.hint.text = time.toMillisString()

        holder.itemView.action.setOnClickListener {
            clickListener.onElementClicked(workout, recyclerView, holder.adapterPosition)
        }
    }
}