package cu.marilasoft.selibrary

import cu.marilasoft.selibrary.Net.connection
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.LoginException
import cu.marilasoft.selibrary.utils.findError
import cu.marilasoft.selibrary.utils.getSessionParameters
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


interface NautaClient {
    var cookies: MutableMap<String, String>
        get() = mCookies
        set(value) {
            mCookies = value
        }

    var dataMap: MutableMap<String, String>
        get() = mData
        set(value) {
            mData = value
        }

    var actionLogin: String
        get() = ACTION_LOGIN
        set(value) {
            ACTION_LOGIN = value
        }

    var actionLogout: String
        get() = ACTION_LOGOUT
        set(value) {
            ACTION_LOGOUT = value
        }

    private fun getInputs(form: Element): MutableMap<String, String> {
        val inputs = HashMap<String, String>()
        for (input in form.select("input[name]")) inputs[input.attr("name")] = input.attr("value")
        return inputs
    }

    @Throws(IOException::class)
    fun preLogin() {
        try {
            page = connection(url = "http://1.1.1.1").get()
            var form = page.selectFirst("form")
            ACTION_LOGIN = form.attr("action")
            mData = getInputs(form)

            val response = connection(ACTION_LOGIN, mData).execute()
            mCookies = response.cookies()
            page = response.parse()
            form = page.selectFirst("form[id=\"formulario\"]")
            ACTION_LOGIN = form.attr("action")
            mData = getInputs(form)
            mData.remove("Enviar")
            mData.remove("cancel")
            mData.remove("ayuda")
        } catch (e: CommunicationException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    fun login() {
        page = connection(url = ACTION_LOGIN, dataMap = mData, cookies = mCookies).post()
        if (findError(page, "IP")!!.isNotEmpty()) {
            val errors = findError(page, "IP")
            throw LoginException(errors.toString())
        } else {
            val parameters = getSessionParameters(page)
            val data = HashMap<String, String>()
            data["ATTRIBUTE_UUID"] = parameters["ATTRIBUTE_UUID"] as String
            data["loggerId"] = parameters["LOGGER_ID"] as String
            data["wlanuserip"] = mData["wlanuserip"]!!
            data["username"] = mData["username"]!!
            mData = data
            ACTION_LOGOUT = parameters["ACTION_LOGOUT"] as String
        }
    }

    @Throws(IOException::class)
    fun getUserTime(): String? {
        mData["op"] = "getLeftTime"
        return connection(url = "https://secure.etecsa.net:8443/EtecsaQueryServlet",
                dataMap = mData).post().text()
    }

    @Throws(IOException::class)
    fun logout() {
        page = connection(url = ACTION_LOGOUT, cookies = mCookies).get()
        if (page.text().replace("logoutcallback('", "")
                        .replace("');", "") != "SUCCESS") {
            throw LoginException("No se pudo cerrar la session")
        }
    }

    @Throws(IOException::class)
    fun getUserInfo(): MutableMap<String, String> {
        page = connection(url = CP_USER_INFO_URL, dataMap = mData, cookies = mCookies).post()
        val trs: Elements = page.select("table[id=sessioninfo]").first().select("tr")
        val userInfo: MutableMap<String, String> = HashMap()
        userInfo["status"] = trs[0].select("td").last().text()
        userInfo["credit"] = trs[1].select("td").last().text()
        userInfo["expire"] = trs[2].select("td").last().text()
        userInfo["access_areas"] = trs[3].select("td").last().text()
        return userInfo
    }

    @Throws(IOException::class)
    fun getTermsOfUse(): List<String>? {
        page = connection(url = CP_TERMS_OF_USE_URL, dataMap = mData, cookies = mCookies).post()
        val ol: Element = page.select("ol[class=\"condiciones\"]").first()
        val lis: Elements = ol.select("li")
        val terms: MutableList<String> = ArrayList()
        for (term in lis) {
            terms.add(term.text())
        }
        return terms
    }

    companion object {
        var mCookies: MutableMap<String, String> = HashMap()
        private lateinit var page: Document
        private lateinit var ACTION_LOGIN: String
        private lateinit var ACTION_LOGOUT: String
        private lateinit var mData: MutableMap<String, String>

        const val CP_USER_INFO_URL = "https://secure.etecsa.net:8443/EtecsaQueryServlet"
        const val CP_TERMS_OF_USE_URL = "https://secure.etecsa.net:8443/nauta_etecsa/LoginURL/pc/pc_termsofuse.jsp"
    }
}