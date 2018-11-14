package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.listui.GroupItem
import com.sleepyduck.listui.ListUIAdapterItem
import kotlinx.android.synthetic.main.action_layout.view.*

data class ItemWorkoutRepeat(
    val identifier: Long,
    val name: String,
    val count: Int,
    val items: List<ListUIAdapterItem>
) : GroupItem(identifier, items) {

    override val backgroundColor = R.color.materialLight_Grey

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.name.text = name
        holder.itemView.actionGroup.visibility = View.GONE
        holder.itemView.hint.visibility = View.GONE
        holder.itemView.icon.setImageResource(com.sleepyduck.fingerboardtrainer.R.drawable.icon_repeat)
    }
}