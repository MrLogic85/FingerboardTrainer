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
) : ActionItem(workout.hashCode().toLong(), adapter) {

    companion object {
        const val KEY_ICON = "icon"
        const val KEY_NAME = "name"
        const val KEY_SAY = "say"
        const val KEY_TIME_LEFT = "timeLeft"
        const val KEY_COUNT = "count"
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

    var count = workout.repeat
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
}