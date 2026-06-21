package com.notary.dao;

import com.notary.database.DatabaseConnection;
import com.notary.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.notary.model.Role;

public class UserDAO {

    public User findByLogin(String login) throws SQLException {
        String query = "SELECT * FROM users WHERE login = ?";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, login);
        ResultSet rs = statement.executeQuery();

        if (rs.next()) {
            return new User(
                    rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("password_hash"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getTimestamp("registration_date").toLocalDateTime(),
                    rs.getInt("id_role")
            );
        }
        return null;
    }

    public List<User> findAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            users.add(new User(
                    rs.getInt("id"),
                    rs.getString("login"),
                    rs.getString("password_hash"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getTimestamp("registration_date").toLocalDateTime(),
                    rs.getInt("id_role")
            ));
        }
        return users;
    }

    public void add(User user) throws SQLException {
        String query = "INSERT INTO users (login, password_hash, email, phone, id_role) VALUES (?, ?, ?, ?, ?)";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, user.getLogin());
        statement.setString(2, user.getPasswordHash());
        statement.setString(3, user.getEmail());
        statement.setString(4, user.getPhone());
        statement.setInt(5, user.getIdRole());
        statement.executeUpdate();
    }

    public void update(User user) throws SQLException {
        String query = "UPDATE users SET login=?, password_hash=?, email=?, phone=?, id_role=? WHERE id=?";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, user.getLogin());
        statement.setString(2, user.getPasswordHash());
        statement.setString(3, user.getEmail());
        statement.setString(4, user.getPhone());
        statement.setInt(5, user.getIdRole());
        statement.setInt(6, user.getId());
        statement.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM users WHERE id=?";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    public List<Role> findAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String query = "SELECT * FROM roles";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            roles.add(new Role(
                    rs.getInt("id"),
                    rs.getString("role_type")
            ));
        }
        return roles;
    }
}