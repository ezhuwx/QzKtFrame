package com.ez.kotlin.frame.base

import androidx.annotation.CallSuper
/**
 * Activity 状态页基类
 */

abstract class BaseStateActivity<VM : BaseViewModel> : BaseActivity<VM>() {

    @CallSuper
    override fun initBindView() {
       pageStateManager.onInitStatePage(binding.root)
       pageStateManager.onPageStateChangeListener = object : OnPageStateChangeListener {
            override fun onErrorOrEmptyRetry(isError: Boolean) {
                this@BaseStateActivity.onErrorOrEmptyRetry(isError)
            }
        }
    }


    fun stateMain() {
       pageStateManager.stateMain()
    }

    fun stateLoading() {
       pageStateManager.stateLoading()
    }

    fun stateEmpty() {
       pageStateManager.stateEmpty()
    }

    fun stateNetError() {
       pageStateManager.stateNetError()
    }

    fun stateUnknownError() {
       pageStateManager.stateUnknownError()
    }

    /**
     * 失败点击重试
     *
     * @param isError 错误状态
     */
    protected abstract fun onErrorOrEmptyRetry(isError: Boolean)
}