package com.sleepyduck.workoutui.adapteritem

import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.datamodel.WorkoutElement
import com.sleepyduck.workoutui.ActionItem
import com.sleepyduck.workoutui.ListUIAdapter
import com.sleepyduck.workoutui.R
import com.sleepyduck.workoutui.TextViewAnimator
import com.sleepyduck.workoutui.toIconRes
import com.sleepyduck.workoutui.toMillisString
import kotlinx.android.synthetic.main.action_layout.view.*

open class ItemWorkout(
    val workout: WorkoutElement,
    private val adapter: ListUIAdapter,
    val onItemClickListener: (ItemWorkout) -> Unit?
) : ActionItem(workout.id.hashCode().toLong(), adapter) {

    companion object {
        const val KEY_ICON = "icon"
        const val KEY_NAME = "name"
        const val KEY_SAY = "say"
        const val KEY_TIME_LEFT = "timeLeft"
        const val KEY_COUNT = "repeat"
    }

    var icon = workout.icon
        set(value) {
            adapter.notifyItemChanged(this, KEY_ICON to field)
            field = value
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

    var timeLeft = workout.timeMillis
        set(value) {
            adapter.notifyItemChanged(this, KEY_TIME_LEFT to field)
            field = value
        }

    var repeat = workout.repeat
        set(value) {
            adapter.notifyItemChanged(this, KEY_COUNT to field)
            field = value
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.icon.setImageResource(icon.toIconRes())
        holder.itemView.name.text = name
        holder.itemView.action.text = say
        holder.itemView.hint.text = timeLeft.toMillisString()

        if (payloads.containsKey(KEY_SELECTED)) {
            TextViewAnimator(holder.itemView.hint)
                .animateTextSize(
                    R.dimen.hint_font_size_small,
                    R.dimen.hint_font_size_large,
                    !selected
                )
        }

        holder.itemView.setOnClickListener {
            onItemClickListener(this)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemWorkout) return false
        if (!super.equals(other)) return false

        if (icon != other.icon) return false
        if (name != other.name) return false
        if (say != other.say) return false
        if (timeLeft != other.timeLeft) return false
        if (repeat != other.repeat) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + icon
        result = 31 * result + name.hashCode()
        result = 31 * result + say.hashCode()
        result = 31 * result + timeLeft.hashCode()
        result = 31 * result + repeat
        return result
    }
}