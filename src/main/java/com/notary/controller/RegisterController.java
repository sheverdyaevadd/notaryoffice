package com.notary.controller;

import com.notary.dao.UserDAO;
import com.notary.model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class RegisterController {

    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Label errorLabel;

    private final UserDAO userDAO = new UserDAO();

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");

    @FXML
    private void handleRegister() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (login.isEmpty() || password.isEmpty() || email.isEmpty()) {
            errorLabel.setText("Заполните все обязательные поля");
            return;
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            errorLabel.setText("Пароль: 8+ символов, цифра, заглавная, спецсимвол");
            return;
        }

        try {
            User existing = userDAO.findByLogin(login);
            if (existing != null) {
                errorLabel.setText("Логин уже занят");
                return;
            }

            User newUser = new User(
                    0, login, password, email, phone,
                    LocalDateTime.now(), 2
            );

            userDAO.add(newUser);

            errorLabel.setStyle("-fx-text-fill: green;");
            errorLabel.setText("Регистрация успешна! Войдите в систему.");

        } catch (Exception e) {
            errorLabel.setText("Ошибка регистрации");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/LoginView.fxml")
            );
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
        } catch (Exception e) {
            errorLabel.setText("Ошибка перехода");
            e.printStackTrace();
        }
    }
}