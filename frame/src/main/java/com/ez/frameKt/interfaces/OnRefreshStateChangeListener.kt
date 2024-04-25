package com.ez.frameKt.interfaces

/**
 * @author : ezhuwx
 * Describe :状态页状态回调
 * Designed on 2023/4/21
 * E-mail : ezhuwx@163.com
 * Update on 13:14 by ezhuwx
 */
interface OnRefreshStateChangeListener {

    /**
     * 错误状态
     *
     * @param errorMsg 错误提示文字
     */
    fun stateError(errorMsg: String):Boolean

    /**
     *  加载状态
     * */
    fun stateLoading():Boolean

    /**
     *  无数据状态
     *
     */
    fun stateEmpty():Boolean
}