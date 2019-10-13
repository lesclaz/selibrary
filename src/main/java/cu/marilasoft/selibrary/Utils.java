package cu.marilasoft.selibrary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Utils {

    static int creditToInt(String credit) {
        return Integer.parseInt(credit.replace("$", "").replace(" CUC", "").replace(",", ""));
    }

    static Element getCardPanel(Document page) {

        return page.select("div.card-panel").first();
    }

    static Element getTableOperationList(Document page) {
        Element tableOperationList;
        tableOperationList = page.select("table.responsive-table").first();

        return tableOperationList;
    }

    static Elements getOperationList(Element tableConnectionList) {
        Elements operations = tableConnectionList.select("tr");
        operations.remove(0);

        return operations;
    }

    public static String[] splitString(String string, String separator) {
        String[] list;
        list = string.split(separator);
        return list;
    }

    static String buildYearMonth(int year, int month) {
        String yearMonth;
        if (month <= 9) {
            yearMonth = year + "-0" + month;
        } else {
            yearMonth = year + "-" + month;
        }
        return yearMonth;
    }

    static List<String> findError(Document page, String _type) {
        String replace_text = null, replace_text_one = null, replace_text_two = null;
        List<String> errors = new ArrayList<>();

        if (_type.equals("IP")) {
            replace_text = "\r\n       \talert";
            replace_text_one = "\r\n       \talert(\"";
            replace_text_two = "\");\r\n   \t";
        } else if (_type.equalsIgnoreCase("UP")) {
            replace_text = "toastr.error";
            replace_text_one = "toastr.error('";
            replace_text_two = "');";
        }

        Element lastScript = page.select("script[type='text/javascript']")
                .last();
        assert replace_text != null;
        if (lastScript.data().startsWith(replace_text)) {
            Document data = Jsoup.parse(lastScript.data()
                    .replace(replace_text_one, "")
                    .replace(replace_text_two, ""));
            if (_type.equals("IP")) {
                errors.add(data.text());
                return errors;
            }
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
        return errors;
    }

    static String findSuccess(Document page) {

        Element lastScript = page.select("script[type='text/javascript']")
                .last();
        if (lastScript.data().startsWith("toastr.success")) {
            Document data = Jsoup.parse(lastScript.data()
                    .replace("toastr.success('", "")
                    .replace("');", ""));
            Element success = data.select("li.msg_message").first();
            return success.text();
        }

        return null;
    }

    static Map<String, Object> getSessionParameters(Document page) {
        Map<String, Object> sessionParameters = new HashMap<>();
        String str = null;
        for (DataNode wholeData : page.getElementsByTag("script").first().dataNodes()) {
            str = wholeData.getWholeData();
        }
        if (str != null) {
            String[] updateAvailableTimeParameters = getUpdateAvailableTimeParameters(str);
            String[] logoutParameters = getLogoutParameters(str);
            sessionParameters.put("updateTimeUrl", updateAvailableTimeParameters[0]);
            sessionParameters.put("updateTimeCSRFHW", updateAvailableTimeParameters[1]);
            sessionParameters.put("updateTimeOp", updateAvailableTimeParameters[2]);
            sessionParameters.put("updateTimeOp1", updateAvailableTimeParameters[3]);
            sessionParameters.put("updateTimeOp2", updateAvailableTimeParameters[4]);
            sessionParameters.put("logoutUrl", logoutParameters[0]);
            sessionParameters.put("logoutATTRIBUTE_UUID", logoutParameters[1]);
            sessionParameters.put("logoutwlanuserip", logoutParameters[2]);
            sessionParameters.put("logoutssid", logoutParameters[3]);
            sessionParameters.put("logoutloggerId", logoutParameters[4]);
            sessionParameters.put("logoutdomain", logoutParameters[5]);
            sessionParameters.put("logoutusername", logoutParameters[6]);
            sessionParameters.put("logoutwlanacname", logoutParameters[7]);
            sessionParameters.put("logoutwlanmac", logoutParameters[8]);
            sessionParameters.put("logoutremove", logoutParameters[9]);
        }
        return sessionParameters;
    }

    private static String[] getUpdateAvailableTimeParameters(String str) {
        String substring;
        int indexOf;
        String[] parameters = new String[5];
        int indexOf2 = str.indexOf("/EtecsaQueryServlet?CSRFHW=");
        if (indexOf2 != -1) {
            substring = str.substring(indexOf2);
            indexOf = substring.indexOf("\"");
            if (indexOf != -1) {
                parameters[0] = substring.substring(0, indexOf);
            }
        }
        indexOf2 = parameters[0].indexOf("CSRFHW=");
        if (indexOf2 != -1) {
            substring = parameters[0].substring(indexOf2);
            indexOf = substring.indexOf("&");
            if (indexOf != -1) {
                parameters[1] = substring.substring(7, indexOf);
            }
        }
        indexOf2 = parameters[0].indexOf("op=");
        if (indexOf2 != -1) {
            substring = parameters[0].substring(indexOf2);
            indexOf = substring.indexOf("&");
            if (indexOf != -1) {
                parameters[2] = substring.substring(3, indexOf);
            }
        }
        indexOf2 = parameters[0].indexOf("op1=");
        if (indexOf2 != -1) {
            substring = parameters[0].substring(indexOf2);
            indexOf = substring.indexOf("&");
            if (indexOf != -1) {
                parameters[3] = substring.substring(4, indexOf);
            }
        }
        indexOf2 = parameters[0].indexOf("op2=");
        if (indexOf2 != -1) {
            parameters[4] = parameters[0].substring(indexOf2).substring(4);
        }
        return parameters;
    }

    private static String[] getLogoutParameters(String str) {
        String substring;
        int indexOf;
        String[] parameters = new String[10];
        int indexOf2 = str.indexOf("\"GET\", \"");
        if (indexOf2 != -1) {
            substring = str.substring(indexOf2 + 8);
            indexOf = substring.indexOf("\"");
            if (indexOf != -1) {
                parameters[0] = substring.substring(0, indexOf);
            }
        }
        indexOf2 = str.indexOf("ATTRIBUTE_UUID=");
        if (indexOf2 != -1) {
            substring = str.substring(indexOf2 + 15);
            indexOf = substring.indexOf("\"");
            if (indexOf != -1) {
                parameters[1] = substring.substring(0, indexOf);
            }
        }
        indexOf2 = str.indexOf("+ \"&wlanuserip=");
        if (indexOf2 != -1) {
            substring = str.substring(indexOf2 + 15);
            indexOf = substring.indexOf("\"");
            if (indexOf != -1) {
                parameters[2] = substring.substring(0, indexOf);
            }
        }
        parameters[3] = "";
        indexOf2 = str.indexOf("+ \"&loggerId=");
        if (indexOf2 != -1) {
            substring = str.substring(indexOf2 + 13);
            indexOf = substring.indexOf("\"");
            if (indexOf != -1) {
                parameters[4] = substring.substring(0, indexOf);
            }
        }
        parameters[5] = "";
        indexOf2 = str.indexOf("+ \"&username=");
        if (indexOf2 != -1) {
            substring = str.substring(indexOf2 + 13);
            indexOf = substring.indexOf("\"");
            if (indexOf != -1) {
                parameters[6] = substring.substring(0, indexOf);
            }
        }
        parameters[7] = "";
        parameters[8] = "";
        parameters[9] = "1";
        parameters[0] = parameters[0] + "ATTRIBUTE_UUID=" + parameters[1] + "&wlanuserip=" + parameters[2] + "&ssid=" + parameters[3] +
                "&loggerId=" + parameters[4] + "&domain=" + parameters[5] + "&username=" + parameters[6] + "&wlanacname=" +
                parameters[7] + "&wlanmac=" + parameters[8] + "&remove=" + parameters[9];
        return parameters;
    }
}
