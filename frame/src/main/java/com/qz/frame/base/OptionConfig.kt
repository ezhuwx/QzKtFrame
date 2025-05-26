package com.qz.frame.base

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.LiveEvent
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.qz.frame.utils.logD
import com.qz.frame.utils.observe
import com.tencent.mmkv.MMKV
import com.tencent.mmkv.MMKVLogLevel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.jessyan.autosize.AutoSize
import me.jessyan.autosize.AutoSizeConfig
import me.jessyan.autosize.onAdaptListener
import me.jessyan.autosize.utils.ScreenUtils

/**
 * @author : ezhuwx
 * Describe : 项目初始化配置
 * Designed on 2025/3/21
 * E-mail : ezhuwx@163.com
 * Update on 16:29 by ezhuwx
 */
abstract class OptionConfig() : LifecycleOwner {
    private lateinit var mLifecycleRegistry: LifecycleRegistry
    override val lifecycle: Lifecycle
        get() = mLifecycleRegistry

    /**
     * 是否屏蔽系统字体大小
     */
    open var isExcludeFontScale = false

    /**
     * 默认跟随系统深色模式
     */
    open var dayNightMode = DayNightMode.SYSTEM

    /**
     * 是否是debug模式
     */
    abstract var isDebug: Boolean

    /**
     * 默认状态栏颜色
     */
    abstract var statusBarColorId: Int

    /**
     * 初始化mmkv
     */
    open lateinit var mmkv: MMKV

    /**
     * mmkv实例获取
     */
    abstract fun getMMKV(): MMKV

    @CallSuper
    open fun init(context: Context) {
        mLifecycleRegistry = LifecycleRegistry(this)
        initMMKVAndDayNight(context)
        initSmartRefresh()
        initAutoSize(context)
        initLogger(context)
    }

    /**
     * Logger初始化
     */
    open fun initLogger(context: Context) {
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
     * AutoSize屏幕适配初始化
     */
    open fun initAutoSize(context: Context) {
        AutoSize.initCompatMultiProcess(context)
        AutoSizeConfig.getInstance()
            //屏蔽系统字体大小
            .setExcludeFontScale(isExcludeFontScale).onAdaptListener = object : onAdaptListener {
            override fun onAdaptBefore(target: Any, activity: Activity) {
                AutoSizeConfig.getInstance().screenWidth =
                    ScreenUtils.getScreenSize(activity)[0]
            }

            override fun onAdaptAfter(target: Any, activity: Activity) {}
        }
    }


    /**
     * 初始化MMKV和深色模式
     * */
    open fun initMMKVAndDayNight(context: Context) {
        //初始化腾讯mmkv
        MMKV.initialize(
            context,
            if (isDebug) MMKVLogLevel.LevelDebug
            else MMKVLogLevel.LevelNone
        )
        mmkv = getMMKV()
        MainScope().launch {
            //保存的深色模式设置
            dayNightMode = DayNightMode.valueOf(
                mmkv.decodeString(DayNightMode::class.simpleName, dayNightMode.name)!!
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
            //监听深色模式变化
            SysConfigChangeEvent::class.java.observe(this@OptionConfig) { event ->
                //保存的深色模式设置
                dayNightMode = DayNightMode.valueOf(
                    mmkv.decodeString(DayNightMode::class.simpleName, DayNightMode.SYSTEM.name)!!
                )
                //如跟随系统
                if (event !== null && dayNightMode == DayNightMode.SYSTEM) {
                    when (event.newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                        Configuration.UI_MODE_NIGHT_NO -> {
                            //关闭夜间模式
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }

                        Configuration.UI_MODE_NIGHT_YES -> {
                            //打开夜间模式
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        }

                        else -> {}
                    }
                }
            }
        }
    }


    /**
     * Logger TAG
     * */
    abstract fun getAppName(): String

    /**
     * 初始化 SmartRefresh
     */
    abstract fun initSmartRefresh()


    /**
     * 删除全部数据(传了参数就是按key删除)
     */
    fun deleteKeyOrAll(key: String? = null) {
        if (key == null) mmkv.clearAll()
        else mmkv.removeValueForKey(key)
    }

    /** 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    fun contains(key: String) = mmkv.contains(key)

    /**
     * 深色模式切换
     *
     */
    fun switchDayNightMode(userSet: DayNightMode) {
        when (userSet) {
            DayNightMode.NIGHT -> {
                //打开深色模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                mmkv.encode(DayNightMode::class.simpleName, DayNightMode.NIGHT.name)
            }

            DayNightMode.LIGHT -> {
                //打开浅色模式
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                mmkv.encode(DayNightMode::class.simpleName, DayNightMode.LIGHT.name)
            }

            DayNightMode.SYSTEM -> {
                //跟随系统
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                mmkv.decodeString(DayNightMode::class.simpleName, DayNightMode.SYSTEM.name)
            }
        }
    }
}


/**
 *  深色模式
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

/**
 * 深色模式变化事件
 */
data class SysConfigChangeEvent(val newConfig: Configuration) : LiveEvent
