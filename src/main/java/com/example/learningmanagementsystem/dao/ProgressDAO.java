package com.example.learningmanagementsystem.dao;

import com.example.learningmanagementsystem.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ProgressDAO {

    /**
     * Calculates average grade (as progress) per course for a student.
     * @param studentId the logged-in student's ID
     * @return a map of course name to average progress percentage (0–100)
     * @throws SQLException if DB access fails
     */
    public Map<String, Double> getCourseProgressForStudent(int studentId) throws SQLException {
        String sql = """
            SELECT c.course_name,
                   AVG(CASE\s
                       WHEN s.grade IS NOT NULL AND s.grade ~ '^[0-9]+$'\s
                       THEN CAST(s.grade AS DOUBLE PRECISION)
                       ELSE NULL\s
                   END) AS average_grade
            FROM submissions s
            JOIN assignments a ON s.assignment_id = a.assignment_id
            JOIN courses c ON a.course_id = c.course_id
            WHERE s.student_id = ?
            GROUP BY c.course_name
           \s""";

        return getStringDoubleMap(studentId, sql);
    }

    private static Map<String, Double> getStringDoubleMap(int studentId, String sql) throws SQLException {
        Map<String, Double> progressMap = new HashMap<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String courseName = rs.getString("course_name");
                    double avg = rs.getDouble("average_grade");
                    progressMap.put(courseName, avg);
                }
            }
        }
        return progressMap;
    }
}