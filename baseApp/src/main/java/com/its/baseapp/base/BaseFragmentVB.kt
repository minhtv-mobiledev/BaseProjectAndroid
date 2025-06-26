package com.its.baseapp.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.draw.animation.utils.extension.addOnBackPressedCallback

abstract class BaseFragmentVB<VB : ViewBinding> : Fragment() {
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    private var _binding: VB? = null
    protected val binding get() = _binding!!
//    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    inline fun <reified T : ViewModel> Fragment.getViewModel(): T {
        return ViewModelProvider(this)[T::class.java]
    }
    inline fun <reified T : ViewModel> Fragment.getActivityViewModel(): T {
        return ViewModelProvider(requireActivity())[T::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addOnBackPressedCallback {
            onBackPressed()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View? {
//        val vbClass = (javaClass.genericSuperclass as? ParameterizedType)
//            ?.actualTypeArguments
//            ?.firstOrNull() as? Class<VB>
//            ?: throw IllegalArgumentException("ViewBinding class not found.")
//
//        val inflateMethod = vbClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        _binding = bindingInflater(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initData()
    }

    override fun onResume() {
        super.onResume()
        onResumeOrVisible()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            onResumeOrVisible()
        } else {
            onPauseOrInvisible()
        }
    }

    open fun onResumeOrVisible() {}
    open fun onPauseOrInvisible(){}
    /** Optional override: setup layout, view properties */
    open fun initView() {}

    /** Optional override: attach click listeners etc. */
    open fun initListener() {}

    /** Optional override: load data, observe LiveData etc. */
    open fun initData() {}
    open fun onBackPressed() {
        try {
            requireActivity().supportFragmentManager.popBackStack()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
