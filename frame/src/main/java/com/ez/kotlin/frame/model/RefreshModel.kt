package com.ez.kotlin.frame.model

import com.ez.kotlin.frame.base.*
import com.ez.kotlin.frame.utils.SingleLiveEvent
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/11/8
 * E-mail : ezhuwx@163.com
 * Update on 16:47 by ezhuwx
 */

class RefreshModel : BaseViewModel() {
    companion object {
        /**
         * 初始页码
         */
        const val PAGE_FIRST_INDEX = 1
    }

    /**
     * 页码
     *
     * */
    var page = PAGE_FIRST_INDEX

    /**
     * 是否是加载
     *
     * */
    var isLoadMore = false

    /**
     * 单页大小
     *
     * */
    val dataSize = SingleLiveEvent<Int>()

    /**
     * 每页大小
     *
     * */
    val pageSize = SingleLiveEvent<Int>().apply { value = 20 }

    /**
     * 监听
     */
    lateinit var refreshListener: OnRefreshLoadMoreListener


    /**
     * 刷新加载
     * */
    fun <E, V : BaseStateActivity<E>> observeRefreshLoadMore(
        view: V,
        call: () -> Unit,
    ) where E : BaseViewModel {
        refreshListener = object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                isLoadMore = false
                //屏蔽loading
                view.setSkipLoading(true)
                //屏蔽错误状态UI
                view.setSkipError(false)
                //重置页码
                page = PAGE_FIRST_INDEX
                //请求
                call()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                isLoadMore = true
                //屏蔽loading
                view.setSkipLoading(true)
                //屏蔽错误状态UI
                view.setSkipError(true)
                //请求
                call()
            }
        }
    }

    /**
     * 刷新加载
     * */
    fun <E, V : BaseStateFragment<E>> observeRefreshLoadMore(
        view: V,
        call: () -> Unit,
    ) where E : BaseViewModel {
        refreshListener = object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                isLoadMore = false
                //屏蔽loading
                view.setSkipLoading(true)
                //屏蔽错误状态UI
                view.setSkipError(false)
                //重置页码
                page = PAGE_FIRST_INDEX
                //请求
                call()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                isLoadMore = true
                //屏蔽loading
                view.setSkipLoading(true)
                //屏蔽错误状态UI
                view.setSkipError(true)
                //请求
                call()
            }
        }
    }

    /**
     * 刷新加载
     * */
    fun <E, V : BaseActivity<E>> observeRefreshLoadMore(
        view: V,
        call: () -> Unit,
    ) where E : BaseViewModel {
        refreshListener = object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                isLoadMore = false
                //重置页码
                page = PAGE_FIRST_INDEX
                //请求
                call()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                isLoadMore = true
                //请求
                call()
            }
        }
    }

    /**
     * 刷新加载
     * */
    fun <E, V : BaseFragment<E>> observeRefreshLoadMore(
        view: V,
        call: () -> Unit,
    ) where E : BaseViewModel {
        refreshListener = object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                isLoadMore = false
                //重置页码
                page = PAGE_FIRST_INDEX
                //请求
                call()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                isLoadMore = true
                //请求
                call()
            }
        }
    }

    /**
     * 刷新加载结束页码自增
     *
     * */
    fun finished(dataSize: Int) {
        this.dataSize.value = dataSize
        isLoadMore = false
        page++
    }
}