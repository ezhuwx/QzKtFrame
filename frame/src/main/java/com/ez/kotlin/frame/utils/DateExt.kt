package com.ez.kotlin.frame.utils

import java.lang.Exception
import java.sql.Timestamp
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs
import kotlin.math.ceil

/**
 * @author : ezhuwx
 * Describe :
 * Designed on 2021/10/28
 * E-mail : ezhuwx@163.com
 * Update on 14:44 by ezhuwx
 */
private const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
private const val YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm"
private const val HH_MM_SS = "HH:mm:ss"
private const val YYYY_MM_DD = "yyyy-MM-dd"
private const val YYYY = "yyyy"
private const val MM = "MM"
private const val DD = "dd"

/**
 *   TODO  根据long毫秒数，获得时分秒
 */
fun Long.hhMmSsColon(): String {
    val totalSeconds = (this / 1000).toInt()
    val seconds = totalSeconds % 60
    val minutes = totalSeconds / 60 % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) String.format(
        Locale.getDefault(),
        "%02d:%02d:%02d",
        hours,
        minutes,
        seconds
    ) else String.format(
        Locale.getDefault(), "%02d:%02d", minutes, seconds
    )
}


fun Long?.yyMmDd(): String {
    return this?.let {
        SimpleDateFormat(
            YYYY_MM_DD, Locale.getDefault()
        ).format(this)
    } ?: "--"
}

fun Long?.hhMmSs(): String {
    return this?.let {
        SimpleDateFormat(
            HH_MM_SS, Locale.getDefault()
        ).format(this)
    } ?: "--"
}

fun String?.fromYyMmDd(): Long {
    return this?.let {
        SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault()
        ).parse(this)?.time ?: System.currentTimeMillis()
    } ?: System.currentTimeMillis()
}
/**
 * TODO  获取时间间隔（天）
 */
fun Long.daysToNow(): String {
    val interval = abs(this - System.currentTimeMillis())
    return ceil((interval.toFloat() / (1000 * 60 * 60 * 24)).toDouble()).toString()
}

/**
 *  TODO Long时间转换为年月日时分秒
 */
fun Long?.yyyyMmDmHhMmSs(): String {
    return this?.let {
        SimpleDateFormat(
            YYYY_MM_DD_HH_MM_SS, Locale.getDefault()
        ).format(this)
    } ?: "-:-:-"
}
/**
 * TODO  格式化日期对象
 */
fun Date.yyyyMmDmHhMmSs(): String {
    val sdf = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.getDefault())
    return sdf.format(this)
}


/**
 * TODO  sql时间对象转换成字符串
 */
fun Timestamp.yyyyMmDmHhMmSs(): String {
    val sdf = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.getDefault())
    return sdf.format(this)
}

/**
 * TODO  字符串转换成时间对象
 */
fun String?.yyyyMmDmHhMmSsToDate(): Date? {
    val format: DateFormat = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.getDefault())
    return this?.let { format.parse(it) }
}

fun String?.yyyyMmDmHhMmSsToLong(): Long? {
    val format: DateFormat = SimpleDateFormat(YYYY_MM_DD_HH_MM_SS, Locale.getDefault())
    return this?.let { format.parse(it) }?.time
}

/**
 * TODO  字符串转换成时间对象
 */
fun stringToLong(year: String, month: String, day: String): Long {
    val dateString = "$year-$month-$day"
    val format: DateFormat = SimpleDateFormat(YYYY_MM_DD, Locale.getDefault())
    return try {
        format.parse(dateString)!!.time
    } catch (e: ParseException) {
        System.currentTimeMillis()
    }
}

/**
 * TODO  字符串转换成时间对象
 */
fun String?.yyyyMmDd(): Long? {
    val format: DateFormat = SimpleDateFormat(YYYY_MM_DD, Locale.getDefault())
    return this?.let { format.parse(it) }?.time
}

/**
 * TODO  Date类型转换为Timestamp类型
 */
fun Date?.timestamp(): Timestamp? {
    return this?.let { Timestamp(this.time) }
}

/**
 *  TODO 获得当前年份
 */
