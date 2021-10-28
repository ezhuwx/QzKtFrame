package com.ez.kotlin.frame.net

/**
 * @author : ezhuwx
 * Describe : 数据类基类
 * Designed on 2021/10/27
 * E-mail : ezhuwx@163.com
 * Update on 14:42 by ezhuwx
 */
data class ResponseData<out T>(
    val errorCode: Int,
    val errorMsg: String,
    val data: T
)


