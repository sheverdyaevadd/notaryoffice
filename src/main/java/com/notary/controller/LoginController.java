package com.notary.controller;

import com.notary.model.User;
import com.notary.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LoginController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Заполните все поля");
            return;
        }

        try {
            User user = authService.login(login, password);
            if (user != null) {
                errorLabel.setStyle("-fx-text-fill: green;");
                errorLabel.setText("Добро пожаловать, " + user.getLogin() + "!");
            } else {
                errorLabel.setText("Неверный логин или пароль");
            }
        } catch (Exception e) {
            errorLabel.setText("Ошибка подключения к БД");
            e.printStackTrace();
        }
    }
}