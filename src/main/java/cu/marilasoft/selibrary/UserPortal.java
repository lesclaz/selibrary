package cu.marilasoft.selibrary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cu.marilasoft.selibrary.models.Connection;

public class UserPortal {
	
	private static String
	urlLogin = "https://www.portal.nauta.cu/user/login/es-es";
	private static String
	urlCaptcha = "https://www.portal.nauta.cu/captcha/?";
	private static String
	urlServiceDetail = "https://www.portal.nauta.cu/useraaa/service_detail";
	private static String
	urlServiceDetailList = "https://www.portal.nauta.cu/useraaa/service_detail_list/";
	private static String
	urlUserInfo = "https://www.portal.nauta.cu/useraaa/user_info";
	private static String
	urlRecharge = "https://www.portal.nauta.cu/useraaa/recharge_account";
	private static String csrf = null;
	private static Document page = null;
	private static Map<String, String> cookies;
	private static List<String> errors = new ArrayList<String>();
	private static byte[] captcha = null;

	public byte[] captchaImg;
	
	private static void getCSRF(String url, Map<String, String> cookies)
			throws IOException {
		page = (Document) Net.connection(url, cookies).get();
		Element input = page.select("input[name='csrf']").first();
		csrf = input.attr("value").toString();
	}
	
	private void loadCAPTCHA (Map<String, String> cookies)
			throws IOException {
		captcha = null;
		captcha = Net.getCaptcha(urlCaptcha, cookies);
		captchaImg = captcha;
	}
	
	private static void loadLoging (Map<String, String> cookies)
			throws IOException {
		getCSRF(urlLogin, cookies);
	}
	
	@SuppressWarnings("unchecked")
	private static void preLogin () throws IOException {
		cookies = (Map<String, String>) Net.getCookies(urlLogin);
		loadLoging(cookies);
	}
	
	public UserPortal () {
		
	}
	
	public void initialice () throws IOException {
		preLogin();
	}
	
	public void reloadCAPTCHA (Map<String, String> cookies)
			throws IOException {
		loadCAPTCHA(cookies);
	}
	
	public void reloadCAPTCHA ()
			throws IOException {
		loadCAPTCHA(cookies);
	}
	
	public void reload_userInfo (Map<String, String> cookies)
			throws IOException {
		getCSRF(urlUserInfo, cookies);
	}
	
	public void reload_userInfo ()
			throws IOException {
		getCSRF(urlUserInfo, cookies);
	}
	
	private int _login(String userName, String password, String captchaCode,
			Map<String, String> cookies) throws IOException {
		int ret = 0;
		Map<String, String> dataMap = new HashMap<String, String>();
		
		dataMap.put("btn_submit", "");
		dataMap.put("captcha", captchaCode);
		dataMap.put("csrf", csrf);
		dataMap.put("login_user", userName);
		dataMap.put("password_user", password);
		page = Net.connection(urlLogin, cookies).data(dataMap).post();
		int errorStatus = findError(page);
		if (errorStatus != ret) {
			ret = errorStatus;
		}
		
		return ret;
	}
	
	public int login(String userName, String password, String captchaCode,
			Map<String, String> cookies) throws IOException {
		getCSRF(urlLogin, cookies);
		return _login(userName, password, captchaCode, cookies);
	}
	
	public int login (String userName, String password,String captchaCode)
			throws IOException {
		return _login(userName, password, captchaCode, cookies);
	}
	
	public String userName () {
		String userName = null;
		
		Element cardPanel = Utils.getCardPanel(page);
		Elements userTemp = cardPanel.select("div.m6");
		for (Element info : userTemp){
			String temp = info.select("h5").text();
			if (temp.equals("Usuario")) {
				userName = info.select("p").text();
				break;
			}
		}
		
		return userName;
	}
	
	public String blockDate () {
		String blockDate = null;
		
		Element cardPanel = Utils.getCardPanel(page);
		Elements userTemp = cardPanel.select("div.m6");
		for (Element info : userTemp){
			String temp = info.select("h5").text();
			if (temp.equals("Fecha de bloqueo")) {
				blockDate = info.select("p").text();
				break;
			}
		}
		
		return blockDate;
	}
	
	public String delDate () {
		String delDate = null;
		
		Element cardPanel = Utils.getCardPanel(page);
		Elements userTemp = cardPanel.select("div.m6");
		for (Element info : userTemp){
			String temp = info.select("h5").text();
			if (temp.equals("Fecha de eliminación")) {
				delDate = info.select("p").text();
				break;
			}
		}
		
		return delDate;
	}
	
