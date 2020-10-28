package com.example.armmvvm.http.imageloader

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.example.arm.http.imageloader.ImageConfig

class ImageConfigImpl : ImageConfig() {
    //0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
    var cacheStrategy: Int = CacheStrategy.ALL

    //请求 url 为空,则使用此图片作为占位符
    var fallback: Int = 0

    //图片每个圆角的大小
    var imageRadius: Int = 0

    //高斯模糊值, 值越大模糊效果越大
    var blurvarue: Int = 0

    var loadType: Int = 0

    override var url: String? = null
        set(value) {
            loadType = LOAD_TYPE_URL
            field = value
        }
    var loadResId: Int = 0
        set(value) {
            loadType = LOAD_TYPE_RES_ID
            field = value
        }
    var loadBitmap: Bitmap? = null
        set(value) {
            loadType = LOAD_TYPE_BITMAP
            field = value
        }
    var loadDrawable: Drawable? = null
        set(value) {
            loadType = LOAD_TYPE_DRAWABLE
            field = value
        }
    var placeHoldDrawable: Drawable? = null
    var errorDrawable: Drawable? = null

    //glide用它来改变图形的形状
    var transformation: BitmapTransformation? = null

    var imageViews: Array<ImageView>? = null

    //是否使用淡入淡出过渡动画
    var isCrossFade: Boolean = false

    //是否将图片剪切为 CenterCrop
    var isCenterCrop: Boolean = false

    //是否将图片剪切为圆形
    var isCircle: Boolean = false

    //清理内存缓存
    var isClearMemory: Boolean = false

    //清理本地缓存
    var isClearDiskCache: Boolean = false

    val isBlurImage: Boolean
        get() = blurvarue > 0

    val isImageRadius: Boolean
        get() = imageRadius > 0


    companion object {
        const val LOAD_TYPE_URL = 0
        const val LOAD_TYPE_RES_ID = 1
        const val LOAD_TYPE_BITMAP = 2
        const val LOAD_TYPE_DRAWABLE = 3
    }
}

fun ImageConfigImplBuilder(init:ImageConfigImpl.()->Unit):ImageConfigImpl {
    val imageConfigImpl = ImageConfigImpl()
    imageConfigImpl.init()
    return imageConfigImpl
}