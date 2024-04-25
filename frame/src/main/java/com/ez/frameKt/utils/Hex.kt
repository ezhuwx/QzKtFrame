package com.ez.frameKt.utils

import android.util.Log
import java.io.UnsupportedEncodingException
import java.lang.IllegalArgumentException

/**
 * @author : ezhuwx
 * Describe :16进制转换工具
 * Designed on 2021/11/18
 * E-mail : ezhuwx@163.com
 * Update on 15:22 by ezhuwx
 */
object Hex {
    private val DIGITS = charArrayOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'a', 'b', 'c', 'd', 'e', 'f'
    )

    fun encodeToString(bytes: ByteArray): String {
        val encodedChars = encode(bytes)
        return String(encodedChars)
    }

    fun encode(data: ByteArray): CharArray {
        val l = data.size
        val out = CharArray(l shl 1)
        var i = 0
        var var4 = 0
        while (i < l) {
            out[var4++] = DIGITS[240 and data[i].toInt() ushr 4]
            out[var4++] = DIGITS[15 and data[i].toInt()]
            ++i
        }
        return out
    }

    @Throws(IllegalArgumentException::class)
    fun decode(array: ByteArray): ByteArray {
        val s = toString(array)
        return decode(s)
    }

    fun decode(hex: String?): ByteArray {
        return decode(hex!!.toCharArray())
    }

    @Throws(IllegalArgumentException::class)
    fun decode(data: CharArray): ByteArray {
        val len = data.size
        return if (len and 1 != 0) {
            throw IllegalArgumentException("Odd number of characters.")
        } else {
            val out = ByteArray(len shr 1)
            var i = 0
            var j = 0
            while (j < len) {
                var f = toDigit(data[j], j) shl 4
                ++j
                f = f or toDigit(data[j], j)
                ++j
                out[i] = (f and 255).toByte()
                ++i
            }
            out
        }
    }

    @Throws(IllegalArgumentException::class)
    private fun toDigit(ch: Char, index: Int): Int {
        val digit = Character.digit(ch, 16)
        return if (digit == -1) {
            throw IllegalArgumentException("Illegal hexadecimal character $ch at index $index")
        } else {
            digit
        }
    }

    fun toString(bytes: ByteArray): String? {
        return toString(bytes, "UTF-8")
    }

    fun toString(bytes: ByteArray, encoding: String): String? {
        return try {
            String(bytes, charset(encoding))
        } catch (var4: UnsupportedEncodingException) {
            val msg =
                "Unable to convert byte array to String with encoding '$encoding'."
            Log.e("Hex", msg)
            null
        }
    }
}