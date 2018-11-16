package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.fingerboardtrainer.toMillisString
import com.sleepyduck.listui.ListUIAdapter
import com.sleepyduck.listui.TextViewAnimator
import kotlinx.android.synthetic.main.action_layout.view.*

class ItemWorkoutSay(
    workout: WorkoutElement,
    private val adapter: ListUIAdapter
) : ItemWorkoutTimer(workout, adapter) {

    companion object {
        const val KEY_NAME = "name"
        const val KEY_SAY = "say"
        const val KEY_TIME_LEFT = "timeLeft"
    }

    var name = workout.name
        set(value) {
            adapter.notifyItemChanged(this, KEY_NAME to field)
            field = value
        }

    var say = workout.say
        set(value) {
            adapter.notifyItemChanged(this, KEY_SAY to field)
            field = value
        }

    override val backgroundColor = R.color.materialLight_Teal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.name.text = name
        holder.itemView.action.text = say
        holder.itemView.icon.setImageResource(R.drawable.icon_climb_1)
        holder.itemView.actionIcon.setImageResource(R.drawable.icon_say)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemWorkoutSay) return false
        if (!super.equals(other)) return false

        if (name != other.name) return false
        if (say != other.say) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (say?.hashCode() ?: 0)
        return result
    }
}