package com.qz.frame.base

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.qz.frame.interfaces.OnRefreshStateChangeListener
import com.qz.frame.utils.DayNightMode
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingFragment

abstract class BaseFragment<VM : BaseViewModel> : DataBindingFragment() {
    /**
     * ViewModel 实例
     */
    protected lateinit var viewModel: VM

    /**
     * 跳过网络监听
     */
    open var isObserveViewModelRequest = true

    /**
     * 状态栏颜色
     */
    open var statusBarColor: Int? = BaseApplication.instance.statusBarColorId

    /**
     * 页面状态管理
     */
    open lateinit var pageStateManager: PageStateManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (statusBarColor != null) immersionBar {
            fitsSystemWindows(true)
            transparentNavigationBar()
            statusBarColor(statusBarColor!!)
        }
        //页面状态管理
        pageStateManager = PageStateManager(
            requireActivity(),
            viewLifecycleOwner,
            viewModel,
            isObserveViewModelRequest
        )
        //view绑定方法
        initBindView(view)
        //数据初始化方法
        initData()
    }

    /**
     *  初始化ViewModel
     *
     */
    override fun initViewModel() {
        getBindingVMClass()?.let {
            viewModel = ViewModelProvider(this)[it]
        }
    }

    /**
     *  添加ViewModel
     *
     */
    open fun <T : BaseViewModel> getViewModel(clazz: Class<T>): T {
        return ViewModelProvider(this)[clazz]
    }

    override fun onDestroyView() {
        pageStateManager.onClearNetObservers()
        super.onDestroyView()
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
     *  */
    abstract fun getBindingVMClass(): Class<VM>?

    /**
     * 初始化 View
     */
    abstract fun initBindView(view: View)

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 点击事件
     */
    open fun onClick(v: View) {}

}