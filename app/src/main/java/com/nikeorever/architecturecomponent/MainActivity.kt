package com.nikeorever.architecturecomponent

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        toSecondButton.setOnClickListener {
//            startActivity(Intent(this, SecondActivity::class.java))
//            MyIntentService.start(this@MainActivity)
//              runWork(applicationContext)
            testCompletableDeferred()
        }
    }
}


