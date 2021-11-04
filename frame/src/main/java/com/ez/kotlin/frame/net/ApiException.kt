package com.ez.kotlin.frame.net

/**
 * @author : ezhuwx
 * Describe :Api错误
 * @param code 错误码
 * @param message 错误信息
 * Designed on 2021/11/4
 * E-mail : ezhuwx@163.com
 * Update on 13:22 by ezhuwx
 */
open class ApiException(val code: Int, override val message: String) : Exception()