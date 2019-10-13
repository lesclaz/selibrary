package cu.marilasoft.selibrary;

import cu.marilasoft.selibrary.models.Connection;
import cu.marilasoft.selibrary.models.Recharge;
import cu.marilasoft.selibrary.models.Transfer;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cu.marilasoft.selibrary.Utils.*;

public class UserPortal {

    private static String
            urlLogin = "https://www.portal.nauta.cu/user/login/es-es";
    private static String csrf = null;
    private static Document page = null;
    private static Map<String, String> cookies = null;
    private static Map<Object, Object> status = new HashMap<>();
    private static List<String> errors = new ArrayList<>();

    private String userName, blockDate, delDate, accountType, serviceType, credit, time, mailAccount = null;

    private byte[] captchaImg = null;

    // return captchaImg in bytes format
    public byte[] getCaptchaImg() {
        return captchaImg;
    }

    // return status
    public Map<Object, Object> getStatus() {
        return status;
    }

    // return cookies
    public Map<String, String> getCookies() {
        return cookies;
    }

    // return userName
    public String getUserName() {
        return userName;
    }

    // return blockDate
    public String getBlockDate() {
        return blockDate;
    }

    // return delDate
    public String getDelDate() {
        return delDate;
    }

    // return accountType
    public String getAccountType() {
        return accountType;
    }

    // return serviceType
    public String getServiceType() {
        return serviceType;
    }

    // return credit
    public String getCredit() {
        return credit;
    }

    // return time
    public String getTime() {
        return time;
    }

    // return mailAccount
    public String getMailAccount() {
        return mailAccount;
    }

    // load csrf code
    private static void getCSRF(String url, Map<String, String> cookies)
            throws IOException {
        page = Net.connection(url, cookies).get();
        Element input = page.select("input[name='csrf']").first();
        csrf = input.attr("value");
    }

    // save captchaImg in bytes format
    public void loadCAPTCHA(Map<String, String> cookies)
            throws IOException {
        byte[] captcha;
        String urlCaptcha = "https://www.portal.nauta.cu/captcha/?";
        captcha = Net.getCaptcha(urlCaptcha, cookies);
        captchaImg = captcha;
    }

    // call to function getCSRF for load the login page
    private static void loadLogin(Map<String, String> cookies)
            throws IOException {
        getCSRF(urlLogin, cookies);
    }

    // save cookies and call to function getCSRF for load the login page
    public void preLogin() throws IOException {
        cookies = Net.getCookies(urlLogin);
        loadLogin(cookies);
    }

    // Constructor
    public UserPortal() {

    }

    // reload the user info
    public void reloadUserInfo(Map<String, String> cookies)
            throws IOException {
        String urlUserInfo = "https://www.portal.nauta.cu/useraaa/user_info";
        page = Net.connection(urlUserInfo, cookies).get();

        // finding errors
        status.clear();
        status.put("function", "reloadUserInfo");
        if (findError(page, "UP").size() != 0) {
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", "SUCCESS :: reload user info");
            getUserInfo();
        }
    }

    // Login an account in user portal and load the user info
    public void login(String userName, String password, String captchaCode,
                      Map<String, String> cookies) throws IOException {
        Map<String, String> dataMap = new HashMap<>();

        dataMap.put("btn_submit", "");
        dataMap.put("captcha", captchaCode);
        dataMap.put("csrf", csrf);
        dataMap.put("login_user", userName);
        dataMap.put("password_user", password);
        page = Net.connection(urlLogin, cookies).data(dataMap).post();

        // finding errors
        status.clear();
        status.put("function", "login");
        if (findError(page, "UP").size() != 0) {
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", "SUCCESS :: login");
            getUserInfo();
        }
    }

