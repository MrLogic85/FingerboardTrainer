package com.sleepyduck.listui

open class ActionItem(
    id: Long,
    adapter: ListUIAdapter
) : ListUIAdapterItem(id, adapter) {

    override val layout: Int = R.layout.action_layout

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ActionItem) return false
        if (!super.equals(other)) return false

        if (layout != other.layout) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + layout
        return result
    }
}