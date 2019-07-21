package cu.marilasoft.selibrary;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class Net {
	
	public static Connection connection (String url) {
		
		return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000);
	}
	
	public static Connection connection (String url, Map<String, String> cookies) {
		
		return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
				.cookies(cookies);
	}
	
	public static Connection connection (String url, Map<String, String> cookies,
			Map<String, String> dataMap) {
		
		return Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
				.cookies(cookies).data(dataMap);
	}
	
	public static Map<?, ?> getCookies(String url) throws IOException {
		
		return connection(url).execute().cookies();
	}
	
	public static byte[] getCaptcha (String url, Map<String, String> cookies)
			throws IOException {

		return Jsoup.connect(url).header("Accept-Encoding", "gzip, deflate")
				.ignoreContentType(true).timeout(25000).cookies(cookies)
				.execute().bodyAsBytes();
	}
	
	public static byte[] getImg (String url, Map<String, String> cookies)
			throws IOException {
		
		return Jsoup.connect(url).header("Accept-Encoding", "gzip, deflate")
				.ignoreContentType(true).timeout(25000).cookies(cookies).execute()
				.bodyAsBytes();
	}

}
