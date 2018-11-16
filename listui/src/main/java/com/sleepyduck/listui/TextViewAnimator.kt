package com.sleepyduck.listui

import android.animation.ObjectAnimator
import android.util.TypedValue
import android.widget.TextView

class TextViewAnimator(val textView: TextView) {
    var textPx: Float
        get() {
            return textView.textSize
        }
        set(value) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    fun animateTextSize(fromId: Int, toId: Int, reverse: Boolean) {
        val resources = textView.context.resources
        val animator = ObjectAnimator.ofFloat(
            this,
            "textPx",
            resources.getDimension(fromId),
            resources.getDimension(toId)
        )
        if (reverse) animator.reverse()
        else animator.start()
    }
}