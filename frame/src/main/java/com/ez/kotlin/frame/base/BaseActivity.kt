package com.ez.kotlin.frame.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ez.kotlin.frame.net.NetDialog
import com.gyf.immersionbar.ImmersionBar
import com.kunminx.architecture.ui.page.DataBindingActivity

abstract class BaseActivity<VM : BaseViewModel> : DataBindingActivity() {
    /**
     * ViewModel 实例
     */
    protected lateinit var viewModel: VM

    /**
     * Loading
     * */
    private val mNetDialog by lazy { NetDialog(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).init()
        BaseApplication.instance.addActivity(this)
        initView()
        initData()
    }

    /**
     * 实例销毁
     */
    override fun onDestroy() {
        super.onDestroy()
        BaseApplication.instance.removeActivity(this)
        stateDialogDismiss()
    }

    /**
     *  初始化ViewModel
     *
     */
    override fun initViewModel() {
        providerVMClass().let {
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
     *
     */
    abstract fun initView()

    /**
     *  viewModel实例
     *
     */
    abstract fun providerVMClass(): Class<VM>

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