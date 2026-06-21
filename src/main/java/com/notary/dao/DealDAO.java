package com.notary.dao;

import com.notary.database.DatabaseConnection;
import com.notary.model.Deal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DealDAO {

    public List<Deal> findAll() throws SQLException {
        List<Deal> deals = new ArrayList<>();
        String query = "SELECT * FROM deals";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            deals.add(new Deal(
                    rs.getInt("id"),
                    rs.getInt("id_client"),
                    rs.getTimestamp("deal_date").toLocalDateTime(),
                    rs.getBigDecimal("total_amount"),
                    rs.getBigDecimal("commission")
            ));
        }
        return deals;
    }

    public void add(Deal deal) throws SQLException {
        String query = "INSERT INTO deals (id_client, deal_date, total_amount, commission) VALUES (?, ?, ?, ?)";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, deal.getIdClient());
        statement.setTimestamp(2, Timestamp.valueOf(deal.getDealDate()));
        statement.setBigDecimal(3, deal.getTotalAmount());
        statement.setBigDecimal(4, deal.getCommission());
        statement.executeUpdate();
    }

    public void update(Deal deal) throws SQLException {
        String query = "UPDATE deals SET id_client=?, deal_date=?, total_amount=?, commission=? WHERE id=?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, deal.getIdClient());
        statement.setTimestamp(2, Timestamp.valueOf(deal.getDealDate()));
        statement.setBigDecimal(3, deal.getTotalAmount());
        statement.setBigDecimal(4, deal.getCommission());
        statement.setInt(5, deal.getId());
        statement.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM deals WHERE id=?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.executeUpdate();
    }
}