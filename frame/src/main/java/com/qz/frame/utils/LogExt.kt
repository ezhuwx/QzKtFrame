package com.qz.frame.utils

import com.orhanobut.logger.Logger

/**
 * @author : ezhuwx
 * Describe : Log工具类
 * Designed on 2018/8/28
 * E-mail : ezhuwx@163.com
 * Update on 16:22 by ezhuwx
 * version 1.0.0
 */
fun logD(message: String?, vararg args: Any?) {
    Logger.d(message.empty(), *args)
}

fun logD(`object`: Any?) {
    Logger.d(`object`)
}

fun logE(message: String?, vararg args: Any?) {
    Logger.e(Throwable(), message.empty(), *args)
}

fun logE(throwable: Throwable?, message: String?, vararg args: Any?) {
    Logger.e(throwable, message.empty(), *args)
}

fun logI(message: String?, vararg args: Any?) {
    Logger.i(message.empty(), *args)
}

fun logV(message: String?, vararg args: Any?) {
    Logger.v(message.empty(), *args)
}

fun logW(message: String?, vararg args: Any?) {
    Logger.w(message.empty(), *args)
}

fun logWtf(message: String?, vararg args: Any?) {
    Logger.wtf(message.empty(), *args)
}

/**
 * Formats the json content and print it
 *
 * @param json the json content
 */
fun logJson(json: String?) {
    Logger.json(json)
}

/**
 * Formats the json content and print it
 *
 * @param xml the xml content
 */
fun logXml(xml: String?) {
    Logger.xml(xml)
}
