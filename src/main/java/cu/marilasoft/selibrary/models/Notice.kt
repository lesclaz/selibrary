package cu.marilasoft.selibrary.models

import cu.marilasoft.selibrary.Net
import org.jsoup.nodes.Element

class Notice(private val element: Element) {
    val title: String
        get() {
            return element.select("a").attr("title")
        }
    val url: String
        get() {
            return element.select("a").first().attr("href")
        }
    val img: ByteArray
        get() {
            return Net.getImg(element.select("img[class=\"img-responsive\"]").first().attr("src"))
        }
}