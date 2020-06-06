package cu.marilasoft.selibrary

import com.google.gson.JsonParser
import cu.marilasoft.selibrary.models.FamilyAndFriends
import cu.marilasoft.selibrary.models.ETECSAPackage
import cu.marilasoft.selibrary.models.Notice
import cu.marilasoft.selibrary.models.Product
import cu.marilasoft.selibrary.utils.*
import org.jsoup.Connection
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

interface CubacelClient {
    var cookies: MutableMap<String, String>
        get() = mCookies
        set(value) {
            mCookies = value
        }
    val urls: MutableMap<String, String>
        get() = urlsMCP

    private val myAccountDetailsBlock: Elements
        get() {
            return myAccountPage.select("div[class=\"myaccount_details_block\"]")
        }
    private val divsCol1a: Elements
        get() {
            return myAccountPage.select("div[class=\"col1a\"]")
        }
    private val divsCol2a: Elements
        get() {
            return myAccountPage.select("div[class=\"col2a\"]")
        }
    private val script: String
        get() {
            return myAccountPage.select("script").last().data()
        }

    val welcomeMessage: String?
        get() {
            return homePage.select("div[class=\"banner_bg_color mBottom20\"]").first()
                    .select("h2").text()
        }
    val userName: String?
        get() {
            return welcomeMessage?.replace("Bienvenido ", "")?.replace(" a MiCubacel", " ")
        }
    val phoneNumber: String?
        get() {
            for (div in myAccountDetailsBlock) {
                if (div.select("div[class=\"mad_row_header\"]").first()
                                .select("div[class=\"col1\"]").first()
                                .text().startsWith("Mi Cuenta")) {
                    return div.select("div[class=\"mad_row_footer\"]").first()
                            .select("div[class=\"col1\"]").first()
                            .select("span[class=\"cvalue\"]").first().text()
                }
            }
            return null
        }
    val credit: String?
        get() {
            for (div in myAccountDetailsBlock) {
                if (div.select("div[class=\"mad_row_header\"]").first()
                                .select("div[class=\"col1\"]").first()
                                .text().startsWith("Mi Cuenta")) {
                    return div.select("div[class=\"mad_row_header\"]").first()
                            .select("div[class=\"col2\"]").first()
                            .select("span[class=\"cvalue bold cuc-font\"]").first().text()
                }
            }
            return null
        }
    val expire: String?
        get() {
            for (div in myAccountDetailsBlock) {
                if (div.select("div[class=\"mad_row_header\"]").first()
                                .select("div[class=\"col1\"]").first()
                                .text().startsWith("Mi Cuenta")) {
                    return div.select("div[class=\"mad_row_footer\"]").first()
                            .select("div[class=\"col2\"]").first()
                            .select("span[class=\"cvalue\"]").first().text()
                }
            }
            return null
        }
    val creditBonus: String?
        get() {
            for (div in myAccountDetailsBlock) {
                if (div.select("div[class=\"mad_row_header\"]").first()
                                .select("div[class=\"col1\"]").isNotEmpty() &&
                        div.select("div[class=\"mad_row_header\"]").first()
                                .select("div[class=\"col1\"]").first()
                                .text().startsWith("Bono")) {

                    return div.select("div[class=\"mad_row_header\"]").first()
                            .select("div[class=\"col2\"]").first()
                            .select("span[class=\"cvalue bold cuc-font\"]").first().text()
                }
            }
            return null
        }
    val expireBonus: String?
        get() {
            for (div in myAccountDetailsBlock) {
                if (div.select("div[class=\"mad_row_header\"]").first()
                                .select("div[class=\"col1\"]").isNotEmpty() &&
                        div.select("div[class=\"mad_row_header\"]").first()
                                .select("div[class=\"col1\"]").first()
                                .text().startsWith("Bono")) {
                    return div.select("div[class=\"mad_row_footer\"]").first()
                            .select("div[class=\"col2\"]").first()
                            .select("span[class=\"cvalue\"]").first().text()
                }
            }
            return null
        }
    val date: String?
        get() {
            for (div in divsCol1a) {
                if (div.text().startsWith("Fecha del Adelanto: ")) {
                    return div.select("span[class=\"cvalue bold\"]").first().text()
                }
            }
            return null
        }
    val payableBalance: String?
        get() {
            for (div in divsCol2a) {
                if (div.text().startsWith("Saldo pendiente por pagar: ")) {
                    return div.select("span[class=\"cvalue bold cuc-font\"]").first().text()
                }
            }
            return null
        }
    val isActiveBonusServices: Boolean
        get() {
            val indexOf: Int
            var substring = ""
            var onOff: String
            var indexOf2 = script.indexOf("'false'; prop=")
            if (indexOf2 != -1) {
                substring = script.substring(indexOf2)
            }
            indexOf2 = substring.indexOf("prop=")
            if (indexOf2 != -1) {
                substring = substring.substring(indexOf2)
                indexOf = substring.indexOf(";")
                if (indexOf != -1) {
                    onOff = substring.substring(0, indexOf)
                    onOff = onOff.split("=".toRegex()).toTypedArray()[1].replace("'", "")
                    return onOff == "true"
                }
            }
            return false
        }
    val products: List<Product>
        get() {
            val list = ArrayList<Product>()
            for (element: Element in productsPage.select("div[class=\"product_inner_block\"]")) {
                list.add(Product(element))
            }
            return list
        }
    val news: List<Notice>
        get() {
            val temp = ArrayList<Notice>()
            temp.add(Notice(newsPage.select("div[class=\"carousel-inner\"]").first()
                    .select("div[class=\"item active\"]").first()))
            for (notice in newsPage.select("div[class=\"carousel-inner\"]").first()
                    .select("div[class=\"item\"]")) {
                temp.add(Notice(notice))
            }
            return temp
        }
    val familyAndFriends: FamilyAndFriends
        get() {
            return FamilyAndFriends(myAccountPage.select("div[id=\"familyAndFriends\"]").first()
                    .select("div[class=\"settings_block\"]").first(),
                    myAccountPage.select("div[id=\"fnfBlock\"]").first()
                            .select("input[id=\"fnfBlockValue\"]").first().attr("value"))
        }
    val buys: ArrayList<ETECSAPackage>
        get() {
            val buys = ArrayList<ETECSAPackage>()
            for (element in myAccountPage.select("div[class=\"mad_accordion_container\"]")) {
                for (jElement in element.select("div [id=\"multiAccordion1\"]")) {
                    for ((count, title) in jElement.select("h3[class=\"ac_block_title\"]").withIndex()) {
                        val ePackage = ETECSAPackage(jElement.select("div[class=\"ac_block\"]")[count],
                                true)
                        ePackage.title = title.text()
                        buys.add(ePackage)
                    }
                }
                for (jElement in element.select("div [id=\"multiAccordion\"]")) {
                    for ((count, title) in jElement.select("h3[class=\"ac_block_title\"]").withIndex()) {
                        val ePackage = ETECSAPackage(jElement.select("div[class=\"ac_block\"]")[count])
                        ePackage.title = title.text()
                        buys.add(ePackage)
                    }
                }
            }
            return buys
        }

