package com.qz.frame.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.LiveEvent
import com.jeremyliao.liveeventbus.core.Observable

/**
 * @author : ezhuwx
 * Describe : Event事件
 * Designed on 2023/12/11
 * E-mail : ezhuwx@163.com
 * Update on 19:12 by ezhuwx
 */
inline fun <reified T : LiveEvent> T?.post() {
    event().post(this)
}

/**
 * App内发送消息，跨进程使用
 *
 * @param value 发送的消息
 */
inline fun <reified T : LiveEvent> T?.postAcrossProcess() {
    event().postAcrossProcess(this)
}

/**
 * App之间发送消息
 *
 * @param value 发送的消息
 */
inline fun <reified T : LiveEvent> T?.postAcrossApp() {
    event().postAcrossApp(this)
}

/**
 * 进程内发送消息，延迟发送
 *
 * @param value 发送的消息
 * @param delay 延迟毫秒数
 */
inline fun <reified T : LiveEvent> T?.postDelay(value: T?, delay: Long) {
    event().postDelay(this, delay)
}

/**
 * 进程内发送消息，延迟发送，带生命周期
 * 如果延时发送消息的时候sender处于非激活状态，消息取消发送
 *
 * @param sender 消息发送者
 * @param value  发送的消息
 * @param delay  延迟毫秒数
 */
inline fun <reified T : LiveEvent> T?.postDelay(sender: LifecycleOwner?, delay: Long) {
    event().postDelay(sender, this, delay)
}

/**
 * 进程内发送消息
 * 强制接收到消息的顺序和发送顺序一致
 *
 * @param value 发送的消息
 */
inline fun <reified T : LiveEvent> T?.postOrderly() {
    event().postOrderly(this)
}

/**
 * 以广播的形式发送一个消息
 * 需要跨进程、跨APP发送消息的时候调用该方法
 *
 * @param value      发送的消息
 * @param foreground true:前台广播、false:后台广播
 * @param onlyInApp  true:只在APP内有效、false:全局有效
 */
inline fun <reified T : LiveEvent> T?.broadcast(foreground: Boolean, onlyInApp: Boolean) {
    event().broadcast(this, foreground, onlyInApp)
}

inline fun <reified T : LiveEvent> T?.event(): Observable<T> {
    return LiveEventBus.get(T::class.java.simpleName)

}

/**
 * 注册一个Observer，生命周期感知，自动取消订阅
 *
 * @param owner    LifecycleOwner
 * @param observer 观察者
 */
inline fun <reified T : LiveEvent> Class<T>.observe(owner: LifecycleOwner, observer: Observer<T?>) {
    event().observe(owner, observer)
}

/**
 * 注册一个Observer，生命周期感知，自动取消订阅
 * 如果之前有消息发送，可以在注册时收到消息（消息同步）
 *
 * @param owner    LifecycleOwner
 * @param observer 观察者
 */
inline fun <reified T : LiveEvent> Class<T>.observeSticky(
    owner: LifecycleOwner,
    observer: Observer<T?>
) {
    event().observeSticky(owner, observer)
}

/**
 * 注册一个Observer，需手动解除绑定
 *
 * @param observer 观察者
 */
inline fun <reified T : LiveEvent> Class<T>.observeForever(observer: Observer<T?>) {
    event().observeForever(observer)
}

/**
 * 注册一个Observer，需手动解除绑定
 * 如果之前有消息发送，可以在注册时收到消息（消息同步）
 *
 * @param observer 观察者
 */
inline fun <reified T : LiveEvent> Class<T>.observeStickyForever(observer: Observer<T?>) {
    event().observeStickyForever(observer)
}

/**
 * 通过observeForever或observeStickyForever注册的，需要调用该方法取消订阅
 *
 * @param observer 观察者
 */
inline fun <reified T : LiveEvent> Class<T>.removeObserver(observer: Observer<T?>) {
    event().removeObserver(observer)
}

inline fun <reified T : LiveEvent> Class<T>.event(): Observable<T> {
    return LiveEventBus.get(T::class.java.simpleName)

}