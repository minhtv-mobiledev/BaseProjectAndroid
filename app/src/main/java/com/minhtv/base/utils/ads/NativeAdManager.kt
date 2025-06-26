package com.draw.animation.utils.ads

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.draw.animation.BuildConfig
import com.draw.animation.R
import com.minhtv.base.application.MyApplication
import com.draw.animation.billing.BillingRepository
import com.draw.animation.models.NativeAdWrapper
import com.draw.animation.utils.AppLogger
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class NativeAdManager  constructor(){
    var viewLifecycleOwner : LifecycleOwner? = null
    var isFromAnimationSetting = false
    var failedCount = 0
    var failedCountLimit = 0
    var shouldPauseLoadFailed = false
    var shouldAddAtBottom = false

    // Queue to hold loaded ads for reuse
    var nativeAd : NativeAd? = null

    companion object {
        // Singleton access
        val instanceFullscreenOB: NativeAdManager by lazy { NativeAdManager() } //preload 2
        val instanceHomeScreenContent = NativeAdManager()
        val instanceBottomOB: NativeAdManager by lazy { NativeAdManager() } //preload 2
        val instanceCollapseTemplate: NativeAdManager by lazy { NativeAdManager() }
        val instanceLanguageOrSetting : NativeAdManager by lazy { NativeAdManager() }
//        val instanceSetting : NativeAdManager by lazy { NativeAdManager() }
    }
//    val adQueue: Queue<NativeAd> = LinkedList()
    val listNativeAdWrapper: MutableList<NativeAdWrapper> = mutableListOf()

    /**
     * Preload a number of native ads into the queue.
     */
    var isCollapsible = false
    fun preloadAds(context: Context = MyApplication.currentActivity!!,
                   adUnitId: String = "ca-app-pub-3940256099942544/2247696110", preloadCount : Int = 1) {
        var adUnitId =
            if (this == instanceCollapseTemplate) AdModId.COLLAPSE_NATIVE_UNIT_ID else
            adUnitId
        if (BuildConfig.ENABLE_LOGGING) {
            adUnitId = testNativeId
        }
        if (BillingRepository.isVip.value == true) return
        CoroutineScope(Dispatchers.Main).launch {
            repeat(preloadCount) {
                val loader = createAdLoader(context, adUnitId) { ad, id ->
                    listNativeAdWrapper.add(
                        NativeAdWrapper(id = id, nativeAd = ad)
                    )
                }
                loader.loadAd(AdRequest.Builder().build())
                delay(800L) // Delay 800ms giữa các lần preload (có thể chỉnh tùy tình hình)
            }
        }
    }
    fun createShimmerPlacereHolder(context: Context, resIdLayout: Int) : ShimmerFrameLayout {
        val shimmer = Shimmer.AlphaHighlightBuilder()
            .setBaseAlpha(0.5f)           // view gốc chỉ sáng 70%
            .setHighlightAlpha(0.4f)      // highlight trong suốt 30%
            .setDuration(1200L)
            .setDirection(Shimmer.Direction.TOP_TO_BOTTOM)
            .setTilt(20f)                 // nghiêng 20° cho dễ thấy
            .build()


        // 2. Tạo ShimmerFrameLayout và gán shimmer config
        val shimmerFrame = ShimmerFrameLayout(context).apply {
            setShimmer(shimmer)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Nếu muốn tự start/stop:
        }
        val inflater = LayoutInflater.from(context)
        val placeholderView = inflater.inflate(R.layout.ad_shimmer, shimmerFrame, false) as ViewGroup
        placeholderView.apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundResource(R.drawable.background_shimer_border_ob1_native_ad)
        }

        // 4. Add vào và trả về
        shimmerFrame.addView(placeholderView)
        shimmerFrame.startShimmer()
        return shimmerFrame
    }
    /**
     * Display next ad from queue into the provided layout, or load if queue is empty.
     */
    fun isAllAdAvailable(): Boolean {
        return instanceBottomOB.isAdAvailable() || instanceFullscreenOB.isAdAvailable() || instanceCollapseTemplate.isAdAvailable()
                 || instanceLanguageOrSetting.isAdAvailable()
    }
    fun isAdAvailable(): Boolean {
        return listNativeAdWrapper.any { !it.hasImpression }
    }
    val testNativeId = "ca-app-pub-3940256099942544/1044960115"
    fun showAdOrLoad(context: Context = MyApplication.currentActivity!!,
                     resIdLayout: Int,
                     adViewLayout: ViewGroup,
                     adUnitId: String = "ca-app-pub-3940256099942544/2247696110", preloadCountAgain: Int = 1, isCollapse: Boolean = false, viewLifecycleOwner: LifecycleOwner? = null, onGetAd : (NativeAd) -> Unit = {}) {
//        AppUtils.attachAutoVideoControlToNativeAdView(MyApplication.currentActivity!!, adViewLayout)

        if (BillingRepository.isVip.value == true) {
            adViewLayout.isVisible = false
            return
        }
        var adUnitId = if (this == instanceCollapseTemplate) AdModId.COLLAPSE_NATIVE_UNIT_ID else adUnitId
        if (BuildConfig.ENABLE_LOGGING) {
            adUnitId = testNativeId
        }
        adViewLayout.isVisible = !isCollapse

        if (!isCollapse) {
            val shimmerFrame = createShimmerPlacereHolder(context, resIdLayout)
            adViewLayout.removeAllViews()
            adViewLayout.addView(shimmerFrame)
        }
        val firstNativeExpression = if (this == instanceCollapseTemplate) {
            findAdInAllPlace()
        } else {
            listNativeAdWrapper.firstOrNull {
                !it.hasImpression
            }
        }
//        listNativeAdWrapper.removeAll { it.hasImpression }

        if (firstNativeExpression != null) {
            AppLogger.i("Native preload first expression")
            inflateAndPopulate(resIdLayout, adViewLayout,viewLifecycleOwner ,firstNativeExpression.nativeAd)
            onGetAd(firstNativeExpression.nativeAd)
        } else { // case load nếu ko co data???
            val loader = createAdLoader(context, adUnitId) { ad , id ->
                inflateAndPopulate(resIdLayout, adViewLayout,viewLifecycleOwner ,ad)
                onGetAd(ad)
                listNativeAdWrapper.add(NativeAdWrapper(ad , id))
            }
            loader.loadAd(AdRequest.Builder().build())
        }
        return
    }
    var isAdLoading = true      //dung dc trong case  chi preloads 1 lan
    fun findAdInAllPlace() : NativeAdWrapper? { //should be using in collapse instance
        return instanceFullscreenOB.listNativeAdWrapper.firstOrNull { !it.hasImpression }
            ?: instanceBottomOB.listNativeAdWrapper.firstOrNull { !it.hasImpression }
            ?: instanceLanguageOrSetting.listNativeAdWrapper.firstOrNull { !it.hasImpression }
            ?: instanceCollapseTemplate.listNativeAdWrapper.firstOrNull { !it.hasImpression }
    }

    private fun createAdLoader(context: Context,
                               adUnitId: String,
                               loadCount : Int = 0,
                               onLoaded: (NativeAd, Int) -> Unit): AdLoader {
        isAdLoading = true
        val nativeAdOptions: NativeAdOptions = NativeAdOptions.Builder()
            .setAdChoicesPlacement(if (!isCollapsible) NativeAdOptions.ADCHOICES_BOTTOM_RIGHT else NativeAdOptions.ADCHOICES_TOP_RIGHT)
            .build()
        val adId = Random.nextInt()
        return AdLoader.Builder(context, adUnitId)
            .withNativeAdOptions(nativeAdOptions)
            .forNativeAd { nativeAd ->
                onLoaded(nativeAd, adId)
                isAdLoading = false
                this.nativeAd = nativeAd
                failedCount = 0
            }
            .withAdListener(object : AdListener() {
                override fun onAdImpression() {
                    super.onAdImpression()
                    val nativeWrapper = listNativeAdWrapper.firstOrNull{
                        it.id == adId
                    }
                    if (nativeWrapper == null) {
                        findAdWrapperAllPlace(adId)?.hasImpression = true
                    } else {
                        nativeWrapper.hasImpression = true
                        AppLogger.i("Native ad impression recorded, id: $adId")
                    }
                    if (this@NativeAdManager == instanceCollapseTemplate && !isAllAdAvailable()) {
                        preloadAds(context, adUnitId, preloadCount = 1)
                    }
                }
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    AppLogger.i("Native ad failed to load: ${adError.message}")
                    return
                }
            })
            .build()
    }
    lateinit var onTVClick : () -> Unit
    var onLoadFailed = {
    }
    fun getNativeWrapper(nativeAd: NativeAd): NativeAdWrapper? {
        return listNativeAdWrapper.firstOrNull { it.nativeAd == nativeAd }
    }
    fun getNativeWrapper(nativeAdId: Int): NativeAdWrapper? {
        return listNativeAdWrapper.firstOrNull { it.id == nativeAdId }
    }
    fun findAdWrapperAllPlace(nativeAd: NativeAd) : NativeAdWrapper? {
        return instanceFullscreenOB.getNativeWrapper(nativeAd)
            ?: instanceBottomOB.getNativeWrapper(nativeAd)
            ?: instanceLanguageOrSetting.getNativeWrapper(nativeAd)
            ?: instanceCollapseTemplate.getNativeWrapper(nativeAd)
    }
    fun findAdWrapperAllPlace(nativeAdId: Int) : NativeAdWrapper? {
        return instanceFullscreenOB.getNativeWrapper(nativeAdId)
            ?: instanceBottomOB.getNativeWrapper(nativeAdId)
            ?: instanceLanguageOrSetting.getNativeWrapper(nativeAdId)
            ?: instanceCollapseTemplate.getNativeWrapper(nativeAdId)
    }
    private fun inflateAndPopulate(resIdLayout: Int,
                                   adViewLayout: ViewGroup,
                                   viewLifecycleOwner: LifecycleOwner?,
                                   nativeAd: NativeAd) {
        val context = adViewLayout.context
        viewLifecycleOwner?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                val nativeAdWrapper = getNativeWrapper(nativeAd)
                if (nativeAdWrapper == null) {
                    val nativeAdW = findAdWrapperAllPlace(nativeAd) ?: return
                    if (nativeAdW.hasImpression) {
                        nativeAd.destroy()
                    }
                    return
                }
                if (nativeAdWrapper.hasImpression) {
                    nativeAd.destroy()
                    listNativeAdWrapper.remove(nativeAdWrapper)
                }
            }
        })

        val inflater = LayoutInflater.from(context)
        val adView = inflater.inflate(resIdLayout, adViewLayout, false) as NativeAdView
        populateNativeAdView(nativeAd, adView, isHideAdImg = resIdLayout == R.layout.native_ads_recycleview || resIdLayout == R.layout.native_ads_recycleview_animation_setting)
        if (this == instanceCollapseTemplate) {
            shouldAddAtBottom = true
        }
        if (adViewLayout.id == R.id.bannerAdLayout2) {
            val lp: ViewGroup.LayoutParams = when (adViewLayout) {

                is FrameLayout -> FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM            // ép dính đáy
                )

                is androidx.constraintlayout.widget.ConstraintLayout -> {
                    (ConstraintLayout.LayoutParams(
                        ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                        ConstraintLayout.LayoutParams.WRAP_CONTENT
                    )).apply {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        endToEnd   = ConstraintLayout.LayoutParams.PARENT_ID
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }

                is RelativeLayout -> RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                }

                is LinearLayout -> LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.BOTTOM
                }

                else -> ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
            adViewLayout.layoutParams = lp
        }
        adViewLayout.apply {
            removeAllViews()
            adViewLayout.isVisible = true
            addView(adView)
        }
    }

    fun populateNativeAdView(nativeAd: NativeAd, nativeAdView: NativeAdView, isHideAdImg : Boolean = false) {
//        nativeAdView.adChoicesView = nativeAdView.findViewById<AdChoicesView?>(R.id.ad_choices_view)?.apply {
//            AppLogger.i("NativeAdview is adchoice  not null"  )
//        }
        nativeAdView.findViewById<View?>(R.id.containerBtnAdCollapse)?.apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                AppLogger.i("NativeAdview is containerBtnAdCollapse")
                nativeAdView.findViewById<View?>(R.id.btnAdCollapse)?.performClick()
            }
        }
        nativeAdView.findViewById<View?>(R.id.btnAdCollapse)?.apply {
            setOnClickListener {
                isVisible = false
                nativeAdView.findViewById<View?>(R.id.ad_media)?.let {
                    it.isVisible = false
                }
                val layoutParams = nativeAdView.layoutParams
                layoutParams.height = context.resources.getDimension(com.intuit.sdp.R.dimen._55sdp).toInt() // should be similar height whiles collapsing
                nativeAdView.layoutParams = layoutParams
            }
        }

        AppLogger.i("NativeAdview is adchoice null " + nativeAdView.adChoicesView )

        nativeAdView.headlineView = nativeAdView.findViewById(R.id.adTitle)

        if (nativeAdView.findViewById<View?>(R.id.ad_media)?.visibility != View.GONE) {
            nativeAdView.mediaView = nativeAdView.findViewById(R.id.ad_media)
            // Đặt mediaContent cho video hoặc hình ảnh
            val mediaContent = nativeAd.mediaContent


            if (mediaContent != null) {
                nativeAdView.mediaView?.setMediaContent(mediaContent)
            }
        }

        nativeAdView.callToActionView = nativeAdView.findViewById(R.id.adInstallButton)

        (nativeAdView.headlineView as TextView).text = nativeAd.headline
        (nativeAdView.callToActionView as Button).text = nativeAd.callToAction
        if (!nativeAd.body.isNullOrEmpty()) {
            nativeAdView.findViewById<TextView>(R.id.adDescription).text = nativeAd.body
        }
        nativeAd.icon?.let {
            (nativeAdView.iconView as? ImageView)?.setImageDrawable(it.drawable)
            if (!isHideAdImg)  nativeAdView.findViewById<ImageView>(R.id.adImg).apply {
                isVisible = true
                setImageDrawable(it.drawable)
            }
        }

        nativeAdView.setNativeAd(nativeAd)
    }
}