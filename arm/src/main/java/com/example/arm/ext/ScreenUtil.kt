package com.example.arm.ext

import android.content.res.Resources
import android.util.TypedValue

/**
 *  author : yanghaozhang
 *  date : 2020/9/16 14:14
 *  description :
 */
val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )