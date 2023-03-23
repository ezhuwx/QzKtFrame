package com.ez.kotlin.frame.utils;

import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * @author : ezhuwx
 * Describe :连点工具类
 * Designed on 2021/10/25
 * E-mail : ezhuwx@163.com
 * Update on 15:02 by ezhuwx
 */
const val INTERNAL_TIME: Long = 1000

/**
 * Whether this click event is invalid.
 *
 * @param view target view
 * @return true, invalid click event.
 * @see .isInvalidClick
 */
fun isInvalidClick(view: View): Boolean {
    return isInvalidClick(view, INTERNAL_TIME)
}

/**
 * Whether this click event is invalid.
 *
 * @param view         target view
 * @param internalTime the internal time. The unit is millisecond.
 * @return true, invalid click event.
 */
fun isInvalidClick(view: View, @IntRange(from = 0) internalTime: Long): Boolean {
    val lastClickTimeStamp: Long
    val o = view.getTag(view.id)
    if (o == null) {
        view.setTag(view.id, System.currentTimeMillis())
        return false
    }
    lastClickTimeStamp = o as Long
    val currentTime = System.currentTimeMillis() - lastClickTimeStamp
    val isInvalid = currentTime < internalTime
    if (!isInvalid) {
        view.setTag(view.id, System.currentTimeMillis())
    }
    return isInvalid
}
