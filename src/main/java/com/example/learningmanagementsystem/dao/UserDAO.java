package com.example.learningmanagementsystem.dao;

import com.example.learningmanagementsystem.DatabaseConnector;
import com.example.learningmanagementsystem.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private static final String AUTHENTICATE_SQL =
            "SELECT user_id, username, password, full_name, email, role FROM users WHERE username = ? AND password = ?";

    private static final String AUTHENTICATE_WITH_ROLE_SQL =
            "SELECT user_id, username, password, full_name, email, role FROM users WHERE username = ? AND password = ? AND role = ?";

    private static final String INSERT_USER_SQL =
            "INSERT INTO users (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";

    private static final String CHECK_USERNAME_SQL =
            "SELECT 1 FROM users WHERE username = ?";

    private static final String SELECT_ALL_USERS_SQL =
            "SELECT * FROM users ORDER BY user_id";

    private static final String UPDATE_USER_SQL =
            "UPDATE users SET full_name = ?, role = ? WHERE user_id = ?";

    private static final String DELETE_USER_SQL =
            "DELETE FROM users WHERE user_id = ?";

    // --- Authenticate by username/password
    public User authenticateUser(String username, String password) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AUTHENTICATE_SQL)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? extractUserFromResultSet(rs) : null;
            }
        }
    }

    // --- Authenticate by username/password/role
    public User authenticateUser(String username, String password, String role) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(AUTHENTICATE_WITH_ROLE_SQL)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role.toLowerCase());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? extractUserFromResultSet(rs) : null;
            }
        }
    }

    // --- Register new user
    public boolean registerUser(User newUser) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_USER_SQL)) {

            stmt.setString(1, newUser.getUsername());
            stmt.setString(2, newUser.getPassword());
            stmt.setString(3, newUser.getFullName());
            stmt.setString(4, newUser.getEmail());
            stmt.setString(5, newUser.getRole());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Check if username exists
    public boolean usernameExists(String username) {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(CHECK_USERNAME_SQL)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Get all users
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_ALL_USERS_SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        }
        return users;
    }

    // --- Update user full name and role
    public void updateUser(User user) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(UPDATE_USER_SQL)) {

            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getRole());
            stmt.setInt(3, user.getUserId());
            stmt.executeUpdate();
        }
    }

    // --- Delete user by ID
    public void deleteUser(int userId) throws SQLException {
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_USER_SQL)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    // --- Internal: convert ResultSet to User object
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("role")
        );
    }
}