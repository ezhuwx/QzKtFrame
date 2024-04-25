package com.qz.frame.binder

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.databinding.*
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.qz.frame.interfaces.MotionAnimListener
import com.qz.frame.interfaces.OnEditorActionListener
import com.qz.frame.interfaces.OnEditorSearchActionListener
import com.qz.frame.utils.INTERNAL_TIME
import com.qz.frame.utils.addRedStar
import com.qz.frame.utils.glideLoad
import com.qz.frame.utils.glideWith
import com.qz.frame.utils.isInvalidClick
import com.qz.frame.utils.setupWithViewPager2
import com.qz.frame.widget.FragmentPager2Adapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputLayout
import com.qz.frame.R
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener


/**
 * @author : ezhuwx
 * Describe :自定义Binding
 * Designed on 2021/11/8
 * E-mail : ezhuwx@163.com
 * Update on 15:36 by ezhuwx
 */

object CommonBinder {
    /**
     *  Glide 方法适配
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
     *  ImageSrc 方法适配
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
     *  ImageSrc 方法适配
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
     *  backgroundColor 方法适配
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
     *  backgroundColor 方法适配
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
     *  OnFocusChangeListener 方法适配
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
     *  animation 方法适配
     *
     * @param view
     */
    @BindingAdapter(value = ["animation"], requireAll = false)
    @JvmStatic
    fun animation(view: View, animation: Animation?) {
        animation?.let { view.startAnimation(it) }
    }

    /**
     *  viewTint 方法适配
     *
     * @param view
     */
    @BindingAdapter(value = ["viewTint"], requireAll = false)
    @JvmStatic
    fun viewTint(view: ImageView, color: Int?) {
        color?.let { view.imageTintList = ColorStateList.valueOf(color) }
    }

