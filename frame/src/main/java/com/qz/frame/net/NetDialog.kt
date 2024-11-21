package com.qz.frame.net

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import com.qz.frame.R
import me.jessyan.autosize.AutoSizeCompat

/**
 * @author : ezhuwx
 * Describe :DialogLoading
 * Designed on 2021/10/25
 * E-mail : ezhuwx@163.com
 * Update on 14:34 by ezhuwx
 */
class NetDialog(val context: AppCompatActivity) :
    AppCompatDialog(context, R.style.NetDialog) {
    private var loadingContent: String? = null
    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var _lifecycleRegistry: LifecycleRegistry? = null
    private val lifecycleRegistry: LifecycleRegistry
        get() = _lifecycleRegistry ?: LifecycleRegistry(this).also {
            _lifecycleRegistry = it
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //view
        val contentView: View =
            inflater.inflate(R.layout.view_dialog_progress, null)
        //提示文字
        contentView.findViewById<TextView>(R.id.loading_tv)?.run {
            loadingContent?.let {
                visibility = View.VISIBLE
                text = it
            }
        }
        //适配
        AutoSizeCompat.autoConvertDensityOfGlobal(context.resources)
        setContentView(contentView)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    @CallSuper
    override fun onStop() {
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        _lifecycleRegistry = null
        super.onStop()
    }

    init {
        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //可取消
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }

    fun showLoadingText(loadingContent: String?) {
        this.loadingContent = loadingContent
    }

    override val lifecycle: Lifecycle = lifecycleRegistry

}