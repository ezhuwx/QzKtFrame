package com.qz.frame.utils

import com.qz.frame.base.BaseApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qz.frame.R
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.reflect.Type

/**
 * @author : ezhuwx
 * Describe :json拓展
 * Designed on 2023/1/18
 * E-mail : ezhuwx@163.com
 * Update on 11:25 by ezhuwx
 */
/**
 * 把一个map变成json字符串
 */
fun Map<*, *>?.mapToJson(): String? {
    try {
        return Gson().toJson(this)
    } catch (e: Exception) {
        MainScope().launch {
            BaseApplication.instance.getString(R.string.data_decode_failed).shortShow()
            logE("Gson Error %s", e.message)
        }
    }
    return null
}

fun Any?.json(): String {
    return Gson().toJson(this)
}

inline fun <reified T> String?.parseJson(): T? {
    var t: T? = null
    try {
        t = Gson().fromJson(this, object : TypeToken<T>() {}.type)
    } catch (e: Exception) {
        MainScope().launch {
            BaseApplication.instance.getString(R.string.data_decode_failed).shortShow()
            logE("Gson Error", e.message)
        }
    }
    return t
}

fun String?.jsonDefault(): String = if (empty().trim().isEmpty()) "{}" else this ?: "{}"

/**
 * 把json字符串变成map
 */
fun String?.jsonToMap(): HashMap<String, Any>? {
    val gson = Gson()
    val type: Type = object : TypeToken<HashMap<String?, Any?>?>() {}.type
    var map: HashMap<String, Any>? = null
    try {
        map = gson.fromJson<HashMap<String, Any>>(this, type)
    } catch (e: Exception) {
        MainScope().launch {
            BaseApplication.instance.getString(R.string.data_decode_failed).shortShow()
            logE("Gson Error", e.message)
        }
    }
    return map
}
