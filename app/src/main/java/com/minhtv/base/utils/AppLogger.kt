package com.draw.animation.utils

import android.util.Log
import com.minhtv.base.BuildConfig

object AppLogger {
    private const val DEFAULT_TAG = "AppLogger"
    private var isDebugMode: Boolean = BuildConfig.DEBUG

    /** Khởi tạo logger, chỉ bật log trong chế độ debug */
    fun init(isDebug: Boolean) {
//        isDebugMode = isDebug
    }

    private fun getCallerInfo(): String {
        val stackTrace = Throwable().stackTrace
        for (element in stackTrace) {
            if (!element.className.contains("AppLogger") && !element.className.contains("java.lang.Thread")) {
                val className = element.className.substringAfterLast(".") // Lấy tên class
                return "$className.${element.methodName}()"
            }
        }
        return "Unknown"
    }

    /** DEBUG logs */
    fun d(message: String) = d(DEFAULT_TAG, getCallerInfo(), message)
    fun d(tag: String, callerInfo: String, message: String) {
        if (isDebugMode) Log.d(tag, "$callerInfo: $message")
    }

    /** INFO logs */
    fun i(message: String) = i(DEFAULT_TAG, getCallerInfo(), message)
    fun i(tag: String, callerInfo: String, message: String) {
        if (isDebugMode) Log.i(tag, "$callerInfo: $message")
    }

    /** WARNING logs */
    fun w(message: String) = w(DEFAULT_TAG, getCallerInfo(), message)
    fun w(tag: String, callerInfo: String, message: String) {
        if (isDebugMode) Log.w(tag, "$callerInfo: $message")
    }

    /** ERROR logs */
    fun e(message: String, throwable: Throwable? = null) = e(DEFAULT_TAG, getCallerInfo(), message, throwable)
    fun e(tag: String, callerInfo: String, message: String, throwable: Throwable?) {
        if (isDebugMode) Log.e(tag, "$callerInfo: $message", throwable)
    }

    /** VERBOSE logs */
    fun v(message: String) = v(DEFAULT_TAG, getCallerInfo(), message)
    fun v(tag: String, callerInfo: String, message: String) {
        if (isDebugMode) Log.v(tag, "$callerInfo: $message")
    }
    fun d(tag: String, message: String) {
        if (isDebugMode) Log.d(tag, message)
    }
    fun i(tag: String, message: String) {
        if (isDebugMode) Log.i(tag, message)
    }
    fun w(tag: String, message: String) {
        if (isDebugMode) Log.w(tag, message)
    }
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        if (isDebugMode) Log.e(tag, message, throwable)
    }
    fun v(tag: String, message: String) {
        if (isDebugMode) Log.v(tag, message)
    }
}
