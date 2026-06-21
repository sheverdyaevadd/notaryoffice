package com.notary.controller;

import com.notary.dao.DealDAO;
import com.notary.model.Deal;
import com.notary.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class DealController {

    @FXML private TableView<Deal> dealTable;
    @FXML private TableColumn<Deal, Integer> colId;
    @FXML private TableColumn<Deal, Integer> colClient;
    @FXML private TableColumn<Deal, String> colDate;
    @FXML private TableColumn<Deal, BigDecimal> colAmount;
    @FXML private TableColumn<Deal, BigDecimal> colCommission;
    @FXML private Label statusLabel;

    private final DealDAO dealDAO = new DealDAO();
    private final ObservableList<Deal> dealList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colClient.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getIdClient()).asObject());
        colDate.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getDealDate().toString()));
        colAmount.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getTotalAmount()));
        colCommission.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getCommission()));

        loadDeals();
    }

    private void loadDeals() {
        try {
            dealList.setAll(dealDAO.findAll());
            dealTable.setItems(dealList);
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки данных");
        }
    }

    @FXML
    private void handleAdd() {
        Dialog<Deal> dialog = buildDialog(null);
        dialog.showAndWait().ifPresent(deal -> {
            try {
                dealDAO.add(deal);
                loadDeals();
            } catch (Exception e) {
                statusLabel.setText("Ошибка добавления");
            }
        });
    }

    @FXML
    private void handleEdit() {
        Deal selected = dealTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите сделку");
            return;
        }
        Dialog<Deal> dialog = buildDialog(selected);
        dialog.showAndWait().ifPresent(deal -> {
            try {
                dealDAO.update(deal);
                loadDeals();
            } catch (Exception e) {
                statusLabel.setText("Ошибка редактирования");
            }
        });
    }

    @FXML
    private void handleDelete() {
        Deal selected = dealTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите сделку");
            return;
        }
        try {
            dealDAO.delete(selected.getId());
            loadDeals();
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
            Stage stage = (Stage) dealTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
        } catch (Exception e) {
            statusLabel.setText("Ошибка открытия экрана клиентов");
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
            Stage stage = (Stage) dealTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
        } catch (Exception e) {
            statusLabel.setText("Ошибка выхода");
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

            Stage stage =
                    (Stage) dealTable.getScene().getWindow();

            stage.setScene(scene);
            stage.setMaximized(true);

        } catch (Exception e) {

            statusLabel.setText("Ошибка перехода");
            e.printStackTrace();
        }
    }


    private Dialog<Deal> buildDialog(Deal existing) {
        Dialog<Deal> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Добавить сделку" : "Редактировать сделку");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField clientIdField = new TextField(existing != null ? String.valueOf(existing.getIdClient()) : "");
        TextField amountField = new TextField(existing != null ? existing.getTotalAmount().toString() : "");

        clientIdField.setPromptText("ID клиента");
        amountField.setPromptText("Сумма");

        VBox box = new VBox(8, clientIdField, amountField);
        box.setStyle("-fx-padding: 16;");
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                BigDecimal amount = new BigDecimal(amountField.getText());
                BigDecimal commission = amount.multiply(new BigDecimal("0.20"));
                return new Deal(
                        existing != null ? existing.getId() : 0,
                        Integer.parseInt(clientIdField.getText()),
                        LocalDateTime.now(),
                        amount,
                        commission
                );
            }
            return null;
        });

        return dialog;
    }
}