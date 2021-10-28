package com.ez.kotlin.frame.net

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.Window
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
    override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        val contentView: View = layoutInflater.inflate(R.layout.view_progress, null)
        AutoSizeCompat.autoConvertDensityOfGlobal(context.resources)
        setContentView(contentView)
    }

    init {
        var layoutInflater = LayoutInflater.from(this.context)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(true)
        setCanceledOnTouchOutside(false)
    }
}