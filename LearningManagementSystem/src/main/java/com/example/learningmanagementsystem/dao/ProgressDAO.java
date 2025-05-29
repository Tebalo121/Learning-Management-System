package com.example.learningmanagementsystem.dao;

import com.example.learningmanagementsystem.DatabaseConnector;

import java.sql.*;
import java.util.*;

public class ProgressDAO {

    public List<String> getInstructorCourseNames(int instructorId) throws SQLException {
        List<String> courses = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.course_name " +
                             "FROM courses c " +
                             "JOIN course_instructors ci ON c.course_id = ci.course_id " +
                             "WHERE ci.instructor_id = ? ORDER BY c.course_name")) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                courses.add(rs.getString("course_name"));
            }
            System.out.println("ProgressDAO: Fetched " + courses.size() + " courses for instructor ID: " + instructorId);
            return courses;
        }
    }

    public Map<Integer, String> getStudentsForCourse(String courseName) throws SQLException {
        Map<Integer, String> studentMap = new HashMap<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.user_id, u.full_name " +
                             "FROM users u " +
                             "JOIN enrollments e ON u.user_id = e.student_id " +
                             "JOIN courses c ON e.course_id = c.course_id " +
                             "WHERE c.course_name = ? AND e.status = 'Active' " +
                             "ORDER BY u.full_name")) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                studentMap.put(rs.getInt("user_id"), rs.getString("full_name"));
                count++;
            }
            System.out.println("ProgressDAO: Fetched " + count + " students for course: " + courseName);
            return studentMap;
        }
    }

    public Map<String, Double> getStudentProgressForCourse(String courseName) throws SQLException {
        Map<String, Double> progressMap = new HashMap<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT u.full_name, p.test1, p.midterm, p.test2, p.final_exam " +
                             "FROM progress p " +
                             "JOIN users u ON p.student_id = u.user_id " +
                             "JOIN courses c ON p.course_id = c.course_id " +
                             "WHERE c.course_name = ?")) {
            stmt.setString(1, courseName);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                String studentName = rs.getString("full_name");
                double test1 = rs.getDouble("test1");
                double midterm = rs.getDouble("midterm");
                double test2 = rs.getDouble("test2");
                double finalExam = rs.getDouble("final_exam");
                double average = (test1 + midterm + test2 + finalExam) / 4.0;
                progressMap.put(studentName, average);
                count++;
            }
            System.out.println("ProgressDAO: Fetched " + count + " progress records for course: " + courseName);
            return progressMap;
        }
    }

    public Map<String, Double> getCourseProgressForStudent(int studentId) throws SQLException {
        Map<String, Double> progressMap = new HashMap<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT c.course_name, p.test1, p.midterm, p.test2, p.final_exam " +
                             "FROM progress p " +
                             "JOIN courses c ON p.course_id = c.course_id " +
                             "JOIN enrollments e ON p.student_id = e.student_id AND p.course_id = e.course_id " +
                             "WHERE p.student_id = ? AND e.status = 'Active'")) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            int count = 0;
            while (rs.next()) {
                String courseName = rs.getString("course_name");
                double test1 = rs.getDouble("test1");
                double midterm = rs.getDouble("midterm");
                double test2 = rs.getDouble("test2");
                double finalExam = rs.getDouble("final_exam");
                double average = (test1 + midterm + test2 + finalExam) / 4.0;
                progressMap.put(courseName, average);
                count++;
            }
            System.out.println("ProgressDAO: Fetched " + count + " course progress records for student ID: " + studentId);
            return progressMap;
        }
    }

    public Map<String, Double> getCourseProgressForStudent(int studentId, String courseName) throws SQLException {
        Map<String, Double> progress = new HashMap<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT p.test1, p.midterm, p.test2, p.final_exam " +
                             "FROM progress p " +
                             "JOIN courses c ON p.course_id = c.course_id " +
                             "WHERE p.student_id = ? AND c.course_name = ?")) {
            stmt.setInt(1, studentId);
            stmt.setString(2, courseName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                progress.put("test1", rs.getDouble("test1"));
                progress.put("midterm", rs.getDouble("midterm"));
                progress.put("test2", rs.getDouble("test2"));
                progress.put("final_exam", rs.getDouble("final_exam"));
                System.out.println("ProgressDAO: Fetched progress for student ID: " + studentId + ", course: " + courseName);
            } else {
                System.out.println("ProgressDAO: No progress found for student ID: " + studentId + ", course: " + courseName);
            }
            return progress;
        }
    }

    public boolean saveOrUpdateProgress(String courseName, String studentName, double test1, double midterm,
                                        double test2, double finalExam) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection()) {
            // Get student_id and course_id
            int studentId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT user_id FROM users WHERE full_name = ?")) {
                stmt.setString(1, studentName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    studentId = rs.getInt("user_id");
                }
            }

            int courseId = -1;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT course_id FROM courses WHERE course_name = ?")) {
                stmt.setString(1, courseName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    courseId = rs.getInt("course_id");
                }
            }

            if (studentId == -1 || courseId == -1) {
                System.err.println("ProgressDAO: Invalid student (" + studentName + ") or course (" + courseName + ")");
                return false;
            }

            // Check if progress exists
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT 1 FROM progress WHERE student_id = ? AND course_id = ?")) {
                stmt.setInt(1, studentId);
                stmt.setInt(2, courseId);
                ResultSet rs = stmt.executeQuery();
                boolean exists = rs.next();

                if (exists) {
                    // Update progress
                    try (PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE progress SET test1 = ?, midterm = ?, test2 = ?, final_exam = ? " +
                                    "WHERE student_id = ? AND course_id = ?")) {
                        updateStmt.setDouble(1, test1);
                        updateStmt.setDouble(2, midterm);
                        updateStmt.setDouble(3, test2);
                        updateStmt.setDouble(4, finalExam);
                        updateStmt.setInt(5, studentId);
                        updateStmt.setInt(6, courseId);
                        updateStmt.executeUpdate();
                        System.out.println("ProgressDAO: Updated progress for student ID: " + studentId + ", course ID: " + courseId);
                        return true;
                    }
                } else {
                    // Insert new progress
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO progress (student_id, course_id, test1, midterm, test2, final_exam) " +
                                    "VALUES (?, ?, ?, ?, ?, ?)")) {
                        insertStmt.setInt(1, studentId);
                        insertStmt.setInt(2, courseId);
                        insertStmt.setDouble(3, test1);
                        insertStmt.setDouble(4, midterm);
                        insertStmt.setDouble(5, test2);
                        insertStmt.setDouble(6, finalExam);
                        insertStmt.executeUpdate();
                        System.out.println("ProgressDAO: Inserted new progress for student ID: " + studentId + ", course ID: " + courseId);
                        return true;
                    }
                }
            }
        }
    }
}