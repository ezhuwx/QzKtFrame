package com.qz.frame.base

import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
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
import com.qz.frame.utils.isInvalidClick
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
    private val isObserveViewModelRequest: Boolean = true,
    viewModel: BaseViewModel
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
    private val mNetDialog by lazy {
        NetDialog(context as AppCompatActivity).apply {
            setOnDismissListener { onLoadingCancelListener?.onCancel() }
        }
    }

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
    var loadingLayoutId: Int? = null

    /**
     * 空数据页面ID
     * 根布局ID必须为：empty_root
     */
    var emptyLayoutId: Int? = null

    /**
     * 网络错误页面ID
     * 根布局ID必须为：net_error_root
     */
    var netErrorLayoutId: Int? = null

    /**
     * 未知错误页面ID
     * 根布局ID必须为：unknown_error_root
     */
    var unknownErrorLayoutId: Int? = null

    /**
     * 加载提示
     */
    var loadingMsg: String? = null

    /**
     * 空数据提示
     */
    var emptyResourceMsg: String? = null

    /**
     * 未知错误提示
     */
    var unknownResourceMsg: String? = null

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

    /**
     * Dialog Loading加载取消监听
     */
    var onLoadingCancelListener: OnLoadingCancelListener? = null

    /**
     * 所有请求编码
     */
    private var pageControlRequestCode = mutableSetOf<String>()

    init {
        //监听ViewModel请求
        onAddObserve(viewModel)
    }

    /**
     * 初始化状态布局
     */
    open fun onInitStatePage(view: View? = null) {
        //Activity主界面
        viewMain = if (view != null) view.findViewById(R.id.view_main)
        else context.findViewById(R.id.view_main)
        isStatePage = viewMain != null
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
            //默认显示加载页面
            stateLoading()
        }
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
                    onRequestStart(it.requestCode, it.isForceLoading)
                    onPageStateChangeListener?.onRequestStart(it.requestCode, it.isForceLoading)
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

    open fun stateDialogLoading(isForce: Boolean, loading: String? = null) {
        if (!mNetDialog.isShowing) {
            mNetDialog.setCancelable(!isForce)
            mNetDialog.showLoadingText(loading)
            mNetDialog.show()
        }
    }

    /**
     *  接口请求开始，子类可以重写此方法做一些操作
     *  */
    open fun onRequestStart(requestCode: String?, isForce: Boolean = false) {
        //保存可控制页面完成状态的请求的请求码
        if (isSkipMainState[requestCode] != true) requestCode?.let { pageControlRequestCode.add(it) }
        //重置错误提示判定
        isErrorToastShowed = false
        when {
            isStatePage -> stateLoading(requestCode)
            isSkipAllLoading[requestCode] != true -> stateDialogLoading(isForce)
        }
    }

    /**
     *  接口请求成功，子类可以重写此方法做一些操作
     *  */
    open fun onRequestSuccess(requestCode: String?) {
        if (isSkipMainState[requestCode] != true) {
            //清除已完成的请求
            pageControlRequestCode.remove(requestCode)
            //所有可控制页面完成状态的请求，均已完成
            if (pageControlRequestCode.isEmpty()) {
                if (isStatePage) {
                    if (onPageStateChangeListener?.stateEmptyCondition() == true) stateEmpty()
                    else stateMain()
                } else stateDialogDismiss()
            }
        }
    }

    /**
     * 接口请求完毕，子类可以重写此方法做一些操作
     * */
    open fun onRequestFinally(requestCode: String?, isSuccess: Boolean) {
        //清除已完成的请求(非成功状态)
        pageControlRequestCode.remove(requestCode)
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
            is ApiException -> {
                stateDialogDismiss()
                onPageStateChangeListener?.onServiceError(
                    requestCode, error.code, error.message
                )
            }
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
        if ((!isStatePage || isSkipPageError[requestCode] == true)) {
            if (!isErrorToastShowed) {
                isErrorToastShowed = true
                context.resources.getString(R.string.net_error).shortShow()
            }
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
        unknownResourceMsg =
            unknownResourceMsg ?: PageStateOptionManager.instance.unknownResourceMsg
                    ?: context.getString(R.string.unknown_error)
        //加载框消失
        stateDialogDismiss()
        //分页加载跳过
        val isLoadSkip = onRefreshStateChangeListener?.stateError(unknownResourceMsg!!) == true
        //跳过
        if (isLoadSkip || currentState == PageState.STATE_UNKNOWN_ERROR
            || isSkipAllError[requestCode] == true
        ) return
        //显示错误提示
        if ((!isStatePage || isSkipPageError[requestCode] == true)) {
            if (!isErrorToastShowed) {
                isErrorToastShowed = true
                unknownResourceMsg.shortShow()
            }
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
    open fun stateLoading(requestCode: String? = null, isForce: Boolean = false) {
        //跳过
        if (onRefreshStateChangeListener?.stateLoading() == true
            || currentState == PageState.STATE_LOADING
            || isSkipAllLoading[requestCode] == true
        ) return
        //是否显示弹窗Loading
        if (!isStatePage || isSkipPageLoading[requestCode] == true) stateDialogLoading(isForce)
        //状态切换
        else {
            //布局
            viewLoading = onInflateView(PageState.STATE_LOADING)
            viewLoading?.findViewById<TextView>(R.id.loading_tv)?.run {
                //优先级本页面配置 > 全局配置 > App名称
                text = loadingMsg ?: PageStateOptionManager.instance.loadingText
                        ?: BaseApplication.instance.config.getAppName()
                //字体颜色
                setTextColor(
                    PageStateOptionManager.instance.loadingTextColor
                        ?: context.resources.getColor(R.color.tip_text_color)
                )
                //字体大小
                textSize = PageStateOptionManager.instance.loadingTextSize ?: 28f
                //字体样式
                PageStateOptionManager.instance.loadingTextFontAsset?.let { asset ->
                    typeface = Typeface.createFromAsset(context.assets, asset)
                }
                //字体 > 字体样式
                PageStateOptionManager.instance.loadingTextStyle?.let { style ->
                    typeface = style
                }
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
            text = emptyResourceMsg ?: PageStateOptionManager.instance.emptyResourceMsg
                    ?: context.getString(R.string.none_data)
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
            //优先级本页面配置 > 全局配置
            val layoutResId = when (state) {
                PageState.STATE_LOADING -> loadingLayoutId
                    ?: PageStateOptionManager.instance.loadingLayoutId

                PageState.STATE_EMPTY -> emptyLayoutId
                    ?: PageStateOptionManager.instance.emptyLayoutId

                PageState.STATE_NET_ERROR -> netErrorLayoutId
                    ?: PageStateOptionManager.instance.netErrorLayoutId

                PageState.STATE_UNKNOWN_ERROR -> unknownErrorLayoutId
                    ?: PageStateOptionManager.instance.unknownErrorLayoutId

                else -> null
            }
            if (parent != null) {
                //布局载入
                layoutResId?.let { View.inflate(context, it, parent) }
                //实例View
                parent!!.findViewById<View>(
                    when (state) {
                        PageState.STATE_MAIN -> R.id.view_main
                        PageState.STATE_LOADING -> R.id.loading_root
                        PageState.STATE_EMPTY -> R.id.empty_root
                        PageState.STATE_NET_ERROR -> R.id.net_error_root
                        PageState.STATE_UNKNOWN_ERROR -> R.id.unknown_error_root
                    }
                )
            } else null
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
    fun onRequestStart(requestCode: String?, isForceLoading: Boolean) {
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
 * Dialog Loading加载取消监听
 */
fun interface OnLoadingCancelListener {
    fun onCancel()
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
 * @author : ezhuwx
 * Describe : 页面状态全局配置
 * Designed on 2024/5/8
 * E-mail : ezhuwx@163.com
 * Update on 18:23 by ezhuwx
 */
class PageStateOptionManager {
    companion object {
        val instance: PageStateOptionManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            PageStateOptionManager()
        }
    }

    /**
     * 加载文字内容
     */
    var loadingText: String? = null

    /**
     * 加载文字颜色
     */
    @ColorInt
    var loadingTextColor: Int? = null

    /**
     * 加载文字大小sp
     */
    var loadingTextSize: Float? = null

    /**
     * 加载文字Style
     */
    var loadingTextStyle: Typeface? = null

    /**
     * 默认加载loading字体
     */
    var loadingTextFontAsset: String? = null

    /**
     * 空数据提示
     */
    var emptyResourceMsg: String? = null

    /**
     * 未知错误提示
     */
    var unknownResourceMsg: String? = null

    /**
     * 加载页面ID
     * 根布局ID必须为：loading_root
     */
    var loadingLayoutId: Int = R.layout.view_progress

    /**
     * 空数据页面ID
     * 根布局ID必须为：empty_root
     */
    var emptyLayoutId: Int = R.layout.view_empty

    /**
     * 网络错误页面ID
     * 根布局ID必须为：net_error_root
     */
    var netErrorLayoutId: Int = R.layout.view_net_error

    /**
     * 未知错误页面ID
     * 根布局ID必须为：unknown_error_root
     */
    var unknownErrorLayoutId: Int = R.layout.view_unknown_error
}