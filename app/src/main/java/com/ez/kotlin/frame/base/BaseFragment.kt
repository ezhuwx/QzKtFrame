package com.ez.kotlin.frame.base

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ez.kotlin.frame.net.NetDialog
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
        initView(view)
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::viewModel.isInitialized)
            lifecycle.removeObserver(viewModel)
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
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化 View
     */
    abstract fun initView(view: View)

    /**
     *  viewModel实例
     *  */
    abstract fun providerVMClass(): Class<VM>?

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

    open fun stateDialogLoading(isCancel: Boolean, cancelOperation: Boolean) {
        if (!mNetDialog.isShowing) {
            mNetDialog.setCancelable(isCancel)
            mNetDialog.setOnCancelListener { dialog ->
                if (cancelOperation) {

                }
            }
            mNetDialog.show()
        }
    }
}