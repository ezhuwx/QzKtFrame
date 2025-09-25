package com.qz.frame.base

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.kunminx.architecture.ui.page.DataBindingActivity

import androidx.lifecycle.ViewModelStoreOwner
import com.qz.frame.interfaces.OnRefreshStateChangeListener
import com.gyf.immersionbar.ktx.immersionBar
import com.qz.frame.R
import com.qz.frame.utils.attachBaseContextExcludeFontScale
import com.qz.frame.utils.getResourcesExcludeFontScale
import com.qz.frame.utils.post
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
    open var statusBarColor: Int? = BaseApplication.instance.config.statusBarColorId

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(
            if (BaseApplication.instance.config.isExcludeFontScale) {
                //禁用字体缩放
                newBase?.attachBaseContextExcludeFontScale()
            } else newBase
        )
    }

    override fun getResources(): Resources {
        return if (BaseApplication.instance.config.isExcludeFontScale) {
            //禁用字体缩放
            getResourcesExcludeFontScale(super.getResources())
        } else super.getResources()
    }

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
        super.onConfigurationChanged(newConfig.apply {
            if (BaseApplication.instance.config.isExcludeFontScale) fontScale = 1.0f
        })
        SysConfigChangeEvent(newConfig).post()
    }

    open fun addOnRefreshStateChangeListener(listener: OnRefreshStateChangeListener) {
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