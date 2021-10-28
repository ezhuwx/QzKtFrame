package com.ez.kotlin.frame.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.ez.kotlin.frame.base.BaseApplication
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException

/**
 * @author : ezhuwx
 * Describe : 图片工具类
 * Designed on 2021/10/28
 * E-mail : ezhuwx@163.com
 * Update on 14:20 by ezhuwx
 */
private const val MAX_PIXEL: Int = 3000

/**
 * 质量压缩
 *
 * @param image
 * @param maxSize
 */
fun compressImage(image: Bitmap?, maxSize: Int, isPng: Boolean): Bitmap? {
    val os = ByteArrayOutputStream()
    // scale
    var options = 100
    // Store the bitmap into output stream(no compress)
    image!!.compress(
        if (isPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
        options,
        os
    )
    if (!isPng && maxSize > 0) {
        // Compress by loop
        while (os.toByteArray().size / 1024 > maxSize) {
            // Clean up os
            os.reset()
            // interval 10
            options -= 10
            image.compress(Bitmap.CompressFormat.JPEG, options, os)
        }
    }
    var bitmap: Bitmap? = null
    val b = os.toByteArray()
    if (b.isNotEmpty()) {
        bitmap = BitmapFactory.decodeByteArray(b, 0, b.size)
    }
    return bitmap
}

/**
 * 质量压缩
 *
 * @param image
 * @param maxSize
 */
fun compressImageByte(image: Bitmap, maxSize: Int, isPng: Boolean): ByteArray? {
    val os = ByteArrayOutputStream()
    // scale
    var options = 100
    // Store the bitmap into output stream(no compress)
    image.compress(
        if (isPng) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
        options,
        os
    )
    if (!isPng && maxSize > 0) {
        // Compress by loop
        while (os.toByteArray().size / 1024 > maxSize) {
            // Clean up os
            os.reset()
            // interval 10
            options -= 10
            image.compress(Bitmap.CompressFormat.JPEG, options, os)
        }
    }
    return os.toByteArray()
}

@Throws(IOException::class)
fun decodeSampledBitmapFromFile(imageFile: File, reqWidth: Int, reqHeight: Int): Bitmap? {
    // First decode with inJustDecodeBounds=true to check dimensions
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(imageFile.absolutePath, options)

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false
    var scaledBitmap = BitmapFactory.decodeFile(imageFile.absolutePath, options)

    //check the rotation of the image and display it properly
    val exif = ExifInterface(imageFile.absolutePath)
    val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
    val matrix = Matrix()
    when (orientation) {
        6 -> {
            matrix.postRotate(90f)
        }
        3 -> {
            matrix.postRotate(180f)
        }
        8 -> {
            matrix.postRotate(270f)
        }
    }
    scaledBitmap = Bitmap.createBitmap(
        scaledBitmap,
        0,
        0,
        scaledBitmap.width,
        scaledBitmap.height,
        matrix,
        true
    )
    return scaledBitmap
}

private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    // Raw height and width of image
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

/**
 * 矫正图片解析角度
 */
fun correctBitmapDegree(path: String?, newWidth: Int, newHeight: Int): Bitmap? {
    val degree = readPictureDegree(path)
    return rotateBitmap(path, degree, newWidth, newHeight)
}

/**
 * 读取图片角度
 */
private fun readPictureDegree(path: String?): Int {
    var degree = 0
    try {
        val exifInterface = ExifInterface(path!!)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> degree = 90
            ExifInterface.ORIENTATION_ROTATE_180 -> degree = 180
            ExifInterface.ORIENTATION_ROTATE_270 -> degree = 270
            else -> {
            }
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return degree
    }
    return degree
}

/**
 * 传入bitmap和图片的旋转角度，即可得到矫正后的图片
 */
private fun rotateBitmap(filePath: String?, degree: Int, newWidth: Int, newHeight: Int): Bitmap? {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(filePath, options)
    return if (degree != 0 || options.outHeight > MAX_PIXEL || options.outWidth > MAX_PIXEL) {
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight)
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(filePath, options)
        if (degree == 0) {
            val newBitmap = compressImage(bitmap, 1024, false)
            bitmap.recycle()
            newBitmap
        } else {
            val matrix = Matrix()
            matrix.setRotate(
                degree.toFloat(),
                (bitmap.width / 2).toFloat(),
                (bitmap.height / 2).toFloat()
            )
            val result =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            result
        }
    } else {
        BitmapFactory.decodeFile(filePath)
    }
}

/**
 * bitmap转为base64
 * @param bitmap
 */
fun bitmapToBase64(bitmap: Bitmap?): String? {
    var result: String? = null
    var baos: ByteArrayOutputStream? = null
    try {
        if (bitmap != null) {
            baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            baos.flush()
            baos.close()
            val bitmapBytes = baos.toByteArray()
            result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            if (baos != null) {
                baos.flush()
                baos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return result
}

/**
 * TODO 从View内获取Bitmap
 *
 * @param view
 * @return
 */
fun bitmapFromView(view: View?): Bitmap? {
    return try {
        run {
            var container: FrameLayout
            FrameLayout(BaseApplication.mContext).also { container = it }.addView(view)
            container.isDrawingCacheEnabled = true
            cacheBitmap(container)
        }
    } catch (throwable: Throwable) {
        null
    }
}

/**
 *  缓存Bitmap
 *
 * @param view
 */
private fun cacheBitmap(view: View): Bitmap? {
    return try {
        setNoScroll(view)
        view.destroyDrawingCache()
        view.measure(
            View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            ), View.MeasureSpec.makeMeasureSpec(
                View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED
            )
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        var bitmap: Bitmap
        if (view.drawingCache.also { bitmap = it } != null) bitmap.copy(
            Bitmap.Config.ARGB_8888,
            false
        ) else null
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
        null
    }
}

/**
 *  禁止滚动
 *
 * @param view
 */
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