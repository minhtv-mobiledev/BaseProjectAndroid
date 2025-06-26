package com.minhtv.base.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.minhtv.base.application.MyApplication
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.SystemClock
import android.text.TextPaint
import android.widget.TextView
import android.widget.VideoView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.minhtv.base.R

fun Fragment.setDebouncedClick(vararg views: Pair<View, () -> Unit>, debounceTime: Long = 500L) {
    var lastClickTime = 0L

    val listener = View.OnClickListener { view ->
        val now = System.currentTimeMillis()
        if (now - lastClickTime >= debounceTime) {
            lastClickTime = now
            views.find { it.first == view }?.second?.invoke()
        }
    }

    views.forEach { (view, _) ->
        view.setOnClickListener(listener)
    }
}

fun dpToPx(context: Context = MyApplication.context, dp: Int): Int {
    val density = context.resources.displayMetrics.density
    return Math.round(dp * density)
}

fun View.setDebouncedClickListener(interval: Long = 500L, onClick: (View) -> Unit) {
    // mỗi View có một biến lastClickTime riêng nhờ closure
    var lastClickTime = 0L

    setOnClickListener { view ->
        val now = SystemClock.elapsedRealtime()
        if (now - lastClickTime >= interval) {
            lastClickTime = now
            onClick(view)
        }
    }
}
fun TextView.setGradiantText(start: String, end: String) {
    val paint: TextPaint = this.paint
    val width: Float = paint.measureText(this.text.toString())
    val textShader: Shader = LinearGradient(
        0f, 0f, width, this.textSize, intArrayOf(
            Color.parseColor(start), Color.parseColor(end)
        ), null, Shader.TileMode.CLAMP
    )
    this.paint.shader = textShader
}

class AppUtils {
    companion object {
        fun progressCircle(context: Context): CircularProgressDrawable {
            return CircularProgressDrawable(context)
                .apply {
                    strokeWidth = 10f
                    centerRadius = 30f
                    setColorSchemeColors(
                        ContextCompat.getColor(
                            context,
                            R.color.white
                        )
                    )
                }
        }
        fun isNetworkAvailable(context: Context = MyApplication.context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            // For Android M and above
            val network = cm.activeNetwork ?: return false
            val caps   = cm.getNetworkCapabilities(network) ?: return false
            return caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                    || caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
        }

        fun dpToPx(dp: Float, resources: Resources): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.displayMetrics
            ).toInt()
        }
        fun isTablet(context: Context? = MyApplication.context): Boolean {
            val context = context ?: MyApplication.context
            return context!!.resources.configuration.smallestScreenWidthDp >= 600
        }
        fun setLayoutDirectionForAllViews(view: View, context: Context = MyApplication.currentActivity!!, isCheckViewGroup: Boolean = true) {
//            if (isCheckViewGroup && view !is ViewGroup) {
//                return
//            }
            view.post {
                val layoutDirection = if (LanguagePrefHelper.getSavedLanguageCode(context) in listOf("ar", "he", "fa")) {
                    View.LAYOUT_DIRECTION_RTL
                } else {
                    View.LAYOUT_DIRECTION_LTR
                }
                // Set layout direction cho chính view hiện tại
                view.layoutDirection = layoutDirection

                // Duyệt qua tất cả các ViewGroup con (nếu có) và áp dụng layoutDirection
                if (view is ViewGroup) {
                    if (view is RecyclerView) {
                        return@post
                    }
                    for (i in 0 until view.childCount) {
                        val childView = view.getChildAt(i)
                        setLayoutDirectionForAllViews(childView, context, isCheckViewGroup) // Đệ quy gọi lại hàm
                    }
                }
            }
        }

        fun setViewDirection(view : View, context: Context = MyApplication.currentActivity!!) {
            view.post {
                val layoutDirection = if (LanguagePrefHelper.getSavedLanguageCode(context) in listOf("ar", "he", "fa")) {
                    View.LAYOUT_DIRECTION_RTL
                } else {
                    View.LAYOUT_DIRECTION_LTR
                }
                view.layoutDirection = layoutDirection
            }
        }
        fun attachAutoVideoControlToNativeAdView(lifecycleOwner: LifecycleOwner, nativeAdView: ViewGroup) {
            val observer = object : DefaultLifecycleObserver {
                override fun onPause(owner: LifecycleOwner) {
                    pauseVideoIn(nativeAdView)
                }

                override fun onResume(owner: LifecycleOwner) {
                    resumeVideoIn(nativeAdView)
                }
            }

            lifecycleOwner.lifecycle.addObserver(observer)
        }

        private fun pauseVideoIn(viewGroup: ViewGroup) {
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                when (child) {
                    is VideoView -> {
                        if (child.isPlaying) child.pause()
                    }
                    is ViewGroup -> pauseVideoIn(child)
                }
            }
        }

        private fun resumeVideoIn(viewGroup: ViewGroup) {
            for (i in 0 until viewGroup.childCount) {
                val child = viewGroup.getChildAt(i)
                when (child) {
                    is VideoView -> {
                        try {
                            child.start() // resume lại nếu có thể
                        } catch (e: IllegalStateException) {
                            // có thể xảy ra nếu video chưa được chuẩn bị
                            e.printStackTrace()
                        }
                    }
                    is ViewGroup -> resumeVideoIn(child)
                }
            }
        }

    }
}