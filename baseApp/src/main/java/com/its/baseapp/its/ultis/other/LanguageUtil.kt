package com.its.baseapp.its.ultis.other

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.its.baseapp.its.preferences.SharedPreferenceUtils
import java.util.Locale

object LanguageUtil {
    fun setLanguage(context: Context?) {
        if (context == null) return
        var language = SharedPreferenceUtils.getLocateLanguage
        Log.d("TAG", "setLanguage: $language")
        if (language != null) {
            if (language.isEmpty()) {
                language = Locale.getDefault().language
            }
        }
        val newLocale = Locale(language?.lowercase(Locale.getDefault()) ?: "en")
        Locale.setDefault(newLocale)
        val res = context.resources
        val conf = res.configuration
        conf.setLocale(newLocale)
        res.updateConfiguration(conf, res.displayMetrics)
    }
    fun setLanguage() {
        val language = SharedPreferenceUtils.getLocateLanguage
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(
                language
            )
        )
    }
    fun getLanguageName(context: Context?): String? {
        return try {
            if (context == null) return ""
            var language = SharedPreferenceUtils.getLocateLanguage
            if (language != null) {
                if (language.isEmpty()) {
                    language = Locale.getDefault().language
                }
            }
            val newLocale = Locale(language?.lowercase(Locale.getDefault()) ?: "en")
            Locale.setDefault(newLocale)
            newLocale.displayName + ""
        } catch (e: Exception) {
            ""
        }
    }
}