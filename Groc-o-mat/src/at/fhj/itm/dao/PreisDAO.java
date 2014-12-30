package at.fhj.itm.dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import at.fhj.itm.bl.Item;
import at.fhj.itm.bl.Preis;

public class PreisDAO extends GenericSqlDAO<Preis, Integer>{

	@Override
	public Integer create(Preis newInstance) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;

		// Check if Price is already in the DB
		if (getPreisID(newInstance) != -1) {
			System.out.println("INFO: This price object " + newInstance.getId()+ " already exists in the database!");
		}

		else {

			try {
				stmt = conn.prepareStatement("INSERT INTO PRICE (PRC_FK_ITEM_EANCODE, PRC_PRICE) VALUES (?, ?)");
				stmt.setLong(1, newInstance.getEan());
				stmt.setDouble(2, newInstance.getPrice());
				stmt.executeUpdate();
				System.out.println("INFO: Price with ID: "+ getPreisID(newInstance) + ", creation successful!");
			} catch (SQLException e) {
				System.err.println("ERROR: - PriceDAO - CREATE: Creation failed!");
			}

			return newInstance.getId();
		}

		return -1;
	}

	@Override
	public Preis read(Integer id) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;
		Preis p = new Preis(new Item(-1, "unknown Description"), -1);

		try {
			stmt = conn.prepareStatement("SELECT * FROM PRICE WHERE PRC_PK_ID = ?");
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			rs.first();

			p.setEan(rs.getInt("PRC_FK_ITEM_EANCODE"));
			p.setPrice(Double.parseDouble(rs.getString("PRC_PRICE")));

		} catch (SQLException e) {
			System.err.println("ERROR: - PriceDAO - READ: Price with ID: " + id+ " reading failed!");
		}

		return p;
	}

	@Override
	public void update(Preis transientObject) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("UPDATE PRICE SET PRC_PRICE = ? WHERE PRC_PK_ID = ?");
			stmt.setDouble(1, transientObject.getPrice());
			stmt.setLong(2, transientObject.getId());
			int affectedRows = stmt.executeUpdate();

			if (affectedRows != 1) {
				System.out.println("INFO: price object not found "+ transientObject);
			}
		} catch (SQLException e) {
			System.err.println("ERROR: - PriceDAO - UPDATE: Update failed!");
		}
	}

	@Override
	public void delete(Preis persistentObject) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("DELETE FROM PRICE WHERE PRC_PK_ID = ?");
			stmt.setInt(1, persistentObject.getId());
			int affectedRows = stmt.executeUpdate();

			if (affectedRows != 1) {
				System.out.println("INFO: Price not found "+ persistentObject);
			}
		} catch (SQLException e) {
			System.err.println("ERROR: - PriceDAO - DELETE: Delete failed!");
		}
	}
	
	/**
	 * Gets the PreisID from a 'Preis' instance
	 * @param newInstance - a 'Preis' object
	 * @return an ID if found | -1 if not found
	 */
	public static Integer getPreisID(Preis newInstance) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("SELECT PRC_PK_ID FROM PRICE WHERE PRC_FK_ITEM_EANCODE = ? AND PRC_PRICE = ?");
			stmt.setLong(1, newInstance.getEan());
			stmt.setDouble(2, newInstance.getPrice());
			
			ResultSet rs = stmt.executeQuery();
			rs.first();
			
			return rs.getInt("PRC_PK_ID");
		} catch (SQLException e) {
			System.err.println("ERROR: - PriceDAO - getPreisId: Price not found!");
		}
		return -1;
	}
}
