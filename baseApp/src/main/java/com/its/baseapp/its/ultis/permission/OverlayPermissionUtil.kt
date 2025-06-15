package com.its.baseapp.its.ultis.permission

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import com.its.baseapp.its.ultis.device.HuaweiUtils
import com.its.baseapp.its.ultis.device.MeizuUtils
import com.its.baseapp.its.ultis.device.MiuiUtils
import com.its.baseapp.its.ultis.device.OppoUtils
import com.its.baseapp.its.ultis.device.QikuUtils
import com.its.baseapp.its.ultis.device.RomUtils

/**
 * Created by cyl on 2018/6/29.
 */
object OverlayPermissionUtil {
    fun applyOrShowFloatWindow(context: Context) {
        if (checkPermission(context)) {
        } else {
            applyPermission(context)
        }
    }

    fun checkPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 23) {
            when {
                RomUtils.checkIsMiuiRom() -> return miuiPermissionCheck(context)
                RomUtils.checkIsMeizuRom() -> return meizuPermissionCheck(context)
                RomUtils.checkIsHuaweiRom() -> return huaweiPermissionCheck(context)
                RomUtils.checkIs360Rom() -> return qikuPermissionCheck(context)
                RomUtils.checkIsOppoRom() -> return oppoROMPermissionCheck(context)
                else -> {
                }
            }
        }
        return commonROMPermissionCheck(context)
    }

    private fun huaweiPermissionCheck(context: Context): Boolean {
        return HuaweiUtils.checkFloatWindowPermission(context)
    }

    private fun miuiPermissionCheck(context: Context): Boolean {
        return MiuiUtils.checkFloatWindowPermission(context)
    }

    private fun meizuPermissionCheck(context: Context): Boolean {
        return MeizuUtils.checkFloatWindowPermission(context)
    }

    private fun qikuPermissionCheck(context: Context): Boolean {
        return QikuUtils.checkFloatWindowPermission(context)
    }

    private fun oppoROMPermissionCheck(context: Context): Boolean {
        return OppoUtils.checkFloatWindowPermission(context)
    }

    private fun commonROMPermissionCheck(context: Context): Boolean {
        return if (RomUtils.checkIsMeizuRom()) {
            meizuPermissionCheck(context)
        } else {
            var result: Boolean? = true
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    val clazz = Settings::class.java
                    val canDrawOverlays =
                        clazz.getDeclaredMethod("canDrawOverlays", Context::class.java)
                    result = canDrawOverlays.invoke(null, context) as Boolean
                } catch (e: Exception) {
                    Log.e("FloatUtil", Log.getStackTraceString(e))
                }
            }
            result!!
        }
    }

    fun applyPermission(context: Context) {
        if (Build.VERSION.SDK_INT < 23) {
            when {
                RomUtils.checkIsMiuiRom() -> MiuiUtils.applyMiuiPermission(context)
                RomUtils.checkIsMeizuRom() -> MeizuUtils.applyPermission(context)
                RomUtils.checkIsHuaweiRom() -> HuaweiUtils.applyPermission(context)
                RomUtils.checkIs360Rom() -> QikuUtils.applyPermission(context)
                RomUtils.checkIsOppoRom() -> OppoUtils.applyOppoPermission(context)
            }
        } else {
            if (RomUtils.checkIsMeizuRom()) {
                MeizuUtils.applyPermission(context)
            } else {
                commonROMPermissionApplyInternal(context)
            }
        }
    }

    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun commonROMPermissionApplyInternal(context: Context) {
        val clazz = Settings::class.java
        val field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION")

        val intent = Intent(field.get(null).toString())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = Uri.parse("package:" + context.packageName)
        context.startActivity(intent)
    }
}