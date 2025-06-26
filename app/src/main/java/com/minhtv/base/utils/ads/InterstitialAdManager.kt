package com.draw.animation.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.draw.animation.R
import com.minhtv.base.application.MyApplication
import com.draw.animation.billing.BillingRepository
import com.draw.animation.dialog.LoadingFullscreenDialogFragment
import com.draw.animation.utils.AppLogger
import com.draw.animation.utils.ads.AdModId
import com.minhtv.base.utils.remoteconfig.RemoteConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A simple manager to preload and show Interstitial ads across the app.
 * Usage:
 * 1) In Application.onCreate():
 *      InterstitialAdManager.init(applicationContext)
 * 2) Whenever you need to display:
 *      InterstitialAdManager.showAd(this) { /* optional onDismissed */ }
 */
object InterstitialAdManager {
    // TODO: replace with your own Interstitial Ad Unit ID
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    private var interstitialAd: InterstitialAd? = null
    private var isLoading = false
    var successFirstLoad = false

    var failedCount = 0

    /** Initialize the SDK (if needed) and start loading an ad. */
    fun init(context: Context) {
        // Make sure MobileAds is initialized before loading
        loadAd(context)
    }

    /** Load a new interstitial ad if none is loaded or loading. */
    fun loadAd(context: Context, adUnit : String = AdModId.INTERSTITIAL_AD_UNIT_ID) {
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
                    failedCount = 0
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    return
                    AppLogger.e("InterstitialAdManager Ad failed to load: ${error.message}")
                    if (adUnit != AdModId.INTERSTITIAL_AD_UNIT_ID) {
                        return
                    }
                    // Log or handle the error as needed
                    interstitialAd = null
                    isLoading = false
                    // Retry logic (Exponential backoff)
                    if (failedCount < 3) {  // Retry a maximum of 3 times
                        failedCount++
                        if (failedCount >5) failedCount = 5
                        val retryDelay = 1000 * (1 + failedCount) * (1 + failedCount)  // Increase delay for each retry
                        Handler(Looper.getMainLooper()).postDelayed({
                            loadAd(context)
                        }, retryDelay.toLong())
                    }
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
        val dialog = LoadingFullscreenDialogFragment(R.raw.loading_ads,)
        dialog.isCancelable = false
        if (BillingRepository.isVip.value == true) {
            onDismissed()
            return
        }
//        if (timeFirstOpenApp != 0L) {
//            if (System.currentTimeMillis() - timeFirstOpenApp < 1000 * RemoteConfig.interstitialDelayAtFirstOpenInSeconds) {
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
            loadAd(activity)
            onDismissed()
            return
        }
        val onDismissed = {
            if (!dialog.isStateSaved) {
                dialog.dismissAllowingStateLoss()
                onDismissed()
            } else {
                CoroutineScope(Dispatchers.Main).launch {
                    while (dialog.isStateSaved) {
                        delay(50)
                    }
                    dialog.dismissAllowingStateLoss()
                    onDismissed()
                    delay(500)
                    if (dialog.isVisible) {
                        while (dialog.isStateSaved) {
                            delay(50)
                        }
                        try {
                            dialog.dismissAllowingStateLoss()
                        } catch (e: Exception) {
                            Log.e("InterstitialAdManager", "Error dismissing dialog: ${e.message}")
                        }
                    }
                }
            }
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // ad is showing
            }

            override fun onAdDismissedFullScreenContent() {
                // ad dismissed; clean up and load a new one
                interstitialAd = null
                onDismissed()
                loadAd(activity)
                lastShowInter = System.currentTimeMillis()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                // failed to show; clean up and load a new one
                interstitialAd = null
                onDismissed()
                loadAd(activity)
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            dialog.show(MyApplication.currentActivity!!.supportFragmentManager, "loading")
            delay(1500)
            ad.show(activity)
        }
    }
}
