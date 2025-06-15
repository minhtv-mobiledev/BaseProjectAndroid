package com.its.baseapp.its.preferences

import com.its.baseapp.its.application.MyApplication


object SharedPreferenceUtils {
    private const val FIRST_OPEN_APP = "FIRST_OPEN_APP"
    private const val PREF_LANGUAGE_LOCAL = "PREF_LANGUAGE_LOCAL"
    private const val PREF_THEME = "PREF_THEME"
    private const val LATITUDE = "LATITUDE"
    private const val LONGITUDE = "LONGITUDE"

    private const val VALUE_PING = "VALUE_PING"
    private const val VALUE_DOWNLOAD = "VALUE_DOWNLOAD"
    private const val VALUE_UPLOAD = "VALUE_UPLOAD"

    private const val ADVERTISING_ID = "ADVERTISING_ID"
    private const val DEVICE_ID = "DEVICE_ID"
    private const val COIN_USER = "COIN_USER"

    var getUserCoin: Int
        get() = MyApplication.instanceSharePreference.getIntValue(COIN_USER, 0)
        set(value) = MyApplication.instanceSharePreference.setIntValue(COIN_USER, value)
    var firstOpenApp: Boolean
        get() = MyApplication.instanceSharePreference.getValueBool(FIRST_OPEN_APP, false)
        set(value) = MyApplication.instanceSharePreference.setValueBool(FIRST_OPEN_APP, value)
    var getAdvertisingId: String?
        get() = MyApplication.instanceSharePreference.getValue(ADVERTISING_ID, "")
        set(value) = MyApplication.instanceSharePreference.setValue(ADVERTISING_ID, value)

    var getDeviceId: String?
        get() = MyApplication.instanceSharePreference.getValue(DEVICE_ID, "")
        set(value) = MyApplication.instanceSharePreference.setValue(DEVICE_ID, value)
    var getPing: Float
        get() = MyApplication.instanceSharePreference.getValue(VALUE_PING, 0f)
        set(value) = MyApplication.instanceSharePreference.setValue(VALUE_PING, value)
    var getDownload: Float
        get() = MyApplication.instanceSharePreference.getValue(VALUE_DOWNLOAD, 0f)
        set(value) = MyApplication.instanceSharePreference.setValue(VALUE_DOWNLOAD, value)
    var getUpload: Float
        get() = MyApplication.instanceSharePreference.getValue(VALUE_UPLOAD, 0f)
        set(value) = MyApplication.instanceSharePreference.setValue(VALUE_UPLOAD, value)

    var getLatitude: Float
        get() = MyApplication.instanceSharePreference.getValue(LATITUDE, 0f)
        set(value) = MyApplication.instanceSharePreference.setValue(LATITUDE, value)

    var getLongitude: Float
        get() = MyApplication.instanceSharePreference.getValue(LONGITUDE, 0f)
        set(value) = MyApplication.instanceSharePreference.setValue(LONGITUDE, value)
    var getLocateLanguage: String?
        get() = MyApplication.instanceSharePreference.getValue(PREF_LANGUAGE_LOCAL, "en")
        set(value) = MyApplication.instanceSharePreference.setValue(PREF_LANGUAGE_LOCAL, value)

    var curTheme: Int?
        get() = MyApplication.instanceSharePreference.getIntValue(PREF_THEME)
        set(value) {
            value?.let {
                MyApplication.instanceSharePreference.setIntValue(PREF_THEME, value)
            }
        }

}