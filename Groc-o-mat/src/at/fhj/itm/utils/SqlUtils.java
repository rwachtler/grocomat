package at.fhj.itm.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class SqlUtils 
{
	/**
	 * Liefert den Max Integer einer Spalte in einer Tabelle zurück.
	 * @param tablename
	 * @param columnname
	 * @return
	 */
	public static int getMaxInt(String tablename, String columnname)
	{
		int ret = -1;
		
		try {
			// Liest den letzten Integer aus einer Tabelle raus
			Connection c = DBConnector.getConnection();
			PreparedStatement s = c.prepareStatement("SELECT MAX(" + columnname + ") FROM " + tablename);	
			
			// Ergebnisse holen
			ResultSet rs = s.executeQuery();
			// Auf erste Zeile stellen
			rs.first();
			
			// Auslesen des Wertes
			ret = rs.getInt(1);
			
			// ResultSet schließen
			rs.close();
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.err.println("Max(" + columnname + ") von " + tablename + " konnte nicht bestimmt werden");
		}
		
		return ret;
	}
	
	/**
	 * Erzeugt aus einem ResultSet ein TableModel
	 * 
	 * @see http://stackoverflow.com/questions/10620448/most-simple-code-to-populate-jtable-from-resultset
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {

	    ResultSetMetaData metaData = rs.getMetaData();

	    // names of columns
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = metaData.getColumnCount();
	    for (int column = 1; column <= columnCount; column++) {
	        columnNames.add(metaData.getColumnLabel(column));
	    }

	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	            vector.add(rs.getObject(columnIndex));
	        }
	        data.add(vector);
	    }
	    
	    return new DefaultTableModel(data, columnNames);

	}
	
	
}
