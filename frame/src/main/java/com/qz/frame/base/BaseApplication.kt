package com.qz.frame.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.Process
import android.os.StrictMode
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import com.jeremyliao.liveeventbus.LiveEventBus
import com.qz.frame.utils.attachBaseContextExcludeFontScale
import com.qz.frame.utils.getResourcesExcludeFontScale

abstract class BaseApplication : Application() {
    /**
     * Activity管理集合
     */
    var allActivities = mutableSetOf<AppCompatActivity>()

    @SuppressLint("StaticFieldLeak")
    companion object {
        lateinit var instance: BaseApplication
        lateinit var mContext: Context
    }

    lateinit var config: OptionConfig

    /**
     * 初始化配置项
     */
    abstract fun onInitConfig(): OptionConfig

    /**
     * 初始化
     * */
    @CallSuper
    open fun install() {
        instance = this
        mContext = applicationContext
        //解决7.0版本后调用相机报错的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
        lateInitSDK()
    }

    /**
     * 非必须在主线程初始化的SDK放到子线程初始化
     */
    open fun lateInitSDK() {
        Thread {
            //设置进程的优先级，不与主线程抢资源
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            LiveEventBus.config().lifecycleObserverAlwaysActive(false).setContext(this)
            //初始化配置项
            config = onInitConfig()
            config.init(this)
            //自定义初始化调用
            init()
        }.start()
    }

    open fun init() {}

    /**
     * 分包
     */
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base?.attachBaseContextExcludeFontScale())
        MultiDex.install(this)
    }

    override fun getResources(): Resources {
        return getResourcesExcludeFontScale(super.getResources())
    }

    /**
     * 添加Activity管理
     */
    open fun addActivity(act: AppCompatActivity) {
        allActivities.add(act)
    }

    /**
     * 从管理中移出
     */
    open fun removeActivity(act: AppCompatActivity) {
        allActivities.remove(act)
    }

    /**
     * 退出程序
     */
    open fun exitApp() {
        allActivities.forEach { it.finish() }
        allActivities.clear()
    }

    /**
     * 退出程序
     */
    open fun loginOut() {
        allActivities.filterIndexed { index, _ -> index != allActivities.size - 1 }.forEach {
            it.finish()
        }
    }
}