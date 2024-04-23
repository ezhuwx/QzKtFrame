package com.ez.kotlin.frame.base

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ez.kotlin.frame.net.*
import com.ez.kotlin.frame.utils.logD
import kotlinx.coroutines.*
import kotlin.math.log

/**
 * @author : ezhuwx
 * Describe : ViewModel 基类
 * Designed on 2021/10/25
 * E-mail : ezhuwx@163.com
 * Update on 10:23 by ezhuwx
 */
typealias CoroutineBlock = suspend CoroutineScope.() -> Unit
typealias onRequestStart = () -> Unit
typealias onRequestError = (Exception, ResponseException) -> Unit

open class BaseViewModel : ViewModel() {
    /**
     * 自定义异常码
     */
    open val apiErrorCode = arrayListOf<Int>()

    /**
     * 请求开始
     */
    val start by lazy { MutableLiveData<StateCallbackData>() }

    /**
     * 请求错误
     */
    val error by lazy { MutableLiveData<StateCallbackData>() }

    /**
     * 请求成功
     */
    val success by lazy { MutableLiveData<StateCallbackData>() }

    /**
     * 请求完成
     */
    val finally by lazy { MutableLiveData<StateCallbackData>() }


    /**
     *  标准UI线程协程
     *  @param  requestCode 请求码
     *  @param  manager 页面管理
     *  @param  onStart 请求前回调
     *  @param  onError 请异常回调
     *  @param  onSuccess 请成功回调
     *  @param  onFinally 请完成回调,
     *  @param  isSkipPageLoading 跳过页面加载
     *  @param  isSkipAllLoading 跳过所有加载
     *  @param  isSkipPageError 跳过页内错误提示
     *  @param  isSkipAllError 跳过所有错误提示
     *  @param  isSkipMainState 跳过主界面显示
     *  @param  block 请求方法
     *
     */
    fun launchUI(
        requestCode: String? = null,
        manager: PageStateManager? = null,
        onStart: onRequestStart? = null,
        onError: onRequestError? = null,
        onSuccess: (() -> Unit)? = null,
        onFinally: (() -> Unit)? = null,
        isSkipPageLoading: Boolean = false,
        isSkipAllLoading: Boolean = false,
        isSkipPageError: Boolean = false,
        isSkipAllError: Boolean = false,
        isSkipMainState: Boolean = false,
        block: CoroutineBlock,
    ) = MainScope().launch {
        //页面状态管理设置
        onSetPageState(
            manager, isSkipPageLoading, isSkipAllLoading,
            isSkipPageError, isSkipAllError, isSkipMainState
        )
        try {
            //执行请求开始前准备
            onStart?.invoke()
            //请求开始
            start.value = StateCallbackData(requestCode, manager?.pageManageCode)
            //请求方法
            block()
            //请求成功
            onSuccess?.invoke()
            success.value = StateCallbackData(
                requestCode,
                manager?.pageManageCode,
            )
        } catch (e: Exception) {
            if (BaseApplication.instance.isDebug) {
                Log.e("ViewModel", "ERROR:  【$e\n】")
            }
            //异常格式化
            val finalException = ExceptionHandler.parseException(e, apiErrorCode)
            //执行异常回调
            onError?.invoke(e, finalException)
            //请求异常
            error.value = StateCallbackData(
                requestCode,
                manager?.pageManageCode,
                originalError = e,
                error = finalException
            )
        } finally {
            //请求结束
            onFinally?.invoke()
            finally.value = StateCallbackData(
                requestCode,
                manager?.pageManageCode,
                originalError = error.value?.originalError,
                error = error.value?.error,
                isSuccess = error.value == null
            )
        }
    }


    /**
     * 下载协程
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
     * 页面状态设置
     */
    private fun onSetPageState(
        pageStateManager: PageStateManager?,
        skipPageLoading: Boolean,
        skipAllLoading: Boolean,
        skipPageError: Boolean,
        skipErrorAll: Boolean,
        isSkipMainState: Boolean
    ) {
        pageStateManager?.isSkipPageLoading?.set(skipPageLoading)
        pageStateManager?.isSkipAllLoading?.set(skipAllLoading)
        pageStateManager?.isSkipPageError?.set(skipPageError)
        pageStateManager?.isSkipAllError?.set(skipErrorAll)
        pageStateManager?.isSkipMainState?.set(isSkipMainState)
    }

    /**
     * 请求状态回调数据
     */
    data class StateCallbackData(
        /**
         * 请求编码
         */
        var requestCode: String? = null,
        /**
         * 调用页面管理编码
         */
        var pageManagerCode: String? = null,
        /**
         * 异常
         */
        var originalError: Exception? = null,
        /**
         * 异常
         */
        var error: ResponseException? = null,
        /**
         * 是否成功
         */
        var isSuccess: Boolean? = null,

        )
}