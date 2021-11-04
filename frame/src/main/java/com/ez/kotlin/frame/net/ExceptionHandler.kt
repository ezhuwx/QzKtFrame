package com.ez.kotlin.frame.net

import android.net.ParseException
import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.base.BaseApplication
import com.google.gson.JsonParseException
import org.json.JSONException
import retrofit2.HttpException
import java.lang.Exception
import java.lang.RuntimeException
import java.net.ConnectException
import javax.net.ssl.SSLHandshakeException

/**
 * @author : ezhuwx
 * Describe :错误码提示
 * Designed on 2021/11/4
 * E-mail : ezhuwx@163.com
 * Update on 11:05 by ezhuwx
 */
class ExceptionHandler {
    companion object {
        const val UNAUTHORIZED = 401
        const val FORBIDDEN = 403
        const val NOT_FOUND = 404
        const val REQUEST_TIMEOUT = 408
        const val INTERNAL_SERVER_ERROR = 500
        const val BAD_GATEWAY = 502
        const val SERVICE_UNAVAILABLE = 503
        const val GATEWAY_TIMEOUT = 504
        const val SYSTEM_TIME_ERROR = 9999
        const val UNKNOWN = 1000
        const val PARSE_ERROR = 1001
        const val NETWORK_ERROR = 1002
        const val HTTP_ERROR = 1003
        const val SSL_ERROR = 1005
       fun parseException(e: Throwable?): ResponseException {
            var ex = ResponseException(e, HTTP_ERROR)
            when (e) {
                is HttpException -> {
                    when (e.code()) {
                        GATEWAY_TIMEOUT -> {
                            ex.code = NETWORK_ERROR
                            ex.message = BaseApplication.mContext.getString(R.string.net_error)
                        }
                        SYSTEM_TIME_ERROR -> {
                            ex.message = BaseApplication.mContext.getString(R.string.system_time_error_tip)
                        }
                        UNAUTHORIZED, FORBIDDEN, NOT_FOUND, REQUEST_TIMEOUT, INTERNAL_SERVER_ERROR,
                        BAD_GATEWAY, SERVICE_UNAVAILABLE ->
                            ex.message = BaseApplication.mContext.getString(R.string.request_error)
                        else -> ex.message = BaseApplication.mContext.getString(R.string.server_error)
                    }
                }
                is ServerException -> {
                    ex = ResponseException(e, e.code)
                    ex.message = BaseApplication.mContext.getString(R.string.server_error)
                }
                is JsonParseException, is JSONException, is ParseException -> {
                    ex = ResponseException(e, PARSE_ERROR)
                    ex.message = BaseApplication.mContext.getString(R.string.parse_error)
                }
                is ConnectException -> {
                    ex = ResponseException(e, NETWORK_ERROR)
                    ex.message = BaseApplication.mContext.getString(R.string.net_error)
                }
                is SSLHandshakeException -> {
                    ex = ResponseException(e, SSL_ERROR)
                    ex.message = BaseApplication.mContext.getString(R.string.ssl_error)
                }
                else -> {
                    ex = ResponseException(e, UNKNOWN)
                    ex.message = BaseApplication.mContext.getString(R.string.unknown_error)
                }
            }
            return ex
        }
    }



    /**
     * ServerException发生后，将自动转换为ResponseException返回
     */
    class ServerException(override var message: String, var code: Int) : RuntimeException()
}