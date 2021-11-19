package com.ez.kotlin.frame.base

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.net.NetDialog
import com.gyf.immersionbar.ImmersionBar
import com.kunminx.architecture.ui.page.DataBindingFragment

abstract class BaseFragment<VM : BaseViewModel> : DataBindingFragment() {
    /**
     * ViewModel 实例
     */
    protected lateinit var viewModel: VM

    /**
     * Loading
     * */
    private val mNetDialog by lazy { NetDialog(activity as AppCompatActivity) }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ImmersionBar.with(this)
            .fitsSystemWindows(true)
            .statusBarColor(BaseApplication.instance.statusBarColorId)
            .init()
        initView(view)
        initData()
    }

    /**
     *  初始化ViewModel
     *
     */
    override fun initViewModel() {
        providerVMClass()?.let {
            viewModel = ViewModelProvider(this).get(it)
            lifecycle.addObserver(viewModel)
        }
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

    override fun onDestroy() {
        super.onDestroy()
        if (this::viewModel.isInitialized)
            lifecycle.removeObserver(viewModel)
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
            loading?.let {
                mNetDialog.findViewById<TextView>(R.id.loading_tv)?.run {
                    visibility = View.VISIBLE
                    text = it
                }
            }
            mNetDialog.show()
        }
    }

    /**
     *  viewModel实例
     *  */
    abstract fun providerVMClass(): Class<VM>?

    /**
     * 初始化 View
     */
    abstract fun initView(view: View)

    /**
     * 初始化数据
     */
    abstract fun initData()
}