package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.fingerboardtrainer.data.WorkoutElement
import com.sleepyduck.listui.ListUIAdapter
import com.sleepyduck.listui.ListUIAdapterItem
import com.sleepyduck.listui.setupForListUIAdapter
import kotlinx.android.synthetic.main.action_layout.view.*
import kotlinx.android.synthetic.main.group_item_layout.view.*


class ItemWorkoutRepeat(
    val workout: WorkoutElement,
    private val adapter: ListUIAdapter
) : ListUIAdapterItem(workout.hashCode().toLong(), adapter) {

    var items = listOf<ListUIAdapterItem>()
        private set

    private val innerAdapter = ListUIAdapter().apply {
        items = workout.workouts?.toListUIAdapterItems(this) ?: listOf()
        this@ItemWorkoutRepeat.items = items
    }

    var name = workout.name
        set(value) {
            field = value
            adapter.notifyItemChanged(this)
        }

    var count = workout.repeat
        set(value) {
            field = value
            adapter.notifyItemChanged(this)
        }

    override val layout: Int = com.sleepyduck.listui.R.layout.group_item_layout

    override val backgroundColor = R.color.materialLight_Grey

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: Map<String, Any?>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.recyclerView.apply {
            setupForListUIAdapter()

            if (adapter != innerAdapter) {
                adapter = innerAdapter
            }
        }

        holder.itemView.name.text = name
        holder.itemView.actionGroup.visibility = View.GONE
        holder.itemView.hint.visibility = View.GONE
        holder.itemView.icon.setImageResource(com.sleepyduck.fingerboardtrainer.R.drawable.icon_repeat)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemWorkoutRepeat

        if (name != other.name) return false
        if (count != other.count) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (count ?: 0)
        return result
    }
}