package com.its.baseapp.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.its.baseapp.R
import com.its.baseapp.its.ultis.other.AppExtensions
import com.its.baseapp.its.ultis.other.LanguageUtil
import com.its.baseapp.its.ultis.theme.ThemeUtil

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {
    lateinit var binding: VB
    protected abstract fun getViewBinding(): VB

    //    protected abstract fun getLayoutId(): Int
    abstract fun createView()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageUtil.setLanguage()
        setTheme(ThemeUtil.getTheme())
        binding = getViewBinding()
//        binding = DataBindingUtil.setContentView(this, getLayoutId())
        setContentView(binding.root)
        createView()
    }

    override fun onResume() {
        super.onResume()
        AppExtensions.setFullscreen(this)
    }
    fun handleBackPress() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ContextCompat.getColor(this, android.R.color.transparent)
        } else {
            finish()
        }
    }

    open fun replaceFragment(
        fragment: Fragment,
        viewId: Int = android.R.id.content,
        addToBackStack: Boolean = true
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(viewId, fragment, fragment.javaClass.simpleName)
        transaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        if (addToBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.commit()
    }

    open fun addFragment(
        fragment: Fragment,
        viewId: Int = android.R.id.content,
        addToBackStack: Boolean = true
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in,
            R.anim.fade_out,
            R.anim.fade_in,
            R.anim.slide_out
        )
        transaction.add(viewId, fragment, fragment.javaClass.simpleName)
        if (addToBackStack) {
            transaction.addToBackStack(fragment.javaClass.simpleName)
        }
        transaction.commit()
    }

    protected open fun openAppSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }

    open fun startNewActivityWithAnimation(
        context: Context,
        targetActivity: Class<out AppCompatActivity>,
        view: View
    ) {
        // Tạo Intent để khởi động Activity mới
        val intent = Intent(context, targetActivity)

        // Lấy vị trí và kích thước của view để tạo animation phóng to
        val options = ActivityOptionsCompat.makeScaleUpAnimation(
            view, // View để bắt đầu animation
            view.width / 2, // X bắt đầu
            view.height / 2, // Y bắt đầu
            0, // Chiều rộng phóng to
            0 // Chiều cao phóng to
        )
        // Bắt đầu Activity mới với animation
        context.startActivity(intent, options.toBundle())
    }

    open fun startNewActivityWithAnimation(context: Context, intent: Intent, view: View) {
        // Lấy vị trí và kích thước của view để tạo animation phóng to
        val options = ActivityOptionsCompat.makeScaleUpAnimation(
            view, // View để bắt đầu animation
            view.width / 2, // X bắt đầu
            view.height / 2, // Y bắt đầu
            0, // Chiều rộng phóng to
            0 // Chiều cao phóng to
        )
        // Bắt đầu Activity mới với animation
        context.startActivity(intent, options.toBundle())
    }

}