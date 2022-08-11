package com.ez.kotlin.frame.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.net.ApiException
import com.ez.kotlin.frame.net.NetDialog
import com.ez.kotlin.frame.net.ResponseException
import com.ez.kotlin.frame.utils.NetWorkUtil
import com.ez.kotlin.frame.utils.ToastUtil
import com.gyf.immersionbar.ImmersionBar
import com.kunminx.architecture.ui.page.DataBindingActivity

import androidx.appcompat.app.AppCompatDelegate
import com.ez.kotlin.frame.utils.DayNightMode


abstract class BaseActivity<VM : BaseViewModel> : DataBindingActivity() {
    /**
     * ViewModel 实例
     */
    protected lateinit var viewModel: VM

    /**
     * Loading
     * */
    private val mNetDialog by lazy { NetDialog(this) }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //锁定竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //状态栏适配
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .transparentNavigationBar()
            .statusBarColor(BaseApplication.instance.statusBarColorId)
            .init()
        //activity管理
        BaseApplication.instance.addActivity(this)
        //请求状态监听
        startObserve()
        //view绑定方法
        initBindView()
        //数据初始化方法
        initData()
    }

    /**
     *  添加ViewModel
     *
     */
    fun <T : BaseViewModel> getViewModel(clazz: Class<T>): T {
        val vm = ViewModelProvider(this).get(clazz)
        lifecycle.addObserver(vm)
        return vm
    }

    /**
     *  初始化ViewModel
     *
     */
    override fun initViewModel() {
        getBindingVMClass().let {
            viewModel = ViewModelProvider(this).get(it)
            lifecycle.addObserver(viewModel)
        }
    }

    /**
     * 实例销毁
     */
    override fun onDestroy() {
        super.onDestroy()
        BaseApplication.instance.removeActivity(this)
        stateDialogDismiss()
        // 必须调用该方法，防止内存泄漏
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (BaseApplication.instance.dayNightMode == DayNightMode.SYSTEM) {
            when (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    //关闭夜间模式
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    //打开夜间模式
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                else -> {}
            }
        }
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
     *  viewModel实例
     *
     */
    abstract fun getBindingVMClass(): Class<VM>

    /**
     *  绑定视图
     *
     */
    abstract fun initBindView()

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 点击事件
     */
    abstract fun onClick(v: View)

    /**
     * TODO 请求监听
     *
     */
    private fun startObserve() {
        viewModel.run {
            start().observe(this@BaseActivity) {
                //开始
                onRequestStart(it)
            }
            success().observe(this@BaseActivity) {
                //成功
                onRequestSuccess(it)
            }
            error().observe(this@BaseActivity) {
                //报错
                onRequestError(it)
            }
            finally().observe(this@BaseActivity) {
                //结束
                onRequestFinally(it)
            }
        }
    }


    /**
     *  接口请求开始，子类可以重写此方法做一些操作
     *  */
    open fun onRequestStart(it: Boolean) {

    }

    /**
     *  接口请求成功，子类可以重写此方法做一些操作
     *  */
    open fun onRequestSuccess(it: Boolean) {

    }

    /**
     *  接口请求完毕，子类可以重写此方法做一些操作
     *  */
    open fun onRequestFinally(it: Int?) {
    }

    /**
     *  接口请求出错，子类可以重写此方法做一些操作
     *  */
    open fun onRequestError(it: Exception?) {
        //处理一些已知异常
        showErrorTip(it)
    }

    /**
     * 处理一些已知异常
     */
    open fun showErrorTip(it: Exception?) {
        //处理一些已知异常
        it?.run {
            if (NetWorkUtil.isNoProxyConnected(this@BaseActivity)) {
                when (it) {
                    //服务器特殊错误处理
                    is ApiException -> {
                        onServiceError(it.code, it.message)
                    }
                    //正常错误显示
                    is ResponseException -> {
                        ToastUtil().longShow("(${it.code})${it.message}")
                    }
                    //无提示信息错误显示
                    else -> {
                        ToastUtil().longShow(R.string.unknown_error)
                    }
                }
            } else {
                //无网络提示
                ToastUtil().longShow(R.string.network_error_content)
            }
        }
    }

    /**
     * 服务器特殊错误处理
     * ‘登录超时’等
     * */
    open fun onServiceError(code: Int, message: String?) {

    }
}