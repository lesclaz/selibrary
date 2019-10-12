package cu.marilasoft.selibrary;

import cu.marilasoft.selibrary.models.Product;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MCPortal {

    private Document page;
    private Map<String, String> cookies = null;
    private Map<String, String> mcPortalUrls = new HashMap<>();
    private Map<String, String> status = new HashMap<>();
    private String urlBase = "https://mi.cubacel.net";
    private String credit, phoneNumber, expire, date, payableBalance,
            phoneNumberOne, phoneNumberTwo, phoneNumberTree = null;
    private boolean activeBonusServices;

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Map<String, String> getStatus() {
        return status;
    }

    public Map<String, String> getMcPortalUrls() {
        return mcPortalUrls;
    }

    public String getCredit() {
        return credit;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getExpire() {
        return expire;
    }

    public String getDate() {
        return date;
    }

    public String getPayableBalance() {
        return payableBalance;
    }

    public String getPhoneNumberOne() {
        return phoneNumberOne;
    }

    public String getPhoneNumberTwo() {
        return phoneNumberTwo;
    }

    public String getPhoneNumberTree() {
        return phoneNumberTree;
    }

    public boolean isActiveBonusServices() {
        return activeBonusServices;
    }

    public MCPortal() {
    }

    public void login(String userName, String password) throws IOException {
        Connection.Response response = Net.connection(urlBase, false).execute();
        page = response.parse();
        String urlSpanish = "";
        Elements urls = page.select("a[class=\"link_msdp langChange\"]");
        for (Element url : urls) {
            if (url.attr("id").equals("spanishLanguage")) {
                urlSpanish = url.attr("href");
            }
        }
        cookies = response.cookies();

        String urlHome = urlBase + urlSpanish;

        response = Net.connection(urlHome, cookies, false).execute();
        page = response.parse();
        Element div = page.select("div[class=\"collapse navbar-collapse navbar-main-collapse\"]").first();
        Elements lis = div.select("li");
        for (Element li : lis) {
            switch (li.text()) {
                case "Ofertas":
                    mcPortalUrls.put("offers", urlBase + li.select("a").first().attr("href"));
                    break;
                case "Productos":
                    mcPortalUrls.put("products", urlBase + li.select("a").first().attr("href"));
                    break;
                case "Mi Cuenta":
                    mcPortalUrls.put("myAccount", urlBase + li.select("a").first().attr("href"));
                    break;
                case "Soporte":
                    mcPortalUrls.put("support", urlBase + li.select("a").first().attr("href"));
                    break;
            }
        }
        cookies = response.cookies();

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("language", "es_ES");
        dataMap.put("username", userName);
        dataMap.put("password", password);
        dataMap.put("uword", "step");
        String urlLogin = "https://mi.cubacel.net:8443/login/Login";
        response = Net.connection(urlLogin, cookies, dataMap, false).method(Connection.Method.POST).execute();
        cookies = response.cookies();

        loadMyAccount();
        mcPortalUrls.put("changeBonusServices", urlBase + page.select("form[id=\"toogle-internet\"]")
                .first().attr("action"));
        getInfoAccount();

    }

    private void loadMyAccount() throws IOException {
        page = Net.connection(mcPortalUrls.get("myAccount"), cookies, false).get();
    }

    private void loadProducts(Map<String, String> cookies) throws IOException {
        page = Net.connection(mcPortalUrls.get("products"), cookies, false).get();
    }

    public void changeBonusCervices(boolean activeBonusServices, String urlAction,
                                    Map<String, String> cookies) throws IOException {
        Map<String, String> dataMap = new HashMap<>();
        if (activeBonusServices) {
            dataMap.put("onoffswitchctm", "off");
        } else {
            dataMap.put("onoffswitch", "on");
            dataMap.put("onoffswitchctm", "on");
        }
        Net.connection(urlAction, cookies, dataMap, false).post();
    }

    public void buy(String urlAction, Map<String, String> cookies) throws IOException {
        page = Net.connection(urlBase + urlAction, cookies, false).get();
        String buyUrl = page
                .select("a[class=\"offerPresentationProductBuyLink_msdp button_style link_button\"]")
                .first().attr("href");
        page = Net.connection(urlBase + buyUrl, cookies, false).get();
        String msg = page.select("div[class=\"products_purchase_details_block\"]").first().select("p")
                .last().text();
        status.clear();
        if (msg.startsWith("Ha ocurrido un error.")) {
            status.put("status", "error");
            status.put("msg", msg);
        } else {
            status.put("status", "success");
            status.put("msg", "Su solicitud esta siendo procesada.");
        }
    }

    public List<Product> getProducts(Map<String, String> cookies) throws IOException {
        List<Product> products = new ArrayList<>();
        loadProducts(cookies);
        Elements products_ = page.select("div[class=\"product_inner_block\"]");
        for (Element product_ : products_) {
            products.add(new Product(product_));
        }

        return products;
    }

    private void getInfoAccount() {
        Elements divs_col1 = page.select("div[class=\"col1\"]");
        Elements divs_col2 = page.select("div[class=\"col2\"]");
        Elements divs_col1a = page.select("div[class=\"col1a\"]");
        Elements divs_col2a = page.select("div[class=\"col2a\"]");
        Element phForm1 = page.select("form[id=\"phForm1\"]").first();
        Element phForm2 = page.select("form[id=\"phForm2\"]").first();
        Element phForm3 = page.select("form[id=\"phForm3\"]").first();
        String script = page.select("script").last().data();
        // Asignando valor a phoneNumber
        for (Element div : divs_col1) {
            if (div.text().startsWith("Número de Teléfono: ")) {
                phoneNumber = div.select("span[class=\"cvalue\"]").first().text();
            }
        }
        // Asignando valor a credit y expire
        for (Element div : divs_col2) {
            if (div.text().startsWith("Saldo: ")) {
                credit = div.select("span[class=\"cvalue bold cuc-font\"]").first().text();
            } else if (div.text().startsWith("Expira: ")) {
                expire = div.select("span[class=\"cvalue\"]").first().text();
            }
        }
        // Asignando valor a date
        for (Element div : divs_col1a) {
            if (div.text().startsWith("Fecha del Adelanto: ")) {
                date = div.select("span[class=\"cvalue bold\"]").first().text();
            }
        }
        // Asignando valor a payableBalance
        for (Element div : divs_col2a) {
            if (div.text().startsWith("Saldo pendiente por pagar: ")) {
                payableBalance = div.select("span[class=\"cvalue bold cuc-font\"]").first().text();
            }
        }
        // Asignando valor a phoneNumberOne
        for (Element input_ : phForm1.select("input[type=\"hidden\"]")) {
            if (input_.attr("id").equals("cancelFlag")) {
                phoneNumberOne = input_.attr("value");
            }
        }
        // Asignando valor a phoneNumberTwo
        for (Element input_ : phForm2.select("input[type=\"hidden\"]")) {
            if (input_.attr("id").equals("cancelFlag")) {
                phoneNumberTwo = input_.attr("value");
            }
        }
        // Asignando valor a phoneNumberTree
        for (Element input_ : phForm3.select("input[type=\"hidden\"]")) {
            if (input_.attr("id").equals("cancelFlag")) {
                phoneNumberTree = input_.attr("value");
            }
        }
        // Asignando valor a activeBonusServices
        int indexOf;
        String substring = "";
        String onOff = "";
        int indexOf2 = script.indexOf("'false'; prop=");
        if (indexOf2 != -1) {
            substring = script.substring(indexOf2);
        }
        indexOf2 = substring.indexOf("prop=");
        if (indexOf2 != -1) {
            substring = substring.substring(indexOf2);
            indexOf = substring.indexOf(";");
            if (indexOf != -1) {
                onOff = substring.substring(0, indexOf);
                onOff = onOff.split("=")[1].replace("'", "");
                System.out.println("Hasta aqui todo bien");
                if (onOff.equalsIgnoreCase("true")) {
                    activeBonusServices = true;
                } else {
                    activeBonusServices = false;
                }
            }
        }
    }
}
