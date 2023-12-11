package com.ez.kotlin.frame.base

import android.view.View
import androidx.annotation.CallSuper
import com.ez.kotlin.frame.interfaces.OnRefreshStateChangeListener
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

    fun stateEmpty() {
        pageStateManager.stateEmpty()
    }

    /**
     * 失败点击重试
     *
     * @param isError 错误状态
     */
    protected abstract fun onErrorOrEmptyRetry(isError: Boolean)
}