package com.ez.kotlin.frame.net

import android.net.ParseException
import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.base.BaseApplication
import com.google.gson.JsonParseException
import kotlinx.coroutines.TimeoutCancellationException
import org.json.JSONException
import retrofit2.HttpException
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
        private const val UNAUTHORIZED = 401
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404
        private const val REQUEST_TIMEOUT = 408
        private const val INTERNAL_SERVER_ERROR = 500
        private const val BAD_GATEWAY = 502
        private const val SERVICE_UNAVAILABLE = 503
        private const val GATEWAY_TIMEOUT = 504
        private const val SYSTEM_TIME_ERROR = 9999
        private const val UNKNOWN = 1000
        private const val PARSE_ERROR = 1001
        private const val NETWORK_ERROR = 1002
        private const val HTTP_ERROR = 1003
        private const val SSL_ERROR = 1005
        fun parseException(e: Throwable): ResponseException {
            var ex = ResponseException(e, HTTP_ERROR)
            when (e) {
                is HttpException -> {
                    ex.code = e.code()
                    when (e.code()) {
                        GATEWAY_TIMEOUT -> {
                            ex.message = BaseApplication.mContext.getString(R.string.net_error)
                        }
                        SYSTEM_TIME_ERROR -> {
                            ex.message =
                                BaseApplication.mContext.getString(R.string.system_time_error_tip)
                        }
                        UNAUTHORIZED -> ex.message =
                            BaseApplication.mContext.getString(R.string.request_unauthorized)

                        FORBIDDEN -> ex.message =
                            BaseApplication.mContext.getString(R.string.request_forbidden)

                        NOT_FOUND -> ex.message =
                            BaseApplication.mContext.getString(R.string.request_invalid)

                        REQUEST_TIMEOUT -> ex.message =
                            BaseApplication.mContext.getString(R.string.request_time_out)

                        INTERNAL_SERVER_ERROR, BAD_GATEWAY, SERVICE_UNAVAILABLE ->
                            ex.message = BaseApplication.mContext.getString(R.string.server_error)

                        else -> ex.message =
                            BaseApplication.mContext.getString(R.string.request_error)

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
                is TimeoutCancellationException -> {
                    ex = ResponseException(e, REQUEST_TIMEOUT)
                    ex.message = BaseApplication.mContext.getString(R.string.request_time_out)
                }
                is ResponseException -> {
                    ex.code = e.code
                    return ex
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