package com.example.arm.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

@Deprecated("use LiveDataKt liveData.observe(owner,(T)->Unit)")
fun <T> LifecycleOwner.observe(liveData: LiveData<T>, observer: (t: T) -> Unit) {
    liveData.observe(this, Observer {
        it?.let { t: T ->
            observer(t)
        }
    })
}
