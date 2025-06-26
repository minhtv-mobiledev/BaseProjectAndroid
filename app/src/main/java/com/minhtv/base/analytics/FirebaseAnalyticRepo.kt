package com.draw.animation.analytics

import android.os.Bundle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseAnalyticRepo {
    // Kết nối đến Firebase Analytics
    val firebaseAnalytics = Firebase.analytics

    //region ***** CONSTANTS *****
    // Tên sự kiện "button_click"
    const val BUTTON_CLICK = "button_click"
    const val NAME = "name"
    const val SCREEN = "screen"

    // Tên sự kiện "select_template"
    const val SELECT_TEMPLATE = "select_template"
    const val TEMPLATE_ID = "template_id"

    // Tên sự kiện "choose_mode"
    const val CHOOSE_MODE = "choose_mode"
    const val MODE = "mode"

    // Tên sự kiện "project_saved"
    const val PROJECT_SAVED = "project_saved"
    const val FRAME_RATIO = "frame_ratio"
    const val FRAME_RATE = "frame_rate"
    const val BACKGROUND_TYPE = "background_type"
    const val FORMAT = "format"
    const val SAVE_TYPE = "save_type"

    // Tên sự kiện "set_wallpaper"
    const val SET_WALLPAPER = "set_wallpaper"
    const val WALLPAPER_TYPE = "wallpaper_type"

    // Tên sự kiện "create_project"
    const val CREATE_PROJECT = "create_project"

    const val INTERSTITIAL_OPEN_LOADED = "interstitial_open_loaded"
    const val LOADING_FINISHED = "loading_finished"
    const val SELECT_LANGUAGE_COMPLETED = "select_language_completed"
    const val ONBOARD_STEP_X = "select_language_completed"

    //endregion


    //region ***** BASE LOG FUNCTION *****
    /**
     * Hàm gốc để log event, bạn có thể tái sử dụng ở mọi nơi.
     */
    private fun logEvent(eventName: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(eventName, bundle)
    }
    fun logInterstitialOpenLoaded() {
        logEvent(INTERSTITIAL_OPEN_LOADED, Bundle())
    }
    fun logLoadingFinished() {
        logEvent(LOADING_FINISHED, Bundle())
    }
    fun logSelectLanguageCompleted() {
        logEvent(SELECT_LANGUAGE_COMPLETED, Bundle())
    }
    fun logOnboardStepX(step: Int) {
        val bundle = Bundle().apply {
            putInt(ONBOARD_STEP_X, step)
        }
        logEvent(ONBOARD_STEP_X, bundle)
    }

    fun setUserProperty(propertyName: String, value: String) {
        firebaseAnalytics.setUserProperty(propertyName, value)
    }
    //endregion

    //region ***** EXAMPLE EVENT FUNCTION *****
    /**
     * Ví dụ: log event khi người dùng bấm button,
     * kèm tên button và màn hình xuất phát.
     */
    fun logEventButtonClick(name: String, screen: String) {
        val bundle = Bundle().apply {
            putString(NAME, name)
            putString(SCREEN, screen)
        }
        logEvent(BUTTON_CLICK, bundle)
    }
    //endregion

    //region ***** ADDITIONAL EVENT FUNCTIONS *****

    /**
     * 1) Log event "select_template"
     *    - Khi người dùng chọn một template nào đó.
     */
    fun logEventSelectTemplate(templateIdValue: Int) {
        val bundle = Bundle().apply {
            putInt(TEMPLATE_ID, templateIdValue)
        }
        logEvent(SELECT_TEMPLATE, bundle)
    }

    /**
     * 2) Log event "choose_mode"
     *    - Ví dụ: "draw_use_template" hoặc "draw_according_template"...
     */
    fun logEventChooseMode(modeValue: String) {
        val bundle = Bundle().apply {
            putString(MODE, modeValue)
        }
        logEvent(CHOOSE_MODE, bundle)
    }

    /**
     * 3) Log event "project_saved"
     *    - Khi người dùng lưu dự án, kèm thông số:
     *       + save_type: "draft" hoặc "gallery"
     *       + frame_ratio: "1:1", "9:16" ...
     *       + frame_rate: Số khung hình, ví dụ 30
     *       + background_type: "color_fill", "camera", "gallery" ...
     *       + format: "mp4", "gif"
     *       + template_id: template đang dùng (optional)
     */
    fun logEventProjectSaved(
        saveTypeValue: String,
        frameRatioValue: String,
        frameRateValue: Int,
        backgroundTypeValue: String,
        formatValue: String,
        templateIdValue: Int? = null
    ) {
        val bundle = Bundle().apply {
            putString(SAVE_TYPE, saveTypeValue)
            putString(FRAME_RATIO, frameRatioValue)
            putInt(FRAME_RATE, frameRateValue)
            putString(BACKGROUND_TYPE, backgroundTypeValue)
            putString(FORMAT, formatValue)

            // Template_id đôi khi không có
            templateIdValue?.let { putInt(TEMPLATE_ID, it) }
        }
        logEvent(PROJECT_SAVED, bundle)
    }

    /**
     * 4) Log event "set_wallpaper"
     *    - Khi người dùng set wallpaper cho màn hình Home, Lock, ...
     *       + wallpaper_type: "home_screen" / "lock_screen"
     *       + frame_ratio: "9:16" ...
     *       + frame_rate: 30 ...
     */
    fun logEventSetWallpaper(
        wallpaperTypeValue: String,
    ) {
        val bundle = Bundle().apply {
            putString(WALLPAPER_TYPE, wallpaperTypeValue)
        }
        logEvent(SET_WALLPAPER, bundle)
    }

    /**
     * 5) Log event "create_project"
     *    - Khi user tạo project mới, kèm:
     *       + background_type: "default", "color_fill", ...
     *       + format: "mp4", "gif", ...
     *       + template_id: optional
     */
    fun logEventCreateProject(
        frameRatioValue: String,
        frameRateValue: Int,
        backgroundTypeValue: String,
        formatValue: String,
        templateIdValue: Int? = null
    ) {
        val bundle = Bundle().apply {
            putString(BACKGROUND_TYPE, backgroundTypeValue)
            putString(FORMAT, formatValue)
            templateIdValue?.let { putInt(TEMPLATE_ID, it)
                putString(FRAME_RATIO, frameRatioValue)
                putInt(FRAME_RATE, frameRateValue)
            }
        }
        logEvent(CREATE_PROJECT, bundle)
    }
    //endregion
}
