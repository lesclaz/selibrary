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

class Net {

    private static Connection connection(String url) {

        return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000);
    }

    static Connection connection(String url, boolean verify) {
        if (!verify) {
            return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                    .sslSocketFactory(socketFactory());
        } else {
            return connection(url);
        }
    }

    static Connection connection(String url, Map<String, String> cookies) {

        return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                .cookies(cookies);
    }

    static Connection connection(String url, Map<String, String> cookies, boolean verify) {
        if (!verify) {
            return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                    .cookies(cookies).sslSocketFactory(socketFactory());
        } else {
            return connection(url, cookies);
        }
    }

    static Connection connection(String url, Map<String, String> cookies,
                                 Map<String, String> dataMap) {

        return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                .cookies(cookies).data(dataMap);
    }

    static Connection connection(String url, Map<String, String> cookies,
                                 Map<String, String> dataMap, boolean verify) {
        if (!verify) {
            return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
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

    ;

    static Map<String, String> getCookies(String url) throws IOException {

        return connection(url).execute().cookies();
    }

    static byte[] getCaptcha(String url, Map<String, String> cookies)
            throws IOException {

        return Jsoup.connect(url).header("Accept-Encoding", "gzip, deflate")
                .ignoreContentType(true).timeout(25000).cookies(cookies)
                .execute().bodyAsBytes();
    }

}
