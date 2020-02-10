package cu.marilasoft.selibrary

import cu.marilasoft.selibrary.Net.connection
import cu.marilasoft.selibrary.Net.getCaptcha
import cu.marilasoft.selibrary.Net.getCookies
import cu.marilasoft.selibrary.models.Connection
import cu.marilasoft.selibrary.models.Recharge
import cu.marilasoft.selibrary.models.Transfer
import cu.marilasoft.selibrary.utils.*
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.io.IOException
import java.net.UnknownHostException
import java.util.*
import javax.net.ssl.SSLHandshakeException


class UserPortal {
    private lateinit var csrf: String
    private lateinit var page: Document
    private lateinit var homePage: Document
    var cookies: Map<String, String>? = null
    var captchaImg: ByteArray? = null

    private val m6: Elements
        get() {
            return homePage.select("div.card-panel").first().select("div.m6")
        }
    val userName: String?
        get() {
            for (element in m6) {
                when (element.select("h5").text()) {
                    "Usuario" -> return element.select("p").text()
                }
            }
            return null
        }
    val blockDate: String?
        get() {
            for (element in m6) {
                when (element.select("h5").text()) {
                    "Fecha de bloqueo" -> return element.select("p").text()
                }
            }
            return null
        }
    val delDate: String?
        get() {
            for (element in m6) {
                when (element.select("h5").text()) {
                    "Fecha de eliminación" -> return element.select("p").text()
                }
            }
            return null
        }
    val accountType: String?
        get() {
            for (element in m6) {
                when (element.select("h5").text()) {
                    "Tipo de cuenta" -> return element.select("p").text()
                }
            }
            return null
        }
    val serviceType: String?
        get() {
            for (element in m6) {
                when (element.select("h5").text()) {
                    "Tipo de servicio" -> return element.select("p").text()
                }
            }
            return null
        }
    val credit: String?
        get() {
            for (element in m6) {
                when (element.select("h5").text()) {
                    "Saldo disponible" -> return element.select("p").text()
                }
            }
            return null
        }
    val mailAccount: String?
        get() {
            for (element in m6) {
                when (element.select("h5").text()) {
                    "Cuenta de correo" -> return element.select("p").text()
                }
            }
            return null
        }
    val time: String?
        get() {
            for (element in m6) {
                when (element.select("h5").text()) {
                    "Tiempo disponible de la cuenta" -> return element.select("p").text()
                }
            }
            return null
        }

