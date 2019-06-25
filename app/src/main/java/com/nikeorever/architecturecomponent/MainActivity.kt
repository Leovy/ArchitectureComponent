package com.nikeorever.architecturecomponent

import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        toSecondButton.setOnClickListener {
//            startActivity(Intent(this, SecondActivity::class.java))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this@MainActivity, MyIntentService::class.java))
            }
        }


        val job = GlobalScope.launch(context = Dispatchers.Main) {
            val bitmap = suspendLoadImage()
            ImageView(this@MainActivity).setImageBitmap(bitmap)
        }



        job.cancel()

        loadImage(object : ImageLoadCallback{
            override fun onComplete(bitmap: Bitmap) {
                ImageView(this@MainActivity).setImageBitmap(bitmap)
            }

            override fun onErro() {
            }
        })


    }


    suspend fun suspendLoadImage(): Bitmap{
        return suspendCancellableCoroutine { ctn ->
            ctn.invokeOnCancellation {
                //取消网络请求
            }
            loadImage(object : ImageLoadCallback {
                override fun onComplete(bitmap: Bitmap) {
                    ctn.resume(bitmap)
                }

                override fun onErro() {
                    ctn.resumeWithException(Exception())
                }

            })
        }
    }

    fun loadImage(callback: ImageLoadCallback) {

    }

    interface ImageLoadCallback {
        fun onComplete(bitmap: Bitmap)
        fun onErro()
    }
}


