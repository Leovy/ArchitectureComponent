package com.nikeorever.architecturecomponent

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

fun CoroutineScope.log(tag: String, msg: String?) {
    val format = "${System.currentTimeMillis()} [$coroutineContext] $msg"
    Log.i(tag, format)
}

fun log(tag: String, msg: String?) {
    val format = "${System.currentTimeMillis()} $msg"
    Log.i(tag, format)
}

suspend fun <T> LiveData<T>.observeAwait(owner: LifecycleOwner): T {
    return suspendCancellableCoroutine { ctn: CancellableContinuation<T> ->
        ctn.invokeOnCancellation {
            removeObservers(owner)
        }
        observe(owner, Observer {
            ctn.resume(it)
        })
    }
}