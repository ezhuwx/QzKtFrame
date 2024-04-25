package com.qz.frame.model

import android.view.View
import androidx.core.content.ContextCompat
import com.qz.frame.base.BaseApplication
import com.qz.frame.base.BaseViewModel
import com.qz.frame.interfaces.OnEditorActionListener
import com.qz.frame.interfaces.OnEditorSearchActionListener
import com.qz.frame.utils.SingleLiveEvent
import com.qz.kotlin.frame.R

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
     * 标题颜色
     */
    val titleColor = SingleLiveEvent<Int>().apply {
        value = ContextCompat.getColor(BaseApplication.mContext, R.color.white_in_deep_bg)
    }

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
     * 键盘事件
     */
    val inputActionListener = SingleLiveEvent<OnEditorActionListener>()
    /**
     * 键盘搜索事件
     */
    val inputSearchActionListener = SingleLiveEvent<OnEditorSearchActionListener>()
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
        value = ContextCompat.getColor(BaseApplication.mContext, R.color.white_in_deep_bg)
    }

    /**
     * 右侧按钮显示
     */

    val rightVisible = SingleLiveEvent<Int>().apply { value = View.GONE }

    /**
     * 点击事件代理
     */
    val titleClickListener = SingleLiveEvent<OnTitleClick>()

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
        value = ContextCompat.getColor(BaseApplication.mContext, R.color.colorPrimary)
    }

    /**
     * TODO 标题点击代理
     *
     */
    fun interface OnTitleClick {
        fun onClick()
    }

    /**
     * TODO 返回点击代理
     *
     */
    fun interface OnBackClick {
        fun onClick()
    }

    /**
     * TODO 右键点击代理
     *
     */
    fun interface OnRightClick {
        fun onClick()
    }


}