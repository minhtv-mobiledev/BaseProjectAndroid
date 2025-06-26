package com.minhtv.base.utils


import android.content.Context
import android.preference.PreferenceManager
import java.util.Locale

object LocaleUtils {
    private const val PREF_LANG_KEY = "selected_language"

    /**
     * Lấy ngôn ngữ đã lưu, nếu chưa lưu thì lấy ngôn ngữ mặc định của hệ thống.
     */
    fun getSavedLanguage(context: Context): String {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getString(PREF_LANG_KEY, Locale.getDefault().language) ?: Locale.getDefault().language
    }

    /**
     * Cập nhật locale dựa trên ngôn ngữ được cung cấp và trả về context mới.
     */
    fun setLocale(context: Context, language: String): Context {
        // Lưu giá trị ngôn ngữ vào SharedPreferences
        persistLanguage(context, language)
        // Tạo locale mới
        val locale = Locale(language)
        Locale.setDefault(locale)

//        // Cập nhật cấu hình với locale mới
//        val config = Configuration(context.resources.configuration)
//        config.setLocale(locale)
//
//        // Trả về context đã được cập nhật locale
//        return context.createConfigurationContext(config)

        val res = context.resources
        val config = res.configuration

        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    /**
     * Lưu giá trị ngôn ngữ vào SharedPreferences.
     */
    private fun persistLanguage(context: Context, language: String) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        prefs.edit().putString(PREF_LANG_KEY, language).apply()
    }
}
