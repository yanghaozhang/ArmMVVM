package com.example.arm.http.imageloader

import android.widget.ImageView

/**
 * ================================================
 * 这里是图片加载配置信息的基类,定义一些所有图片加载框架都可以用的通用参数
 *
 *
 * Created by JessYan on 8/5/16 15:19
 * [Contact me](mailto:jess.yan.effort@gmail.com)
 * [Follow me](https://github.com/JessYanCoding)
 * ================================================
 */
open class ImageConfig {

    open var url: String? = null

    var imageView: ImageView? = null

    //占位符
    var placeholder = 0
        protected set

    //错误占位符
    var errorPic = 0
        protected set
}