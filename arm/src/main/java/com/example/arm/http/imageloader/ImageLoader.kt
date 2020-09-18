package com.example.arm.http.imageloader

import android.content.Context
import com.example.arm.ext.AppDIAware

class ImageLoader<T : ImageConfig>(val imageLoaderStrategy: BaseImageLoaderStrategy<T>) : AppDIAware {

    /**
     * 加载图片
     *
     * @param ctx    [Context]
     * @param config 图片加载配置信息
     */
    fun loadImage(ctx: Context, config: T) {
        imageLoaderStrategy.loadImage(ctx, config)
    }

    /**
     * 停止加载
     *
     * @param ctx    [Context]
     * @param config 图片加载配置信息
     */
    fun clear(ctx: Context, config: T) {
        imageLoaderStrategy.clear(ctx, config)
    }
}