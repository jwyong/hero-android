package com.ntu.hero.global

import android.content.Context

import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.module.AppGlideModule

/**
 * Created by ibrahim on 26/02/2018.
 */

@GlideModule
class GlideHelper : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)

        val memoryCacheSizeBytes = 1024 * 1024 * 200 // set ram 200mb
        builder.setMemoryCache(LruResourceCache(memoryCacheSizeBytes.toLong()))
    }
}
