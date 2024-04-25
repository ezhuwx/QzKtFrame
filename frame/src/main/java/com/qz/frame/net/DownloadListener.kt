package com.qz.frame.net


/**
 * @author : ezhuwx
 * Describe : 下载监听
 * Designed on 2021/11/5
 * E-mail : ezhuwx@163.com
 * Update on 10:23 by ezhuwx
 */
class DownloadListener<out T>(val value: Any?) {

    val isSuccess: Boolean get() = value !is Failure && value !is Progress

    val isFailure: Boolean get() = value is Failure

    val isLoading: Boolean get() = value is Progress

    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    companion object {
        fun <T> success(value: T): DownloadListener<T> =
            DownloadListener(value)

        fun <T> failure(exception: Throwable): DownloadListener<T> =
            DownloadListener(createFailure(exception))

        fun <T> progress(currentLength: Long, length: Long, process: Float): DownloadListener<T> =
            DownloadListener(createLoading(currentLength, length, process))
    }

    data class Failure(val exception: Throwable)

    data class Progress(val currentLength: Long, val length: Long, val process: Float)
}


private fun createFailure(exception: Throwable): DownloadListener.Failure =
    DownloadListener.Failure(exception)


private fun createLoading(currentLength: Long, length: Long, process: Float) =
    DownloadListener.Progress(currentLength, length, process)


inline fun <R, T> DownloadListener<T>.fold(
    onSuccess: (value: T) -> R,
    onLoading: (loading: DownloadListener.Progress) -> R,
    onFailure: (exception: Throwable?) -> R
): R {
    return when {
        isFailure -> {
            onFailure(exceptionOrNull())
        }
        isLoading -> {
            onLoading(value as DownloadListener.Progress)
        }
        else -> {
            onSuccess(value as T)
        }
    }
}


