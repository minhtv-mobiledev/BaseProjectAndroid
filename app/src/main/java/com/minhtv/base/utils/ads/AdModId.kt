package com.draw.animation.utils.ads

import com.draw.animation.BuildConfig

val testIdInter = "ca-app-pub-3940256099942544/1033173712"
val testIdReward = "ca-app-pub-3940256099942544/5224354917"
val testIdAppOpen = "ca-app-pub-3940256099942544/9257395921"

object AdModId {
    const val APP_ID = "ca-app-pub-6979109590044054~1488234659"
    val APP_OPEN_ID = if (BuildConfig.ENABLE_LOGGING) testIdAppOpen else "ca-app-pub-6979109590044054/7095768644"

    const val BANNER_AD_UNIT_ID = "ca-app-pub-6979109590044054/3076632589"

    const val COLLAPSE_NATIVE_UNIT_ID = "ca-app-pub-6979109590044054/5504684489"


    val INTERSTITIAL_AD_UNIT_ID = if (BuildConfig.ENABLE_LOGGING) testIdInter else   "ca-app-pub-6979109590044054/1763550912"
    val inter_unit_splash = if (BuildConfig.ENABLE_LOGGING) testIdInter else   "ca-app-pub-6979109590044054/9534207647"
    const val NATIVE_AD_OB = "ca-app-pub-6979109590044054/5814476563"
    const val NATIVE_AD_OB_FULLSCREEN = "ca-app-pub-6979109590044054/3156523636"
    const val NATIVE_AD_LIST_VIEW = "ca-app-pub-6979109590044054/9562149887"
    const val NATIVE_AD_SETTING = "ca-app-pub-6979109590044054/3242485249"

    val REWARDED_AD_UNIT_ID = if (BuildConfig.ENABLE_LOGGING) testIdReward else "ca-app-pub-6979109590044054/7243717115"

    const val BANNER_COLLAPSE_UNIT_ID = "ca-app-pub-6979109590044054/9605322426"
}