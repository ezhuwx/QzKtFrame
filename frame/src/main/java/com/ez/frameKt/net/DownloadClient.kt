package com.ez.frameKt.net

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.ResponseBody
import java.io.*


typealias Error = suspend (Throwable) -> Unit
typealias Process = suspend (downloadedSize: Long, length: Long, progress: Float) -> Unit
typealias Success = suspend (uri: File) -> Unit
typealias DownloadService = suspend () -> ResponseBody

/**
 * @author : ezhuwx
 * Describe : 下载请求
 * Designed on 2021/11/5
 * E-mail : ezhuwx@163.com
 * Update on 10:23 by ezhuwx
 */
object DownloadClient {
    suspend fun download(
        call: DownloadService, outputFile: String,
        onError: Error = {},
        onProcess: Process = { _, _, _ -> },
        onSuccess: Success = { }
    ) {
        flow {
            try {
                //执行下载方法或缺文件流
                val body = call.invoke()
                //文件大小
                val contentLength = body.contentLength()
                //输入流
                val inputStream = body.byteStream()
                //输出文件
                val file = File(outputFile)
                //输出流
                val outputStream = FileOutputStream(file)
                //当前读取长度
                var currentLength = 0
                //每次要写入的字符数
                val bufferSize = 1024 * 8
                val buffer = ByteArray(bufferSize)
                val bufferedInputStream = BufferedInputStream(inputStream, bufferSize)
                //实际读取长度
                var readLength: Int
                //循环读取
                while (bufferedInputStream.read(buffer, 0, bufferSize)
                        .also { readLength = it } != -1
                ) {
                    //输入流写入
                    outputStream.write(buffer, 0, readLength)
                    //叠加当前读取长度
                    currentLength += readLength
                    //返回读取进度
                    emit(
                        DownloadListener.progress(
                            currentLength.toLong(),
                            contentLength,
                            currentLength.toFloat() / contentLength.toFloat()
                        )
                    )
                }
                //字符读取流关闭
                bufferedInputStream.close()
                //写入流关闭
                outputStream.close()
                outputStream.flush()
                //读取流关闭
                inputStream.close()
                //成功回调
                emit(DownloadListener.success(file))
            } catch (e: Exception) {
                //异常回调
                emit(DownloadListener.failure(e))
            }
        }.flowOn(Dispatchers.IO)
            .collect {
                it.fold(onFailure = { e ->
                    e?.let { it1 -> onError(it1) }
                }, onSuccess = { file ->
                    onSuccess(file)
                }, onLoading = { progress ->
                    onProcess(progress.currentLength, progress.length, progress.process)
                })
            }
    }
}
