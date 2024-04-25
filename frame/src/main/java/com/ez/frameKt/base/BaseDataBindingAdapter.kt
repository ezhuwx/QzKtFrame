package com.ez.frameKt.base

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseDataBindingHolder

/**
 * @author : ezhuwx
 * Describe :通用列表基类
 * Designed on 2021/11/2
 * E-mail : ezhuwx@163.com
 * Update on 16:40 by ezhuwx
 */
abstract class BaseDataBindingAdapter<T, BD : ViewDataBinding>(resId: Int) :
    BaseQuickAdapter<T, BaseDataBindingHolder<BD>>(resId)  {
    /**
     * 当 ViewHolder 创建完毕以后，会执行此回掉
     * 可以在这里做任何你想做的事情
     */
    override fun onItemViewHolderCreated(
        viewHolder: BaseDataBindingHolder<BD>,
        viewType: Int
    ) {
        // 绑定 view
        DataBindingUtil.bind<ViewDataBinding>(viewHolder.itemView)
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * 实现此方法，并使用 helper 完成 item 视图的操作
     *
     * @param holder A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    override fun convert(
        holder: BaseDataBindingHolder<BD>,
        item: T
    ) {
        // 获取 Binding
        val binding: BD = holder.dataBinding!!
        convertData(holder, binding, item)
        binding.executePendingBindings()
    }

    abstract fun convertData(holder: BaseDataBindingHolder<BD>, binding: BD, item: T)


}