package ssc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CreateDB {

	private static final String[] tables = { "NextOfKin", "StudentContact",
			"Marks", "Course", "Lecturer", "Student", "Type", "Session",
			"Titles" };
	private Connection conn;

	public static void main(String[] args) {
		new CreateDB().cleanDatabase();
	}
	
	public void cleanDatabase() {
		dropTables();
		createTables();
		populateTables();
	}

	public CreateDB() {
		this.conn = DBConnect.connect();
	}

	public void dropTables() {

		for (String table : tables) {
			dropTable(table);
		}
		System.out.println("Successfully dropped all tables.");
	}

	private void dropTable(String table) {

		try {
			System.out.print("Attempting to drop table " + table + "...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("DROP TABLE " + table);
			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while trying to drop table " + table);
			e.printStackTrace();
			//System.exit(0);
		}

	}

	public void createTables() {
		// Reverse the array
		List<String> tablesList = Arrays.asList(tables);
		Collections.reverse(tablesList);
		String[] rTables = (String[]) tablesList.toArray();
		for (String table : tables) {
			
			createTable(table);
			
		}
		
		System.out.println("Successfully created all tables.");

	}

	private void createTable(String table) {
		String sql = "";
		// Was originally a switch statement, converted to if-elseif-else
		// in order to work on Java 1.6 if necessary (which doesn't support
		// switching on strings).
		if (table.equals("Titles")) {
			sql = "CREATE TABLE Titles ( "
					+ "titleID INT NOT NULL PRIMARY KEY, "
					+ "titleString VARCHAR(20) NOT NULL)";
		} else if (table.equals("Session")) {
			sql = "CREATE TABLE Session ( "
					+ "sessionID INT NOT NULL PRIMARY KEY, "
					+ "sessionString VARCHAR(20) NOT NULL)";
		} else if (table.equals("Type")) {
			sql = "CREATE TABLE Type ( " + "typeID INT NOT NULL PRIMARY KEY, "
					+ "typeString VARCHAR(20) NOT NULL)";
		} else if (table.equals("Student")) {
			sql = "CREATE TABLE Student ( "
					+ "studentID INT NOT NULL PRIMARY KEY, "
					+ "titleID INT NOT NULL REFERENCES Titles (titleID), "
					+ "forename VARCHAR(30) NOT NULL, "
					+ "familyName VARCHAR(50), "
					+ "dateOfBirth DATE NOT NULL, " + "sex BOOLEAN NOT NULL)";
		} else if (table.equals("Lecturer")) {
			sql = "CREATE TABLE Lecturer ( "
					+ "lecturerID INT NOT NULL PRIMARY KEY, "
					+ "titleID INT NOT NULL REFERENCES Titles (titleID), "
					+ "forename VARCHAR(30) NOT NULL, "
					+ "familyName VARCHAR(50) NOT NULL)";
		} else if (table.equals("Course")) {
			sql = "CREATE TABLE Course ( "
					+ "courseID INT NOT NULL PRIMARY KEY, "
					+ "courseName VARCHAR(50) NOT NULL, "
					+ "courseDescription VARCHAR(255) NOT NULL, "
					+ "lecturerID INT NOT NULL REFERENCES Lecturer (lecturerID))";
		} else if (table.equals("Marks")) {
			sql = "CREATE TABLE Marks ( "
					+ "studentID INT NOT NULL REFERENCES Student (studentID), "
					+ "courseID INT NOT NULL REFERENCES Course (courseID), "
					+ "year INT NOT NULL CHECK (year BETWEEN 1 AND 5), "
					+ "sessionID INT NOT NULL REFERENCES Session (sessionID), "
					+ "typeID INT NOT NULL REFERENCES Type (typeID), "
					+ "mark INT NOT NULL CHECK (mark BETWEEN 0 AND 100), "
					+ "notes VARCHAR(255))";
		} else if (table.equals("StudentContact")) {
			sql = "CREATE TABLE StudentContact ( "
					+ "studentID INT NOT NULL REFERENCES Student (studentID), "
					+ "emailAddress VARCHAR(255) CHECK (emailAddress LIKE '%@%'), "
					+ "postalAddress VARCHAR(255))";
		} else if (table.equals("NextOfKin")) {
			sql = "CREATE TABLE NextOfKin ( "
					+ "studentID INT NOT NULL REFERENCES Student (studentID), "
					+ "emailAddress VARCHAR(255) CHECK (emailAddress LIKE '%@%'), "
					+ "postalAddress VARCHAR(255))";
		} else {
			throw new IllegalArgumentException("Invalid table name specified");
		}
		try {
			System.out.print("Attempting to create table " + table + "...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(sql);
			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while creating table " + table);
			e.printStackTrace();
			System.exit(0);
		}

	}
	
	public void populateTables() {
		try {
			System.out.print("Populating table Titles...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Titles "
					+ "(titleID, titleString) "
					+ "VALUES (1, 'Mr.'), "
					+ "(2, 'Mrs.'), "
					+ "(3, 'Miss'), "
					+ "(4, 'Ms.'), "
					+ "(5, 'Dr.'), "
					+ "(6, 'Lord')");
			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while populating table Titles");
			e.printStackTrace();
		}
		try {
			System.out.print("Populating table Lecturer...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Lecturer "
					+ "(lecturerID, titleID, forename, familyName)"
					+ "VALUES (1, 5, 'Martin', 'Escardó'), "
					+ "(2, 5, 'Robert', 'Hendley'), "
					+ "(3, 5, 'Nick', 'Hawes'), "
					+ "(4, 5, 'Dhruvil', 'Tank'), "
					+ "(5, 5, 'Paul', 'Dines')");
			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while populating table Lecturer");
			e.printStackTrace();
		}
		try {
			System.out.print("Populating table Student...");
			Statement stmt = conn.createStatement();
		} catch (SQLException e) {
			System.err.println("Exception while populating table Student");
			e.printStackTrace();
		}
	}
	
}
