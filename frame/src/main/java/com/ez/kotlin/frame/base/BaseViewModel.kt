package com.ez.kotlin.frame.base

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.ez.kotlin.frame.net.RetrofitClient
import com.ez.kotlin.frame.utils.SingleLiveEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

/**
 * @author : ezhuwx
 * Describe : ViewModel 基类
 * Designed on 2021/10/25
 * E-mail : ezhuwx@163.com
 * Update on 10:23 by ezhuwx
 */
open class BaseViewModel : ViewModel(), LifecycleObserver {
    private val start by lazy { SingleLiveEvent<Boolean>() }
    private val error by lazy { SingleLiveEvent<Exception>() }
    private val finally by lazy { SingleLiveEvent<Int>() }

    //运行在UI线程的协程
    fun launchUI(block: suspend CoroutineScope.() -> Unit) = MainScope().launch {
        try {
            start.value = true
            withTimeout(RetrofitClient.TIME_OUT) {
                block()
            }
        } catch (e: Exception) {
            //此处接收到BaseRepository里的request抛出的异常，直接赋值给error
            error.value = e
        } finally {
            finally.value = 200
        }
    }

    /**
     * TODO 请求开始
     */
    fun getStart(): LiveData<Boolean> = start

    /**
     * 请求失败，出现异常
     */
    fun getError(): LiveData<Exception> = error

    /**
     * 请求完成，在此处做一些关闭操作
     */
    fun getFinally(): LiveData<Int> = finally
}