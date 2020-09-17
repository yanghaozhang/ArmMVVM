package com.example.arm.mvvm

import com.example.arm.ext.AppDIAware
import com.example.arm.integration.IRepositoryManager
import org.kodein.di.instance

/**
 *  author : yanghaozhang
 *  date : 2020/9/17 15:32
 *  description :
 */
open class BaseModel : IModel, AppDIAware {

    val mRepositoryManager: IRepositoryManager by instance()

    override fun onDestroy() {
        mRepositoryManager.clearAllCache()
    }
}