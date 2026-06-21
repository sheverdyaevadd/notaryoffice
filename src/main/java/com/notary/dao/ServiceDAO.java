package com.notary.dao;

import com.notary.database.DatabaseConnection;
import com.notary.model.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDAO {

    public List<Service> findAll() throws SQLException {
        List<Service> services = new ArrayList<>();
        String query = "SELECT * FROM services";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            services.add(new Service(
                    rs.getInt("id"),
                    rs.getString("service_name"),
                    rs.getString("description"),
                    rs.getBigDecimal("base_price")
            ));
        }
        return services;
    }

    public void add(Service service) throws SQLException {
        String query = "INSERT INTO services (service_name, description, base_price) VALUES (?, ?, ?)";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, service.getServiceName());
        statement.setString(2, service.getDescription());
        statement.setBigDecimal(3, service.getBasePrice());
        statement.executeUpdate();
    }

    public void update(Service service) throws SQLException {
        String query = "UPDATE services SET service_name=?, description=?, base_price=? WHERE id=?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, service.getServiceName());
        statement.setString(2, service.getDescription());
        statement.setBigDecimal(3, service.getBasePrice());
        statement.setInt(4, service.getId());
        statement.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM services WHERE id=?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.executeUpdate();
    }
}