package com.sleepyduck.listui

import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

open class ListUIAdapterItem(val id: Long) {

    @LayoutRes
    open val layout: Int = 0

    @ColorRes
    open val backgroundColor = 0

    open fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        holder.itemView.run {
            setBackgroundColor(context.resources.getColor(backgroundColor))
        }
    }
}