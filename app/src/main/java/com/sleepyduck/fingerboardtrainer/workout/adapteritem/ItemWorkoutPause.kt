package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.listui.ListUIAdapter
import kotlinx.android.synthetic.main.action_layout.view.*

class ItemWorkoutPause(
    workout: WorkoutElement,
    private val adapter: ListUIAdapter
) : ItemWorkoutTimer(workout, adapter) {

    var name = workout.name
        set(value) {
            field = value
            adapter.notifyItemChanged(this)
        }

    override val backgroundColor = R.color.materialLight_Blue

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.name.text = name
        holder.itemView.icon.setImageResource(R.drawable.icon_climb_2)
        holder.itemView.actionGroup.visibility = View.GONE
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemWorkoutPause) return false
        if (!super.equals(other)) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }
}