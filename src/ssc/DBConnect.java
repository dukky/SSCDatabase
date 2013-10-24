package ssc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnect {
	public static void main(String[] args) throws SQLException {
		// Test the connection
		Connection conn = connect("ssclibrary");
		PreparedStatement pStmt = conn.prepareStatement("SELECT * FROM BOOKS");
		ResultSet set = pStmt.executeQuery();
		
		while(set.next()) {
			System.out.println(set.getString("title"));
		}
		
	}
	public static Connection connect() {
		return connect("amh226");
	}
	
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
