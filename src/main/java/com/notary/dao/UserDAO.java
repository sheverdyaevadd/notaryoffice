package com.notary.dao;

import com.notary.database.DatabaseConnection;
import com.notary.model.User;

import java.sql.*;
import java.time.LocalDateTime;

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
}