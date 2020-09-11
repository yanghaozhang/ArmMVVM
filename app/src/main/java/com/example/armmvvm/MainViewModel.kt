package com.example.armmvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.di
import timber.log.Timber

/**
 *  author : yanghaozhang
 *  date : 2020/9/11 1:20
 *  description :
 */
class MainViewModel(application: Application) : AndroidViewModel(application), DIAware {

    fun printMainActivity() {
        Timber.tag("MainViewModel").d("")
    }

    override val di: DI by di()

}