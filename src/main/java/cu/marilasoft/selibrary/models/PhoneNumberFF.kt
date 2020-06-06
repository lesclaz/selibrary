package cu.marilasoft.selibrary.models

import com.google.gson.JsonParser
import cu.marilasoft.selibrary.Net
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.Constants
import cu.marilasoft.selibrary.utils.OperationException
import org.jsoup.nodes.Element
import java.io.IOException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class PhoneNumberFF(private val element: Element, private val subscriber: String) {

    val status = HashMap<String, String>()
    val title: String
        get() {
            return element.select("h4").first().text()
        }
    val phoneNumber: String
        get() {
            return element.select("input[type=\"tel\"]").first().attr("value")
        }
    val textAdd: String
        get() {
            return element.select("a[id=\"btn-add-ph1\"]").text()
        }
    val textChange: String
        get() {
            return element.select("a[id=\"btn-change-ph1\"]").text()
        }
    val textDelete: String
        get() {
            return element.select("a[id=\"btn-delete-ph1\"]").text()
        }

    @Throws(IOException::class, CommunicationException::class, OperationException::class)
    fun add(phoneNumber: String, cookies: MutableMap<String, String>) {
        try {
            val dataMap = HashMap<String, String>()
            dataMap["numberToAdd"] = phoneNumber
            dataMap["subscriber"] = subscriber
            dataMap["format"] = "jsonp"
            val jsonObject = JsonParser().parse(Net.connection(MCP_PHONE_NUMBER_FF_ADD_URL,
                    cookies, dataMap, false).ignoreContentType(true).get().text()).asJsonObject
            val responseCode = jsonObject["responseCode"]
            status["code"] = responseCode.asString
            status["responseMessage"] = jsonObject["responseMessage"].asString
            if (responseCode.toString() == "124") {
                throw OperationException("$responseCode: Debajo del balance mínimo.")
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class, OperationException::class)
    fun change(phoneNumber: String, cookies: MutableMap<String, String>) {
        try {
            val dataMap = HashMap<String, String>()
            dataMap["numberToAdd"] = phoneNumber
            dataMap["numberToDelete"] = this.phoneNumber
            dataMap["subscriber"] = subscriber
            dataMap["format"] = "jsonp"
            val jsonObject = JsonParser().parse(Net.connection(MCP_PHONE_NUMBER_FF_CHANGE_URL,
                    cookies, dataMap, false).ignoreContentType(true).get().text()).asJsonObject
            val responseCode = jsonObject["responseCode"]
            status["code"] = responseCode.asString
            status["responseMessage"] = jsonObject["responseMessage"].asString
            if (responseCode.toString() == "124") {
                throw OperationException("$responseCode: Debajo del balance mínimo.")
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class, OperationException::class)
    fun delete(cookies: MutableMap<String, String>) {
        try {
            val dataMap = HashMap<String, String>()
            dataMap["numberToDelete"] = phoneNumber
            dataMap["subscriber"] = subscriber
            dataMap["format"] = "jsonp"
            val jsonObject = JsonParser().parse(Net.connection(MCP_PHONE_NUMBER_FF_DELETE_URL,
                    cookies, dataMap, false).ignoreContentType(true).get().text()).asJsonObject
            val responseCode = jsonObject["responseCode"]
            status["code"] = responseCode.asString
            status["responseMessage"] = jsonObject["responseMessage"].asString
            if (responseCode.toString() == "124") {
                throw OperationException("$responseCode: Debajo del balance mínimo.")
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    companion object {
        const val MCP_PHONE_NUMBER_FF_ADD_URL = "https://mi.cubacel.net:8443/AirConnector/rest/AirConnect/addFAFNumber"
        const val MCP_PHONE_NUMBER_FF_CHANGE_URL = "https://mi.cubacel.net:8443/AirConnector/rest/AirConnect/changeFAFNumber"
        const val MCP_PHONE_NUMBER_FF_DELETE_URL = "https://mi.cubacel.net:8443/AirConnector/rest/AirConnect/deleteFAFNumber"
    }
}