package com.ez.kotlin.frame.utils

import com.orhanobut.logger.BuildConfig;
import com.orhanobut.logger.Logger;

/**
 * @author : ezhuwx
 * Describe : Log工具类
 * Designed on 2018/8/28
 * E-mail : ezhuwx@163.com
 * Update on 16:22 by ezhuwx
 * version 1.0.0
 */
fun logD(message: String?, vararg args: Any?) {
    if (BuildConfig.DEBUG) {
        Logger.d(message!!, *args)
    }
}

fun logD(`object`: Any?) {
    if (BuildConfig.DEBUG) {
        Logger.d(`object`)
    }
}

fun logE(message: String?, vararg args: Any?) {
    if (BuildConfig.DEBUG) {
        Logger.e(Throwable(), message!!, *args)
    }
}

fun logE(throwable: Throwable?, message: String?, vararg args: Any?) {
    if (BuildConfig.DEBUG) {
        Logger.e(throwable, message!!, *args)
    }
}

fun logI(message: String?, vararg args: Any?) {
    if (BuildConfig.DEBUG) {
        Logger.i(message!!, *args)
    }
}

fun logV(message: String?, vararg args: Any?) {
    if (BuildConfig.DEBUG) Logger.v(message!!, *args)
}

fun logW(message: String?, vararg args: Any?) {
    if (BuildConfig.DEBUG) {
        Logger.w(message!!, *args)
    }
}

fun logWtf(message: String?, vararg args: Any?) {
    if (BuildConfig.DEBUG) Logger.wtf(message!!, *args)
}

/**
 * Formats the json content and print it
 *
 * @param json the json content
 */
fun logJson(json: String?) {
    if (BuildConfig.DEBUG) {
        Logger.json(json)
    }
}

/**
 * Formats the json content and print it
 *
 * @param xml the xml content
 */
fun logXml(xml: String?) {
    if (BuildConfig.DEBUG) {
        Logger.xml(xml)
    }
}
