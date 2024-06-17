package com.qz.frame.utils
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State.FAILED
import androidx.work.WorkInfo.State.RUNNING
import androidx.work.WorkInfo.State.SUCCEEDED
import androidx.work.WorkManager
import androidx.work.WorkerParameters
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
    private var isNotificationCreated: Boolean = false
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
    suspend fun setProgressAndNotice(
        data: Data,
        content: String = "",
        process: Int? = null,
        noticeType: BaseNotification? = null,
        onBuild: NotificationBuildFun? = null
    ) {
        noticeType?.create(applicationContext, content, process) { onBuild?.invoke(it) }
            ?.run {
                if (!isNotificationCreated) {
                    isNotificationCreated = true
                    val (noticeId, notification) = this
                    setForegroundAsync(ForegroundInfo(noticeId, notification))
                } else postNotify(applicationContext)
            }
        setProgress(data)
    }


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


