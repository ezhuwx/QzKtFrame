package com.qz.frame.net

import com.qz.frame.base.BaseApplication
import com.qz.frame.utils.NetWorkUtil
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import java.net.Proxy


/**
 * @author : ezhuwx
 * Describe : RetrofitClient 基类
 * Designed on 2021/10/26
 * E-mail : ezhuwx@163.com
 * Update on 13:57 by ezhuwx
 */
abstract class BaseRetrofitClient<Api> {
    companion object {
        const val TIME_OUT = 60 * 1000L
    }

    /**
     * 超时时间
     */
    private val requestTimeOut
        get() = getTimeOut()

    /**
     * 允许服务器与系统最大时间差
     * */
    private val maxAllowTimeDiff
        get() = getMaxAllowDiffTime()

    /**
     * ssl证书忽略
     * */
    private val isSSLIgnore
        get() = getSSLIgnore()


    /**
     * 请求的地址
     * */
    private val baseUrl
        get() = getRetrofitBaseUrl()

    /**
    retrofit对象
     */
    private var retrofit: Retrofit? = null

    /**
    设置BaseUrl
     */
    abstract fun getRetrofitBaseUrl(): String

    /**
     * 请求的api，可以根据不同的场景设置多个
     */
    val service: Api
        get() = provideApiService().let {
            getRetrofit().create(it)
        }


    /**
     * 提供接口服务
     */
    abstract fun provideApiService(): Class<Api>

    /**
     * 获取Retrofit
     * */
    open fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }

    /**
     * 获取 OkHttpClient
     */
    private fun getOkHttpClient(): OkHttpClient {
        //缓存
        val cacheInterceptor = Interceptor { chain: Interceptor.Chain ->
            var request = chain.request()
            request = if (!NetWorkUtil.isNoProxyConnected(BaseApplication.mContext)) {
                request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build()
            } else {
                request.newBuilder().apply {
                    val headers = getHeaders()
                    for (header in headers) {
                        addHeader(header.key, header.value)
                    }
                }.build()
            }
            var response = chain.proceed(request)
            val responseBuilder: Response.Builder = response.newBuilder()
            response = if (NetWorkUtil.isNoProxyConnected(BaseApplication.mContext)) {
                //设置服务器时间
                val responseTime: Long = getHeaderTime(response.header("Date"))
                //服务器时间与系统时间应当不超过超时间
                if (maxAllowTimeDiff >= 0
                    && abs(responseTime - System.currentTimeMillis()) > maxAllowTimeDiff
                ) {
                    //服务器时间与系统时间超过超时间，提示更新至最新时间
                    responseBuilder.code(9999)
                }
                // 有网络时, 不缓存, 最大保存时长为0
                val maxAge = 0
                responseBuilder.header("Cache-Control", "public, max-age=$maxAge")
                    .removeHeader("Pragma")
                    .build()
            } else {
                // 无网络时，设置超时为4周
                val maxStale = 60 * 60 * 24 * 28
                responseBuilder
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .removeHeader("Pragma")
                    .build()
            }
            response
        }
        //Log
        val loggingInterceptor: HttpLoggingInterceptor
        if (BaseApplication.instance.isDebug) {
            loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else loggingInterceptor = HttpLoggingInterceptor()
        //builder
        val builder = OkHttpClient().newBuilder().proxy(Proxy.NO_PROXY)
        builder.run {
            //添加自定义拦截器
            val customInterceptors = getInterceptors()
            for (interceptor in customInterceptors) {
                addInterceptor(interceptor)
            }
            //缓存
            addNetworkInterceptor(cacheInterceptor)
            addInterceptor(cacheInterceptor)
            //日志
            addInterceptor(loggingInterceptor)
            //超时时间设置
            connectTimeout(requestTimeOut, TimeUnit.MILLISECONDS)
            readTimeout(requestTimeOut, TimeUnit.MILLISECONDS)
            writeTimeout(requestTimeOut, TimeUnit.MILLISECONDS)
            callTimeout(requestTimeOut, TimeUnit.MILLISECONDS)
            //错误重连
            retryOnConnectionFailure(true)
            //证书忽略
            if (isSSLIgnore) {
                sslSocketFactory(
                    TLSSocketFactory(SSLSocketClient.sSLSocketFactory), SSLSocketClient.trustManager
                )
                hostnameVerifier(SSLSocketClient.hostnameVerifier)
            }
        }
        return builder.build()
    }

    /**
     * 设置服务器时间
     * .header("Date")
     */
    private fun getHeaderTime(strServerDate: String?): Long {
        strServerDate?.let {
            //Thu, 29 Sep 2016 07:57:42 GMT
            val simpleDateFormat = SimpleDateFormat(
                "EEE, d MMM yyyy HH:mm:ss z",
                Locale.ENGLISH
            )
            TimeZone.setDefault(TimeZone.getTimeZone("GMT+8"))
            try {
                val serverDate = simpleDateFormat.parse(strServerDate)
                serverDate?.let { return serverDate.time }
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
        return System.currentTimeMillis()
    }

    /**
     *  允许服务器与系统最大时间差
     *  （<0不校验）
     * */
    abstract fun getMaxAllowDiffTime(): Int

    /**
     * ssl证书忽略
     */
    abstract fun getSSLIgnore(): Boolean

    /**
     *  添加请求头
     * */
    open fun getHeaders(): MutableMap<String, String> = mutableMapOf()

    /**
     *  添加请求头
     * */
    open fun getInterceptors(): MutableList<Interceptor> = mutableListOf()

    /**
     *  超时时间
     * */
    open fun getTimeOut(): Long = TIME_OUT
}