package com.minhtv.base.application

// MyApplication.kt

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.webkit.WebView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.minhtv.base.activities.MainActivity
import com.draw.animation.ads.InterstitialAdManager
import com.draw.animation.ads.InterstitialAdManager2
import com.draw.animation.utils.AppLogger
import com.draw.animation.utils.ads.AdModId
import com.draw.animation.utils.ads.NativeAdManager
import com.minhtv.base.utils.remoteconfig.RemoteConfig
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.android.gms.ads.FullScreenContentCallback
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@HiltAndroidApp
class MyApplication : Application(),
    Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver {

    companion object {
        var shouldDisableAOA = false
        private var isSdkInitialized = false
        var isConsentDone = false
        var shouldToastInitAdsSDK = true
        private var shouldLoadAds = true

        private const val AD_EXPIRATION_HOURS = 4L
        private const val PREFS_NAME = "app_open_prefs"
        private const val KEY_FIRST_OPEN  = "first_open_done"
        @Volatile
        var isBillingSetUpFinished = false
        @Volatile
        var  currentActivity: MainActivity? = null
        @Volatile
        var  isCurrentMain = false
        var timeProcessCreate = 0L
        lateinit var context : MyApplication
        val testMediaId = "ca-app-pub-3940256099942544/1044960115"

        var flagUIDefault : Int? = null

        fun setStatusBarColor(color : Int) {
            currentActivity?.apply {
                fakeStatusBar.setBackgroundColor(color)
            }
        }
        fun setStatusBarColorResId(resId : Int) {
            setStatusBarColor(currentActivity!!.getColor(resId))
        }
        fun setNavigationBarColor(color : Int) {
            currentActivity?.apply {
                fakeNavBar.setBackgroundColor(color)
            }
        }
        fun hideNavBar(activity: MainActivity? = currentActivity) {
            uiDefault()
            activity?.apply {
                window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                this.apply {
                    fakeNavBar.isVisible = false
                    fakeStatusBar.isVisible = true
                }
            }
        }
        fun hideBoth(activity: MainActivity? = currentActivity) {
            activity?.apply {
                window.decorView.systemUiVisibility =
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                this.apply {
                    fakeNavBar.isVisible = false
                    fakeStatusBar.isVisible = false
                }
            }
        }
        fun hideStatusBar(activity: MainActivity? = currentActivity) {
            activity?.let{
                it.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                it.window.decorView.systemUiVisibility =
                    it.window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION.inv()

                it.apply {
                    fakeStatusBar.isVisible = false
                    fakeNavBar.isVisible = true
                }
            }
        }
        fun uiDefault() {
            currentActivity?.apply {
                window.decorView.systemUiVisibility = flagUIDefault!! // This makes sure the system UI is visible again
                this.apply {
                    fakeNavBar.isVisible = true
                    fakeStatusBar.isVisible = true
                }
            }
        }
    }
    fun isMainProcess(context: Context = this): Boolean {
        val pid = android.os.Process.myPid()
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processName = manager.runningAppProcesses?.firstOrNull { it.pid == pid }?.processName
        return processName == context.packageName
    }
    private lateinit var prefs: SharedPreferences
    private lateinit var billListener: BillingClientStateListener
    lateinit var appOpenAdManager: AppOpenAdManager
    var failedCount = 0
    override fun onCreate() {
        super<Application>.onCreate()
        if (timeProcessCreate == 0L) timeProcessCreate = System.currentTimeMillis()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = Application.getProcessName()
            if (applicationContext.packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
                return
            }
        }
        context = this
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        AppLogger.i("MyApplication onCreate called")


        // init mobile ads
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        if (!isSdkInitialized) {
            isSdkInitialized = true
            MobileAds.initialize(this@MyApplication) { initializationStatus ->
                if (!shouldLoadAds) {
                    return@initialize
                }
                shouldLoadAds = false
                backgroundScope.launch {
//                    if (!BillingRepository.isVip.value!!) {
                        withContext(Dispatchers.Main) {
                            // SDK đã được khởi tạo, có thể load ad
                            InterstitialAdManager2.loadAd(context)
                            if (isMainProcess()) {
                                RemoteConfig.setupRemoteConfig()
                            }
//                            while (!(InterstitialAdManager2.successFirstLoad || InterstitialAdManager2.isLoadFailed) && !WelcomeScreenFragment.isWelcomeDestroy) {
//                                AppLogger.i("MyApplication wait for interstitial ad to load")
//                                delay(200)
//                            }
                            NativeAdManager.instanceHomeScreenContent.preloadAds(context, preloadCount = 1, adUnitId = AdModId.NATIVE_AD_LIST_VIEW)
                            NativeAdManager.instanceLanguageOrSetting.preloadAds(context, preloadCount = 1, adUnitId = AdModId.NATIVE_AD_SETTING)
                            InterstitialAdManager.loadAd(context)

                            NativeAdManager.instanceCollapseTemplate.apply {
                                preloadAds(context, preloadCount = 1)
                                failedCountLimit = 5
                            }

                            val showOB = PreferenceManager.getDefaultSharedPreferences(context)
                                .getBoolean("shouldShowOnBoardingAgain", true)
                            if (showOB) {
                                NativeAdManager.instanceFullscreenOB.preloadAds(context, preloadCount = 2, adUnitId = AdModId.NATIVE_AD_OB_FULLSCREEN)
                            }
                            NativeAdManager.instanceBottomOB.preloadAds(context,  preloadCount = if (showOB) 3 else 1, adUnitId = AdModId.NATIVE_AD_OB)
                        }
//                    }
                }
            }

        }


        // 1. Đăng ký lifecycle callbacks để theo dõi activity hiện tại
        registerActivityLifecycleCallbacks(this)
        // 2. Khởi tạo Mobile Ads SDK trên background thread

        // 3. Đăng ký observer để lắng nghe app về foreground
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

        // 4. Khởi tạo manager
        appOpenAdManager = AppOpenAdManager()
    }

