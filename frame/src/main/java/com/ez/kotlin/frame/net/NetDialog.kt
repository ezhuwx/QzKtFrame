package com.ez.kotlin.frame.net

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.ez.kotlin.frame.R
import me.jessyan.autosize.AutoSizeCompat

/**
 * @author : ezhuwx
 * Describe :DialogLoading
 * Designed on 2021/10/25
 * E-mail : ezhuwx@163.com
 * Update on 14:34 by ezhuwx
 */
class NetDialog constructor(context: AppCompatActivity) : AlertDialog(context, R.style.NetDialog) {
    private var loadingContent: String? = null
    private var inflater: LayoutInflater = LayoutInflater.from(context)

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
    }

    init {
        //全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        //可取消
        setCancelable(true)
        setCanceledOnTouchOutside(false)
    }

    fun showLoadingText(loadingContent: String?) {
        this.loadingContent = loadingContent
    }
}