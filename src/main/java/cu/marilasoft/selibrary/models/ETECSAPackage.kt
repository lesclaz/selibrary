package cu.marilasoft.selibrary.models

import org.jsoup.nodes.Element

class ETECSAPackage(private val element: Element, private val bonusPackage: Boolean = false) {
    private val expireDateBlock = element.select("div[class=\"expires_date_block\"]")
    private val expireDateBlockRed = element.select("div[class=\"expires_date_block red_bg\"]")
    private val expireDateBlockOrange = element.select("div[class=\"expires_date_block orange_bg\"]")

    private lateinit var mTitle: String

    val isStatusRed: Boolean
        get() {
            return (expireDateBlock.isEmpty() && expireDateBlockOrange.isEmpty())
        }

    val isStatusOrange: Boolean
        get() {
            return (expireDateBlock.isEmpty() && expireDateBlockRed.isEmpty())
        }

    val packageId: String
        get() {
            return element.select("div[class=\"charts_data\"]").first().select("div")[1]
                    .attr("id")
        }

    val isBonusPackage: Boolean
        get() {
            return bonusPackage
        }

    var title: String
        get() {
            return mTitle
        }
        set(value) {
            mTitle = value
        }

    val description: String
        get() {
            return element.select("div[class=\"features_block\"]").first().text()
        }

    val dataInfo: String
        get() {
            return element.select("div[class=\"charts_data\"]").first()
                    .select("div[id=\"$packageId\"]").first().attr("data-info")
        }

    val restData: Float
        get() {
            return element.select("div[class=\"charts_data\"]").first()
                    .select("div[id=\"$packageId\"]").first().attr("data-text").toFloat()
        }

    val percent: Int
        get() {
            return Integer.parseInt(element.select("div[class=\"charts_data\"]").first()
                    .select("div[id=\"$packageId\"]").first().attr("data-percent"))
        }

    val expireInDate: Int
        get() {
            if (expireDateBlock.isEmpty() && expireDateBlockOrange.isEmpty()) {
                return Integer.parseInt(expireDateBlockRed.first().select("div[class=\"expires_date\"]")
                        .first().text())
            }
            if (expireDateBlock.isEmpty() && expireDateBlockRed.isEmpty()) {
                return Integer.parseInt(expireDateBlockOrange.first().select("div[class=\"expires_date\"]")
                        .first().text())
            }
            return Integer.parseInt(expireDateBlock.first().select("div[class=\"expires_date\"]")
                    .first().text())
        }

    val expireInHours: String
        get() {
            if (expireDateBlock.isEmpty() && expireDateBlockOrange.isEmpty()) {
                return expireDateBlockRed.first().select("div[class=\"expires_hours\"]")
                        .first().text()
            }
            if (expireDateBlock.isEmpty() && expireDateBlockRed.isEmpty()) {
                return expireDateBlockOrange.first().select("div[class=\"expires_hours\"]")
                        .first().text()
            }
            return expireDateBlock.first().select("div[class=\"expires_hours\"]")
                    .first().text()
        }

    val expireDate: String
        get() {
            return element.select("div[class=\"expiry_date_right\"]").first()
                    .select("span[class=\"date_value\"]").first().text()
        }
}