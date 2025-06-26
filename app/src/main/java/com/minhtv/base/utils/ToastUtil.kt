package com.draw.animation.utils

import android.graphics.Color
import android.widget.TextView
import android.widget.Toast
import com.minhtv.base.application.MyApplication

object ToastUtil {
    fun showToast(message: String) {
        val toast = Toast.makeText(MyApplication.context, message, Toast.LENGTH_SHORT)
        val view = toast.view
//        view?.background?.setTint(Color.parseColor("#99000000")) // Nền đen 60%
        view?.background?.setTint(Color.parseColor("#99000000")) // Nền đen 60%

        // Đổi màu chữ thành trắng
        val text = view?.findViewById<TextView>(android.R.id.message)
        text?.setTextColor(Color.BLACK)
        toast.show()
    }
}