package com.draw.animation.analytics

import android.content.Context
import android.preference.PreferenceManager

object UserPropertyManager {
    private val propertyKeys = listOf(
        "gallery_count",
        "draft_count",
        "draw_use_tem_count",
        "draw_according_tem_count",
        "wallpaper_usage_count"
    )

    fun increase(context: Context, key: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val current = prefs.getInt(key, 0)
        prefs.edit().putInt(key, current + 1).apply()
    }

    fun syncAll(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        propertyKeys.forEach { key ->
            val value = prefs.getInt(key, 0)
            FirebaseAnalyticRepo.setUserProperty(key, value.toString())
        }
    }
}
