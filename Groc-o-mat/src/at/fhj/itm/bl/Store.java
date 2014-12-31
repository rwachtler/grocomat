package at.fhj.itm.bl;


import java.util.ArrayList;
import java.util.List;

import at.fhj.itm.dao.ItemDAO;
import at.fhj.itm.dao.PreisDAO;
import at.fhj.itm.dao.StoreDAO;
/**
 * This class represents a grocery store.
 * @author Hutter G.
 */
public class Store
{
	String name = "";
	List<Preis> items = new ArrayList<Preis>();
	private ItemDAO itmDAO;
	private PreisDAO prcDAO;
	private StoreDAO strDAO;
	
	/**
	 * ctor
	 * @param name
	 */
	public Store(String name)
	{
		this.name = name;
		itmDAO = new ItemDAO();
		prcDAO = new PreisDAO();
		strDAO = new StoreDAO();
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
		double price = 0.0;
		price = strDAO.getPriceForStorenameAndItem(name, i);
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
	public int howManyAvailable(Item i){/*NOT IMPLEMENTED*/return 0;}

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
		for (Preis p : items) {
			itmDAO.create(p.getItem());
			prcDAO.create(p);
		}
		strDAO.create(this);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Preis> getItems() {
		return items;
	}

	public void setItems(List<Preis> items) {
		this.items = items;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Store other = (Store) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}