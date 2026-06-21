package com.notary.controller;

import com.notary.dao.ServiceDAO;
import com.notary.model.Service;
import com.notary.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;

public class ServiceController {

    @FXML private TableView<Service> serviceTable;
    @FXML private TableColumn<Service, Integer> colId;
    @FXML private TableColumn<Service, String> colName;
    @FXML private TableColumn<Service, String> colDescription;
    @FXML private TableColumn<Service, BigDecimal> colPrice;
    @FXML private Label statusLabel;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final ObservableList<Service> serviceList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colName.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getServiceName()));
        colDescription.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        colPrice.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getBasePrice()));

        // оператор не может добавлять/редактировать/удалять услуги
        boolean canManage = SessionManager.canManageServiceTypes();
        btnAdd.setVisible(canManage);
        btnEdit.setVisible(canManage);
        btnDelete.setVisible(canManage);

        loadServices();
    }

    private void loadServices() {
        try {
            serviceList.setAll(serviceDAO.findAll());
            serviceTable.setItems(serviceList);
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки данных");
        }
    }

    @FXML
    private void handleAdd() {
        Dialog<Service> dialog = buildDialog(null);
        dialog.showAndWait().ifPresent(service -> {
            try {
                serviceDAO.add(service);
                loadServices();
            } catch (Exception e) {
                statusLabel.setText("Ошибка добавления");
            }
        });
    }

    @FXML
    private void handleEdit() {
        Service selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите услугу");
            return;
        }
        Dialog<Service> dialog = buildDialog(selected);
        dialog.showAndWait().ifPresent(service -> {
            try {
                serviceDAO.update(service);
                loadServices();
            } catch (Exception e) {
                statusLabel.setText("Ошибка редактирования");
            }
        });
    }

    @FXML
    private void handleDelete() {
        Service selected = serviceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите услугу");
            return;
        }
        try {
            serviceDAO.delete(selected.getId());
            loadServices();
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
            Stage stage = (Stage) serviceTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            statusLabel.setText("Ошибка перехода");
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
            Stage stage = (Stage) serviceTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
        } catch (Exception e) {
            statusLabel.setText("Ошибка выхода");
            e.printStackTrace();
        }
    }

    private Dialog<Service> buildDialog(Service existing) {
        Dialog<Service> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Добавить услугу" : "Редактировать услугу");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField nameField = new TextField(existing != null ? existing.getServiceName() : "");
        TextField descField = new TextField(existing != null ? existing.getDescription() : "");
        TextField priceField = new TextField(existing != null ? existing.getBasePrice().toString() : "");

        nameField.setPromptText("Название");
        descField.setPromptText("Описание");
        priceField.setPromptText("Базовая цена");

        VBox box = new VBox(8, nameField, descField, priceField);
        box.setStyle("-fx-padding: 16;");
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                return new Service(
                        existing != null ? existing.getId() : 0,
                        nameField.getText(),
                        descField.getText(),
                        new BigDecimal(priceField.getText())
                );
            }
            return null;
        });

        return dialog;
    }
}