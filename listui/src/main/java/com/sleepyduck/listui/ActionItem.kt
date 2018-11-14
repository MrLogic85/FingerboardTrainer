package com.sleepyduck.listui

open class ActionItem(
    id: Long,
    selected: Boolean
) : ListUIAdapterItem(id, selected) {
    override val layout: Int = R.layout.action_layout
}