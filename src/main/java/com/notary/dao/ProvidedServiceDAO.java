package com.notary.dao;

import com.notary.database.DatabaseConnection;
import com.notary.model.ProvidedService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProvidedServiceDAO {

    public List<ProvidedService> findAll() throws SQLException {
        List<ProvidedService> list = new ArrayList<>();
        String query = "SELECT ps.*, dps.id_discount " +
                "FROM provided_services ps " +
                "LEFT JOIN discount_provided_service dps ON ps.id = dps.id_provided_service";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            int discountId = rs.getInt("id_discount");
            Integer idDiscount = rs.wasNull() ? null : discountId;

            list.add(new ProvidedService(
                    rs.getInt("id"),
                    rs.getInt("id_deal"),
                    rs.getInt("id_service"),
                    idDiscount,
                    rs.getTimestamp("service_date").toLocalDateTime(),
                    rs.getDouble("final_price")
            ));
        }
        return list;
    }

    public int add(ProvidedService ps) throws SQLException {
        String query = "INSERT INTO provided_services (id_deal, id_service, service_date, final_price) VALUES (?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, ps.getIdDeal());
        statement.setInt(2, ps.getIdService());
        statement.setTimestamp(3, Timestamp.valueOf(ps.getServiceDate()));
        statement.setDouble(4, ps.getFinalPrice());
        statement.executeUpdate();

        ResultSet keys = statement.getGeneratedKeys();
        int generatedId = 0;
        if (keys.next()) {
            generatedId = keys.getInt(1);
        }

        if (ps.getIdDiscount() != null) {
            addDiscount(generatedId, ps.getIdDiscount());
        }

        return generatedId;
    }

    public void update(ProvidedService ps) throws SQLException {
        String query = "UPDATE provided_services SET id_deal=?, id_service=?, service_date=?, final_price=? WHERE id=?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, ps.getIdDeal());
        statement.setInt(2, ps.getIdService());
        statement.setTimestamp(3, Timestamp.valueOf(ps.getServiceDate()));
        statement.setDouble(4, ps.getFinalPrice());
        statement.setInt(5, ps.getId());
        statement.executeUpdate();

        deleteDiscount(ps.getId());
        if (ps.getIdDiscount() != null) {
            addDiscount(ps.getId(), ps.getIdDiscount());
        }
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM provided_services WHERE id=?";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    private void addDiscount(int providedServiceId, int discountId) throws SQLException {
        String query = "INSERT INTO discount_provided_service (id_discount, id_provided_service) VALUES (?, ?)";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, discountId);
        statement.setInt(2, providedServiceId);
        statement.executeUpdate();
    }

    private void deleteDiscount(int providedServiceId) throws SQLException {
        String query = "DELETE FROM discount_provided_service WHERE id_provided_service=?";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, providedServiceId);
        statement.executeUpdate();
    }
}