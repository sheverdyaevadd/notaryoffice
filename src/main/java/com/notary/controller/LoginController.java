package com.notary.controller;

import com.notary.model.User;
import com.notary.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.notary.util.SessionManager;

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
                SessionManager.setCurrentUser(user);
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/view/ClientView.fxml")
                );
                Scene scene = new Scene(loader.load(), 900, 600);
                Stage stage = (Stage) loginField.getScene().getWindow();
                stage.setScene(scene);
                stage.setMaximized(true);
            } else {
                errorLabel.setText("Неверный логин или пароль");
            }
        } catch (Exception e) {
            errorLabel.setText("Ошибка подключения к БД");
            e.printStackTrace();
        }
    }
    @FXML
    private void handleGoToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/RegisterView.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setMaximized(false);
            stage.setScene(scene);
            javafx.application.Platform.runLater(() -> stage.setMaximized(true));
        } catch (Exception e) {
            errorLabel.setText("Ошибка перехода");
            e.printStackTrace();
        }
    }
}