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

    var url: String? = null
        protected set

    var imageView: ImageView? = null
        protected set

    //占位符
    var placeholder = 0
        protected set

    //错误占位符
    var errorPic = 0
        protected set
}