package ssc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A Class with static methods connect creating a connection to the database
 * @author Andreas
 *
 */
public class DBConnect {
	
	
	/**
	 * Main method used for testing the connection initially
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		// Test the connection
		Connection conn = connect("ssclibrary");
		PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM BOOKS");
		ResultSet set = pStmt.executeQuery();
		
		while(set.next()) {
			System.out.println(set.getString("title"));
		}
		
	}
	
	
	/**
	 * Create a connection to the default database (amh226)
	 * @return the created connection object
	 */
	public static Connection connect() {
		return connect("amh226");
	}
	
	
	/**
	 * Create a connection to a custom database
	 * @param database the database to connect to 
	 * @return the conection object
	 */
	public static Connection connect(String database) {
		
		try {
			
			Class.forName("org.postgresql.Driver");
			
		} catch (ClassNotFoundException ex) {
			
			System.out.println("Driver not found");
			
		}
		
		System.out.println("PostgreSQL driver registered.");
		Connection conn = null;
		
		try {
			
			conn = DriverManager.getConnection("jdbc:postgresql://dbteach2.cs.bham.ac.uk/" + database, "amh226", "spuprast");
			
		} catch (SQLException ex) {
			
			ex.printStackTrace();
			
		}
		
		if (conn != null) {
			
			System.out.println("Database accessed!");
			
		} else {
			
			System.out.println("Failed to make connection");
			
		}
		
		return conn;

	}
}
