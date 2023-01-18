package com.ez.kotlin.frame.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import java.io.*

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2022/12/1
 * E-mail : ezhuwx@163.com
 * Update on 15:54 by ezhuwx
 */
@Throws(IOException::class)
fun fileFromUri(context: Context, uri: Uri?): File? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        uriToFileApiQ(context, uri)
    } else {
        getRealFilePath(context, uri)?.let { File(it) }
    }
}

private fun getRealFilePath(context: Context, uri: Uri?): String? {
    if (null == uri) {
        return null
    }
    val scheme = uri.scheme
    var data: String? = null
    if (scheme == null) {
        data = uri.path
    } else if (ContentResolver.SCHEME_FILE == scheme) {
        data = uri.path
    } else if (ContentResolver.SCHEME_CONTENT == scheme) {
        val cursor = context.contentResolver.query(
            uri,
            arrayOf(MediaStore.Images.ImageColumns.DATA),
            null,
            null,
            null
        )
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                if (index > -1) {
                    data = cursor.getString(index)
                }
            }
            cursor.close()
        }
    }
    return data
}

@RequiresApi(Build.VERSION_CODES.Q)
@Throws(IOException::class, NoSuchMethodError::class)
private fun uriToFileApiQ(context: Context, uri: Uri?): File? {
    var file: File? = null
    if (uri != null) {
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            file = uri.path?.let { File(it) }
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件复制到缓存目录
            val contentResolver = context.contentResolver
            val displayName = "temp." + MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(contentResolver.getType(uri))
            val `is` = contentResolver.openInputStream(uri)
            val cache = File(context.cacheDir.absolutePath, displayName)
            val fos = FileOutputStream(cache)
            copy(`is`, fos)
            file = cache
            fos.close()
            `is`!!.close()
        }
    }
    return file
}

/***
 * 复制
 * @param ist 输入流
 * @param ost 输出流
 * @throws IOException IO异常
 */
@Throws(IOException::class)
fun copy(ist: InputStream?, ost: OutputStream?) {
    if (ost != null) {
        val buffer = ByteArray(4096)
        var byteCount: Int
        while (ist!!.read(buffer).also { byteCount = it } != -1) {
            ost.write(buffer, 0, byteCount)
        }
    }
    ist?.close()
    ost?.close()
}
