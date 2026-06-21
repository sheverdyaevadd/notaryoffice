package com.notary.controller;

import com.notary.dao.ClientDAO;
import com.notary.dao.UserDAO;
import com.notary.model.Client;
import com.notary.model.User;
import com.notary.util.PasswordHasher;
import com.notary.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.regex.Pattern;

public class ClientController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colActivity;
    @FXML private TableColumn<Client, String> colAddress;
    @FXML private TableColumn<Client, String> colPhone;
    @FXML private Label statusLabel;
    @FXML private Button btnUsers;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final ClientDAO clientDAO = new ClientDAO();
    private ObservableList<Client> clientList = FXCollections.observableArrayList();

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));
        colActivity.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getActivityType()));
        colAddress.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getAddress()));
        colPhone.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getPhone()));

        btnUsers.setVisible(SessionManager.isSuperAdmin());

        boolean canEdit = SessionManager.canEdit();
        btnAdd.setVisible(canEdit);
        btnEdit.setVisible(canEdit);
        btnDelete.setVisible(canEdit);

        loadClients();
    }

    private void loadClients() {
        try {
            clientList.setAll(clientDAO.findAll());
            clientTable.setItems(clientList);
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки данных");
        }
    }

    @FXML
    private void handleAdd() {
        Dialog<Client> dialog = buildDialog(null);
        dialog.showAndWait().ifPresent(client -> {
            try {
                clientDAO.add(client);
                loadClients();
            } catch (Exception e) {
                statusLabel.setText("Ошибка добавления");
            }
        });
    }

    @FXML
    private void handleEdit() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите клиента");
            return;
        }
        Dialog<Client> dialog = buildDialog(selected);
        dialog.showAndWait().ifPresent(client -> {
            try {
                clientDAO.update(client);
                loadClients();
            } catch (Exception e) {
                statusLabel.setText("Ошибка редактирования");
            }
        });
    }

    @FXML
    private void handleDelete() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите клиента");
            return;
        }
        try {
            clientDAO.delete(selected.getId());
            loadClients();
        } catch (Exception e) {
            statusLabel.setText("Ошибка удаления");
        }
    }

    @FXML
    private void handleGoToDeals() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/DealView.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) clientTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (Exception e) {
            statusLabel.setText("Ошибка открытия экрана сделок");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToProvidedServices() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/ProvidedServiceView.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) clientTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (Exception e) {
            statusLabel.setText("Ошибка открытия экрана оказанных услуг");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToServices() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/ServiceView.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) clientTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (Exception e) {
            statusLabel.setText("Ошибка открытия экрана услуг");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToDiscounts() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/DiscountView.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) clientTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (Exception e) {
            statusLabel.setText("Ошибка открытия экрана скидок");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToUsers() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/UserView.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) clientTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.setMaximized(true);
        } catch (Exception e) {
            statusLabel.setText("Ошибка открытия экрана пользователей");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleMyProfile() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;

        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle("Мой профиль");
        dialog.getDialogPane().setMinWidth(420);
        dialog.getDialogPane().setPrefWidth(420);

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField loginField = new TextField(currentUser.getLogin());
        PasswordField passwordField = new PasswordField();
        TextField emailField = new TextField(currentUser.getEmail());
        TextField phoneField = new TextField(currentUser.getPhone() != null ? currentUser.getPhone() : "");

        passwordField.setPromptText("Новый пароль (оставьте пустым чтобы не менять)");

        Label errorLabel = new Label();
        errorLabel.setStyle(
                "-fx-text-fill: #e74c3c; -fx-font-size: 13; -fx-font-weight: bold;" +
                        "-fx-background-color: #fdecea; -fx-padding: 6 10; -fx-background-radius: 4;"
        );
        errorLabel.setWrapText(true);
        errorLabel.setMaxWidth(Double.MAX_VALUE);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        VBox box = new VBox(8,
                new Label("Логин:"), loginField,
                new Label("Новый пароль:"), passwordField,
                new Label("Email:"), emailField,
                new Label("Телефон:"), phoneField,
                errorLabel
        );
        box.setStyle("-fx-padding: 16;");
        box.setPrefWidth(390);
        dialog.getDialogPane().setContent(box);

        String defaultPasswordFieldStyle = passwordField.getStyle();

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String login = loginField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String newPassword = passwordField.getText();

            if (login.isEmpty()) {
                errorLabel.setText("Логин не может быть пустым");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }
            if (email.isEmpty()) {
                errorLabel.setText("Email не может быть пустым");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                errorLabel.setText("Введите корректный email");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }
            if (phone.isEmpty()) {
                errorLabel.setText("Не может быть пустым");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }
            if (!newPassword.isEmpty() && !PASSWORD_PATTERN.matcher(newPassword).matches()) {
                errorLabel.setText("8+ символов, цифра, заглавная буква, спецсимвол");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                passwordField.setStyle(defaultPasswordFieldStyle +
                        "-fx-border-color: #e74c3c; -fx-border-width: 2; -fx-border-radius: 4;");
                event.consume();
                return;
            }
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            passwordField.setStyle(defaultPasswordFieldStyle);
        });

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                String newPassword = passwordField.getText();
                String passwordHash = newPassword.isEmpty()
                        ? currentUser.getPasswordHash()
                        : PasswordHasher.hash(newPassword);

                return new User(
                        currentUser.getId(),
                        loginField.getText().trim(),
                        passwordHash,
                        emailField.getText().trim(),
                        phoneField.getText().trim(),
                        currentUser.getRegistrationDate(),
                        currentUser.getIdRole()
                );
            }
            return null;
        });

        dialog.showAndWait().ifPresent(user -> {
            try {
                new UserDAO().update(user);
                SessionManager.setCurrentUser(user);
                statusLabel.setText("Профиль обновлён");
            } catch (Exception e) {
                statusLabel.setText("Ошибка сохранения профиля");
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleLogout() {
        try {
            SessionManager.setCurrentUser(null);
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/LoginView.fxml")
            );
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) clientTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
        } catch (Exception e) {
            statusLabel.setText("Ошибка выхода");
            e.printStackTrace();
        }
    }

    private Dialog<Client> buildDialog(Client existing) {
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Добавить клиента" : "Редактировать клиента");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField nameField = new TextField(existing != null ? existing.getName() : "");
        TextField activityField = new TextField(existing != null ? existing.getActivityType() : "");
        TextField addressField = new TextField(existing != null ? existing.getAddress() : "");
        TextField phoneField = new TextField(existing != null ? existing.getPhone() : "");

        nameField.setPromptText("Название");
        activityField.setPromptText("Вид деятельности");
        addressField.setPromptText("Адрес");
        phoneField.setPromptText("Телефон");

        VBox box = new VBox(8, nameField, activityField, addressField, phoneField);
        box.setStyle("-fx-padding: 16;");
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                return new Client(
                        existing != null ? existing.getId() : 0,
                        nameField.getText(),
                        activityField.getText(),
                        addressField.getText(),
                        phoneField.getText()
                );
            }
            return null;
        });

        return dialog;
    }
}