fun getNowYear(): String {
    val sdf = SimpleDateFormat(YYYY, Locale.getDefault())
    return sdf.format(Date())
}

/**
 *  TODO 获得当前月份
 */
fun getNowMonth(): String {
    val sdf = SimpleDateFormat(MM, Locale.getDefault())
    return sdf.format(Date())
}

/**
 *  TODO 获得当前日期
 */
fun getNowDay(): String {
    val sdf = SimpleDateFormat(DD, Locale.getDefault())
    return sdf.format(Date())
}

/**
 *  TODO 获得当前年月日
 */
fun getNowYearMonthDay(): String {
    val sdf = SimpleDateFormat(YYYY_MM_DD, Locale.getDefault())
    return sdf.format(Date())
}

/**
 *  TODO Long时间转换为年月日
 */
fun Long?.getNowYearMonthDay(): String? {
    val sdf = SimpleDateFormat(YYYY_MM_DD, Locale.getDefault())
    return this?.let { sdf.format(Date(it)) }
}


/**
 * TODO 天数
 */
fun Long.days(): Long {
    return this / (24 * 60 * 60 * 1000L)
}

/**
 * TODO 十天内
 */
fun getTenStartTime(): Long {
    val c = Calendar.getInstance()
    //本周
    c.time = Date()
    c.add(Calendar.DATE, -9)
    val d = c.time
    return d.time
}

/**
 * TODO 15天内
 */
fun getFifteenStartTime(): Long {
    val c = Calendar.getInstance()
    c.time = Date()
    c.add(Calendar.DATE, -14)
    val d = c.time
    return d.time
}

/**
 * TODO 三十天内
 */
fun getThirtyStartTime(): Long {
    val c = Calendar.getInstance()
    c.time = Date()
    c.add(Calendar.DATE, -29)
    val d = c.time
    return d.time
}

/**
 * TODO 近一年
 */
fun getNearYearStartTime(): Calendar? {
    val c = Calendar.getInstance()
    c.time = Date()
    c.add(Calendar.DATE, -365)
    val d = c.time
    return c
}

/**
 * TODO 半月
 */
fun getHalfMonthStartTime(): Long {
    val ca = Calendar.getInstance()
    ca[Calendar.HOUR_OF_DAY] = 0
    ca.clear(Calendar.MINUTE)
    ca.clear(Calendar.SECOND)
    ca.clear(Calendar.MILLISECOND)
    ca[Calendar.DAY_OF_MONTH] = if (ca[Calendar.DAY_OF_MONTH] > 15) 16 else 1
    return ca.timeInMillis
}

/**
 * TODO 本日
 */
fun getFirstTimeOfDay(): Long {
    val ca = Calendar.getInstance()
    ca[Calendar.HOUR_OF_DAY] = 0
    ca.clear(Calendar.MINUTE)
    ca.clear(Calendar.SECOND)
    ca.clear(Calendar.MILLISECOND)
    return ca.timeInMillis
}

/**
 * TODO 本日截至时间
 */
fun getEndTimeOfDay(): Long {
    val ca = Calendar.getInstance()
    ca.add(Calendar.DATE, 1)
    ca[Calendar.HOUR_OF_DAY] = 0
    ca.clear(Calendar.MINUTE)
    ca.clear(Calendar.SECOND)
    ca.clear(Calendar.MILLISECOND)
    return ca.timeInMillis - 1
}

/**
 * TODO 时间范围内首天
 */
fun Long.rangeStart(): Calendar? {
    val ca = Calendar.getInstance()
    ca.add(Calendar.DATE, 1)
    ca[Calendar.HOUR_OF_DAY] = 0
    ca.clear(Calendar.MINUTE)
    ca.clear(Calendar.SECOND)
    ca.clear(Calendar.MILLISECOND)
    ca.time = Date(ca.timeInMillis - this)
    return ca
}

/**
 * TODO 时间
 */
fun Long.calendarOfTime(): Calendar? {
    val ca = Calendar.getInstance()
    ca.time = Date(this)
    return ca
}

/**
 *TODO  获取本周时间
 */
fun getTimeOfWeekRange(): Long {
    return 7 * 24 * 60 * 60 * 1000L
}

