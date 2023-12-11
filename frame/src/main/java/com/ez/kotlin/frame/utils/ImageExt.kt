package com.ez.kotlin.frame.utils

import android.graphics.Bitmap
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2023/11/22
 * E-mail : ezhuwx@163.com
 * Update on 16:06 by ezhuwx
 */
fun View.bitmapFromView(): Bitmap? {
    return try {
        isDrawingCacheEnabled = true
        cacheBitmap()
    } catch (throwable: Throwable) {
        null
    }
}

fun View.cacheBitmap(): Bitmap? = try {
    setNoScroll(this)
    destroyDrawingCache()
    measure(
        View.MeasureSpec.makeMeasureSpec(
            View.MeasureSpec.UNSPECIFIED,
            View.MeasureSpec.UNSPECIFIED
        ), View.MeasureSpec.makeMeasureSpec(
            View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED
        )
    )
    layout(0, 0, measuredWidth, measuredHeight)
    var bitmap: Bitmap
    if (drawingCache.also { bitmap = it } != null) bitmap.copy(
        Bitmap.Config.ARGB_8888,
        false
    ) else null
} catch (throwable: Throwable) {
    throwable.printStackTrace()
    null
}

/**
 * bitmap转为base64
 *
 * @param bitmap
 * @return
 */
fun Bitmap?.base64():ByteArray? {
    var result: ByteArray? = null
    var byteOS: ByteArrayOutputStream? = null
    try {
            byteOS = ByteArrayOutputStream()
            this?.compress(Bitmap.CompressFormat.JPEG, 100, byteOS)
            byteOS.flush()
            byteOS.close()
            result = byteOS.toByteArray()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            byteOS?.flush()
            byteOS?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return result
}

private fun setNoScroll(view: View) {
    if (view !is ViewGroup) {
        if (view is TextView) {
            view.setHorizontallyScrolling(false)
        }
    } else {
        for (index in 0 until view.childCount) {
            setNoScroll(view.getChildAt(index))
        }
    }
}
