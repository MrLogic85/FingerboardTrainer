package com.sleepyduck.listui

import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.group_item_layout.view.*

open class GroupItem(
    id: Long,
    private val subItems: List<ListUIAdapterItem> = listOf()
) : ListUIAdapterItem(id) {

    override val layout: Int = R.layout.group_item_layout

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, payloads)
        holder.itemView.recyclerView.run {
            adapter = ListUIAdapter(subItems)

            if (itemDecorationCount == 0) {
                addItemDecoration(
                    DividerItemDecoration(
                        divider = context.resources.getDrawable(
                            R.drawable.group_item_divider,
                            context.theme
                        )
                    )
                )
            }
        }
    }
}