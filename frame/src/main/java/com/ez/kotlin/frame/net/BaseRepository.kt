package com.ez.kotlin.frame.net

import com.ez.kotlin.frame.utils.json
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody


abstract class BaseRepository {
    /**
     * TODO 请求
     *
     * @param call 请求体
     * @return
     */
    suspend fun <K : BaseResponseData> request(call: suspend () -> K): K {
        return withContext(Dispatchers.IO) {
            call.invoke()
        }.apply {
            onResult(code, message)
        }
    }

    /**
     * TODO 结果返回
     *
     * @param code 状态码
     * @param msg 提示信息
     */
    abstract fun onResult(code: String, msg: String)


}

/**
 * JSON请求体
 */
fun Any?.toRequestBody(): RequestBody {
    return json().toRequestBody("application/json; charset=utf-8".toMediaType())
}