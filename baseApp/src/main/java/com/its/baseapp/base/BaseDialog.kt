package com.its.baseapp.base

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import com.its.baseapp.R
import com.its.baseapp.its.preferences.SharedPreferenceUtils
import com.its.baseapp.its.ultis.other.LanguageUtil


open class BaseDialog(context: Context) : Dialog(context, R.style.Theme_Dialog) {
    init {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window!!.setGravity(Gravity.CENTER)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (SharedPreferenceUtils.getLocateLanguage?.isNotEmpty() == true){
            LanguageUtil.setLanguage()
        }
        super.onCreate(savedInstanceState)
        if (window != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
                window!!.setDecorFitsSystemWindows(false)
                val controller = window!!.insetsController
                if (controller != null) {
                    controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                    controller.systemBarsBehavior =
                        WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }
            }else{

                @Suppress("DEPRECATION")
                window!!.decorView.systemUiVisibility = hideSystemBars()
            }
        }
    }

    private fun hideSystemBars(): Int {
        return (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

}
