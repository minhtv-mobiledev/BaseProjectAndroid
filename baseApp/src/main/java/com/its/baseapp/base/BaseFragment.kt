package com.its.baseapp.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.its.baseapp.its.ultis.other.AppExtensions
import com.its.baseapp.its.ultis.other.LanguageUtil


abstract class BaseFragment<T : ViewBinding> : Fragment() {

    lateinit var binding: T
    private lateinit var callback: OnBackPressedCallback

    protected lateinit var mActivity: BaseActivity<*>
    private lateinit var mContext: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        mActivity = context as BaseActivity<*>
    }

    open fun handlerBackPressed() {}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageUtil.setLanguage()
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                handlerBackPressed()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }


    override fun onDestroy() {
        super.onDestroy()
        callback.remove()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = getBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    abstract fun initView()

    abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup?): T

    fun addFragment(fragment: Fragment) {
        (requireActivity() as BaseActivity<*>).addFragment(fragment)
    }

    fun replaceFragment(fragment: Fragment) {
        (requireActivity() as BaseActivity<*>).replaceFragment(fragment)
    }

    open fun closeFragment(fragment: Fragment) {
        (requireActivity() as BaseActivity<*>).handleBackPress()
    }
    open fun startNewActivityWithAnimation(context: Context, targetActivity: Class<out AppCompatActivity>, view: View) {
        (requireActivity() as BaseActivity<*>).startNewActivityWithAnimation(context,targetActivity,view)
    }
    override fun onStop() {
        super.onStop()
        AppExtensions.hideSoftKeyboard(requireActivity())
    }

    companion object {
        fun <F : Fragment> newInstance(fragment: Class<F>, args: Bundle? = null): F {
            val f = fragment.newInstance()
            args?.let {
                f.arguments = it
            }
            return f
        }
    }
}
