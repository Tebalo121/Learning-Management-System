package com.example.learningmanagementsystem.dao;

import com.example.learningmanagementsystem.Announcement;
import com.example.learningmanagementsystem.DatabaseConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDAO {

    public void postAnnouncement(int instructorId, String title, String message) throws SQLException {
        String sql = "INSERT INTO announcements (instructor_id, title, message) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            stmt.setString(2, title);
            stmt.setString(3, message);
            stmt.executeUpdate();
        }
    }

    public List<Announcement> getAnnouncementsByInstructor(int instructorId) throws SQLException {
        String sql = "SELECT title, message, posted_at FROM announcements WHERE instructor_id = ? ORDER BY posted_at DESC";
        List<Announcement> list = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, instructorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(new Announcement(
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getTimestamp("posted_at").toString()
                ));
            }
        }
        return list;
    }
}