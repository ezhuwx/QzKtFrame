package com.ez.kotlin.frame.net

import com.ez.kotlin.frame.utils.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class BaseRepository {

    /**
     * TODO 请求
     *
     * @param T 数据类型
     * @param call 请求体
     * @return
     */
    suspend fun <T : Any> request(call: suspend () -> BaseResponseData<T>): BaseResponseData<T> {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }.apply {
            //这儿可以对返回结果code做一些特殊处理，比如token失效等，可以通过抛出异常的方式实现
            //例：当token失效时，后台返回code 为 100，下面代码实现,再到baseActivity通过观察error来处理
            logE("status($statusCode)：$message")
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