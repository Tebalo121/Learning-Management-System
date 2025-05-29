package com.example.learningmanagementsystem.dao;

import com.example.learningmanagementsystem.DatabaseConnector;
import com.example.learningmanagementsystem.Grade;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    /**
     * Fetches all grades for a given student based on submissions.
     *
     * @param studentId ID of the student
     * @return List of Grade objects (course name, assignment title, grade)
     * @throws SQLException if database access fails
     */
    public List<Grade> getGradesForStudent(int studentId) throws SQLException {
        String sql = """
            SELECT c.course_name, a.title AS assignment_title, s.grade
            FROM submissions s
            JOIN assignments a ON s.assignment_id = a.assignment_id
            JOIN courses c ON a.course_id = c.course_id
            WHERE s.student_id = ?
        """;

        List<Grade> grades = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                grades.add(new Grade(
                        rs.getString("course_name"),
                        rs.getString("assignment_title"),
                        rs.getString("grade") == null ? "Ungraded" : rs.getString("grade")
                ));
            }
        }

        return grades;
    }
}