package com.bibliomanager.controller;

import com.bibliomanager.model.Loan;
import com.bibliomanager.service.DashboardService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

public class HomeController {

    @FXML
    private Label activeLoansLabel;
    @FXML
    private Label availableBooksLabel;
    @FXML
    private Label availablePercentageLabel;
    @FXML
    private PieChart categoryPieChart;
    @FXML
    private TableColumn<Loan, String> colOverdueBook;
    @FXML
    private TableColumn<Loan, String> colOverdueDays;
    @FXML
    private TableColumn<Loan, String> colOverdueStudent;
    @FXML
    private TableColumn<Loan, String> colRecentBook;
    @FXML
    private TableColumn<Loan, String> colRecentStatus;
    @FXML
    private TableColumn<Loan, String> colRecentStudent;
    @FXML
    private Label overdueLabel;
    @FXML
    private TableView<Loan> overdueTable;
    @FXML
    private TableView<Loan> recentBorrowsTable;
    @FXML
    private Label totalBooksLabel;
    @FXML
    private Label totalCategoriesLabel;
    @FXML
    private BarChart<String, Number> weeklyBarChart;

    private final DashboardService dashboardService = new DashboardService();

    @FXML
    public void initialize() {
        setupTablePolicies();
        refreshDashboard();
    }

    private void refreshDashboard() {

        //1. Quick stats
        Map<String, Object> stats = dashboardService.getQuickStats();
        totalBooksLabel.setText(stats.get("totalBooks").toString());
        totalCategoriesLabel.setText(stats.get("totalCategories").toString());
        availableBooksLabel.setText(stats.get("availableBooks").toString());
        availablePercentageLabel.setText(stats.get("availabilityRate").toString() + "%");
        activeLoansLabel.setText(stats.get("activeLoans").toString());
        overdueLabel.setText(stats.get("overdueLoans").toString());

        //2. Weekly Trend BarChart
        weeklyBarChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Borrowings");
        dashboardService.getWeeklyTrend().forEach((date, count) -> {
            series.getData().add(new XYChart.Data<>(date, count));
        });
        weeklyBarChart.getData().add(series);

        //3. PieChart(Stock by Category)
        categoryPieChart.getData().clear();
        dashboardService.getCategoryData().forEach((name, count) -> {
            categoryPieChart.getData().add(new PieChart.Data(name, count));
        });

        //4. Tableviews
        loadRecentBorrows();
        loadOverdueTable();
    }

    private void loadRecentBorrows() {
        colRecentStudent.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudent().getFirstName() + " " + cellData.getValue().getStudent().getLastName()));

        colRecentBook.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBook().getTitle()));

        //I used a CellFactory to put color on the status
        colRecentStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStatus().toString()));
        colRecentStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    setAlignment(javafx.geometry.Pos.CENTER);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
                    switch (status) {
                        case "ONGOING"  -> setStyle(getStyle() + "-fx-background-color: #1f92de;");
                        case "RETURNED" -> setStyle(getStyle() + "-fx-background-color: #1fa162;");
                        case "OVERDUE"  -> setStyle(getStyle() + "-fx-background-color: #ff6b6b;");
                    }
                }
            }
        });
        recentBorrowsTable.setItems(FXCollections.observableArrayList(dashboardService.getRecentBorrowings()));
    }

    private void loadOverdueTable() {
        colOverdueStudent.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStudent().getFirstName() + " " + cellData.getValue().getStudent().getLastName()));

        colOverdueBook.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getBook().getTitle()));

        colOverdueDays.setCellValueFactory(cellData -> {
            long days = ChronoUnit.DAYS.between(cellData.getValue().getDueDate(), LocalDate.now());
            return new SimpleStringProperty("+" + days + " jours");
        });

        colOverdueDays.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || value == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(value);
                    setStyle("-fx-text-fill: #ff6b6b; -fx-font-weight: bold; -fx-alignment: CENTER;");
                }
            }
        });

        overdueTable.setItems(FXCollections.observableArrayList(dashboardService.getOverdueAlerts()));
    }

    private void setupTablePolicies() {
        // Avoid the drag-and-drop of columns to keep a fix design
        recentBorrowsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        overdueTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }
}
