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

    private byte[] captchaImg;

    // Devuelve la imagen captcha en formato de bytes
    public byte[] captchaImg() {
        return captchaImg;
    }

    // Devuelve el contenido de la variable status
    public Map<Object, Object> status() {
        return status;
    }

    // Devuelve las cookies
    public Map<String, String> cookies() {
        return cookies;
    }

    // Carga el codigo csrf
    private static void getCSRF(String url, Map<String, String> cookies)
            throws IOException {
        page = Net.connection(url, cookies).get();
        Element input = page.select("input[name='csrf']").first();
        csrf = input.attr("value");
    }

    // Descarga la imagen captcha en formato de bytes
    public void loadCAPTCHA(Map<String, String> cookies)
            throws IOException {
        byte[] captcha;
        String urlCaptcha = "https://www.portal.nauta.cu/captcha/?";
        captcha = Net.getCaptcha(urlCaptcha, cookies);
        captchaImg = captcha;
    }

    // Llama a la funcion getCSRF
    private static void loadLogin(Map<String, String> cookies)
            throws IOException {
        getCSRF(urlLogin, cookies);
    }

    // Carga las cookies y llama a la funcion loadLogin
    public static void preLogin() throws IOException {
        cookies = Net.getCookies(urlLogin);
        loadLogin(cookies);
    }

    // Constructor
    public UserPortal() {

    }

    // Recarga la informacion del usuario
    public int reload_userInfo(Map<String, String> cookies)
            throws IOException {
        int ret = 0;
        String urlUserInfo = "https://www.portal.nauta.cu/useraaa/user_info";
        page = Net.connection(urlUserInfo, cookies).get();

        /* Se buscan errors en el proceso, y dependiendo del resultado de la busqueda
         * almacena el resultado de la ejecucion en la variable status */
        status.clear();
        if (findError(page, "UP").size() != 0) {
            ret = 1;
            errors.clear();
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", findSuccess(page));
        }
        return ret;
    }

    // Logea una cuenta en el portal y carga la informacion del usuario
    public int login(String userName, String password, String captchaCode,
                     Map<String, String> cookies) throws IOException {
        int ret = 0;
        Map<String, String> dataMap = new HashMap<>();

        dataMap.put("btn_submit", "");
        dataMap.put("captcha", captchaCode);
        dataMap.put("csrf", csrf);
        dataMap.put("login_user", userName);
        dataMap.put("password_user", password);
        page = Net.connection(urlLogin, cookies).data(dataMap).post();

        /* Se buscan errors en el proceso, y dependiendo del resultado de la busqueda
         * almacena el resultado de la ejecucion en la variable status */
        status.clear();
        if (findError(page, "UP").size() != 0) {
            ret = 1;
            errors.clear();
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", findSuccess(page));
        }

        return ret;
    }

    // Recarga la cuenta logeada
    public int recharge(String rechargeCode, Map<String, String> cookies)
            throws IOException {
        int ret = 0;

        String urlRecharge = "https://www.portal.nauta.cu/useraaa/recharge_account";
        getCSRF(urlRecharge, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("recharge_code", rechargeCode);
        dataMap.put("btn_submit", "");
        page = Net.connection(urlRecharge, cookies, dataMap).post();

        /* Se buscan errors en el proceso, y dependiendo del resultado de la busqueda
         * almacena el resultado de la ejecucion en la variable status */
        status.clear();
        if (findError(page, "UP").size() != 0) {
            ret = 1;
            errors.clear();
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", findSuccess(page));
        }

        return ret;
    }

    // Transfiere saldo hacia otra cuenta
    public int transfer(String mountToTransfer, String password, String accountToTransfer,
                        Map<String, String> cookies) throws IOException {
        int ret = 0;

        String urlTransfer = "https://www.portal.nauta.cu/useraaa/transfer_balance";
        getCSRF(urlTransfer, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("tranfer", mountToTransfer);
        dataMap.put("password_user", password);
        dataMap.put("id_cuenta", accountToTransfer);
        dataMap.put("action", "checkdata");
        page = Net.connection(urlTransfer, cookies, dataMap).post();

        /* Se buscan errors en el proceso, y dependiendo del resultado de la busqueda
         * almacena el resultado de la ejecucion en la variable status */
        status.clear();
        if (findError(page, "UP").size() != 0) {
            ret = 1;
            errors.clear();
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", findSuccess(page));
        }

        return ret;
    }

    // Cambia la contrasena de la cuenta logeada
    public int changePassword(String oldPassword, String newPassword, Map<String, String> cookies)
            throws IOException {
        int ret = 0;

        String urlChangePassword = "https://www.portal.nauta.cu/useraaa/change_password";
        getCSRF(urlChangePassword, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("old_password", oldPassword);
        dataMap.put("new_password", newPassword);
        dataMap.put("repeat_new_password", newPassword);
        dataMap.put("btn_submit", "");
        page = Net.connection(urlChangePassword, cookies, dataMap).post();

        /* Se buscan errors en el proceso, y dependiendo del resultado de la busqueda
         * almacena el resultado de la ejecucion en la variable status */
        status.clear();
        if (findError(page, "UP").size() != 0) {
            ret = 1;
            errors.clear();
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", findSuccess(page));
        }

        return ret;
    }

    // Cambia la contrasena de la cuenta de correo asociada a la cuenta logeada
    public int changeEmailPassword(String oldPassword, String newPassword, Map<String, String> cookies)
            throws IOException {
        int ret = 0;

        String urlChangePassword = "https://www.portal.nauta.cu/email/change_password";
        getCSRF(urlChangePassword, cookies);
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("csrf", csrf);
        dataMap.put("old_password", oldPassword);
        dataMap.put("new_password", newPassword);
        dataMap.put("repeat_new_password", newPassword);
        dataMap.put("btn_submit", "");
        page = Net.connection(urlChangePassword, cookies, dataMap).post();

        /* Se buscan errors en el proceso, y dependiendo del resultado de la busqueda
         * almacena el resultado de la ejecucion en la variable status */
        status.clear();
        if (findError(page, "UP").size() != 0) {
            ret = 1;
            errors.clear();
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", findSuccess(page));
        }

        return ret;
    }

    // Devuelve la lista de conexiones (del ano-mes especificado) de la cuenta logeada
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

            /* Se buscan errors en el proceso, y dependiendo del resultado de la busqueda
             * almacena el resultado de la ejecucion en la variable status */
            status.clear();
            if (findError(page, "UP").size() != 0) {
                errors.clear();
                errors = findError(page, "UP");
                status.put("status", "error");
                status.put("msg", errors);
            } else {
                status.put("status", "success");
                status.put("msg", findSuccess(page));
            }
        }

        return connections;
    }

    // Devuelve la lista de recargas (del ano-mes especificado) de la cuenta logeada
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

            /* Se buscan errors en el proceso, y dependiendo del resultado de la busqueda
             * almacena el resultado de la ejecucion en la variable status */
            status.clear();
            if (findError(page, "UP").size() != 0) {
                errors.clear();
                errors = findError(page, "UP");
                status.put("status", "error");
                status.put("msg", errors);
            } else {
                status.put("status", "success");
                status.put("msg", findSuccess(page));
            }
        }

        return recharges;
    }

    // Devuelve la lista de transferencias (del ano-mes especificado) de la cuenta logeada
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

            /* Se buscan errors en el proceso, y dependiendo del resultado de la busqueda
             * almacena el resultado de la ejecucion en la variable status */
            status.clear();
            if (findError(page, "UP").size() != 0) {
                errors.clear();
                errors = findError(page, "UP");
                status.put("status", "error");
                status.put("msg", errors);
            } else {
                status.put("status", "success");
                status.put("msg", findSuccess(page));
            }
        }

        return transfers;
    }

    // Cierra la session de la cuenta logeada
    public int logout(Map<String, String> cookies) throws IOException {
        int ret = 0;
        String urlLogout = "https://www.portal.nauta.cu/user/logout";
        page = Net.connection(urlLogout, cookies).get();

        /* Se buscan errors en el proceso, y dependiendo del resultado de la busqueda
         * almacena el resultado de la ejecucion en la variable status */
        status.clear();
        if (findError(page, "UP").size() != 0) {
            ret = 1;
            errors.clear();
            errors = findError(page, "UP");
            status.put("status", "error");
            status.put("msg", errors);
        } else {
            status.put("status", "success");
            status.put("msg", "La session fue  cerrada correctamente!");
        }

        return ret;
    }

    // Devuelve el nombre de usuario de la cuenta logeada
    public String userName() {
        String userName = null;

        Element cardPanel = Utils.getCardPanel(page);
        Elements userTemp = cardPanel.select("div.m6");
        for (Element info : userTemp) {
            String temp = info.select("h5").text();
            if (temp.equals("Usuario")) {
                userName = info.select("p").text();
                break;
            }
        }

        return userName;
    }

    // Devuelve la fecha de bloqueo de la cuenta logeada
    public String blockDate() {
        String blockDate = null;

        Element cardPanel = Utils.getCardPanel(page);
        Elements userTemp = cardPanel.select("div.m6");
        for (Element info : userTemp) {
            String temp = info.select("h5").text();
            if (temp.equals("Fecha de bloqueo")) {
                blockDate = info.select("p").text();
                break;
            }
        }

        return blockDate;
    }

    // Devuelve la fecha de eliminacion de la cuenta logeada
    public String delDate() {
        String delDate = null;

        Element cardPanel = Utils.getCardPanel(page);
        Elements userTemp = cardPanel.select("div.m6");
        for (Element info : userTemp) {
            String temp = info.select("h5").text();
            if (temp.equals("Fecha de eliminación")) {
                delDate = info.select("p").text();
                break;
            }
        }

        return delDate;
    }

    // Devuelve el tipo de la cuenta logeada
    public String accountType() {
        String accountType = null;

        Element cardPanel = Utils.getCardPanel(page);
        Elements userTemp = cardPanel.select("div.m6");
        for (Element info : userTemp) {
            String temp = info.select("h5").text();
            if (temp.equals("Tipo de cuenta")) {
                accountType = info.select("p").text();
                break;
            }
        }

        return accountType;
    }

    // Devuelve el tipo de servicio de la cuenta logeada
    public String serviceType() {
        String serviceType = null;

        Element cardPanel = Utils.getCardPanel(page);
        Elements userTemp = cardPanel.select("div.m6");
        for (Element info : userTemp) {
            String temp = info.select("h5").text();
            if (temp.equals("Tipo de servicio")) {
                serviceType = info.select("p").text();
                break;
            }
        }

        return serviceType;
    }

    // Devuelve el saldo disponible de la cuenta logeada
    public String credit() {
        String credit = null;

        Element cardPanel = Utils.getCardPanel(page);
        Elements userTemp = cardPanel.select("div.m6");
        for (Element info : userTemp) {
            String temp = info.select("h5").text();
            if (temp.equals("Saldo disponible")) {
                credit = info.select("p").text();
                break;
            }
        }

        return credit;
    }

    // Devuelve el tiempo disponible de la cuenta logeada
    public String time() {
        String time = null;

        Element cardPanel = Utils.getCardPanel(page);
        Elements userTemp = cardPanel.select("div.m6");
        for (Element info : userTemp) {
            String temp = info.select("h5").text();
            if (temp.equals("Tiempo disponible de la cuenta")) {
                time = info.select("p").text();
                break;
            }
        }

        return time;
    }

    // Devuelve la cuenta de correo vinculada a la cuenta logeada
    public String mailAccount() {
        String mailAccount = null;

        Element cardPanel = Utils.getCardPanel(page);
        Elements userTemp = cardPanel.select("div.m6");
        for (Element info : userTemp) {
            String temp = info.select("h5").text();
            if (temp.equals("Cuenta de correo")) {
                mailAccount = info.select("p").text();
                break;
            }
        }

        return mailAccount;
    }

}
