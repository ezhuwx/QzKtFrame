package com.ez.kotlin.frame.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Process
import android.os.StrictMode
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.ez.kotlin.frame.utils.logD
import com.jeremyliao.liveeventbus.LiveEventBus
import com.orhanobut.logger.*
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import me.jessyan.autosize.utils.ScreenUtils
import com.ez.kotlin.frame.utils.DayNightMode
import com.ez.kotlin.frame.utils.MMKVUtil

abstract class BaseApplication : Application() {
    //activity集合
    private var allActivities = mutableSetOf<AppCompatActivity>()

    //是否屏蔽系统字体大小
    var isExcludeFontScale = false

    //是否是debug模式
    var isDebug = false

    //默认状态栏颜色
    var statusBarColorId = Color.BLACK

    //默认跟随系统深色模式
    var dayNightMode = DayNightMode.SYSTEM

    //MMKV file名称
    var mmkvName = "prefs"

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: BaseApplication

        @SuppressLint("StaticFieldLeak")
        lateinit var mContext: Context
    }

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
            isDebug = debug()
            statusBarColorId = statusBarColor()
            //设置进程的优先级，不与主线程抢资源
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            LiveEventBus.config().lifecycleObserverAlwaysActive(false).setContext(this)
            initMMKVAndDayNight()
            initSmartRefresh()
            initAutoSize()
            initLogger()
            init()
        }.start()
    }

    /**
     * 初始化MMKV和深色模式
     * */
    open fun initMMKVAndDayNight() {
        //初始化腾讯mmkv
        MMKV.initialize(
            this,
            if (isDebug) MMKVLogLevel.LevelDebug
            else MMKVLogLevel.LevelNone
        )
        //保存的深色模式设置
        dayNightMode = DayNightMode.valueOf(
            MMKVUtil.mmkv.decodeString(
                DayNightMode::class.simpleName,
                DayNightMode.SYSTEM.name
            )!!
        )
        //恢复设置
        when (dayNightMode) {
            DayNightMode.NIGHT ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            DayNightMode.LIGHT ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> {
                logD("DayNightMode：${DayNightMode.SYSTEM.name}")
            }
        }
    }

    open fun init() {}

    /**
     * 初始化 SmartRefresh
     */
    abstract fun initSmartRefresh()

    /**
     * AutoSize屏幕适配初始化
     */
    open fun initAutoSize() {
        AutoSize.initCompatMultiProcess(this)
        AutoSizeConfig.getInstance() //屏蔽系统字体大小
            .setExcludeFontScale(isExcludeFontScale).onAdaptListener = object : onAdaptListener {
            override fun onAdaptBefore(target: Any, activity: Activity) {
                AutoSizeConfig.getInstance().screenWidth =
                    ScreenUtils.getScreenSize(activity)[0]
            }

            override fun onAdaptAfter(target: Any, activity: Activity) {}
        }
    }

    /**
     * Logger初始化
     */
    open fun initLogger() {
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
     * 状态栏颜色
     * */
    abstract fun statusBarColor(): Int


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
        allActivities.forEach {
            it.finish()
        }
        allActivities.clear()
    }
}