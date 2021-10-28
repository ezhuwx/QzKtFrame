package com.ez.kotlin.frame.net

import com.ez.kotlin.frame.utils.logD
import com.orhanobut.logger.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * @author : ezhuwx
 * Describe : RetrofitClient 基类
 * Designed on 2021/10/26
 * E-mail : ezhuwx@163.com
 * Update on 13:57 by ezhuwx
 */
abstract class RetrofitClient<Api> {
    companion object {
        const val TIME_OUT: Long = 30
    }

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
        val builder = OkHttpClient().newBuilder()
        val loggingInterceptor: HttpLoggingInterceptor
        if (BuildConfig.DEBUG) {
            loggingInterceptor = HttpLoggingInterceptor { message -> logD(message) }
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else loggingInterceptor = HttpLoggingInterceptor()
        builder.run {
            addInterceptor(loggingInterceptor)
            connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            readTimeout(TIME_OUT, TimeUnit.SECONDS)
            writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            //错误重连
            retryOnConnectionFailure(true)
        }
        return builder.build()
    }
}