package com.ez.kotlin.frame.utils

import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.LiveEvent
import com.jeremyliao.liveeventbus.core.Observable

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2023/12/11
 * E-mail : ezhuwx@163.com
 * Update on 19:12 by ezhuwx
 */
inline fun <reified T> T?.post() {
    LiveEventBus.get<T>(T::class.java.simpleName).post(this)
}
inline fun <reified T> T?.get(): Observable<T>? {
   return LiveEventBus.get<T>(T::class.java.simpleName)
}