/**
 * TODO 本周
 */
fun getTimeOfWeekStart(): Long {
    val ca = Calendar.getInstance()
    ca[Calendar.HOUR_OF_DAY] = 0
    ca.clear(Calendar.MINUTE)
    ca.clear(Calendar.SECOND)
    ca.clear(Calendar.MILLISECOND)
    ca.firstDayOfWeek = Calendar.MONDAY
    ca[Calendar.DAY_OF_WEEK] = ca.firstDayOfWeek
    return ca.timeInMillis
}

/**
 *TODO  获取本月时间
 */
fun getTimeOfMonthRange(): Long {
    val ca = Calendar.getInstance()
    return ca.getActualMaximum(Calendar.DAY_OF_MONTH) * 24 * 60 * 60 * 1000L
}

/**
 * TODO 本月
 */
fun getTimeOfMonthStart(): Long {
    val ca = Calendar.getInstance()
    ca[Calendar.HOUR_OF_DAY] = 0
    ca.clear(Calendar.MINUTE)
    ca.clear(Calendar.SECOND)
    ca.clear(Calendar.MILLISECOND)
    ca[Calendar.DAY_OF_MONTH] = 1
    return ca.timeInMillis
}

/**
 * TODO 获取本年时间
 */
fun getTimeOfYearRange(): Long {
    val ca = Calendar.getInstance()
    return ca.getActualMaximum(Calendar.DAY_OF_YEAR) * 24 * 60 * 60 * 1000L
}

/**
 * TODO 本年
 */
fun getTimeOfYearStart(): Long {
    val ca = Calendar.getInstance()
    ca[Calendar.HOUR_OF_DAY] = 0
    ca.clear(Calendar.MINUTE)
    ca.clear(Calendar.SECOND)
    ca.clear(Calendar.MILLISECOND)
    ca[Calendar.DAY_OF_YEAR] = 1
    return ca.timeInMillis
}

/**
 * TODO 获取本季时间
 */
