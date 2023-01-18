package com.ez.kotlin.frame.utils

import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.base.BaseApplication
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.lang.reflect.Type

/**
 * @author : ezhuwx
 * Describe :
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
            logE("Gson Error", e.message)
        }
    }
    return null
}

fun Any?.json(): String {
    return Gson().toJson(this)
}

/**
 * 把一个json字符串变成对象
 */
fun <T> String?.jsonToObject(cls: Class<T>?): T? {
    val t: T = try {
        Gson().fromJson(this, cls)
    } catch (e: Exception) {
        MainScope().launch {
            BaseApplication.instance.getString(R.string.data_decode_failed).shortShow()
            logE("Gson Error", e.message)
        }
        return null
    }
    return t
}

fun <T> String?.jsonToObject(): T? {
    try {
        return Gson().fromJson(this, object : TypeToken<T?>() {}.type)
    } catch (e: Exception) {
        MainScope().launch {
            BaseApplication.instance.getString(R.string.data_decode_failed).shortShow()
            logE("Gson Error", e.message)
        }
    }
    return null
}

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
