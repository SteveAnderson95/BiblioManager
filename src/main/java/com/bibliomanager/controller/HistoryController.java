package com.bibliomanager.controller;

import com.bibliomanager.UserSession;
import com.bibliomanager.model.Loan;
import com.bibliomanager.service.CsvExportService;
import com.bibliomanager.service.LoanService;
import com.bibliomanager.service.PdfReportService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryController {

    @FXML private TableView<Loan> historyTable;
    @FXML private TableColumn<Loan,String> colStudent;
    @FXML private TableColumn<Loan,String> colBook;
    @FXML private TableColumn<Loan,String> colLoanDate;
    @FXML private TableColumn<Loan,String> colDueDate;
    @FXML private TableColumn<Loan,String> colReturnDate;
    @FXML private TableColumn<Loan,String> colStatus;
    @FXML private TableColumn<Loan,String> colRegisteredBy;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private ComboBox<String> periodFilter;
    @FXML private Label totalBadge;

    private final LoanService loanService = new LoanService();
    private final CsvExportService csvExportService = new CsvExportService();
    private final PdfReportService pdfReportService = new PdfReportService();

    private List<Loan> currentLoans;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        loadAll();
    }

    private void setupColumns() {
        colStudent.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getStudent().getFirstName() + " " + d.getValue().getStudent().getLastName()));

        colBook.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getBook().getTitle()));

        colLoanDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getLoanDate().format(FMT)));

        colDueDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getDueDate().format(FMT)));

        colReturnDate.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getReturnDate() != null ? d.getValue().getReturnDate().format(FMT) : "-"));

        colStatus.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getStatus().toString()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); setStyle(""); return; }
                setText(val);
                setAlignment(javafx.geometry.Pos.CENTER);
                setStyle("-fx-font-weight: bold; -fx-font-size: 12px;" +
                        switch (val) {
                            case "RETURNED" -> "-fx-text-fill: #1a7a3c;";
                            case "OVERDUE" -> "-fx-text-fill: #b71c1c;";
                            default -> "-fx-text-fill: #2E3D60;";
                        });
            }
        });

        colRegisteredBy.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getRegisteredBy() != null ? d.getValue().getRegisteredBy().getUsername() : "-"));
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList("All status", "ONGOING", "RETURNED", "OVERDUE"));
        statusFilter.setValue("All status");

        periodFilter.setItems(FXCollections.observableArrayList("All time", "Today", "This week", "This month"));
        periodFilter.setValue("All time");

        searchField.textProperty().addListener((o, old, v) -> applyFilters());
        statusFilter.valueProperty().addListener((o, old, v) -> applyFilters());
        periodFilter.valueProperty().addListener((o, old, v) -> applyFilters());
    }

    private void loadAll() {
        currentLoans = loanService.getAllLoans(null, null);
        historyTable.setItems(FXCollections.observableArrayList(currentLoans));
        totalBadge.setText(currentLoans.size() + " records");
    }

    private void applyFilters() {
        String query  = searchField.getText();
        String status = statusFilter.getValue().equals("All status") ? null : statusFilter.getValue();
        String period = periodFilter.getValue().equals("All time") ? null : periodFilter.getValue();

        currentLoans = loanService.searchAllLoans(query, status, period);
        historyTable.setItems(FXCollections.observableArrayList(currentLoans));
        totalBadge.setText(currentLoans.size() + " records");
    }

    @FXML
    private void handleExportCsv() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save CSV");
        fc.setInitialFileName("history_export.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        File file = fc.showSaveDialog(historyTable.getScene().getWindow());
        if (file == null) return;

        try {
            csvExportService.exportLoans(file.getAbsolutePath(), currentLoans);
            showSuccess("CSV exported successfully.");
        } catch (Exception e) {
            showError("Export failed: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportPdf() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save PDF Report");
        fc.setInitialFileName("library_report.pdf");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

        File file = fc.showSaveDialog(historyTable.getScene().getWindow());
        if (file == null) return;

        try {
            String period = periodFilter.getValue();
            String librarian = UserSession.getInstance()
                    .getCurrentUser().getFirstName() + " "
                    + UserSession.getInstance().getCurrentUser().getLastName();

            pdfReportService.generateReport(file.getAbsolutePath(), currentLoans, period, librarian);
            showSuccess("PDF report generated successfully.");
        } catch (Exception e) {
            showError("PDF generation failed: " + e.getMessage());
        }
    }

    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}