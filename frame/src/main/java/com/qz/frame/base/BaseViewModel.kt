package com.qz.frame.base

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qz.frame.net.*
import com.qz.frame.utils.logD
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
typealias onRequestError = (Exception, ResponseException) -> Unit
typealias onServiceError = (ApiException) -> Unit

open class BaseViewModel : ViewModel() {
    /**
     * 请求集合
     */
    val requestJobs = mutableMapOf<String, Job>()

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
     * 取消请求
     */
    fun cancelRequest(requestCode: String? = null) {
        if (requestCode.isNullOrEmpty()) requestJobs.values.forEach { it.cancel() }
        else requestJobs[requestCode]?.cancel()
    }

    /**
     *  标准UI线程协程
     *  @param  code 请求码
     *  @param  manager 页面管理
     *  @param  onStart 请求前回调
     *  @param  onError 请异常回调
     *  @param  onServiceError 服务端错误回调
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
        code: String? = null,
        manager: PageStateManager? = null,
        onStart: onRequestStart? = null,
        onError: onRequestError? = null,
        onServiceError: onServiceError? = null,
        onSuccess: (() -> Unit)? = null,
        onFinally: (() -> Unit)? = null,
        isSkipPageLoading: Boolean = false,
        isSkipAllLoading: Boolean = false,
        isSkipPageError: Boolean = false,
        isSkipAllError: Boolean = false,
        isSkipMainState: Boolean = false,
        isSkipPageState: Boolean = false,
        isForceLoading: Boolean = false,
        block: CoroutineBlock,
    ): Job {
        //请求码
        val requestCode = code ?: UUID.randomUUID().toString()
        //取消重复请求
        requestJobs[requestCode]?.cancel("Cancel Repeat Request")
        //请求
        val job = viewModelScope.launch {
            //页面状态管理设置
            onSetPageState(
                requestCode, manager, isSkipPageLoading, isSkipAllLoading,
                isSkipPageError, isSkipAllError, isSkipMainState, isSkipPageState
            )
            try {
                //执行请求开始前准备
                onStart?.invoke()
                //请求开始
                start.value = StateCallbackData(
                    requestCode,
                    manager?.pageManageCode,
                    isForceLoading = isForceLoading
                )
                //请求方法
                block()
                //请求成功
                onSuccess?.invoke()
                success.value = StateCallbackData(
                    requestCode,
                    manager?.pageManageCode,
                )
            } catch (e: Exception) {
                logD("ViewModel:ERROR:  【$e\n】")
                //异常格式化
                val finalException = ExceptionHandler.parseException(e, apiErrorCode)
                //自定义异常回调
                if (finalException is ApiException) onServiceError?.invoke(finalException)
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
                //移除请求Job
                requestJobs.remove(requestCode)
            }
        }
        //保存请求
        requestJobs[requestCode] = job
        return job
    }


    /**
     * 下载协程
     */
    fun downloadLaunch(
        call: DownloadService, outputFile: String,
        onError: Error = {},
        onProcess: Process = { _, _, _ -> },
        onSuccess: Success = { }
    ) = viewModelScope.launch {
        DownloadClient.download(call, outputFile, onError, onProcess, onSuccess)
    }


    /**
     * 页面状态设置
     */
    private fun onSetPageState(
        requestCode: String,
        pageStateManager: PageStateManager?,
        skipPageLoading: Boolean,
        skipAllLoading: Boolean,
        skipPageError: Boolean,
        skipErrorAll: Boolean,
        isSkipMainState: Boolean,
        isSkipPageState: Boolean
    ) {
        pageStateManager?.isSkipPageLoading?.put(requestCode, isSkipPageState || skipPageLoading)
        pageStateManager?.isSkipAllLoading?.put(requestCode, skipAllLoading)
        pageStateManager?.isSkipPageError?.put(requestCode, isSkipPageState || skipPageError)
        pageStateManager?.isSkipAllError?.put(requestCode, skipErrorAll)
        pageStateManager?.isSkipMainState?.put(requestCode, isSkipPageState || isSkipMainState)
        pageStateManager?.onLoadingCancelListener = OnLoadingCancelListener {
            cancelRequest(requestCode)
        }
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
        /**
         * 是否强制Loading
         */
        var isForceLoading: Boolean = false,
    )
}

/**
 * 返回只读状态数据
 */
fun <T> MutableLiveData<T>.readOnly(): LiveData<T> {
    return this
}