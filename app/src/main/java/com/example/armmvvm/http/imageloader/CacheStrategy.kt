package com.example.armmvvm.http.imageloader

import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Incremental change is better than ambitious failure.
 *
 * @author : [MysticCoder](http://mysticcoder.coding.me)
 * @date : 2019/4/29
 * @desc :0对应DiskCacheStrategy.all,1对应DiskCacheStrategy.NONE,2对应DiskCacheStrategy.SOURCE,3对应DiskCacheStrategy.RESULT
 * see [com.bumptech.glide.load.engine.DiskCacheStrategy]
 */
interface CacheStrategy {
    @IntDef(ALL, NONE, RESOURCE, DATA, AUTOMATIC)
    @Retention(RetentionPolicy.SOURCE)
    annotation class Strategy companion object {
        const val ALL = 0
        const val NONE = 1
        const val RESOURCE = 2
        const val DATA = 3
        const val AUTOMATIC = 4
    }
}
