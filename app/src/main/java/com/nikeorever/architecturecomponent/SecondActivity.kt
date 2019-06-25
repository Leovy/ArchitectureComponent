package com.nikeorever.architecturecomponent

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.android.synthetic.main.activity_second.*
import kotlinx.coroutines.*

class SecondActivity : AppCompatActivity(), AndroidMainScope, CoroutineScope by MainScope() {

    private val lazySecondViewModel: SecondViewModel by ViewModelLazy<SecondViewModel>(
        SecondViewModel::class,
        { viewModelStore },
        { ViewModelProvider.AndroidViewModelFactory.getInstance(application) }

    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val secondViewModel = SecondViewModel.instance(this)

//        secondViewModel.liveData.observe(this, Observer {
//            textView.text = it.toString()
//        })

//        secondViewModel.commentsLiveData.observe(this, Observer {
//            textView.text = it.toString()
//        })

//        secondViewModel.postsStrLiveData.observe(this, Observer {
//            textView.text = it
//        })

        withLaunchedCoroutineScope {
            val postsStrLiveData = lazySecondViewModel.postsStrLiveData.observeAwait(this@SecondActivity)
            textView.text = postsStrLiveData
        }

//        textView.postDelayed({
//            finish()
//        }, 500)

        launch {
            try {
                log(TAG, "[mainScope] launched")
                delay(Long.MAX_VALUE)
            } finally {
                log(TAG, "[mainScope] cancelled")
            }
        }

        withLaunchedCoroutineScope {
            try {
                log(TAG, "[AndroidMainScope] launched")
                delay(Long.MAX_VALUE)
            } finally {
                log(TAG, "[AndroidMainScope] cancelled")
            }
        }

        textView.onClick {
            val deferred = async {
                try {
                    log(TAG, "[AndroidMainScope-TextView] launched")
                    delay(4000)
                    log(TAG, "[AndroidMainScope-TextView] start return result")
                    coroutineContext.toString()
                } catch (e: CancellationException) {
                    log(TAG, "[AndroidMainScope-TextView] cancelled")
                    "CancellationException"
                } finally {
                    log(TAG, "[AndroidMainScope-TextView] finally")
                }
            }
            try {
                (it as TextView).text = deferred.await()
            } catch (e: CancellationException) {
                log(TAG, "[AndroidMainScope-TextView-await] cancelled")
            }
        }

        textView.onClickDisposable { v: TextView ->
            val deferred = async {
                try {
                    log(TAG, "[AutoDisposable-TextView] launched")
                    delay(Long.MAX_VALUE)
                    log(TAG, "[AutoDisposable-TextView] start return result")
                    coroutineContext.toString()
                } catch (e: CancellationException) {
                    log(TAG, "[AutoDisposable-TextView] cancelled")
                    "CancellationException"
                } finally {
                    log(TAG, "[AutoDisposable-TextView] finally")
                }
            }
            try {
                v.text = deferred.await()
            } catch (e: CancellationException) {
                log(TAG, "[AutoDisposable-TextView-await] cancelled")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }


    companion object {
        private const val TAG: String = "SecondActivity"
    }
}
