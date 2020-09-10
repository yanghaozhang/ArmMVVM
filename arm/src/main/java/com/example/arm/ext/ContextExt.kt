package com.example.arm.ext

import android.content.Context
import org.kodein.di.DI
import org.kodein.di.DIAware

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 18:58
 *  description :
 */

val Context.di: DI
    get() {
        return (this as DIAware).di
    }
