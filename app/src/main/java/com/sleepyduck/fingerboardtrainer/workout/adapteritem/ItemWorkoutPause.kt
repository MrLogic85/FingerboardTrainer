package com.sleepyduck.fingerboardtrainer.workout.adapteritem

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.sleepyduck.fingerboardtrainer.R
import com.sleepyduck.listui.ActionItem
import kotlinx.android.synthetic.main.action_layout.view.*

data class ItemWorkoutPause(
    val identifier: Long,
    val name: String
) : ActionItem(identifier) {

    override val backgroundColor = R.color.secondaryDarkColor

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.name.text = name
        holder.itemView.icon.setImageResource(R.drawable.icon_climb_2)
        holder.itemView.actionGroup.visibility = View.GONE
        holder.itemView.hint.visibility = View.GONE
    }
}