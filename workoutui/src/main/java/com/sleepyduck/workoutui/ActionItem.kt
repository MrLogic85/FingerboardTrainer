package com.sleepyduck.workoutui

open class ActionItem(
    id: Long,
    adapter: ListUIAdapter
) : ListUIAdapterItem(id, adapter) {
    override val layout: Int = R.layout.action_layout
}