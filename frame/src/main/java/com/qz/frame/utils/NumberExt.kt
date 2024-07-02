package com.qz.frame.utils

/**
 * @author : ezhuwx
 * Describe : 数字类拓展
 * Designed on 2024/7/2
 * E-mail : ezhuwx@163.com
 * Update on 11:02 by ezhuwx
 */
fun Int?.orZero(): Int {
    return this ?: 0
}

fun Long?.orZero(): Long {
    return this ?: 0L
}

fun Float?.orZero(): Float {
    return this ?: 0f
}

fun Double?.orZero(): Double {
    return this ?: 0.0
}

fun Int?.orMax(): Int {
    return this ?: Int.MAX_VALUE
}

fun Long?.orMax(): Long {
    return this ?: Long.MAX_VALUE
}

fun Float?.orMax(): Float {
    return this ?: Float.MAX_VALUE
}

fun Double?.orMax(): Double {
    return this ?: Double.MAX_VALUE
}

fun Int?.orMin(): Int {
    return this ?: Int.MIN_VALUE
}

fun Long?.orMin(): Long {
    return this ?: Long.MIN_VALUE
}

fun Float?.orMin(): Float {
    return this ?: Float.MIN_VALUE
}

fun Double?.orMin(): Double {
    return this ?: Double.MIN_VALUE
}