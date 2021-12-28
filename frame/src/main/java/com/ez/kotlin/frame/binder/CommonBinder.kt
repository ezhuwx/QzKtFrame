package com.ez.kotlin.frame.binder

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.*
import com.ez.kotlin.frame.utils.glideWith
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import android.graphics.Bitmap
import android.view.animation.Animation
import android.widget.TextView
import androidx.annotation.IntDef
import androidx.constraintlayout.motion.widget.MotionLayout

import androidx.databinding.InverseBindingListener

import androidx.databinding.BindingAdapter
import com.ez.kotlin.frame.interfaces.MotionAnimListener
import com.ez.kotlin.frame.utils.addRedStar
import com.google.android.material.textfield.TextInputLayout


/**
 * @author : ezhuwx
 * Describe :自定义Binding
 * Designed on 2021/11/8
 * E-mail : ezhuwx@163.com
 * Update on 15:36 by ezhuwx
 */

object CommonBinder {
    /**
     * TODO Glide 方法适配
     *
     * @param view
     * @param url
     * @param placeHolder
     */
    @BindingAdapter(value = ["imageUrl", "placeHolder"], requireAll = false)
    @JvmStatic
    fun imageUrl(view: ImageView, url: String?, placeHolder: Drawable?) {
        glideWith(view.context).load(url).placeholder(placeHolder).into(view)
    }

    /**
     * TODO ImageSrc 方法适配
     *
     * @param view
     * @param src
     */
    @BindingAdapter(value = ["imageSrc"], requireAll = false)
    @JvmStatic
    fun imageSrc(view: ImageView, src: Int) {
        view.setImageResource(src)
    }

    /**
     * TODO ImageSrc 方法适配
     *
     * @param view
     * @param bitmap
     */
    @BindingAdapter(value = ["imageBitmap"], requireAll = false)
    @JvmStatic
    fun imageBitmap(view: ImageView, bitmap: Bitmap) {
        view.setImageBitmap(bitmap)
    }

    /**
     * TODO backgroundColor 方法适配
     *
     * @param view
     * @param backgroundColor
     */
    @BindingAdapter(value = ["backgroundColor"], requireAll = false)
    @JvmStatic
    fun backgroundColor(view: View, backgroundColor: Int) {
        view.setBackgroundColor(backgroundColor)
    }

    /**
     * TODO backgroundColor 方法适配
     *
     * @param view
     * @param backgroundRes
     */
    @BindingAdapter(value = ["backgroundRes"], requireAll = false)
    @JvmStatic
    fun backgroundRes(view: View, backgroundRes: Int) {
        view.setBackgroundResource(backgroundRes)
    }

    /**
     * TODO OnFocusChangeListener 方法适配
     *
     */
    @InverseBindingAdapter(attribute = "hasFocus", event = "onFocusChanged")
    @JvmStatic
    fun hasFocus(view: EditText): Boolean {
        return view.hasFocus()
    }

    @BindingAdapter(value = ["onFocusChanged"], requireAll = false)
    @JvmStatic
    fun setOnFocusChangeListener(view: EditText, onFocusChanged: InverseBindingListener?) {
        view.onFocusChangeListener = null
        view.onFocusChangeListener =
            View.OnFocusChangeListener { _, _ -> onFocusChanged?.onChange() }
    }

    @BindingAdapter(value = ["hasFocus"], requireAll = false)
    @JvmStatic
    fun setOnHasFocus(view: EditText, hasFocus: Boolean) {

    }

    /**
     * TODO animation 方法适配
     *
     * @param view
     */
    @BindingAdapter(value = ["animation"], requireAll = false)
    @JvmStatic
    fun animation(view: View, animation: Animation?) {
        animation?.let { view.startAnimation(it) }
    }

    /**
     * TODO viewTint 方法适配
     *
     * @param view
     */
    @BindingAdapter(value = ["viewTint"], requireAll = false)
    @JvmStatic
    fun viewTint(view: ImageView, color: Int?) {
        color?.let { view.imageTintList = ColorStateList.valueOf(color) }
    }

    /**
     * TODO 下拉刷新SmartRefreshLayout 方法适配
     * @param dataSize 数据大小
     * @param pageSize 每页大小
     * @param listener  监听
     */
    @BindingAdapter(
        value = ["pageSize", "dataSize", "isAutoRefresh", "listener"],
        requireAll = false
    )
    @JvmStatic
    fun refreshHandler(
        refreshLayout: SmartRefreshLayout,
        pageSize: Int,
        dataSize: Int,
        isAutoRefresh: Boolean,
        listener: OnRefreshLoadMoreListener,
    ) {
        if (isAutoRefresh) {
            refreshLayout.autoRefresh()
        }
        //结束刷新
        if (refreshLayout.isRefreshing) {
            if (dataSize < pageSize) refreshLayout.finishRefreshWithNoMoreData()
            else {
                refreshLayout.finishRefresh()
                refreshLayout.resetNoMoreData()
            }
        }
        //结束加载
        if (refreshLayout.isLoading) {
            //无更多数据
            if (dataSize < pageSize) refreshLayout.finishLoadMoreWithNoMoreData()
            //正常结束
            else refreshLayout.finishLoadMore()
        }
        refreshLayout.setOnRefreshLoadMoreListener(listener)
    }

    /**
     * TODO 下拉刷新SmartRefreshLayout 方法适配
     * @param isNoMoreData  是否无更多数据
     */
    @BindingAdapter(
        value = ["isNoMoreData", "canLoadMore", "canRefresh"],
        requireAll = false
    )
    @JvmStatic
    fun refreshNoMore(
        refreshLayout: SmartRefreshLayout,
        isNoMoreData: Boolean?,
        canLoadMore: Boolean?,
        canRefresh: Boolean?,
    ) {
        //有无更多数据
        isNoMoreData?.let {
            refreshLayout.setNoMoreData(isNoMoreData)
        }
        //是否可以加载更多
        canLoadMore?.let {
            refreshLayout.setEnableLoadMore(it)
        }
        //是否可以下拉刷新
        canRefresh?.let {
            refreshLayout.setEnableRefresh(it)
        }
    }

    /**
     * TODO 添加红星
     *
     * @param view
     */
    @BindingAdapter(
        value = ["addRedStar"],
        requireAll = false
    )
    @JvmStatic
    fun addRedStarAdapter(view: TextView, text: String) {
        view.text = text
        addRedStar(view)
    }


    /**
     * TODO TextInputLayout
     *
     * @param view
     */
    @BindingAdapter(
        value = ["inputError", "backgroundEnable", "endIconClick"],
        requireAll = false
    )
    @JvmStatic
    fun textInputLayout(
        view: TextInputLayout,
        inputError: String?,
        enable: Boolean?,
        endIconClick: View.OnClickListener?
    ) {
        inputError?.let {
            view.error = inputError
        }
        enable?.let {
            view.boxBackgroundMode =
                if (enable) TextInputLayout.BOX_BACKGROUND_FILLED else TextInputLayout.BOX_BACKGROUND_NONE
        }
        endIconClick?.let {
            view.setEndIconOnClickListener(endIconClick)
        }
    }

    /**
     * TODO completeListener
     *
     * @param view
     */
    @BindingAdapter(
        value = ["motionAnimListener"],
        requireAll = false
    )
    @JvmStatic
    fun completeListener(view: MotionLayout, listener: MotionAnimListener) {
        view.addTransitionListener(listener)
    }


    @BindingConversion
    @JvmStatic
    fun visibleConversion(visible: Boolean): Int {
        return if (visible) View.VISIBLE else View.GONE
    }
}