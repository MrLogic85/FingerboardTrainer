package com.sleepyduck.workoutui.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.datamodel.WorkoutElement
import com.sleepyduck.workoutui.ListUIAdapter
import com.sleepyduck.workoutui.ListUIAdapterItem
import com.sleepyduck.workoutui.R
import com.sleepyduck.workoutui.setupForListUIAdapter
import com.sleepyduck.workoutui.toListUIAdapterItems
import kotlinx.android.synthetic.main.action_layout.view.*
import kotlinx.android.synthetic.main.group_item_layout.view.*

class ItemWorkoutRepeat(
    workout: WorkoutElement,
    adapter: ListUIAdapter,
    onItemClickListener: (ItemWorkout) -> Unit?
) : ItemWorkout(workout, adapter, onItemClickListener) {

    var items = listOf<ListUIAdapterItem>()
        private set

    private val innerAdapter = ListUIAdapter().apply {
        items = workout.workouts?.toListUIAdapterItems(this, onItemClickListener) ?: listOf()
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
    }
}