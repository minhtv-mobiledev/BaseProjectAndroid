package com.its.baseapp.its.ultis.theme

import android.content.Context
import com.its.baseapp.R
import com.its.baseapp.its.constant.DARK_THEME
import com.its.baseapp.its.constant.LIGHT_THEME
import com.its.baseapp.its.preferences.SharedPreferenceUtils

object ThemeUtil {
    fun getTheme(): Int {
        val curTheme: Int? = SharedPreferenceUtils.curTheme
        when (curTheme) {
            DARK_THEME -> return R.style.Theme_ProjectBase_Dark
            LIGHT_THEME -> return R.style.Theme_ProjectBase_Light
        }
        return R.style.Theme_ProjectBase_Light
    }

    fun getResColor(context: Context, attr: Int): Int {
        var intColor = 0
        try {
            val themeId = getTheme()
            val a = context.theme.obtainStyledAttributes(themeId, intArrayOf(attr))
            intColor = a.getColor(0, 0)
            a.recycle()
            val hexColor = Integer.toHexString(intColor)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return intColor
    }

    fun getHexColor(context: Context, attr: Int): String {
        var hexColor = "#FFFFFF"
        try {
            val themeId = getTheme()
            val a = context.theme.obtainStyledAttributes(themeId, intArrayOf(attr))
            hexColor = Integer.toHexString(a.getColor(0, 0))
            a.recycle()
            return hexColor
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return hexColor
    }
}