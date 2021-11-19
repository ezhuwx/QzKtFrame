package com.ez.kotlin.frame.base

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.net.ApiException
import com.ez.kotlin.frame.net.NetDialog
import com.ez.kotlin.frame.net.ResponseException
import com.ez.kotlin.frame.utils.NetWorkUtil
import com.ez.kotlin.frame.utils.ToastUtil
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
        startObserve()
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

    /**
     * TODO 请求监听
     *
     */
    private fun startObserve() {
        viewModel.run {
            start().observe(requireActivity(), {
                //开始
                requestStart(it)
            })
            success().observe(requireActivity(), {
                //成功
                requestSuccess(it)
            })
            error().observe(requireActivity(), {
                //报错
                requestError(it)
            })
            finally().observe(requireActivity(), {
                //结束
                requestFinally(it)
            })
        }
    }

    /**
     *  接口请求开始，子类可以重写此方法做一些操作
     *  */
    open fun requestStart(it: Boolean) {
    }

    /**
     *  接口请求成功，子类可以重写此方法做一些操作
     *  */
    open fun requestSuccess(it: Boolean) {
    }

    /**
     * 接口请求完毕，子类可以重写此方法做一些操作
     * */
    open fun requestFinally(it: Int?) {
    }

    /**
     *  接口请求出错，子类可以重写此方法做一些操作
     *  */
    open fun requestError(it: Exception?) {
        //处理一些已知异常
        it?.run {
            if (NetWorkUtil.isNetworkConnected(requireContext())) {
                when (it) {
                    //服务器特殊错误处理
                    is ApiException -> {
                        onServiceError(it.code, it.message)
                    }
                    //正常错误显示
                    is ResponseException -> {
                        ToastUtil().longShow("${it.message}(${it.code})")
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