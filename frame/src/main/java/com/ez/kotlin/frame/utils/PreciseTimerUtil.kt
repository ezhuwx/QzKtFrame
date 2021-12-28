package com.ez.kotlin.frame.utils

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.lang.ref.WeakReference
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit

/**
 * @author : ezhuwx
 * Describe :精准定时器
 * Designed on 2021/11/12
 * E-mail : ezhuwx@163.com
 * Update on 9:13 by ezhuwx
 */
class PreciseTimerUtil {
    private var timeListener: OnTimeListener? = null
    private var onMultiTimeListener: OnMultiTimeListener? = null
    private var threadPoolExecutor: ScheduledThreadPoolExecutor? = null
    private var handler: TimeHandler = TimeHandler(this)
    private var delay: Long = 0
    private var interval: Long = 0
    private var jobName: String? = null


    fun startNewTimer(delay: Int, interval: Int): PreciseTimerUtil {
        return startNewTimer(delay.toLong(), interval.toLong(), null)
    }

    /**
     * 开始计时
     * 单位：毫秒
     *
     * @param delay    延时
     * @param interval 间隔
     * @param jobName  名称
     * @return PreciseTimer
     */
    fun startNewTimer(delay: Long, interval: Long, jobName: String?): PreciseTimerUtil {
        if (interval > 0) {
            if (threadPoolExecutor == null) {
                this.delay = delay
                this.jobName = jobName
                this.interval = interval
                threadPoolExecutor = ScheduledThreadPoolExecutor(1,
                    jobName?.let { TimeThreadFactory(it) } ?: TimeThreadFactory())
            }
        }
        threadPoolExecutor?.scheduleAtFixedRate({
            val message = Message()
            message.what = TimeHandler.TYPE_TIME
            handler.sendMessage(message)
        }, delay, interval, TimeUnit.MILLISECONDS)

        return this
    }

    /**
     * 开始计时
     * 单位：毫秒
     *
     * @param delay    延时
     * @param interval 间隔
     */
    fun startMultiTimer(
        delay: Long,
        interval: Long,
        listener: OnMultiTimeListener?,
    ): ScheduledFuture<*>? {
        return startMultiTimer(delay, interval, null, listener)
    }

    /**
     * 开始计时
     * 单位：毫秒
     *
     * @param delay    延时
     * @param interval 间隔
     * @param jobName  名称
     */
    fun startMultiTimer(
        delay: Long,
        interval: Long,
        jobName: String?,
        listener: OnMultiTimeListener?,
    ): ScheduledFuture<*>? {
        if (interval > 0) {
            if (threadPoolExecutor == null) {
                this.delay = delay
                this.jobName = jobName
                this.interval = interval
                onMultiTimeListener = listener
                threadPoolExecutor = ScheduledThreadPoolExecutor(Int.MAX_VALUE,
                    jobName?.let { TimeThreadFactory(it) } ?: TimeThreadFactory())
            }
        }
        return threadPoolExecutor?.scheduleAtFixedRate({
            val message = Message()
            message.what = TimeHandler.TYPE_TIME
            handler.sendMessage(message)
        }, delay, interval, TimeUnit.MILLISECONDS)
    }

    /**
     * 时间线工具
     */
    private class TimeThreadFactory : ThreadFactory {
        private val threadGroup: ThreadGroup?
        private var jobName = "TimeExecutor"

        constructor() {
            val s = System.getSecurityManager()
            threadGroup = if (s == null) Thread.currentThread().threadGroup else s.threadGroup
        }

        constructor(jobName: String) {
            this.jobName = jobName
            val s = System.getSecurityManager()
            threadGroup = if (s == null) Thread.currentThread().threadGroup else s.threadGroup
        }

        override fun newThread(runnable: Runnable): Thread {
            return Thread(threadGroup, runnable, jobName, 0)
        }
    }

    /**
     * 时间信使
     */
    private class TimeHandler constructor(scheduledUtil: PreciseTimerUtil) :
        Handler(Looper.getMainLooper()) {
        private val scheduledUtil: PreciseTimerUtil
        private val weakReference: WeakReference<PreciseTimerUtil> =
            WeakReference<PreciseTimerUtil>(scheduledUtil)

        override fun handleMessage(msg: Message) {
            when (msg.what) {
                TYPE_TIME -> {
                    scheduledUtil.timeListener?.onTime()
                    scheduledUtil.onMultiTimeListener?.onTime()
                }
            }
            super.handleMessage(msg)
        }

        companion object {
            const val TYPE_TIME = 0
        }

        init {
            this.scheduledUtil = weakReference.get()!!
        }
    }

    /**
     * 重启定时器
     */
    fun restart() {
        restart(delay)
    }

    /**
     * 重启定时器
     *
     * @param delay 延时执行：毫秒
     */
    fun restart(delay: Long) {
        startNewTimer(delay, interval, jobName)
    }

    /**
     * 定时器是否关闭
     */
    fun isShutDown(): Boolean {
        return threadPoolExecutor?.run { isShutdown } ?: true
    }

    /**
     * 关闭定时器
     */
    fun shutDown() {
        threadPoolExecutor?.run {
            timeListener?.onShutdown()
            shutdownNow()
            threadPoolExecutor = null
        }
    }

    fun addTimeListener(timeListener: OnTimeListener?): PreciseTimerUtil {
        this.timeListener = timeListener
        return this
    }

    interface OnTimeListener {
        /**
         * 定时时间到
         */
        fun onTime()

        /**
         * 定时结束
         */
        fun onShutdown()
    }

    fun interface OnMultiTimeListener {
        /**
         * 定时时间到
         */
        fun onTime()
    }
}