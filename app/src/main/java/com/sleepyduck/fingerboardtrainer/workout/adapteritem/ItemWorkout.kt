package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.fingerboardtrainer.toMillisString
import com.sleepyduck.listui.ActionItem
import com.sleepyduck.listui.ListUIAdapter
import com.sleepyduck.listui.TextViewAnimator
import kotlinx.android.synthetic.main.action_layout.view.*

open class ItemWorkout(
    val workout: WorkoutElement,
    private val adapter: ListUIAdapter
) : ActionItem(workout.hashCode().toLong(), adapter) {

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

    var timeLeft = workout.timeMillis
        set(value) {
            adapter.notifyItemChanged(this, KEY_TIME_LEFT to field)
            field = value
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
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
    }
}