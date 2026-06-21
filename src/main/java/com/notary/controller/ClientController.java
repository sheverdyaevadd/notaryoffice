package com.notary.controller;

import com.notary.dao.ClientDAO;
import com.notary.model.Client;
import com.notary.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ClientController {

    @FXML private TableView<Client> clientTable;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colActivity;
    @FXML private TableColumn<Client, String> colAddress;
    @FXML private TableColumn<Client, String> colPhone;
    @FXML private Label statusLabel;
    @FXML private Button btnUsers;

    private final ClientDAO clientDAO = new ClientDAO();
    private ObservableList<Client> clientList = FXCollections.observableArrayList();

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
            stage.setMaximized(true);
        } catch (Exception e) {
            statusLabel.setText("Ошибка открытия экрана сделок");
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
            stage.setMaximized(true);
        } catch (Exception e) {
            statusLabel.setText("Ошибка открытия экрана пользователей");
            e.printStackTrace();
        }
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