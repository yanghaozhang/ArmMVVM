package com.example.arm.http.imageloader.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry

interface GlideAppliesOption {
    fun applyOptions(context: Context, builder: GlideBuilder)
    fun registerComponents(context: Context, glide: Glide, registry: Registry)
}