package com.sleepyduck.listui

import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

open class ListUIAdapterItem(
    val id: Long,
    private val adapter: ListUIAdapter
) {

    companion object {
        const val KEY_SELECTED = "selected"
    }

    @LayoutRes
    open val layout: Int = 0

    @ColorRes
    open val backgroundColor = 0

    open var selected = false
        set(value) {
            adapter.notifyItemChanged(this, KEY_SELECTED to field)
            field = value
        }

    open fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        payloads: Map<String, Any?>
    ) {
        holder.itemView.run {
            setBackgroundColor(context.resources.getColor(backgroundColor))
            tag = if (selected) ListUIAdapter.TAG_SELECTED else null
        }
    }
}