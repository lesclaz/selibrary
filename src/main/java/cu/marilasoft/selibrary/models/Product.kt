package cu.marilasoft.selibrary.models

import cu.marilasoft.selibrary.Net
import cu.marilasoft.selibrary.utils.CommunicationException
import cu.marilasoft.selibrary.utils.Constants
import cu.marilasoft.selibrary.utils.OperationException
import org.jsoup.nodes.Element
import java.io.IOException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class Product(html: Element) {
    private val element = html
    val title: String
        get() {
            return element.select("h4").first().text()
        }
    val description: String
        get() {
            return element.select("div[class=\"offerPresentationProductDescription_msdp product_desc\"]")
                    .first().select("span").first().text()
        }
    val price: Float
        get() {
            return element.select("div[class=\"offerPresentationProductDescription_msdp product_desc\"]")
                    .first().select("span[class=\"bold\"]").first().text().toFloat()
        }
    val urlBuyAction: String
        get() {
            return element.select("div[class=\"offerPresentationProductBuyAction_msdp ptype\"]").first()
                    .select("a[class=\"offerPresentationProductBuyLink_msdp button_style link_button\"]").first()
                    .attr("href")
        }
    val urlLongDescriptionAction: String
        get() {
            return element.select("div[class=\"offerPresentationProductBuyAction_msdp ptype\"]").first()
                    .select("a[class=\"offerPresentationProductBuyLink_msdp\"]").first().attr("href")
        }

    @Throws(IOException::class, CommunicationException::class, OperationException::class)
    fun buy(urlBuyAction: String, cookies: MutableMap<String, String>) {
        try {
            var page = Net.connection(Constants.MCP_BASE_URL + urlBuyAction, cookies = cookies, verify = false).get()
            val urlBuy = page.select("a[class=\"offerPresentationProductBuyLink_msdp button_style link_button\"]")
                    .first().attr("href")
            page = Net.connection(Constants.MCP_BASE_URL + urlBuy, cookies = cookies, verify = false).get()
            val purchaseDetail = page.select("div[class=\"products_purchase_details_block\"]").first()
                    .select("p").last().text()
            if (purchaseDetail.startsWith("Ha ocurrido un error.")) {
                throw OperationException(purchaseDetail)
            }
        } catch (e: UnknownHostException) {
            throw CommunicationException("${Constants.EXCEPTION_UNKNOWN_HOST} ${e.message}")
        } catch (e2: SSLHandshakeException) {
            throw CommunicationException(Constants.EXCEPTION_SSL_HANDSHAKE)
        } catch (e3: NullPointerException) {
            throw CommunicationException(Constants.EXCEPTION_NULL_POINTER)
        }
    }
}