package com.example.arm.ext

import com.example.arm.base.BaseApplication
import org.kodein.di.DI
import org.kodein.di.DIAware

/**
 *  author : yanghaozhang
 *  date : 2020/9/17 15:40
 *  description :
 */
interface AppDIAware : DIAware {
    override val di: DI
        get() = BaseApplication.INSTANCE.di
}