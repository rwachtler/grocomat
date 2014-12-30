package at.fhj.itm.bl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import at.fhj.itm.dao.ItemDAO;
import at.fhj.itm.dao.StoreDAO;

/**
 * This class represents a plain shopping list
 * @author Hutter G.
 */
public class ShoppingList
{
	List<String> sl = new ArrayList<String>();
	
	private ItemDAO itmDAO;
	private StoreDAO strDAO;
	
	
	public ShoppingList(){
		itmDAO = new ItemDAO();
		strDAO = new StoreDAO();
	}
	/**
	 * Enries are always in text form and can be one of these:
	 * 
	 *  o The (unique) EAN Code of an Item
	 *  o The name of an Item
	 *  o The cateory of an item
	 *  
	 * @param entry
	 */
	public final void add(String entry)
	{
		sl.add(entry);
	}
	
	/**
	 * Check if a given store exists in the database
	 * @param storenames
	 * @return true - store available | false - store not available
	 */
	private boolean checkForStore(String[] storenames) {
		for (String storename : storenames) {
			Store strTemp = strDAO.read(storename);
			if (strTemp.getName().equals("unknown")) {
				System.out.println("INFO: Store insertion succeeded");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Determines where i have to buy which Item to have the loowest price.
	 * but only in the stores that are within the strenames array
	 * 
	 * @param storenames Stores where i want to shop
	 */
	public Map<Store, Item[]> calcCheapest(String ...storenames)
	{		
		// Beware: The storenames is a String[]. So maybe it is a good idea to
		// look into the database to see if the sores are really existing.
		
		// If a store does not exist then an exception may be thrown.
		//init a hash-map
		Map<Store, Item[]> map = new HashMap<Store, Item[]>();
		//checking if a store exists
		if (checkForStore(storenames)) {

			//init
			ArrayList<String> unset_elements = new ArrayList<String>();
			ArrayList<Item> cheap_elements = new ArrayList<Item>();
			ArrayList<Item> unset_stores = new ArrayList<Item>();
			
			Store unset_item = new Store("unset items");
			Store unset_store = new Store("unset store");
			
			
			for (String s : sl) {
				//get item
				Item itm = itmDAO.getItemBySearchterm(s);

				////if item not found --> store it in unset_item
				if (itm.getDescription().equals("unknown")) {
					if (map.containsKey(unset_item)) {
						unset_elements.add(s);
						Item[] items = null;

						Item[] itmTemp = new Item[unset_elements.size()];
						ArrayList<Item> list = new ArrayList<Item>();
						for (String tmpString : unset_elements) {
							Item i = new Item(-1, tmpString);
							list.add(i);
						}
						itmTemp = list.toArray(itmTemp);

						items = itmTemp;
						
						map.put(unset_item, items);
					}
					else {
						unset_elements.add(s);
						Item[] unset_itms = null;
						
						Item[] itmTemp = new Item[unset_elements.size()];
						ArrayList<Item> list = new ArrayList<Item>();
						for (String tmpString : unset_elements) {
							Item i = new Item(-1, tmpString);
							list.add(i);
						}
						itmTemp = list.toArray(itmTemp);

						unset_itms = itmTemp;

						map.put(unset_item, unset_itms);
					}
				} else {
					//get cheapeast store
					Store cheapestStore = strDAO.getCheapest(itm, s, storenames);
					//if store not found --> store it in unset_store
					if (cheapestStore.getName().equals("unkown")) {
						if (map.containsKey(unset_store)) {
							Item[] unset_stored_itms = map.get("unset Store");
							unset_stored_itms[unset_stored_itms.length] = itm;
							map.put(unset_store, unset_stored_itms);
						}
						else {
							unset_stores.add(itm);
							Item[] unset_itms = unset_stores.toArray(new Item[unset_stores.size()]);

							map.put(unset_store, unset_itms);
						}
					} else {
						//add store if it's not in the map already
						if (!map.containsKey(cheapestStore)) {
							Item[] itm_array = new Item[0];
							map.put(cheapestStore, itm_array);
						}

						Item[] mapItems = map.get(cheapestStore);

						cheap_elements = new ArrayList<Item>(Arrays.asList(mapItems));
						cheap_elements.add(itm);

						//array of available items
						Item[] avalaibleItms = new Item[cheap_elements.size()];

						Item[] cheapest_elements = cheap_elements.toArray(avalaibleItms);

						//Store the values
						map.put(cheapestStore, cheapest_elements);

					}
				}
			}

		} else {
			throw new IllegalArgumentException();
		}

		return map;
	}
	
	/**
	 * Static method that formats the given list as HTML.
	 * The List should be organized in a way where the Items are grouped by
	 * the stores where you buy them. It should also contain the Name of the
	 * 
	 * Items that are not available in any store should be listed at the end.
	 * @param buylist
	 */
	public static String formatAsHtml(Map<Store, Item[]> buylist)
	{
		//declaration
		String htmlString = "";
		Iterator<Store> i = buylist.keySet().iterator();
		Iterator<Store> i2 = buylist.keySet().iterator();
		DecimalFormat formatter = new DecimalFormat("##0.00");
		double sum = 0.0;
		
		htmlString += "<!DOCTYPE html>";
		htmlString += "<html>\n";
		htmlString += "\t<head>\n";
		htmlString += "\t\t<meta name="+"author"+" content="+"R.Wachtler"+">\n";
		htmlString += "\t\t<title>\n";
		htmlString += "\t\tGrocomat\n";
		htmlString += "\t\t</title>\n";
		htmlString += "\t</head>\n";
		htmlString += "\t<body>\n";
		
		//Loop through found items
		while(i.hasNext()){
			Store str = (Store)i.next();
			Item[] itms = buylist.get(str);
			if(!(str.getName().equals("unset store") ||
			str.getName().equals("unset items"))){
				htmlString += "\t\t\t<h3>Store: "+str.getName()+"</h2>\n";
				for (Item item : itms) {
					htmlString += "\t\t\t\t <p>** " + item.getDescription()
					+ "Price: "+ formatter.format(str.getPriceForItem(item)) + "€</p>\n";
					sum += str.getPriceForItem(item);
				}
				htmlString += "\n";
			}
		}
		
		//loop through not found items
		while (i2.hasNext()) {
			Store str = (Store)i2.next();
			Item[] itms = buylist.get(str);
			if(!(str.getName().equals("unset store") ||
			str.getName().equals("unset items"))){
				htmlString += "\t\t\t<h3>Store: "+str.getName()+"</h2>\n";
				for (Item item : itms) {
					htmlString += "\t\t\t\t <p>** " + item.getDescription()
					+ "Price: "+ formatter.format(str.getPriceForItem(item)) + "€</p>\n";
					sum += str.getPriceForItem(item);
				}
				htmlString += "\n";
			}
		}
		
		htmlString += "\t\t\t <h2>SUM: " + formatter.format(sum) + "€</h2> \n";
		htmlString += "\t</body>\n";
		htmlString += "</html>\n";
		
		return htmlString;
	}

}
