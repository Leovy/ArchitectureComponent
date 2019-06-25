package com.nikeorever.architecturecomponent

import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun onClick(block: CoroutineScope.(v: View) -> Unit){

}

open class KtClass {}

interface KtInterface

interface CompanionInterface1 {
    companion object Key: KtClass()
}

class CompaionClass1 : CompanionInterface1 {
}

interface CompanionInterface2 {
    companion object Key2: KtInterface {
        @JvmField
        val answer: Int = 42

        fun sayHello() {
            println("Hello, world!")
        }
    }
}

class CompaionClass2 : CompanionInterface2 {

}


fun x(x: String?){
    if (!x.isNullOrEmpty()) {
        x.toCharArray()
    }

    require(x != null)
    x.toCharArray()

}

@UseExperimental(ExperimentalContracts::class)
fun require(condition: Boolean) {
    contract {
        returns() implies condition
    }

    if (!condition) throw IllegalStateException()
}


fun main() {
    (1 .. 10).associateWith { it.toString().repeat(5).capitalize() }
    CompanionInterface2.Key2
    onClick { v: View ->
        launch {

        }
        v.addOnAttachStateChangeListener(null)
    }
}