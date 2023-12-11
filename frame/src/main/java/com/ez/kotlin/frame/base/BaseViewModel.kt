package com.ez.kotlin.frame.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ez.kotlin.frame.net.*
import com.ez.kotlin.frame.utils.NetWorkUtil
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
    /**
     * 页面状态管理
     */
    var pageStateManager: PageStateManager? = null

    /**
     * 请求开始
     */
    protected val start by lazy { MutableLiveData<Pair<String, Boolean>>() }

    /**
     * 请求错误
     */
    protected val error by lazy { MutableLiveData<Pair<String, Exception>>() }

    /**
     * 请求成功
     */
    protected val success by lazy { MutableLiveData<Pair<String, Boolean>>() }

    /**
     * 请求完成
     */
    protected val finally by lazy { MutableLiveData<Pair<String, Int>>() }

    /**
     * TODO 标准UI线程协程
     */
    fun launchUI(
        customCode: String? = null,
        onStart: onRequestStart? = null,
        onError: onRequestError? = null,
        block: CoroutineBlock,
        isSkipPageLoading: Boolean = false,
        isSkipAllLoading: Boolean = false,
        isSkipPageError: Boolean = false,
        isSkipAllError: Boolean = false,
    ) = MainScope().launch {
        //自定义请求码
        if (!customCode.isNullOrEmpty()) pageStateManager?.isCustomCode = true
        //最终请求码
        val requestCode =
            customCode ?: pageStateManager?.requestCodeInPage ?: UUID.randomUUID().toString()
        try {
            //页面状态管理设置
            onSetPageState(isSkipPageLoading, isSkipAllLoading, isSkipPageError, isSkipAllError)
            //请求开始前准备
            onStart?.invoke()
            //请求开始
            start.value = Pair(requestCode, true)
            withTimeout(BaseRetrofitClient.TIME_OUT) {
                //请求方法
                block()
                //请求成功
                success.value = Pair(requestCode, true)
            }
        } catch (e: Exception) {
            //此处接收到BaseRepository里的request抛出的异常，处理后赋值给error
            logE("${block.javaClass.name}\nError：$e")
            //异常格式化
            val finalException = ExceptionHandler.parseException(e)
            onError?.invoke(finalException)
            //请求异常
            error.value = Pair(requestCode, finalException)
        } finally {
            //请求结束
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
     *  请求开始
     */
    fun start(): MutableLiveData<Pair<String, Boolean>> = start

    /**
     * 请求失败，出现异常
     */
    fun error(): MutableLiveData<Pair<String, Exception>> = error

    /**
     *  请求开始
     */
    fun success(): MutableLiveData<Pair<String, Boolean>> = success

    /**
     * 请求完成，在此处做一些关闭操作
     */
    fun finally(): MutableLiveData<Pair<String, Int>> = finally

    /**
     * 页面状态设置
     */
    private fun onSetPageState(
        skipPageLoading: Boolean,
        skipAllLoading: Boolean,
        skipPageError: Boolean,
        skipErrorAll: Boolean
    ) {
        pageStateManager?.isSkipPageLoading = skipPageLoading
        pageStateManager?.isSkipAllLoading = skipAllLoading
        pageStateManager?.isSkipPageError = skipPageError
        pageStateManager?.isSkipAllError = skipErrorAll
    }
}