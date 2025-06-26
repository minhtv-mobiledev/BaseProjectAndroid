package com.minhtv.base.utils.extension
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment

fun Fragment.addOnBackPressedCallback(enabled: Boolean = true, handle: () -> Unit) {
    try {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(enabled) {
                override fun handleOnBackPressed() {
                    // 2) Bọc phần xử lý back trong try/catch để tránh crash khi fragment đã detach
                    try {
                        // đảm bảo fragment vẫn attached trước khi gọi requireContext()/view...
                        if (!isAdded || view == null) return
                        handle()
                    } catch (e: Exception) {
                        // log hoặc ignore
                        Log.w("BackCallback", "Error in back handler", e)
                    }
                }
            }
        )
    } catch (e: IllegalStateException) {
        // fragment chưa attach → không register nữa
        Log.w("BackCallback", "Cannot register back callback (fragment not attached)", e)
    }
}
