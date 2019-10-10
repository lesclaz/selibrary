package cu.marilasoft.selibrary.models;

import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public class Product {

    private Element product_;

    public Product(Element product_) {
        this.product_ = product_;
    }

    public String title() {
        return product_.select("h4").first().text();
    }

    public String description() {
        return product_.select("div[class=\"offerPresentationProductDescription_msdp product_desc\"]")
                .first().select("span").first().text();
    }

    public String price() {
        return product_.select("div[class=\"offerPresentationProductDescription_msdp product_desc\"]")
                .first().select("span[class=\"bold\"]").first().text() + " CUC";
    }

    public Map<String, String> actions() {
        Map<String, String> actions = new HashMap<>();
        Element actions_ = product_.select("div[class=\"offerPresentationProductBuyAction_msdp ptype\"]")
                .first();
        String actionMostInfo = actions_.select("a[class=\"offerPresentationProductBuyLink_msdp\"]")
                .first().attr("href");
        String actionBuy = actions_
                .select("a[class=\"offerPresentationProductBuyLink_msdp button_style link_button\"]")
                .first().attr("href");
        actions.put("mostInfo", actionMostInfo);
        actions.put("buy", actionBuy);
        return actions;
    }
}
