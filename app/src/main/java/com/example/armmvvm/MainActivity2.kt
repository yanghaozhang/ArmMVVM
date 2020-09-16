package com.example.armmvvm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.di
import org.kodein.di.instance
import org.kodein.di.newInstance
import timber.log.Timber

class MainActivity2 : AppCompatActivity(), DIAware {

    override val di: DI by di()

    val mGsonBuilder: GsonBuilder by instance()

    val httpUrl: HttpUrl? by newInstance { instance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        applicationContext
        Timber.tag("MainActivity2").d("onCreate() called with: savedInstanceState = $savedInstanceState   %s ", "$mGsonBuilder")
        Timber.tag("MainActivity2").d("onCreate() called with: savedInstanceState = $savedInstanceState   %s ", "$httpUrl")
    }
}