package com.notary;

import com.notary.database.DatabaseConnection;
import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        try {
            Connection connection = DatabaseConnection.getInstance().getConnection();
            System.out.println("Singleton успех");
        } catch (Exception e) {
            System.out.println("ошибка:");
            e.printStackTrace();
        }
    }
}