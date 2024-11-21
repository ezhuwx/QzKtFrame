package com.qz.frame.base

import androidx.annotation.CallSuper

/**
 * Activity 状态页基类
 */

abstract class BaseStateActivity<VM : BaseViewModel> : BaseActivity<VM>() {

    @CallSuper
    override fun initBindView() {
        pageStateManager.onInitStatePage()
        pageStateManager.onPageStateChangeListener = object : OnPageStateChangeListener {
            override fun onErrorOrEmptyRetry(isError: Boolean) {
                this@BaseStateActivity.onErrorOrEmptyRetry(isError)
            }

            override fun stateEmptyCondition(): Boolean {
                return this@BaseStateActivity.stateEmptyCondition()
            }
        }
    }

    /**
     * 显示主视图
     */

    fun stateMain() {
        pageStateManager.stateMain()
    }

    /**
     * 加载状态
     * */
    fun stateLoading(isForce: Boolean = true) {
        pageStateManager.stateLoading(isForce = isForce)
    }

    /**
     * 无数据状态
     */
    fun stateEmpty() {
        pageStateManager.stateEmpty()
    }

    /**
     * 网络错误
     */
    fun stateNetError() {
        pageStateManager.stateNetError()
    }

    /**
     * 未知错误状态
     */
    fun stateUnknownError() {
        pageStateManager.stateUnknownError()
    }

    /**
     * 提供状态为空的判断依据
     */
    protected open fun stateEmptyCondition(): Boolean {
        return false
    }

    /**
     * 失败点击重试
     *
     * @param isError 错误状态
     */
    protected abstract fun onErrorOrEmptyRetry(isError: Boolean)
}