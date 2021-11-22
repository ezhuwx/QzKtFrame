package com.ez.kotlin.frame.model

import android.view.View
import androidx.annotation.IntDef
import com.ez.kotlin.frame.R
import com.ez.kotlin.frame.base.BaseApplication
import com.ez.kotlin.frame.base.BaseViewModel
import com.ez.kotlin.frame.binder.CommonBinder
import com.ez.kotlin.frame.utils.SingleLiveEvent

/**
 * @author : ezhuwx
 * Describe :标题Include Model
 * Designed on 2021/11/2
 * E-mail : ezhuwx@163.com
 * Update on 12:09 by ezhuwx
 */
class TitleIncludeModel : BaseViewModel() {
    /**
     * 标题
     */
    val title = SingleLiveEvent<String>()

    /**
     * 输入
     */
    val inputStr = SingleLiveEvent<String>()

    /**
     * 输入提示
     */
    val inputHint = SingleLiveEvent<String>().apply {
        value = BaseApplication.mContext.getString(R.string.default_hint)
    }

    /**
     * 返回按钮显示
     */
    val backVisible = SingleLiveEvent<Int>().apply { value = View.VISIBLE }

    /**
     * 返回按钮显示颜色
     */
    val backTintColor = SingleLiveEvent<Int>()

    /**
     * 右侧按钮
     */
    val right = SingleLiveEvent<String>().apply {
        value = BaseApplication.mContext.getString(R.string.confirm)
    }

    /**
     * 右侧按钮颜色
     */
    val rightColor = SingleLiveEvent<Int>().apply {
        value = BaseApplication.mContext.getColor(R.color.white)
    }

    /**
     * 右侧按钮显示
     */

    val rightVisible = SingleLiveEvent<Int>().apply { value = View.GONE }

    /**
     * 点击事件代理
     */
    val backClickListener = SingleLiveEvent<OnBackClick>()

    /**
     * 点击事件代理
     */
    val rightClickListener = SingleLiveEvent<OnRightClick>()

    /**
     * 背景颜色
     */
    val backgroundColor = SingleLiveEvent<Int>().apply {
        value = BaseApplication.mContext.getColor(R.color.colorPrimary)
    }

    /**
     * TODO 返回点击代理
     *
     */
    interface OnBackClick {
        fun onClick()
    }

    /**
     * TODO 右键点击代理
     *
     */
    interface OnRightClick {
        fun onClick()
    }
}