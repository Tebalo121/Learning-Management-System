package com.example.learningmanagementsystem.dao;

import com.example.learningmanagementsystem.DatabaseConnector;
import com.example.learningmanagementsystem.Grade;
import com.example.learningmanagementsystem.Submission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubmissionDAO {

    // Submit a new assignment (insert or update)
    public void submitAssignment(int studentId, int assignmentId, String answerText, String filePath) throws SQLException {
        String sql = """
            INSERT INTO submissions (student_id, assignment_id, answer_text, file_path)
            VALUES (?, ?, ?, ?)
            ON CONFLICT (assignment_id, student_id)
            DO UPDATE SET answer_text = EXCLUDED.answer_text,
                          file_path = EXCLUDED.file_path,
                          submitted_on = CURRENT_TIMESTAMP;
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, assignmentId);
            stmt.setString(3, answerText);
            stmt.setString(4, filePath);
            stmt.executeUpdate();
        }
    }

    // Check if student already submitted
    public boolean hasSubmitted(int studentId, int assignmentId) throws SQLException {
        String sql = "SELECT 1 FROM submissions WHERE student_id = ? AND assignment_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, assignmentId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    // Get grade for a student's submission
    public String getGrade(int studentId, int assignmentId) throws SQLException {
        String sql = "SELECT grade FROM submissions WHERE student_id = ? AND assignment_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, assignmentId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("grade") : "Not Graded";
        }
    }

    // Get all submissions by a student
    public List<Submission> getSubmissionsByStudent(int studentId) throws SQLException {
        String sql = """
            SELECT s.submission_id, s.submitted_on, s.grade,
                   a.title AS assignment_title, c.course_name
            FROM submissions s
            JOIN assignments a ON s.assignment_id = a.assignment_id
            JOIN courses c ON a.course_id = c.course_id
            WHERE s.student_id = ?
        """;

        List<Submission> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Submission(
                        rs.getInt("submission_id"),
                        rs.getString("course_name"),
                        rs.getString("assignment_title"),
                        rs.getString("submitted_on"),
                        rs.getString("grade")
                ));
            }
        }
        return list;
    }

    // Get all submissions for assignments created by instructor
    public List<Submission> getSubmissionsByInstructor(int instructorId) throws SQLException {
        String sql = """
            SELECT s.submission_id, s.submitted_on, s.grade,
                   a.title AS assignment_title, u.full_name AS student_name
            FROM submissions s
            JOIN assignments a ON s.assignment_id = a.assignment_id
            JOIN courses c ON a.course_id = c.course_id
            JOIN users u ON s.student_id = u.user_id
            WHERE c.instructor_id = ?
        """;

        List<Submission> list = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Submission(
                        rs.getInt("submission_id"),
                        rs.getString("student_name"),
                        rs.getString("assignment_title"),
                        rs.getString("submitted_on"),
                        rs.getString("grade")
                ));
            }
        }
        return list;
    }

    // Update grade
    public void updateGrade(int submissionId, String grade) throws SQLException {
        String sql = "UPDATE submissions SET grade = ? WHERE submission_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, grade);
            stmt.setInt(2, submissionId);
            stmt.executeUpdate();
        }
    }

    public List<Grade> getGradesForStudent(int userId) {
        return List.of();
    }
}