    // recharge account credit
    public void recharge(String rechargeCode, Map<String, String> cookies)
            throws IOException {

        String urlRecharge = "https://www.portal.nauta.cu/useraaa/recharge_account";
        getCSRF(urlRecharge, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("recharge_code", rechargeCode);
        dataMap.put("btn_submit", "");
        page = Net.connection(urlRecharge, cookies, dataMap).post();

        // finding errors
        status.clear();
        status.put("function", "recharge");
        if (findError(page, "UP").size() != 0) {
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", findSuccess(page));
            getUserInfo();
        }
    }

    // transfer credit to other account
    public void transfer(String mountToTransfer, String password, String accountToTransfer,
                         Map<String, String> cookies) throws IOException {

        String urlTransfer = "https://www.portal.nauta.cu/useraaa/transfer_balance";
        getCSRF(urlTransfer, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("tranfer", mountToTransfer);
        dataMap.put("password_user", password);
        dataMap.put("id_cuenta", accountToTransfer);
        dataMap.put("action", "checkdata");

        if (creditToInt(credit) == 0) {
            // set error
            errors.clear();
            errors.add("Usted no tiene saldo en la cuenta. Por favor recargue.");
            status.clear();
            status.put("function", "transfer");
            status.put("status", "error");
            status.put("msg", errors);
        } else if (creditToInt(credit) == Integer.parseInt(mountToTransfer.replace(",", "")) ||
                creditToInt(credit) > Integer.parseInt(mountToTransfer.replace(",", ""))) {
            page = Net.connection(urlTransfer, cookies, dataMap).post();

            // finding errors
            status.clear();
            status.put("function", "transfer");
            if (findError(page, "UP").size() != 0) {
                errors = findError(page, "UP");
                status.put("status", "error");
                status.put("msg", errors);
            } else {
                status.put("status", "success");
                status.put("msg", findSuccess(page));
                getUserInfo();
            }
        } else {
            // set error
            errors.clear();
            errors.add("Su saldo es inferior a la cantidad que quiere transferir.");
            status.clear();
            status.put("function", "transfer");
            status.put("status", "error");
            status.put("msg", errors);
        }
    }

    // change account password
    public void changePassword(String oldPassword, String newPassword, Map<String, String> cookies)
            throws IOException {

        String urlChangePassword = "https://www.portal.nauta.cu/useraaa/change_password";
        getCSRF(urlChangePassword, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("old_password", oldPassword);
        dataMap.put("new_password", newPassword);
        dataMap.put("repeat_new_password", newPassword);
        dataMap.put("btn_submit", "");
        page = Net.connection(urlChangePassword, cookies, dataMap).post();

        // finding errors
        status.clear();
        status.put("function", "changePassword");
        if (findError(page, "UP").size() != 0) {
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", findSuccess(page));
            getUserInfo();
        }
    }

    // changed email account password
    public void changeEmailPassword(String oldPassword, String newPassword, Map<String, String> cookies)
            throws IOException {

        String urlChangePassword = "https://www.portal.nauta.cu/email/change_password";
        getCSRF(urlChangePassword, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("old_password", oldPassword);
        dataMap.put("new_password", newPassword);
        dataMap.put("repeat_new_password", newPassword);
        dataMap.put("btn_submit", "");
        page = Net.connection(urlChangePassword, cookies, dataMap).post();

        // finding errors
        status.clear();
        status.put("function", "changeEmailAccount");
        if (findError(page, "UP").size() != 0) {
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", findSuccess(page));
            getUserInfo();
        }
    }

    // return connections list on the year-month selected
    public List<Connection> getConnections(int year, int month,
                                           Map<String, String> cookies) throws IOException {
        List<Connection> connections = new ArrayList<>();
        String yearMonth = buildYearMonth(year, month);

        String urlServiceDetail = "https://www.portal.nauta.cu/useraaa/service_detail";
        getCSRF(urlServiceDetail, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("year_month", yearMonth);
        dataMap.put("list_type", "service_detail");

        String urlServiceDetailList = "https://www.portal.nauta.cu/useraaa/service_detail_list/" +
                yearMonth;
        String urlServiceDetailSummary = "https://www.portal.nauta.cu/useraaa/service_detail_summary";
        page = Net.connection(urlServiceDetailList, cookies, dataMap).post();
        Element tableOperationList = Utils.getTableOperationList(page);
        try {
            Elements operationList = Utils.getOperationList(tableOperationList);
            for (Element connection_ : operationList) {
                Elements _connection = connection_.select("td");
                connections.add(new Connection(_connection.get(0).text(),
                        _connection.get(1).text(), _connection.get(2).text(),
                        _connection.get(3).text(), _connection.get(4).text(),
                        _connection.get(5).text()));
            }
        } catch (NullPointerException ignored) {

        }

        if (connections.size() == 0) {
            page = Net.connection(urlServiceDetailSummary, cookies, dataMap).post();

            // finding errors
            status.clear();
            status.put("function", "getConnections");
            if (findError(page, "UP").size() != 0) {
                errors = findError(page, "UP");
                status.put("status", "error");
                status.put("msg", errors);
            } else {
                status.put("status", "success");
                status.put("msg", "SUCCESS :: get connections");
                getUserInfo();
            }
        }

        return connections;
    }

    // return recharges list on the year-month selected
    public List<Recharge> getRecharges(int year, int month,
                                       Map<String, String> cookies) throws IOException {
        List<Recharge> recharges = new ArrayList<>();
        String yearMonth = buildYearMonth(year, month);

        String urlRechargeDetail = "https://www.portal.nauta.cu/useraaa/recharge_detail/";
        getCSRF(urlRechargeDetail, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("year_month", yearMonth);
        dataMap.put("list_type", "recharge_detail");

        String urlRechargeDetailList = "https://www.portal.nauta.cu/useraaa/recharge_detail_list/" +
                yearMonth;
        String urlRechargeDetailSummary = "https://www.portal.nauta.cu/useraaa/recharge_detail_summary/";
        page = Net.connection(urlRechargeDetailList, cookies, dataMap).post();
        Element tableOperationList = Utils.getTableOperationList(page);
        try {
            Elements operationList = Utils.getOperationList(tableOperationList);
            for (Element recharge_ : operationList) {
                Elements _recharge = recharge_.select("td");
                recharges.add(new Recharge(_recharge.get(0).text(),
                        _recharge.get(1).text(), _recharge.get(2).text(),
                        _recharge.get(3).text()));
            }
        } catch (NullPointerException ignored) {

        }

        if (recharges.size() == 0) {
            page = Net.connection(urlRechargeDetailSummary, cookies, dataMap).post();

            // finding errors
            status.clear();
            status.put("function", "getRecharges");
            if (findError(page, "UP").size() != 0) {
                errors = findError(page, "UP");
                status.put("status", "error");
                status.put("msg", errors);
            } else {
                status.put("status", "success");
                status.put("msg", "SUCCESS :: get recharges");
                getUserInfo();
            }
        }

        return recharges;
    }

    // return transfers list on the year-month selected
    public List<Transfer> getTransfers(int year, int month, Map<String, String> cookies)
            throws IOException {
        List<Transfer> transfers = new ArrayList<>();
        String yearMonth = buildYearMonth(year, month);

        String urlTransferDetail = "https://www.portal.nauta.cu/useraaa/transfer_detail/";
        getCSRF(urlTransferDetail, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("year_month", yearMonth);
        dataMap.put("list_type", "recharge_detail");

        String urlTransferDetailList = "https://www.portal.nauta.cu/useraaa/transfer_detail_list/" +
                yearMonth;
        String urlTransferDetailSummary = "https://www.portal.nauta.cu/useraaa/transfer_detail_summary/";
        page = Net.connection(urlTransferDetailList, cookies, dataMap).post();
        Element tableOperationList = Utils.getTableOperationList(page);
        try {
            Elements operationList = Utils.getOperationList(tableOperationList);
            for (Element transfer_ : operationList) {
                Elements _transfer = transfer_.select("td");
                transfers.add(new Transfer(_transfer.get(0).text(),
                        _transfer.get(1).text(), _transfer.get(2).text()));
            }
        } catch (NullPointerException ignored) {

        }

        if (transfers.size() == 0) {
            page = Net.connection(urlTransferDetailSummary, cookies, dataMap).post();

            // finding errors
            status.clear();
            status.put("function", "getTransfers");
            if (findError(page, "UP").size() != 0) {
                errors = findError(page, "UP");
                status.put("status", "error");
                status.put("msg", errors);
            } else {
                status.put("status", "success");
                status.put("msg", "SUCCESS :: get transfers");
                getUserInfo();
            }
        }

        return transfers;
    }

    // logout
    public void logout(Map<String, String> cookies) throws IOException {
        String urlLogout = "https://www.portal.nauta.cu/user/logout";
        page = Net.connection(urlLogout, cookies).get();

        // finding errors
        status.clear();
        status.put("function", "logout");
        if (findError(page, "UP").size() != 0) {
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", "SUCCESS :: logout");
            getUserInfo();
        }
    }

    // load user info
    private void getUserInfo() {
        Element cardPanel = Utils.getCardPanel(page);
        Elements userTemp = cardPanel.select("div.m6");
        for (Element info : userTemp) {
            String temp = info.select("h5").text();
            switch (temp) {
                case "Usuario":
                    // Assigning userName value
                    userName = info.select("p").text();
                    break;
                case "Fecha de bloqueo":
                    // Assigning blockDate value
                    blockDate = info.select("p").text();
                    break;
                case "Fecha de eliminación":
                    // Assigning delDate value
                    delDate = info.select("p").text();
                    break;
                case "Tipo de cuenta":
                    // Assigning accountType value
                    accountType = info.select("p").text();
                    break;
                case "Tipo de servicio":
                    // Assigning serviceType value
                    serviceType = info.select("p").text();
                    break;
                case "Saldo disponible":
                    // Assigning credit value
                    credit = info.select("p").text();
                    break;
                case "Tiempo disponible de la cuenta":
                    // Assigning time value
                    time = info.select("p").text();
                    break;
                case "Cuenta de correo":
                    // Assigning mailAccount value
                    mailAccount = info.select("p").text();
                    break;
            }
        }
    }

}
