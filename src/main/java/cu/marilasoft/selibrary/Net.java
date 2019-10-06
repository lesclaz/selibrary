package cu.marilasoft.selibrary;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

class Net {

    private static Connection connection(String url) {

        return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000);
    }

    static Connection connection(String url, Map<String, String> cookies) {

        return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                .cookies(cookies);
    }

    static Connection connection(String url, Map<String, String> cookies,
                                 Map<String, String> dataMap) {

        return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
                .cookies(cookies).data(dataMap);
    }

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
