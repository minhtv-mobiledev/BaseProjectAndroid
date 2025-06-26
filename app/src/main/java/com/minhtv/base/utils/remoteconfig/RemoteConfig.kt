package com.minhtv.base.utils.remoteconfig

import android.util.Log
import com.draw.animation.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

object RemoteConfig {
    private const val TAG = "RemoteConfig"

    // Biến cache local để app dễ lấy ra
    var interstitialIntervalInSeconds: Long = 15
        private set
    var interstitialDelayAtFirstOpenInSeconds: Long = 60
        private set
    var collapseBannerDelayIntervalInSeconds = 30L
        private set
    var timeOpenAppIntervals = 10L


    var timeFirstOpenApp = 0L //lan` 2 thi van = 0

    fun setupRemoteConfig() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 giờ mới fetch 1 lần
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "RemoteConfig fetch success")
                } else {
                    Log.d(TAG, "RemoteConfig fetch failed, using cached  or default values")
                }
                applyRemoteConfigValues()
            }
    }

    private fun applyRemoteConfigValues() {
        val remoteConfig = Firebase.remoteConfig

        interstitialIntervalInSeconds = remoteConfig.getLong("wdg_interstitial_interval_in_seconds")
        interstitialDelayAtFirstOpenInSeconds = remoteConfig.getLong("wdg_interstitial_delay_at_first_open_in_seconds")
        collapseBannerDelayIntervalInSeconds = remoteConfig.getLong("wdg_collapsible_banner_interval_in_seconds")
        timeOpenAppIntervals = remoteConfig.getLong("wdg_aoa_interval_in_seconds")

        Log.d(TAG, "interstitialIntervalInSeconds = $interstitialIntervalInSeconds")
        Log.d(TAG, "interstitialDelayAtFirstOpenInSeconds = $interstitialDelayAtFirstOpenInSeconds")
    }

    var isDebug = true
    fun setNoInter() {
        interstitialIntervalInSeconds = 100000000000
    }
}
