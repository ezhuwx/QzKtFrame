package com.ez.kotlin.frame.utils

import java.lang.NullPointerException
import java.security.MessageDigest
import java.security.MessageDigestSpi
import java.security.NoSuchAlgorithmException
import kotlin.math.max

/**
 * @author : ezhuwx
 * Describe : 加密工具 Kotlin版 Apache Shiro SimpleHash
 * Designed on 2021/11/18
 * E-mail : ezhuwx@163.com
 * Update on 15:14 by ezhuwx
 */
class SimpleHashUtil {
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

    private var algorithmName: String = ""
    private var bytes = ByteArray(0)
    private var salt: ByteArray? = null
    private var iterations = 0


    @Throws(NoSuchAlgorithmException::class)
    constructor(algorithmName: String, source: String) {
        SimpleHashUtil(algorithmName, source, null as String?, DEFAULT_ITERATIONS)
    }

    @Throws(NoSuchAlgorithmException::class)
    constructor(algorithmName: String, source: String, salt: String?) {
        SimpleHashUtil(algorithmName, source, salt, DEFAULT_ITERATIONS)
    }

    @Throws(NoSuchAlgorithmException::class)
    constructor(algorithmName: String, source: String, salt: String?, hashIterations: Int) {
        if (algorithmName.isEmpty()) {
            throw NullPointerException("algorithmName argument cannot be null or empty.")
        } else {
            this.algorithmName = algorithmName
            iterations = max(DEFAULT_ITERATIONS, hashIterations)
            var saltBytes: ByteArray? = null
            if (salt != null) {
                saltBytes = salt.toByteArray()
                this.salt = saltBytes
            }
            val sourceBytes = source.toByteArray()
            hashSet(sourceBytes, saltBytes, hashIterations)
        }
    }


    @Throws(NoSuchAlgorithmException::class)
    private fun hashSet(source: ByteArray, salt: ByteArray?, hashIterations: Int) {
        val hashedBytes = this.hash(source, salt, hashIterations)
        setBytes(hashedBytes)
    }

    fun getAlgorithmName(): String {
        return algorithmName
    }

    fun getSalt(): ByteArray? {
        return salt
    }

    fun getIterations(): Int {
        return iterations
    }

    fun getBytes(): ByteArray {
        return bytes
    }

    fun setBytes(alreadyHashedBytes: ByteArray) {
        bytes = alreadyHashedBytes
    }

    fun setIterations(iterations: Int) {
        this.iterations = max(DEFAULT_ITERATIONS, iterations)
    }

    fun setSalt(salt: ByteArray) {
        this.salt = salt
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun getDigest(algorithmName: String): MessageDigest {
        return MessageDigest.getInstance(algorithmName)
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun hash(bytes: ByteArray): ByteArray {
        return this.hash(bytes, null, 1)
    }

    @Throws(NoSuchAlgorithmException::class)
    fun hash(bytes: ByteArray, salt: ByteArray?): ByteArray {
        return this.hash(bytes, salt, 1)
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun hash(bytes: ByteArray, salt: ByteArray?, hashIterations: Int): ByteArray {
        val digest = getDigest(getAlgorithmName())
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
        return bytes.isEmpty()
    }

    fun toHex(): String {
        return Hex.encodeToString(bytes)
    }
}