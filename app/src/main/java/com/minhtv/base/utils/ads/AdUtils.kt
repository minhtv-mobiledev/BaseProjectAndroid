package com.draw.animation.utils.ads

import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.AdView

object AdUtils {
    fun destroyAdViews(viewGroup: ViewGroup) {
        viewGroup.visibility = View.GONE
        for (i in 0 until viewGroup.childCount) {
            val childView = viewGroup.getChildAt(i)

            if (childView is AdView) {
                // Nếu là AdView, gọi destroy() để hủy nó
                childView.destroy()
                Log.d("AdView", "AdView đã được hủy.")
            }

            // Nếu childView là một ViewGroup khác, gọi đệ quy để kiểm tra các con của nó
            if (childView is ViewGroup) {
                destroyAdViews(childView)  // Đệ quy
            }
        }
    }

}