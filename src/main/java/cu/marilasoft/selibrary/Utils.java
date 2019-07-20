package cu.marilasoft.selibrary;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Utils {

	public static Element getCardPanel(Document page) {
		
		Element cardPanel = page.select("div.card-panel").first();
		return cardPanel;
	}
	
	public static Element getTableConnectionList (Document page) {
		Element tableConnectionList;
		tableConnectionList = page.select("table.responsive-table").first();
		
		return tableConnectionList;
	}
	
	public static Elements getConnectionList (Element tableConnectionList) {
		Elements connections = tableConnectionList.select("tr");
		connections.remove(0);
		
		return connections;
	}
	
	public static String[] separeString (String string, String separator) {
		String[] list;
		list = string.split(separator);
		return list;
	}
}