//    fun handleBilling() {
//        billListener = object : BillingClientStateListener {
//            override fun onBillingSetupFinished(billingResult: BillingResult) {
//                AppLogger.i("Here come billing setup finished ${billingResult.responseCode}")
//                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
//                    isBillingSetUpFinished = true
//                    // The BillingClient is ready. You can query purchases here.
//                    if (true) BillingRepository.isInAppProductPurchased(productId = BillingRepository.ID_LIFETIME_INAPP) { isPurchased ->
//                        if (isPurchased) {
//                            BillingRepository.isVip.postValue(true)
//                            return@isInAppProductPurchased
//                        }
//                    }
//                    BillingRepository.checkIfUserAlreadyPurchased(
//                        BillingRepository.getBillingClient()!!,
//                        BillingRepository.ID_MONTHLY_SUSCRIPTION) {
//                        isBillingSetUpFinished = true
//                        if (it) {
//                            BillingRepository.isVip.postValue(true)
//                            return@checkIfUserAlreadyPurchased
//                        }
//                    }
//                }
//            }
//
//            override fun onBillingServiceDisconnected() {
//                AppLogger.i("Here come billing service disconnected")
//                // Try to restart the connection on the next request to
//                // Google Play by calling the startConnection() method.
//                failedCount++
//                if (failedCount < 3 ) {
//                    CoroutineScope(Dispatchers.IO).launch {
//                        delay(500)
//                        BillingRepository.getBillingClient()?.startConnection(billListener)
//                    }
//                } else {
//                    isBillingSetUpFinished =  true
//                }
//            }
//        }
//        BillingRepository.getBillingClient()?.startConnection(billListener)
//    }
    /** Khi app chuyển từ background -> foreground */
    override fun onStart(owner: LifecycleOwner) {
        AppLogger.i("MyApplication onStart")
        // Show ad nếu có sẵn
        val firstDone = prefs.getBoolean(KEY_FIRST_OPEN, false)
        if (!firstDone) {
            // lần đầu: đánh dấu, chỉ tải trước (load) mà không show
            RemoteConfig.timeFirstOpenApp = System.currentTimeMillis()
            prefs.edit().putBoolean(KEY_FIRST_OPEN, true).apply()
//            appOpenAdManager.loadAd()
        } else {
            // từ lần 2 trở đi: show ngay nếu có
            AppLogger.i("MyApplication show ad if available is activity null: ${currentActivity == null}")
//            CoroutineScope(Dispatchers.Main).launch {
//                while (!isBillingSetUpFinished) {
//                    AppLogger.i("MyApplication wait for billing setup finished")
//                    delay(500)
//                }
//                if (GlobalInstance.isJustUseGif) {
//                    GlobalInstance.isJustUseGif = false
//                    return@launch
//                }
//                if (!BillingRepository.isVip.value!!) {
//                    if (System.currentTimeMillis() - lastAdOpenApp < timeOpenAppIntervals * 1000) {
//                        return@launch
//                    }
//                    if (GlobalInstance.shouldDelayShowAOA) {
//                        GlobalInstance.shouldDelayShowAOA = false
//                        return@launch
//                    }
//                    currentActivity?.let {
//                        appOpenAdManager.showAdIfAvailable(it)
//                    }
//                }
//
//
//            }
        }
    }
    var lastAdOpenApp = 0L
    var timeOpenAppIntervals = RemoteConfig.timeOpenAppIntervals

    ////////// ActivityLifecycleCallbacks //////////
    override fun onActivityStarted(activity: Activity) {
        if (activity is MainActivity) {
            flagUIDefault = flagUIDefault ?: activity.window.decorView.systemUiVisibility
            currentActivity = activity
            AppLogger.i("MyApplication onActivityStarted: ${activity.javaClass.simpleName} " +
                    "flagUIDefault: $flagUIDefault")
        }
        isCurrentMain = activity is MainActivity
    }
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (activity == currentActivity) {
            currentActivity = null
        }
    }
    var appOpenAd: AppOpenAd? = null
    var loadTime: Long = 0
    var isShowingAd = false
    var pendingShow = false
    var onDisMissCallback = {

    }
    ////////// AppOpenAdManager //////////
    inner class AppOpenAdManager {
        /** Tải ad mới */
        fun loadAd() {
            // Nếu ad đang load hoặc đã load, không load lại
            if (isAdAvailable()) return


            val request = AdRequest.Builder().build()
//            AppOpenAd.load(
//                this@MyApplication,
//                AdModId.APP_OPEN_ID,
//                request,
//                object : AppOpenAdLoadCallback() {
//                    override fun onAdLoaded(ad: AppOpenAd) {
//                        AppLogger.i("MyApplication ad loaded")
//                        appOpenAd = ad
//                        loadTime = Date().time
//                        if (pendingShow) {
//                            pendingShow = false
//                            if (GlobalInstance.firstOpenApp) {
//                                GlobalInstance.firstOpenApp = false
//                            } else {
//                                currentActivity?.let {
//                                    showAdIfAvailable(it)
//                                }
//                            }
//                        }
//                    }
//                    override fun onAdFailedToLoad(error: LoadAdError) {
//                        AppLogger.i("MyApplication ad loaded error: ${error.message}")
//                        // Log hoặc xử lý lỗi load
//                        // Retry logic (Exponential backoff)
////                        if (failedCount < 5) {  // Retry a maximum of 3 times
////                            failedCount++
////                            val retryDelay = 1000 * (1 + failedCount) * (1 + failedCount)  // Increase delay for each retry
////                            Handler(Looper.getMainLooper()).postDelayed({
////                                InterstitialAdManager.loadAd(context)
////                            }, retryDelay.toLong())
////                        }
//                    }
//                }
//            )
        }

        /** Kiểm tra ad còn hiệu lực (load < 4h trước) */
        private fun isAdAvailable(): Boolean {
            val age = Date().time - loadTime
            return appOpenAd != null && age < AD_EXPIRATION_HOURS * 3600_000
        }

        /** Hiển thị ad nếu có sẵn, ngược lại sẽ load và callback ngay */
        fun showAdIfAvailable(activity: Activity) {
            AppLogger.i("Myapplication show ad if available  isShowingAd: $isShowingAd" +
                    " isAdAvailable: ${isAdAvailable()}  isAppnull: ${appOpenAd == null}")
            if (isShowingAd) return
            if (!isAdAvailable()) {
                pendingShow = true
                loadAd()
                return
            }
            if (appOpenAd == null) {
                AppLogger.i("MyApplication appOpenAd is null")
                return
            }
            if (System.currentTimeMillis() - timeAppStop < RemoteConfig.timeOpenAppIntervals){
                return
            }
            if (shouldDisableAOA) {
                return
            }
            if (!isCurrentMain) {
                return
            }

            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                    AppLogger.i("onAdShowedFullScreenContent")
                    isShowingAd = true
                }
                override fun onAdDismissedFullScreenContent() {
                    AppLogger.i("onAdDismissedFullScreenContent")
                    isShowingAd = false
                    appOpenAd = null
                    // Tự load ad tiếp
                    loadAd()
                }
                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    AppLogger.i("onAdFailedToShowFullScreenContent: ${adError.message}")
                    isShowingAd = false
                    appOpenAd = null
                    loadAd()
                }
            }
            appOpenAd?.show(activity)
            lastAdOpenApp = System.currentTimeMillis()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        timeAppStop = System.currentTimeMillis()
    }
    var timeAppStop = 0L
}
