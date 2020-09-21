/*
 * Copyright 2017 JessYan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.armmvvm.http.imageloader

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.example.arm.http.imageloader.ImageConfig

/**
 * ================================================
 * 这里存放图片请求的配置信息,可以一直扩展字段,如果外部调用时想让图片加载框架
 * 做一些操作,比如清除缓存或者切换缓存策略,则可以定义一个 int 类型的变量,内部根据 switch(int) 做不同的操作
 * 其他操作同理
 *
 *
 * Created by JessYan on 8/5/16 15:19
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
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