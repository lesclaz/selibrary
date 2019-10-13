package cu.marilasoft.selibrary.models;

import cu.marilasoft.selibrary.Net;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Product {

    private Element product_;
    private String title, description, price, urlBuyAction, urlLongDescriptionAction;
    private String longDescription = null;
    private boolean loadLongDescription = false;
    Map<String, String> cookies = new HashMap<>();

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPrice() {
        return price;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public String getLongDescription(Map<String, String> cookies) throws IOException {
        return _getLongDescription(cookies);
    }

    public String getUrlBuyAction() {
        return urlBuyAction;
    }

    public String getUrlLongDescriptionAction() {
        return urlLongDescriptionAction;
    }

    public Product(Element product_) {
        this.product_ = product_;
        getProductInfo();
    }

    public Product(Element product_, Map<String, String> cookies, boolean loadLongDescription) {
        this.product_ = product_;
        this.loadLongDescription = loadLongDescription;
        this.cookies = cookies;
        getProductInfo();
    }

    private void getProductInfo() {
        title = product_.select("h4").first().text();
        description = product_.select("div[class=\"offerPresentationProductDescription_msdp product_desc\"]")
                .first().select("span").first().text();
        price = product_.select("div[class=\"offerPresentationProductDescription_msdp product_desc\"]")
                .first().select("span[class=\"bold\"]").first().text() + " CUC";
        Element actions_ = product_.select("div[class=\"offerPresentationProductBuyAction_msdp ptype\"]")
                .first();
        urlBuyAction = actions_
                .select("a[class=\"offerPresentationProductBuyLink_msdp button_style link_button\"]")
                .first().attr("href");
        urlLongDescriptionAction = actions_.select("a[class=\"offerPresentationProductBuyLink_msdp\"]")
                .first().attr("href");
        if (loadLongDescription) {
            try {
                longDescription = _getLongDescription(cookies);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String _getLongDescription(Map<String, String> cookies) throws IOException {
        return Net.connection("https://mi.cubacel.net" + urlLongDescriptionAction, cookies, false)
                .get().select("div[class=\"products_page_details\"]").first()
                .select("div[class=\"pd_information\"]").first()
                .select("p").first().text();
    }
}
