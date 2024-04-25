package com.ez.frameKt.utils

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Base64
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.ez.frameKt.base.BaseApplication
import java.net.URLEncoder
import java.util.regex.Pattern

/**
 * @author : ezhuwx
 * Describe :字符串工具类
 * Designed on 2021/10/28
 * E-mail : ezhuwx@163.com
 * Update on 14:33 by ezhuwx
 */

/**
 * TODO 数字判断
 *
 * @param str
 * @return
 */
fun isNumeric(str: String): Boolean {
    val pattern = Pattern.compile("[0-9]*")
    val isNum = pattern.matcher(str)
    return isNum.matches()
}

/**
 * TODO 判定输入汉字
 *
 * @return
 */
fun isChinese(str: String): Boolean {
    for (element in str) {
        if (isChineseChar(element)) {
            return true
        }
    }
    return false
}

/**
 * 汉字字符判断
 *
 * @return
 */
private fun isChineseChar(c: Char): Boolean {
    val ub = Character.UnicodeBlock.of(c)
    return ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
            || ub === Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
            || ub === Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
            || ub === Character.UnicodeBlock.GENERAL_PUNCTUATION
            || ub === Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
            || ub === Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
}

/**
 * TODO 文字末尾添加红星
 */
fun addRedStar(textView: TextView) {
    val ss = SpannableString(
        textView.text.toString() + "*"
    )
    val colorSpan = ForegroundColorSpan(Color.RED)
    ss.setSpan(colorSpan, ss.length - 1, ss.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
    textView.text = ss
}

/**
 * TODO 金额输入格式
 */
fun decimalInputFormat(editText: EditText, edt: Editable, maxLength: Int) {
    val temp = edt.toString()
    //返回指定字符在此字符串中第一次出现处的索引
    val posDot = temp.indexOf(".")
    //获取光标位置
    val index = editText.selectionStart
    if (posDot < 0) {
        //不包含小数点
        if (temp.length <= maxLength) {
            //小于五位数直接返回
            return
        } else {
            //删除光标前的字符
            edt.delete(index - 1, index)
            return
        }
    } else if (posDot == 0) {
        edt.insert(0, "0")
    }
    //小数点前大于10位数就删除光标前一位
    if (posDot > maxLength) {
        //删除光标前的字符
        edt.delete(index - 1, index)
        return
    }
    //如果包含小数点
    if (temp.length - posDot - 1 > maxLength) {
        //删除光标前的字符
        edt.delete(index - 1, index)
    }
}

/**
 * TODO 汉字过滤器
 */
fun getChineseFilter(): InputFilter {
    return InputFilter { source, start, end, _, _, _ ->
        for (i in start until end) {
            if (isChineseChar(source[i])) {
                return@InputFilter ""
            }
        }
        null
    }
}

fun String?.safeToF(): Float {
    try {
        return (this ?: "0").toFloat()
    } catch (_: Exception) {

    }
    return 0f
}

fun String?.safeToD(): Double {
    try {
        return (this ?: "0").toDouble()
    } catch (_: Exception) {

    }
    return 0.0
}

fun String?.safeToInt(): Int {
    try {
        return (this ?: "0").toInt()
    } catch (_: Exception) {

    }
    return 0
}

fun String?.empty(): String = if (equals("null")) "" else this ?: ""

fun String?.default(): String = if (empty().trim().isEmpty()) "-" else this!!

fun String?.shortShow() {
    ToastUtil().shortShow(empty())
}

fun String?.longShow() {
    ToastUtil().longShow(empty())
}

fun String?.subSafe10() = endSafe(10)

fun String?.fileName() =
    empty().subSafe(empty().lastIndexOf("/"), empty().length)

fun String?.ext() =
    empty().subSafe(empty().lastIndexOf("."), empty().length)

///安全截取
fun String?.subSafe(start: Int? = null, end: Int? = null): String {
    if (start != null && end == null) return startSafe(start)
    if (end != null && start == null) return endSafe(end)
    if (start != null && end != null && start >= 0 && end >= 0) {
        val endNew = end.coerceAtMost(empty().length)
        return if (end > start) empty().substring(start, endNew) else empty()
    }
    return empty()
}

///安全截取
fun String?.startSafe(start: Int): String {
    return if (empty().length > start && start >= 0) empty().substring(start) else empty()
}

///安全截取
fun String?.endSafe(end: Int): String {
    return if (empty().length > end && end > 0) empty().substring(0, end) else empty()
}

fun String?.colorSpan(@ColorInt color: Int, start: Int, end: Int): SpannableString {
    return SpannableString(this).apply {
        setSpan(
            ForegroundColorSpan(color),
            start,
            end,
            android.text.Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
    }
}

fun ByteArray?.string(flags: Int = Base64.DEFAULT): String {
    return if (this == null) "" else Base64.encodeToString(this, flags)
}

fun String?.urlSafe(): String {
    return URLEncoder.encode(empty())
}

fun String?.emptyNull(): String? {
    return if (isNullOrEmpty()) null
    else this
}

fun String?.ifEmptyValue(emptyValue: String?): String? {
    return if (isNullOrEmpty()) emptyValue else this
}

fun String?.ifEmptyValue(value: String?, emptyValue: String?): String? {
    return if (isNullOrEmpty()) emptyValue else value
}

fun @receiver:StringRes Int.resString(context: Context = BaseApplication.instance.applicationContext)
        : String {
    return context.resources.getString(this)
}