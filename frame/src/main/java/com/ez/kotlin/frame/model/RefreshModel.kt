package com.ez.kotlin.frame.model

import com.ez.kotlin.frame.base.*
import com.ez.kotlin.frame.interfaces.OnRefreshStateChangeListener
import com.ez.kotlin.frame.utils.SingleLiveEvent
import com.ez.kotlin.frame.utils.shortShow
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
     * 请求方法
     */
    lateinit var call: () -> Unit

    /**
     * 预加载页数 0关闭
     */
    var preLoadPage = 1


    /**
     * 是否无更多数据
     */
    val isNoMoreData = SingleLiveEvent<Boolean>().apply { value = false }

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
     * 刷新中
     */
    var isRefresh = false

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
        this.call = call
        view.addStateChangeListener(StateListener())
        refreshListener = object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                isRefresh = true
                isLoadMore = false
                //重置页码
                page = PAGE_FIRST_INDEX
                //请求
                call()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                isRefresh = false
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
        this.call = call
        view.addStateChangeListener(StateListener())
        refreshListener = object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                isRefresh = true
                isLoadMore = false
                //重置页码
                page = PAGE_FIRST_INDEX
                //请求
                call()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                isRefresh = false
                isLoadMore = true
                //请求
                call()
            }
        }
    }

    /**
     * 刷新加载
     * */
    fun observeRefreshLoadMore(call: () -> Unit) {
        this.call = call
        refreshListener = object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                isRefresh = true
                isLoadMore = false
                //重置页码
                page = PAGE_FIRST_INDEX
                //请求
                call()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                isRefresh = false
                isLoadMore = true
                //请求
                call()
            }
        }
    }

    /**
     * 刷新加载
     * */
    fun observeRefreshLoadMore(manager: PageStateManager, call: () -> Unit) {
        this.call = call
        manager.onRefreshStateChangeListener = StateListener()
        refreshListener = object : OnRefreshLoadMoreListener {
            override fun onRefresh(refreshLayout: RefreshLayout) {
                isRefresh = true
                isLoadMore = false
                //重置页码
                page = PAGE_FIRST_INDEX
                //请求
                call()
            }

            override fun onLoadMore(refreshLayout: RefreshLayout) {
                isRefresh = false
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
        this.isNoMoreData.value = dataSize < pageSize.value!!
        this.dataSize.value = dataSize
        isLoadMore = false
        isRefresh = false
        page++
    }

    /**
     * 刷新加载结束页码自增
     *
     * */
    fun loadError() {
        this.dataSize.value = -1
    }

    inner class StateListener : OnRefreshStateChangeListener {
        override fun stateError(errorMsg: String): Boolean {
            if (isLoadMore) {
                loadError()
                errorMsg.shortShow()
            }
            return isLoadMore
        }

        override fun stateLoading(): Boolean {
            return isLoadMore || isRefresh
        }

        override fun stateEmpty(): Boolean {
            return isLoadMore
        }

    }

}
