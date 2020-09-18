package com.example.armmvvm.http.imageloader

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.cache.DiskLruCacheWrapper
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.arm.BuildConfig
import com.example.arm.http.imageloader.BaseImageLoaderStrategy
import com.example.arm.http.imageloader.glide.ArmsForkGlide
import com.example.arm.http.imageloader.glide.GlideAppliesOption
import com.example.arm.http.imageloader.glide.GlideRequest
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File

/**
 * glide 是一个强大的加载图片工具,如果要将glide所有功能都强加到ImageConfig中去,无疑是画蛇添足
 * 最好的集成是将项目所需加入到框架中去,如果后续有需求,就根据需求而变动加载策略,不能幻想一步到位
 */
class GlideImageLoaderStrategy : BaseImageLoaderStrategy<ImageConfigImpl>, GlideAppliesOption {
    override fun loadImage(ctx: Context, config: ImageConfigImpl) {
        requireNotNull(config.imageView) { "ImageView is required" }
        val glideRequests = ArmsForkGlide.with(ctx)
        val load: GlideRequest<Drawable>
        load = when (config.loadType) {
            ImageConfigImpl.LOAD_TYPE_RES_ID -> {
                require(config.loadResId > 0) { "ResId is required" }
                glideRequests.load(config.loadResId)
            }
            ImageConfigImpl.LOAD_TYPE_BITMAP -> {
                requireNotNull(config.loadBitmap) { "Bitmap is required" }
                glideRequests.load(config.loadBitmap)
            }
            ImageConfigImpl.LOAD_TYPE_DRAWABLE -> {
                requireNotNull(config.loadDrawable) { "Drawable is required" }
                glideRequests.load(config.loadDrawable)
            }
            ImageConfigImpl.LOAD_TYPE_URL -> {
                requireNotNull(config.url) { "URL is required" }
                glideRequests.load(config.url)
            }
            else -> {
                requireNotNull(config.url) { "URL is required" }
                glideRequests.load(config.url)
            }
        }
        load.run {
            diskCacheStrategy(mCacheStrategy[config.cacheStrategy])

            if (config.placeholder != 0) {
                placeholder(config.placeholder)
            } else if (config.placeHoldDrawable != null) {
                placeholder(config.placeHoldDrawable)
            }
            //设置请求 url 为空图片
            if (config.fallback != 0) {
                fallback(config.fallback)
            }
            if (config.errorPic != 0) {
                error(config.errorPic)
            } else if (config.errorDrawable != null) {
                error(config.errorDrawable)
            }
            if (config.isCrossFade) {
                transition(DrawableTransitionOptions.withCrossFade())
            }
            if (config.isCenterCrop) {
                centerCrop()
            }
            if (config.isCircle) {
                circleCrop()
            }
            if (config.isImageRadius) {
                transform(RoundedCorners(config.imageRadius))
            }
            if (config.isBlurImage) {
                transform(BlurTransformation(config.blurvarue))
            }
            if (config.transformation != null) {
                transform(config.transformation)
            }
            into(config.imageView!!)
        }

    }

    override fun clear(ctx: Context, config: ImageConfigImpl) {
        requireNotNull(config.imageView) { "ImageView is required" }
        if (config.imageView != null) {
            ArmsForkGlide.get(ctx).requestManagerRetriever[ctx].clear(config.imageView!!)
        }
        config.imageViews?.forEach { imageView ->
            ArmsForkGlide.get(ctx).requestManagerRetriever[ctx].clear(imageView)
        }
        if (config.isClearDiskCache) {
            Completable.fromAction { ArmsForkGlide.get(ctx).clearDiskCache() }.subscribeOn(Schedulers.io()).subscribe()
        }
        if (config.isClearMemory) {
            Completable.fromAction { ArmsForkGlide.get(ctx).clearMemory() }.subscribeOn(AndroidSchedulers.mainThread())
                .subscribe()
        }
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setDiskCache {
            DiskLruCacheWrapper.create(
                File(context.externalCacheDir, GLIDE_FILE_PATH),
                IMAGE_DISK_CACHE_MAX_SIZE.toLong()
            )
        }
        builder.addGlobalRequestListener(object : RequestListener<Any?> {
            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Any?>, isFirstResource: Boolean): Boolean {
                Timber.tag("Glide-G-LoadFailed").e(e)
                Timber.tag("Glide-Model").d(model.toString())
                return false
            }

            override fun onResourceReady(
                resource: Any?,
                model: Any,
                target: Target<Any?>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        /* RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(22)
                .error(22)
                .centerCrop()
                .
        builder.setDefaultRequestOptions(requestOptions);*/
        //Glide会在VERBOSE级别打印成功日志,在DEBUG级别打印错误日志
        builder.setLogLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.INFO)
        builder.setDiskCacheExecutor(
            GlideExecutor
                .newDiskCacheBuilder()
                .setUncaughtThrowableStrategy { t: Throwable? -> Timber.tag("Glide-ThrowableStrategy").e(t) }
                .build())
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        Timber.tag(TAG).d("registerComponents")
    }

    companion object {
        private const val TAG = "GlideImageLoaderStrateg"
        private val mCacheStrategy =
            arrayOf(DiskCacheStrategy.ALL, DiskCacheStrategy.NONE, DiskCacheStrategy.RESOURCE, DiskCacheStrategy.DATA)
        const val IMAGE_DISK_CACHE_MAX_SIZE = 250 * 1024 * 1024 //图片缓存文件最大值为250Mb
        const val GLIDE_FILE_PATH = "GLIDE"
    }
}