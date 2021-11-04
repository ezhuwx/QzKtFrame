package com.ez.kotlin.frame.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import androidx.multidex.MultiDex
import com.ez.kotlin.frame.utils.logD
import com.jeremyliao.liveeventbus.LiveEventBus
import com.orhanobut.logger.*
import com.tencent.bugly.Bugly
import com.tencent.mmkv.MMKV
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import me.jessyan.autosize.utils.ScreenUtils
import kotlin.system.exitProcess


abstract class BaseApplication : Application() {
    private var allActivities: HashSet<AppCompatActivity>? = null
    var isDebug = false

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: BaseApplication
        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        mContext = applicationContext
        //解决7.0版本后调用相机报错的问题
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
        }
        //初始化腾讯mmkv
        val rootDir = MMKV.initialize(this)
        logD("mmkv_root------:${rootDir}")
        lateInitSDK()
    }

    /**
     * 非必须在主线程初始化的SDK放到子线程初始化
     */
    private fun lateInitSDK() {
        Thread {
            isDebug = debug()
            //设置进程的优先级，不与主线程抢资源
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            LiveEventBus.config().lifecycleObserverAlwaysActive(false).setContext(this)
            initAutoSize()
            initLogger()
            init()
        }.start()
    }

    abstract fun init()

    /**
     * 初始化 腾讯Bugly
     */
    fun initBugly(buglyID: String) {
        Bugly.init(applicationContext, buglyID, false)
    }

    /**
     * AutoSize屏幕适配初始化
     */
    private fun initAutoSize() {
        AutoSize.initCompatMultiProcess(this)
        AutoSizeConfig.getInstance() //屏蔽系统字体大小
            .setExcludeFontScale(true).onAdaptListener = object : onAdaptListener {
            override fun onAdaptBefore(target: Any, activity: Activity) {
                AutoSizeConfig.getInstance().screenWidth = ScreenUtils.getScreenSize(activity)[0]
            }

            override fun onAdaptAfter(target: Any, activity: Activity) {}
        }
    }

    /**
     * Logger初始化
     */
    private fun initLogger() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .tag(getAppName())
            .build()
        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return isDebug
            }
        })
    }

    /**
     *  debug
     * */
    abstract fun debug(): Boolean

    /**
     * Logger TAG
     * */
    abstract fun getAppName(): String

    /**
     * 刷新样式全局设置
     * */
    abstract fun initSmartRefresh()

    /**
     * 分包
     */
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    /**
     * 添加Activity管理
     */
    open fun addActivity(act: AppCompatActivity) {
        if (allActivities == null) {
            allActivities = hashSetOf()
        }
        allActivities!!.add(act)
    }

    /**
     * 从管理中移出
     */
    open fun removeActivity(act: AppCompatActivity) {
        allActivities!!.remove(act)
    }

    /**
     * 退出程序
     */
    open fun exitApp() {
        if (allActivities != null) {
            for (act in allActivities!!) {
                act.finish()
            }
        }
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }
}