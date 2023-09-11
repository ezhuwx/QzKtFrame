package com.ez.kotlin.frame.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.base.BaseApplication
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.*
import java.net.URLConnection
import java.nio.charset.StandardCharsets

/**
 * @author : ezhuwx
 * Describe :文件拓展
 * Designed on 2022/12/1
 * E-mail : ezhuwx@163.com
 * Update on 15:54 by ezhuwx
 */
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
            val displayName = uri.path.fileName() + "_temp." + MimeTypeMap.getSingleton()
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

@Throws(IOException::class)
fun saveBitmap(bitmap: Bitmap, file: File?) {
    if (file != null) {
        if (!file.exists()) {
            val result = file.createNewFile()
        }
        val outputStream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.close()
    }
}


@Throws(IOException::class)
fun saveBitmap(context: Context, bitmap: Bitmap, uri: Uri?) {
    if (uri != null) {
        val outputStream: OutputStream? = context.contentResolver.openOutputStream(uri)
        if (outputStream != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
        }
    }
}

/**
 * 保存上传
 */
fun File.saveToGallery(context: Context, name: String? = null): String? {
    try {
        //保存时间
        val timeStr = name ?: System.currentTimeMillis().toYyMmDdHhMmSsUnder()
        //保存路径
        val filePath =
            Environment.DIRECTORY_PICTURES + "/${context.resources.getString(R.string.app_name)}/"
        //保存
        createPathUri(context, timeStr, filePath, path.mimeType())?.let {
            copy(FileInputStream(this), context.contentResolver.openOutputStream(it))
            return Environment.getExternalStorageDirectory().path +
                    "/$filePath" + name + ".${path.mimeType()?.ext}"
        }
    } catch (e: IOException) {
        logE("save to gallery filed: ${e.message}")
    }
    return null
}

/**
 * 获取图片URI
 */
fun createPathUri(
    context: Context,
    fileName: String?,
    filePath: String?,
    mimeType: MediaMimeType?
): Uri? {
    //ContentValues
    val contentValues = ContentValues()
    contentValues.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, fileName)
    contentValues.put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType?.type)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, filePath)
    }

    return context.contentResolver.insert(
        when {
            mimeType.isImage() -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            mimeType.isVideo() -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            mimeType.isAudio() -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }, contentValues
    )
}

/**
 * 得到json文件中的内容
 */
fun String.getAssetsFile(context: Context): String {
    val isTxt = "txt" == ext()
    val stringBuilder = StringBuilder()
    //获得assets资源管理器
    val assetManager = BaseApplication.mContext.assets
    //使用IO流读取json文件内容
    try {
        val bufferedReader = BufferedReader(
            InputStreamReader(
                assetManager.open(this), StandardCharsets.UTF_8
            )
        )
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            stringBuilder.append(line).append(if (isTxt) "\n" else "")
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return stringBuilder.toString()
}

fun String.getAssetsFileBytes(context: Context): ByteArray? {
    var buffer: ByteArray? = null
    var ins: InputStream? = null
    try {
        ins = context.resources.assets.open(this)
        buffer = ByteArray(ins.available())
        ins.read(buffer)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            ins?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return buffer
}

fun File.mediaType(): MediaType {
    return URLConnection.getFileNameMap().getContentTypeFor(name).toMediaType()
}

fun File.mediaBody(): RequestBody {
    return asRequestBody(mediaType())
}

fun File.multiPart(paramName: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(paramName, name, mediaBody())
}

