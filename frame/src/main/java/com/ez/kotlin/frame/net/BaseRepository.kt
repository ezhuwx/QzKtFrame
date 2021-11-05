package com.ez.kotlin.frame.net

import com.ez.kotlin.frame.utils.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

typealias RequestService<T> = suspend () -> BaseResponseData<T>

abstract class BaseRepository {
    /**
     * TODO 请求
     *
     * @param T 数据类型
     * @param call 请求体
     * @return
     */
    suspend fun <T : Any> request(call: RequestService<T>): BaseResponseData<T> {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }.apply {
            onResult(statusCode, message)
        }
    }

    /**
     * TODO 结果返回
     *
     * @param code 状态码
     * @param msg 提示信息
     */
    abstract fun onResult(code: Int, msg: String?)
}