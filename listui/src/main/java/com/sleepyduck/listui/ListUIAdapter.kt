package com.sleepyduck.listui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ListUIAdapter(
        items: List<ListUIAdapterItem> = listOf()
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items = items
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) =
            items[position].onBindViewHolder(holder, payloads)

    override fun getItemId(position: Int) = items[position].id

    override fun getItemViewType(position: Int) = items[position].layout

}

class ListUIViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

private class Diff(val oldList: List<ListUIAdapterItem>, val newList: List<ListUIAdapterItem>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int) = oldList[oldPos].id == newList[newPos].id

    override fun areContentsTheSame(oldPos: Int, newPos: Int) = oldList[oldPos] == newList[newPos]

}