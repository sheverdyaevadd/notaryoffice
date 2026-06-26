package com.notary.dao;

import com.notary.database.DatabaseConnection;
import com.notary.model.ProvidedService;

import java.sql.*;
import java.util.*;

public class ProvidedServiceDAO {

    public List<ProvidedService> findAll() throws SQLException {
        String query = "SELECT * FROM provided_services ORDER BY id";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        LinkedHashMap<Integer, ProvidedService> map = new LinkedHashMap<>();
        while (rs.next()) {
            int id = rs.getInt("id");
            map.put(id, new ProvidedService(
                    id,
                    rs.getInt("id_deal"),
                    rs.getInt("id_service"),
                    new ArrayList<>(),
                    rs.getTimestamp("service_date").toLocalDateTime(),
                    rs.getDouble("final_price")
            ));
        }

        String discQuery = "SELECT id_provided_service, id_discount FROM discount_provided_service";
        PreparedStatement discStatement = connection.prepareStatement(discQuery);
        ResultSet discRs = discStatement.executeQuery();
        while (discRs.next()) {
            int psId = discRs.getInt("id_provided_service");
            int discId = discRs.getInt("id_discount");
            if (map.containsKey(psId)) {
                map.get(psId).getDiscountIds().add(discId);
            }
        }

        return new ArrayList<>(map.values());
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

        addDiscounts(generatedId, ps.getDiscountIds());
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

        deleteDiscounts(ps.getId());
        addDiscounts(ps.getId(), ps.getDiscountIds());
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM provided_services WHERE id=?";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.executeUpdate();
    }

    private void addDiscounts(int providedServiceId, List<Integer> discountIds) throws SQLException {
        if (discountIds == null || discountIds.isEmpty()) return;
        String query = "INSERT INTO discount_provided_service (id_discount, id_provided_service) VALUES (?, ?)";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        for (int discountId : discountIds) {
            statement.setInt(1, discountId);
            statement.setInt(2, providedServiceId);
            statement.executeUpdate();
        }
    }

    private void deleteDiscounts(int providedServiceId) throws SQLException {
        String query = "DELETE FROM discount_provided_service WHERE id_provided_service=?";
        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, providedServiceId);
        statement.executeUpdate();
    }
}
