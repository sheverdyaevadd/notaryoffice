package com.notary.controller;

import com.notary.dao.ClientDAO;
import com.notary.dao.DealDAO;
import com.notary.model.Client;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DealController {

    @FXML private TableView<Deal> dealTable;
    @FXML private TableColumn<Deal, Integer> colId;
    @FXML private TableColumn<Deal, Integer> colClient;
    @FXML private TableColumn<Deal, String> colDate;
    @FXML private TableColumn<Deal, BigDecimal> colAmount;
    @FXML private TableColumn<Deal, BigDecimal> colCommission;
    @FXML private Label statusLabel;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final DealDAO dealDAO = new DealDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final ObservableList<Deal> dealList = FXCollections.observableArrayList();
    private List<Client> clients;

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

        boolean canEdit = SessionManager.canEdit();
        btnAdd.setVisible(canEdit);
        btnEdit.setVisible(canEdit);
        btnDelete.setVisible(canEdit);

        try {
            clients = clientDAO.findAll();
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки списка клиентов");
        }

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
        try {
            clients = clientDAO.findAll();
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки списка клиентов");
            return;
        }
        if (clients.isEmpty()) {
            statusLabel.setText("Сначала добавьте хотя бы одного клиента");
            return;
        }
        Dialog<Deal> dialog = buildDialog(null);
        dialog.showAndWait().ifPresent(deal -> {
            try {
                dealDAO.add(deal);
                loadDeals();
            } catch (Exception e) {
                statusLabel.setText("Ошибка добавления: " + e.getMessage());
                e.printStackTrace();
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
        try {
            clients = clientDAO.findAll();
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки списка клиентов");
            return;
        }
        Dialog<Deal> dialog = buildDialog(selected);
        dialog.showAndWait().ifPresent(deal -> {
            try {
                dealDAO.update(deal);
                loadDeals();
            } catch (Exception e) {
                statusLabel.setText("Ошибка редактирования: " + e.getMessage());
                e.printStackTrace();
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
    private void handleGoToProvidedServices() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/ProvidedServiceView.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) dealTable.getScene().getWindow();
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
            Stage stage = (Stage) dealTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
        } catch (Exception e) {
            statusLabel.setText("Ошибка выхода");
            e.printStackTrace();
        }
    }

    private Dialog<Deal> buildDialog(Deal existing) {
        Dialog<Deal> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Добавить сделку" : "Редактировать сделку");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        ComboBox<Client> clientCombo = new ComboBox<>();
        clientCombo.getItems().addAll(clients);
        clientCombo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Client c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getId() + " — " + c.getName());
            }
        });
        clientCombo.setButtonCell(new ListCell<>() {
            protected void updateItem(Client c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? "" : c.getId() + " — " + c.getName());
            }
        });
        if (existing != null) {
            clients.stream().filter(c -> c.getId() == existing.getIdClient())
                    .findFirst().ifPresent(clientCombo::setValue);
        }

        Label amountInfoLabel = new Label();
        amountInfoLabel.setStyle("-fx-font-size: 12; -fx-text-fill: #555;");
        if (existing != null) {
            amountInfoLabel.setText("Текущая сумма сделки: " + existing.getTotalAmount()
                    + " (формируется автоматически из оказанных услуг)");
        } else {
            amountInfoLabel.setText("Сделка будет создана с нулевой суммой. " +
                    "Сумма сложится автоматически, когда вы добавите оказанные услуги по этой сделке.");
        }
        amountInfoLabel.setWrapText(true);
        amountInfoLabel.setMaxWidth(320);

        Label errorLabel = new Label();
        errorLabel.setStyle(
                "-fx-text-fill: #e74c3c; -fx-font-size: 13; -fx-font-weight: bold;" +
                        "-fx-background-color: #fdecea; -fx-padding: 6 10; -fx-background-radius: 4;"
        );
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        VBox box = new VBox(8,
                new Label("Клиент:"), clientCombo,
                amountInfoLabel,
                errorLabel
        );
        box.setStyle("-fx-padding: 16;");
        dialog.getDialogPane().setContent(box);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (clientCombo.getValue() == null) {
                errorLabel.setText("Выберите клиента");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
            }
        });

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Client selectedClient = clientCombo.getValue();
                if (selectedClient == null) return null;

                if (existing == null) {
                    return new Deal(
                            0,
                            selectedClient.getId(),
                            LocalDateTime.now(),
                            BigDecimal.ZERO,
                            BigDecimal.ZERO
                    );
                } else {
                    return new Deal(
                            existing.getId(),
                            selectedClient.getId(),
                            existing.getDealDate(),
                            existing.getTotalAmount(),
                            existing.getCommission()
                    );
                }
            }
            return null;
        });

        return dialog;
    }
}