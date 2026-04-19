package com.bibliomanager.controller;

import com.bibliomanager.model.Loan;
import com.bibliomanager.service.LoanService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReturnController {

    @FXML
    private TableView<Loan> returnsTable;
    @FXML
    private TableColumn<Loan,String> colStudent;
    @FXML
    private TableColumn<Loan,String> colBook;
    @FXML
    private TableColumn<Loan,String> colLoanDate;
    @FXML
    private TableColumn<Loan,String> colDueDate;
    @FXML
    private TableColumn<Loan,String> colReturnedOn;
    @FXML
    private TableColumn<Loan,String> colReturnStatus;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> timeFilter;
    @FXML
    private ComboBox<String> typeFilter;
    @FXML
    private Label totalBadge;
    @FXML
    private Label punctualityRateCount;
    @FXML
    private Label expectedReturnsCount;
    @FXML
    private Label averageOverdueCount;
    @FXML
    private VBox  returnThisWeekContainer;
    @FXML
    private Label noOverdueLabel;

    private final LoanService loanService = new LoanService();
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        loadAll();
    }

    // Columns

    private void setupColumns() {
        colStudent.setCellValueFactory(d -> new SimpleStringProperty(
                        d.getValue().getStudent().getFirstName() + " " + d.getValue().getStudent().getLastName()));

        colBook.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBook().getTitle()));

        colLoanDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getLoanDate().format(FMT)));

        colDueDate.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDueDate().format(FMT)));

        colReturnedOn.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getReturnDate() != null ? d.getValue().getReturnDate().format(FMT) : "—"));

        // Return status - dynamically calculated
        colReturnStatus.setCellValueFactory(d -> {
            Loan loan = d.getValue();
            if (loan.getReturnDate() == null)
                return new SimpleStringProperty("—");
            boolean late = loan.getReturnDate().isAfter(loan.getDueDate());
            if (late) {
                long days = ChronoUnit.DAYS.between(
                        loan.getDueDate(), loan.getReturnDate());
                return new SimpleStringProperty("Late +" + days + " days");
            }
            return new SimpleStringProperty("On time");
        });

        colReturnStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); setStyle(""); return; }
                setText(val);
                setAlignment(javafx.geometry.Pos.CENTER);
                boolean late = val.startsWith("Late");
                setStyle("-fx-font-weight: bold; -fx-font-size: 12px;" +
                        (late ? "-fx-text-fill: #b71c1c;" : "-fx-text-fill: #1a7a3c;"));
            }
        });
    }

    // Filters

    private void setupFilters() {
        timeFilter.setItems(FXCollections.observableArrayList("All time", "Today", "This week", "This month"));
        timeFilter.setValue("All time");
        typeFilter.setItems(FXCollections.observableArrayList("All returns", "On time", "Late"));
        typeFilter.setValue("All returns");
        searchField.textProperty().addListener((o, old, v) -> applyFilters());
        timeFilter.valueProperty().addListener((o, old, v) -> applyFilters());
        typeFilter.valueProperty().addListener((o, old, v) -> applyFilters());
    }

    private void applyFilters() {
        String query  = searchField.getText();
        String period = normalizePeriod(timeFilter.getValue());
        String type   = normalizeType(typeFilter.getValue());

        List<Loan> results = loanService.searchReturnedLoans(query, period, type);
        returnsTable.setItems(FXCollections.observableArrayList(results));
        totalBadge.setText(results.size() + " total");
    }

    // Loading

    private void loadAll() {
        loadTable();
        loadStats();
        loadWeeklyChart();
    }

    private void loadTable() {
        List<Loan> loans = loanService.getReturnedLoans(null, null);
        returnsTable.setItems(FXCollections.observableArrayList(loans));
        totalBadge.setText(loans.size() + " total");
    }

    private void loadStats() {
        double rate = loanService.getPunctualityRate();
        int expected = loanService.getExpectedReturnsToday();
        double avgDays = loanService.getAverageOverdueDays();

        punctualityRateCount.setText(String.format("%.0f%%", rate));
        expectedReturnsCount.setText(String.valueOf(expected));
        averageOverdueCount.setText(String.format("%.1f days", avgDays));

        // Dynamic colors
        punctualityRateCount.setStyle("-fx-font-size: 21px; -fx-font-weight: bold;" +
                        (rate >= 80 ? "-fx-text-fill: #1a7a3c;"
                                : rate >= 50
                                ? "-fx-text-fill: #f25f29;"
                                : "-fx-text-fill: #b71c1c;"));

        expectedReturnsCount.setStyle("-fx-font-size: 21px; -fx-font-weight: bold;" +
                (expected > 0 ? "-fx-text-fill: #f25f29;" : "-fx-text-fill: #1a7a3c;"));

        averageOverdueCount.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: #2E3D60;");
    }

    private void loadWeeklyChart() {
        returnThisWeekContainer.getChildren().clear();
        Map<String, Integer> data = loanService.getReturnsThisWeek();

        // Generate the last 7 days even if no data
        LocalDate today = LocalDate.now();
        int maxVal = data.values().stream().mapToInt(i -> i).max().orElse(1);

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            String key = day.toString();
            int count = data.getOrDefault(key, 0);
            String dayLbl = day.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

            HBox row = buildChartRow(dayLbl, count, maxVal);
            returnThisWeekContainer.getChildren().add(row);
        }

        boolean hasData = data.values().stream().anyMatch(v -> v > 0);
        noOverdueLabel.setVisible(!hasData);
        noOverdueLabel.setManaged(!hasData);
    }

    private HBox buildChartRow(String dayLabel, int count, int maxVal) {
        Label day = new Label(dayLabel);
        day.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");
        day.setMinWidth(32);

        double ratio = maxVal > 0 ? (double) count / maxVal : 0;

        ProgressBar bar = new ProgressBar(ratio);
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefHeight(8);
        bar.setStyle("""
            -fx-accent: #2E3D60;
            -fx-background-color: #f0f0ee;
            -fx-background-radius: 4;
            -fx-border-radius: 4;
        """);
        HBox.setHgrow(bar, Priority.ALWAYS);

        Label val = new Label(String.valueOf(count));
        val.setStyle("-fx-font-size: 11px; -fx-text-fill: #555;");
        val.setMinWidth(18);

        HBox row = new HBox(6, day, bar, val);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 4 12;");
        return row;
    }

    // Helpers

    private String normalizePeriod(String val) {
        if (val == null || val.equals("All time")) return null;
        return val;
    }

    private String normalizeType(String val) {
        if (val == null || val.equals("All returns")) return null;
        return val;
    }
}