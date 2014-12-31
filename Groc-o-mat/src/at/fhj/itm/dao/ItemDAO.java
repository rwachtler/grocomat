package at.fhj.itm.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.fhj.itm.bl.Item;

public class ItemDAO extends GenericSqlDAO<Item, Integer> {
	//declaration
	private CategoryDAO catDAO;

	//constructor
	public ItemDAO() {
		catDAO = new CategoryDAO();
	}

	@Override
	public Integer create(Item newInstance) {
		PreparedStatement stmt;

		// Check, if items is already in the DB
		if (read(newInstance.getEanCode()).getEanCode() != -1) {
			System.out.println("INFO: ItemDAO - Item " + newInstance.toString()+ " already exists in the database!");
		}

		else {

			try {
				stmt = conn.prepareStatement("INSERT INTO ITEM (ITM_PK_EANCODE, ITM_DESCRIPTION) VALUES (?, ?)");
				stmt.setLong(1, newInstance.getEanCode());
				stmt.setString(2, newInstance.getDescription());
				stmt.executeUpdate();

				for (String category_string : newInstance.getKategorien()) {
					int category_id = -1;

					category_id = CategoryDAO.getCategoryIDbyName(category_string);

					if (category_id < 0) {
						category_id = catDAO.create(category_string);
					}

					try {
						stmt = conn.prepareStatement("INSERT INTO ITEM_CATEGORY (ITMCAT_FK_ITEM_EANCODE, ITMCAT_FK_CATEGORY_ID) VALUES (?, ?)");
						stmt.setLong(1, newInstance.getEanCode());
						stmt.setLong(2, category_id);
						stmt.executeUpdate();
					} catch (SQLException e) {
						System.err.println("ERROR: ItemDAO - CREATE-Category: '"+ newInstance.getDescription()
						+ "CATEGORY ID: "+ category_id+ "' creation failed!");
					}

				}

				System.out.println("INFO: Item " + newInstance.toString()+ " creation succeeded!");
			} catch (SQLException e) {
				System.err.println("ERROR: ItemDAO - CREATE - Item '"+ newInstance.getDescription()+ "' creation failed!");
			}

			return newInstance.getEanCode();
		}
		return -1;
	}

	@Override
	public Item read(Integer eancode) {
		PreparedStatement stmt;
		Item p = new Item(-1, "Unknown Description");

		try {
			stmt = conn.prepareStatement("SELECT * FROM ITEM WHERE ITM_PK_EANCODE = ?");
			stmt.setInt(1, eancode);
			
			ResultSet rs = stmt.executeQuery();
			rs.first();

			int eanTemp = rs.getInt("ITM_PK_EANCODE");
			String descTemp = rs.getString("ITM_DESCRIPTION");

			p.setEanCode(eanTemp);
			p.setDescription(descTemp);

		} catch (SQLException e) {
			System.err.println("ERROR: ItemDAO - READ - Item " + eancode + " access failed!");
		}

		return p;
	}

	@Override
	public void update(Item transientObject) {

		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("UPDATE ITEM SET ITM_DESCRIPTION = ? WHERE ITM_PK_EANCODE = ?");
			stmt.setString(1, transientObject.getDescription());
			stmt.setLong(2, transientObject.getEanCode());
			int affectedRows = stmt.executeUpdate();

			if (affectedRows != 1) {
				System.out.println("INFO: Item not found "+ transientObject);
			}
		} catch (SQLException e) {
			System.err.println("ERROR: ItemDAO - UPDATE - Item '"+ transientObject.getDescription()+ "' update failed!");
		}

	}

	@Override
	public void delete(Item persistentObject) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("DELETE FROM ITEM WHERE ITM_PK_EANCODE = ?");
			stmt.setInt(1, persistentObject.getEanCode());
			int affectedRows = stmt.executeUpdate();

			if (affectedRows != 1) {
				System.out.println("INFO: Item not found "+ persistentObject);
			}
		} catch (SQLException e) {
			System.err.println("ERROR: ItemDAO - DELETE - Item '"+ persistentObject.getDescription()+ "' delete operation failed!");
		}
	}


	public Item getItemBySearchterm(String input) {
		Item tmp = new Item(-1, "unknown");

		if (!getItemByCategoryName(input).getDescription().equals("unknown")) {
			tmp = getItemByCategoryName(input);
		} else if (!getItemByName(input).getDescription().equals("unknown")) {
			tmp = getItemByName(input);
		} else {
			try {
				tmp = read(Integer.parseInt(input));
			} catch (NumberFormatException e) {
				System.err.println("ERROR: ItemDAO - GetItembyInput(): Conversion error!");
			}
		}

		return tmp;
	}

	/**
	 * Gets an item for the given categoryname
	 * @param name - categoryname
	 * @return item
	 */
	public Item getItemByCategoryName(String name) {
		PreparedStatement stmt;

		Item item = new Item(-1, "unknown");

		try {
			stmt = conn.prepareStatement("SELECT * FROM ITEM INNER JOIN PRICE ON ITEM.ITM_PK_EANCODE=PRICE.PRC_FK_ITEM_EANCODE "
			+ "INNER JOIN ITEM_CATEGORY ON ITEM.ITM_PK_EANCODE=ITEM_CATEGORY.ITMCAT_FK_ITEM_EANCODE "
			+ "INNER JOIN CATEGORY ON ITEM_CATEGORY.ITMCAT_FK_CATEGORY_ID=CATEGORY.CAT_PK_ID "
			+ "WHERE CAT_NAME = ? ORDER BY PRC_PRICE ASC");
			stmt.setString(1, name);
			
			ResultSet rs = stmt.executeQuery();
			rs.first();

			int eanTemp = rs.getInt("ITM_PK_EANCODE");
			String descTemp = rs.getString("ITM_DESCRIPTION");

			item.setEanCode(eanTemp);
			item.setDescription(descTemp);

		} catch (SQLException e) {
			System.err.println("ERROR: ItemDAO - GetItemIDByKategory() - '"+ name + "' not found!");
		}

		return item;
	}

	/**
	 * Gets an item for the given item description
	 * @param desc - item description
	 * @return item matching to this description 
	 */
	public Item getItemByName(String desc) {
		PreparedStatement stmt;

		Item item = new Item(-1, "unknown");

		try {
			stmt = conn.prepareStatement("SELECT * FROM ITEM WHERE ITM_DESCRIPTION = ?");
			stmt.setString(1, desc);
			
			ResultSet rs = stmt.executeQuery();
			rs.first();

			int tempEan = rs.getInt("ITM_PK_EANCODE");
			String tempDesc = rs.getString("ITM_DESCRIPTION");

			item.setEanCode(tempEan);
			item.setDescription(tempDesc);

		} catch (SQLException e) {
			System.err.println("ERROR: ItemDAO - GetItemIDByName() - Item '" + desc+ "' not found!");
		}

		return item;
	}

}
