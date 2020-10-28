package com.example.arm.http.imageloader

import android.content.Context

/**
 * ================================================
 * 图片加载策略,实现 [BaseImageLoaderStrategy]
 * 并通过 [ImageLoader.setLoadImgStrategy] 配置后,才可进行图片请求
 *
 *
 * Created by JessYan on 8/5/2016 15:50
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
interface BaseImageLoaderStrategy<T : ImageConfig> {
    /**
     * 加载图片
     *
     * @param ctx    [Context]
     * @param config 图片加载配置信息
     */
    fun loadImage(ctx: Context, config: T)

    /**
     * 停止加载
     *
     * @param ctx    [Context]
     * @param config 图片加载配置信息
     */
    fun clear(ctx: Context, config: T)
}