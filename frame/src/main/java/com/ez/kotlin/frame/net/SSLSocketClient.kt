package com.ez.kotlin.frame.net

import android.annotation.SuppressLint
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * @author : ezhuwx
 * Describe :SSL证书忽略配置
 * Designed on 2018/8/7
 * E-mail : ezhuwx@163.com
 * Update on 14:55 by ezhuwx
 * version 1.0.0
 */
object SSLSocketClient {
    /**
     * 获取这个SSLSocketFactory
     */
    val sSLSocketFactory: SSLSocketFactory
        get() = try {
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
            sslContext.socketFactory
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    /**
     * 获取TrustManager
     */
     val trustManager = @SuppressLint("CustomX509TrustManager")
    object : X509TrustManager {
        @SuppressLint("TrustAllX509TrustManager")
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @SuppressLint("TrustAllX509TrustManager")
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    /**
     * 获取HostnameVerifier
     */
    val hostnameVerifier: HostnameVerifier
        get() = HostnameVerifier { _: String?, _: SSLSession? -> true }
}