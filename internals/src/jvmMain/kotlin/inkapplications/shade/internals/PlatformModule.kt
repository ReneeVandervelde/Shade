package inkapplications.shade.internals

import inkapplications.shade.structures.HueConfigurationContainer
import inkapplications.shade.structures.SecurityStrategy
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import io.ktor.util.network.*
import kimchi.logger.KimchiLogger
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.internal.peerName
import okhttp3.internal.platform.Platform
import okhttp3.tls.HandshakeCertificates
import okhttp3.tls.decodeCertificatePem
import java.io.IOException
import java.net.InetAddress
import java.net.Socket
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.*


actual class PlatformModule actual constructor(
    private val configurationContainer: HueConfigurationContainer,
    private val json: Json,
    private val logger: KimchiLogger
) {
    val sseClient by CachedProperty(configurationContainer.securityStrategy::value) { key ->
        createSseClient(key)
    }

    actual val httpClient by CachedProperty(configurationContainer.securityStrategy::value) { key ->
        createHttpClient(key)
    }

    private fun createHttpClient(securityStrategy: SecurityStrategy): HueHttpClient {
        return OkHttpHueClient(configurationContainer, OkHttpClient.Builder().apply {
            OkHttpDebugLogging(logger).apply {
                enableHttp2()
                enableTaskRunner()
            }
            addInterceptor(SSLHandshakeInterceptor(logger))
            applyPlatformConfiguration(this, securityStrategy)
        }.build(), json, logger)
    }

    actual fun createEngine(securityStrategy: SecurityStrategy): HttpClientEngineFactory<*> {
        return object: HttpClientEngineFactory<OkHttpConfig> {
            override fun create(block: OkHttpConfig.() -> Unit): HttpClientEngine = OkHttpEngine(OkHttpConfig().apply {
                config { applyPlatformConfiguration(this, securityStrategy) }
            }.apply(block))
        }
    }

    fun applyPlatformConfiguration(builder: OkHttpClient.Builder, securityStrategy: SecurityStrategy) = builder.apply {
        when (securityStrategy) {
            is SecurityStrategy.Insecure -> insecure(securityStrategy)
            is SecurityStrategy.CustomCa -> withSecurity(securityStrategy)
            is SecurityStrategy.PlatformTrust -> {}
            else -> throw IllegalArgumentException("Unsupported Security Scheme: ${securityStrategy::class.simpleName}")
        }
    }

    private fun OkHttpClient.Builder.insecure(strategy: SecurityStrategy.Insecure) {
        logger.debug("Creating Insecure Client")
        val certificates = HandshakeCertificates.Builder()
            .addInsecureHost(strategy.hostname)
            .addPlatformTrustedCertificates()
            .build()
        hostnameVerifier { hostname, session -> hostname == strategy.hostname.also { logger.debug("Verifying $hostname is ${strategy.hostname}") } }

        logger.debug("Creating Socket Factory")
        val defaultTrust = certificates.trustManager as X509ExtendedTrustManager
        logger.debug("Default is: ${defaultTrust::class.java.simpleName}")
        val dummyTrust = object: X509ExtendedTrustManager() {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?, socket: Socket?) {
                defaultTrust.checkClientTrusted(chain, authType, socket)
            }

            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?, engine: SSLEngine?) {
                defaultTrust.checkClientTrusted(chain, authType, engine)
            }

            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                defaultTrust.checkClientTrusted(chain, authType)
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?, socket: Socket?) {
                logger.debug("Huh?")
                if (socket?.remoteSocketAddress?.address != strategy.hostname) {
                    logger.debug("Hmmmmmmmmmmmmmmmmmmmmmm")
                    defaultTrust.checkServerTrusted(chain, authType, socket)
                }
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?, engine: SSLEngine?) {
                defaultTrust.checkServerTrusted(chain, authType, engine)
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                defaultTrust.checkServerTrusted(chain, authType)
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return defaultTrust.acceptedIssuers
            }
        }
        val delegateSocketFactory = certificates.sslSocketFactory()
        val betterDelegate = Platform.get().newSSLContext().apply {
            init(arrayOf<KeyManager>(certificates.keyManager), arrayOf<TrustManager>(dummyTrust), SecureRandom())
        }.socketFactory
        sslSocketFactory(object: SSLSocketFactory() {
            override fun createSocket(s: Socket?, host: String?, port: Int, autoClose: Boolean): Socket {
                logger.debug("Creating Socket")
                return betterDelegate.createSocket(s, host, port, autoClose).also {
                    logger.debug("Socket Created5")
                }
            }

            override fun createSocket(host: String?, port: Int): Socket {
                logger.debug("Creating Socket")
                return delegateSocketFactory.createSocket(host, port).also {
                    logger.debug("Socket Created4")
                }
            }

            override fun createSocket(host: String?, port: Int, localHost: InetAddress?, localPort: Int): Socket {
                logger.debug("Creating Socket")
                return delegateSocketFactory.createSocket(host, port, localHost, localPort).also {
                    logger.debug("Socket Created3")
                }
            }

            override fun createSocket(host: InetAddress?, port: Int): Socket {
                logger.debug("Creating Socket")
                return delegateSocketFactory.createSocket(host, port).also {
                    logger.debug("Socket Created2")
                }
            }

            override fun createSocket(
                address: InetAddress?,
                port: Int,
                localAddress: InetAddress?,
                localPort: Int
            ): Socket {
                logger.debug("Creating Socket")
                return delegateSocketFactory.createSocket(address, port, localAddress, localPort).also {
                    logger.debug("Socket Created1")
                }
            }

            override fun getDefaultCipherSuites(): Array<String> {
                logger.debug("Getting Default Cipher Suites")
                return delegateSocketFactory.defaultCipherSuites
            }

            override fun getSupportedCipherSuites(): Array<String> {
                logger.debug("Getting Supported Cipher Suites")
                return delegateSocketFactory.supportedCipherSuites
            }

        }, dummyTrust).also {
            logger.debug("Socket Factory Created")
        }
        followSslRedirects(false)
    }

    private fun OkHttpClient.Builder.withSecurity(strategy: SecurityStrategy.CustomCa) {
        val certificates = HandshakeCertificates.Builder()
            .addTrustedCertificate(strategy.certificatePem.decodeCertificatePem())
            .build()
        sslSocketFactory(certificates.sslSocketFactory(), certificates.trustManager)
        dns(object: Dns {
            override fun lookup(hostname: String): List<InetAddress> {
                logger.debug("Looking up $hostname")
                if (hostname == strategy.hostname) {
                    return listOf(InetAddress.getByName(strategy.ip)).also {
                        logger.debug("Resolved $hostname to ${strategy.ip}")
                    }
                }
                return emptyList<InetAddress>().also {
                    logger.debug("Could not resolve $hostname")
                }
            }
        })
        hostnameVerifier { hostname, session -> hostname == strategy.hostname }
    }

    private fun createSseClient(securityStrategy: SecurityStrategy): SseClient {
        val okHttpClient = OkHttpClient.Builder().apply {
            applyPlatformConfiguration(this, securityStrategy)
            readTimeout(0, TimeUnit.SECONDS)
        }.build()
        return OkHttpSseClient(configurationContainer, json, okHttpClient, logger)
    }
}

/** Prints TLS Version and Cipher Suite for SSL Calls through OkHttp3  */
class SSLHandshakeInterceptor(private val logger: KimchiLogger) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        printTlsAndCipherSuiteInfo(response)
        return response
    }

    private fun printTlsAndCipherSuiteInfo(response: Response?) {
        if (response != null) {
            val handshake = response.handshake
            if (handshake != null) {
                val cipherSuite = handshake.cipherSuite
                val tlsVersion = handshake.tlsVersion
                logger.debug("TLS: $tlsVersion, CipherSuite: $cipherSuite")
            }
        }
    }
}
