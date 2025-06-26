package com.minhtv.base.activities

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.android.billingclient.api.BillingClient
import com.draw.animation.analytics.FirebaseAnalyticRepo

import com.draw.animation.utils.AppLogger

import com.draw.animation.utils.ads.NativeAdManager
import com.minhtv.base.BuildConfig
import com.minhtv.base.R
import com.minhtv.base.databinding.ActivityMainBinding
import com.minhtv.base.utils.LanguagePrefHelper
import com.minhtv.base.utils.LocaleUtils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var fakeStatusBar: View
    lateinit var fakeNavBar: View
    lateinit var billingClient: BillingClient
    lateinit var prefs: SharedPreferences


    companion object {
        const val HOME_SCREEN_TAG = "HomeScreenFragment"
        const val ANIMATION_SETTING_TAG = "AnimationSettingFragment"
        const val TEM_PREVIEW_TAG = "TEM_PREVIEW_TAG"
        const val SETTING_TAG = "SETTING_TAG"

        const val gallery_count = "gallery_count"
        const val draft_count = "draft_count"
        const val draw_use_tem_count = "draw_use_tem_count"
        const val draw_according_tem_count = "draw_according_tem_count"
        const val wallpaper_usage_count = "wallpaper_usage_count"
        var flagUIDefault : Int = 0
        val binUrl = "https://cdn.widodc.com/games/v4_picture_116_android___.bin"


    }
    override fun attachBaseContext(newBase: Context) {
        // Lấy ngôn ngữ đã lưu (hoặc dùng locale mặc định của hệ thống nếu chưa lưu)
        val language = LanguagePrefHelper.getSavedLanguageCode(newBase)
        // Cập nhật context với locale mới
        var context = newBase
//        GlobalInstance.alreadyChooseLang = language != null
        language?.let {
            AppLogger.i("Here come attachBaseContext on configuration changed")
            context = LocaleUtils.setLocale(newBase, it)
        }
        super.attachBaseContext(context)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        AppLogger.i("Here come activity on configuration changed")
        // Kiểm tra thay đổi cấu hình
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Cập nhật giao diện cho chế độ landscape
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Cập nhật giao diện cho chế độ portrait
        }
        if (newConfig.layoutDirection == Configuration.SCREENLAYOUT_LAYOUTDIR_RTL) {
            AppLogger.i("Here come activity on configuration changed RTL")

            // Cập nhật giao diện cho RTL
//            val view = findViewById<View>(R.id.main)
//            AppUtils.setViewDirection(view)
        } else {
            // Cập nhật giao diện cho LTR
        }

        // Nếu có thay đổi về uiMode (chẳng hạn chuyển từ light sang dark)
        if ((newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            // Cập nhật giao diện cho dark mode
        } else {
            // Cập nhật giao diện cho light mode
        }

        // Nếu cần cập nhật lại các tài nguyên khác (ví dụ: hình nền, màu sắc, layout)
        // bạn có thể gọi lại các phương thức cập nhật UI hoặc thậm chí inflate lại layout phần nào.
    }



    override fun onPause() {
        super.onPause()
        AppLogger.i("Here come activity on pause")
        val properties = listOf(
            "gallery_count",
            "draft_count",
            "draw_use_tem_count",
            "draw_according_tem_count",
            "wallpaper_usage_count"
        )

        for (property in properties) {
            val value = prefs.getInt(property, 0)
            FirebaseAnalyticRepo.firebaseAnalytics.setUserProperty(property, value.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AppLogger.i("Here come activity on destroy")
    }

    override fun onRestart() {
        super.onRestart()
        AppLogger.i("Here come activity on restart")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isRecreated", true)
    }
    fun getScreenSize(context: Context): String {
        val screenSize = context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK

        val configuration = resources.configuration
        val screenWidth = configuration.screenWidthDp
        val screenHeight = configuration.screenHeightDp

        var screenSized = when (screenSize) {
            Configuration.SCREENLAYOUT_SIZE_SMALL -> "Small"
            Configuration.SCREENLAYOUT_SIZE_NORMAL -> "Normal"
            Configuration.SCREENLAYOUT_SIZE_LARGE -> "Large"
            Configuration.SCREENLAYOUT_SIZE_XLARGE -> "Xlarge"
            else -> "Unknown"
        }
        screenSized += " width $screenWidth height $screenHeight"

        return screenSized
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if(viewModel.isFirstTime) {
//            viewModel.loadDataFromUrl(this, binUrl)
//            viewModel.isFirstTime = false
//        }
        top = 0
        bottom = 0

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        AppLogger.i("Here come screen size + ${getScreenSize(this)}")
        var isRecreated = savedInstanceState != null
//        if (!isRecreated) {
//            (applicationContext as MyApplication).handleBilling()
//            lifecycleScope.launch(Dispatchers.IO) {
//                while (!isBillingSetUpFinished) {
//                    delay(1000)
//                }
//                activityViewModels.getBilling()
//            }
//        }
        if (savedInstanceState != null) {
            isRecreated = savedInstanceState.getBoolean("isRecreated", false)
        }
        setContentView(R.layout.activity_main)
        enableEdgeToEdge()
        // Trong Activity hoặc Fragment của bạn
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // API 30+
            val layoutParams = window.attributes
            layoutParams.preferredRefreshRate = 60f // Đặt preferred refresh rate là 60Hz
            window.attributes = layoutParams
        }

//        if (!isRecreated) {
//            supportFragmentManager.navigateWithDefaultAnimations(fragment = WelcomeScreenFragment(),args = null, containerId = R.id.fragment_container, null, addToBackStack = false, safeNavigate = false)
//        } else if (GlobalInstance.isJustUseGif && isRecreated) {
//            AppLogger.i("Here come activity re created")
////            supportFragmentManager.navigateWithDefaultAnimations(fragment = DrawResult(),args = null, containerId = R.id.fragment_container, "123", addToBackStack = false)
//        } else if (GlobalInstance.isFromSetting) {
//            supportFragmentManager.popBackStack(SETTING_TAG, 0)
//        }
//        GlobalInstance.justChooseLang = false
//        GlobalInstance.shouldShowOnBoarding = false
//        GlobalInstance.isFromSetting = false

        if (BuildConfig.DEBUG) CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val runtime = Runtime.getRuntime()
                val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 // MB
                val maxMemory = runtime.maxMemory() / 1024 / 1024 // MB
                val allocatedMemory = runtime.totalMemory() / 1024 / 1024 // MB

                val displayMetrics = resources.displayMetrics
                val width = displayMetrics.widthPixels
                val height = displayMetrics.heightPixels

// Tính toán tỷ lệ màn hình
                val aspectRatio = height.toFloat() / width.toFloat()
                Log.d("heap info", "Used Memory: ${usedMemory}MB, Allocated: ${allocatedMemory}MB, Max: ${maxMemory}MB" + "aspecratio: $aspectRatio")
                AppLogger.i("Here is available  ${NativeAdManager.instanceFullscreenOB.isAdAvailable()}  ${NativeAdManager.instanceLanguageOrSetting.isAdAvailable()}  ${NativeAdManager.instanceBottomOB.isAdAvailable()} ")
                printScreenRefreshRate()
                delay(5000) // Đợi 1 giây trước khi chạy lại
            }
        }
        fakeNavBar = findViewById<View>(R.id.fakeNavBar)
        fakeStatusBar = findViewById<View>(R.id.fakeStatusBar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            AppLogger.i("Here come system bars: ${systemBars.top} ${systemBars.bottom} ${systemBars.left} ${systemBars.right}")
            if (systemBars.top > 0 && systemBars.bottom > 0) {
                if (top > 0) {
                    return@setOnApplyWindowInsetsListener insets
                }
                val params = fakeStatusBar.layoutParams
                params.height = systemBars.top
                fakeStatusBar.layoutParams = params


                val params2 = fakeNavBar.layoutParams
                params2.height = systemBars.bottom
                fakeNavBar.layoutParams = params2

                top = systemBars.top

            }
//            if (!GlobalInstance.shouldUpdateInsets) {
//                return@setOnApplyWindowInsetsListener insets
//            }
            if (systemBars.top == 0 || systemBars.bottom == 0) {
                return@setOnApplyWindowInsetsListener insets
            }
            insets
        }
    }
    var top = 0
    var bottom = 0
    // Trong Activity hoặc Fragment của bạn
    fun printScreenRefreshRate() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay

        // Lấy tần số quét hiện tại
        val currentRefreshRate = display.refreshRate
        Log.d("ScreenInfo", "Tần số quét hiện tại (Hz): $currentRefreshRate")

        // Đối với Android 11 (API level 30) trở lên, bạn có thể lấy danh sách các chế độ hiển thị được hỗ trợ
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val supportedModes = display.supportedModes
            Log.d("ScreenInfo", "Các tần số quét được hỗ trợ:")
            for (mode in supportedModes) {
                Log.d("ScreenInfo", "  - ${mode.refreshRate} Hz (rộng: ${mode.physicalWidth}px, cao: ${mode.physicalHeight}px)")
            }
        }
    }
}

