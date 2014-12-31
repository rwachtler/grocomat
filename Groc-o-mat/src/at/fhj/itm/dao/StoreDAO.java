package at.fhj.itm.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.fhj.itm.bl.Item;
import at.fhj.itm.bl.Preis;
import at.fhj.itm.bl.Store;

public class StoreDAO extends GenericSqlDAO<Store, String> {

	@Override
	public String create(Store newInstance) {
		PreparedStatement stmt;

		// Check if Store is already in the DB
		if (!(read(newInstance.getName()).getName().equals("unknown"))) {
			System.out.println("INFO: StoreDAO - CREATE - Store" + newInstance.getName()+ " item already exists in the database!");
		}

		else {

			try {
				stmt = conn.prepareStatement("INSERT INTO STORE (STR_PK_NAME) VALUES (?)");
				stmt.setString(1, newInstance.getName());
				stmt.executeUpdate();
				System.out.println("INFO: StoreDAO - CREATE - Store " + newInstance.getName()+ " creation successful!");

				for (Preis p : newInstance.getItems()) {
					stmt = conn.prepareStatement("INSERT INTO STORE_PRICE (STRPRC_FK_STORE_NAME, STRPRC_FK_PRICE_ID) VALUES (?, ?)");
					stmt.setString(1, newInstance.getName());
					stmt.setLong(2, PreisDAO.getPreisID(p));
					stmt.executeUpdate();
				}

			} catch (SQLException e) {
				System.err.println("ERROR: StoreDAO - CREATE - Store "+ newInstance.getName() + " creation failed!");
			}

			return newInstance.getName();
		}
		return "-1";
	}

	@Override
	public Store read(String name) {
		PreparedStatement stmt;
		Store s = new Store("unknown");

		try {
			stmt = conn.prepareStatement("SELECT * FROM STORE WHERE STR_PK_NAME = ?");
			stmt.setString(1, name);
			
			ResultSet rs = stmt.executeQuery();
			rs.first();

			s.setName(rs.getString("STR_PK_NAME"));

		} catch (SQLException e) {
			// e.printStackTrace();
			System.err.println("ERROR: StoreDAO - READ - Store: '" + name+ "' access failed!");
		}

		return s;
	}

	@Override
	public void update(Store transientObject) {}

	@Override
	public void delete(Store persistentObject) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("DELETE FROM STORE WHERE STR_PK_NAME = ?");
			stmt.setString(1, persistentObject.getName());
			int affectedRows = stmt.executeUpdate();

			if (affectedRows != 1) {
				System.out.println("INFO: StoreDAO - Store not found "+ persistentObject);
			}
		} catch (SQLException e) {
			// e.printStackTrace();
			System.err.println("INFO: StoreDAO - DELETE - delete failed!");
		}
	}

	/**
	 * Gets cheapest store for given parameters
	 * @param itm - Item to search
	 * @param catName - Category name
	 * @param storeAmount - amount of stores
	 * @return cheapeast store for given item / category
	 */
	public Store getCheapest(Item itm, String catName, String[] storeAmount) {
		Store storeTemp = new Store("unknown");
		PreparedStatement stmt;

		String sql = "";
		for (int i = 0; i < storeAmount.length - 1; i++) {
			sql += "STRPRC_FK_STORE_NAME = '" + storeAmount[i] + "' OR ";
		}
		sql += "STRPRC_FK_STORE_NAME = '" + storeAmount[storeAmount.length - 1] + "'";

		try {
			sql = "SELECT * FROM ITEM INNER JOIN PRICE ON ITEM.ITM_PK_EANCODE=PRICE.PRC_FK_ITEM_EANCODE "+ "INNER JOIN STORE_PRICE ON STORE_PRICE.STRPRC_FK_PRICE_ID=PRICE.PRC_PK_ID "
			+ "LEFT JOIN ITEM_CATEGORY ON ITEM.ITM_PK_EANCODE=ITEM_CATEGORY.ITMCAT_FK_ITEM_EANCODE "
			+ "LEFT JOIN CATEGORY ON ITEM_CATEGORY.ITMCAT_FK_CATEGORY_ID=CATEGORY.CAT_PK_ID "
			+ "WHERE (ITM_DESCRIPTION = ? OR CAT_NAME = ? OR ITM_PK_EANCODE = ?) AND ("+ sql + ") ORDER BY PRC_PRICE ASC";
			
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, itm.getDescription());
			stmt.setString(2, catName);
			stmt.setLong(3, itm.getEanCode());
			
			ResultSet rs = stmt.executeQuery();
			rs.first();

			storeTemp.setName(rs.getString("STRPRC_FK_STORE_NAME"));

		} catch (SQLException e) {
			System.err.println("ERROR: StoreDAO - getCheapest() - Store not found");
		}

		return storeTemp;
	}
	
	/**
	 * Gets the price for 
	 * @param storename - name of a specific store
	 * @param itm - an item to search
	 * @return
	 */
	public double getPriceForStorenameAndItem(String storename, Item itm) {
		PreparedStatement stmt;

		double priceTemp = -1.0;

		try {
			String sql = "SELECT PRC_PRICE FROM ITEM INNER JOIN PRICE ON ITEM.ITM_PK_EANCODE=PRICE.PRC_FK_ITEM_EANCODE "
			+ "INNER JOIN STORE_PRICE ON PRICE.PRC_PK_ID=STORE_PRICE.STRPRC_FK_PRICE_ID "
			+ "WHERE STRPRC_FK_STORE_NAME = ? AND ITM_PK_EANCODE = ?;";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, storename);
			stmt.setLong(2, itm.getEanCode());
			
			ResultSet rs = stmt.executeQuery();
			rs.first();

			priceTemp = Double.parseDouble(rs.getString("PRC_PRICE"));

		} catch (SQLException e) {
			System.err.println("ERROR: StoreDAO - getPriceFromNameAndItem - No price found '"+ itm.getDescription() + "'");
		}

		return priceTemp;
	}
}
