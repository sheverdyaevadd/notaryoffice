package com.notary.dao;

import com.notary.database.DatabaseConnection;
import com.notary.model.Discount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DiscountDAO {

    public List<Discount> findAll() throws SQLException {
        List<Discount> discounts = new ArrayList<>();
        String query = "SELECT * FROM discounts";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            discounts.add(new Discount(
                    rs.getInt("id"),
                    rs.getString("discount_type"),
                    rs.getBigDecimal("discount_size"),
                    rs.getString("conditions_text")
            ));
        }
        return discounts;
    }

    public void add(Discount discount) throws SQLException {
        String query = "INSERT INTO discounts (discount_type, discount_size, conditions_text) VALUES (?, ?, ?)";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, discount.getDiscountType());
        statement.setBigDecimal(2, discount.getDiscountSize());
        statement.setString(3, discount.getConditionsText());
        statement.executeUpdate();
    }

    public void update(Discount discount) throws SQLException {
        String query = "UPDATE discounts SET discount_type=?, discount_size=?, conditions_text=? WHERE id=?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, discount.getDiscountType());
        statement.setBigDecimal(2, discount.getDiscountSize());
        statement.setString(3, discount.getConditionsText());
        statement.setInt(4, discount.getId());
        statement.executeUpdate();
    }

    public void delete(int id) throws SQLException {
        String query = "DELETE FROM discounts WHERE id=?";

        Connection connection = DatabaseConnection.getInstance().getConnection();
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setInt(1, id);
        statement.executeUpdate();
    }
}