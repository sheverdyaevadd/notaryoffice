package com.notary.controller;

import com.notary.dao.DealDAO;
import com.notary.dao.DiscountDAO;
import com.notary.dao.ProvidedServiceDAO;
import com.notary.dao.ServiceDAO;
import com.notary.model.Deal;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProvidedServiceController {

    @FXML private TableView<ProvidedService> providedServiceTable;
    @FXML private TableColumn<ProvidedService, Integer> colId;
    @FXML private TableColumn<ProvidedService, Integer> colDeal;
    @FXML private TableColumn<ProvidedService, Integer> colService;
    @FXML private TableColumn<ProvidedService, String> colDiscount;
    @FXML private TableColumn<ProvidedService, String> colDate;
    @FXML private TableColumn<ProvidedService, Double> colPrice;
    @FXML private Label statusLabel;
    @FXML private Button btnAdd;
    @FXML private Button btnEdit;
    @FXML private Button btnDelete;

    private final ProvidedServiceDAO providedServiceDAO = new ProvidedServiceDAO();
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final DiscountDAO discountDAO = new DiscountDAO();
    private final DealDAO dealDAO = new DealDAO();
    private final ObservableList<ProvidedService> providedServiceList = FXCollections.observableArrayList();
    private List<Service> services;
    private List<Discount> discounts;
    private List<Deal> deals;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getId()).asObject());
        colDeal.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getIdDeal()).asObject());
        colService.setCellValueFactory(data ->
                new SimpleIntegerProperty(data.getValue().getIdService()).asObject());
        colDiscount.setCellValueFactory(data -> {
            List<Integer> ids = data.getValue().getDiscountIds();
            if (ids == null || ids.isEmpty()) return new SimpleStringProperty("—");
            String joined = ids.stream().map(String::valueOf).collect(Collectors.joining(", "));
            return new SimpleStringProperty(joined);
        });
        colDate.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getServiceDate().toString()));
        colPrice.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getFinalPrice()).asObject());

        boolean canEdit = SessionManager.canEdit();
        btnAdd.setVisible(canEdit);
        btnEdit.setVisible(canEdit);
        btnDelete.setVisible(canEdit);

        try {
            services = serviceDAO.findAll();
            discounts = discountDAO.findAll();
            deals = dealDAO.findAll();
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
        try {
            services = serviceDAO.findAll();
            discounts = discountDAO.findAll();
            deals = dealDAO.findAll();
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки справочников");
            return;
        }
        if (deals.isEmpty()) {
            statusLabel.setText("Сначала создайте хотя бы одну сделку");
            return;
        }
        if (services.isEmpty()) {
            statusLabel.setText("Сначала добавьте хотя бы одну услугу");
            return;
        }
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
        try {
            services = serviceDAO.findAll();
            discounts = discountDAO.findAll();
            deals = dealDAO.findAll();
        } catch (Exception e) {
            statusLabel.setText("Ошибка загрузки справочников");
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
            stage.setMaximized(false);
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

    private BigDecimal calculateFinalPrice(Service service, List<Discount> selectedDiscounts) {
        if (service == null) return BigDecimal.ZERO;
        BigDecimal basePrice = service.getBasePrice();
        if (selectedDiscounts == null || selectedDiscounts.isEmpty()) {
            return basePrice.setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal totalDiscount = selectedDiscounts.stream()
                .map(Discount::getDiscountSize)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalDiscount.compareTo(new BigDecimal("100")) > 0) {
            totalDiscount = new BigDecimal("100");
        }
        BigDecimal discountFraction = totalDiscount.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        BigDecimal multiplier = BigDecimal.ONE.subtract(discountFraction);
        return basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }

    private Dialog<ProvidedService> buildDialog(ProvidedService existing) {
        Dialog<ProvidedService> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Добавить оказанную услугу" : "Редактировать");

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);


        ComboBox<Deal> dealCombo = new ComboBox<>();
        dealCombo.getItems().addAll(deals);
        dealCombo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Deal d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? "" : "Сделка №" + d.getId() + " (клиент " + d.getIdClient() + ")");
            }
        });
        dealCombo.setButtonCell(new ListCell<>() {
            protected void updateItem(Deal d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? "" : "Сделка №" + d.getId() + " (клиент " + d.getIdClient() + ")");
            }
        });
        if (existing != null) {
            deals.stream().filter(d -> d.getId() == existing.getIdDeal())
                    .findFirst().ifPresent(dealCombo::setValue);
        }


        ComboBox<Service> serviceCombo = new ComboBox<>();
        serviceCombo.getItems().addAll(services);
        serviceCombo.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Service s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" : s.getId() + " — " + s.getServiceName() + " (" + s.getBasePrice() + ")");
            }
        });
        serviceCombo.setButtonCell(new ListCell<>() {
            protected void updateItem(Service s, boolean empty) {
                super.updateItem(s, empty);
                setText(empty || s == null ? "" : s.getId() + " — " + s.getServiceName() + " (" + s.getBasePrice() + ")");
            }
        });
        if (existing != null) {
            services.stream().filter(s -> s.getId() == existing.getIdService())
                    .findFirst().ifPresent(serviceCombo::setValue);
        }

        ListView<Discount> discountListView = new ListView<>();
        discountListView.getItems().addAll(discounts);
        discountListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        discountListView.setPrefHeight(120);
        discountListView.setCellFactory(lv -> new ListCell<>() {
            protected void updateItem(Discount d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? "" :
                        d.getId() + " — " + d.getDiscountType() + " (" + d.getDiscountSize() + "%)");
            }
        });

        if (existing != null && !existing.getDiscountIds().isEmpty()) {
            for (int i = 0; i < discounts.size(); i++) {
                if (existing.getDiscountIds().contains(discounts.get(i).getId())) {
                    discountListView.getSelectionModel().select(i);
                }
            }
        }

        Label priceLabel = new Label("Итоговая стоимость: ");
        priceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #222;");

        Runnable recalc = () -> {
            Service s = serviceCombo.getValue();
            List<Discount> selected = new ArrayList<>(discountListView.getSelectionModel().getSelectedItems());
            BigDecimal price = calculateFinalPrice(s, selected);
            priceLabel.setText("Итоговая стоимость: " + price);
        };
        serviceCombo.setOnAction(e -> recalc.run());
        discountListView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> recalc.run());
        recalc.run();

        Label errorLabel = new Label();
        errorLabel.setStyle(
                "-fx-text-fill: #e74c3c; -fx-font-size: 13; -fx-font-weight: bold;" +
                        "-fx-background-color: #fdecea; -fx-padding: 6 10; -fx-background-radius: 4;"
        );
        errorLabel.setWrapText(true);
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        Label discountHint = new Label("Удерживайте Ctrl для выбора нескольких скидок");
        discountHint.setStyle("-fx-font-size: 11; -fx-text-fill: #888;");

        VBox box = new VBox(8,
                new Label("Сделка:"), dealCombo,
                new Label("Услуга:"), serviceCombo,
                new Label("Скидки:"), discountListView, discountHint,
                priceLabel,
                errorLabel
        );
        box.setStyle("-fx-padding: 16;");
        dialog.getDialogPane().setContent(box);

        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveBtn);
        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (dealCombo.getValue() == null) {
                errorLabel.setText("Выберите сделку");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }
            if (serviceCombo.getValue() == null) {
                errorLabel.setText("Выберите услугу");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                event.consume();
                return;
            }
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
        });

        dialog.setResultConverter(btn -> {
            if (btn == saveBtn) {
                Deal selectedDeal = dealCombo.getValue();
                Service selectedService = serviceCombo.getValue();
                if (selectedDeal == null || selectedService == null) return null;

                List<Discount> selectedDiscounts = new ArrayList<>(discountListView.getSelectionModel().getSelectedItems());
                List<Integer> discountIds = selectedDiscounts.stream()
                        .map(Discount::getId)
                        .collect(Collectors.toList());
                BigDecimal finalPrice = calculateFinalPrice(selectedService, selectedDiscounts);

                return new ProvidedService(
                        existing != null ? existing.getId() : 0,
                        selectedDeal.getId(),
                        selectedService.getId(),
                        discountIds,
                        existing != null ? existing.getServiceDate() : LocalDateTime.now(),
                        finalPrice.doubleValue()
                );
            }
            return null;
        });

        return dialog;
    }
}
