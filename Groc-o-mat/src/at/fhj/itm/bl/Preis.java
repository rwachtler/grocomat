package at.fhj.itm.bl;

/**
 * Simple class that assigns an item a price.
 * @author Hutter G.
 *
 */
public class Preis
{
	//Declaration
	private int id;
	private int ean;
	Item item;
	double price;
	
	/**
	 * Default constructor, initializes a 'Preis' object with an item and price
	 * the ean code will be set with relationship to the item
	 * @param item
	 * @param price
	 */
	public Preis(Item item, double price)
	{
		this.ean = item.getEanCode();
		this.item = item;
		this.price = price;
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEan() {
		return ean;
	}

	public void setEan(int ean) {
		this.ean = ean;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	

}
