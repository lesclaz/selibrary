package cu.marilasoft.selibrary;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class Net {

    private static Connection connection(String url) {

        return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Accept-Encoding", "gzip, deflate, br");
    }

    /**
     * Connection connection
     *
     * @param url    string url of the server to connect
     * @param verify if verify SSL Cert
     * @return a Jsoup connection
     */
    static Connection connection(String url, boolean verify) {
        if (verify) {
            return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .sslSocketFactory(socketFactory());
        } else {
            return connection(url);
        }
    }

    /**
     * Connection connection
     *
     * @param url string url of the server to connect
     * @param cookies cookies of session
     * @return a Jsoup connection
     */
    static Connection connection(String url, Map<String, String> cookies) {

        return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Accept-Encoding", "gzip, deflate, br")
                .cookies(cookies);
    }

    /**
     * Connection connection
     *
     * @param url     string url of the server to connect
     * @param cookies cookies of session
     * @param verify  if verify SSL Cert
     * @return a Jsoup connection
     */
    public static Connection connection(String url, Map<String, String> cookies, boolean verify) {
        if (!verify) {
            return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .cookies(cookies).sslSocketFactory(socketFactory());
        } else {
            return connection(url, cookies);
        }
    }

    /**
     * Connection connection
     *
     * @param url string url of the server to connect
     * @param cookies cookies of session
     * @param dataMap data map for POST request
     * @return a Jsoup connection
     */
    static Connection connection(String url, Map<String, String> cookies,
                                 Map<String, String> dataMap) {

        return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                .cookies(cookies).data(dataMap);
    }

    /**
     * Connection connection
     *
     * @param url string url of the server to connect
     * @param cookies cookies of session
     * @param dataMap data map for POST request
     * @param verify if verify SSL Cert
     * @return a Jsoup connection
     */
    static Connection connection(String url, Map<String, String> cookies,
                                 Map<String, String> dataMap, boolean verify) {
        if (!verify) {
            return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                    .header("Accept-Encoding", "gzip, deflate, br")
                    .cookies(cookies).data(dataMap).sslSocketFactory(socketFactory());
        } else {
            return connection(url, cookies, dataMap);
        }
    }

    private static SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};

        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        }
    }

    /**
     * cookies of session
     *
     * @param url string url of the server to connect
     * @return cookies map
     * @throws IOException if connection fail
     */
    static Map<String, String> getCookies(String url) throws IOException {

        return connection(url).execute().cookies();
    }

    /**
     * Captcha image in bytes format
     *
     * @param url string url of the server to connect
     * @param cookies cookies of session
     * @return captcha image in bytes format
     * @throws IOException if connection fail
     */
    static byte[] getCaptcha(String url, Map<String, String> cookies)
            throws IOException {

        return Jsoup.connect(url)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "es-ES,es;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("Accept-Encoding", "gzip, deflate, br")
                .ignoreContentType(true).timeout(25000).cookies(cookies)
                .execute().bodyAsBytes();
    }

}
