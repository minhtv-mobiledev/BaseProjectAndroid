package com.its.baseapp.its.application

import android.app.Application
import com.its.baseapp.its.preferences.SharedPreferencesManager

open class MyApplication : Application() {

    companion object {
        private lateinit var instance: MyApplication
        lateinit var instanceSharePreference: SharedPreferencesManager
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        instanceSharePreference = SharedPreferencesManager(this)
    }

}

