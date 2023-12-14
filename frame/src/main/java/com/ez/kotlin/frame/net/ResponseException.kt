package com.ez.kotlin.frame.net

open class ResponseException(throwable: Throwable?, open var code: Int) :
    Exception(throwable) {
    override var message: String? = throwable?.message
}