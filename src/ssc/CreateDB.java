package ssc;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CreateDB {

	private String[] tables = { "NextOfKin", "StudentContact", "Marks",
			"Course", "Lecturer", "Student", "Type", "Session", "Titles" };
	private Connection conn;
	Random random = new Random(54123);

	public static void main(String[] args) {
		new CreateDB().cleanDatabase();
	}

	public void cleanDatabase() {
		try {
			conn.setAutoCommit(false);
			dropTables();
			createTables();
			populateTables();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			System.err
					.println("Exception while cleaning database. (This message should never be triggered).");
			e.printStackTrace();
		}
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
			// System.exit(0);
		}

	}

	public void createTables() {
		// Reverse the array
		List<String> tablesList = Arrays.asList(tables);
		Collections.reverse(tablesList);
		// String[] rTables = (String[]) tablesList.toArray();
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

		populateTitles();

		populateLecturer();

		populateStudent();

		populateCourse();

		populateSession();

		populateType();

		populateMarks();

		populateStudentContact();

		populateNextOfKin();

	}

	private void populateNextOfKin() {
		try {
			System.out.print("Populating table NextOfKin...");
			Statement stmt = conn.createStatement();
			for (int i = 1; i <= 100; ++i) {
				int sid = i;
				String emailAddress = "nextofkin" + i + "@email.com";
				String postalAddress = (i * 3)
						+ " Example Road, Example Town, EXM PL" + (i * 2);

				stmt.executeUpdate("INSERT INTO NextOfKin "
						+ "(studentID, emailAddress, postalAddress) "
						+ "VALUES (" + sid + ", '" + emailAddress + "', '"
						+ postalAddress + "')");
			}
			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while populating table NextOfKin");
			e.printStackTrace();
		}
	}

	private void populateStudentContact() {
		try {
			System.out.print("Populating table StudentContact...");
			Statement stmt = conn.createStatement();
			for (int i = 1; i <= 100; ++i) {
				int sid = i;
				String emailAddress = "student" + i + "@email.com";
				String postalAddress = (i * 3)
						+ " Example Road, Example Town, EXM PL" + i;

				stmt.executeUpdate("INSERT INTO StudentContact "
						+ "(studentID, emailAddress, postalAddress) "
						+ "VALUES (" + sid + ", '" + emailAddress + "', '"
						+ postalAddress + "')");
			}
			System.out.println("Done");
		} catch (SQLException e) {
			System.err
					.println("Exception while populating table StudentContact");
			e.printStackTrace();
		}
	}

	private void populateMarks() {
		try {
			System.out.print("Populating table Marks...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Marks "
					+ "(studentID, courseID, year, sessionID, typeID, mark, notes) "
					+ "VALUES (2, 1, 1, 1, 1, 67, 'Aim was lacking but other aspects ok.'), "
					+ "(2, 2, 2, 1, 1, 82, 'Good macro strategy and build'), "
					+ "(2, 3, 3, 2, 2, 40, 'Good mark but capped at 40 due to resit'), "
					+ "(2, 4, 4, 1, 1, 70, 'Reasonable understanding of haskell language'), "
					+ "(2, 5, 5, 1, 1, 60, 'Good understanding of algorithms and ai concepts'), "
					+ "(3, 2, 2, 1, 1, 90, 'Diamond league level player'), "
					+ "(3, 3, 3, 1, 1, 60, 'Good understanding of databases and multithreading.'), "
					+ "(3, 4, 4, 1, 1, 82, 'Reasonable understanding of haskell language'), "
					+ "(3, 5, 5, 1, 1, 65, 'Reasonable understanding of algorithms and ai concepts')");

			for (int i = 6; i <= 100; ++i) {
				int sid = i;
				int cid = random.nextInt(99) + 1;
				int year = random.nextInt(4) + 1;
				int sesid = random.nextInt(1) + 1;
				int tid = random.nextInt(2) + 1;
				int mark = random.nextInt(99) + 1;
				String notes = "Notes for mark " + i;

				stmt.executeUpdate("INSERT INTO Marks "
						+ "(studentID, courseID, year, sessionID, typeID, mark, notes) "
						+ "VALUES (" + sid + ", " + cid + ", " + year + ", "
						+ sesid + ", " + tid + ", " + mark + ", '" + notes
						+ "')");

			}

			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while populating table Marks");
			e.printStackTrace();
		}
	}

	private void populateType() {
		try {
			System.out.print("Populating table Type...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Type " + "(typeID, typeString) "
					+ "VALUES (1, 'Normal'), " + "(2, 'Resit'), "
					+ "(3, 'Repeat')");
			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while populating table Type.");
			e.printStackTrace();
		}
	}

	private void populateSession() {
		try {
			System.out.print("Populating table Session...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Session "
					+ "(sessionID, sessionString) "
					+ "VALUES (1, 'May'), (2, 'August')");
			System.out.println("Done");

		} catch (SQLException e) {
			System.err.println("Exception while populating table Session");
			e.printStackTrace();
		}
	}

	private void populateCourse() {
		try {
			System.out.print("Populating table Course...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Course "
					+ "(courseID, courseName, courseDescription, lecturerID) "
					+ "VALUES (1, 'Introduction to Counter Strike', "
					+ "'An introduction by Dr. Tank to the FPS Counter strike.', 4), "
					+ "(2, 'Protoss 101', 'Paul Dines teaches starcraft strategies.', 5), "
					+ "(3, 'Software System Components A', 'Sql, databases, threading, web in java.', 2), "
					+ "(4, 'Functional Programming', 'Functional Programming in Haskell.', 1), "
					+ "(5, 'Robot Programming', 'Programming Lego NXT robots in java', 3)");
			for (int i = 6; i <= 100; ++i) {
				int courseID = i;
				String courseName = "CourseName " + i;
				String courseDescription = "CourseDescription " + i;
				int lecturerID = random.nextInt(4) + 1;
				stmt.executeUpdate("INSERT INTO Course "
						+ "(courseID, courseName, courseDescription, lecturerID) "
						+ "VALUES (" + courseID + ", '" + courseName + "', '"
						+ courseDescription + "', " + lecturerID + ")");
			}
			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while populating table Course");
			e.printStackTrace();
		}
	}

	private void populateStudent() {
		try {
			System.out.print("Populating table Student...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Student "
					+ "(studentID, titleID, forename, familyName, dateOfBirth, sex) "
					+ "VALUES (1, 1, 'Samuel', 'Hill', '1993-07-15', True), "
					+ "(2, 1, 'Luke', 'Cross', '1994-05-19', True), "
					+ "(3, 2, 'Tara', 'Dein', '1992-11-26', False), "
					+ "(4, 1, 'Matthew', 'Masters', '1990-01-03', True), "
					+ "(5, 1, 'George', 'Collins', '1993-10-27', True)");

			String[] firstNames = { "James", "John", "Andrew", "Andreas",
					"Paul", "Joel", "Samuel", "Jack", "George", "Tristan",
					"Christopher", "Victor", "Matthew", "Katie", "Sarah",
					"Hannah", "Tara", "Natalie", "Pauline", "Geoffrey", "Beth",
					"Bob", "Oscar" };

			String[] familyNames = { "Dines", "Hill", "Cross", "Tank",
					"Holley", "Bevan", "Bell", "Collins", "Danielson",
					"Simmonds", "Lanza", "Hendley", "Hawes", "Vickers",
					"McKeand", "Stanley", "Raskino" };

			for (int i = 6; i <= 100; ++i) {
				String forename = firstNames[random.nextInt(firstNames.length)];
				String familyName = familyNames[random
						.nextInt(familyNames.length)];
				int studentID = i;
				int titleID = random.nextInt(4) + 1;
				int year = random.nextInt(20) + 1980;
				int day = random.nextInt(28) + 1;
				int month = random.nextInt(12) + 1;
				boolean sex = random.nextFloat() > 0.5 ? true : false;

				stmt.executeUpdate("INSERT INTO Student "
						+ "(studentID, titleID, forename, familyName, dateOfBirth, sex) "
						+ "VALUES(" + studentID + ", " + titleID + ", '"
						+ forename + "', '" + familyName + "', '" + year + "-"
						+ month + "-" + day + "', " + sex + ")");

				// System.out.println(forename + " " + familyName);
			}
			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while populating table Student");
			e.printStackTrace();
		}
	}

	private void populateLecturer() {
		try {
			System.out.print("Populating table Lecturer...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Lecturer "
					+ "(lecturerID, titleID, forename, familyName)"
					+ "VALUES (1, 5, 'Martin', 'Escardó'), "
					+ "(2, 5, 'Robert', 'Hendley'), "
					+ "(3, 5, 'Nick', 'Hawes'), "
					+ "(4, 5, 'Dhruvil', 'Tank'), " + "(5, 5, 'Paul', 'Dines')");
			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while populating table Lecturer");
			e.printStackTrace();
		}
	}

	private void populateTitles() {
		try {
			System.out.print("Populating table Titles...");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO Titles "
					+ "(titleID, titleString) " + "VALUES (1, 'Mr.'), "
					+ "(2, 'Mrs.'), " + "(3, 'Miss'), " + "(4, 'Ms.'), "
					+ "(5, 'Dr.'), " + "(6, 'Lord')");
			System.out.println("Done");
		} catch (SQLException e) {
			System.err.println("Exception while populating table Titles");
			e.printStackTrace();
		}
	}

}
