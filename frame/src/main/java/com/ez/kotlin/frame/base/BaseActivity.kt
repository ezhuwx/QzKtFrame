package com.ez.kotlin.frame.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.ez.kotlin.frame.net.NetDialog
import com.kunminx.architecture.ui.page.DataBindingActivity

import androidx.appcompat.app.AppCompatDelegate
import com.ez.kotlin.frame.interfaces.OnRefreshStateChangeListener
import com.ez.kotlin.frame.utils.DayNightMode
import com.gyf.immersionbar.ktx.immersionBar

/**
 * Activity 基类
 */

abstract class BaseActivity<VM : BaseViewModel> : DataBindingActivity() {
    /**
     * ViewModel 实例
     */
    protected lateinit var viewModel: VM

    /**
     * 跳过网络监听
     */
    open var isObserveViewModelRequest = true

    /**
     * 页面状态管理
     */
    open lateinit var pageStateManager: PageStateManager

    /**
     * 状态栏颜色
     */
    open var statusBarColor: Int? = BaseApplication.instance.statusBarColorId

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
        if (statusBarColor != null) immersionBar {
            fitsSystemWindows(true)
            transparentNavigationBar()
            statusBarColor(statusBarColor!!)
        }
        //页面状态管理
        pageStateManager = PageStateManager(
            this,
            this,
            viewModel,
            isObserveViewModelRequest
        )
        //activity管理
        BaseApplication.instance.addActivity(this)
        //view绑定方法
        initBindView()
        //数据初始化方法
        initData()
    }

    /**
     *  添加ViewModel
     *
     */
    open fun <T : BaseViewModel> getViewModel(clazz: Class<T>): T {
        return ViewModelProvider(this)[clazz]
    }

    /**
     *  初始化ViewModel
     *
     */
    override fun initViewModel() {
        getBindingVMClass().let {
            viewModel = ViewModelProvider(this)[it]
        }
    }

    /**
     * 实例销毁
     */
    override fun onDestroy() {
        super.onDestroy()
        BaseApplication.instance.removeActivity(this)
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

    open fun addStateChangeListener(listener: OnRefreshStateChangeListener) {
        pageStateManager.onRefreshStateChangeListener = listener
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
    open fun onClick(v: View) {

    }

}