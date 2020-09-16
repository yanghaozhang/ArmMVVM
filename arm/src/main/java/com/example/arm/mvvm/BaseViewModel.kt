package com.example.arm.mvvm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.x.di

/**
 *  author : yanghaozhang
 *  date : 2020/9/16 10:55
 *  description :
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application), DIAware {

    override val di: DI by di()


}