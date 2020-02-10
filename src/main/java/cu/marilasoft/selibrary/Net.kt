package cu.marilasoft.selibrary

import org.jsoup.Connection
import org.jsoup.Jsoup
import java.io.IOException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object Net {

    fun connection(
            url: String,
            dataMap: Map<String, String>? = null,
            cookies: Map<String, String>? = null,
            verify: Boolean = true): Connection {
        var connection = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Accept-Encoding", "gzip, deflate, br")
        if (dataMap != null) connection = connection.data(dataMap)
        if (cookies != null) connection = connection.cookies(cookies)
        if (!verify) connection = connection.sslSocketFactory(socketFactory())
        return connection
    }

    private fun socketFactory(): SSLSocketFactory? {
        val trustAllCerts: Array<TrustManager> = arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate?>? {
                return null
            }

            override fun checkClientTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
            override fun checkServerTrusted(certs: Array<X509Certificate?>?, authType: String?) {}
        })
        return try {
            val sslContext: SSLContext = SSLContext.getInstance("TLS")
            sslContext.init(null, trustAllCerts, SecureRandom())
            sslContext.socketFactory
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to create a SSL socket factory", e)
        } catch (e: KeyManagementException) {
            throw RuntimeException("Failed to create a SSL socket factory", e)
        }
    }

    @Throws(IOException::class)
    fun getCookies(url: String): Map<String, String> {
        return connection(url).execute().cookies()
    }

    @Throws(IOException::class)
    fun getCaptcha(url: String, cookies: Map<String, String>): ByteArray {
        return Jsoup.connect(url)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Accept-Encoding", "gzip, deflate, br")
                .ignoreContentType(true).timeout(25000).cookies(cookies)
                .execute().bodyAsBytes()
    }

    @Throws(IOException::class)
    fun getImg(url: String): ByteArray {
        return Jsoup.connect(url)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Accept-Encoding", "gzip, deflate, br")
                .ignoreContentType(true).timeout(25000)
                .execute().bodyAsBytes()
    }
}