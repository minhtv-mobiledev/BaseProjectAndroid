package com.draw.animation.utils

import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.widget.LinearLayout.HORIZONTAL
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class SlowScrollLinearLayoutManager(
    context: Context,
    orientation: Int = RecyclerView.HORIZONTAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {
    override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
        val scroller = object : LinearSmoothScroller(recyclerView.context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 1000f / displayMetrics.densityDpi  // → chậm gấp 4 lần mặc định //default = 25f
            }
        }
        scroller.targetPosition = position
        startSmoothScroll(scroller)
    }
}
