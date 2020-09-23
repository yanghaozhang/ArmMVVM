package com.example.arm.integration

import android.content.Context
import retrofit2.Retrofit

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 14:46
 *  description :
 */
interface IRepositoryManager {

    /**
     * 根据传入的 Class 获取对应的 Retrofit service
     * 根据传入tag不同,使用不同retrofit
     *
     * @param service Retrofit service class
     * @param <T>     Retrofit service 类型
     * @return Retrofit service
    </T> */
    fun <T> obtainRetrofitService(service: Class<T>, tag: String? = null): T

    /**
     * 清理所有缓存
     */
    fun clearAllCache()

    /**
     * 获取 [Context]
     *
     * @return [Context]
     */
    val context: Context

    interface ObtainServiceDelegate {
        fun <T> createRetrofitService(retrofit: Retrofit?, serviceClass: Class<T>?): T?
    }
}