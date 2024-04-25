package com.qz.frame.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.qz.frame.base.BaseApplication
import com.qz.kotlin.frame.R
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

fun glideWith(
    context: AppCompatActivity,
    radius: Int? = null,
    @DrawableRes errorResId: Int = R.drawable.image_error,
    skipMemoryCache: Boolean = false,
    diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.DATA
): RequestManager {
    try {
        if (isValidContext(context)) {
            return Glide.with(context)
                .setDefaultRequestOptions(
                    RequestOptions()
                        .transform(RoundedCorners(radius.pxDp(context) ?: 1))
                        .placeholder(randomColorHolder())
                        .skipMemoryCache(skipMemoryCache)
                        .error(errorResId)
                        .diskCacheStrategy(diskCacheStrategy)
                )
        }
    } catch (e: IllegalArgumentException) {
        logW(e.message)
    }
    return glideWith(BaseApplication.mContext)
}

fun glideWith(
    context: Context,
    radius: Int? = null,
    @DrawableRes errorResId: Int = R.drawable.image_error,
    skipMemoryCache: Boolean = false,
    diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.DATA
): RequestManager {
    return Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .transform(RoundedCorners(radius.pxDp(context) ?: 1))
            .placeholder(randomColorHolder())
            .skipMemoryCache(skipMemoryCache)
            .error(errorResId)
            .diskCacheStrategy(diskCacheStrategy)
    )
}

/**
 * 通用加载
 */
fun ImageView.glideLoad(
    path: String?, radius: Int? = null,
    skipMemoryCache: Boolean = false,
    diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.DATA
) {
    if (path.isVideo()) loadVideoCover(path)
    else glideWith(
        context,
        radius,
        skipMemoryCache = skipMemoryCache,
        diskCacheStrategy = diskCacheStrategy
    ).load(path).into(this)
}

/**
 * 封面加载
 */
fun ImageView.loadVideoCover(
    url: String?,
    radius: Int? = null,
    skipMemoryCache: Boolean = false,
    diskCacheStrategy: DiskCacheStrategy = DiskCacheStrategy.DATA
) {
    if (url != null) {
        val retriever = MediaMetadataRetriever()
        if (url.startsWith("http")) {
            //setDataSource(String uri,  Map<String, String> headers) 是获取视频链接
            retriever.setDataSource(url, emptyMap())
        } else {
            //setDataSource(String path)是获取视频文件
            retriever.setDataSource(url)
        }
        //获得第1帧图片，这里的第一个参数以微秒为单位
        val bitmap =
            retriever.getFrameAtTime(1 * 1000 * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        retriever.release()
        //加载
        glideWith(
            context,
            radius,
            skipMemoryCache = skipMemoryCache,
            diskCacheStrategy = diskCacheStrategy
        ).load(bitmap).into(this)
    }
}


