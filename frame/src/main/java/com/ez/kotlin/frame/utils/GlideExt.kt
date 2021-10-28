package com.ez.kotlin.frame.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.base.BaseApplication
import java.lang.IllegalArgumentException
import java.util.*

/**
 * @author : ezhuwx
 * Describe :Glide工具类
 * Designed on 2021/10/28
 * E-mail : ezhuwx@163.com
 * Update on 13:35 by ezhuwx
 */

private val colors = intArrayOf(
    R.color.random_color_1, R.color.random_color_2,
    R.color.random_color_3, R.color.random_color_4, R.color.random_color_5,
    R.color.random_color_6, R.color.random_color_7, R.color.random_color_8,
    R.color.random_color_9, R.color.random_color_10
)

private fun randomColorHolder(): Int {
    val random = Random()
    return colors[random.nextInt(10)]
}

private fun isValidContext(context: AppCompatActivity?): Boolean {
    return context != null && !context.isDestroyed && !context.isFinishing
}

fun glideWith(context: AppCompatActivity?): RequestManager {
    try {
        if (isValidContext(context)) {
            return Glide.with(context!!)
                .setDefaultRequestOptions(
                    RequestOptions()
                        .placeholder(randomColorHolder())
                        .error(R.drawable.image_error)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                )
        }
    } catch (e: IllegalArgumentException) {
        logW(e.message)
    }
    return glideWith(BaseApplication.mContext)
}

fun glideWith(context: Context?): RequestManager {
    return Glide.with(context!!).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(randomColorHolder())
            .error(R.drawable.image_error)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
    )
}
