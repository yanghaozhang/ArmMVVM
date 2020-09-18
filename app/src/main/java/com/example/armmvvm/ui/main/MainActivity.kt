package com.example.armmvvm.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.example.arm.base.BaseActivity
import com.example.arm.di.GlobalConfigModule
import com.example.arm.ext.DIViewModelFactory
import com.example.arm.http.ErrorListener
import com.example.arm.integration.IRepositoryManager
import com.example.arm.util.TestUtil
import com.example.armmvvm.R
import com.example.armmvvm.ui.test.TestActivity
import okhttp3.HttpUrl
import org.kodein.di.*
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
            MainViewModel(MainModel())
        }
    }

    val mainViewModel: MainViewModel by viewModels() {
        DIViewModelFactory(di)
    }

    override fun initView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_main
    }

    override fun initData(savedInstanceState: Bundle?) {
        mainViewModel.printMainActivity()
    }

    fun onclick(view: View) {
        startActivity(Intent(this, TestActivity::class.java))
    }

}