package com.ez.kotlin.frame.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AppCompatDelegate
import com.ez.kotlin.frame.BuildConfig
import com.ez.kotlin.frame.base.BaseApplication

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

/**
 * 深色模式切换
 *
 */
fun switchDayNightMode(listener: OnDayNightChange) {
    val userSet = DayNightMode.valueOf(
        MMKVUtil.mmkv.getString(
            DayNightMode::class.simpleName,
            DayNightMode.SYSTEM.name
        )!!
    )
    when (userSet) {
        DayNightMode.SYSTEM -> {
            //打开深色模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            MMKVUtil.mmkv.putString(
                DayNightMode::class.simpleName,
                DayNightMode.NIGHT.name
            )
            BaseApplication.instance.dayNightMode = DayNightMode.NIGHT
            listener.onChange(DayNightMode.NIGHT)
        }
        DayNightMode.NIGHT -> {
            //打开浅色模式
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            MMKVUtil.mmkv.putString(
                DayNightMode::class.simpleName,
                DayNightMode.LIGHT.name
            )
            BaseApplication.instance.dayNightMode = DayNightMode.LIGHT
            listener.onChange(DayNightMode.LIGHT)
        }
        DayNightMode.LIGHT -> {
            //跟随系统
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            MMKVUtil.mmkv.putString(
                DayNightMode::class.simpleName,
                DayNightMode.SYSTEM.name
            )
            BaseApplication.instance.dayNightMode = DayNightMode.SYSTEM
            listener.onChange(DayNightMode.SYSTEM)
        }
    }

}

/**
 * TODO 深色模式
 *
 */
enum class DayNightMode {
    /**
     * 跟随系统
     *
     */
    SYSTEM,

    /**
     * 浅色模式
     *
     */
    LIGHT,

    /**
     * 深色模式
     *
     */
    NIGHT

}

interface OnDayNightChange {
    /**
     * TODO 深色模式变化
     *
     * @param mode 当前模式
     */
    fun onChange(mode: DayNightMode)
}