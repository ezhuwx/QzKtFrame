package com.ez.frameKt.interfaces

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2023/12/14
 * E-mail : ezhuwx@163.com
 * Update on 11:31 by ezhuwx
 */
fun interface OnEditorActionListener {
    fun onAction(actionId: Int): Boolean
}

fun interface OnEditorSearchActionListener {
    fun onSearchAction(): Boolean
}