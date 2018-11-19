package com.sleepyduck.workoutui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * By https://gist.github.com/lapastillaroja/858caf1a82791b6c1a36
 */
class DividerItemDecoration(
    context: Context? = null,
    attrs: AttributeSet? = null,
    private val showFirstDivider: Boolean = false,
    private val showLastDivider: Boolean = false,
    private var divider: Drawable? = null
) : RecyclerView.ItemDecoration() {

    init {
        if (divider == null && context != null) {
            val a = context.obtainStyledAttributes(attrs, intArrayOf(android.R.attr.listDivider))
            divider = a.getDrawable(0)
            a.recycle()
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val divider = divider ?: return

        if (parent.getChildAdapterPosition(view) < 1) {
            return
        }

        if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
            outRect.top = divider.intrinsicHeight
        } else {
            outRect.left = divider.intrinsicWidth
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val divider = divider

        if (divider == null) {
            super.onDrawOver(c, parent, state)
            return
        }

        // Initialization needed to avoid compiler warning
        var left = 0
        var right = 0
        var top = 0
        var bottom = 0
        val orientation = getOrientation(parent)
        val childCount = parent.childCount

        val size = when (orientation) {

            LinearLayoutManager.VERTICAL -> {
                left = parent.paddingLeft
                right = parent.width - parent.paddingRight
                divider.intrinsicHeight
            }

            else -> {
                top = parent.paddingTop
                bottom = parent.height - parent.paddingBottom
                divider.intrinsicWidth
            }
        }

        val startPos = if (showFirstDivider) 0 else 1
        for (i in startPos until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams

            if (orientation == LinearLayoutManager.VERTICAL) {
                top = child.top - params.topMargin - divider.intrinsicHeight
                bottom = top + size
            } else { //horizontal
                left = child.left - params.leftMargin - divider.intrinsicWidth
                right = left + size
            }
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }

        // show last divider
        if (showLastDivider && childCount > 0) {
            val child = parent.getChildAt(childCount - 1)
            val params = child.layoutParams as RecyclerView.LayoutParams
            if (orientation == LinearLayoutManager.VERTICAL) {
                top = child.bottom + params.bottomMargin
                bottom = top + size
            } else { // horizontal
                left = child.right + params.rightMargin
                right = left + size
            }
            divider.setBounds(left, top, right, bottom)
            divider.draw(c)
        }
    }

    private fun getOrientation(parent: RecyclerView): Int {
        if (parent.layoutManager is LinearLayoutManager) {
            val layoutManager = parent.layoutManager as LinearLayoutManager
            return layoutManager.orientation
        } else {
            throw IllegalStateException(
                "DividerItemDecoration can only be used with a LinearLayoutManager."
            )
        }
    }
}