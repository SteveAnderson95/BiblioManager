package com.bibliomanager.controller;

import com.bibliomanager.UserSession;
import com.bibliomanager.model.Book;
import com.bibliomanager.model.Librarian;
import com.bibliomanager.model.Loan;
import com.bibliomanager.model.Student;
import com.bibliomanager.service.BookService;
import com.bibliomanager.service.LoanService;
import com.bibliomanager.service.StudentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class LoanController {

    @FXML
    private Label activeLoansCount;
    @FXML
    private ComboBox<Book> bookCombo;
    @FXML
    private TableView<Loan> borrowsTable;
    @FXML
    private TableColumn<Loan, String> colAction;
    @FXML
    private TableColumn<Loan, String> colBook;
    @FXML
    private TableColumn<Loan, String> colDueDate;
    @FXML
    private TableColumn<Loan, String> colLoanDate;
    @FXML
    private TableColumn<Loan, String> colStatus;
    @FXML
    private TableColumn<Loan, String> colStudent;
    @FXML
    private DatePicker dueDatePicker;
    @FXML
    private VBox mostOverdueContainer;
    @FXML
    private Label noOverdueLabel;
    @FXML
    private Label overdueCount;
    @FXML
    private Label returnedTodayCount;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private ComboBox<Student> studentCombo;
    @FXML
    private Label totalBadge;

    private final LoanService loanService = new LoanService();
    private final BookService bookService = new BookService();
    private final StudentService studentService = new StudentService();

    private static final DateTimeFormatter FMT =  DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @FXML
    public void initialize() {
        setupForm();
        setupColumns();
        setupFilters();
        loadAll();
    }

    private void setupForm() {
        studentCombo.setItems(FXCollections.observableArrayList(
                studentService.getAllStudents()));
        studentCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Student s) {
                return s == null ? "" : s.getFirstName() + " " + s.getLastName() + " - " + s.getStudentNumber();
            }
            @Override public Student fromString(String s) { return null; }
        });
        List<Book> availableBooks = bookService.getAllBooks().stream()
                                        .filter(Book::isAvailable)
                                        .toList();
        bookCombo.setItems(FXCollections.observableArrayList(availableBooks));
        bookCombo.setConverter(new StringConverter<>() {
            @Override public String toString(Book b) {
                return b == null ? "" : b.getTitle() + " (" + b.getAvailableQuantity() + " avail.)";
            }
            @Override public Book fromString(String s) { return null; }
        });

        // Default due day : today + 14jrs
        dueDatePicker.setValue(LocalDate.now().plusDays(14));
    }

    @FXML
    private void handleRegister() {
        Student student = studentCombo.getValue();
        Book book       = bookCombo.getValue();
        LocalDate due   = dueDatePicker.getValue();

        if (student == null || book == null || due == null) {
            showError("Please fill all fields.");
            return;
        }

        try {
            Librarian librarian = UserSession.getInstance().getCurrentUser();
            Loan loan = new Loan(student, book, librarian, LocalDate.now(), due, null);
            loanService.registerLoan(loan);
            // Reset form
            studentCombo.setValue(null);
            bookCombo.setValue(null);
            dueDatePicker.setValue(LocalDate.now().plusDays(14));
            // Refresh tout
            loadAll();
            refreshBookCombo();

        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void refreshBookCombo() {
        List<Book> available = bookService.getAllBooks().stream()
                .filter(Book::isAvailable)
                .toList();
        bookCombo.setItems(FXCollections.observableArrayList(available));
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

        // Status with color
        colStatus.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getStatus().toString()));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null); setStyle(""); return;
                }
                setText(status);
                setAlignment(javafx.geometry.Pos.CENTER);
                setStyle("-fx-font-weight: bold; -fx-font-size: 12px;" +
                        switch (status) {
                            case "ONGOING" -> "-fx-text-fill: #2E3D60";
                            case "OVERDUE" -> "-fx-text-fill: #b71c1c";
                            default -> "";
                        });
            }
        });

        // Action column (button per line)
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Mark as returned");
            {
                btn.setStyle("""
                    -fx-background-color: #fff;
                    -fx-border-color: #1a7a3c;
                    -fx-border-radius: 6;
                    -fx-background-radius: 6;
                    -fx-text-fill: #1a7a3c;
                    -fx-font-size: 11px;
                    -fx-font-weight: bold;
                    -fx-cursor: hand;
                """);
                btn.setOnAction(e -> {
                    Loan loan = getTableView().getItems().get(getIndex());
                    handleMarkAsReturned(loan);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
                setText(null);
            }
        });
    }

    private void setupFilters() {
        statusFilter.setItems(FXCollections.observableArrayList("All status", "Ongoing", "Overdue"));
        statusFilter.setValue("All status");

        searchField.textProperty().addListener((o, old, v) -> applyFilters());
        statusFilter.valueProperty().addListener((o, old, v) -> applyFilters());
    }

    private void applyFilters() {
        String query  = searchField.getText();
        String status = statusFilter.getValue();
        List<Loan> results = loanService.searchActiveLoans(query, status);
        borrowsTable.setItems(FXCollections.observableArrayList(results));
    }

    //DATA LOADING

    private void loadAll() {
        loadTable();
        loadStats();
        loadMostOverdue();
    }

    private void loadTable() {
        List<Loan> loans = loanService.getActiveLoans();
        borrowsTable.setItems(FXCollections.observableArrayList(loans));
        totalBadge.setText(loans.size() + " active");
    }

    private void loadStats() {
        int active  = loanService.getActiveLoansCount();
        int overdue = loanService.getOverdueLoansCount();
        int today   = loanService.getReturnedTodayCount();

        activeLoansCount.setText(String.valueOf(active));
        overdueCount.setText(String.valueOf(overdue));
        returnedTodayCount.setText(String.valueOf(today));

        // Dynamic colors
        activeLoansCount.setStyle(
                "-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: #2E3D60;");
        overdueCount.setStyle("-fx-font-size: 21px; -fx-font-weight: bold;" +
                        (overdue > 0 ? "-fx-text-fill: #b71c1c;" : "-fx-text-fill: #1a7a3c;"));
        returnedTodayCount.setStyle("-fx-font-size: 21px; -fx-font-weight: bold; -fx-text-fill: #1a7a3c;");
    }

    private void loadMostOverdue() {
        mostOverdueContainer.getChildren().clear();
        List<Loan> overdue = loanService.getMostOverdue();

        if (overdue.isEmpty()) {
            noOverdueLabel.setVisible(true);
            noOverdueLabel.setManaged(true);
            return;
        }
        noOverdueLabel.setVisible(false);
        noOverdueLabel.setManaged(false);

        for (Loan loan : overdue) {
            HBox row = buildOverdueRow(loan);
            mostOverdueContainer.getChildren().add(row);
        }
    }

    private HBox buildOverdueRow(Loan loan) {
        long days = ChronoUnit.DAYS.between(loan.getDueDate(), LocalDate.now());

        Label name = new Label(loan.getStudent().getFirstName() + " " + loan.getStudent().getLastName());
        name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label book = new Label(loan.getBook().getTitle());
        book.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa;");

        VBox info = new VBox(2, name, book);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label daysLabel = new Label("+" + days + "j");
        daysLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #b71c1c;");

        HBox row = new HBox(8, info, daysLabel);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.setStyle("-fx-padding: 8 15;");
        return row;
    }

    //  ACTIONS

    private void handleMarkAsReturned(Loan loan) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Mark as returned");
        confirm.setHeaderText("Return \"" + loan.getBook().getTitle() + "\" ?");
        confirm.setContentText("Student: " + loan.getStudent().getFirstName() + " " + loan.getStudent().getLastName());

        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    loanService.markAsReturned(loan.getId(), loan.getBook().getId());
                    loadAll();
                    refreshBookCombo();
                } catch (RuntimeException e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
