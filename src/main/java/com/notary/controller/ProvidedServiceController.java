package com.notary.controller;

import com.notary.dao.DiscountDAO;
import com.notary.dao.ProvidedServiceDAO;
import com.notary.dao.ServiceDAO;
import com.notary.model.Discount;
import com.notary.model.ProvidedService;
import com.notary.model.Service;
import com.notary.util.SessionManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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

public class ProvidedServiceController {

    @FXML private TableView<ProvidedService> providedServiceTable;
    @FXML private TableColumn<ProvidedService, Integer> colId;
    @FXML private TableColumn<ProvidedService, Integer> colDeal;
    @FXML private TableColumn<ProvidedService, Integer> colService;
    @FXML private TableColumn<ProvidedService, String> colDiscount;
    @FXML private TableColumn<ProvidedService, String> colDate;
    @FXML private TableColumn<ProvidedService, Double> colPrice;
    @FXML private Label statusLabel;

    private final ProvidedServiceDAO providedServiceDAO = new ProvidedServiceDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final DiscountDAO discountDAO = new DiscountDAO();
    private final ObservableList<ProvidedService> providedServiceList = FXCollections.observableArrayList();
    private List<Service> services;
    private List<Discount> discounts;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colDeal.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getIdDeal()).asObject());
        colService.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getIdService()).asObject());
        colDiscount.setCellValueFactory(data -> {
            Integer discountId = data.getValue().getIdDiscount();
            if (discountId == null) return new SimpleStringProperty("—");
            return new SimpleStringProperty(String.valueOf(discountId));
        });
        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getServiceDate().toString()));
        colPrice.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getFinalPrice()).asObject());

        try {
            services = serviceDAO.findAll();
            discounts = discountDAO.findAll();
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки справочников");
        }

        loadProvidedServices();
    }

    private void loadProvidedServices() {
        try {
            providedServiceList.setAll(providedServiceDAO.findAll());
            providedServiceTable.setItems(providedServiceList);
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки данных");
        }
    }

    @FXML
    private void handleAdd() {
        Dialog<ProvidedService> dialog = buildDialog(null);
        dialog.showAndWait().ifPresent(ps -> {
            try {
                providedServiceDAO.add(ps);
                loadProvidedServices();
            } catch (Exception e) {
                statusLabel.setText("Ошибка добавления");
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleEdit() {
        ProvidedService selected = providedServiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите запись");
            return;
        }
        Dialog<ProvidedService> dialog = buildDialog(selected);
        dialog.showAndWait().ifPresent(ps -> {
            try {
                providedServiceDAO.update(ps);
                loadProvidedServices();
            } catch (Exception e) {
                statusLabel.setText("Ошибка редактирования");
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void handleDelete() {
        ProvidedService selected = providedServiceTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Выберите запись");
            return;
        }
        try {
            providedServiceDAO.delete(selected.getId());
            loadProvidedServices();
        } catch (Exception e) {
            statusLabel.setText("Ошибка удаления");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToClients() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/view/ClientView.fxml")
            );
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) providedServiceTable.getScene().getWindow();
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
            Stage stage = (Stage) providedServiceTable.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(false);
        } catch (Exception e) {
            statusLabel.setText("Ошибка выхода");
            e.printStackTrace();
        }
    }

    private Dialog<ProvidedService> buildDialog(ProvidedService existing) {
        Dialog<ProvidedService> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Добавить оказанную услугу" : "Редактировать");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        TextField dealIdField = new TextField(existing != null ? String.valueOf(existing.getIdDeal()) : "");
        dealIdField.setPromptText("ID сделки");

        ComboBox<Service> serviceCombo = new ComboBox<>();
        if (services != null) serviceCombo.getItems().addAll(services);
        serviceCombo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Service s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" : s.getId() + " — " + s.getServiceName());
            }
        });
        serviceCombo.setButtonCell(new ListCell<>() {
            protected void updateItem(Service s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" : s.getId() + " — " + s.getServiceName());
            }
        });
        if (existing != null && services != null) {
            services.stream().filter(s -> s.getId() == existing.getIdService())
                    .findFirst().ifPresent(serviceCombo::setValue);
        }

        ComboBox<Discount> discountCombo = new ComboBox<>();
        discountCombo.getItems().add(null);
        if (discounts != null) discountCombo.getItems().addAll(discounts);
        discountCombo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Discount d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty ? "" : d == null ? "— без скидки —" : d.getId() + " — " + d.getDiscountType());
            }
        });
        discountCombo.setButtonCell(new ListCell<>() {
            protected void updateItem(Discount d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty ? "" : d == null ? "— без скидки —" : d.getId() + " — " + d.getDiscountType());
            }
        });
        if (existing != null && existing.getIdDiscount() != null && discounts != null) {
            discounts.stream().filter(d -> d.getId() == existing.getIdDiscount())
                    .findFirst().ifPresent(discountCombo::setValue);
        }

        TextField priceField = new TextField(existing != null ? String.valueOf(existing.getFinalPrice()) : "");
        priceField.setPromptText("Итоговая стоимость");

        VBox box = new VBox(8,
                new Label("ID сделки:"), dealIdField,
                new Label("Услуга:"), serviceCombo,
                new Label("Скидка:"), discountCombo,
                new Label("Итоговая стоимость:"), priceField
        );
        box.setStyle("-fx-padding: 16;");
        dialog.getDialogPane().setContent(box);

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Service selectedService = serviceCombo.getValue();
                if (selectedService == null) return null;

                Discount selectedDiscount = discountCombo.getValue();
                Integer discountId = selectedDiscount != null ? selectedDiscount.getId() : null;

                return new ProvidedService(
                        existing != null ? existing.getId() : 0,
                        Integer.parseInt(dealIdField.getText()),
                        selectedService.getId(),
                        discountId,
                        LocalDateTime.now(),
                        Double.parseDouble(priceField.getText())
                );
            }
            return null;
        });

        return dialog;
    }
}