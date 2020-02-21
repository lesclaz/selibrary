package cu.marilasoft.selibrary

import cu.marilasoft.selibrary.Net.connection
import cu.marilasoft.selibrary.Net.getCookies
import cu.marilasoft.selibrary.utils.LoginException
import cu.marilasoft.selibrary.utils.findError
import cu.marilasoft.selibrary.utils.getSessionParameters
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap


interface CaptivePortal {
    var cookies: MutableMap<String, String>
        get() = mCookies
        set(value) {
            mCookies = value
        }

    val wLanUserIp: String
        get() = page.select("input[name=\"wlanuserip\"]").first()
                .attr("value")
    val wLanAcName: String
        get() = page.select("input[name=\"wlanacname\"]").first()
                .attr("value")
    val wLanMac: String
        get() = page.select("input[name=\"wlanmac\"]").first()
                .attr("value")
    val firstUrl: String
        get() = page.select("input[name=\"firsturl\"]").first()
                .attr("value")
    val SSId: String
        get() = page.select("input[name=\"ssid\"]").first()
                .attr("value")
    val userType: String
        get() = page.select("input[name=\"usertype\"]").first()
                .attr("value")
    val gotoPage: String
        get() = page.select("input[name=\"gotopage\"]").first()
                .attr("value")
    val successPage: String
        get() = page.select("input[name=\"successpage\"]").first()
                .attr("value")
    val loggerId: String
        get() = page.select("input[name=\"loggerId\"]").first()
                .attr("value")
    val lang: String
        get() = page.select("input[name=\"lang\"]").first()
                .attr("value")
    val CSRFHW: String
        get() = page.select("input[name=\"CSRFHW\"]").first()
                .attr("value")
    val sessionParameters: Map<String, Any?>?
        get() = getSessionParameters(page)

    @Throws(IOException::class)
    private fun getInfoLogin(cookies: Map<String, String>) {
        page = connection(CP_BASE_URL, cookies = cookies).get()
    }

    @Throws(IOException::class)
    fun preLogin() {
        mCookies = getCookies(CP_BASE_URL)
        getInfoLogin(mCookies)
    }

    @Throws(IOException::class)
    fun login(userName: String, password: String, cookies: Map<String, String>, dataMap: MutableMap<String, String>) {
        dataMap["username"] = userName
        dataMap["password"] = password
        page = connection(CP_ACTION_LOGIN_URL, dataMap, cookies).post()
        if (findError(page, "IP")!!.isNotEmpty()) {
            val errors = findError(page, "IP")
            throw LoginException(errors.toString())
        }
    }

    @Throws(IOException::class)
    fun updateAvailableTime(updateTimeUrl: String, cookies: Map<String, String>): String? {
        return connection(CP_BASE_URL + updateTimeUrl, cookies = cookies).get().text()
    }

    @Throws(IOException::class)
    fun logout(logoutUrl: String, cookies: Map<String, String>) {
        page = connection(CP_BASE_URL + logoutUrl, cookies).get()
        if (page.text().replace("logoutcallback('", "")
                        .replace("');", "") != "SUCCESS") {
            throw LoginException("No se pudo cerrar la session")
        }
    }

    @Throws(IOException::class)
    fun getUserInfo(cookies: Map<String, String>, dataMap: Map<String, String>): MutableMap<String, String> {
        page = connection(CP_USER_INFO_URL, dataMap, cookies).post()
        val trs: Elements = page.select("table[id=sessioninfo]").first().select("tr")
        val userInfo: MutableMap<String, String> = HashMap()
        userInfo["status"] = trs[0].select("td").last().text()
        userInfo["credit"] = trs[1].select("td").last().text()
        userInfo["expire"] = trs[2].select("td").last().text()
        userInfo["access_areas"] = trs[3].select("td").last().text()
        return userInfo
    }

    @Throws(IOException::class)
    fun getTermsOfUse(cookies: Map<String, String>, dataMap: Map<String, String>): List<String>? {
        page = connection(CP_TERMS_OF_USE_URL, dataMap, cookies).post()
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

        const val CP_BASE_URL = "https://secure.etecsa.net:8443/"
        const val CP_ACTION_LOGIN_URL = "https://secure.etecsa.net:8443//LoginServlet"
        const val CP_USER_INFO_URL = "https://secure.etecsa.net:8443/EtecsaQueryServlet"
        const val CP_TERMS_OF_USE_URL = "https://secure.etecsa.net:8443/nauta_etecsa/LoginURL/pc/pc_termsofuse.jsp"
    }
}