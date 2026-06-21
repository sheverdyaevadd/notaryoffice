package com.notary.controller;

import com.notary.dao.UserDAO;
import com.notary.model.Role;
import com.notary.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class UserController {

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colId;
    @FXML private TableColumn<User, String> colLogin;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colPhone;
    @FXML private TableColumn<User, String> colRole;
    @FXML private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();
    private final ObservableList<User> userList = FXCollections.observableArrayList();
    private List<Role> roles;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colLogin.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getLogin()));
        colEmail.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));
        colPhone.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));
        colRole.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        getRoleName(data.getValue().getIdRole())));

        try {
            roles = userDAO.findAllRoles();
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки ролей");
        }

        loadUsers();
    }

    private String getRoleName(int roleId) {
        if (roles == null) return String.valueOf(roleId);
        return roles.stream()
                .filter(r -> r.getId() == roleId)
                .map(Role::getRoleType)
                .findFirst()
                .orElse(String.valueOf(roleId));
    }

    private void loadUsers() {
        try {
            userList.setAll(userDAO.findAll());
            userTable.setItems(userList);
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки данных");
        }
    }

    @FXML
    private void handleAdd() {
        Dialog<User> dialog = buildDialog(null);
        dialog.showAndWait().ifPresent(user -> {
            try {
                userDAO.add(user);
                loadUsers();
            } catch (Exception e) {
                statusLabel.setText("Ошибка добавления");
            }
        });
    }

    @FXML
    private void handleEdit() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите пользователя");
            return;
        }
        Dialog<User> dialog = buildDialog(selected);
        dialog.showAndWait().ifPresent(user -> {
            try {
                userDAO.update(user);
                loadUsers();
            } catch (Exception e) {
                statusLabel.setText("Ошибка редактирования");
            }
        });
    }

    @FXML
    private void handleDelete() {
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите пользователя");
            return;
        }
        try {
            userDAO.delete(selected.getId());
            loadUsers();
        } catch (Exception e) {
            statusLabel.setText("Ошибка удаления");
        }
    }

    @FXML
    private void handleGoToClients() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/ClientView.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) userTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            statusLabel.setText("Ошибка перехода");
            e.printStackTrace();
        }
    }

    private Dialog<User> buildDialog(User existing) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Добавить пользователя" : "Редактировать пользователя");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField loginField = new TextField(existing != null ? existing.getLogin() : "");
        PasswordField passwordField = new PasswordField();
        TextField emailField = new TextField(existing != null ? existing.getEmail() : "");
        TextField phoneField = new TextField(existing != null ? existing.getPhone() : "");

        ComboBox<Role> roleCombo = new ComboBox<>();
        if (roles != null) roleCombo.getItems().addAll(roles);
        roleCombo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Role r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? "" : r.getRoleType());
            }
        });
        roleCombo.setButtonCell(new ListCell<>() {
            protected void updateItem(Role r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? "" : r.getRoleType());
            }
        });
        if (existing != null && roles != null) {
            roles.stream().filter(r -> r.getId() == existing.getIdRole())
                    .findFirst().ifPresent(roleCombo::setValue);
        }

        loginField.setPromptText("Логин");
        passwordField.setPromptText("Пароль (мин. 8 симв., цифра, заглавная, спецсимвол)");
        emailField.setPromptText("Email");
        phoneField.setPromptText("Телефон");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 11;");

        VBox box = new VBox(8, loginField, passwordField, emailField, phoneField, roleCombo, errorLabel);
        box.setStyle("-fx-padding: 16;");
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                String password = passwordField.getText();

                if (existing == null && !PASSWORD_PATTERN.matcher(password).matches()) {
                    errorLabel.setText("Пароль должен содержать 8+ символов, цифру, заглавную букву и спецсимвол");
                    return null;
                }

                Role selectedRole = roleCombo.getValue();
                if (selectedRole == null) {
                    errorLabel.setText("Выберите роль");
                    return null;
                }

                String passwordHash = password.isEmpty() && existing != null
                        ? existing.getPasswordHash()
                        : password;

                return new User(
                        existing != null ? existing.getId() : 0,
                        loginField.getText(),
                        passwordHash,
                        emailField.getText(),
                        phoneField.getText(),
                        LocalDateTime.now(),
                        selectedRole.getId()
                );
            }
            return null;
        });

        return dialog;
    }
}