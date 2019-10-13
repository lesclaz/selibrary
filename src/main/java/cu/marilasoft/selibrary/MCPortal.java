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
            phoneNumberOne, phoneNumberTwo, phoneNumberTree, welcomeMsg, password = null;
    private boolean activeBonusServices;

    /**
     * Cookies Map
     *
     * @return cookies of connection
     */
    public Map<String, String> getCookies() {
        return cookies;
    }

    /**
     * Status Map
     *
     * @return status of last used function
     */
    public Map<String, String> getStatus() {
        return status;
    }

    /**
     * Url List of My Cubacel Portal
     *
     * @return a my cubacel portal url list
     */
    public Map<String, String> getMcPortalUrls() {
        return mcPortalUrls;
    }

    /**
     * Welcome Message
     *
     * @return the my cubacel portal welcome message
     */
    public String getWelcomeMsg() {
        return welcomeMsg;
    }

    /**
     * Credit
     *
     * @return account credit
     */
    public String getCredit() {
        return credit;
    }

    /**
     * Phone Number
     *
     * @return account phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Expire date
     *
     * @return account credit date expire
     */
    public String getExpire() {
        return expire;
    }

    /**
     * Date Use Advance Balance
     *
     * @return use advance balance use
     */
    public String getDate() {
        return date;
    }

    /**
     * Payable Balance
     *
     * @return payable balance
     */
    public String getPayableBalance() {
        return payableBalance;
    }

    /**
     * Phone Number One on the Friends and Family
     *
     * @return phone number one on the friends and family
     */
    public String getPhoneNumberOne() {
        return phoneNumberOne;
    }

    /**
     * Phone Number Two on the Friends and Family
     *
     * @return phone number two on the friends and family
     */
    public String getPhoneNumberTwo() {
        return phoneNumberTwo;
    }

    /**
     * Phone Number Tre on the Friends and Family
     *
     * @return phone number tre on the friends and family
     */
    public String getPhoneNumberTre() {
        return phoneNumberTree;
    }

    /**
     * Active Bonus Services
     *
     * @return if bonus services is active (true or false)
     */
    public boolean isActiveBonusServices() {
        return activeBonusServices;
    }

    public MCPortal() {
    }

    private void loadLoginPage() throws IOException {
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

        mcPortalUrls.clear();
        mcPortalUrls.put("home", urlBase + urlSpanish);
        response = Net.connection(urlBase + urlSpanish, cookies, false).execute();
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
    }

    /**
     * Complete the singUp on my cubacel portal
     *
     * @param code     code send by Cubacel
     * @param password password for account
     * @param cookies  cookies of session
     * @throws IOException if connection failed
     */
    public void completeSingUp(String code, String password, Map<String, String> cookies)
            throws IOException {
        String urlVerifyRegistrationCode = "https://mi.cubacel.net:8443/login/VerifyRegistrationCode";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("username", code);
        dataMap.put("uword", "step");
        Net.connection(urlVerifyRegistrationCode, cookies, dataMap, false)
                .method(Connection.Method.POST).execute();

        dataMap = new HashMap<>();
        dataMap.put("newPassword", password);
        dataMap.put("cnewPassword", password);
        dataMap.put("uword", "step");
        page = Net.connection("https://mi.cubacel.net:8443/login/recovery/RegisterPasswordCreation",
                this.cookies, dataMap, false)
                .method(Connection.Method.POST)
                .execute().parse();
        if (page.select("div[class=\"body_wrapper error_page\"]").first() != null) {
            status.clear();
            status.put("function", "resetPassword");
            status.put("status", "error");
            try {
                status.put("msg", page.select("div[class=\"body_wrapper error_page\"]").first()
                        .select("div[class=\"welcome_login error_Block\"]").first()
                        .select("div[class=\"container\"]").first()
                        .select("b").first().text());
            } catch (NullPointerException e) {
                status.put("msg", page.select("div[class=\"body_wrapper error_page\"]").first()
                        .select("div[class=\"welcome_login error_Block\"]").first()
                        .select("div[class=\"container\"]").first().text());
            }

        } else {
            status.clear();
            status.put("function", "resetPassword");
            status.put("status", "success");
            status.put("msg", "Su cuenta ha sido creada!");
        }

    }

    /**
     * SingUp on my cubacel portal
     *
     * @param phoneNumber a phone number of cubacel (required)
     * @param firstName   first name for account (required)
     * @param lastName    last name for account (required)
     * @param email       email address for account (optional)
     * @throws IOException if connection failed
     */
    public void singUp(String phoneNumber, String firstName, String lastName, String email)
            throws IOException {

        String urlWelcomeLogin = "https://mi.cubacel.net:8443/login/jsp/welcome-login.jsp?language=es";
        Connection.Response response = Net.connection(urlWelcomeLogin, false)
                .execute();
        this.cookies = response.cookies();
        String urlSingUp = "https://mi.cubacel.net:8443/login/jsp/registerNew.jsp";
        String urlSingUpAction = "https://mi.cubacel.net:8443/login/NewUserRegistration";
        Net.connection(urlSingUp, this.cookies, false)
                .execute();

        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("msisdn", phoneNumber);
        dataMap.put("firstname", firstName);
        dataMap.put("lastname", lastName);
        dataMap.put("email", email);
        dataMap.put("uword", "step");
        dataMap.put("agree", "on");
        Net.connection(urlSingUpAction, this.cookies, dataMap, false)
                .method(Connection.Method.POST).execute();
    }

    /**
     * Complete reset password process
     *
     * @param code        code send by Cubacel
     * @param newPassword a new password for account
     * @param cookies     cookies of session
     * @throws IOException if connection failed
     */
    public void completeResetPassword(String code, String newPassword, Map<String, String> cookies) throws IOException {
        String urlResetPassword = "https://mi.cubacel.net:8443/login/recovery/ResetPassword";
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("oneTimecode", code);
        dataMap.put("newPassword", newPassword);
        dataMap.put("cnewPassword", newPassword);
        dataMap.put("uword", "step");
        Connection.Response response = Net.connection(urlResetPassword, cookies, dataMap, false)
                .method(Connection.Method.POST).execute();
        page = response.parse();
        if (page.select("div[class=\"body_wrapper error_page\"]").first() != null) {
            status.clear();
            status.put("function", "resetPassword");
            status.put("status", "error");
            status.put("msg", page.select("div[class=\"body_wrapper error_page\"]").first()
                    .select("div[class=\"welcome_login error_Block\"]").first()
                    .select("div[class=\"container\"]").first()
                    .select("b").first().text());
        } else {
            this.cookies = response.cookies();
            status.clear();
            status.put("function", "resetPassword");
            status.put("status", "success");
            status.put("msg", "Su contraseña ha sido restablecida!");
        }
    }

    /**
     * Initialize reset password process
     *
     * @param phoneNumber phone number to send verification code
     * @throws IOException if connection failed
     */
    public void resetPassword(String phoneNumber) throws IOException {
        cookies.clear();
        String urlWelcomeLogin = "https://mi.cubacel.net:8443/login/jsp/welcome-login.jsp?language=es";
        Connection.Response response = Net.connection(urlWelcomeLogin, cookies, false)
                .execute();
        cookies = response.cookies();
        String urlForgot = "https://mi.cubacel.net:8443/login/jsp/forgot-password.jsp";
        String urlForgotAction = "https://mi.cubacel.net:8443/login/recovery/ForgotPassword";
        response = Net.connection(urlForgot, cookies, false).execute();
        this.cookies = response.cookies();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("mobileNumber", phoneNumber);
        dataMap.put("uword", "step");
        response = Net.connection(urlForgotAction, this.cookies, dataMap, false).method(Connection.Method.POST)
                .execute();
        this.cookies = response.cookies();
    }

    /**
     * Login on my cubacel portal
     *
     * @param userName the account phone number
     * @param password the account password
     * @throws IOException if connection failed
     */
    public void login(String userName, String password) throws IOException {
        loadLoginPage();
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("language", "es_ES");
        dataMap.put("username", userName);
        dataMap.put("password", password);
        dataMap.put("uword", "step");
        String urlLogin = "https://mi.cubacel.net:8443/login/Login";
        Connection.Response response = Net.connection(urlLogin, cookies, dataMap, false)
                .method(Connection.Method.POST).execute();
        cookies = response.cookies();
        page = response.parse();
        if (page.select("div[class=\"body_wrapper error_page\"]").first() != null) {
            String msg = page.select("div[class=\"body_wrapper error_page\"]").first()
                    .select("div[class=\"welcome_login error_Block\"]").first()
                    .select("div[class=\"container\"]").first()
                    .select("b").text();
            status.clear();
            status.put("status", "error");
            status.put("msg", msg);
        } else {
            status.clear();
            status.put("status", "success");
            status.put("msg", "Loageo satisfactorio!");
            welcomeMsg = Net.connection(mcPortalUrls.get("home"), cookies, false).get()
                    .select("div[class=\"banner_bg_color mBottom20\"]").first()
                    .select("h2").text();
            loadMyAccount();
            mcPortalUrls.put("changeBonusServices", urlBase + page.select("form[id=\"toogle-internet\"]")
                    .first().attr("action"));
            getInfoAccount();
        }

    }

    private void loadMyAccount() throws IOException {
        page = Net.connection(mcPortalUrls.get("myAccount"), cookies, false).get();
    }

    private void loadProducts(Map<String, String> cookies) throws IOException {
        page = Net.connection(mcPortalUrls.get("products"), cookies, false).get();
    }

    /**
     * Change bonus services status
     *
     * @param activeBonusServices bonus services status
     * @param urlAction url for change
     * @param cookies cookies of session
     * @throws IOException if connection failed
     */
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

    /**
     * Buy a product
     *
     * @param urlAction url for change
     * @param cookies cookies of session
     * @throws IOException if connection failed
     */
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

    /**
     * Get products list
     *
     * @param cookies cookies of session
     * @return a products list
     * @throws IOException if connection failed
     */
    public List<Product> getProducts(Map<String, String> cookies) throws IOException {
        return _getProducts(cookies, false);
    }

    /**
     * Get products list
     *
     * @param cookies             cookies of session
     * @param loadLongDescription if load long description
     * @return a products list
     * @throws IOException if connection failed
     */
    public List<Product> getProducts(Map<String, String> cookies, boolean loadLongDescription)
            throws IOException {
        return _getProducts(cookies, loadLongDescription);
    }

    private List<Product> _getProducts(Map<String, String> cookies, boolean loadLongDescription)
            throws IOException {
        List<Product> products = new ArrayList<>();
        loadProducts(cookies);
        Elements products_ = page.select("div[class=\"product_inner_block\"]");
        if (loadLongDescription) {
            for (Element product_ : products_) {
                products.add(new Product(product_, cookies, true));
            }
        } else {
            for (Element product_ : products_) {
                products.add(new Product(product_));
            }
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
        // Assigning phoneNumber value
        for (Element div : divs_col1) {
            if (div.text().startsWith("Número de Teléfono: ")) {
                phoneNumber = div.select("span[class=\"cvalue\"]").first().text();
            }
        }
        // Assigning credit and expire values
        for (Element div : divs_col2) {
            if (div.text().startsWith("Saldo: ")) {
                credit = div.select("span[class=\"cvalue bold cuc-font\"]").first().text();
            } else if (div.text().startsWith("Expira: ")) {
                expire = div.select("span[class=\"cvalue\"]").first().text();
            }
        }
        // Assigning date value
        for (Element div : divs_col1a) {
            if (div.text().startsWith("Fecha del Adelanto: ")) {
                date = div.select("span[class=\"cvalue bold\"]").first().text();
            }
        }
        // Assigning payableBalance value
        for (Element div : divs_col2a) {
            if (div.text().startsWith("Saldo pendiente por pagar: ")) {
                payableBalance = div.select("span[class=\"cvalue bold cuc-font\"]").first().text();
            }
        }
        // Assigning phoneNumberOne value
        for (Element input_ : phForm1.select("input[type=\"hidden\"]")) {
            if (input_.attr("id").equals("cancelFlag")) {
                phoneNumberOne = input_.attr("value");
            }
        }
        // Assigning phoneNumberTwo value
        for (Element input_ : phForm2.select("input[type=\"hidden\"]")) {
            if (input_.attr("id").equals("cancelFlag")) {
                phoneNumberTwo = input_.attr("value");
            }
        }
        // Assigning phoneNumberTre value
        for (Element input_ : phForm3.select("input[type=\"hidden\"]")) {
            if (input_.attr("id").equals("cancelFlag")) {
                phoneNumberTree = input_.attr("value");
            }
        }
        // Assigning activeBonusServices value
        int indexOf;
        String substring = "";
        String onOff;
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
                activeBonusServices = onOff.equalsIgnoreCase("true");
            }
        }
    }
}
