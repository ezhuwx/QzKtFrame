package com.ez.frameKt.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ez.frameKt.base.BaseApplication
import com.ez.kotlin.frame.R
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream

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
fun Bitmap?.base64(): ByteArray? {
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

/**
 * 获取图片URI
 */
fun String?.pathUri(context: Context, filePath: String? = null): Uri? {
    //文件地址
    val filePathDefault =
        "${Environment.DIRECTORY_PICTURES}/${BaseApplication.instance.resources.getString(R.string.app_name)}"
    //ContentValues
    val contentValues = ContentValues()
    contentValues.put(
        MediaStore.Images.ImageColumns.DISPLAY_NAME,
        this ?: System.currentTimeMillis().toString()
    )
    contentValues.put(MediaStore.Images.ImageColumns.MIME_TYPE, "image/png")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, filePath ?: filePathDefault)
    }
    var uri: Uri? = null
    try {
        uri = context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
    } catch (_: Exception) {
    }
    return uri
}

/**
 * 保存图片
 */
fun Bitmap?.saveToGallery(
    context: Context,
    fileName: String? = null,
    filePath: String? = null
): Boolean {
    if (this != null) {
        val uri = (fileName ?: System.currentTimeMillis().toString()).pathUri(context, filePath)
        if (uri != null) {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
                return true
            }
        }
    }
    return false
}
