package com.minhtv.base.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.draw.animation.R // Thay thế bằng package của bạn nếu khác
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Volatile
var lastNavigateTime = 0L

private const val debounceInterval = 500L // ms

fun canNavigate(): Boolean {
    val now = System.currentTimeMillis()
    return if (now - lastNavigateTime > debounceInterval) {
        lastNavigateTime = now
        true
    } else {
        false
    }
}

fun NavController.navigateWithDefaultAnimations(
    destinationId: Int,
    args: Bundle? = null
) {
    if (!canNavigate()) return

    val defaultNavOptions = NavOptions.Builder()
        .setEnterAnim(R.anim.slide_in_right)
        .setExitAnim(R.anim.slide_out_left)
        .setPopEnterAnim(R.anim.slide_in_left)
        .setPopExitAnim(R.anim.slide_out_right)
        .build()

    this.navigate(destinationId, args, defaultNavOptions)
}

fun FragmentManager.navigateWithDefaultAnimations(
    fragment: Fragment,
    args: Bundle? = null,
    containerId: Int = R.id.fragment_container,
    tag: String? = null,
    addToBackStack: Boolean = true,
    safeNavigate : Boolean = true,
    clearBackStack: Boolean = false
) {
    if (!canNavigate()) return
    if (clearBackStack) {
        popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }
    args?.let {
        fragment.arguments = it
    }
    if (!safeNavigate) {
        beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,  // enter
                R.anim.slide_out_left,  // exit
                R.anim.slide_in_left,   // popEnter
                R.anim.slide_out_right  // popExit
            )
            .replace(containerId, fragment, tag)
            .apply {
                if (addToBackStack) addToBackStack(tag ?: fragment::class.java.simpleName)
            }
            .commit()
        return
    }
    CoroutineScope(Dispatchers.Main).launch {
        while (isStateSaved) {
            delay(50)
        }
        beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,  // enter
                R.anim.slide_out_left,  // exit
                R.anim.slide_in_left,   // popEnter
                R.anim.slide_out_right  // popExit
            )
            .replace(containerId, fragment, tag)
            .apply {
                if (addToBackStack) addToBackStack(tag ?: fragment::class.java.simpleName)
            }
            .setPrimaryNavigationFragment(fragment) // Đặt fragment mới là primary navigation fragment
            .commit()
    }
}
fun Fragment.navigateUp() {
    if (!canNavigate()) return
    val fm = activity?.supportFragmentManager ?: return
    if ((isAdded && !fm.isStateSaved)) {
        fm.popBackStackImmediate()
        return
    }
    lifecycleScope.launch {
        while (!(isAdded && !fm.isStateSaved)) {
            delay(50)
        }
        fm.popBackStackImmediate()
    }
}
fun Fragment.safePopBackstack(tag : String? = null, flag: Int = 0) {
    if (!canNavigate()) return
    val fm = activity?.supportFragmentManager ?: return
    lifecycleScope.launch {
        while (!(isAdded && !fm.isStateSaved)) {
            delay(50)
        }
        if (tag == null) {
            fm.popBackStack()
        } else {
            fm.popBackStack(tag, flag)
        }
    }
}
fun FragmentManager.addFragmentWithAnimation2(
    fragment: Fragment,
    containerId: Int,
    tag: String? = null,
    addToBackStack: Boolean = true,
    hideCurrent : Boolean = true
) {
    if (!canNavigate()) return
    CoroutineScope(Dispatchers.Main).launch {
        while (isStateSaved) {
            delay(50)
        }
        val transaction = beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,  // enter
                R.anim.slide_out_left,  // exit
                R.anim.slide_in_left,   // popEnter
                R.anim.slide_out_right  // popExit
            )
        // Ẩn fragment hiện tại nếu yêu cầu
        if (hideCurrent) {
            findFragmentById(containerId)?.let { currentFragment ->
                transaction.hide(currentFragment)
            }
        }
        transaction.add(containerId, fragment, tag)
            .apply {
                if (addToBackStack) addToBackStack(tag ?: fragment::class.java.simpleName)
            }
            .setPrimaryNavigationFragment(fragment) // Đặt fragment mới là primary navigation fragment
            .commit()
    }
}
fun Fragment.addFragmentWithAnimation(
    fragment: Fragment,
    args: Bundle? = null,
    containerId: Int = R.id.fragment_container,
    tag: String? = null,
    addToBackStack: Boolean = true,
    hideCurrent : Boolean = true
) {

    // Gán bundle nếu có
    args?.let {
        fragment.arguments = it
    }
    parentFragmentManager.addFragmentWithAnimation2(fragment, containerId, tag, addToBackStack, hideCurrent)
}


