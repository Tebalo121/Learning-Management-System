package com.example.learningmanagementsystem.dao;

import com.example.learningmanagementsystem.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdminReportDAO {

    // For PieChart: Count of each user role
    public Map<String, Number> getUserRoleDistribution() throws SQLException {
        String sql = "SELECT role, COUNT(*) AS count FROM users GROUP BY role";
        Map<String, Number> data = new LinkedHashMap<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.put(capitalize(rs.getString("role")), rs.getInt("count"));
            }
        }

        return data;
    }

    // For BarChart: Use course description length as a stand-in for stats
    public Map<String, Number> getCourseDescriptionLengthStats() throws SQLException {
        String sql = "SELECT course_name, LENGTH(description) AS description_length FROM courses";
        Map<String, Number> data = new LinkedHashMap<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.put(rs.getString("course_name"), rs.getInt("description_length"));
            }
        }

        return data;
    }

    // For LineChart: Count of assignments per course
    public Map<String, Number> getAssignmentCountPerCourse() throws SQLException {
        String sql = """
            SELECT c.course_name, COUNT(a.assignment_id) AS assignment_count
            FROM assignments a
            JOIN courses c ON a.course_id = c.course_id
            GROUP BY c.course_name
            """;
        Map<String, Number> data = new LinkedHashMap<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                data.put(rs.getString("course_name"), rs.getInt("assignment_count"));
            }
        }

        return data;
    }

    // Utility to capitalize role names
    private String capitalize(String input) {
        if (input == null || input.isEmpty()) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}