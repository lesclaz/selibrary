package cu.marilasoft.selibrary.models

import org.jsoup.nodes.Element

class NavigationPackage(private val element: Element, private val count: Int) {

    val packageId: String?
        get() {
            return element.select("div[class=\"charts_data\"]")[count].select("div")[1]
                    .attr("id")
        }

    val title: String?
        get() {
            return element.select("h3[class=\"ac_block_title\"]")[count].text()
        }

    val description: String?
        get() {
            return element.select("div[class=\"features_block\"]")[count].text()
        }

    val dataInfo: String?
        get() {
            return element.select("div[class=\"charts_data\"]")[count]
                    .select("div[id=\"$packageId\"]").first().attr("data-info")
        }

    val restData: Float
        get() {
            return element.select("div[class=\"charts_data\"]")[count]
                    .select("div[id=\"$packageId\"]").first().attr("data-text").toFloat()
        }

    val percent: Int?
        get() {
            return Integer.parseInt(element.select("div[class=\"charts_data\"]")[count]
                    .select("div[id=\"$packageId\"]").first().attr("data-percent"))
        }

    val totalData: Int
        get() {
            if (percent == 0) return restData.toInt()
            val tempData = (restData * 100) / (100 - percent!!)
            if (tempData in 47.0..53.0) return 50
            if (tempData in 135.0..165.0) return 150
            if (tempData in 285.0..315.0) return 300
            if (tempData in 385.0..415.0) return 400
            if (tempData in 585.0..615.0) return 600
            return 0
        }

    val expireInDate: Int?
        get() {
            val expireDateBlock = element.select("div[class=\"expires_date_block\"]")
            val expireDateBlockRed = element.select("div[class=\"expires_date_block red_bg\"]")
            val expireDateBlockOrange = element.select("div[class=\"expires_date_block orange_bg\"]")
            if (expireDateBlock.isEmpty() && expireDateBlockOrange.isEmpty()) {
                return Integer.parseInt(expireDateBlockRed[count].select("div[class=\"expires_date\"]")
                        .first().text())
            }
            if (expireDateBlock.isEmpty() && expireDateBlockRed.isEmpty()) {
                return Integer.parseInt(expireDateBlockOrange[count].select("div[class=\"expires_date\"]")
                        .first().text())
            }
            return Integer.parseInt(expireDateBlock[count].select("div[class=\"expires_date\"]")
                    .first().text())
        }

    val expireInHours: String?
        get() {
            val expireDateBlock = element.select("div[class=\"expires_date_block\"]")
            val expireDateBlockRed = element.select("div[class=\"expires_date_block red_bg\"]")
            val expireDateBlockOrange = element.select("div[class=\"expires_date_block orange_bg\"]")
            if (expireDateBlock.isEmpty() && expireDateBlockOrange.isEmpty()) {
                return expireDateBlockRed[count].select("div[class=\"expires_hours\"]")
                        .first().text()
            }
            if (expireDateBlock.isEmpty() && expireDateBlockRed.isEmpty()) {
                return expireDateBlockOrange[count].select("div[class=\"expires_hours\"]")
                        .first().text()
            }
            return expireDateBlock[count].select("div[class=\"expires_hours\"]")
                    .first().text()
        }

    val expireDate: String?
        get() {
            return element.select("div[class=\"expiry_date_right\"]")[count].select("span[class=\"date_value\"]")
                    .first().text()
        }
}