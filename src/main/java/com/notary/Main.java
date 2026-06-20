package com.notary;

import com.notary.model.User;
import com.notary.service.AuthService;

public class Main {
    public static void main(String[] args) {
        try {
            AuthService authService = new AuthService();
            User user = authService.login("admin", "test_hash_admin");

            if (user != null) {
                System.out.println("Авторизация успешна: " + user.getLogin());
            } else {
                System.out.println("Неверный логин или пароль");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}