package cu.marilasoft.selibrary;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cu.marilasoft.selibrary.Utils.*;

public class CaptivePortal {

    private static Map<String, String> cookies;
    private static Map<Object, Object> status;
    private static Map<String, Object> sessionParameters;
    private static List<String> errors;
    private static Document page;

    private static String wLanUserIp;
    private static String wLanAcName;
    private static String wLanMac;
    private static String firstUrl;
    private static String SSId;
    private static String userType;
    private static String gotoPage;
    private static String successPage;
    private static String loggerId;
    private static String lang;
    private static String CSRFHW;

    private static String
            urlBase = "https://secure.etecsa.net:8443/";

    public CaptivePortal() {
    }

    public Map<String, String> cookies() {
        return cookies;
    }

    public Map<Object, Object> status() {
        return status;
    }

    public Map<String, Object> sessionParameters() {
        return sessionParameters;
    }

    private void getInfoLogin(Map<String, String> cookies) throws IOException {
        page = Net.connection(urlBase, cookies).get();
        wLanUserIp = page.select("input[name=\"wlanuserip\"]").first()
                .attr("value");
        wLanAcName = page.select("input[name=\"wlanacname\"]").first()
                .attr("value");
        wLanMac = page.select("input[name=\"wlanmac\"]").first()
                .attr("value");
        firstUrl = page.select("input[name=\"firsturl\"]").first()
                .attr("value");
        SSId = page.select("input[name=\"ssid\"]").first()
                .attr("value");
        userType = page.select("input[name=\"usertype\"]").first()
                .attr("value");
        gotoPage = page.select("input[name=\"gotopage\"]").first()
                .attr("value");
        successPage = page.select("input[name=\"successpage\"]").first()
                .attr("value");
        loggerId = page.select("input[name=\"loggerId\"]").first()
                .attr("value");
        lang = page.select("input[name=\"lang\"]").first()
                .attr("value");
        CSRFHW = page.select("input[name=\"CSRFHW\"]").first()
                .attr("value");
    }

    public void preLogin() throws IOException {
        status = new HashMap<>();
        errors = new ArrayList<>();
        cookies = Net.getCookies(urlBase);
        getInfoLogin(cookies);
    }

    public int login(String userName, String password, Map<String, String> cookies)
            throws IOException {
        int ret = 0;

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("wlanuserip", wLanUserIp);
        dataMap.put("wlanacname", wLanAcName);
        dataMap.put("wlanmac", wLanMac);
        dataMap.put("firsturl", firstUrl);
        dataMap.put("ssid", SSId);
        dataMap.put("usertype", userType);
        dataMap.put("gotopage", gotoPage);
        dataMap.put("successpage", successPage);
        dataMap.put("loggerId", loggerId);
        dataMap.put("lang", lang);
        dataMap.put("username", userName);
        dataMap.put("password", password);
        dataMap.put("CSRFHW", CSRFHW);

        String urlLoginAction = "https://secure.etecsa.net:8443//LoginServlet";
        page = Net.connection(urlLoginAction, cookies, dataMap).post();

        status.clear();
        if (findError(page, "IP").size() != 0) {
            ret = 1;
            errors.clear();
            errors = findError(page, "IP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", findSuccess(page));
            sessionParameters = getSessionParameters(page);
            sessionParameters.put("cookies", cookies);
        }

        return ret;
    }

    public String updateAvailableTime(String updateTimeUrl, Map<String, String> cookies)
            throws IOException {
        return Net.connection(urlBase + updateTimeUrl, cookies).get().text();
    }

    public int logout(String logoutUrl, Map<String, String> cookies) throws IOException {
        int ret = 0;
        page = Net.connection(urlBase + logoutUrl, cookies).get();
        status.clear();
        if (page.text().replace("logoutcallback('", "")
                .replace("');", "").equals("SUCCESS")) {
            status.put("status", "success");
            status.put("msg", "Session cerrada correctamente!");
        } else {
            ret = 1;
            errors.clear();
            status.put("status", "error");
            status.put("msg", "Error al cerrar session!");
        }
        return ret;
    }

    public Map<String, String> getUserInfo(String userName, String password, Map<String,
            String> cookies) throws IOException {
        String urlUserInfo = "https://secure.etecsa.net:8443/EtecsaQueryServlet";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("wlanuserip", wLanUserIp);
        dataMap.put("wlanacname", wLanAcName);
        dataMap.put("wlanmac", wLanMac);
        dataMap.put("firsturl", firstUrl);
        dataMap.put("ssid", SSId);
        dataMap.put("usertype", userType);
        dataMap.put("gotopage", gotoPage);
        dataMap.put("successpage", successPage);
        dataMap.put("loggerId", loggerId);
        dataMap.put("lang", lang);
        dataMap.put("username", userName);
        dataMap.put("password", password);
        dataMap.put("CSRFHW", CSRFHW);
        page = Net.connection(urlUserInfo, cookies, dataMap).post();
        Elements trs = page.select("table[id=sessioninfo]").first().select("tr");
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("status", trs.get(0).select("td").last().text());
        userInfo.put("credit", trs.get(1).select("td").last().text());
        userInfo.put("expire", trs.get(2).select("td").last().text());
        userInfo.put("access_areas", trs.get(3).select("td").last().text());

        return userInfo;
    }

    public List<String> getTermsOfUse(Map<String, String> cookies) throws IOException {
        String urlTermsOfUse = "https://secure.etecsa.net:8443/nauta_etecsa/LoginURL/pc/pc_termsofuse.jsp";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("wlanuserip", wLanUserIp);
        dataMap.put("wlanacname", wLanAcName);
        dataMap.put("wlanmac", wLanMac);
        dataMap.put("firsturl", firstUrl);
        dataMap.put("ssid", SSId);
        dataMap.put("usertype", userType);
        dataMap.put("gotopage", gotoPage);
        dataMap.put("successpage", successPage);
        dataMap.put("loggerId", loggerId);
        dataMap.put("lang", lang);
        dataMap.put("username", "");
        dataMap.put("password", "");
        dataMap.put("CSRFHW", CSRFHW);

        page = Net.connection(urlTermsOfUse, cookies, dataMap).post();
        Element ol = page.select("ol[class=\"condiciones\"]").first();
        Elements lis = ol.select("li");
        List<String> terms = new ArrayList<>();
        for (Element term : lis) {
            terms.add(term.text());
        }
        return terms;
    }
}
