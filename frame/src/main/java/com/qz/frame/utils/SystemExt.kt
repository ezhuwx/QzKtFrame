package com.qz.frame.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.net.*
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import com.jeremyliao.liveeventbus.LiveEventBus.config
import com.qz.frame.base.BaseApplication
import me.jessyan.autosize.utils.AutoSizeUtils

/**
 * @author : ezhuwx
 * Describe :系统工具类
 * Designed on 2021/10/28
 * E-mail : ezhuwx@163.com
 * Update on 15:37 by ezhuwx
 */
/**
 * 获取当前手机系统版本号
 *
 * @return 系统版本号
 */
fun getSystemVersion(): String? {
    return Build.VERSION.RELEASE
}

/**
 * 获取手机型号
 *
 * @return 手机型号
 */
fun getSystemModel(): String? {
    return Build.MODEL
}

/**
 * 获取手机厂商
 *
 * @return 手机厂商
 */
fun getDeviceBrand(): String? {
    return Build.BRAND
}

/**
 * 版本号
 */
fun getVersionCode(context: Context): Int {
    val pm = context.applicationContext.packageManager
    val packageInfo = try {
        pm.getPackageInfo(context.applicationContext.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return -1
    }
    return packageInfo.versionCode
}

/**
 * 版本名
 */
fun getVersionName(context: Context): String? {
    val pm = context.applicationContext.packageManager
    val packageInfo = try {
        pm.getPackageInfo(context.applicationContext.packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return ""
    }
    return packageInfo.versionName
}

/**
 * 获取设备ID
 */
@SuppressLint("HardwareIds")
fun getAndroidId(): String? {
    return Settings.Secure.getString(
        BaseApplication.instance.contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

/**
 * 判断相对应的APP是否存在
 *
 * @param packageName(包名) (包名)
 */
fun isAppAvailable(packageName: String): Boolean {
    var packageInfo: PackageInfo? = null
    try {
        packageInfo = BaseApplication.instance.packageManager.getPackageInfo(packageName, 0)
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return packageInfo != null
}

/**
 * App跳转到应用市场
 */
fun goSoftMarket(packageName: String) {
    val mAddress = "market://details?id=$packageName"
    val marketIntent = Intent("android.intent.action.VIEW")
    marketIntent.data = Uri.parse(mAddress)
    marketIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    BaseApplication.instance.startActivity(marketIntent)
}

fun Int?.pxDp(context: Context): Int? {
    return this?.let {
        AutoSizeUtils.dp2px(context, this.toFloat())
    }
}

/**
 * 保持字体大小不随系统设置变化（用在界面加载之前）
 * 要重写Activity的attachBaseContext()
 */
fun Context.attachBaseContextExcludeFontScale(): Context {
    return createConfigurationContext(resources.configuration.apply {
        //调整屏幕密度
        densityDpi = (densityDpi / fontScale).toInt()
        // 固定字体缩放
        this.fontScale = 1.0f
    })
}

/**
 * 保持字体大小不随系统设置变化（用在界面加载之前）
 * 要重写Activity的getResources()
 */
fun Context.getResourcesExcludeFontScale(resources: Resources): Resources {
    return with(resources.configuration) {
        //调整屏幕密度
        densityDpi = (densityDpi / fontScale).toInt()
        // 固定字体缩放
        this.fontScale = 1.0f
        createConfigurationContext(this).resources
    }
}
