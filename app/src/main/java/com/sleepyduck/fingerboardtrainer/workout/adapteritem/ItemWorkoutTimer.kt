package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.fingerboardtrainer.toMillisString
import com.sleepyduck.listui.ActionItem
import com.sleepyduck.listui.ListUIAdapter
import com.sleepyduck.listui.TextViewAnimator
import kotlinx.android.synthetic.main.action_layout.view.*

open class ItemWorkoutTimer(
    val workout: WorkoutElement,
    private val adapter: ListUIAdapter
) : ActionItem(workout.hashCode().toLong(), adapter) {

    companion object {
        const val KEY_TIME_LEFT = "timeLeft"
    }

    var timeLeft = workout.timeMillis
        set(value) {
            adapter.notifyItemChanged(this, KEY_TIME_LEFT to field)
            field = value
        }

    override val backgroundColor = R.color.materialLight_Teal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.hint.text = timeLeft.toMillisString()

        if (payloads.containsKey(KEY_SELECTED)) {
            TextViewAnimator(holder.itemView.hint)
                .animateTextSize(
                    R.dimen.hint_font_size_small,
                    R.dimen.hint_font_size_large,
                    !selected
                )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ItemWorkoutTimer) return false
        if (!super.equals(other)) return false

        if (timeLeft != other.timeLeft) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (timeLeft?.hashCode() ?: 0)
        return result
    }
}