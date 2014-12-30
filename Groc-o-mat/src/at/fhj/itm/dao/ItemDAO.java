package at.fhj.itm.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.fhj.itm.bl.Item;

public class ItemDAO extends GenericSqlDAO<Item, Integer>{
	
	private CategoryDAO catDAO;
	
	public ItemDAO(){
		catDAO = new CategoryDAO();
	}

	@Override
	public Integer create(Item newInstance) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;

		/*
		 * Checking if item was already inserted into the database
		 */
		if (read(newInstance.getEanCode()).getEanCode() > -1) {
			System.out.println("INFO:" + newInstance.toString()+ " was already inserted into the database!");
		}

		else {

			try {
				stmt = conn.prepareStatement("INSERT INTO ITEM (ITM_PK_EANCODE, ITM_DESCRIPTION) VALUES (?, ?)");
				stmt.setLong(1, newInstance.getEanCode());
				stmt.setString(2, newInstance.getDescription());
				stmt.executeUpdate();

				for (String catName : newInstance.getKategorien()) {
					int catID = -1;

					catID = CategoryDAO.getCategoryIDbyName(catName);

					if (catID < 0) {
						catID = catDAO.create(catName);
					}

					try {
						stmt = conn.prepareStatement("INSERT INTO ITEM_CATEGORY (ITMCAT_FK_ITEM_EANCODE, ITEMCAT_FK_CATEGORY_ID) VALUES (?, ?)");
						stmt.setLong(1, newInstance.getEanCode());
						stmt.setLong(2, catID);
						stmt.executeUpdate();
					} catch (SQLException e) {
						System.err.println("ERROR at ItemDAO - CREATE-Category:"+ newInstance.getDescription()+ "' creation failed!");
					}

				}

				System.out.println("INFO at ItemDAO: " + newInstance.toString()+ " creation successful!");
			} catch (SQLException e) {
				System.err.println("ERROR at ItemDAO - CREATE: Item '"+ newInstance.getDescription()+ "' creation failed!");
			}

			return newInstance.getEanCode();
		}
		return -1;
	}
	
	@Override
	public Item read(Integer ean) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;
		Item itm = new Item(-1, "Unknown Description");

		try {
			stmt = conn.prepareStatement("SELECT * FROM ITEM WHERE ITM_PK_EANCODE = ?");
			stmt.setInt(1, ean);
			ResultSet rs = stmt.executeQuery();
			rs.first();

			int eanTemp = rs.getInt("ITM_PK_EANCODE");
			String descTemp = rs.getString("ITM_DESCRIPTION");

			itm.setEanCode(eanTemp);
			itm.setDescription(descTemp);

		} catch (SQLException e) {
			System.err.println("ERROR at ItemDAO - READ: Item " + ean + " not able to read!");
		}

		return itm;
	}

	@Override
	public void update(Item transientObject) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("UPDATE ITEM SET ITM_DESCRIPTION = ? WHERE ITM_PK_EANCODE = ?");
			stmt.setString(1, transientObject.getDescription());
			stmt.setLong(2, transientObject.getEanCode());
			int affectedRows = stmt.executeUpdate();

			if (affectedRows != 1) {
				System.out.println("WARNING: Item not found "+ transientObject);
			}
		} catch (SQLException e) {
			System.err.println("ERROR at ItemDAO - UPDATE: Item '" + transientObject.getDescription()
			+ "' update failed!");
		}
	}

	@Override
	public void delete(Item persistentObject) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("DELETE FROM ITEM WHERE ITM_PK_EANCODE = ?");
			stmt.setInt(1, persistentObject.getEanCode());
			int affectedRows = stmt.executeUpdate();
			if (affectedRows != 1) {
				System.out.println("WARNING: Item not found "+ persistentObject);
			}
		} catch (SQLException e) {
			System.err.println("ERROR at ItemDaoI: DELETE: Item '"+ persistentObject.getDescription()+ "' delete failed!");
		}
	}

	public Item getItemBySearchterm(String searchterm) {
		Item itmTemp = new Item(-1, "unknown");

		if (!getItemByCategory(searchterm).getDescription().equals("unknown")) {
			itmTemp = getItemByCategory(searchterm);
		} else if (!getItemByName(searchterm).getDescription().equals("unknown")) {
			itmTemp = getItemByName(searchterm);
		} else {
			try {
				itmTemp = read(Integer.parseInt(searchterm));
			} catch (NumberFormatException e) {
				System.err.println("ERROR in ItemDAO - getItemBySearchterm - Not able to convert the input!");
			}
		}

		return itmTemp;
	}
	
	public Item getItemByCategory(String name) {
		PreparedStatement stmt;

		Item itmTemp = new Item(-1, "unknown");

		try {
			stmt = conn.prepareStatement("SELECT * FROM ITEM INNER JOIN PRICE ON ITEM.ITM_PK_EANCODE=PRICE.PRC_FK_ITEM_EANCODE "
			+ "INNER JOIN ITEM_CATEGORY ON ITEM.ITM_PK_EANCODE=ITEM_CATEGORY.ITMCAT_FK_ITEM_EANCODE "
			+ "INNER JOIN CATEGORY ON ITEM_CATEGORY.ITMCAT_FK_CATEGORY_ID=CATEGORY.CAT_PK_ID "
			+ "WHERE CATEGORY_NAME = ? ORDER BY PRC_PRICE ASC");
			stmt.setString(1, name);
			
			ResultSet rs = stmt.executeQuery();
			rs.first();

			int eanTemp = rs.getInt("ITEM_PK_EANCODE");
			String descTemp = rs.getString("ITEM_DESCRIPTION");

			itmTemp.setEanCode(eanTemp);
			itmTemp.setDescription(descTemp);

		} catch (SQLException e) {
			System.err.println("ERROR in ItemDAO - GetItemIDByKategory - Category '"+ name + "' not found!");
		}

		return itmTemp;
	}
	
	public Item getItemByName(String name) {
		PreparedStatement stmt;

		Item itmTemp = new Item(-1, "unknown");

		try {
			stmt = conn.prepareStatement("SELECT * FROM ITEM WHERE ITM_DESCRIPTION = ?");
			stmt.setString(1, name);
			
			ResultSet rs = stmt.executeQuery();
			rs.first();

			int tmp1 = rs.getInt("ITEM_PK_EANCODE");
			String tmp2 = rs.getString("ITEM_DESCRIPTION");

			itmTemp.setEanCode(tmp1);
			itmTemp.setDescription(tmp2);

		} catch (SQLException e) {
			System.err.println("ERROR in ItemDAO - GetItemIDByName - Item '" + name+ "' couldnt been found!");
		}

		return itmTemp;
	}
}
