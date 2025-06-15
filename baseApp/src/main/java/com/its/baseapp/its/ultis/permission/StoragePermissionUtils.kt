package com.its.baseapp.its.ultis.permission

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.result.ActivityResultLauncher

/**
 *  this class is used for check/ request device shared-storage permission on all android version
 **/
object StoragePermissionUtils {
    fun isReadStorageGranted(context: Context): Boolean {
        // android 13 or higher
        return if (VersionAndroid.isApi33orHigher()) {
            PermissionUtils.isPermissionGrandted(
                context,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
            )

        } else {
            //below android 13
            PermissionUtils.isPermissionGrandted(
                context,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    fun requestReadStoragePermission(resultLauncher: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // android 13 or higher
            PermissionUtils.requestPermission(
                (Manifest.permission.READ_MEDIA_IMAGES),
                resultLauncher
            )
        } else {
            //below android 13
            PermissionUtils.requestPermission(
                (Manifest.permission.READ_EXTERNAL_STORAGE),
                resultLauncher
            )
        }
    }

    fun requestReadStorageManagerPermission(resultLauncher: ActivityResultLauncher<String>) {
        PermissionUtils.requestPermission(
            (Manifest.permission.MANAGE_EXTERNAL_STORAGE),
            resultLauncher
        )
    }

    fun requestCameraPermission(resultLauncher: ActivityResultLauncher<Array<String>>) {
        PermissionUtils.requestMultiplePermission(
            arrayOf(Manifest.permission.CAMERA),
            resultLauncher
        )
    }
}