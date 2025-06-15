package com.its.baseapp.its.ultis.device

import android.app.AppOpsManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.Settings

object DeviceSecurityUtil {

    private fun isXiaoMi(): Boolean {
        return checkManufacturer("xiaomi")
    }

    private fun isOppo(): Boolean {
        return checkManufacturer("oppo")
    }

    private fun isVivo(): Boolean {
        return checkManufacturer("vivo")
    }

    private fun checkManufacturer(manufacturer: String): Boolean {
        return manufacturer.equals(Build.MANUFACTURER, true)
    }

    fun isBackgroundStartAllowed(context: Context): Boolean {
        return when {
            isXiaoMi() -> {
                isXiaomiBgStartPermissionAllowed(context)
            }

            isVivo() -> {
                isVivoBgStartPermissionAllowed(context)
            }

            isOppo() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                Settings.canDrawOverlays(context)
            }

            else -> true
        }
    }

    fun isDeviceChina(): Boolean = when {
        isXiaoMi() -> {
            true
        }

        isVivo() -> {
            true
        }

        isOppo() -> {
            true
        }

        else -> false
    }


    private fun isXiaomiBgStartPermissionAllowed(context: Context): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        try {
            val op = 10021
            val method = ops.javaClass.getMethod(
                "checkOpNoThrow",
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            val result =
                method.invoke(ops, op, android.os.Process.myUid(), context.packageName) as Int
            return result == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun isVivoBgStartPermissionAllowed(context: Context): Boolean {
        return getVivoBgStartPermissionStatus(context) == 0
    }

    private fun getVivoBgStartPermissionStatus(context: Context): Int {
        val uri: Uri =
            Uri.parse("content://com.vivo.permissionmanager.provider.permission/start_bg_activity")
        val selection = "pkgname = ?"
        val selectionArgs = arrayOf(context.packageName)
        var state = 1
        try {
            context.contentResolver.query(uri, null, selection, selectionArgs, null)?.use {
                if (it.moveToFirst()) {
                    state = it.getInt(it.getColumnIndexOrThrow("currentstate"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return state
    }

}