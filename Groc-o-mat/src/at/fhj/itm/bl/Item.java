package at.fhj.itm.bl;

import java.util.ArrayList;
import java.util.List;



/**
 * This class represents a grocery item. 
 * @author R.Wachtler.
 */
public class Item
{
	
	// Each Item has an barcode.
	private int eanCode = -1;
	
	// Description of the Item
	private String description;
	
	// What are the categories the item belongs to (may be empty)
	private List<String> kategorien = new ArrayList<String>();
	
	public Item(int eanCode, String description)
	{
		this.eanCode = eanCode;
		this.description = description;
	}
	
	public void addKategorie(String katname)
	{
		this.kategorien.add(katname);
	}
	
	public List<String> getKategorien() {
		return kategorien;
	}
	
	public int getEanCode() {
		return eanCode;
	}
	
	public void setEanCode(int code){
		this.eanCode = code;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String desc){
		this.description = desc;
	}
	
	public String toString()
	{
		return description + "[" + eanCode + "]";
	}

	
}
