package cu.marilasoft.selibrary.utils

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.*


fun updateCookies(
        cookies: MutableMap<String, String>,
        cookiesUpdate: MutableMap<String, String>
): MutableMap<String, String> {
    val keys = cookies.keys
    for (key in keys) {
        cookiesUpdate[key] = cookies[key].toString()
    }
    return cookiesUpdate
}

fun creditToInt(credit: String): Int {
    return credit.replace("$", "").replace(" CUC", "").replace(",", "").toInt()
}

fun getOperationList(tableConnectionList: Element): Elements {
    val operations = tableConnectionList.select("tr")
    operations.removeAt(0)
    return operations
}

fun buildYearMonth(year: Int, month: Int): String {
    return if (month <= 9) {
        "$year-0$month"
    } else {
        "$year-$month"
    }
}

fun findError(page: Document, _type: String): List<String>? {
    var replaceText: String? = null
    var replaceTextOne: String? = null
    var replaceTextTwo: String? = null
    val errors: MutableList<String> = ArrayList()
    if (_type == "IP") {
        replaceText = "\r\n       \talert"
        replaceTextOne = "\r\n       \talert(\""
        replaceTextTwo = "\");\r\n   \t"
    } else if (_type.equals("UP", ignoreCase = true)) {
        replaceText = "toastr.error"
        replaceTextOne = "toastr.error('"
        replaceTextTwo = "');"
    }
    val lastScript: Element = page.select("script[type='text/javascript']")
            .last()
    assert(replaceText != null)
    if (replaceText?.let { lastScript.data().startsWith(it) }!!) {
        val data: Document = Jsoup.parse(replaceTextOne?.let {
            replaceTextTwo?.let { it1 ->
                lastScript.data()
                        .replace(it, "")
                        .replace(it1, "")
            }
        })
        if (_type == "IP") {
            errors.add(data.text())
            return errors
        }
        val error: Element = data.select("li.msg_error").first()
        if (error.text().startsWith("Se han detectado algunos errores.")) {
            val subMessages: Elements = data.select("li.sub-message")
            for (sub_message in subMessages) {
                errors.add(sub_message.text())
            }
        } else {
            errors.add(error.text())
        }
    }
    return errors
}

fun getSessionParameters(page: Document): MutableMap<String, Any?> {
    val sessionParameters: MutableMap<String, Any?> = HashMap()
    var str: String? = null
    for (wholeData in page.getElementsByTag("script").first().dataNodes()) {
        str = wholeData.wholeData
    }
    if (str != null) {
        val updateAvailableTimeParameters = getUpdateAvailableTimeParameters(str)
        val logoutParameters = getLogoutParameters(str)
        sessionParameters["updateTimeUrl"] = updateAvailableTimeParameters[0]
        sessionParameters["CSRFHW"] = updateAvailableTimeParameters[1]
        sessionParameters["Op"] = updateAvailableTimeParameters[2]
        sessionParameters["Op1"] = updateAvailableTimeParameters[3]
        sessionParameters["Op2"] = updateAvailableTimeParameters[4]
        sessionParameters["ACTION_LOGOUT"] = logoutParameters[0]
        sessionParameters["ATTRIBUTE_UUID"] = logoutParameters[1]
        sessionParameters["logoutwlanuserip"] = logoutParameters[2]
        sessionParameters["logoutssid"] = logoutParameters[3]
        sessionParameters["LOGGER_ID"] = logoutParameters[4]
        sessionParameters["domain"] = logoutParameters[5]
        sessionParameters["username"] = logoutParameters[6]
        sessionParameters["wlanacname"] = logoutParameters[7]
        sessionParameters["wlanmac"] = logoutParameters[8]
        sessionParameters["remove"] = logoutParameters[9]
    }
    return sessionParameters
}

fun getUpdateAvailableTimeParameters(str: String): Array<String?> {
    var substring: String
    var indexOf: Int
    val parameters = arrayOfNulls<String>(5)
    var indexOf2 = str.indexOf("/EtecsaQueryServlet?CSRFHW=")
    if (indexOf2 != -1) {
        substring = str.substring(indexOf2)
        indexOf = substring.indexOf("\"")
        if (indexOf != -1) {
            parameters[0] = substring.substring(0, indexOf)
        }
    }
    indexOf2 = parameters[0]!!.indexOf("CSRFHW=")
    if (indexOf2 != -1) {
        substring = parameters[0]!!.substring(indexOf2)
        indexOf = substring.indexOf("&")
        if (indexOf != -1) {
            parameters[1] = substring.substring(7, indexOf)
        }
    }
    indexOf2 = parameters[0]!!.indexOf("op=")
    if (indexOf2 != -1) {
        substring = parameters[0]!!.substring(indexOf2)
        indexOf = substring.indexOf("&")
        if (indexOf != -1) {
            parameters[2] = substring.substring(3, indexOf)
        }
    }
    indexOf2 = parameters[0]!!.indexOf("op1=")
    if (indexOf2 != -1) {
        substring = parameters[0]!!.substring(indexOf2)
        indexOf = substring.indexOf("&")
        if (indexOf != -1) {
            parameters[3] = substring.substring(4, indexOf)
        }
    }
    indexOf2 = parameters[0]!!.indexOf("op2=")
    if (indexOf2 != -1) {
        parameters[4] = parameters[0]!!.substring(indexOf2).substring(4)
    }
    return parameters
}

fun getLogoutParameters(str: String): Array<String?> {
    var substring: String
    var indexOf: Int
    val parameters = arrayOfNulls<String>(10)
    var indexOf2 = str.indexOf("\"GET\", \"")
    if (indexOf2 != -1) {
        substring = str.substring(indexOf2 + 8)
        indexOf = substring.indexOf("\"")
        if (indexOf != -1) {
            parameters[0] = substring.substring(0, indexOf)
        }
    }
    indexOf2 = str.indexOf("ATTRIBUTE_UUID=")
    if (indexOf2 != -1) {
        substring = str.substring(indexOf2 + 15)
        indexOf = substring.indexOf("\"")
        if (indexOf != -1) {
            parameters[1] = substring.substring(0, indexOf)
        }
    }
    indexOf2 = str.indexOf("+ \"&wlanuserip=")
    if (indexOf2 != -1) {
        substring = str.substring(indexOf2 + 15)
        indexOf = substring.indexOf("\"")
        if (indexOf != -1) {
            parameters[2] = substring.substring(0, indexOf)
        }
    }
    parameters[3] = ""
    indexOf2 = str.indexOf("+ \"&loggerId=")
    if (indexOf2 != -1) {
        substring = str.substring(indexOf2 + 13)
        indexOf = substring.indexOf("\"")
        if (indexOf != -1) {
            parameters[4] = substring.substring(0, indexOf)
        }
    }
    parameters[5] = ""
    indexOf2 = str.indexOf("+ \"&username=")
    if (indexOf2 != -1) {
        substring = str.substring(indexOf2 + 13)
        indexOf = substring.indexOf("\"")
        if (indexOf != -1) {
            parameters[6] = substring.substring(0, indexOf)
        }
    }
    parameters[0] = "https://secure.etecsa.net:8443/${parameters[0].toString()}ATTRIBUTE_UUID=${parameters[1]}" +
            "&wlanuserip=${parameters[2]}&ssid=${parameters[3]}&loggerId=${parameters[4]}&domain=${parameters[5]}" +
            "&username=${parameters[6]}&wlanacname=&wlanmac=&remove=1}"
    return parameters
}