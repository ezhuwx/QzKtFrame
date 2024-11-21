package com.qz.frame.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.kunminx.architecture.ui.page.DataBindingActivity

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelStoreOwner
import com.qz.frame.interfaces.OnRefreshStateChangeListener
import com.qz.frame.utils.DayNightMode
import com.gyf.immersionbar.ktx.immersionBar
import com.qz.frame.R
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Activity 基类
 */

abstract class BaseActivity<VM : BaseViewModel> : DataBindingActivity() {
    /**
     * 是否已经初始化
     */
    private val isResumed = AtomicBoolean(false)

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

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        //binding前处理
        onBindingBefore()
        //重置Resume状态
        isResumed.set(false)
        super.onCreate(savedInstanceState)
        //锁定竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        //状态栏适配
        statusBarColor?.let {
            immersionBar {
                fitsSystemWindows(true)
                statusBarColor(it)
                navigationBarColor(R.color.white)
            }
        }
        //页面状态管理
        pageStateManager = PageStateManager(
            this,
            this,
            isObserveViewModelRequest,
            viewModel
        )
        //activity管理
        BaseApplication.instance.addActivity(this)
        //view绑定方法
        initBindView()
    }

    override fun onResume() {
        super.onResume()
        if (isResumed.compareAndSet(false, true)) {
            //数据初始化方法
            initData()
        }
    }


    /**
     *  初始化ViewModel
     *
     */
    override fun initViewModel() {
        viewModel = getBindingVMClass().vm()
    }

    /**
     *  添加ViewModel
     *
     */
    open fun <T : BaseViewModel> getViewModel(
        clazz: Class<T>,
        owner: ViewModelStoreOwner = this@BaseActivity
    ): T {
        return ViewModelProvider(owner)[clazz]
    }

    open fun <T : BaseViewModel> Class<T>.vm(owner: ViewModelStoreOwner = this@BaseActivity): T {
        return getViewModel(this, owner)
    }

    /**
     * 实例销毁
     */
    override fun onDestroy() {
        super.onDestroy()
        pageStateManager.onClearNetObservers()
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
     * onCreateView 起始位置，binding之前
     */
    open fun onBindingBefore() {}

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