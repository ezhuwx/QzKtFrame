package com.qz.frame.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleOwner
import androidx.work.*
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import androidx.work.WorkInfo.State.SUCCEEDED
import com.qz.frame.R
import com.qz.frame.base.BaseApplication
import java.util.concurrent.TimeUnit

/**
 * @author : ezhuwx
 * Describe : WorkerManager拓展工具
 * Designed on 2024/6/14
 * E-mail : ezhuwx@163.com
 * Update on 16:34 by ezhuwx
 */
abstract class CommonWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        onWork(this)
        return Result.success()
    }

    /**
     * Work
     */
    abstract suspend fun onWork(worker: CommonWorker)

    /**
     * 设置进度和通知
     */
    suspend fun setProgressAndNotice(data: Data, content: String) {
        setForeground(buildForegroundInfo(content))
        setProgress(data)
    }

    /**
     * 创建通知渠道
     */
    open fun buildForegroundInfo(content: String): ForegroundInfo {
        //创建通知渠道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel()
        //创建通知
        val notification = onGetNoticeChannel().let { channelId ->
            NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle(onGetNoticeTitle())
                .setTicker(onGetNoticeTitle())
                .setContentText(content)
                .setSmallIcon(onGetSmallIcon())
                .setOngoing(isOngoing())
                .apply { onBuildNotice(this) }
                .build()
        }
        return ForegroundInfo(onGetNoticeId(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    open fun createChannel() {
        onGetNoticeChannel().let { channelId ->
            //创建通知渠道
            val channel = NotificationManagerCompat.from(applicationContext)
                .getNotificationChannel(channelId)
            if (channel == null) NotificationManagerCompat.from(applicationContext)
                .createNotificationChannel(NotificationChannel(
                    channelId,
                    onGetChannelName(),
                    getNoticeImportance()
                ).apply {
                    description = onGetChannelDescription()
                    enableVibration(enableVibration())
                    enableLights(enableLights())
                    lightColor = lightColor()
                })
        }
    }


    /**
     * 通知重要程度
     */
    @RequiresApi(Build.VERSION_CODES.N)
    open fun getNoticeImportance(): Int {
        return NotificationManager.IMPORTANCE_DEFAULT
    }

    /**
     * 构建通知
     */
    open fun onBuildNotice(notice: NotificationCompat.Builder) {}

    /**
     * 获取通知图标
     */
    abstract fun onGetSmallIcon(): Int

    /**
     * 是否锁定通知
     */
    open fun isOngoing(): Boolean = true

    /**
     * 是否开启震动
     */
    open fun enableVibration(): Boolean = true

    /**
     * 是否开启呼吸灯
     */
    open fun enableLights(): Boolean = true

    /**
     *  呼吸灯颜色
     */
    open fun lightColor(): Int = Color.WHITE

    /**
     * 通知频道
     */
    open fun onGetNoticeChannel(): String = R.string.other_channel.resString()

    /**
     * 通知频道名称
     */
    open fun onGetChannelName(): CharSequence? = R.string.other_notice.resString()

    /**
     * 通知频道描述
     */
    open fun onGetChannelDescription(): String = R.string.other_type_notice.resString()

    /**
     * 通知ID
     */
    open fun onGetNoticeId(): Int = onGetNoticeChannel().hashCode()

    /**
     * 通知标题
     */
    open fun onGetNoticeTitle(): String = BaseApplication.instance.getAppName()


}

/**
 * 创建一个一次性任务
 */
inline fun <reified W : CommonWorker> Data.Builder.createOneTimeWork(
    owner: LifecycleOwner,
    crossinline onProcess: (WorkInfo) -> Unit = {},
    crossinline onFailed: (WorkInfo) -> Unit = {},
    crossinline onSuccess: (WorkInfo) -> Unit = {},
    crossinline onStateChange: (WorkInfo) -> Unit = {},
): OneTimeWorkRequest {
    val request = OneTimeWorkRequestBuilder<W>()
        .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, Long.MAX_VALUE, TimeUnit.DAYS)
        .setInputData(build())
        .build()
    with(WorkManager.getInstance(BaseApplication.mContext.applicationContext)) {
        // 任务状态监听
        getWorkInfoByIdLiveData(request.id)
            .observe(owner) { info ->
                when (info.state) {
                    RUNNING -> onProcess(info)
                    SUCCEEDED -> onSuccess(info)
                    FAILED -> onFailed(info)
                    else -> {}
                }
                onStateChange(info)
            }
        // 执行任务
        enqueue(request)
    }
    return request
}

