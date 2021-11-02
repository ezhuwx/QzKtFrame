package com.ez.kotlin.frame.base

import com.ez.kotlin.frame.net.BaseResponseData
import com.ez.kotlin.frame.utils.logD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {

    suspend fun <T : Any> request(call: suspend () -> BaseResponseData<T>): BaseResponseData<T> {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }.apply {
            logD(this.toString())
            //这儿可以对返回结果errorCode做一些特殊处理，比如token失效等，可以通过抛出异常的方式实现
            //例：当token失效时，后台返回errorCode 为 100，下面代码实现,再到baseActivity通过观察error来处理
            onError(errorCode, errorMsg)
        }
    }

    abstract fun onError(errorCode: Int, errorMsg: String)
}