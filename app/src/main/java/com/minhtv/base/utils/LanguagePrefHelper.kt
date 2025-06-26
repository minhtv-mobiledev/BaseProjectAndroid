package com.minhtv.base.utils

import android.content.Context

object LanguagePrefHelper {
    private const val PREF_NAME = "app_settings"
    private const val KEY_LANGUAGE_CODE = "language_code"
    const val  KEY_ALREADY_CHOOSE_LANGE = "isAlreadyChooseLanguage"

    fun saveLanguageCode(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_LANGUAGE_CODE, languageCode)
            .putBoolean(KEY_ALREADY_CHOOSE_LANGE, true)
            .apply()
    }

    fun getSavedLanguageCode(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANGUAGE_CODE, null)
    }

    fun clearLanguage(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_LANGUAGE_CODE).apply()
    }
}
