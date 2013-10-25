package ssc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.InputMismatchException;
import java.util.Scanner;

public class UserInterface {

	Scanner s;
	Connection conn;

	/**
	 * Create an instance of the UserInterface class
	 */
	public UserInterface() {
		this.s = new Scanner(System.in);
		this.conn = DBConnect.connect();
	}

	/**
	 * Main method. Creates an instance of the class and then runs it
	 * @param args
	 */
	public static void main(String[] args) {
		new UserInterface().run();
	}

	/**
	 * (Actual) main method. Forever calls showMenu() in a loop and switches
	 * on the result in order to perform the actions that the menu offers.
	 */
	public void run() {
		while (true) {
			switch (showMenu()) {
			case 1:
				new CreateDB(this.conn).cleanDatabase();
				break;
			case 2:
				s.nextLine();
				registerStudent();
				break;
			case 3:
				s.nextLine();
				addMarks();
				break;
			case 4:
				// s.nextLine();
				produceTranscript();
				break;
			case 5:
				produceReport();
				break;
			case 6:
				System.exit(0);
				break;
			default:
				System.out.println("Invalid input (this should never happen)");
				break;
			}
		}
	}

	/**
	 * Produces a report for a specific year and session
	 */
	private void produceReport() {
		String sql = "SELECT m.courseID, c.coursename, COUNT(m.mark), AVG(m.mark) "
				+ "FROM marks m, course c WHERE m.courseID = c.courseID "
				+ "AND year = ? AND sessionid = ? "
				+ "GROUP BY m.courseID, c.courseName";
		try {
			System.out.println("Please enter the desired year: (1-5)");
			s.nextLine();
			int year = s.nextInt();
			s.nextLine();
			System.out.println("Please enter the desired session: (1-3): ");
			int sessionID = s.nextInt();
			s.nextLine();
			PreparedStatement pStmt = conn.prepareStatement(sql);
			pStmt.setInt(1, year);
			pStmt.setInt(2, sessionID);
			ResultSet rs = pStmt.executeQuery();
			
			System.out.println("Report for year " + year + " session " + sessionID + ":");
			System.out.println("-----------------------------------------------------");
			while(rs.next()) {
				System.out.println("CourseID: " + rs.getInt("courseID") + " CourseName: " 
			+ rs.getString("courseName") + " Count: " + rs.getInt("count") + " Average: " + rs.getFloat("avgs"));
			}
		} catch (InputMismatchException e) {
			System.out.println("You attempted to type a string when an int was requested. Please try again.");
			produceReport();
		} catch (SQLException e) {
			System.out.println("Error occured while executing statement");
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Produces a transcript for a specific student, showing their details and
	 * All marks.
	 * 
	 */
	private void produceTranscript() {
		System.out
				.println("Please enter the student ID to produce a transcript for:");
		s.nextLine();
		int sid = 0;
		try {
			sid = s.nextInt();
		} catch (InputMismatchException e) {
			System.out
					.println("Invalid input, you probably entered a string when an int was requested?");
			System.out.println(e.getMessage());
			produceTranscript();
		}
		try {
			PreparedStatement pStmt = conn
					.prepareStatement("SELECT "
							+ "t.titleString, s.forename, s.familyname, s.sex, s.dateOfBirth, sc.emailaddress, sc.postaladdress "
							+ "FROM titles t, student s, studentcontact sc WHERE s.studentid = sc.studentid "
							+ "AND t.titleid = s.titleid AND s.studentid = ?");
			pStmt.setInt(1, sid);
			ResultSet rs1 = pStmt.executeQuery();

			String title = "";
			String forename = "";
			String familyName = "";
			String sex = "";
			String dateOfBirth = "";
			String emailAddress = "";
			String postalAddress = "";

			if (rs1.next()) {
				title = rs1.getString("titleString");
				forename = rs1.getString("forename");
				familyName = rs1.getString("familyName");
				sex = rs1.getBoolean("sex") ? "m" : "f";
				dateOfBirth = rs1.getDate("dateOfBirth").toString();
				emailAddress = rs1.getString("emailAddress");
				postalAddress = rs1.getString("postalAddress");

			}
			pStmt = conn
					.prepareStatement("SELECT m.year, m.courseid, c.coursename,"
							+ " m.mark, s.sessionString, t.typeString "
							+ "FROM marks m, course c, type t, session s "
							+ "WHERE m.courseid = c.courseid AND m.studentid = ? "
							+ "AND t.typeid = m.typeid AND s.sessionid = m.sessionid "
							+ "ORDER BY year");
			pStmt.setInt(1, sid);

			ResultSet rs2 = pStmt.executeQuery();

			System.out.println("Transcript for " + title + " " + forename + " "
					+ familyName + ":");
			System.out.println("Sex: " + sex);
			System.out.println("BirthDate: " + dateOfBirth);
			System.out.println("Email Address: " + emailAddress);
			System.out.println("Postal Address: " + postalAddress);
			int year = 0;
			int oldyear = 0;
			System.out.println();
			while (rs2.next()) {
				oldyear = year;
				year = rs2.getInt("year");
				if (year > oldyear) {
					System.out.println("--------Year " + year + "--------");
				}
				System.out.println("CourseID: " + rs2.getInt("courseID"));
				System.out
						.println("CourseName: " + rs2.getString("courseName"));
				System.out.println("Mark: " + rs2.getInt("mark"));
				System.out
						.println("Session: " + rs2.getString("sessionString"));
				System.out.println("Type: " + rs2.getString("typeString"));
				System.out.println();
			}

		} catch (SQLException e) {
			System.out.println("Exception occured while executing statement: ");
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Adds a mark to the database for a specific student, course, year and session.
	 */
	private void addMarks() {
		try {
			System.out.println("Please enter student ID of student: ");
			int sid = s.nextInt();
			s.nextLine();
			System.out.println("Please enter course ID: ");
			int cid = s.nextInt();
			s.nextLine();
			System.out.println("Please enter year(1-5)");
			int year = s.nextInt();
			s.nextLine();
			System.out.println("Please enter session (1: May, 2: August)");
			int sessionID = s.nextInt();
			s.nextLine();
			System.out
					.println("Please enter type (1: Normal, 2: Resit, 3: Repeat)");
			int typeID = s.nextInt();
			s.nextLine();
			System.out.println("Please enter mark (1-100)");
			int mark = s.nextInt();
			s.nextLine();
			System.out.println("Please enter any notes:");
			String notes = s.nextLine();
			System.out.print("Adding mark...");
			PreparedStatement pStmt = conn
					.prepareStatement("INSERT INTO MARKS "
							+ "(studentID, courseID, year, sessionID, typeID, mark, notes) "
							+ "VALUES (?, ?, ?, ?, ?, ?, ?)");
			pStmt.setInt(1, sid);
			pStmt.setInt(2, cid);
			pStmt.setInt(3, year);
			pStmt.setInt(4, sessionID);
			pStmt.setInt(5, typeID);
			pStmt.setInt(6, mark);
			pStmt.setString(7, notes);
			pStmt.execute();
			System.out.println("Done");
		} catch (InputMismatchException e) {
			System.out.println("Invalid input, you probably entered a string"
					+ " when and int was required. " + e.getMessage());
		} catch (SQLException e) {
			System.out
					.println("Exception while adding mark: " + e.getMessage());
			System.out.println("Please try again:");
			e.printStackTrace();
			addMarks();

		}

	}

	/**
	 * Registers a new student with the database
	 */
	private void registerStudent() {
		String specifiySID;
		boolean askSID = false;
		while (true) {
			System.out.print("Specify studentID?(y/n): ");
			specifiySID = s.nextLine();
			if (specifiySID.equals("y") || specifiySID.equals("n")) {
				break;
			}
			System.out.println("Please enter only y or n");
		}
		int sid = 0;
		int latestSid = 0;
		if (specifiySID.equals("y")) {
			askSID = true;
		} else {
			try {
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt
						.executeQuery("SELECT MAX(studentID) FROM student");
				if (rs.next()) {
					latestSid = rs.getInt("max") + 1;
					System.out.println("Using student ID: " + latestSid);
				}

			} catch (SQLException e) {
				System.out.println("Exception while getting latest studentID ");
				System.exit(1);
				// e.printStackTrace();
			}

		}
		sid = latestSid;

		int titleID = 0;
		String forename = "";
		String familyName = "";
		int birthYear = 0;
		int birthMonth = 0;
		int birthDay = 0;
		try {
			if (askSID) {
				System.out.print("Please enter desired student ID number: ");
				sid = s.nextInt();
			}

			System.out
					.print("Please enter titleID(1. Mr., 2. Mrs., 3. Miss, 4. Ms., 5. Dr., 6. Lord");
			titleID = s.nextInt();
			s.nextLine();
			System.out.print("Please enter student's forname: ");
			forename = s.nextLine();

			System.out.print("Please enter student's family name: ");
			familyName = s.nextLine();

			System.out.print("Please enter student's year of birth:(YYYY)");
			birthYear = s.nextInt();

			System.out.print("Please enter student's month of birth:(MM)");
			birthMonth = s.nextInt();

			System.out.print("Please enter student's day of birth:(DD)");
			birthDay = s.nextInt();
		} catch (InputMismatchException e) {
			System.out.println(e.getMessage()
					+ " You probably entered a string when "
					+ "an int was expected. Please try again.");
			registerStudent();
		}
		s.nextLine();
		String sex = "";
		while (true) {
			System.out.println("Please enter student's sex:(m/f)");
			sex = s.nextLine();
			if (sex.equals("m") || sex.equals("f")) {
				break;
			}
			System.out.println("Please enter only m or f");
		}

		boolean sexb = sex.equals("m") ? true : false;
		String dob = birthYear + "-" + birthMonth + "-" + birthDay;
		try {
			System.out.println("Attempting to register student...");

			PreparedStatement pStmt = conn
					.prepareStatement("INSERT INTO Student "
							+ "(studentID, titleID, forename, familyName, dateOfBirth, sex) "
							+ "VALUES( ?, ?, ?, ?, ?, ?)");

			pStmt.setInt(1, sid);
			pStmt.setInt(2, titleID);
			pStmt.setString(3, forename);
			pStmt.setString(4, familyName);
			java.util.Date utildate;
			try {
				utildate = new SimpleDateFormat("yyyy-MM-dd").parse(dob);
				pStmt.setDate(5, new java.sql.Date(utildate.getTime()));
			} catch (ParseException e) {

				System.out.println("Invalid date format");
				e.printStackTrace();
			}
			pStmt.setBoolean(6, sexb);
			pStmt.execute();
			System.out.println("Done");
		} catch (SQLException e) {
			System.out.println("Exception while attempting to insert student:");
			System.out.println(e.getMessage());
			System.out.println("Please try again.");
			registerStudent();
		}
	}

	/**
	 * Shows the menu and asks the user for input
	 * @return the option on the menu that the user chose
	 */
	private int showMenu() {
		System.out.println();
		System.out
				.println("Main menu, please select what you would like to do:");
		System.out
				.println("1. Clean Database (drops, creates and populate all tables)");
		System.out.println("2. Register new student");
		System.out.println("3. Add marks for a student");
		System.out.println("4. Produce academic transcript for a Student");
		System.out.println("5. Produce a report");
		System.out.println("6. Quit");
		System.out.println();
		System.out.print("> ");
		boolean validInput = false;
		int res;
		while (!validInput) {
			while (!s.hasNextInt()) {
				System.out.println("Please enter an integer.");
				s.nextLine();
			}
			res = s.nextInt();
			if (!(res > 6 || res < 1)) {
				validInput = true;
				return res;
			} else {
				System.out
						.println("Invalid input, please enter only a number from 1-6");
				System.out.print("> ");
			}
		}
		return -1;
	}
}
