package com.qz.frame.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.qz.frame.base.BaseApplication
import java.lang.reflect.Type
import kotlin.reflect.KProperty

/**
 * @author : ezhuwx
 * Describe : MMKV 用户信息管理工具类
 * Designed on 2021/10/28
 * E-mail : ezhuwx@163.com
 * Update on 10:57 by ezhuwx
 */

/**
 * MMKV委托
 */
interface MMKVReadWriteProperty<in Any, T> {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T)
    fun encode(value: T)
}

/**
 * MMKV委托(Int)
 */
class MMKVInt(
    private val key: String,
    private val defaultValue: Int
) : MMKVReadWriteProperty<Any?, Int> {
    private val mmkv by lazy { BaseApplication.instance.config.mmkv }
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return mmkv.decodeInt(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        mmkv.encode(key, value)
    }

    override fun encode(value: Int) {
        mmkv.encode(key, value)
    }
}

/**
 * MMKV委托(Long)
 */
class MMKVLong(
    private val key: String,
    private val defaultValue: Long
) : MMKVReadWriteProperty<Any?, Long> {
    private val mmkv by lazy { BaseApplication.instance.config.mmkv }
    override fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return mmkv.decodeLong(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        mmkv.encode(key, value)
    }

    override fun encode(value: Long) {
        mmkv.encode(key, value)
    }
}

/**
 * MMKV委托(Float)
 */
class MMKVFloat(
    private val key: String,
    private val defaultValue: Float
) : MMKVReadWriteProperty<Any?, Float> {
    private val mmkv by lazy { BaseApplication.instance.config.mmkv }
    override fun getValue(thisRef: Any?, property: KProperty<*>): Float {
        return mmkv.decodeFloat(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        mmkv.encode(key, value)
    }

    override fun encode(value: Float) {
        mmkv.encode(key, value)
    }
}

/**
 * MMKV委托(Float)
 */
class MMKVDouble(
    private val key: String,
    private val defaultValue: Double
) : MMKVReadWriteProperty<Any?, Double> {
    private val mmkv by lazy { BaseApplication.instance.config.mmkv }
    override fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return mmkv.decodeDouble(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        mmkv.encode(key, value)
    }

    override fun encode(value: Double) {
        mmkv.encode(key, value)
    }
}

/**
 * MMKV委托(Boolean)
 */
class MMKVBoolean(
    private val key: String,
    private val defaultValue: Boolean
) : MMKVReadWriteProperty<Any?, Boolean> {
    private val mmkv by lazy { BaseApplication.instance.config.mmkv }
    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return mmkv.decodeBool(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        mmkv.encode(key, value)
    }

    override fun encode(value: Boolean) {
        mmkv.encode(key, value)
    }
}

/**
 * MMKV委托(String)
 */
class MMKVString(
    private val key: String,
    private val defaultValue: String? = null
) : MMKVReadWriteProperty<Any?, String?> {
    private val mmkv by lazy { BaseApplication.instance.config.mmkv }
    override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return mmkv.decodeString(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        mmkv.encode(key, value)
    }

    override fun encode(value: String?) {
        mmkv.encode(key, value)
    }
}

/**
 * MMKV委托(ByteArray)
 */
class MMKVByteArray(
    private val key: String,
    private val defaultValue: ByteArray? = null
) : MMKVReadWriteProperty<Any?, ByteArray?> {
    private val mmkv by lazy { BaseApplication.instance.config.mmkv }
    override fun getValue(thisRef: Any?, property: KProperty<*>): ByteArray? {
        return mmkv.decodeBytes(key, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: ByteArray?) {
        mmkv.encode(key, value)
    }

    override fun encode(value: ByteArray?) {
        mmkv.encode(key, value)
    }
}

/**
 * MMKV委托
 */
class MMKVObject<T>(private val key: String, private val type: Type? = null) :
    MMKVReadWriteProperty<Any?, T?> {
    private val mmkv by lazy { BaseApplication.instance.config.mmkv }
    override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
        val data = mmkv.decodeString(key, null)
        return Gson().fromJson(data, type)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        mmkv.encode(key, value.json())
    }

    override fun encode(value: T?) {
        mmkv.encode(key, value.json())
    }
}

/**
 * MMKV委托存储拓展
 */
fun <T> String.mmkvEncode(value: T) = when (value) {
    is Int -> MMKVInt(this, value).encode(value)
    is Long -> MMKVLong(this, value).encode(value)
    is Float -> MMKVFloat(this, value).encode(value)
    is Double -> MMKVDouble(this, value).encode(value)
    is Boolean -> MMKVBoolean(this, value).encode(value)
    is String -> MMKVString(this, value).encode(value)
    is ByteArray -> MMKVByteArray(this, value).encode(value)
    else -> MMKVObject<T>(this).encode(value)
}

/**
 * MMKV委托(Int)
 */
fun String.mmkvInt(defaultValue: Int) = MMKVInt(this, defaultValue)

/**
 * MMKV委托(Long)
 */
fun String.mmkvLong(defaultValue: Long) = MMKVLong(this, defaultValue)

/**
 * MMKV委托(Float)
 */
fun String.mmkvFloat(defaultValue: Float) = MMKVFloat(this, defaultValue)

/**
 * MMKV委托(Double)
 */
fun String.mmkvDouble(defaultValue: Double) = MMKVDouble(this, defaultValue)

/**
 * MMKV委托(Boolean)
 */
fun String.mmkvBoolean(defaultValue: Boolean) = MMKVBoolean(this, defaultValue)

/**
 * MMKV委托(String)
 */
fun String.mmkvString(defaultValue: String? = null) = MMKVString(this, defaultValue)

/**
 * MMKV委托(ByteArray)
 */
fun String.mmkvByteArray(defaultValue: ByteArray? = null) = MMKVByteArray(this, defaultValue)

/**
 * MMKV委托
 */
inline fun <reified T> String.mmkvObject() = MMKVObject<T>(this, object : TypeToken<T>() {}.type)