package com.qz.frame.utils

import java.lang.NullPointerException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.math.max

/**
 * @author : ezhuwx
 * Describe : 加密工具 Kotlin版 Apache Shiro SimpleHash
 * Designed on 2021/11/18
 * E-mail : ezhuwx@163.com
 * Update on 15:14 by ezhuwx
 */

class SimpleHashUtils(
    var algorithmName: String,
    source: String,
    var salt: String?,
    iterations: Int
) {
    companion object ALGORITHM {
        /**
         * 算法名称
         */
        const val MD5 = "MD5"
        const val SHA1 = "SHA-1"
        const val SHA224 = "SHA-224"
        const val SHA256 = "SHA-256"
        const val SHA384 = "SHA-384"
        const val SHA512 = "SHA-512"

        /**
         * 默认迭代次数
         *
         * */
        private const val DEFAULT_ITERATIONS = 1
    }

    lateinit var hashBytes: ByteArray
    var iterations = 0
        set(value) {
            field = max(DEFAULT_ITERATIONS, value)
        }

    constructor(algorithmName: String, source: String) : this(
        algorithmName,
        source,
        null as String?,
        DEFAULT_ITERATIONS
    )

    constructor(algorithmName: String, source: String, salt: String?) : this(
        algorithmName,
        source,
        salt,
        DEFAULT_ITERATIONS
    )

    init {
        if (algorithmName.isEmpty()) {
            throw NullPointerException("algorithmName argument cannot be null or empty.")
        } else {
            this.iterations = iterations
            var saltBytes: ByteArray? = null
            salt?.let {
                saltBytes = salt?.toByteArray()
            }
            val sourceBytes = source.toByteArray()
            hashSet(sourceBytes, saltBytes, iterations)
        }
    }


    @Throws(NoSuchAlgorithmException::class)
    fun hashSet(source: ByteArray, salt: ByteArray?, hashIterations: Int) {
        hashBytes = this.hash(source, salt, hashIterations)
    }


    fun setHashIterations(iterations: Int) {
        this.iterations = max(DEFAULT_ITERATIONS, iterations)
    }


    @Throws(NoSuchAlgorithmException::class)
    fun getDigest(algorithmName: String): MessageDigest {
        return MessageDigest.getInstance(algorithmName)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun hash(bytes: ByteArray): ByteArray {
        return this.hash(bytes, null, 1)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun hash(bytes: ByteArray, salt: ByteArray?): ByteArray {
        return this.hash(bytes, salt, 1)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun hash(bytes: ByteArray, salt: ByteArray?, hashIterations: Int): ByteArray {
        val digest = getDigest(algorithmName)
        if (salt != null) {
            digest.reset()
            digest.update(salt)
        }
        var hashed = digest.digest(bytes)
        val iterations = hashIterations - 1
        for (i in 0 until iterations) {
            digest.reset()
            hashed = digest.digest(hashed)
        }
        return hashed
    }

    fun isEmpty(): Boolean {
        return hashBytes.isEmpty()
    }

    fun toHex(): String {
        return Hex.encodeToString(hashBytes)
    }
}