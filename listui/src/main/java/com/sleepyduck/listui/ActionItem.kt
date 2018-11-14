package com.sleepyduck.listui

abstract class ActionItem(id: Long) : ListUIAdapterItem(id) {

    override val layout: Int = R.layout.action_layout
}