package com.ez.kotlin.frame.base

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.base.BaseApplication.Companion.mContext
import com.ez.kotlin.frame.utils.ToastUtil
import com.ez.kotlin.frame.utils.isInvalidClick
import kotlinx.coroutines.TimeoutCancellationException
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

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
    private var isSkipError = false
    private var currentState = STATE_MAIN
    private var isNetErrorViewAdded = false
    private var isUnknownErrorViewAdded = false
    private var isEmptyViewAdded = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startObserve()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(viewModel)
    }

    /**
     * TODO 初始化页面
     *
     */
    override fun initView() {
        viewMain = findViewById(R.id.view_main)
        checkNotNull(viewMain) { "The subclass of RootActivity must contain a View named 'view_main'." }
        check(viewMain!!.parent is ViewGroup) { "view_main's ParentView should be a ViewGroup." }
        mParent = viewMain!!.parent as ViewGroup
        View.inflate(mContext, R.layout.view_progress, mParent)
        viewLoading = mParent!!.findViewById(R.id.view_loading_fl)
        viewLoading!!.visibility = View.GONE
        viewMain!!.visibility = View.GONE
    }



    /**
     * TODO 请求监听
     *
     */
    private fun startObserve() {
        viewModel.run {
            getStart().observe(this@BaseStateActivity, {
                requestStart(it)
            })
            getError().observe(this@BaseStateActivity, {
                requestError(it)
            })
            getFinally().observe(this@BaseStateActivity, {
                requestFinally(it)
            })
        }
    }

    /**
     *  接口请求开始，子类可以重写此方法做一些操作
     *  */
    open fun requestStart(it: Boolean) {
        stateLoading()
    }

    /**
     *  接口请求完毕，子类可以重写此方法做一些操作
     *  */
    open fun requestFinally(it: Int?) {
        stateMain()
    }

    /**
     *  接口请求出错，子类可以重写此方法做一些操作
     *  */
    open fun requestError(it: Exception?) {
        //处理一些已知异常
        it?.run {
            when (it) {
                is CancellationException -> Log.d(
                    getString(R.string.cancel_request),
                    it.message.toString()
                )
                is TimeoutCancellationException ->
                    stateUnknownError(getString(R.string.request_time_out))
                is HttpException -> {
                    if (it.code() == 504) stateNetError()
                    else ToastUtil(this@BaseStateActivity).shortShow(it.message.toString())
                }
                else -> stateUnknownError(it.message.toString())
            }
        }
    }

    /**
     * 失败点击重试
     *
     * @param isError 错误状态
     */
    protected abstract fun onErrorOrEmptyRetry(isError: Boolean)

    open fun stateNetError() {
        if (currentState == STATE_NET_ERROR || isSkipError) {
            if (isSkipError) {
                isSkipError = false
            }
            return
        }
        if (!isNetErrorViewAdded) {
            isNetErrorViewAdded = true
            val mNetErrorResource: Int = R.layout.view_net_error
            View.inflate(mContext, mNetErrorResource, mParent)
            viewNetError = mParent!!.findViewById(R.id.view_net_error)
            mParent!!.findViewById<View>(R.id.view_net_error_tv)
                .setOnClickListener { v: View? ->
                    if (!isInvalidClick(v!!)) {
                        onErrorOrEmptyRetry(true)
                    }
                }
            checkNotNull(viewNetError) { "A View should be named 'view_error' in ErrorLayoutResource." }
        }
        hideCurrentView()
        currentState = STATE_NET_ERROR
        viewNetError!!.visibility = View.VISIBLE
    }

    open fun stateUnknownError(errorMsg: String) {
        this.mUnknownResourceMsg = errorMsg
        stateUnknownError()
    }

    open fun stateUnknownError() {
        if (currentState == STATE_UNKNOWN_ERROR || isSkipError) {
            if (isSkipError) {
                isSkipError = false
            }
            return
        }
        if (!isUnknownErrorViewAdded) {
            isUnknownErrorViewAdded = true
            View.inflate(mContext, mUnknownErrorResource, mParent)
            viewUnknownError = mParent!!.findViewById(R.id.view_unknown_error)
            val tipMsg: TextView =
                mParent!!.findViewById(R.id.view_unknown_error_content_tv)
            if (!TextUtils.isEmpty(mUnknownResourceMsg)) {
                tipMsg.text = mUnknownResourceMsg
            }
            mParent!!.findViewById<View>(R.id.view_unknown_error_tv)
                .setOnClickListener { v: View? ->
                    if (!isInvalidClick(v!!)) {
                        onErrorOrEmptyRetry(true)
                    }
                }
            checkNotNull(viewUnknownError) { "A View should be named 'view_error' in ErrorLayoutResource." }
        }
        hideCurrentView()
        currentState = STATE_UNKNOWN_ERROR
        viewUnknownError!!.visibility = View.VISIBLE
    }

    open fun stateLoading() {
        if (currentState == STATE_LOADING || isSkipLoading) {
            if (isSkipLoading) {
                isSkipLoading = false
            }
            return
        }
        hideCurrentView()
        currentState = STATE_LOADING
        viewLoading!!.visibility = View.VISIBLE
    }

    open fun stateEmpty() {
        if (currentState == STATE_EMPTY) {
            return
        }
        if (!isEmptyViewAdded) {
            isEmptyViewAdded = true
            View.inflate(mContext, mEmptyResource, mParent)
            viewEmpty = mParent!!.findViewById(R.id.view_empty)
            val tipMsg: TextView = mParent!!.findViewById(R.id.view_empty_content_tv)
            if (!TextUtils.isEmpty(mEmptyResourceMsg)) {
                tipMsg.text = mEmptyResourceMsg
            }
            mParent!!.findViewById<View>(R.id.view_empty_tv)
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


    open fun stateMain() {
        hideCurrentView()
        currentState = STATE_MAIN
        viewMain?.visibility = View.VISIBLE
    }

    /**
     * 隐藏状态View
     */
    private fun hideCurrentView() {
        when (currentState) {
            STATE_MAIN -> viewMain!!.visibility = View.GONE
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
     * 跳过loading
     * @param skipLoading
     */
    open fun setSkipLoading(skipLoading: Boolean) {
        isSkipLoading = skipLoading
    }

    /**
     * 跳过错误提示
     */
    open fun setSkipError(skipError: Boolean) {
        isSkipError = skipError
    }
}