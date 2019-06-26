package com.nikeorever.architecturecomponent

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume

fun CoroutineScope.log(tag: String, msg: String?) {
    val format =
        "${SimpleDateFormat.getDateTimeInstance().format(Date(System.currentTimeMillis()))} [${Thread.currentThread().name}] $msg"
    Log.i(tag, format)
}

fun log(tag: String, msg: String?) {
    val format =
        "${SimpleDateFormat.getDateTimeInstance().format(Date(System.currentTimeMillis()))} [${Thread.currentThread().name}] $msg"
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