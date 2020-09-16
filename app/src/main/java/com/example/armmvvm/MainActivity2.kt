package com.example.armmvvm

import android.os.Bundle
import com.example.arm.base.BaseActivity
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import org.kodein.di.DI
import org.kodein.di.android.di
import org.kodein.di.instance
import org.kodein.di.newInstance
import timber.log.Timber

class MainActivity2 : BaseActivity() {

    override val di: DI by di()

    val mGsonBuilder: GsonBuilder by instance()

    val httpUrl: HttpUrl? by newInstance { instance() }

    override fun initView(savedInstanceState: Bundle?): Int = R.layout.activity_main2

    override fun initData(savedInstanceState: Bundle?) {
        Timber.tag("MainActivity2").d("onCreate() called with: savedInstanceState = $savedInstanceState   %s ", "$mGsonBuilder")
        Timber.tag("MainActivity2").d("onCreate() called with: savedInstanceState = $savedInstanceState   %s ", "$httpUrl")
    }
}