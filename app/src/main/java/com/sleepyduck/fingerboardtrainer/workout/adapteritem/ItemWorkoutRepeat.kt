package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.listui.ListUIAdapter
import com.sleepyduck.listui.ListUIAdapterItem
import kotlinx.android.synthetic.main.action_layout.view.*
import kotlinx.android.synthetic.main.group_item_layout.view.*

data class ItemWorkoutRepeat(
    val workout: WorkoutElement,
    val selectedElement: WorkoutElement?,
    val recyclerView: RecyclerView,
    val clickListener: ListUIAdapterClickListener
) : ListUIAdapterItem(workout.hashCode().toLong(), workout == selectedElement) {

    override val layout: Int = com.sleepyduck.listui.R.layout.group_item_layout


    override val backgroundColor = R.color.materialLight_Grey

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.recyclerView.run {
            adapter = ListUIAdapter(
                this,
                workout.workouts?.toListUIAdapterItems(selectedElement, clickListener) ?: listOf()
            )
        }

        holder.itemView.name.text = workout.name
        holder.itemView.actionGroup.visibility = View.GONE
        holder.itemView.hint.visibility = View.GONE
        holder.itemView.icon.setImageResource(com.sleepyduck.fingerboardtrainer.R.drawable.icon_repeat)

        holder.itemView.action.setOnClickListener {
            clickListener.onElementClicked(workout, recyclerView, holder.adapterPosition)
        }
    }
}