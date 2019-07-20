package cu.marilasoft.selibrary;

import java.io.IOException;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;

public class Net {
	
	public static Connection connection (String url) {
		Connection conn = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000);
		
		return conn;
	}
	
	public static Connection connection (String url, Map<String, String> cookies) {
		Connection conn = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
				.cookies(cookies);
		
		return conn;
	}
	
	public static Connection connection (String url, Map<String, String> cookies,
			Map<String, String> dataMap) {
		Connection conn = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000)
				.cookies(cookies).data(dataMap);
		
		return conn;
	}
	
	public static Map<?, ?> getCookies(String url) throws IOException {
		Response resp = connection(url).execute();
		
		return resp.cookies();
	}
	
	public static byte[] getCaptcha (String url, Map<String, String> cookies)
			throws IOException {
		
		Response excecute = Jsoup.connect(url).header("Accept-Encoding", "gzip, deflate")
				.ignoreContentType(true).timeout(25000).cookies(cookies).execute();
		
		return excecute.bodyAsBytes();
	}
	
	public static byte[] getImg (String url, Map<String, String> cookies)
			throws IOException {
		Response excecute = Jsoup.connect(url).header("Accept-Encoding", "gzip, deflate")
				.ignoreContentType(true).timeout(25000).cookies(cookies).execute();
		
		return excecute.bodyAsBytes();
	}

}