    @Throws(IOException::class, CommunicationException::class)
    private fun getCSRF(url: String, cookies: Map<String, String>) {
        try {
            page = connection(url, cookies = cookies).get()
            val input: Element = page.select("input[name='csrf']").first()
            csrf = input.attr("value")
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class)
    fun loadCAPTCHA(cookies: Map<String, String>) {
        try {
            captchaImg = getCaptcha(UP_CAPTCHA_URL, cookies)
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class)
    private fun loadLogin(cookies: Map<String, String>) {
        getCSRF(UP_LOGIN_URL, cookies)
    }

    @Throws(IOException::class, CommunicationException::class)
    fun preLogin() {
        try {
            cookies = getCookies(UP_LOGIN_URL)
            loadLogin(cookies!!)
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class, CommunicationException::class)
    fun loadUserInfo(cookies: Map<String, String>) {
        try {
            homePage = connection(UP_USER_INFO_URL, cookies = cookies).get()

            if (findError(homePage, "UP")!!.isNotEmpty()) {
                val errors = findError(homePage, "UP")
                throw OperationException(errors.toString())
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class)
    fun login(userName: String, password: String, captchaCode: String, cookies: Map<String, String>) {
        try {
            val dataMap: MutableMap<String, String> = HashMap()
            dataMap["btn_submit"] = ""
            dataMap["captcha"] = captchaCode
            dataMap["csrf"] = csrf
            dataMap["login_user"] = userName
            dataMap["password_user"] = password
            homePage = connection(UP_LOGIN_URL, dataMap, cookies).post()

            if (findError(homePage, "UP")!!.isNotEmpty()) {
                val errors = findError(homePage, "UP")
                throw LoginException(errors.toString())
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class)
    fun recharge(rechargeCode: String, cookies: Map<String, String>) {
        try {
            getCSRF(UP_RECHARGE_URL, cookies)
            val dataMap: MutableMap<String, String> = HashMap()
            dataMap["csrf"] = csrf
            dataMap["recharge_code"] = rechargeCode
            dataMap["btn_submit"] = ""
            page = connection(UP_RECHARGE_URL, dataMap, cookies).post()
            if (findError(page, "UP")!!.isNotEmpty()) {
                val errors = findError(page, "UP")
                throw OperationException(errors.toString())
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class)
    fun transfer(credit: String, mountToTransfer: String, password: String, accountToTransfer: String,
                 cookies: Map<String, String>) {
        try {
            getCSRF(UP_TRANSFER_URL, cookies)
            val dataMap: MutableMap<String, String> = HashMap()
            dataMap["csrf"] = csrf
            dataMap["tranfer"] = mountToTransfer
            dataMap["password_user"] = password
            dataMap["id_cuenta"] = accountToTransfer
            dataMap["action"] = "checkdata"
            if (creditToInt(credit) == 0) { // set error
                throw OperationException("Usted no tiene saldo en la cuenta. Por favor recargue.")
            } else if (creditToInt(credit) == mountToTransfer.replace(",", "").toInt() ||
                    creditToInt(credit) > mountToTransfer.replace(",", "").toInt()) {
                page = connection(UP_TRANSFER_URL, dataMap, cookies).post()
                if (findError(page, "UP")!!.isNotEmpty()) {
                    val errors = findError(page, "UP")
                    throw OperationException(errors.toString())
                }
            } else {
                throw OperationException("Su saldo es inferior a la cantidad que quiere transferir.")
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class)
    fun changePassword(oldPassword: String, newPassword: String, cookies: Map<String, String>) {
        try {
            getCSRF(UP_CHANGE_PASSWORD_URL, cookies)
            val dataMap: MutableMap<String, String> = HashMap()
            dataMap["csrf"] = csrf
            dataMap["old_password"] = oldPassword
            dataMap["new_password"] = newPassword
            dataMap["repeat_new_password"] = newPassword
            dataMap["btn_submit"] = ""
            page = connection(UP_CHANGE_PASSWORD_URL, dataMap, cookies).post()
            if (findError(page, "UP")!!.isNotEmpty()) {
                val errors = findError(page, "UP")
                throw OperationException(errors.toString())
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class)
    fun changeEmailPassword(oldPassword: String, newPassword: String, cookies: Map<String, String>) {
        try {
            getCSRF(UP_CHANGE_EMAIL_PASSWORD_URL, cookies)
            val dataMap: MutableMap<String, String> = HashMap()
            dataMap["csrf"] = csrf
            dataMap["old_password"] = oldPassword
            dataMap["new_password"] = newPassword
            dataMap["repeat_new_password"] = newPassword
            dataMap["btn_submit"] = ""
            page = connection(UP_CHANGE_EMAIL_PASSWORD_URL, dataMap, cookies).post()
            if (findError(page, "UP")!!.isNotEmpty()) {
                val errors = findError(page, "UP")
                throw OperationException(errors.toString())
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class)
    fun getConnections(year: Int, month: Int, cookies: Map<String, String>): List<Connection>? {
        try {
            val connections: MutableList<Connection> = ArrayList<Connection>()
            val yearMonth: String = buildYearMonth(year, month)
            getCSRF(UP_SERVICE_DETAIL_URL, cookies)
            val dataMap: MutableMap<String, String> = HashMap()
            dataMap["csrf"] = csrf
            dataMap["year_month"] = yearMonth
            dataMap["list_type"] = "service_detail"
            page = connection(UP_SERVICE_DETAIL_LIST_URL + yearMonth, dataMap, cookies).post()
            val tableOperationList: Element = page.select("table.responsive-table").first()
            try {
                val operationList: Elements = getOperationList(tableOperationList)
                for (connection_ in operationList) {
                    val _connection: Elements = connection_.select("td")
                    connections.add(Connection(_connection[0].text(),
                            _connection[1].text(), _connection[2].text(),
                            _connection[3].text(), _connection[4].text(),
                            _connection[5].text()))
                }
            } catch (ignored: NullPointerException) {
            }
            if (connections.size == 0) {
                page = connection(UP_SERVICE_DETAIL_SUMMARY, dataMap, cookies).post()
                if (findError(page, "UP")!!.isNotEmpty()) {
                    val errors = findError(page, "UP")
                    throw OperationException(errors.toString())
                }
            }
            return connections
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class)
    fun getRecharges(year: Int, month: Int, cookies: Map<String, String>): List<Recharge>? {
        try {
            val recharges: MutableList<Recharge> = ArrayList()
            val yearMonth = buildYearMonth(year, month)
            getCSRF(UP_RECHARGE_DETAIL_URL, cookies)
            val dataMap: MutableMap<String, String> = HashMap()
            dataMap["csrf"] = csrf
            dataMap["year_month"] = yearMonth
            dataMap["list_type"] = "recharge_detail"
            page = connection(UP_RECHARGE_DETAIL_LIST_URL + yearMonth, dataMap, cookies).post()
            val tableOperationList: Element = page.select("table.responsive-table").first()
            try {
                val operationList: Elements = getOperationList(tableOperationList)
                for (recharge_ in operationList) {
                    val _recharge = recharge_.select("td")
                    recharges.add(Recharge(_recharge[0].text(),
                            _recharge[1].text(), _recharge[2].text(),
                            _recharge[3].text()))
                }
            } catch (ignored: NullPointerException) {
            }
            if (recharges.size == 0) {
                page = connection(UP_RECHARGE_DETAIL_SUMMARY, dataMap, cookies).post()
                if (findError(page, "UP")!!.isNotEmpty()) {
                    val errors = findError(page, "UP")
                    throw OperationException(errors.toString())
                }
            }
            return recharges
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class)
    fun getTransfers(year: Int, month: Int, cookies: Map<String, String>): List<Transfer>? {
        try {
            val transfers: MutableList<Transfer> = ArrayList<Transfer>()
            val yearMonth = buildYearMonth(year, month)
            getCSRF(UP_TRANSFER_DETAIL_URL, cookies)
            val dataMap: MutableMap<String, String> = HashMap()
            dataMap["csrf"] = csrf
            dataMap["year_month"] = yearMonth
            dataMap["list_type"] = "recharge_detail"
            page = connection(UP_TRANSFER_DETAIL_LIST_URL + yearMonth, dataMap, cookies).post()
            val tableOperationList: Element = page.select("table.responsive-table").first()
            try {
                val operationList: Elements = getOperationList(tableOperationList)
                for (transfer_ in operationList) {
                    val _transfer = transfer_.select("td")
                    transfers.add(Transfer(_transfer[0].text(),
                            _transfer[1].text(), _transfer[2].text()))
                }
            } catch (ignored: NullPointerException) {
            }
            if (transfers.size == 0) {
                page = connection(UP_TRANSFER_DETAIL_SUMMARY_URL, dataMap, cookies).post()
                if (findError(page, "UP")!!.isNotEmpty()) {
                    val errors = findError(page, "UP")
                    throw OperationException(errors.toString())
                }
            }
            return transfers
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }

    @Throws(IOException::class)
    fun logout(cookies: Map<String, String>) {
        try {
            page = connection(UP_LOGOUT_URL, cookies = cookies).get()
            if (findError(page, "UP")!!.isNotEmpty()) {
                val errors = findError(page, "UP")
                throw OperationException(errors.toString())
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
        const val UP_LOGIN_URL = "https://www.portal.nauta.cu/user/login/es-es"
        const val UP_LOGOUT_URL = "https://www.portal.nauta.cu/user/logout"
        const val UP_CAPTCHA_URL = "https://www.portal.nauta.cu/captcha/?"
        const val UP_USER_INFO_URL = "https://www.portal.nauta.cu/useraaa/user_info"
        const val UP_RECHARGE_URL = "https://www.portal.nauta.cu/useraaa/recharge_account"
        const val UP_TRANSFER_URL = "https://www.portal.nauta.cu/useraaa/transfer_balance"
        const val UP_CHANGE_PASSWORD_URL = "https://www.portal.nauta.cu/useraaa/change_password"
        const val UP_CHANGE_EMAIL_PASSWORD_URL = "https://www.portal.nauta.cu/email/change_password"
        const val UP_SERVICE_DETAIL_URL = "https://www.portal.nauta.cu/useraaa/service_detail"
        const val UP_SERVICE_DETAIL_LIST_URL = "https://www.portal.nauta.cu/useraaa/service_detail_list/"
        const val UP_SERVICE_DETAIL_SUMMARY = "https://www.portal.nauta.cu/useraaa/service_detail_summary"
        const val UP_RECHARGE_DETAIL_URL = "https://www.portal.nauta.cu/useraaa/recharge_detail/"
        const val UP_RECHARGE_DETAIL_LIST_URL = "https://www.portal.nauta.cu/useraaa/recharge_detail_list/"
        const val UP_RECHARGE_DETAIL_SUMMARY = "https://www.portal.nauta.cu/useraaa/recharge_detail_summary/"
        const val UP_TRANSFER_DETAIL_URL = "https://www.portal.nauta.cu/useraaa/transfer_detail/"
        const val UP_TRANSFER_DETAIL_LIST_URL = "https://www.portal.nauta.cu/useraaa/transfer_detail_list/"
        const val UP_TRANSFER_DETAIL_SUMMARY_URL = "https://www.portal.nauta.cu/useraaa/transfer_detail_summary/"
    }
}