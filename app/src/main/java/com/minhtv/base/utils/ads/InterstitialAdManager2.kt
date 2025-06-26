package com.draw.animation.ads

import android.app.Activity
import android.content.Context
import android.widget.Toast
import com.draw.animation.BuildConfig
import com.draw.animation.analytics.FirebaseAnalyticRepo
import com.minhtv.base.application.MyApplication
import com.draw.animation.billing.BillingRepository
import com.draw.animation.utils.AppLogger
import com.draw.animation.utils.ads.AdModId
import com.minhtv.base.utils.remoteconfig.RemoteConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * A simple manager to preload and show Interstitial ads across the app.
 * Usage:
 * 1) In Application.onCreate():
 *      InterstitialAdManager.init(applicationContext)
 * 2) Whenever you need to display:
 *      InterstitialAdManager.showAd(this) { /* optional onDismissed */ }
 */
object InterstitialAdManager2 {
    var timeDebug = 0L
    // TODO: replace with your own Interstitial Ad Unit ID
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null

    var isLoading = false
    var isLoadFailed = false
    @Volatile
    var successFirstLoad = false

    var failedCount = 0

    /** Initialize the SDK (if needed) and start loading an ad. */
    fun init(context: Context) {
        // Make sure MobileAds is initialized before loading
        loadAd(context)
    }

    /** Load a new interstitial ad if none is loaded or loading. */
    fun loadAd(context: Context, adUnit : String = AdModId.inter_unit_splash) {
        timeDebug = System.currentTimeMillis()
        if (interstitialAd != null || isLoading) return
        isLoading = true

        val request = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            adUnit,
            request,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    successFirstLoad = true
                    isLoading = false
                    FirebaseAnalyticRepo.logInterstitialOpenLoaded()
                    AppLogger.w("InterstitialAdManager2 time diff : ${System.currentTimeMillis() - timeDebug} + process : ${System.currentTimeMillis() - MyApplication.timeProcessCreate} ms")
                    if (!BuildConfig.IS_REALEASED) {
                        Toast.makeText(context, "Load Inter open success " , Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    if (!BuildConfig.IS_REALEASED) {
                        Toast.makeText(context, "Load Inter open failed: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                    isLoading = false
                    isLoadFailed = true
                    AppLogger.e("InterstitialAdManager2 Ad failed to load: ${error.message}")
                }
            }
        )
    }

    /**
     * Show the interstitial ad if available.
     * If not loaded yet, triggers a load and returns immediately.
     * @param activity the current Activity to show the ad in
     * @param onDismissed optional callback when the ad is closed or fails to show
     */

    fun showAd(onAdNull : () -> Unit = {},onDismissed :  () -> Unit = {}) {
        showAd(activity = MyApplication.currentActivity!!,onAdNull,  onDismissed)
    }
    var lastShowInter = 0L

    fun showAd(activity: Activity,onAdNull: () -> Unit = {} , onDismissed: () -> Unit = {}) {
        if (BillingRepository.isVip.value == true) {
            onDismissed()
            return
        }
//        if (timeFirstOpenApp != 0L) {
//            if (System.currentTimeMillis() - timeFirstOpenApp < 1000 * RemoteConfig.interstitialDelayAtFirstOpenInSeconds) {
//                AppLogger.i("onDissmiss call 1")
//                return onDismissed()
//            }
//        }

        if (System.currentTimeMillis() - lastShowInter < 1000 * RemoteConfig.interstitialIntervalInSeconds) {
            return onDismissed()
        }
        val ad = interstitialAd
        if (ad == null) {
            // not ready yet; start loading for next time
            onAdNull()
//            loadAd(activity)
            onDismissed()
            return
        }

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // ad is showing
            }

            override fun onAdDismissedFullScreenContent() {
                // ad dismissed; clean up and load a new one
                interstitialAd = null
                onDismissed()
//                loadAd(activity)
                lastShowInter = System.currentTimeMillis()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // failed to show; clean up and load a new one
                interstitialAd = null
                onDismissed()
//                loadAd(activity)
            }
        }

        ad.show(activity)
    }
}
