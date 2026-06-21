package com.notary.controller;

import com.notary.dao.DiscountDAO;
import com.notary.model.Discount;
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

public class DiscountController {

    @FXML private TableView<Discount> discountTable;
    @FXML private TableColumn<Discount, Integer> colId;
    @FXML private TableColumn<Discount, String> colType;
    @FXML private TableColumn<Discount, BigDecimal> colSize;
    @FXML private TableColumn<Discount, String> colConditions;
    @FXML private Label statusLabel;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final DiscountDAO discountDAO = new DiscountDAO();
    private final ObservableList<Discount> discountList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(data.getValue().getId()).asObject());
        colType.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getDiscountType()));
        colSize.setCellValueFactory(data ->
                new javafx.beans.property.SimpleObjectProperty<>(data.getValue().getDiscountSize()));
        colConditions.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getConditionsText()));

        boolean canManage = SessionManager.canManageDiscountTypes();
        btnAdd.setVisible(canManage);
        btnEdit.setVisible(canManage);
        btnDelete.setVisible(canManage);

        loadDiscounts();
    }

    private void loadDiscounts() {
        try {
            discountList.setAll(discountDAO.findAll());
            discountTable.setItems(discountList);
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки данных");
        }
    }

    @FXML
    private void handleAdd() {
        Dialog<Discount> dialog = buildDialog(null);
        dialog.showAndWait().ifPresent(discount -> {
            try {
                discountDAO.add(discount);
                loadDiscounts();
            } catch (Exception e) {
                statusLabel.setText("Ошибка добавления");
            }
        });
    }

    @FXML
    private void handleEdit() {
        Discount selected = discountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите скидку");
            return;
        }
        Dialog<Discount> dialog = buildDialog(selected);
        dialog.showAndWait().ifPresent(discount -> {
            try {
                discountDAO.update(discount);
                loadDiscounts();
            } catch (Exception e) {
                statusLabel.setText("Ошибка редактирования");
            }
        });
    }

    @FXML
    private void handleDelete() {
        Discount selected = discountTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите скидку");
            return;
        }
        try {
            discountDAO.delete(selected.getId());
            loadDiscounts();
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
            Stage stage = (Stage) discountTable.getScene().getWindow();
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
            Stage stage = (Stage) discountTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
        } catch (Exception e) {
            statusLabel.setText("Ошибка выхода");
            e.printStackTrace();
        }
    }

    private Dialog<Discount> buildDialog(Discount existing) {
        Dialog<Discount> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Добавить скидку" : "Редактировать скидку");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField typeField = new TextField(existing != null ? existing.getDiscountType() : "");
        TextField sizeField = new TextField(existing != null ? existing.getDiscountSize().toString() : "");
        TextField conditionsField = new TextField(existing != null ? existing.getConditionsText() : "");

        typeField.setPromptText("Тип скидки");
        sizeField.setPromptText("Размер (%)");
        conditionsField.setPromptText("Условия");

        VBox box = new VBox(8, typeField, sizeField, conditionsField);
        box.setStyle("-fx-padding: 16;");
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                return new Discount(
                        existing != null ? existing.getId() : 0,
                        typeField.getText(),
                        new BigDecimal(sizeField.getText()),
                        conditionsField.getText()
                );
            }
            return null;
        });

        return dialog;
    }
}