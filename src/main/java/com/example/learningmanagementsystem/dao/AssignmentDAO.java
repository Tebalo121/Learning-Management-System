package com.example.learningmanagementsystem.dao;

import com.example.learningmanagementsystem.Assignment;
import com.example.learningmanagementsystem.DatabaseConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {

    // Save a new assignment to DB
    public void createAssignment(int courseId, String title, LocalDate dueDate, String description) throws SQLException {
        String sql = "INSERT INTO assignments (course_id, title, due_date, max_score, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            stmt.setString(2, title);
            stmt.setObject(3, dueDate);
            stmt.setInt(4, 100); // Default max score
            stmt.setString(5, description);
            stmt.executeUpdate();
        }
    }

    // Instructors view their assignments
    public List<Assignment> getAssignmentsByInstructor(int instructorId) throws SQLException {
        String sql = """
            SELECT a.assignment_id, a.title, a.due_date, c.description, c.course_name
            FROM assignments a
            JOIN courses c ON a.course_id = c.course_id
            WHERE c.instructor_id = ?
            ORDER BY a.due_date
        """;

        List<Assignment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Assignment(
                        rs.getInt("assignment_id"),
                        rs.getString("course_name"),
                        rs.getString("title"),
                        rs.getDate("due_date").toString(),
                        rs.getString("description"),
                        "Not Submitted"
                ));
            }
        }
        return list;
    }

    // Students view all assignments (no enrollment check)
    public List<Assignment> getAllAssignments() throws SQLException {
        String sql = """
            SELECT a.assignment_id, a.title, a.due_date, c.description, c.course_name
            FROM assignments a
            JOIN courses c ON a.course_id = c.course_id
            ORDER BY a.due_date
        """;

        List<Assignment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(new Assignment(
                        rs.getInt("assignment_id"),
                        rs.getString("course_name"),
                        rs.getString("title"),
                        rs.getDate("due_date").toString(),
                        rs.getString("description"),
                        "Not Submitted"
                ));
            }
        }
        return list;
    }
}