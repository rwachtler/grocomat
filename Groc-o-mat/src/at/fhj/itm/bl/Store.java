package at.fhj.itm.bl;

import java.util.ArrayList;
import java.util.List;


/**
 * This class represents a grocery store.
 * @author Hutter G.
 */
public class Store
{
	String name = "";
	List<Preis> items = new ArrayList<Preis>();
	
	/**
	 * ctor
	 * @param name
	 */
	public Store(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the price for an item. If the Item does not exist then an 
	 * IllegalArgumentException will be thrown.
	 * 
	 * @param i Item to be searched for.
	 * @return Price of that particular item 
	 */
	public double getPriceForItem(Item i)
	{
		double price = 0;
		for (Preis p : this.items) {
			if(p.item.equals(i)){
				price = p.price;
				break;
			}
			else{
				throw new IllegalArgumentException();
			}
		}
		return price;		
	}
	
	/**
	 * Put items into the store for a given price. This instantiates a 
	 * price object that is stored within a collection in this class.
	 * @param i
	 * @param d
	 */
	public void addToStock(Item i, double price)
	{
		this.items.add(new Preis(i,price));
	}
	
	/**
	 * Returns how many Items of this type are available
	 * @param i
	 * @return
	 */
	public int howManyAvailable(Item i)
	{
		int numberOfItems = 0;
		for (Preis p : this.items){
			if(p.item.getKategorien().equals(i.getKategorien())){
				numberOfItems++;
			}
		}
		return numberOfItems;
	}

	/**
	 * This is a lifecycle function. It presists the shop (as 
	 * well as the Items and the Prices) into the database.
	 * 
	 * This function has to save all the other Objects as well (Price and Item data).
	 * There is (by intention) no parameter where to save the stuff because
	 * this will most probably introduce new dependencies.
	 * 
	 * The actual database connection will be read from a central storage point.
	 */
	public void save()
	{
		// Implement me !
	}

	
}
