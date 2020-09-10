package com.example.arm.http

import android.content.Context
import com.example.arm.http.imageloader.ImageConfig

/**
 *  author : yanghaozhang
 *  date : 2020/9/10 15:58
 *  description :
 */
interface BaseImageLoaderStrategy<in T : ImageConfig> {
    /**
     * 加载图片
     *
     * @param ctx    [Context]
     * @param config 图片加载配置信息
     */
    fun loadImage(ctx: Context?, config: T?)

    /**
     * 停止加载
     *
     * @param ctx    [Context]
     * @param config 图片加载配置信息
     */
    fun clear(ctx: Context?, config: T?)
}