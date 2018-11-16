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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ListUIAdapterItem) return false

        if (id != other.id) return false
        if (adapter != other.adapter) return false
        if (layout != other.layout) return false
        if (backgroundColor != other.backgroundColor) return false
        if (selected != other.selected) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + adapter.hashCode()
        result = 31 * result + layout
        result = 31 * result + backgroundColor
        result = 31 * result + selected.hashCode()
        return result
    }
}