    @Throws(IOException::class, CommunicationException::class)
    fun loadHomePage(cookies: MutableMap<String, String>?) {
        try {
            var response = Net.connection(url = Constants.MCP_BASE_URL, cookies = mCookies, verify = false).execute()
            currentPage = response.parse()
            var urlSpanish = ""
            val urls: Elements = currentPage.select("a[class=\"link_msdp langChange\"]")
            for (url in urls) {
                if (url.attr("id") == "spanishLanguage") {
                    urlSpanish = url.attr("href")
                }
            }
            mCookies = updateCookies(response.cookies(), mCookies)
            if (urlsMCP.isNotEmpty()) urlsMCP.clear()
            urlsMCP["home"] = Constants.MCP_BASE_URL + urlSpanish
            response = Net.connection(url = Constants.MCP_BASE_URL + urlSpanish, cookies = mCookies, verify = false).execute()
            currentPage = response.parse()
            val div = currentPage.select("div[class=\"collapse navbar-collapse navbar-main-collapse\"]").first()
            val lis = div.select("li")
            for (li in lis) {
                when (li.text()) {
                    "Ofertas" -> urlsMCP["offers"] = Constants.MCP_BASE_URL + li.select("a").first().attr("href")
                    "Productos" -> urlsMCP["products"] = Constants.MCP_BASE_URL + li.select("a").first().attr("href")
                    "Mi Cuenta" -> urlsMCP["myAccount"] = Constants.MCP_BASE_URL + li.select("a").first().attr("href")
                    "Soporte" -> urlsMCP["support"] = Constants.MCP_BASE_URL + li.select("a").first().attr("href")
                }
            }
            mCookies = updateCookies(response.cookies(), mCookies)
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(LoginException::class, CommunicationException::class)
    fun login(phoneNumber: String, password: String) {
        loadHomePage(null)
        mCookies = Net.connection(url = MCP_WELCOME_LOGIN_ES_URL, verify = false).execute().cookies()
        val dataMap: MutableMap<String, String> = HashMap()
        dataMap["language"] = "es_ES"
        dataMap["username"] = phoneNumber
        dataMap["password"] = password
        dataMap["uword"] = "step"
        try {
            val response = Net.connection(url = MCP_LOGIN_URL, dataMap = dataMap, cookies = mCookies, verify = false)
                    .method(Connection.Method.POST).execute()
            mCookies = response.cookies()
            currentPage = response.parse()
            if (currentPage.select("div[class=\"body_wrapper error_page\"]").first() != null) {
                val msg = currentPage.select("div[class=\"body_wrapper error_page\"]").first()
                        .select("div[class=\"welcome_login error_Block\"]").first()
                        .select("div[class=\"container\"]").first()
                        .select("b").text()
                throw LoginException(msg)
            } else {
                homePage = urlsMCP["home"]!!.let {
                    Net.connection(url = it, cookies = this.cookies, verify = false).get()
                }
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class)
    fun loadMyAccount(url: String?, loadHomePage: Boolean = false) {
        try {
            val urlAction: String = if (loadHomePage) {
                loadHomePage(mCookies)
                urlsMCP["myAccount"].toString()
            } else url!!
            myAccountPage = Net.connection(url = urlAction, cookies = mCookies, verify = false).get()
            urlsMCP["changeBonusServices"] = Constants.MCP_BASE_URL + myAccountPage
                    .select("form[id=\"toogle-internet\"]")
                    .first().attr("action")
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class)
    fun loadNews() {
        try {
            newsPage = Net.connection(ETECSA_HOME_PAGE_URL).get()
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class)
    fun changeBonusServices(isActiveBonusServices: Boolean, urlAction: String) {
        val dataMap: MutableMap<String, String> = HashMap()
        if (isActiveBonusServices) {
            dataMap["onoffswitchctm"] = "off"
        } else {
            dataMap["onoffswitch"] = "on"
            dataMap["onoffswitchctm"] = "on"
        }
        try {
            Net.connection(url = urlAction, dataMap = dataMap, cookies = mCookies, verify = false).post()
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class)
    fun loadProducts(urlAction: String) {
        try {
            productsPage = Net.connection(url = urlAction, cookies = mCookies, verify = false).get()
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class)
    fun resetPassword(phoneNumber: String) {
        try {
            mCookies.clear()
            var response = Net.connection(url = MCP_WELCOME_LOGIN_ES_URL, cookies = cookies, verify = false)
                    .execute()
            mCookies = updateCookies(response.cookies(), mCookies)
            response = Net.connection(url = MCP_FORGOT_URL, cookies = cookies, verify = false).execute()
            mCookies = updateCookies(response.cookies(), mCookies)
            val dataMap = HashMap<String, String>()
            dataMap["mobileNumber"] = phoneNumber
            dataMap["uword"] = "step"
            response = Net.connection(url = MCP_FORGOT_ACTION_URL, dataMap = dataMap, cookies = mCookies, verify = false)
                    .method(Connection.Method.POST).execute()
            mCookies = updateCookies(response.cookies(), mCookies)
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class, OperationException::class)
    fun completeResetPassword(code: String, newPassword: String) {
        try {
            val dataMap = HashMap<String, String>()
            dataMap["oneTimecode"] = code
            dataMap["newPassword"] = newPassword
            dataMap["cnewPassword"] = newPassword
            dataMap["uword"] = "step"
            val response = Net.connection(url = MCP_RESET_PASSWORD_URL, dataMap = dataMap, cookies = mCookies, verify = false)
                    .method(Connection.Method.POST).execute()
            currentPage = response.parse()
            if (currentPage.select("div[class=\"body_wrapper error_page\"]").first() != null) {
                val msg = currentPage.select("div[class=\"body_wrapper error_page\"]").first()
                        .select("div[class=\"welcome_login error_Block\"]").first()
                        .select("div[class=\"container\"]").first()
                        .select("b").first().text()
                throw OperationException(msg)
            } else {
                mCookies = updateCookies(response.cookies(), mCookies)
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class)
    fun signUp(phoneNumber: String, firstName: String, lastName: String, email: String) {
        try {
            val response = Net.connection(url = MCP_WELCOME_LOGIN_ES_URL, verify = false)
                    .execute()
            mCookies = updateCookies(response.cookies(), mCookies)
            Net.connection(url = MCP_SIGN_UP_URL, cookies = mCookies, verify = false).execute()
            val dataMap = HashMap<String, String>()
            dataMap["msisdn"] = phoneNumber
            dataMap["firstname"] = firstName
            dataMap["lastname"] = lastName
            dataMap["email"] = email
            dataMap["uword"] = "step"
            dataMap["agree"] = "on"
            Net.connection(url = MCP_SIGN_UP_ACTION_URL, dataMap = dataMap, cookies = mCookies, verify = false)
                    .method(Connection.Method.POST).execute()
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class, OperationException::class)
    fun verifyCode(code: String) {
        val dataMap = HashMap<String, String>()
        dataMap["username"] = code
        dataMap["uword"] = "step"
        val response = Net.connection(url = MCP_VERIFY_REGISTRATION_CODE_URL, dataMap = dataMap, cookies = mCookies, verify = false)
                .method(Connection.Method.POST).execute()
        currentPage = response.parse()
        if (currentPage.select("div[class=\"body_wrapper error_page\"]").first() != null) {
            try {
                val msg = currentPage.select("div[class=\"body_wrapper error_page\"]").first()
                        .select("div[class=\"welcome_login error_Block\"]").first()
                        .select("div[class=\"container\"]").first()
                        .select("b").first().text()
                throw OperationException(msg)
            } catch (e: NullPointerException) {
                val msg = currentPage.select("div[class=\"body_wrapper error_page\"]").first()
                        .select("div[class=\"welcome_login error_Block\"]").first()
                        .select("div[class=\"container\"]").first().text()
                throw OperationException(msg)
            }
        }
    }

    @Throws(IOException::class, CommunicationException::class, OperationException::class)
    fun completeSignUp(password: String) {
        try {
            val dataMap = HashMap<String, String>()
            dataMap["newPassword"] = password
            dataMap["cnewPassword"] = password
            dataMap["uword"] = "step"
            currentPage = Net.connection(url = MCP_REGISTER_PASSWORD_CREATION_URL, dataMap = dataMap, cookies = mCookies, verify = false)
                    .method(Connection.Method.POST).execute().parse()
            if (currentPage.select("div[class=\"body_wrapper error_page\"]").first() != null) {
                try {
                    val msg = currentPage.select("div[class=\"body_wrapper error_page\"]").first()
                            .select("div[class=\"welcome_login error_Block\"]").first()
                            .select("div[class=\"container\"]").first()
                            .select("b").first().text()
                    throw OperationException(msg)
                } catch (e: NullPointerException) {
                    val msg = currentPage.select("div[class=\"body_wrapper error_page\"]").first()
                            .select("div[class=\"welcome_login error_Block\"]").first()
                            .select("div[class=\"container\"]").first().text()
                    throw OperationException(msg)
                }
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
    fun loanMe(mount: String, subscriber: String) {
        val dataMap = HashMap<String, String>()
        dataMap["subscriber"] = subscriber
        dataMap["transactionAmount"] = mount
        val response = JsonParser().parse(Net.connection(url = MCP_LOAN_ME_URL, dataMap = dataMap, cookies = mCookies, verify = false)
                .ignoreContentType(true).get().text()).asJsonObject
        val responseCode = response["responseCode"]
        if (responseCode.toString() != "200") throw OperationException("${responseCode}: Usted no aplica!")
    }

    companion object {
        private lateinit var currentPage: Document
        private lateinit var homePage: Document
        private lateinit var myAccountPage: Document
        private lateinit var newsPage: Document
        private lateinit var productsPage: Document
        private var urlsMCP: MutableMap<String, String> = HashMap()
        private var mCookies: MutableMap<String, String> = HashMap()

        const val ETECSA_HOME_PAGE_URL = "http://www.etecsa.cu"

        const val MCP_LOGIN_URL = "https://mi.cubacel.net:8443/login/Login"
        const val MCP_WELCOME_LOGIN_ES_URL = "https://mi.cubacel.net:8443/login/jsp/welcome-login.jsp?language=es"
        const val MCP_FORGOT_URL = "https://mi.cubacel.net:8443/login/jsp/forgot-password.jsp"
        const val MCP_FORGOT_ACTION_URL = "https://mi.cubacel.net:8443/login/recovery/ForgotPassword"
        const val MCP_RESET_PASSWORD_URL = "https://mi.cubacel.net:8443/login/recovery/ResetPassword"
        const val MCP_SIGN_UP_URL = "https://mi.cubacel.net:8443/login/jsp/registerNew.jsp"
        const val MCP_SIGN_UP_ACTION_URL = "https://mi.cubacel.net:8443/login/NewUserRegistration"
        const val MCP_VERIFY_REGISTRATION_CODE_URL = "https://mi.cubacel.net:8443/login/VerifyRegistrationCode"
        const val MCP_REGISTER_PASSWORD_CREATION_URL = "https://mi.cubacel.net:8443/login/recovery/RegisterPasswordCreation"

        const val MCP_LOAN_ME_URL = "https://mi.cubacel.net:8443/AirConnector/rest/AirConnect/loanMe"
    }
}