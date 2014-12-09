package at.fhj.itm;

import java.util.Map;

import org.junit.Test;

import at.fhj.itm.bl.ShoppingList;
import at.fhj.itm.dao.Item;
import at.fhj.itm.dao.Store;

/**
 * Unittestclass - This should show how the Classes will be used. 
 * It will be changed for grading. (The principle stays the same but 
 * the data changes - so i can see if the result is correct or just
 * a good guesses). 
 * 
 * STUDENTS: DO NOT CHANGE THIS FILE !!!!!
 * 
 * 
 * @author G. Hutter
 *
 */
public class ShoppingTester
{
	
	/**
	 * Assigns the same category to multiple items
	 * @param katname
	 * @param items
	 */
	public void setCategory(String katname, Item ...items)
	{
		for(Item i : items)
		{
			i.addKategorie(katname);
		}
	}
	
	@Test
	public void testPersistence()
	{
		int ean = 1;
		
		Item wbrot = new Item(ean++ ,"Weissbrot");
		Item sbrot = new Item(ean++,"Schwarzbrot");
		Item salami = new Item(ean++,"Salami");
		Item extra = new Item(ean++, "Extra");
		Item kant = new Item(ean++, "Kantwurst");
		Item vollmilch = new Item(ean++, "Vollmilch");
		Item haltbarmilch = new Item(ean++, "H-milch");
		Item feier = new Item(ean++, "FreilandEier");
		Item beier = new Item(ean++, "BodenhaltungEier");
		
		setCategory("Wurst", extra, salami, kant);
		setCategory("Gebaeck", wbrot, sbrot);
		setCategory("Eier", feier, beier);

		Store billa = new Store("Billa");
		Store lidl = new Store("Lidl");
		Store hofer = new Store("Hofer");
		
		billa.addToStock(salami, 2.9);
		billa.addToStock(extra, 1.8);
		billa.addToStock(wbrot, 2.5);
		billa.addToStock(sbrot, 2.0);
		billa.addToStock(vollmilch, 5.0);
		billa.addToStock(haltbarmilch, 2.75);
		billa.save();
		
		hofer.addToStock(kant, 2.9);
		hofer.addToStock(sbrot, 2.0);
		hofer.addToStock(wbrot, 2.0);
		hofer.addToStock(feier, 2.75);
		hofer.addToStock(haltbarmilch, 2.9);
		hofer.save();
		
		lidl.addToStock(wbrot, 2.2);
		lidl.addToStock(salami, 2.1);
		lidl.addToStock(kant, 2.1);
		lidl.addToStock(vollmilch, 4.7);
		lidl.addToStock(beier, 1.7);
		lidl.addToStock(haltbarmilch, 2.5);
		lidl.save();

		// First part of the assignment: Implement the Store.save() method that
		// persists the given object graph. Therefore the existing classes will 
		// need to be annotated.
		
	}

	@Test
	public void testOptimization()
	{
		
		// Second part of the assignment:
		// Calculate where to shop what - in order to minimize the price

		ShoppingList sl = new ShoppingList();
		sl.add("Weissbrot");
		sl.add("Extra");
		sl.add("Eier");
		sl.add("7"); 
		sl.add("Mineral"); 
		
		// Therefore you have to implement this method: calcCheapest
		// You may wonder why there are only Strings as parameters;
		// That is for a good reason: You should learn how to implement HQL Queries
		// and how to look up the previously stored items
		Map<Store, Item[]> buylist = sl.calcCheapest("Billa", "Lidl");
		
		// Output as HTML on sysout
		System.out.println(ShoppingList.formatAsHtml(buylist));
		
		/*
		 *  The correct solution is (in this case):
		 *  
		 *  Weissbrot ... There is only one item named weissbrot: wbrot. It is 2.50@billa and 2.2@lidl => buy at lidl
		 *  Extra ... The only store that offers extra is billa. It costs 1.80 => Buy at billa.
		 *  Eier ... There is no item named like this, but there is a category.
		 *           The category contains feier und beier.
		 *           	Cheapest feier: feier is neither offered in Billa nor in Lidl
		 *           	Cheapest beier : 1.7@lidl => Buy at Lidl
		 *  7 ... There is neither an Item with that name nor a category .. so it has to be an EAN Code.
		 *         The Item with the EanCode 7 is: haltbarmilch.
		 *         haltbarmilch@Billa:2.75
		 *         haltbarmilch@Lidl:2.5 => Buy at lidl
		 *  
		 *  Mineral ... There is no item, category or ean code named like this => You cannot buy it -> Feel free to list it at the end of the list in a "Unknown" store.
		 *  
		 *  Therefore the correct solution is:
		 *  
		 *  LIDL
		 *  	o Weissbrot 		(2.50) 
		 *   	o BodenhaltungEier 	(1.70)
		 *      o Haltbar Milch 	(2.50)
		 *  
		 *  BILLA
		 *  	o Extra				(1.80)
		 *  
		 *  UNBEKANNT
		 *  	o Mineral			(0.00)
		 *  ==============================
		 *  				 SUM	 8.50
		 */
		
		// Be aware: I am going to change the input parameters to see if everything
		// is persisted correctly and to check if the cheapest solution is found.
	}
}
