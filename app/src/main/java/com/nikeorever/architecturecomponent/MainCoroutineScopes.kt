package com.nikeorever.architecturecomponent

import android.annotation.SuppressLint
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.*
import java.util.*
import kotlin.coroutines.CoroutineContext

fun bindMainScopeTo(lifecycle: LifecycleOwner) {
    with(lifecycle.lifecycle) {
        if (currentState == Lifecycle.State.DESTROYED) {
            return
        }
        addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) onDestroy(owner = source)
            }

            fun onDestroy(owner: LifecycleOwner) {
                // owner is Fragment or AppCompatActivity
                MainCoroutineScope.cancelFrom(owner)
                owner.lifecycle.removeObserver(this)
            }
        })
    }
}

/**
 * 用于在[View]的Click事件回调中使用协程,可以使用[context]指定协程执行的上下文,当[View] detached的时候,尚未结束的协程会自动取消
 */
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

/**
 * 使用[AppCompatActivity] or [Fragment] 实现这个接口,即可在当前生命周期内使用[MainScope],
 * 当页面即将销毁的时候,所有未执行结束的协程都将自动取消
 */
interface MainCoroutineScope {

    private val mainScope: CoroutineScope
        get() = mainScopeTable.getOrPut(this) { MainScope() }

    /**
     * 在[MainScope]协程上下文环境下调用指定的[block]
     * Note:这里并没有启动一个协程,所以不能直接在[block]中调用suspend function
     * @see withLaunchedMainScope
     */
    fun <T> withMainScope(block: CoroutineScope.() -> T): T {
        return with(mainScope, block)
    }

    /**
     * 在[MainScope]协程上下文环境下使用[launch]启动了一个协程并调用指定的[block]
     */
    fun withLaunchedMainScope(block: suspend CoroutineScope.() -> Unit) {
        withMainScope {
            launch(block = block)
        }
    }

    /**
     * 用于在[View]的Click事件回调中使用协程, 也可以使用[onClickDisposable]达到相同的目的,
     * 区别在于后者可以自由指定的协程上下文
     */
    fun <V : View> V.onClick(block: suspend CoroutineScope.(v: V) -> Unit) {
        setOnClickListener {
            withLaunchedMainScope {
                block(this@onClick)
            }
        }
    }

    companion object {

        private val mainScopeTable: IdentityHashMap<MainCoroutineScope, CoroutineScope> by lazy { IdentityHashMap<MainCoroutineScope, CoroutineScope>() }

        internal fun cancelFrom(owner: LifecycleOwner) {
            (owner as? MainCoroutineScope)?.apply {
                mainScopeTable.remove(this)?.cancel()
            }
        }
    }
}