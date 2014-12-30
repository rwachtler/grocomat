package at.fhj.itm.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import at.fhj.itm.utils.DBConnector;
import at.fhj.itm.utils.ScriptExecutor;

/**
 * Creates and returns a new database connection
 * Initscript will be executed when started
 * 
 * @author R.Wachtler
 *
 */
public class DBConnector {

	private static final String DB_PROPFILE = "db.properties";
	
	private static String db_username 		= "";
	private static String db_password 		= "";
	private static String jdbc_driver 		= "";
	private static String jdbc_url			= "";
	private static String app_initscript	= "";
	
	//Will be called once
		static
		{
			//Read all properties from db.properties file
			Properties props = new Properties();
			try(InputStream in = DBConnector.class.getClassLoader().getResourceAsStream(DB_PROPFILE))
			{
				props.load(in);
				in.close();
				
				//Set properties
				db_username = props.getProperty("db.user");
				db_password = props.getProperty("db.password");
				jdbc_driver = props.getProperty("jdbc.driver");
				jdbc_url = props.getProperty("jdbc.url");
				app_initscript = props.getProperty("application.initscript");
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				System.err.println(DB_PROPFILE + " nicht gefunden");
				System.exit(1);
			}
			
			//Checking for the DB Driver
			try 
			{
				Class.forName(jdbc_driver);
			}
			catch (ClassNotFoundException e) 
			{
				e.printStackTrace();
				System.err.println("jdbc.driver konnte nicht geladen werden");
				System.exit(3);
			}
			
			//Initializing the database
			if(app_initscript != null)
			{
				try(InputStream in = DBConnector.class.getClassLoader().getResourceAsStream(app_initscript))
				{				
					ScriptExecutor s = new ScriptExecutor(DBConnector.getConnection(), true, true);
					s.runScript(new InputStreamReader(in));
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
					System.err.println("application.initscript nicht gefunden");
					System.exit(3);
				}
				catch(SQLException e)
				{
					e.printStackTrace();
					System.err.println("application.initscript hat einen Fehler geworfen");
					System.exit(4);
				}
			}
		}
		
		public static Connection getConnection()
		{
			Connection theConnection = null;

			try 
			{
				theConnection = DriverManager.getConnection(jdbc_url, db_username, db_password);
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
				return null;
			}
			return theConnection;
		}
}
