package com.qz.frame.base

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.qz.frame.interfaces.OnRefreshStateChangeListener
import com.qz.frame.utils.DayNightMode
import com.gyf.immersionbar.ktx.immersionBar
import com.kunminx.architecture.ui.page.DataBindingFragment
import com.qz.frame.R
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseFragment<VM : BaseViewModel> : DataBindingFragment() {
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
     * 状态栏颜色
     */
    open var statusBarColor: Int? = BaseApplication.instance.statusBarColorId

    /**
     * 页面状态管理
     */
    open lateinit var pageStateManager: PageStateManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //binding前处理
        onBindingBefore()
        //重置Resume状态
        isResumed.set(false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusBarColor?.let {
            immersionBar {
                fitsSystemWindows(true)
                statusBarColor(it)
                navigationBarColor(R.color.white)
            }
        }
        //页面状态管理
        pageStateManager = PageStateManager(
            requireActivity(),
            viewLifecycleOwner,
            isObserveViewModelRequest,
            viewModel
        )
        //view绑定方法
        initBindView(view)
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
        owner: ViewModelStoreOwner = this@BaseFragment
    ): T {
        return ViewModelProvider(owner)[clazz]
    }

    open fun <T : BaseViewModel> Class<T>.vm(owner: ViewModelStoreOwner = this@BaseFragment): T {
        return getViewModel(this, owner)
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

    open fun addOnRefreshStateChangeListener(listener: OnRefreshStateChangeListener) {
        pageStateManager.onRefreshStateChangeListener = listener
    }

    /**
     *  viewModel实例
     *  */
    abstract fun getBindingVMClass(): Class<VM>

    /**
     * onCreateView 起始位置，binding之前
     */
    open fun onBindingBefore() {}

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