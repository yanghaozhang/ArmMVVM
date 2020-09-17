package com.example.armmvvm

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.arm.base.BaseActivity
import com.example.arm.di.GlobalConfigModule
import com.example.arm.ext.DIViewModelFactory
import com.example.arm.http.ErrorListener
import com.example.arm.integration.IRepositoryManager
import com.google.gson.GsonBuilder
import okhttp3.HttpUrl
import org.kodein.di.*
import org.kodein.di.DI.Companion.defaultFullDescriptionOnError
import org.kodein.di.android.di
import org.kodein.di.android.retainedSubDI
import timber.log.Timber


/**
 *  author : yanghaozhang
 *  date : 2020/9/9 11:13
 *  description :
 */
class MainActivity : BaseActivity() {

    override val di: DI by retainedSubDI(di()) {
        bind<MainViewModel>() with singleton {
            Timber.tag("MainActivity").d("null() called   %s ", "-------$context-------")
            MainViewModel(instance())
        }
    }

    val mHttpUrl: HttpUrl by instance()

    val mRepositoryManager: IRepositoryManager by instance()

    // 直接使用newInstance而不是di.newInstance,因为后者将立即实现,而此时Activity未能完全初始化,applicationContext为null,报NPE
    val mErrorListener: ErrorListener? by newInstance { instance<GlobalConfigModule>().mErrorListener }

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
        Timber.tag("MainActivity")
            .d("initData() called with: savedInstanceState = $savedInstanceState   %s ", "$mRepositoryManager")
    }

    fun onclick(view: View) {
        startActivity(Intent(this, MainActivity2::class.java))
    }

}