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
    String urlBase = "https://mi.cubacel.net";

    public Map<String, String> getCookies() {
        return cookies;
    }

    public Map<String, String> getStatus() {
        return status;
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

    }

    private void loadMyAccount() throws IOException {
        page = Net.connection(mcPortalUrls.get("myAccount"), cookies, false).get();
    }

    private void loadProducts(Map<String, String> cookies) throws IOException {
        page = Net.connection(mcPortalUrls.get("products"), cookies, false).get();
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

    public String credit() {
        Elements divs_col = page.select("div[class=\"col2\"]");
        for (Element div : divs_col) {
            if (div.text().startsWith("Saldo: ")) {
                return div.select("span[class=\"cvalue bold cuc-font\"]").first().text();
            }
        }
        return null;
    }

    public String phoneNumber() {
        Elements divs_col = page.select("div[class=\"col1\"]");
        for (Element div : divs_col) {
            if (div.text().startsWith("Número de Teléfono: ")) {
                return div.select("span[class=\"cvalue\"]").first().text();
            }
        }
        return null;
    }

    public String expire() {
        Elements divs_col = page.select("div[class=\"col2\"]");
        for (Element div : divs_col) {
            if (div.text().startsWith("Expira: ")) {
                return div.select("span[class=\"cvalue\"]").first().text();
            }
        }
        return null;
    }

    public String date() {
        Elements divs_col = page.select("div[class=\"col1a\"]");
        for (Element div : divs_col) {
            if (div.text().startsWith("Fecha del Adelanto: ")) {
                return div.select("span[class=\"cvalue bold\"]").first().text();
            }
        }
        return null;
    }

    public String payableBalance() {
        Elements divs_col = page.select("div[class=\"col2a\"]");
        for (Element div : divs_col) {
            if (div.text().startsWith("Saldo pendiente por pagar: ")) {
                return div.select("span[class=\"cvalue bold cuc-font\"]").first().text();
            }
        }
        return null;
    }

    public String phoneNumberOne() {
        Element form_ph1 = page.select("form[id=\"phForm1\"]").first();
        for (Element input_ : form_ph1.select("input[type=\"hidden\"]")) {
            if (input_.attr("id").equals("cancelFlag")) {
                return input_.attr("value");
            }
        }
        return null;
    }

    public String phoneNumberTwo() {
        Element form_ph1 = page.select("form[id=\"phForm2\"]").first();
        for (Element input_ : form_ph1.select("input[type=\"hidden\"]")) {
            if (input_.attr("id").equals("cancelFlag")) {
                return input_.attr("value");
            }
        }
        return null;
    }

    public String phoneNumberTree() {
        Element form_ph1 = page.select("form[id=\"phForm3\"]").first();
        for (Element input_ : form_ph1.select("input[type=\"hidden\"]")) {
            if (input_.attr("id").equals("cancelFlag")) {
                return input_.attr("value");
            }
        }
        return null;
    }
}
