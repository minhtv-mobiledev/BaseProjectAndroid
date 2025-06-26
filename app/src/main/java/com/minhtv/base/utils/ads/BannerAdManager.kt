package com.draw.animation.utils.ads

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.minhtv.base.application.MyApplication
import com.draw.animation.billing.BillingRepository
import com.draw.animation.utils.AppLogger
import com.minhtv.base.utils.remoteconfig.RemoteConfig.collapseBannerDelayIntervalInSeconds
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class BannerAdManager {
    companion object{
        var lastTimeShowAdCollapse = 0L
        val instanceCollapse : BannerAdManager by lazy { BannerAdManager() }
        val testId = "ca-app-pub-3940256099942544/2014213617"

    }

    var adView: AdView? = null
    private var isAdVisible = false
    var failedCount = 0
    /**
     * Initializes the Banner AdManager.
     * @param activity The current activity to show the ad in
     * @param adUnitId The Ad Unit ID for the banner ad
     */
    fun init(activity: Context, viewGroup: ViewGroup, banner : AdSize = AdSize.BANNER, isCollapsible: Boolean = true, adUnitId : String = AdModId.BANNER_COLLAPSE_UNIT_ID, onAdClose: () -> Unit = {}) {
        viewGroup.visibility = View.VISIBLE

        if (BillingRepository.isVip.value!!) {
            viewGroup.isVisible = false
            return
        }

        adView = AdView(activity)
        // 3. Tạo Bundle extras để bật tính năng collapsible
        adView?.adUnitId = adUnitId
//        adView?.setAdSize(banner)
        viewGroup.post {
            val containerWidthPx = viewGroup.width
            val density = activity.resources.displayMetrics.density
            val widthDp = (containerWidthPx / density).toInt()
            val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, widthDp)
//                AppLogger.i("Ad size123 $ : ${adSize.width}, ${adSize.height}")
//                AppLogger.i("Ad size123 vg : ${viewGroup.width}, ${viewGroup.height}")

            adView?.setAdSize(adSize)

//            if (isCollapsible) {
//                adView?.setAdSize(adSize)
//            } else {
//                AppLogger.i("Ad size123 : ${viewGroup.width}, ${viewGroup.height}")
////                adView?.layoutParams = ViewGroup.LayoutParams(viewGroup.width, viewGroup.height)
//                adView?.setAdSize(AdSize(viewGroup.width, viewGroup.height))
//            }
            var isCollapsible = isCollapsible
            if (isCollapsible) {
                isCollapsible = System.currentTimeMillis() - lastTimeShowAdCollapse > collapseBannerDelayIntervalInSeconds * 1000
            }
            //always false
            isCollapsible = false
            loadAd(activity, viewGroup, isCollapsible, onAdClose)
        }
    }

    /**
     * Load the banner ad.
     * @param activity The activity context for the ad to be loaded into
     */
    private fun loadAd(activity: Context, viewGroup: ViewGroup, isCollapsible: Boolean, onAdClose: () -> Unit) {

        val extras = Bundle().apply {
            // "bottom" để bottom của expanded banner align với bottom của collapsed banner.
            // Hoặc "top" nếu muốn expand từ trên.
            putString("collapsible", "bottom")
        }
        val adRequest = AdRequest.Builder()
            .apply {
                if (isCollapsible) addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
            }
            .build()
        adView?.loadAd(adRequest)

        adView?.setAdListener(object : com.google.android.gms.ads.AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                // Ad loaded successfully, show it
                showAd(activity, viewGroup)
                if (isCollapsible) {
                    lastTimeShowAdCollapse = System.currentTimeMillis()
                }
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                // Handle ad loading failure
                if (failedCount < 5) {  // Retry a maximum of 3 times
                    failedCount++
                    if (failedCount > 5) failedCount = 5
                    val retryDelay = 1000 * (1 + failedCount) * (1 + failedCount)  // Increase delay for each retry
                    Handler(Looper.getMainLooper()).postDelayed({
                        loadAd(activity, viewGroup, isCollapsible, onAdClose)
                    }, retryDelay.toLong())
                }
            }

            override fun onAdClosed() {
                super.onAdClosed()
                // Handle ad close action (if any specific action needed)
                AppLogger.i("Banner ad closed")
                onAdClose()
            }
        })
    }

    /**
     * Show the banner ad.
     * @param activity The activity context where the ad will be shown
     */
    fun showAd(activity: Context, viewGroup: ViewGroup? = null) {
        adView?.visibility = View.VISIBLE
        viewGroup?.isVisible = true
        viewGroup?.removeAllViews()
        viewGroup?.addView(adView)
        isAdVisible = true
    }

    /**
     * Hide the banner ad.
     */
    fun hideAd() {
        adView?.visibility = View.GONE
        isAdVisible = false
    }

    /**
     * Toggle the visibility of the banner ad (show/hide).
     * This can be used for the collapsible ad behavior.
     */
    fun toggleAdVisibility() {
        if (BillingRepository.isVip.value!!) {
            return
        }
        if (isAdVisible) {
            hideAd()
        } else {
            showAd(activity = MyApplication.currentActivity!!)
        }
    }
}
