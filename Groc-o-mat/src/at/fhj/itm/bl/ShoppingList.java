package at.fhj.itm.bl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class represents a plain shopping list
 * @author Hutter G.
 */
public class ShoppingList
{
	List<String> sl = new ArrayList<String>();
	
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
	 * Determines where i have to buy which Item to have the loowest price.
	 * but only in the stores that are within the strenames array
	 * 
	 * @param storenames Stores where i want to shop
	 */
	public Map<Store, Item[]> calcCheapest(String ...storenames)
	{
		// Implement me !
		
		// Beware: The storenames is a String[]. So maybe it is a good idea to
		// look into the database to see if the sores are really existing.
		
		// If a store does not exist then an exception may be thrown.
		return null;
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
		return "Impement me !";	
	}

}
