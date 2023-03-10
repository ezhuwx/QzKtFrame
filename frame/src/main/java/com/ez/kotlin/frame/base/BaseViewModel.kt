package com.ez.kotlin.frame.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ez.kotlin.frame.net.*
import com.ez.kotlin.frame.utils.SingleLiveEvent
import com.ez.kotlin.frame.utils.logE
import kotlinx.coroutines.*

/**
 * @author : ezhuwx
 * Describe : ViewModel 基类
 * Designed on 2021/10/25
 * E-mail : ezhuwx@163.com
 * Update on 10:23 by ezhuwx
 */
typealias CoroutineBlock = suspend CoroutineScope.() -> Unit

open class BaseViewModel : ViewModel(), LifecycleObserver {
    protected val start by lazy { SingleLiveEvent<Boolean>() }
    protected val error by lazy { SingleLiveEvent<Exception>() }
    protected val success by lazy { SingleLiveEvent<Boolean>() }
    protected val finally by lazy { SingleLiveEvent<Int>() }

    /**
     * TODO 标准UI线程协程
     */
    fun launchUI(block: CoroutineBlock) = MainScope().launch {
        try {
            start.value = true
            withTimeout(BaseRetrofitClient.TIME_OUT) {
                block()
                success.value = true
            }
        } catch (e: Exception) {
            //此处接收到BaseRepository里的request抛出的异常，处理后赋值给error
            logE("${block.javaClass.name}\nError：$e")
            error.value = ExceptionHandler.parseException(e)
        } finally {
            finally.value = if (error.value != null) -1 else 200
        }
    }

    /**
     * TODO 下载协程
     */
    fun downloadLaunch(
        call: DownloadService, outputFile: String,
        onError: Error = {},
        onProcess: Process = { _, _, _ -> },
        onSuccess: Success = { }
    ) = MainScope().launch {
        DownloadClient.download(call, outputFile, onError, onProcess, onSuccess)
    }

    /**
     * TODO 请求开始
     */
    fun start(): LiveData<Boolean> = start

    /**
     *TODO 请求失败，出现异常
     */
    fun error(): LiveData<Exception> = error

    /**
     * TODO 请求开始
     */
    fun success(): LiveData<Boolean> = success

    /**
     *TODO 请求完成，在此处做一些关闭操作
     */
    fun finally(): LiveData<Int> = finally
}