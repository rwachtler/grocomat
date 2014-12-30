package at.fhj.itm.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryDAO extends GenericSqlDAO<String, Integer>{
	
	@Override
	public Integer create(String newInstance) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;

		//Checking for the category in db
		if (getCategoryIDbyName(newInstance) != -1) {
			System.out.println("Category " + newInstance+" already existing in database!");
		}

		else {
			int catID = -1;
			try {
				stmt = conn.prepareStatement("INSERT INTO CATEGORY (CAT_NAME) VALUES (?)");
				stmt.setString(1, newInstance);
				stmt.executeUpdate();
				catID=getCategoryIDbyName(newInstance);
				System.out.println("INFO: - Category has been created!");
			} catch (SQLException e) {
				System.err.println("ERROR: - CategoryDAO - CREATE Category: "+ newInstance + " creation failed!");
			}

			return catID;
		}

		return -1;
	}

	@Override
	public String read(Integer id) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;
		String res = "";
		try {
			stmt = conn.prepareStatement("SELECT CAT_NAME FROM CATEGORY WHERE CAT_PK_ID = ?");
			stmt.setInt(1, id);
			
			ResultSet rs = stmt.executeQuery();
			rs.first();

			res = rs.getString("CAT_NAME");

		} catch (SQLException e) {
			System.err.println("ERROR in CategoryDAO - READ - Category with ID: " + id+ " reading operation failed!");
		}

		return res;
	}

	@Override
	public void update(String transientObject) {}

	@Override
	public void delete(String persistentObject) {
		// TODO Auto-generated method stub
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("DELETE FROM CATEGORY WHERE CAT_NAME = ?");
			stmt.setString(1, persistentObject);
			int affectedRows = stmt.executeUpdate();

			if (affectedRows != 1) {
				System.out.println("Category not found: "+ persistentObject);
			}
		} catch (SQLException e) {
			System.err.println("ERROR in CategoryDAO - DELETE - operation failed!");
		}
	}
	
	/**
	 * Gets a categoryID for a given category name
	 * @param newInstance - Category name
	 * @return ID if found | -1 if not found
	 */
	public static Integer getCategoryIDbyName(String newInstance) {
		PreparedStatement stmt;
		try {
			stmt = conn.prepareStatement("SELECT CAT_PK_ID FROM CATEGORY WHERE CAT_NAME = ?");
			stmt.setString(1, newInstance);
			
			ResultSet rs = stmt.executeQuery();
			rs.first();
			
			return rs.getInt("CAT_PK_ID");
		} catch (SQLException e) {
			System.err.println("ERROR in CategoryDAO - getCategoryId - Category not found!");
		}
		return -1;
	}
}
