package com.notary.dao;

import com.notary.database.DatabaseConnection;
import com.notary.model.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String query = "SELECT * FROM clients";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            clients.add(new Client(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("activity_type"),
                    rs.getString("address"),
                    rs.getString("phone")
            ));
        }
        return clients;
    }

    public void add(Client client) throws SQLException {
        String query = "INSERT INTO clients (name, activity_type, address, phone) VALUES (?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, client.getName());
        statement.setString(2, client.getActivityType());
        statement.setString(3, client.getAddress());
        statement.setString(4, client.getPhone());
        statement.executeUpdate();
    }

    public void update(Client client) throws SQLException {
        String query = "UPDATE clients SET name=?, activity_type=?, address=?, phone=? WHERE id=?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, client.getName());
        statement.setString(2, client.getActivityType());
        statement.setString(3, client.getAddress());
        statement.setString(4, client.getPhone());
        statement.setInt(5, client.getId());
        statement.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM clients WHERE id=?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.executeUpdate();
    }
}