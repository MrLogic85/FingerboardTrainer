package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.fingerboardtrainer.toMillisString
import com.sleepyduck.listui.ActionItem
import kotlinx.android.synthetic.main.action_layout.view.*

data class ItemWorkoutPause(
    val workout: WorkoutElement,
    val selectedElement: WorkoutElement?,
    val recyclerView: RecyclerView,
    val clickListener: ListUIAdapterClickListener
) :
    ActionItem(workout.hashCode().toLong(), workout == selectedElement) {

    override val backgroundColor = R.color.materialLight_Blue

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.name.text = workout.name
        holder.itemView.icon.setImageResource(R.drawable.icon_climb_2)
        holder.itemView.actionGroup.visibility = View.GONE
        holder.itemView.hint.text = workout.timeMillis?.toMillisString()

        holder.itemView.action.setOnClickListener {
            clickListener.onElementClicked(workout, recyclerView, holder.adapterPosition)
        }
    }
}