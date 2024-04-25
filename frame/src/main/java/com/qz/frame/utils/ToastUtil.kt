package com.qz.frame.utils

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import com.qz.frame.base.BaseApplication
import com.qz.kotlin.frame.R

/**
 * @author : ezhuwx
 * Describe : 提示信息工具类
 * Designed on 2021/10/25
 * E-mail : ezhuwx@163.com
 * Update on 11:27 by ezhuwx
 */
open class ToastUtil {
    private var toast: Toast? = null
    private var toastCv: CardView? = null
    private var toastTv: TextView? = null

    init {
        toast = Toast(BaseApplication.mContext)
        val view: View = LayoutInflater.from(BaseApplication.mContext).inflate(R.layout.view_content_toast, null)
        toast!!.view = view
        toastCv = view.findViewById(R.id.toast_cv)
        toastTv = view.findViewById(R.id.toast_tv)
        toast!!.setGravity(Gravity.CENTER, 0, 0)
    }

    /**
     * 短时间显示Toast（资源id)
     */
    fun shortShow(@StringRes strResId: Int) {
        toastTv!!.setText(strResId)
        toast!!.duration = Toast.LENGTH_SHORT
        toast!!.show()
    }

    /**
     * 长时间显示Toast（资源id)
     */
    fun longShow(@StringRes strResId: Int) {
        toastTv!!.setText(strResId)
        toast!!.duration = Toast.LENGTH_SHORT
        toast!!.show()
    }

    fun shortShow(message: String?) {
        toastTv!!.text = message
        toast!!.duration = Toast.LENGTH_SHORT
        toast!!.show()
    }

    fun longShow(message: String?) {
        toastTv!!.text = message
        toast!!.duration = Toast.LENGTH_LONG
        toast!!.show()
    }

    /**
     * 短时间显示Toast（资源id)
     */
    fun shortShow(@StringRes strResId: Int, gravity: Int) {
        toastTv!!.setText(strResId)
        toast!!.setGravity(gravity, 0, 0)
        toast!!.duration = Toast.LENGTH_SHORT
        toast!!.show()
    }

    /**
     * 长时间显示Toast（资源id)
     */
    fun longShow(@StringRes strResId: Int, gravity: Int) {
        toastTv!!.setText(strResId)
        toast!!.setGravity(gravity, 0, 0)
        toast!!.duration = Toast.LENGTH_SHORT
        toast!!.show()
    }

    fun shortShow(message: String?, gravity: Int) {
        toastTv!!.text = message
        toast!!.setGravity(gravity, 0, 0)
        toast!!.duration = Toast.LENGTH_SHORT
        toast!!.show()
    }

    fun longShow(message: String?, gravity: Int) {
        toastTv!!.text = message
        toast!!.setGravity(gravity, 0, 0)
        toast!!.duration = Toast.LENGTH_LONG
        toast!!.show()
    }

    /**
     * 短时间显示Toast（资源id)
     */
    fun shortShow(
        @StringRes strResId: Int,
        gravity: Int,
        @ColorInt textColor: Int,
        @ColorInt backgroundColor: Int
    ) {
        toastTv!!.setText(strResId)
        toast!!.setGravity(gravity, 0, 0)
        toastTv!!.setTextColor(textColor)
        toastCv!!.setCardBackgroundColor(backgroundColor)
        toast!!.duration = Toast.LENGTH_SHORT
        toast!!.show()
    }

    /**
     * 长时间显示Toast（资源id)
     */
    fun longShow(
        @StringRes strResId: Int,
        gravity: Int,
        @ColorInt textColor: Int,
        @ColorInt backgroundColor: Int
    ) {
        toastTv!!.setText(strResId)
        toast!!.setGravity(gravity, 0, 0)
        toastTv!!.setTextColor(textColor)
        toastCv!!.setCardBackgroundColor(backgroundColor)
        toast!!.duration = Toast.LENGTH_SHORT
        toast!!.show()
    }

    fun shortShow(
        message: String?,
        gravity: Int,
        @ColorInt textColor: Int,
        @ColorInt backgroundColor: Int
    ) {
        toastTv!!.text = message
        toast!!.setGravity(gravity, 0, 0)
        toastTv!!.setTextColor(textColor)
        toastCv!!.setCardBackgroundColor(backgroundColor)
        toast!!.duration = Toast.LENGTH_SHORT
        toast!!.show()
    }

    fun longShow(
        message: String?,
        gravity: Int,
        @ColorInt textColor: Int,
        @ColorInt backgroundColor: Int
    ) {
        toastTv!!.text = message
        toast!!.setGravity(gravity, 0, 0)
        toastTv!!.setTextColor(textColor)
        toastCv!!.setCardBackgroundColor(backgroundColor)
        toast!!.duration = Toast.LENGTH_LONG
        toast!!.show()
    }
}