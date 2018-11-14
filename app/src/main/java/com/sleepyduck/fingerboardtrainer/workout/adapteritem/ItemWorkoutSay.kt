package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.listui.ActionItem
import kotlinx.android.synthetic.main.action_layout.view.*

data class ItemWorkoutSay(
    val identifier: Long,
    val name: String,
    val say: String
) : ActionItem(identifier) {

    override val backgroundColor = R.color.materialLight_Teal

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.name.text = name
        holder.itemView.action.text = say
        holder.itemView.hint.visibility = View.GONE
        holder.itemView.icon.setImageResource(R.drawable.icon_climb_1)
        holder.itemView.actionIcon.setImageResource(R.drawable.icon_say)
    }
}