package at.fhj.itm;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;

import at.fhj.itm.utils.DBConnector;

public class DBTests {

	@Test
	public void testConnectionFactory() throws SQLException 
	{
		Connection c = DBConnector.getConnection();
		Statement s = c.createStatement();
		s.execute("SELECT * FROM ITEMS");
		//Test is marked as SUCCESSFUL when no exception is thrown
	}

}
