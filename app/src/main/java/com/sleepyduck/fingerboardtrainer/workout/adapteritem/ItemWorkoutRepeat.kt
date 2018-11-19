package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.datamodel.WorkoutElement
import com.sleepyduck.fingerboardtrainer.workout.toListUIAdapterItems
import com.sleepyduck.workoutui.ListUIAdapter
import com.sleepyduck.workoutui.ListUIAdapterItem
import com.sleepyduck.workoutui.setupForListUIAdapter
import kotlinx.android.synthetic.main.action_layout.view.*
import kotlinx.android.synthetic.main.group_item_layout.view.*


class ItemWorkoutRepeat(
    val workout: WorkoutElement,
    adapter: ListUIAdapter
) : ListUIAdapterItem(workout.hashCode().toLong(), adapter) {

    var items = listOf<ListUIAdapterItem>()
        private set

    private val innerAdapter = ListUIAdapter().apply {
        items = workout.workouts?.toListUIAdapterItems(this) ?: listOf()
        this@ItemWorkoutRepeat.items = items
    }

    override val layout: Int = com.sleepyduck.workoutui.R.layout.group_item_layout

    override val backgroundColor = R.color.materialLight_Grey

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.recyclerView.apply {
            setupForListUIAdapter()

            if (adapter != innerAdapter) {
                adapter = innerAdapter
            }
        }

        holder.itemView.actionGroup.visibility = View.GONE
        holder.itemView.hint.visibility = View.GONE
        holder.itemView.icon.setImageResource(com.sleepyduck.fingerboardtrainer.R.drawable.icon_repeat)
    }
}