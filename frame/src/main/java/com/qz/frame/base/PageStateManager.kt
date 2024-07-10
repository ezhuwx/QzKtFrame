package com.qz.frame.base

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.qz.frame.R
import com.qz.frame.interfaces.OnRefreshStateChangeListener
import com.qz.frame.net.ApiException
import com.qz.frame.net.ExceptionHandler
import com.qz.frame.net.NetDialog
import com.qz.frame.net.ResponseException
import com.qz.frame.utils.ToastUtil
import com.qz.frame.utils.isInvalidClick
import com.qz.frame.utils.longShow
import com.qz.frame.utils.resString
import com.qz.frame.utils.shortShow
import java.util.UUID

/**
 * @author : ezhuwx
 * Describe :页面状态管理
 * Designed on 2023/12/11
 * E-mail : ezhuwx@163.com
 * Update on 14:06 by ezhuwx
 */
open class PageStateManager(
    private val context: FragmentActivity,
    private val owner: LifecycleOwner,
    private val viewModel: BaseViewModel,
    private val isObserveViewModelRequest: Boolean = true
) {

    /**
     * 是否是状态页
     */
    private var isStatePage = false

    /**
     * 所有ViewModel
     */
    private var vmList = mutableListOf<BaseViewModel>()

    /**
     * Loading
     * */
    private val mNetDialog by lazy { NetDialog(context as AppCompatActivity) }

    /**
     * 网络错误页
     */
    private var viewNetError: View? = null

    /**
     * 未知错误页
     */
    private var viewUnknownError: View? = null

    /**
     * 空数据页
     */
    private var viewEmpty: View? = null

    /**
     * 加载页
     */
    private var viewLoading: View? = null

    /**
     * 主页面
     */
    private var viewMain: ViewGroup? = null

    /**
     * 主页面父布局
     */
    private var parent: ViewGroup? = null

    /**
     * 当前状态
     */
    private var currentState = PageState.STATE_MAIN

    /**
     * 错误提示已显示
     */
    private var isErrorToastShowed = false

    /**
     * 加载页面ID
     * 根布局ID必须为：loading_root
     */
    var loadingLayoutId: Int = StatePageLayout.LOADING_LAYOUT_ID

    /**
     * 空数据页面ID
     * 根布局ID必须为：empty_root
     */
    var emptyLayoutId: Int = StatePageLayout.EMPTY_LAYOUT_ID

    /**
     * 网络错误页面ID
     * 根布局ID必须为：net_error_root
     */
    var netErrorLayoutId: Int = StatePageLayout.NET_ERROR_LAYOUT_ID

    /**
     * 未知错误页面ID
     * 根布局ID必须为：unknown_error_root
     */
    var unknownErrorLayoutId: Int = StatePageLayout.UNKNOWN_ERROR_LAYOUT_ID

    /**
     * 加载提示
     */
    var loadingMsg: String? = context.getString(R.string.app_name)

    /**
     * 空数据提示
     */
    var emptyResourceMsg: String? = context.getString(R.string.none_data)

    /**
     * 未知错误提示
     */
    var unknownResourceMsg: String? = context.getString(R.string.unknown_error)

    /**
     * 跳过加载状态页
     */
    var isSkipPageLoading = mutableMapOf<String, Boolean>()

    /**
     * 跳过加载状态页，及Loading弹窗
     */
    var isSkipAllLoading = mutableMapOf<String, Boolean>()

    /**
     * 跳过错误状态页
     */
    var isSkipAllError = mutableMapOf<String, Boolean>()

    /**
     * 跳过错误状态页，及Toast错误提示
     */
    var isSkipPageError = mutableMapOf<String, Boolean>()

    /**
     * 跳过主页面加载
     */
    var isSkipMainState = mutableMapOf<String, Boolean>()

    /**
     * 页面管理编码
     */
    var pageManageCode = UUID.randomUUID().toString()

    /**
     * 加载状态变化监听
     */
    var onRefreshStateChangeListener: OnRefreshStateChangeListener? = null

    /**
     * 页面状态变化监听
     */
    var onPageStateChangeListener: OnPageStateChangeListener? = null

    init {
        //监听ViewModel请求
        onAddObserve(viewModel)
    }

    /**
     * 初始化状态布局
     */
    open fun onInitStatePage() {
        //Activity主界面
        try {
            viewMain = context.findViewById(R.id.view_main)
            isStatePage = true
        } catch (_: Exception) {
            isStatePage = false
        }
        //初始化
        onInitView()
    }

    /**
     * 初始化
     */
    open fun onInitView() {
        if (isStatePage) {
            //view_main 父布局检查
            check(viewMain?.parent is ViewGroup) { context.getString(R.string.error_view_main_parent_not_view_group) }
            //父布局
            parent = viewMain?.parent as ViewGroup
        }
        //默认显示加载页面
        stateLoading()
    }

    /**
     * 请求监听
     *
     */
    open fun onAddObserve(viewModel: BaseViewModel) {
        //保存所有VM
        vmList.add(viewModel)
        //需要监听请求
        if (isObserveViewModelRequest) viewModel.run {
            start.observe(owner) {
                //开始
                if (isCustomOrPageRequest(it)) {
                    onRequestStart(it.requestCode)
                    onPageStateChangeListener?.onRequestStart(it.requestCode)
                }
            }
            success.observe(owner) {
                //成功
                if (isCustomOrPageRequest(it)) {
                    onRequestSuccess(it.requestCode)
                    onPageStateChangeListener?.onRequestSuccess(it.requestCode)
                }
            }
            error.observe(owner) {
                //报错
                if (isCustomOrPageRequest(it)) {
                    onRequestError(it.requestCode, it.error)
                    onPageStateChangeListener?.onRequestError(it.requestCode, it.error)
                }
            }
            finally.observe(owner) {
                //结束
                if (isCustomOrPageRequest(it)) {
                    onRequestFinally(it.requestCode, it.isSuccess == true)
                    onPageStateChangeListener?.onRequestFinally(
                        it.requestCode, it.isSuccess == true
                    )
                }
            }
        }
    }

    /**
     * 自定义或本页请求
     */
    private fun isCustomOrPageRequest(data: BaseViewModel.StateCallbackData): Boolean {
        return data.pageManagerCode == null || pageManageCode == data.pageManagerCode
    }

    /**
     *  DialogLoading 显示
     *
     */
    open fun stateDialogLoading() {
        if (!mNetDialog.isShowing) {
            mNetDialog.show()
        }
    }

    open fun stateDialogDismiss() {
        if (mNetDialog.isShowing) {
            mNetDialog.dismiss()
        }
    }

    open fun stateDialogLoading(isCancel: Boolean, loading: String?) {
        if (!mNetDialog.isShowing) {
            mNetDialog.setCancelable(isCancel)
            mNetDialog.showLoadingText(loading)
            mNetDialog.show()
        }
    }

    /**
     *  接口请求开始，子类可以重写此方法做一些操作
     *  */
    open fun onRequestStart(requestCode: String?) {
        when {
            isStatePage -> {
                isErrorToastShowed = false
                stateLoading(requestCode)
            }

            isSkipAllLoading[requestCode] != true -> stateDialogLoading()
        }
    }

    /**
     *  接口请求成功，子类可以重写此方法做一些操作
     *  */
    open fun onRequestSuccess(requestCode: String?) {
        if (isStatePage && isSkipMainState[requestCode] != true) {
            if (onPageStateChangeListener?.stateEmptyCondition() == true) stateEmpty()
            else stateMain()
        }
    }

    /**
     * 接口请求完毕，子类可以重写此方法做一些操作
     * */
    open fun onRequestFinally(requestCode: String?, isSuccess: Boolean) {
        stateDialogDismiss()
    }

    /**
     *  接口请求出错，子类可以重写此方法做一些操作
     *  */
    open fun onRequestError(requestCode: String?, error: Exception?) {
        //处理一些已知异常
        showErrorTip(requestCode, error)
    }

    /**
     * 处理一些已知异常
     */
    open fun showErrorTip(requestCode: String?, error: Exception?) {
        when (error) {
            //服务器特殊错误处理
            is ApiException -> onPageStateChangeListener?.onServiceError(
                requestCode, error.code, error.message
            )
            //正常错误显示
            is ResponseException -> if (error.code == ExceptionHandler.NETWORK_ERROR) {
                //无网络提示
                stateNetError(requestCode)
                //其它异常提示
            } else stateUnknownError(requestCode, "(${error.code})${error.message}")
            //无提示信息错误显示
            else -> stateUnknownError(requestCode)
        }
    }

    /**
     * 网络错误
     */
    open fun stateNetError(requestCode: String? = null) {
        //加载框消失
        stateDialogDismiss()
        //分页加载跳过
        val isLoadSkip =
            onRefreshStateChangeListener?.stateError(context.getString(R.string.net_error)) == true
        //跳过
        if (isLoadSkip || currentState == PageState.STATE_NET_ERROR
            || isSkipAllError[requestCode] == true
        ) return
        //显示错误提示
        if ((!isStatePage || isSkipPageError[requestCode] == true) && !isErrorToastShowed) {
            isErrorToastShowed = true
            context.resources.getString(R.string.net_error).shortShow()
        }
        //显示错误页面
        else if (isSkipPageError[requestCode] != true) {
            //布局
            viewNetError = onInflateView(PageState.STATE_NET_ERROR)
            //错误重试事件
            viewNetError?.findViewById<View>(R.id.view_net_error_tv)?.setOnClickListener { v ->
                if (!isInvalidClick(v)) onPageStateChangeListener?.onErrorOrEmptyRetry(true)
            }
            //状态切换
            onChangeState(PageState.STATE_NET_ERROR)
        }

    }

    /**
     * 未知错误状态
     *
     * @param errorMsg 错误提示文字
     */
    open fun stateUnknownError(requestCode: String?, errorMsg: String) {
        this.unknownResourceMsg = errorMsg
        stateUnknownError(requestCode)
    }

    /**
     * 未知错误状态
     */
    open fun stateUnknownError(requestCode: String? = null) {
        //加载框消失
        stateDialogDismiss()
        //分页加载跳过
        val isLoadSkip = onRefreshStateChangeListener?.stateError(
            unknownResourceMsg ?: context.getString(R.string.unknown_error)
        ) == true
        //跳过
        if (isLoadSkip || currentState == PageState.STATE_UNKNOWN_ERROR
            || isSkipAllError[requestCode] == true
        ) return
        //显示错误提示
        if ((!isStatePage || isSkipPageError[requestCode] == true) && !isErrorToastShowed) {
            isErrorToastShowed = true
            unknownResourceMsg.shortShow()
        }
        //显示错误页面
        else if (isSkipPageError[requestCode] != true) {
            //布局
            viewUnknownError = onInflateView(PageState.STATE_UNKNOWN_ERROR)
            //未知错误提示文字
            viewUnknownError?.findViewById<TextView>(R.id.view_unknown_error_content_tv)?.run {
                text = unknownResourceMsg
            }
            //错误重试事件
            viewUnknownError?.findViewById<View>(R.id.view_unknown_error_tv)
                ?.setOnClickListener { v ->
                    if (!isInvalidClick(v)) onPageStateChangeListener?.onErrorOrEmptyRetry(true)
                }
            //状态切换
            onChangeState(PageState.STATE_UNKNOWN_ERROR)
        }
    }

    /**
     * 加载状态
     * */
    open fun stateLoading(requestCode: String? = null) {
        //跳过
        if (onRefreshStateChangeListener?.stateLoading() == true
            || currentState == PageState.STATE_LOADING
            || isSkipAllLoading[requestCode] == true
        ) return
        //是否显示弹窗Loading
        if (!isStatePage || isSkipPageLoading[requestCode] == true) stateDialogLoading()
        //状态切换
        else {
            //布局
            viewLoading = onInflateView(PageState.STATE_LOADING)
            viewLoading?.findViewById<TextView>(R.id.loading_tv)?.run {
                text = loadingMsg
            }
            onChangeState(PageState.STATE_LOADING)
        }
    }

    /**
     * 无数据状态
     */
    open fun stateEmpty() {
        //加载框消失
        stateDialogDismiss()
        //跳过
        if (isStatePage && onRefreshStateChangeListener?.stateEmpty() == true || currentState == PageState.STATE_EMPTY) return
        //布局
        viewEmpty = onInflateView(PageState.STATE_EMPTY)
        //无数据提示文字
        viewEmpty?.findViewById<TextView>(R.id.view_empty_content_tv)?.run {
            //自定义值
            text = emptyResourceMsg
        }
        //无数据重新获取事件
        viewEmpty?.findViewById<View>(R.id.view_empty_tv)?.setOnClickListener { v ->
            //重试
            if (!isInvalidClick(v)) onPageStateChangeListener?.onErrorOrEmptyRetry(false)
        }
        //状态切换
        onChangeState(PageState.STATE_EMPTY)
    }

    /**
     * 显示主视图
     */
    open fun stateMain() {
        stateDialogDismiss()
        onChangeState(PageState.STATE_MAIN)
    }

    /**
     * 状态切换
     */
    private fun onChangeState(newState: PageState) {
        //隐藏旧布局
        onGetStateView(currentState)?.isVisible = false
        //显示新布局
        onGetStateView(newState)?.isVisible = true
        //新状态
        currentState = newState
    }

    /**
     * 获取状态View
     */
    private fun onGetStateView(state: PageState): View? {
        return when (state) {
            PageState.STATE_MAIN -> viewMain
            PageState.STATE_LOADING -> viewLoading
            PageState.STATE_EMPTY -> viewEmpty
            PageState.STATE_NET_ERROR -> viewNetError
            PageState.STATE_UNKNOWN_ERROR -> viewUnknownError
        }
    }

    /**
     * 载入布局实例
     */
    private fun onInflateView(state: PageState): View? {
        return onGetStateView(state) ?: run {
            //布局Id
            val layoutResId = when (state) {
                PageState.STATE_MAIN -> R.layout.view_main
                PageState.STATE_LOADING -> loadingLayoutId
                PageState.STATE_EMPTY -> emptyLayoutId
                PageState.STATE_NET_ERROR -> netErrorLayoutId
                PageState.STATE_UNKNOWN_ERROR -> unknownErrorLayoutId
            }
            //布局载入
            View.inflate(context, layoutResId, parent)
            //实例View
            parent!!.findViewById(
                when (state) {
                    PageState.STATE_MAIN -> R.id.view_main
                    PageState.STATE_LOADING -> R.id.loading_root
                    PageState.STATE_EMPTY -> R.id.empty_root
                    PageState.STATE_NET_ERROR -> R.id.net_error_root
                    PageState.STATE_UNKNOWN_ERROR -> R.id.unknown_error_root
                }
            )
        }
    }


    /**
     * 清除监听
     */
    open fun onClearNetObservers() {
        vmList.forEach {
            it.start.removeObservers(owner)
            it.success.removeObservers(owner)
            it.error.removeObservers(owner)
            it.finally.removeObservers(owner)
        }
    }

}

