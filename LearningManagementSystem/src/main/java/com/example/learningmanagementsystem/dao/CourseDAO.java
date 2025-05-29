package com.example.learningmanagementsystem.dao;

import com.example.learningmanagementsystem.Course;
import com.example.learningmanagementsystem.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    public List<Course> getCoursesByInstructor(int instructorId) throws SQLException {
        String sql = """
            SELECT c.course_id, c.course_name, u.full_name AS instructor_name, c.description
            FROM courses c
            JOIN users u ON c.instructor_id = u.user_id
            WHERE c.instructor_id = ?
        """;

        List<Course> courses = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("instructor_name"),
                        rs.getString("description")
                ));
            }
        }
        return courses;
    }

    public List<Course> getAllCourses() throws SQLException {
        String sql = """
            SELECT c.course_id, c.course_name, u.full_name AS instructor_name, c.description
            FROM courses c
            JOIN users u ON c.instructor_id = u.user_id
        """;

        List<Course> courses = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                courses.add(new Course(
                        rs.getInt("course_id"),
                        rs.getString("course_name"),
                        rs.getString("instructor_name"),
                        rs.getString("description")
                ));
            }
        }
        return courses;
    }

    public void addCourse(String name, String description, int instructorId) throws SQLException {
        String sql = "INSERT INTO courses (course_name, description, instructor_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setInt(3, instructorId);
            stmt.executeUpdate();
        }
    }

    public void updateCourse(int courseId, String name, String description) throws SQLException {
        String sql = "UPDATE courses SET course_name = ?, description = ? WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setInt(3, courseId);
            stmt.executeUpdate();
        }
    }

    public void deleteCourse(int courseId) throws SQLException {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courseId);
            stmt.executeUpdate();
        }
    }
}