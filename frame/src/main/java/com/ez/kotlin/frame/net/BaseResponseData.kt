package com.ez.kotlin.frame.net

/**
 * @author : ezhuwx
 * Describe : 数据类基类
 * Designed on 2021/10/27
 * E-mail : ezhuwx@163.com
 * Update on 14:42 by ezhuwx
 */
open class BaseResponseData<out T>(
    /**
     * 状态码
     */
    var statusCode: Int,
    /**
     * 总数量
     */
    var totalCount: Int,
    /**
     * 提示信息
     */
    var message: String,
    /**
     * 数据
     */
    val data: T
)