fun getTimeOfSeasonRange(): Long {
    val c = Calendar.getInstance()
    val month = c[Calendar.MONTH] + 1
    var days = 0
    when {
        month <= 3 -> {
            c[Calendar.MONTH] = 0
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
            c[Calendar.MONTH] = 1
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
            c[Calendar.MONTH] = 2
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        month <= 6 -> {
            c[Calendar.MONTH] = 3
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
            c[Calendar.MONTH] = 4
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
            c[Calendar.MONTH] = 5
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        month <= 9 -> {
            c[Calendar.MONTH] = 6
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
            c[Calendar.MONTH] = 7
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
            c[Calendar.MONTH] = 8
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        else -> {
            c[Calendar.MONTH] = 9
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
            c[Calendar.MONTH] = 10
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
            c[Calendar.MONTH] = 11
            days += c.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
    }
    return days * 24 * 60 * 60 * 1000L
}

/**
 *TODO  本季
 */
fun getTimeOfSeasonStart(): Long {
    return getCurrQuarter(getQuarter())
}


/**
 * TODO 获取当前季度
 */
private fun getQuarter(): Int {
    val c = Calendar.getInstance()
    val month = c[Calendar.MONTH] + 1
    return when {
        month <= 3 -> {
            1
        }
        month <= 6 -> {
            2
        }
        month <= 9 -> {
            3
        }
        else -> {
            4
        }
    }
}

/**
 *TODO  获取某季度的第一天
 *
 * @param num 第几季度
 */
fun getCurrQuarter(num: Int): Long {
    // 设置本年的季
    val quarterCalendar = Calendar.getInstance()
    when (num) {
        1 -> {
            quarterCalendar[quarterCalendar[Calendar.YEAR], 0] = 1
            return quarterCalendar.timeInMillis
        }
        2 -> {
            quarterCalendar[quarterCalendar[Calendar.YEAR], 3] = 1
            return quarterCalendar.timeInMillis
        }
        3 -> {
            quarterCalendar[quarterCalendar[Calendar.YEAR], 6] = 1
            return quarterCalendar.timeInMillis
        }
        4 -> {
            quarterCalendar[quarterCalendar[Calendar.YEAR], 9] = 1
            return quarterCalendar.timeInMillis
        }
        else -> {
        }
    }
    return System.currentTimeMillis()
}

/**
 * TODO 根据起始时间和时间范围获取截止时间
 */
fun getRangeTime(time: Long, range: Long): Calendar? {
    val ca = Calendar.getInstance()
    ca.time = Date(time + range)
    return ca
}

/**
 *TODO 以指定的格式格式化日期字符串
 *
 * @param pattern     字符串的格式
 * @param currentDate 被格式化日期
 * @return String 已格式化的日期字符串
 * @throws NullPointerException 如果参数为空
 */
fun formatDate(currentDate: Date?, pattern: String?): String? {
    if (currentDate == null || "" == pattern || pattern == null) {
        return null
    }
    val sdf = SimpleDateFormat(pattern, Locale.getDefault())
    return sdf.format(currentDate)
}

/**
 * TODO 获取-农历年月日
 */
fun getLunarYearMonthDay(SY: Int, SM: Int, SD: Int): String {
    TimeZone.setDefault(TimeZone.getTimeZone("GMT+08"))
    val sDObj: Date
    val sy = (SY - 4) % 12
    val cl = Calendar.getInstance()
    cl[SY, SM - 1] = SD
    sDObj = cl.time
    //日期
    lunar(sDObj) //农历
    val lMDBuffer = StringBuffer()
    lMDBuffer.append("农历")
    lMDBuffer.append(cyclical(getYearCyl()))
    lMDBuffer.append("(")
    lMDBuffer.append(Animals[sy])
    lMDBuffer.append(")年")
    lMDBuffer.append(monthNong[getMonth()])
    lMDBuffer.append("月")
    lMDBuffer.append(cDay(getDay()))
    return lMDBuffer.toString()
}

//农历（阴历）年，月，日
private var yearCyl = 0
private var monCyl = 0
private var dayCyl = 0

//公历（阳历）年，月，日
private var year = 0
private var month = 0
private var day = 0
private var isLeap = false
private val lunarInfo = intArrayOf(
    0x04bd8, 0x04ae0, 0x0a570, 0x054d5,
    0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0, 0x055d2, 0x04ae0,
    0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540, 0x0d6a0, 0x0ada2,
    0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5, 0x06a50, 0x06d40,
    0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970, 0x06566, 0x0d4a0,
    0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3, 0x092e0, 0x1c8d7,
    0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0, 0x1a5b4, 0x025d0,
    0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0, 0x0b550, 0x15355,
    0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8, 0x0e950, 0x06aa0,
    0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570, 0x05260, 0x0f263,
    0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5, 0x04ad0, 0x0a4d0,
    0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0, 0x195a6, 0x095b0,
    0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50, 0x06d40, 0x0af46,
    0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0, 0x074a3, 0x0ea50,
    0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0, 0x0c960, 0x0d954,
    0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7, 0x025d0, 0x092d0,
    0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50, 0x055d9, 0x04ba0,
    0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954, 0x06aa0, 0x0ad50,
    0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260, 0x0ea65, 0x0d530,
    0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0, 0x0a4d0, 0x1d0b6,
    0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0, 0x055b2, 0x049b0,
    0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20, 0x0ada0
)
private val Gan = arrayOf(
    "甲", "乙", "丙", "丁", "戊", "己", "庚", "辛",
    "壬", "癸"
)
private val Zhi = arrayOf(
    "子", "丑", "寅", "卯", "辰", "巳", "午", "未",
    "申", "酉", "戌", "亥"
)
private val Animals = arrayOf(
    "鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊",
    "猴", "鸡", "狗", "猪"
)
private val sTermInfo = intArrayOf(
    0, 21208, 42467, 63836, 85337, 107014,
    128867, 150921, 173149, 195551, 218072, 240693, 263343, 285989,
    308563, 331033, 353350, 375494, 397447, 419210, 440795, 462224,
    483532, 504758
)
private val nStr1 = arrayOf(
    "日", "一", "二", "三", "四", "五", "六", "七",
    "八", "九", "十"
)
private val nStr2 = arrayOf("初", "十", "廿", "卅", "　")
private val monthNong = arrayOf(
    "", "正", "二", "三", "四", "五", "六", "七",
    "八", "九", "十", "冬", "腊"
)

/**
 * 传回农历 y年的总天数
 *
 * @param y
 * @return
 */
private fun lYearDays(y: Int): Int {
    var i: Int
    var sum = 348 //29*12
    i = 0x8000
    while (i > 0x8) {
        sum += if (lunarInfo[y - 1900] and i == 0) 0 else 1 //大月+1天
        i = i shr 1
    }
    return sum + leapDays(y) //+闰月的天数
}

/**
 * 传回农历 y年闰月的天数
 *
 * @param y
 * @return
 */
private fun leapDays(y: Int): Int {
    return if (leapMonth(y) != 0) {
        if (lunarInfo[y - 1900] and 0x10000 == 0) 29 else 30
    } else {
        0
    }
}

/**
 * 传回农历 y年闰哪个月 1-12 , 没闰传回 0
 *
 * @param y
 * @return
 */
private fun leapMonth(y: Int): Int {
    return lunarInfo[y - 1900] and 0xf
}

/**
 * 传回农历 y年m月的总天数
 *
 * @param y
 * @param m
 * @return
 */
private fun monthDays(y: Int, m: Int): Int {
    return if (lunarInfo[y - 1900] and (0x10000 shr m) == 0) 29 else 30
}

/**
 * 算出农历, 传入日期物件, 传回农历日期物件
 * 该物件属性有 .year .month .day .isLeap .yearCyl .dayCyl .monCyl
 *
 * @param objDate
 */
private fun lunar(objDate: Date) {
    val leap: Int
    var temp = 0
    val cl = Calendar.getInstance()
    cl[1900, 0] = 31 //1900-01-31是农历1900年正月初一
    val baseDate = cl.time
    //1900-01-31是农历1900年正月初一
    var offset =
        ((objDate.time - baseDate.time) / 86400000).toInt() //天数(86400000=24*60*60*1000)
    //1899-12-21是农历1899年腊月甲子日
    dayCyl = offset + 40
    //1898-10-01是农历甲子月
    monCyl = 14
    //得到年数
    var i = 1900
    while (i < 2050 && offset > 0) {
        //农历每年天数
        temp = lYearDays(i)
        offset -= temp
        monCyl += 12
        i++
    }
    if (offset < 0) {
        offset += temp
        i--
        monCyl -= 12
    }
    year = i //农历年份
    yearCyl = i - 1864 //1864年是甲子年
    leap = leapMonth(i) //闰哪个月
    isLeap = false
    i = 1
    while (i < 13 && offset > 0) {
        //闰月
        if (leap > 0 && i == leap + 1 && !isLeap) {
            --i
            isLeap = true
            temp = leapDays(year)
        } else {
            temp = monthDays(year, i)
        }
        //解除闰月
        if (isLeap && i == leap + 1) {
            isLeap = false
        }
        offset -= temp
        if (!isLeap) {
            monCyl++
        }
        i++
    }
    if (offset == 0 && leap > 0 && i == leap + 1) {
        if (isLeap) {
            isLeap = false
        } else {
            isLeap = true
            --i
            --monCyl
        }
    }
    if (offset < 0) {
        offset += temp
        --i
        --monCyl
    }
    month = i //农历月份
    day = offset + 1 //农历天份
}

/**
 * 传入 offset 传回干支, 0=甲子
 *
 * @param num
 * @return
 */
private fun cyclical(num: Int): String {
    return Gan[num % 10] + Zhi[num % 12]
}

/**
 * 中文日期
 *
 * @param d
 * @return
 */
private fun cDay(d: Int): String {
    var s: String
    when (d) {
        10 -> s = "初十"
        20 -> s = "二十"
        30 -> s = "三十"
        else -> {
            s = nStr2[(d / 10)] //取商
            s += nStr1[d % 10] //取余
        }
    }
    return s
}


private fun getMonth(): Int {
    return month
}

private fun getDay(): Int {
    return day
}

private fun getYearCyl(): Int {
    return yearCyl
}

