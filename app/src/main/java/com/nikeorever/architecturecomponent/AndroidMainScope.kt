package com.nikeorever.architecturecomponent

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.View
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

interface AndroidMainScope {

    private val scope: CoroutineScope
        get() {
            var innerScope = scopeMap[this]
            if (innerScope == null) {
                innerScope = MainScope()
                scopeMap[this] = innerScope
            }
            return innerScope
        }

    fun <T> withScope(block: CoroutineScope.() -> T): T {
        return with(scope, block)
    }

    fun withLaunchedCoroutineScope(block: suspend CoroutineScope.() -> Unit) {
        withScope {
            launch(block = block)
        }
    }

    fun <V : View> V.onClick(block: suspend CoroutineScope.(v: V) -> Unit) {
        setOnClickListener {
            withLaunchedCoroutineScope {
                block(this@onClick)
            }
        }
    }

    fun <V : View> V.onClickDisposable(
        context: CoroutineContext = Dispatchers.Main,
        block: suspend CoroutineScope.(v: V) -> Unit
    ) {
        setOnClickListener {
            GlobalScope.launch(context = context) {
                block(this, this@onClickDisposable)
            }.asDisposable(this)
        }
    }

    fun createScope() {
        scopeMap[this] = MainScope()
    }

    fun destroyScope() {
        scopeMap.remove(this)?.cancel()
    }

    companion object {
        private val scopeMap: IdentityHashMap<AndroidMainScope, CoroutineScope> = IdentityHashMap()
        fun create(activity: Activity?) {
            (activity as? AndroidMainScope)?.createScope()
        }

        fun destroy(activity: Activity?) {
            (activity as? AndroidMainScope)?.destroyScope()
        }
    }

    private fun Job.asDisposable(v: View) = AutoDisposable(v, this)

    private class AutoDisposable(val view: View, val wrapper: Job) : Job by wrapper, View.OnAttachStateChangeListener {
        override fun onViewDetachedFromWindow(v: View?) {
            view.removeOnAttachStateChangeListener(this)
            cancel()
        }

        override fun onViewAttachedToWindow(v: View?) {
        }

        @SuppressLint("ObsoleteSdkInt")
        fun isAttachedToWindow(): Boolean =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && view.isAttachedToWindow
                    || view.windowToken != null

        init {
            if (isAttachedToWindow()) {
                view.addOnAttachStateChangeListener(this)
            } else {
                cancel()
            }
        }
    }

}