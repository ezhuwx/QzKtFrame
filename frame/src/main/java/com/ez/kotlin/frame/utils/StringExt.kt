package com.ez.kotlin.frame.utils

import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.widget.EditText
import android.widget.TextView
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
