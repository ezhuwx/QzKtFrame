package com.qz.frame.net

import com.qz.frame.utils.json
import com.qz.frame.utils.safeToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


abstract class BaseRepository {
    /**
     *  请求协程
     *
     * @param call 请求体
     * @return 定义类
     */
    open suspend fun <K : BaseResponseData> request(call: suspend () -> K): K {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }.apply {
            if (!onResult(this, baseCode, baseMessage)) {
                //抛出服务器异常
                val exception = ResponseException(null, baseCode.safeToInt())
                exception.message = baseMessage
                throw exception
            }
        }
    }

    /**
     *  结果返回 true 成
     *
     * @param code 状态码
     * @param msg 提示信息
     * @return 是否成功
     */
    abstract fun <K : BaseResponseData> onResult(data: K, code: String, msg: String): Boolean


}

/**
 * JSON请求体
 */
fun Any?.toRequestBody(): RequestBody {
    return json().toRequestBody("application/json; charset=utf-8".toMediaType())
}