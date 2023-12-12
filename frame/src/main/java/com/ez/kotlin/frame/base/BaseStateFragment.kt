package com.ez.kotlin.frame.base

import android.view.View
import androidx.annotation.CallSuper
import com.ez.kotlin.frame.utils.*

abstract class BaseStateFragment<VM : BaseViewModel> : BaseFragment<VM>() {

    @CallSuper
    override fun initBindView(view: View) {
        pageStateManager.onInitStatePage(view)
        pageStateManager.onPageStateChangeListener = object : OnPageStateChangeListener {
            override fun onErrorOrEmptyRetry(isError: Boolean) {
                this@BaseStateFragment.onErrorOrEmptyRetry(isError)
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