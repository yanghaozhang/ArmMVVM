package com.example.armmvvm

import android.os.Bundle
import androidx.activity.viewModels
import com.example.arm.base.BaseActivity
import com.example.arm.ext.DIViewModelFactory
import com.example.arm.http.ErrorListener
import okhttp3.HttpUrl
import org.kodein.di.android.di
import org.kodein.di.android.retainedSubDI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import timber.log.Timber


/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:13
 *  description :
 */
class MainActivity() : BaseActivity() {

    override val di by retainedSubDI(di()) {
        bind<MainActivity>() with singleton { this@MainActivity }
        bind<MainViewModel>() with singleton { MainViewModel(instance()) }
    }

    val mHttpUrl: HttpUrl by instance()

    val mErrorListener: ErrorListener? by instance()

    val mainViewModel: MainViewModel by viewModels() {
        DIViewModelFactory(di)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        mainViewModel.printMainActivity()
        Timber.tag("MainActivity").d(mHttpUrl.url().toString())
        Timber.tag("MainActivity").d(mErrorListener?.javaClass?.simpleName ?: "not exist")
    }

}