/**
 * 页面状态监听
 */
interface OnPageStateChangeListener {
    /**
     *  接口请求开始，子类可以重写此方法做一些操作
     *  */
    fun onRequestStart(requestCode: String?) {
    }

    /**
     *  接口请求成功，子类可以重写此方法做一些操作
     *  */
    fun onRequestSuccess(requestCode: String?) {

    }

    /**
     *  接口请求完毕，子类可以重写此方法做一些操作
     *  */
    fun onRequestFinally(requestCode: String?, isSuccess: Boolean) {
    }

    /**
     *  接口请求出错，子类可以重写此方法做一些操作
     *  */
    fun onRequestError(requestCode: String?, it: Exception?) {
    }

    /**
     * 服务器特殊错误处理
     * ‘登录超时’等
     * */
    fun onServiceError(requestCode: String?, code: Int, message: String?) {

    }

    /**
     * 异常或空数据重试
     */
    fun onErrorOrEmptyRetry(isError: Boolean) {

    }

    /**
     * 提供状态为空的判断依据
     */
    fun stateEmptyCondition(): Boolean {
        return false
    }
}

/**
 * 页面状态
 */
enum class PageState {
    /**
     * 标准主状态
     */
    STATE_MAIN,

    /**
     * 加载中
     */
    STATE_LOADING,

    /**
     * 空数据
     */
    STATE_EMPTY,

    /**
     * 网络错误
     */
    STATE_NET_ERROR,

    /**
     * 未知异常
     */
    STATE_UNKNOWN_ERROR
}

/**
 * 全局内置状态页ID
 */
object StatePageLayout {

    /**
     * 加载页面ID
     * 根布局ID必须为：loading_root
     */
    var LOADING_LAYOUT_ID: Int = R.layout.view_progress

    /**
     * 空数据页面ID
     * 根布局ID必须为：empty_root
     */
    var EMPTY_LAYOUT_ID: Int = R.layout.view_empty

    /**
     * 网络错误页面ID
     * 根布局ID必须为：net_error_root
     */
    var NET_ERROR_LAYOUT_ID: Int = R.layout.view_net_error

    /**
     * 未知错误页面ID
     * 根布局ID必须为：unknown_error_root
     */
    var UNKNOWN_ERROR_LAYOUT_ID: Int = R.layout.view_unknown_error
}