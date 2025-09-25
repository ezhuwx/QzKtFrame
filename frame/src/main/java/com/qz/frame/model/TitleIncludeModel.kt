package com.qz.frame.model

import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.qz.frame.R
import com.qz.frame.base.BaseApplication
import com.qz.frame.base.BaseViewModel
import com.qz.frame.interfaces.OnEditorActionListener
import com.qz.frame.interfaces.OnEditorSearchActionListener
import com.qz.frame.utils.SingleLiveEvent
import com.qz.frame.utils.resColor
import kotlin.apply

/**
 * @author : ezhuwx
 * Describe :标题Include Model
 * Designed on 2021/11/2
 * E-mail : ezhuwx@163.com
 * Update on 12:09 by ezhuwx
 */
class TitleIncludeModel : BaseViewModel() {
    companion object {
        /**
         * 构建TitleIncludeModel
         * 默认实现返回事件
         */
        fun AppCompatActivity.createTitleVm(onBack: (() -> Boolean)? = null): TitleIncludeModel {
            return ViewModelProvider(this)[TitleIncludeModel::class.java].apply {
                onBack(this@createTitleVm, onBack)
            }
        }

        /**
         * 输入框输入，及搜索按钮点击监听
         */
        fun TitleIncludeModel.inInputWatcher(
            owner: LifecycleOwner,
            textWatcher: ((text: String) -> Unit)? = null,
            onKeyAndRightClick: ((text: String?) -> Unit)? = null
        ) {
            //搜索监听
            inputStr.observe(owner) { textWatcher?.invoke(it) }
            //查询点击
            rightClickListener.value = OnRightClick { onKeyAndRightClick?.invoke(inputStr.value) }
            //键盘搜索
            inputSearchActionListener.value = OnEditorSearchActionListener {
                onKeyAndRightClick?.invoke(inputStr.value)
                true
            }
        }
    }

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
     * 返回点击事件代理
     */
    val backClickListener = SingleLiveEvent<OnBackClick>()
    fun onBack(activity: AppCompatActivity, onBack: (() -> Boolean)? = null) {
        backClickListener.value = object : OnBackClick {
            override fun onClick() {
                if (onBack?.invoke() != true) activity.onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    /**
     * 标题点击事件代理
     */
    val titleClickListener = SingleLiveEvent<OnTitleClick>()

    /**
     * 右侧按钮点击事件代理
     */
    val rightClickListener = SingleLiveEvent<OnRightClick>()

    /**
     * 背景颜色
     */
    val backgroundColor = SingleLiveEvent<Int>().apply {
        value = BaseApplication.instance.config.statusBarColorId.resColor()
    }

    /**
     *  标题点击代理
     *
     */
    fun interface OnTitleClick {
        fun onClick()
    }

    /**
     *  返回点击代理
     *
     */
    fun interface OnBackClick {
        fun onClick()
    }

    /**
     *  右键点击代理
     *
     */
    fun interface OnRightClick {
        fun onClick()
    }


}