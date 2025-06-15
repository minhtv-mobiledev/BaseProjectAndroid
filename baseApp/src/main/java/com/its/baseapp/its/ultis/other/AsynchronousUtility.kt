package com.its.baseapp.its.ultis.other

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

object AsynchronousUtility {
    fun <T> coroutineBackground(
        context: CoroutineContext = Dispatchers.IO,
        action: suspend () -> T,
        onSuccess: (result: T) -> Unit = {},
        onError: (t: Throwable) -> Unit = {}
    ): Job {
        return CoroutineScope(context).launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    action()
                }
                withContext(Dispatchers.Main) {
                    onSuccess(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            }
        }
    }
}

