package cu.marilasoft.selibrary.models

import cu.marilasoft.selibrary.Net
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.Constants
import cu.marilasoft.selibrary.utils.OperationException
import org.jsoup.nodes.Element
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException


class FamilyAndFriends(private val element: Element, private val fnfValue: String) {
    val title: String
        get() {
            return element.select("h1[class=\"page_title_heading mTop10\"]").first().text()
        }
    val description: String
        get() {
            return element.select("div[class=\"col100\"]").first().select("div").first().text()
        }
    val subscriber: String
        get() {
            return element.getElementsByAttributeValue("id", "msisdnValue").first().attr("value")
        }
    val changesFree: Int
        get() {
            return Integer.parseInt(element.select("div[class=\"fnf_history_block\"]").first()
                    .select("div[class=\"col70\"]").first().select("span[class=\"field_value\"]")
                    .last().text())
        }
    val isSubscribe: Boolean
        get() {
            return fnfValue == "true"
        }
    val phoneNumbers: List<PhoneNumberFF>
        get() {
            val list = ArrayList<PhoneNumberFF>()
            for (element: Element in element.select("div[class=\"product_inner_block\"]")) {
                list.add(PhoneNumberFF(element, subscriber))
            }
            return list
        }

    @Throws(CommunicationException::class, OperationException::class)
    fun unsubscribe(cookies: MutableMap<String, String>) {
        try {
            val dataMap = HashMap<String, String>()
            dataMap["subscriber"] = subscriber
            Net.connection(MCP_UNSUBSCRIBE_FAF_URL, cookies, dataMap, false).get()
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    companion object {
        const val MCP_UNSUBSCRIBE_FAF_URL = "https://mi.cubacel.net:8443/AirConnector/rest/AirConnect/unSubscribeFAF"
    }
}