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
 * 
 * @author Hutter G.
 */
public class ShoppingList {
	List<String> shoppinglist = new ArrayList<String>();

	private ItemDAO itmDAO;
	private StoreDAO strDAO;

	public ShoppingList() {
		itmDAO = new ItemDAO();
		strDAO = new StoreDAO();
	}

	/**
	 * Enries are always in text form and can be one of these:
	 * 
	 * o The (unique) EAN Code of an Item o The name of an Item o The cateory of
	 * an item
	 * 
	 * @param entry
	 */
	public final void add(String entry) {
		shoppinglist.add(entry);
	}

	/**
	 * Determines where i have to buy which Item to have the loowest price. but
	 * only in the stores that are within the strenames array
	 * 
	 * @param storenames Stores where i want to shop
	 */
	public Map<Store, Item[]> calcCheapest(String... storenames) {
		// Beware: The storenames is a String[]. So maybe it is a good idea to
		// look into the database to see if the sores are really existing.
				
		// If a store does not exist then an exception may be thrown.
		//init a hash-map
		Map<Store, Item[]> map = new HashMap<Store, Item[]>();

		if (checkForStore(storenames)) {

			//init
			ArrayList<String> unset_elements = new ArrayList<String>();
			ArrayList<Item> cheap_elements = new ArrayList<Item>();
			ArrayList<Item> unset_stores = new ArrayList<Item>();
			Store unknown_items = new Store("unknown Items");
			Store unknown_store = new Store("unknown Store");

			for (String s : shoppinglist) {
				//get item
				Item itm = itmDAO.getItemBySearchterm(s);
				//if item not found --> store it in unset_item
				if (itm.getDescription().equals("unknown")) {

					if (map.containsKey(unknown_items)) {
						unset_elements.add(s);
						Item[] items = convertToItemsArray(unset_elements);
						map.put(unknown_items, items);
					}
					else {
						unset_elements.add(s);
						Item[] unkown_elements = convertToItemsArray(unset_elements);
						
						map.put(unknown_items, unkown_elements);
					}
				} else {
					//get cheapeast store
					Store cheapestStore = strDAO.getCheapest(itm, s, storenames);
					//if store not found --> store it in unknown_store
					if (cheapestStore.getName().equals("unknown")) {
						if (map.containsKey(unset_stores)) {
							Item[] unset_stored_itms = map.get("unset Store");
							unset_stored_itms[unset_stored_itms.length] = itm;
							map.put(unknown_store, unset_stored_itms);
						}
						else {
							unset_stores.add(itm);
							Item[] unset_itms = unset_stores.toArray(new Item[unset_stores.size()]);

							map.put(unknown_store, unset_itms);
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
			throw new IllegalArgumentException("Inserted Store not available!");
		}

		return map;
	}

	/**
	 * Converts a string array which contains items as strings into an item array
	 * 
	 * @param unknown_elements_arraylist - list of items
	 * @return Item-Array
	 */
	private Item[] convertToItemsArray(ArrayList<String> unknown_elements_arraylist) {
		Item[] tmp = new Item[unknown_elements_arraylist.size()];

		ArrayList<Item> list = new ArrayList<Item>();

		for (String tmpString : unknown_elements_arraylist) {
			Item tmpItem = new Item(-1, tmpString);
			list.add(tmpItem);
		}

		tmp = list.toArray(tmp);

		return tmp;
	}

	/**
	 * Check if a given store exists in the database
	 * @param storenames
	 * @return true - store available | false - store not available
	 */
	private boolean checkForStore(String[] storenames) {
		for (String storename : storenames) {
			Store str = strDAO.read(storename);
			if (str.getName().equals("unknown")) {
				System.out.println("storename"+ " not available!");
				return false;
			}
		}
		return true;
	}

	/**
	 * Static method that formats the given list as HTML. The List should be
	 * organized in a way where the Items are grouped by the stores where you
	 * buy them. It should also contain the Name of the
	 * 
	 * Items that are not available in any store should be listed at the end.
	 * 
	 * @param buylist
	 *            All Items that are available and the cheapest in the given
	 *            Stores
	 */
	public static String formatAsHtml(Map<Store, Item[]> buylist) {
		
		String htmlString = "";
		htmlString += "<html>\n";
		htmlString += "\t<head>\n";
		htmlString += "\t\t<title>Grocomat\n</title>\n";
		htmlString += "\t</head>\n";
		htmlString += "\t<body>\n";
		htmlString += "\t\t<h1>HTML-List</h1>\n";

		Iterator<Store> i = buylist.keySet().iterator();
		Iterator<Store> i2 = buylist.keySet().iterator();

		DecimalFormat formatter = new DecimalFormat("##0.00");
		double sum = 0.0;

		//loop through items which were found
		while (i.hasNext()) {
			Store store = (Store) i.next();
			Item[] items = buylist.get(store);
			if (!(store.getName().equals("unknown Store") || store.getName().equals("unknown Items"))) {

				htmlString += "\t\t\t <h2>Store: " + store.getName() + "</h2>\n";
				for (Item item : items) {
					htmlString += "\t\t\t\t <p>o " + item.getDescription()+ " with a Price of: "+ formatter.format(store.getPriceForItem(item)) + "€</p>\n";
					sum += store.getPriceForItem(item);
				}
				htmlString += "\n";
			}

		}


		//loop through items which were not found
		while (i2.hasNext()) {
			Store store = (Store) i2.next();
			Item[] items = buylist.get(store);

			if (store.getName().equals("unknown Store")) {
				htmlString += "\t\t\t <h2>Sorry, no Store is selling these Items:</h2>\n";
				for (Item item : items) {
					htmlString += "\t\t\t\t <p>o " + item.getDescription() + "</p>\n";
				}
				htmlString += "\n";
			}
			if (store.getName().equals("unknown Items")) {
				htmlString += "\t\t\t <h2>Sorry, i don't know these Items:</h2>\n";
				for (Item item : items) {
					htmlString += "\t\t\t\t <p>O " + item.getDescription() + "</p>\n";
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
