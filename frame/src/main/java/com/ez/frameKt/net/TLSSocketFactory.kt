package com.ez.frameKt.net

import android.annotation.SuppressLint
import android.os.Build
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

class TLSSocketFactory(private val delegate: SSLSocketFactory) : SSLSocketFactory() {
    override fun getDefaultCipherSuites(): Array<String> {
        return delegate.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return delegate.supportedCipherSuites
    }

    @Throws(IOException::class)
    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket {
        val ssl = delegate.createSocket(s, host, port, autoClose)
        setSupportProtocolAndCipherSuites(ssl)
        return ssl
    }

    @Throws(IOException::class)
    override fun createSocket(host: String, port: Int): Socket {
        val ssl = delegate.createSocket(host, port)
        setSupportProtocolAndCipherSuites(ssl)
        return ssl
    }

    @Throws(IOException::class)
    override fun createSocket(
        host: String,
        port: Int,
        localHost: InetAddress,
        localPort: Int
    ): Socket {
        val ssl = delegate.createSocket(host, port, localHost, localPort)
        setSupportProtocolAndCipherSuites(ssl)
        return ssl
    }

    @Throws(IOException::class)
    override fun createSocket(host: InetAddress, port: Int): Socket {
        val ssl = delegate.createSocket(host, port)
        setSupportProtocolAndCipherSuites(ssl)
        return ssl
    }

    @Throws(IOException::class)
    override fun createSocket(
        address: InetAddress,
        port: Int,
        localAddress: InetAddress,
        localPort: Int
    ): Socket {
        val ssl = delegate.createSocket(address, port, localAddress, localPort)
        setSupportProtocolAndCipherSuites(ssl)
        return ssl
    }

    @Throws(IOException::class)
    override fun createSocket(): Socket {
        val ssl = delegate.createSocket()
        setSupportProtocolAndCipherSuites(ssl)
        return ssl
    }

    class TrustAllHostnameVerifier : HostnameVerifier {
        @SuppressLint("BadHostnameVerifier")
        override fun verify(hostname: String, session: SSLSession): Boolean {
            return true
        }
    }

    companion object {
        private val PROTOCOL_ARRAY: Array<String> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                arrayOf("TLSv1", "TLSv1.1", "TLSv1.2")
            } else {
                arrayOf("SSLv3", "TLSv1", "TLSv1.1", "TLSv1.2")
            }

        private fun setSupportProtocolAndCipherSuites(socket: Socket) {
            if (socket is SSLSocket) {
                socket.enabledProtocols = PROTOCOL_ARRAY
            }
        }
    }
}