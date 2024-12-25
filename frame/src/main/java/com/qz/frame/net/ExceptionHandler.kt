package com.qz.frame.net

import android.net.ParseException
import com.qz.frame.base.BaseApplication
import com.google.gson.JsonParseException
import com.qz.frame.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import org.json.JSONException
import retrofit2.HttpException
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
        const val CANCEL_ERROR = 1006
        fun parseException(e: Throwable, apiErrorCode: ArrayList<Int>? = null): ResponseException {
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

                is CancellationException -> {
                    ex = ResponseException(e, CANCEL_ERROR)
                    ex.message = BaseApplication.mContext.getString(R.string.cancel_error)
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
                    //自定义请求码
                    return if (apiErrorCode?.contains(e.code) == true) ApiException(e, e.code)
                    //其它api请求错误
                    else return e
                }

                else -> {
                    ex = ResponseException(e, UNKNOWN)
                    ex.message = BaseApplication.mContext.getString(R.string.unknown_error)
                }
            }
            return ex
        }
    }
}

/**
 * @author : ezhuwx
 * Describe :Api错误
 * @param code 错误码
 * @param message 错误信息
 * Designed on 2021/11/4
 * E-mail : ezhuwx@163.com
 * Update on 13:22 by ezhuwx
 */
class ApiException(throwable: Throwable?, override var code: Int) :
    ResponseException(throwable, code)