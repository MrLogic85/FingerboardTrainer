package com.sleepyduck.listui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

class ListUIAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TAG_SELECTED = "SELECTED"
    }

    init {
        setHasStableIds(true)
    }

    var items = listOf<ListUIAdapterItem>()
        set(value) {
            val diff = Diff(field, value)
            val res = DiffUtil.calculateDiff(diff)
            field = value
            res.dispatchUpdatesTo(this)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ListUIViewHolder(LayoutInflater.from(parent.context).inflate(viewType, parent, false))

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {}

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) = items[position].onBindViewHolder(holder, payloads.toPayloadsMap())

    override fun getItemId(position: Int) = items[position].id

    override fun getItemViewType(position: Int) = items[position].layout

    fun notifyItemChanged(item: ListUIAdapterItem, payload: Pair<String, Any?>? = null) {
        val index = items.indexOf(item)
        if (index >= 0) {
            notifyItemChanged(index, payload)
        }
    }
}

private fun Collection<*>.toPayloadsMap(): Map<String, Any?> = this
    .map { it as? Pair<*, *> }
    .requireNoNulls()
    .toMap()
    .mapKeys { (key, _) -> key as String }

fun RecyclerView.setupForListUIAdapter() {
    if (itemDecorationCount == 0) {
        addItemDecoration(
            DividerItemDecoration(
                divider = context.resources.getDrawable(
                    com.sleepyduck.listui.R.drawable.group_item_divider,
                    context.theme
                )
            )
        )
    }

    (itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
}

class ListUIViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

private class Diff(val oldList: List<ListUIAdapterItem>, val newList: List<ListUIAdapterItem>) :
    DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int) =
        oldList[oldPos].id == newList[newPos].id

    override fun areContentsTheSame(oldPos: Int, newPos: Int) = oldList[oldPos] == newList[newPos]

    override fun getChangePayload(oldPos: Int, newPos: Int) = oldList[oldPos]

}