    /**
     *  下拉刷新SmartRefreshLayout 方法适配
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
            if (dataSize in 0 until pageSize) refreshLayout.finishRefreshWithNoMoreData()
            else {
                refreshLayout.finishRefresh()
                refreshLayout.resetNoMoreData()
            }
        }
        //结束加载
        if (refreshLayout.isLoading) {
            //无更多数据
            if (dataSize in 0 until pageSize) refreshLayout.finishLoadMoreWithNoMoreData()
            //正常结束
            else refreshLayout.finishLoadMore()
        }
        refreshLayout.setOnRefreshLoadMoreListener(listener)
    }

    /**
     *  下拉刷新SmartRefreshLayout 方法适配
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
     *  添加红星
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
     *  TextInputLayout
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
     *  completeListener
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

    /**
     *  completeListener
     *
     * @param view
     */
    @BindingAdapter(
        value = ["onGlobalLayout"],
        requireAll = false
    )
    @JvmStatic
    fun globalLayoutListener(view: View, onGlobalLayout: (v: View) -> Unit) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            onGlobalLayout.invoke(view)
            //view.viewTreeObserver.removeOnGlobalLayoutListener(this)
        }

    }

    /**
     * @param view
     */
    @BindingAdapter(
        value = ["isSelected"],
        requireAll = false
    )
    @JvmStatic
    fun setSelected(view: View, isSelected: Boolean) {
        view.isSelected = isSelected
    }

    /**
     * @param view
     */
    @BindingAdapter(
        value = ["marginTop", "marginBottom", "marginStart", "marginEnd"],
        requireAll = false
    )
    @JvmStatic
    fun setMarginTop(
        view: View,
        marginTop: Int?,
        marginBottom: Int?,
        marginStart: Int?,
        marginEnd: Int?
    ) {
        view.layoutParams.let {
            if (it is ViewGroup.MarginLayoutParams) {
                marginTop?.run { it.topMargin = this }
                marginBottom?.run { it.bottomMargin = this }
                marginStart?.run { it.marginStart = this }
                marginEnd?.run { it.marginEnd = this }
            }
        }
    }


    /**
     *  绑定ViewPager
     *
     * @param view
     */
    @BindingAdapter(
        value = ["viewPagerId"],
        requireAll = false
    )
    @JvmStatic
    fun bindViewPager(
        view: TabLayout,
        viewPagerId: String,
    ) {
        //id获取
        val id: Int = view.resources.getIdentifier(viewPagerId, "id", view.context.packageName)
        //viewPager
        val viewPager = (view.rootView as View).findViewById<ViewPager>(id)
        view.setupWithViewPager(viewPager)
    }

    /**
     *  绑定ViewPager
     *
     * @param view
     */
    @BindingAdapter(
        value = ["tabLayoutId", "fragmentAdapter"],
        requireAll = true
    )
    @JvmStatic
    fun bindViewPager2(
        view: ViewPager2,
        tabLayoutId: String,
        fragmentAdapter: FragmentPager2Adapter
    ) {
        //tabLayoutId获取
        val id: Int = view.resources.getIdentifier(tabLayoutId, "id", view.context.packageName)
        //adapter
        view.adapter = fragmentAdapter
        //viewPager
        view.setupWithViewPager2((view.rootView as View).findViewById(id))
    }

    /**
     *  colorSpan 方法适配
     *
     */
    @BindingAdapter(value = ["spanText", "spanStart", "spanEnd", "spanColor"], requireAll = false)
    @JvmStatic
    fun setColorSpan(
        view: TextView,
        spanText: String?,
        spanStart: Int,
        spanEnd: Int?,
        @ColorRes spanColor: Int?
    ) {
        val length = spanText?.length ?: 0
        if (spanStart < length) view.text = SpannableString(spanText ?: "").apply {
            setSpan(
                ForegroundColorSpan(spanColor ?: view.context.getColor(R.color.colorPrimary)),
                spanStart,
                spanEnd ?: spanText?.length ?: 0,
                android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }
    }

    /**
     *  ViewPager currentItem
     *
     * @param view
     */
    @BindingAdapter(
        value = ["pagerCurrent"],
        requireAll = false
    )
    @JvmStatic
    fun setViewPagerCurrent(
        view: ViewPager,
        pagerCurrent: Int?
    ) {
        var listener: ViewTreeObserver.OnGlobalLayoutListener? = null
        listener = ViewTreeObserver.OnGlobalLayoutListener {
            view.currentItem = pagerCurrent ?: 0
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)
    }


    /**
     *  ViewPager currentItem
     *
     * @param view
     */
    @BindingAdapter(
        value = ["validClick", "clickDuration"],
        requireAll = false
    )
    @JvmStatic
    fun setInvalidClick(
        view: View,
        validClick: View.OnClickListener,
        duration: Long?
    ) {
        view.setOnClickListener {
            if (!isInvalidClick(view, duration ?: INTERNAL_TIME)) {
                validClick.onClick(it)
            }
        }
    }


    /**
     *  glide load 方法适配
     *
     */
    @BindingAdapter(value = ["path", "radius"], requireAll = false)
    @JvmStatic
    fun path(
        view: ImageView,
        path: String?,
        radius: Int?
    ) {
        if (!path.isNullOrEmpty()) {
            view.glideLoad(path, radius)
        }
    }

    /**
     *  glide load 方法适配
     *
     */
    @BindingAdapter(value = ["bitmap", "radius"], requireAll = false)
    @JvmStatic
    fun bitmap(
        view: ImageView,
        bitmap: Bitmap?,
        radius: Int?
    ) {
        if (bitmap != null) {
            glideWith(view.context, radius)
                .load(bitmap)
                .into(view)
        }
    }

    /**
     *  EditorAction 方法适配
     *
     */
    @BindingAdapter(value = ["onAction", "onActionSearch"], requireAll = false)
    @JvmStatic
    fun editActionClick(
        view: EditText,
        onAction: OnEditorActionListener?,
        onActionSearch: OnEditorSearchActionListener?,
    ) {
        view.setOnEditorActionListener { _, actionId, _ ->
            when {
                actionId == EditorInfo.IME_ACTION_SEARCH && onActionSearch != null -> {
                    return@setOnEditorActionListener onActionSearch.onSearchAction()
                }

                onAction != null -> {
                    return@setOnEditorActionListener onAction.onAction(actionId)
                }

                else -> return@setOnEditorActionListener false
            }
        }
    }

}