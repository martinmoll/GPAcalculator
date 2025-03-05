package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.ClassInfo;

public class DatabaseService {
    private Connection connect() {
        String url = "jdbc:sqlite:gpa_database.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
        return conn;
    }

    // Initialize the database and create tables if they don't exist
    public void initDatabase() {
        String createStudentsTable = "CREATE TABLE IF NOT EXISTS Students (" +
                "student_id TEXT PRIMARY KEY, " +
                "name TEXT NOT NULL" +
                ");";
        String createGradesTable = "CREATE TABLE IF NOT EXISTS Grades (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id TEXT, " +
                "class_id TEXT NOT NULL, " +
                "grade TEXT NOT NULL, " +
                "FOREIGN KEY (student_id) REFERENCES Students(student_id)" +
                ");";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createStudentsTable);
            stmt.execute(createGradesTable);
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    // Save a new student to the database
    public void saveStudent(String studentID, String name) {
        String sql = "INSERT INTO Students (student_id, name) VALUES (?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentID);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving student: " + e.getMessage());
        }
    }

    // Check if a student exists
    public boolean studentExists(String studentID) {
        String sql = "SELECT COUNT(*) FROM Students WHERE student_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentID);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.out.println("Error checking student: " + e.getMessage());
            return false;
        }
    }

    // Save grades for a student
    public void saveGrades(String studentID, List<ClassInfo> classes) {
        // First, delete existing grades for this student to avoid duplicates
        String deleteSql = "DELETE FROM Grades WHERE student_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
            pstmt.setString(1, studentID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting old grades: " + e.getMessage());
        }

        // Now insert the new grades
        String insertSql = "INSERT INTO Grades (student_id, class_id, grade) VALUES (?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (ClassInfo c : classes) {
                String grade = c.getGrade();
                if (grade != null && !grade.trim().isEmpty()) {
                    pstmt.setString(1, studentID);
                    pstmt.setString(2, c.getID());
                    pstmt.setString(3, grade);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error saving grades: " + e.getMessage());
        }
    }

    // Load grades for a student
    public List<ClassInfo> loadGrades(String studentID) {
        List<ClassInfo> classes = new ArrayList<>();
        String sql = "SELECT class_id, grade FROM Grades WHERE student_id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentID);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String classID = rs.getString("class_id");
                String grade = rs.getString("grade");
                ClassInfo classInfo = new ClassInfo(classID);
                classInfo.getClassField().setText(grade);
                classes.add(classInfo);
            }
        } catch (SQLException e) {
            System.out.println("Error loading grades: " + e.getMessage());
        }
        return classes;
    }

    public void printDatabaseContents() {
        // Print Students table
        System.out.println("Students Table:");
        String studentSql = "SELECT * FROM Students";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(studentSql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Student ID: " + rs.getString("student_id") + ", Name: " + rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error printing Students: " + e.getMessage());
        }
    
        // Print Grades table
        System.out.println("\nGrades Table:");
        String gradesSql = "SELECT * FROM Grades";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(gradesSql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id") + ", Student ID: " + rs.getString("student_id") +
                        ", Class ID: " + rs.getString("class_id") + ", Grade: " + rs.getString("grade"));
            }
        } catch (SQLException e) {
            System.out.println("Error printing Grades: " + e.getMessage());
        }
    }
}