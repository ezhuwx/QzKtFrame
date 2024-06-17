package com.qz.frame.utils

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * @author : ezhuwx
 * Describe :通知构建拓展
 * Designed on 2024/6/17
 * E-mail : ezhuwx@163.com
 * Update on 13:37 by ezhuwx
 */
typealias NotificationBuildFun = ((NotificationCompat.Builder) -> Unit)

/**
 * 创建通知渠道
 */
fun BaseNotification.create(
    context: Context,
    content: String = "",
    process: Int? = null,
    onBuild: NotificationBuildFun? = null
): Pair<Int, Notification> {
    //创建通知渠道
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) context.createChannel(
        channelId,
        channelName,
        channelDescription,
        channelImportance,
        enableVibration,
        enableLights,
        lightColor,
        groupId,
        groupName
    )
    //创建通知
    return Pair(
        noticeId, NotificationCompat.Builder(context, channelId)
            .setContentTitle(noticeTitle)
            .setTicker(noticeTitle)
            .setContentText(content)
            .setAutoCancel(true)
            .setOngoing(isOngoing)
            .setOnlyAlertOnce(true)
            .setSmallIcon(noticeSmallIcon)
            .apply {
                noticePriority?.let { priority -> setPriority(priority) }
                noticeCategory?.let { category -> setCategory(category) }
                getPendingIntent(context)?.let { intent -> setContentIntent(intent) }
                process?.let { setProgress(100, process, false) }
                onBuild?.invoke(this)
            }.build()
    )
}

@SuppressLint("MissingPermission")
fun Pair<Int, Notification>.postNotify(context: Context) {
    val (noticeId, notification) = this
    NotificationManagerCompat.from(context).notify(noticeId, notification)
}

@RequiresApi(Build.VERSION_CODES.O)
fun Context.createChannel(
    channelId: String,
    channelName: CharSequence?,
    channelDescription: String? = null,
    channelImportance: Int = NotificationManager.IMPORTANCE_LOW,
    enableVibration: Boolean = false,
    enableLights: Boolean = false,
    lightColor: Int = Color.WHITE,
    groupId: String? = null,
    groupName: String? = null
) {
    //创建通知渠道
    with(NotificationManagerCompat.from(this)) {
        //分组
        if (groupId != null) {
            val channelGroup = getNotificationChannelGroupCompat(groupId)
            if (channelGroup == null) createNotificationChannelGroup(
                NotificationChannelGroup(groupId, groupName)
            )
        }
        //渠道
        val channel = getNotificationChannel(channelId)
        if (channel == null) createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                channelImportance
            ).apply {
                description = channelDescription
                enableVibration(enableVibration)
                enableLights(enableLights)
                this.lightColor = lightColor
                group = groupId
            })
    }
}

/**
 * 通知点击事件
 */
fun Intent.buildPendingIntent(
    context: Context,
    intentFlags: Int = Intent.FLAG_ACTIVITY_NEW_TASK,
    pendingFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
): PendingIntent? {
    flags = intentFlags
    return PendingIntent.getActivity(context, 0, this, pendingFlags)
}

/**
 * 通知点击事件
 */
fun Array<Intent>.buildPendingIntents(
    context: Context,
    pendingFlags: Int = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
): PendingIntent? {
    return PendingIntent.getActivities(context, 0, this, pendingFlags)
}

/**
 * 通知
 */

interface BaseNotification {
    /**
     * 通知图标
     */

    @get:DrawableRes
    val noticeSmallIcon: Int

    /**
     * 通知ID
     */
    val noticeId: Int

    /**
     * 通知标题
     */
    val noticeTitle: String

    /**
     * 通知频道
     */
    val channelId: String

    /**
     * 通知频道名称
     */
    val channelName: CharSequence?

    /**
     * 通知频道描述
     */
    val channelDescription: String?

    /**
     * 通知重要程度
     */
    val channelImportance: Int

    /**
     * 通知优先级
     */
    val noticePriority: Int?

    /**
     * 通知分类
     */
    val noticeCategory: String?

    /**
     * 是否锁定通知
     */
    val isOngoing: Boolean

    /**
     * 是否开启震动
     */
    val enableVibration: Boolean

    /**
     * 是否开启呼吸灯
     */
    val enableLights: Boolean

    /**
     *  呼吸灯颜色
     */
    val lightColor: Int

    /**
     * 通知分组ID
     */
    val groupId: String?

    /**
     * 通知分组名称
     */
    val groupName: String?

    /**
     * 通知点击事件
     */
    fun getPendingIntent(context: Context): PendingIntent? = null

}
