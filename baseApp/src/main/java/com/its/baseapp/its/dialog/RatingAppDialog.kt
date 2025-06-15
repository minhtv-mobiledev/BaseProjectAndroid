package com.its.baseapp.its.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.its.baseapp.R
import com.its.baseapp.base.BaseDialogFragment
import com.its.baseapp.databinding.DialogRateAppBinding
import com.its.baseapp.its.ultis.other.setGradiantText

class RatingAppDialog(
    private val context: Context,
    private val callback: () -> Unit,
    private val callbackCancel: () -> Unit
) :
    BaseDialogFragment<DialogRateAppBinding>() {
    private var numOfStar = 0
    private var img1Star = 0
    private var img2Star = 0
    private var img3Star = 0
    private var img4Star = 0
    private var img5Star = 0
    private var ic_start_active = R.drawable.ic_start_active
    private var ic_star_inactive = R.drawable.ic_star_inactive
    private var text1between3Star = context.getString(R.string.oh_no_we_will_try_to_improve)
    private var text4Star = context.getString(R.string.pretty_good)
    private var text5Star = context.getString(R.string.i_love_it)
    private var backgroundBtnRate = R.drawable.bg_btn_rate
    private var rate_us_on_google_play = context.getString((R.string.rate_us_on_google_play))
    private var rate_us = context.getString(R.string.rate_us)
    private var feedback = context.getString(R.string.feedback)
    private var ivPoint = R.drawable.ic_direction_to_best_rate
    private var colorTextRate = R.color.white

    fun setTextColorTvRate(color: Int) {
        colorTextRate = color
    }
    fun setTextColorTvBest(color: Int) {
        binding.tvBest.setTextColor(color)
    }
    fun setTextColorTvLater(color: Int) {
        binding.tvLater.setTextColor(color)
    }
    fun setIvPoint(ivPoint: Int) {
        this.ivPoint = ivPoint
    }
    fun setText4Star(string: String){
        text4Star = string
    }
    fun setText5Star(string: String){
        text5Star = string
    }
    fun setText1between3Star(string: String){
        text1between3Star = string
    }
    fun setImgWithStar(rsc1: Int, rsc2: Int, rsc3: Int, rsc4: Int, rsc5: Int) {
        img1Star = rsc1
        img2Star = rsc2
        img3Star = rsc3
        img4Star = rsc4
        img5Star = rsc5
    }

    fun setIconStar(startActive: Int, startNotActive: Int) {
        ic_start_active = startActive
        ic_star_inactive = startNotActive
    }

    fun setBgBtnRate(btnBg: Int) {
        backgroundBtnRate = btnBg
    }

    private fun showFeedbackDialog(
        activity: Context,
        subject: String?,
        email: String?,
        body: String?
    ) {
        try {
            val uriText = String.format("mailto:%s?subject=%s&body=%s", email, subject, body)
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(uriText))
            activity.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun rateApp(context: Context, packageName: String) {
        try {
//            SharedPreferenceUtils.isRateApp = true
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (ex: Exception) {
            ex.message
        }
    }

    private fun onStarClicked(
        num: Int,
        imgEmoji: ImageView,
        imgStar1: ImageView,
        imgStar2: ImageView,
        imgStar3: ImageView,
        imgStar4: ImageView,
        imgStar5: ImageView,
        tvRate: TextView,
        tvLikeIt: TextView
    ) {
        this.numOfStar = num
        if (num == 0) imgEmoji.visibility = View.GONE
        else imgEmoji.visibility = View.VISIBLE
        if (num == 1) {
            imgEmoji.setImageResource(if (img1Star != 0) img1Star else R.drawable.img_1_star)
        }
        if (num == 2) {
            imgEmoji.setImageResource(if (img2Star != 0) img2Star else R.drawable.img_2_star)
        }
        if (num == 3) {
            imgEmoji.setImageResource(if (img3Star != 0) img3Star else R.drawable.img_3_star)
        }
        if (num == 4) {
            imgEmoji.setImageResource(if (img4Star != 0) img4Star else R.drawable.img_4_star)
        }
        if (num == 5) {
            imgEmoji.setImageResource(if (img5Star != 0) img5Star else R.drawable.img_5_star)
        }
//        TrackingEvent.init(context).logEvent(TrackingEvent.RATE_CLICK_STAR_+numOfStar)
        imgStar1.setImageResource(if (num >= 1) ic_start_active else ic_star_inactive)
        imgStar2.setImageResource(if (num >= 2) ic_start_active else ic_star_inactive)
        imgStar3.setImageResource(if (num >= 3) ic_start_active else ic_star_inactive)
        imgStar4.setImageResource(if (num >= 4) ic_start_active else ic_star_inactive)
        imgStar5.setImageResource(if (num >= 5) ic_start_active else ic_star_inactive)

        tvRate.visibility = if (num > 0) View.VISIBLE else View.INVISIBLE
        tvRate.text =if ((num > 1)) rate_us else feedback
        if (num > 0) {
            imgEmoji.visibility = View.VISIBLE
            tvRate.visibility = View.VISIBLE
            tvRate.alpha = 1f
            tvRate.setBackgroundResource(backgroundBtnRate)
        } else {
            tvRate.alpha = 0.5f
            imgEmoji.visibility = View.GONE
            tvRate.visibility = View.GONE
            tvLikeIt.text =
                context.getString(R.string.title_dialog_rate)
        }
        when (num) {
            in 1..3 -> {
                tvLikeIt.text =  text1between3Star
            }

            4 -> {
                tvLikeIt.text =  text4Star
                tvRate.text = rate_us_on_google_play
            }

            5 -> {
                tvLikeIt.text =  text5Star
                tvRate.text = rate_us_on_google_play
            }
        }
    }

    override fun initView() {
        binding.tvRate.setTextColor(resources.getColor(colorTextRate))

        binding.point.setImageResource(ivPoint)
        onStarClicked(
            5,
            binding.imgEmotion,
            binding.icStar1,
            binding.icStar2,
            binding.icStar3,
            binding.icStar4,
            binding.icStar5,
            binding.tvRate,
            binding.tvGuide
        )
        binding.icStar1.setOnClickListener {
            onStarClicked(
                1,
                binding.imgEmotion,
                binding.icStar1,
                binding.icStar2,
                binding.icStar3,
                binding.icStar4,
                binding.icStar5,
                binding.tvRate,
                binding.tvGuide
            )
        }
        binding.icStar2.setOnClickListener {
            onStarClicked(
                2,
                binding.imgEmotion,
                binding.icStar1,
                binding.icStar2,
                binding.icStar3,
                binding.icStar4,
                binding.icStar5,
                binding.tvRate,
                binding.tvGuide
            )
        }
        binding.icStar3.setOnClickListener {
            onStarClicked(
                3,
                binding.imgEmotion,
                binding.icStar1,
                binding.icStar2,
                binding.icStar3,
                binding.icStar4,
                binding.icStar5,
                binding.tvRate,
                binding.tvGuide
            )
        }
        binding.icStar4.setOnClickListener {
            onStarClicked(
                4,
                binding.imgEmotion,
                binding.icStar1,
                binding.icStar2,
                binding.icStar3,
                binding.icStar4,
                binding.icStar5,
                binding.tvRate,
                binding.tvGuide
            )
        }
        binding.icStar5.setOnClickListener {
            onStarClicked(
                5,
                binding.imgEmotion,
                binding.icStar1,
                binding.icStar2,
                binding.icStar3,
                binding.icStar4,
                binding.icStar5,
                binding.tvRate,
                binding.tvGuide
            )
        }

        binding.tvRate.setOnClickListener {
            if (numOfStar <= 0) return@setOnClickListener
            this.dismiss()

            Handler(Looper.getMainLooper()).postDelayed({
                if (numOfStar == 1) {
                    //TrackingEvent.init(context).logEvent(TrackingEvent.RATE_CLICK_FEED_BACK)
                }
                if (numOfStar < 4) {
                    if (numOfStar > 1) {
//                        TrackingEvent.init(context).logEvent(TrackingEvent.RATE_CLICK_RATE_US)
                    }
                    showFeedbackDialog(
                        context,
                        context.packageName,
                        "",
                        ""
                    )
                } else {
//                    TrackingEvent.init(context).logEvent(TrackingEvent.RATE_CLICK_RATE_US_ON_GG)
                    rateApp(context, context.packageName)
                }
                callback()
            }, 500L)
        }
        binding.tvLater.setOnClickListener {
//            TrackingEvent.init(context).logEvent(TrackingEvent.RATE_CLICK_MAYBE_LATE)
            callbackCancel()
            dismiss()
        }
//        MyApplication.isRateShowed = true
        binding.tvLater.setGradiantText("#FFB648", "#FF5D5D")
    }

    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DialogRateAppBinding = DialogRateAppBinding.inflate(inflater, container, false)
}