package com.ez.kotlin.frame.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ez.kotlin.frame.net.*
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
typealias onRequestStart = () -> Unit
typealias onRequestError = (Exception) -> Unit

open class BaseViewModel : ViewModel() {

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
     * TODO 标准UI线程协程
     */
    fun launchUI(
        requestCode: String? = null,
        manager: PageStateManager? = null,
        onStart: onRequestStart? = null,
        onError: onRequestError? = null,
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
            isSkipPageError, isSkipAllError
        )
        try {
            //执行请求开始前准备
            onStart?.invoke()
            //请求开始
            start.value = StateCallbackData(requestCode, manager?.pageManageCode)
            withTimeout(BaseRetrofitClient.TIME_OUT) {
                //请求方法
                block()
                //请求成功
                success.value = StateCallbackData(
                    requestCode,
                    manager?.pageManageCode,
                    isSkipMainState = isSkipMainState
                )
            }
        } catch (e: Exception) {
            //异常格式化
            val finalException = ExceptionHandler.parseException(e)
            //执行异常回调
            onError?.invoke(finalException)
            //请求异常
            error.value = StateCallbackData(
                requestCode,
                manager?.pageManageCode,
                exception = finalException
            )
        } finally {
            //请求结束
            finally.value = StateCallbackData(
                requestCode,
                manager?.pageManageCode,
                exception = error.value?.exception,
                isSuccess = error.value == null
            )
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
     * 页面状态设置
     */
    private fun onSetPageState(
        pageStateManager: PageStateManager?,
        skipPageLoading: Boolean,
        skipAllLoading: Boolean,
        skipPageError: Boolean,
        skipErrorAll: Boolean
    ) {
        pageStateManager?.isSkipPageLoading?.set(skipPageLoading)
        pageStateManager?.isSkipAllLoading?.set(skipAllLoading)
        pageStateManager?.isSkipPageError?.set(skipPageError)
        pageStateManager?.isSkipAllError?.set(skipErrorAll)
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
        var exception: Exception? = null,
        /**
         * 是否成功
         */
        var isSuccess: Boolean? = null,
        /**
         * 是否跳过主界面显示
         */
        var isSkipMainState: Boolean? = null,
    )
}