	public String accountType () {
		String accountType = null;
		
		Element cardPanel = Utils.getCardPanel(page);
		Elements userTemp = cardPanel.select("div.m6");
		for (Element info : userTemp){
			String temp = info.select("h5").text();
			if (temp.equals("Tipo de cuenta")) {
				accountType = info.select("p").text();
				break;
			}
		}
		
		return accountType;
	}
	
	public String serviceType () {
		String serviceType = null;
		
		Element cardPanel = Utils.getCardPanel(page);
		Elements userTemp = cardPanel.select("div.m6");
		for (Element info : userTemp){
			String temp = info.select("h5").text();
			if (temp.equals("Tipo de servicio")) {
				serviceType = info.select("p").text();
				break;
			}
		}
		
		return serviceType;
	}
	
	public String credit () {
		String credit = null;
		
		Element cardPanel = Utils.getCardPanel(page);
		Elements userTemp = cardPanel.select("div.m6");
		for (Element info : userTemp){
			String temp = info.select("h5").text();
			if (temp.equals("Saldo disponible")) {
				credit = info.select("p").text();
				break;
			}
		}
		
		return credit;
	}
	
	public String time () {
		String time = null;
		
		Element cardPanel = Utils.getCardPanel(page);
		Elements userTemp = cardPanel.select("div.m6");
		for (Element info : userTemp){
			String temp = info.select("h5").text();
			if (temp.equals("Tiempo disponible de la cuenta")) {
				time = info.select("p").text();
				break;
			}
		}
		
		return time;
	}
	
	public String mailAccount () {
		String mailAccount = null;
		
		Element cardPanel = Utils.getCardPanel(page);
		Elements userTemp = cardPanel.select("div.m6");
		for (Element info : userTemp){
			String temp = info.select("h5").text();
			if (temp.equals("Cuenta de correo")) {
				mailAccount = info.select("p").text();
				break;
			}
		}
		
		return mailAccount;
	}
	
	private static int findError (Document page) {
		int ret = 0;
		
		errors.clear();
		Element lastScript = page.select("script[type='text/javascript']")
				.last();
		if (lastScript.data().startsWith("toastr.error")) {
			ret = 1;
			Document data = Jsoup.parse(lastScript.data()
					.replace("toastr.error('", "")
					.replace("');", ""));
			Element error = data.select("li.msg_error").first();
			if (error.text().startsWith("Se han detectado algunos errores.")) {
				Elements sub_messages = data.select("li.sub-message");
				for (Element sub_message : sub_messages) {
					errors.add(sub_message.text());
				}
			} else {
				errors.add(error.text());
			}
			
		}
		return ret;
	}
	
	public List<String> errors () {
		return errors;
	}
	
	private static List<Connection> _getConnections(int year, int month,
			Map<String, String> cookies) throws IOException {
		List<Connection> connections = new ArrayList<Connection>();
		String year_month = null;
		
		if (month <= 9) {
			year_month = Integer.toString(year) + "-0" + Integer.toString(month);
		} else {
			year_month = Integer.toString(year) + "-" + Integer.toString(month);
		}
		getCSRF(urlServiceDetail, cookies);
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("csrf", csrf);
		dataMap.put("year_month", year_month);
		dataMap.put("list_type", "service_detail");
		
		page = Net.connection(urlServiceDetailList, cookies, dataMap).post();
		Element tableConnectionList = Utils.getTableConnectionList(page);
		Elements connectionList = Utils.getConnectionList(tableConnectionList);
		for (Element connection_ : connectionList) {
			Elements _connection = connection_.select("td");
			connections.add(new Connection(_connection.get(0).text(),
					_connection.get(1).text(), _connection.get(2).text(),
					_connection.get(3).text(), _connection.get(4).text(),
					_connection.get(5).text()));
		}
		
		return connections;
	}
	
	public List<Connection> getConnections(int year, int month,
			Map<String, String> cookies) throws IOException {
		return _getConnections(year, month, cookies);
	}
	
	public List<Connection> getConnections(int year, int month)
			throws IOException {
		return _getConnections(year, month, cookies);
	}
	
	public Map<String, String> cookies () {
		return cookies;
	}
	
	private static int _recharge (String rechargeCode, Map <String, String> cookies)
			throws IOException {
		int ret = 0;
		
		getCSRF(urlRecharge, cookies);
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("csrf", csrf);
		dataMap.put("recharge_code", rechargeCode);
		dataMap.put("btn_submit", "");
		page = Net.connection(urlRecharge, cookies, dataMap).post();
		int errorStatus = findError(page);
		if (errorStatus != ret) {
			ret = errorStatus;
		}

		return ret;
	}
	
	public int recharge (String rechargeCode) throws IOException {
		return _recharge(rechargeCode, cookies);
	}
	
	public int recharge (String rechargeCode, Map<String, String> cookies)
			throws IOException {
		return _recharge(rechargeCode, cookies);
	}

}
