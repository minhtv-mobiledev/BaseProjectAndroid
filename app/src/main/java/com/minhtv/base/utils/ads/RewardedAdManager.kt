// RewardedAdManager.kt
package com.draw.animation.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.draw.animation.BuildConfig
import com.draw.animation.R
import com.minhtv.base.application.MyApplication
import com.draw.animation.utils.ads.AdModId
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.OnUserEarnedRewardListener


object RewardedAdManager {
    private val AD_UNIT_ID = if (BuildConfig.ENABLE_LOGGING) "ca-app-pub-3940256099942544/5224354917" else AdModId.REWARDED_AD_UNIT_ID  // thay bằng của bạn
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false
    var failedCount = 0
    /** Khởi tạo SDK & load ngay một ad */
    fun init(context: Context) {
        loadAd(context)
    }

    /** Load mới nếu chưa có ad hoặc đã hết hạn */
    fun loadAd(context: Context) {
        if (rewardedAd != null || isLoading) return

        isLoading = true
        val request = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            AD_UNIT_ID,
            request,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isLoading = false
                    failedCount = 0
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    return
                    // log hoặc retry với backoff nếu muốn
                    rewardedAd = null
                    isLoading = false
                    // Retry logic (Exponential backoff)
                    if (failedCount < 999) {  // Retry a maximum of 3 times
                        failedCount++
                        if (failedCount  > 5) failedCount = 5
                        val retryDelay = 1000 * (1 + failedCount) * (1 + failedCount)  // Increase delay for each retry
                        Handler(Looper.getMainLooper()).postDelayed({
                            InterstitialAdManager.loadAd(context)
                        }, retryDelay.toLong())
                    }
                }
            }
        )
    }

    /**
     * Show ad nếu có sẵn,
     * - onEarned: callback khi user xem đủ và nhận reward
     * - onDismissed: callback khi ad đóng lại (thường dùng để load lại)
     */
    fun showRewardedAd(
        activity: Activity,
        onEarned: (RewardItem) -> Unit = {},
        onDismissed: () -> Unit = {}, dialogFragment: DialogFragment? = null
    ) {
        val ad = rewardedAd
        if (ad == null) {
            // chưa có ad, load trước và báo cho caller
            Toast.makeText(activity, activity.getString(R.string.rewarded_not_available), Toast.LENGTH_LONG).show()
            loadAd(activity)
            return
        }
        dialogFragment?.dismiss()

        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                // đã show
                MyApplication.shouldDisableAOA = true
            }
            override fun onAdDismissedFullScreenContent() {
                rewardedAd = null
                onDismissed()
                loadAd(activity)
                MyApplication.shouldDisableAOA = false
            }
            override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                rewardedAd = null
                onDismissed()
                loadAd(activity)
            }
        }

        ad.show(activity, OnUserEarnedRewardListener { reward: RewardItem ->
            onEarned(reward)
        })
    }
}
