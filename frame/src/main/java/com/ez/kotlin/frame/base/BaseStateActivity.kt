package com.ez.kotlin.frame.base

import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CallSuper
import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.base.BaseApplication.Companion.mContext
import com.ez.kotlin.frame.net.ApiException
import com.ez.kotlin.frame.net.ResponseException
import com.ez.kotlin.frame.utils.NetWorkUtil
import com.ez.kotlin.frame.utils.ToastUtil
import com.ez.kotlin.frame.utils.isInvalidClick


abstract class BaseStateActivity<VM : BaseViewModel> : BaseActivity<VM>() {
    companion object {
        const val STATE_MAIN: Int = 0x00
        const val STATE_LOADING = 0x01
        const val STATE_NET_ERROR = 0x02
        const val STATE_EMPTY = 0x03
        const val STATE_UNKNOWN_ERROR = 0x04
    }


    private var viewNetError: View? = null
    private var viewUnknownError: View? = null
    private var viewEmpty: View? = null
    private var viewLoading: View? = null
    private var viewMain: ViewGroup? = null
    private var mParent: ViewGroup? = null
    private var mEmptyResource: Int = R.layout.view_empty
    private var mEmptyResourceMsg: String? = null
    private var mUnknownErrorResource: Int = R.layout.view_unknown_error
    private var mUnknownResourceMsg: String? = null
    private var isSkipLoading = false
    private var isSkipAllLoading = false
    private var isOnceSkip = false
    private var isSkipAllError = false
    private var isSkipError = false
    private var currentState = STATE_MAIN
    private var isNetErrorViewAdded = false
    private var isUnknownErrorViewAdded = false
    private var isEmptyViewAdded = false
    private var isErrorToastShowed = false

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }

    /**
     * TODO 初始化页面
     *
     */
    @CallSuper
    override fun initBindView() {
        viewMain = findViewById(R.id.view_main)
        checkNotNull(viewMain) { "The subclass of RootActivity must contain a View named 'view_main'." }
        viewMain?.let { main ->
            check(main.parent is ViewGroup) { "view_main's ParentView should be a ViewGroup." }
            mParent = main.parent as ViewGroup
            mParent?.let { parent ->
                View.inflate(mContext, R.layout.view_progress, parent)
                viewLoading = parent.findViewById(R.id.view_loading_fl)
                viewLoading?.visibility = View.GONE
                main.visibility = View.GONE
            }
        }
    }


    /**
     *  接口请求开始，子类可以重写此方法做一些操作
     *  */
    override fun onRequestStart(it: Boolean) {
        isErrorToastShowed = false
        stateLoading()
    }

    /**
     *  接口请求成功，子类可以重写此方法做一些操作
     *  */
    override fun onRequestSuccess(it: Boolean) {
        stateMain()
        isSkipAllLoading = true
        isSkipError = true
    }

    /**
     *  接口请求完毕，子类可以重写此方法做一些操作
     *  */
    override fun onRequestFinally(it: Int?) {
    }

    /**
     *  接口请求出错，子类可以重写此方法做一些操作
     *  */
    override fun onRequestError(it: Exception?) {
        //处理一些已知异常
        it?.run {
            if (NetWorkUtil.isNoProxyConnected(this@BaseStateActivity)) {
                when (it) {
                    //服务器特殊错误处理
                    is ApiException -> {
                        onServiceError(it.code, it.message)
                    }
                    //正常错误显示
                    is ResponseException -> {
                        stateUnknownError("(${it.code})${it.message}")
                    }
                    //无提示信息错误显示
                    else -> {
                        stateUnknownError()
                    }
                }
            } else {
                //无网络提示
                stateNetError()
            }
        }
    }

    /**
     * 服务器特殊错误处理
     * ‘登录超时’等
     * */
    override fun onServiceError(code: Int, message: String?) {

    }

    /**
     * 失败点击重试
     *
     * @param isError 错误状态
     */
    protected abstract fun onErrorOrEmptyRetry(isError: Boolean)

    /**
     * TODO 网络错误
     *
     */
    open fun stateNetError() {
        mParent?.let { parent ->
            stateDialogDismiss()
            if (currentState == STATE_NET_ERROR || isSkipError || isSkipAllError) {
                if (isSkipAllError) if (isOnceSkip) isSkipAllError = false
                else if (isSkipError && !isErrorToastShowed) {
                    ToastUtil().shortShow(R.string.net_error)
                    isErrorToastShowed = true
                    if (isOnceSkip) isSkipError = false
                }
                return
            }
            if (!isNetErrorViewAdded) {
                //网络错误
                isNetErrorViewAdded = true
                //网络错误UI
                val mNetErrorResource: Int = R.layout.view_net_error
                View.inflate(mContext, mNetErrorResource, parent)
                viewNetError = parent.findViewById(R.id.view_net_error)
                //错误重试事件
                parent.findViewById<View>(R.id.view_net_error_tv)
                    .setOnClickListener { v: View? ->
                        if (!isInvalidClick(v!!)) {
                            onErrorOrEmptyRetry(true)
                        }
                    }
                checkNotNull(viewNetError) { "A View should be named 'view_error' in ErrorLayoutResource." }
            }
            hideCurrentView()
            currentState = STATE_NET_ERROR
            viewNetError?.visibility = View.VISIBLE
        }
    }

    /**
     * TODO 未知错误状态
     *
     * @param errorMsg 错误提示文字
     */
    open fun stateUnknownError(errorMsg: String) {
        this.mUnknownResourceMsg = errorMsg
        stateUnknownError()
    }

    /**
     * TODO 未知错误状态
     */
    open fun stateUnknownError() {
        mParent?.let { parent ->
            stateDialogDismiss()
            if (currentState == STATE_UNKNOWN_ERROR || isSkipError || isSkipAllError) {
                if (isSkipAllError) if (isOnceSkip) isSkipAllError = false
                else if (isSkipError && !isErrorToastShowed) {
                    ToastUtil().shortShow(R.string.net_error)
                    isErrorToastShowed = true
                    if (isOnceSkip) isSkipError = false
                }
                return
            }
            if (!isUnknownErrorViewAdded) {
                //未知错误
                isUnknownErrorViewAdded = true
                //未知错误UI
                View.inflate(mContext, mUnknownErrorResource, parent)
                viewUnknownError = parent.findViewById(R.id.view_unknown_error)
                //未知错误提示文字
                val tipMsg: TextView = parent.findViewById(R.id.view_unknown_error_content_tv)
                if (!TextUtils.isEmpty(mUnknownResourceMsg)) {
                    tipMsg.text = mUnknownResourceMsg
                }
                //错误重试事件
                parent.findViewById<View>(R.id.view_unknown_error_tv)
                    .setOnClickListener { v: View? ->
                        if (!isInvalidClick(v!!)) {
                            onErrorOrEmptyRetry(true)
                        }
                    }
                checkNotNull(viewUnknownError) { "A View should be named 'view_error' in ErrorLayoutResource." }
            }
            hideCurrentView()
            currentState = STATE_UNKNOWN_ERROR
            viewUnknownError?.visibility = View.VISIBLE
        }
    }

    /**
     * TODO 加载状态
     * */
    open fun stateLoading() {
        if (currentState == STATE_LOADING || isSkipLoading || isSkipAllLoading) {
            if (isSkipAllLoading) if (isOnceSkip) isSkipAllLoading = false
            else if (isSkipLoading) {
                stateDialogLoading()
                if (isOnceSkip) isSkipLoading = false
            }
            return
        }
        hideCurrentView()
        currentState = STATE_LOADING
        viewLoading?.visibility = View.VISIBLE
    }

    /**
     * TODO 无数据状态
     *
     */
    open fun stateEmpty() {
        mParent?.let { parent ->
            stateDialogDismiss()
            if (currentState == STATE_EMPTY) {
                return
            }
            if (!isEmptyViewAdded) {
                //无数据
                isEmptyViewAdded = true
                //无数据UI
                View.inflate(mContext, mEmptyResource, mParent)
                viewEmpty = parent.findViewById(R.id.view_empty)
                //无数据提示文字
                val tipMsg: TextView = parent.findViewById(R.id.view_empty_content_tv)
                if (!TextUtils.isEmpty(mEmptyResourceMsg)) {
                    tipMsg.text = mEmptyResourceMsg
                }
                //无数据重新获取事件
                parent.findViewById<View>(R.id.view_empty_tv)
                    .setOnClickListener { v: View? ->
                        if (!isInvalidClick(v!!)) {
                            onErrorOrEmptyRetry(false)
                        }
                    }
                checkNotNull(viewEmpty) { "A View should be named 'view_empty' in ErrorLayoutResource." }
            }
            hideCurrentView()
            currentState = STATE_EMPTY
            viewEmpty?.visibility = View.VISIBLE
        }
    }


    /**
     * TODO 主视图
     *
     */
    open fun stateMain() {
        stateDialogDismiss()
        hideCurrentView()
        currentState = STATE_MAIN
        viewMain?.visibility = View.VISIBLE
    }

    /**
     * 隐藏状态View
     */
    private fun hideCurrentView() {
        when (currentState) {
            STATE_MAIN -> viewMain?.visibility = View.GONE
            STATE_LOADING -> viewLoading?.visibility = View.GONE
            STATE_EMPTY -> viewEmpty?.visibility = View.GONE
            STATE_NET_ERROR -> viewNetError?.visibility = View.GONE
            STATE_UNKNOWN_ERROR -> viewUnknownError?.visibility = View.GONE
            else -> {
            }
        }
    }

    /**
     * 设置错误提示布局
     */
    open fun setErrorLayout(errorLayoutResource: Int) {
        this.mUnknownErrorResource = errorLayoutResource
    }

    /**
     * 设置空数据布局
     */
    open fun setEmptyLayout(emptyLayoutResource: Int) {
        this.mEmptyResource = emptyLayoutResource
    }

    /**
     * 设置错误提示布局提示文字
     */
    open fun setErrorResourceMsg(error: String) {
        this.mUnknownResourceMsg = error
    }

    /**
     * 设置空数据布局提示文字
     */
    open fun setEmptyResourceMsg(empty: String) {
        this.mEmptyResourceMsg = empty
    }

    /**
     * 跳过一次
     * @param skipOnce
     */
    open fun setSkipOnce(skipOnce: Boolean) {
        isOnceSkip = skipOnce
    }


    /**
     * 跳过内部loading 及弹出loading
     * @param isSkipAllLoading
     */
    open fun setSkipAllLoading(isSkipAllLoading: Boolean) {
        this.isSkipAllLoading = isSkipAllLoading
    }

    /**
     * 跳过loading
     * @param skipLoading
     */
    open fun setSkipLoading(skipLoading: Boolean) {
        isSkipLoading = skipLoading
    }

    /**
     * 跳过内部错误布局及Toast提示
     * @param isSkipAllError
     */
    open fun setSkipAllError(isSkipAllError: Boolean) {
        this.isSkipAllError = isSkipAllError
    }

    /**
     * 跳过错误提示
     */
    open fun setSkipError(skipError: Boolean) {
        isSkipError = skipError
    }
}