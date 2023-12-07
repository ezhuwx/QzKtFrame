package com.ez.kotlin.frame.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ez.kotlin.frame.net.*
import com.ez.kotlin.frame.utils.SingleLiveEvent
import com.ez.kotlin.frame.utils.logE
import kotlinx.coroutines.*
import java.util.UUID

/**
 * @author : ezhuwx
 * Describe : ViewModel 基类
 * Designed on 2021/10/25
 * E-mail : ezhuwx@163.com
 * Update on 10:23 by ezhuwx
 */
typealias CoroutineBlock = suspend CoroutineScope.() -> Unit
typealias onRequestStart = () -> Unit
typealias onRequestError = (Exception) -> Unit

open class BaseViewModel : ViewModel() {
    protected val start by lazy { MutableLiveData<Pair<String, Boolean>>() }
    protected val error by lazy { MutableLiveData<Pair<String, Exception>>() }
    protected val success by lazy { MutableLiveData<Pair<String, Boolean>>() }
    protected val finally by lazy { MutableLiveData<Pair<String, Int>>() }

    /**
     * TODO 标准UI线程协程
     */
    fun launchUI(
        requestCode: String = UUID.randomUUID().toString(),
        onStart: onRequestStart? = null,
        onError: onRequestError? = null,
        block: CoroutineBlock
    ) = MainScope().launch {
        try {
            onStart?.invoke()
            start.value = Pair(requestCode, true)
            withTimeout(BaseRetrofitClient.TIME_OUT) {
                block()
                success.value = Pair(requestCode, true)
            }
        } catch (e: Exception) {
            //此处接收到BaseRepository里的request抛出的异常，处理后赋值给error
            logE("${block.javaClass.name}\nError：$e")
            //异常格式化
            val finalException = ExceptionHandler.parseException(e)
            onError?.invoke(finalException)
            error.value = Pair(requestCode, finalException)
        } finally {
            finally.value = Pair(requestCode, if (error.value != null) -1 else 200)
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
    fun start(): MutableLiveData<Pair<String, Boolean>> = start

    /**
     *TODO 请求失败，出现异常
     */
    fun error(): MutableLiveData<Pair<String, Exception>> = error

    /**
     * TODO 请求开始
     */
    fun success(): MutableLiveData<Pair<String, Boolean>> = success

    /**
     *TODO 请求完成，在此处做一些关闭操作
     */
    fun finally(): MutableLiveData<Pair<String, Int>> = finally
}