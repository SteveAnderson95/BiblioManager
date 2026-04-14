package com.bibliomanager.controller;

import com.bibliomanager.model.Loan;
import com.bibliomanager.model.Student;
import com.bibliomanager.service.LoanService;
import com.bibliomanager.service.StudentService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudentController {

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> colStudent;
    @FXML private TableColumn<Student, String> colNumber;
    @FXML private TableColumn<Student, String> colClass;
    @FXML private TableColumn<Student, String> colEmail;
    @FXML private TableColumn<Student, String> colActiveLoans;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> classFilter;
    @FXML private Label totalBadge;

    @FXML private VBox detailPanel;
    @FXML private Label avatarLabel;
    @FXML private Label studentLabel;
    @FXML private Label classLabel;
    @FXML private Label detailNumber;
    @FXML private Label detailClass;
    @FXML private Label detailEmail;
    @FXML private VBox loansContainer;
    @FXML private Label noLoansLabel;

    private final StudentService studentService = new StudentService();
    private final LoanService loanService = new LoanService();
    private Student selectedStudent;

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");

    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        setupSelectionListener();
        loadStudents();
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
    }

    // columns
    private void setupColumns() {
        colStudent.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getFirstName() + " " + d.getValue().getLastName()));

        colNumber.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getStudentNumber()));

        colClass.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getSchoolClass()));

        colEmail.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getEmail() != null ? d.getValue().getEmail() : "—"));

        // column Active Loans with colored badge
        colActiveLoans.setCellValueFactory(d ->
                new SimpleStringProperty(getActiveLoansBadgeText(d.getValue())));

        colActiveLoans.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); setStyle(""); return; }
                setText(val);
                setAlignment(javafx.geometry.Pos.CENTER);
                setStyle("-fx-font-weight: bold; -fx-font-size: 12px;" +
                        (val.contains("overdue") ? "-fx-text-fill: #b71c1c;" :
                                val.equals("None")      ? "-fx-text-fill: #999999;" :
                                        "-fx-text-fill: #2E3D60;"));
            }
        });
    }

    // filters
    private void setupFilters() {
        List<String> classes = studentService.getAllClasses();
        classFilter.setItems(FXCollections.observableArrayList(classes));

        searchField.textProperty().addListener((o, old, v) -> applyFilters());
        classFilter.valueProperty().addListener((o, old, v) -> applyFilters());
    }

    private void applyFilters() {
        String query = searchField.getText();
        String schoolClass = classFilter.getValue();
        List<Student> results = studentService.searchStudents(query, schoolClass);
        studentTable.setItems(FXCollections.observableArrayList(results));
        totalBadge.setText(results.size() + " total");
    }

    // selection
    private void setupSelectionListener() {
        studentTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, student) -> {
                    if (student != null) showDetailPanel(student);
                });
    }

    private void loadStudents() {
        List<Student> students = studentService.getAllStudents();
        studentTable.setItems(FXCollections.observableArrayList(students));
        totalBadge.setText(students.size() + " total");
    }

    // detail panel
    private void showDetailPanel(Student student) {
        selectedStudent = student;

        String initials = (student.getFirstName().charAt(0) + "" + student.getLastName().charAt(0)).toUpperCase();
        avatarLabel.setText(initials);
        studentLabel.setText(student.getFirstName() + " " + student.getLastName());
        classLabel.setText(student.getSchoolClass());
        detailNumber.setText(student.getStudentNumber());
        detailClass.setText(student.getSchoolClass());
        detailEmail.setText(student.getEmail() != null ? student.getEmail() : "—");

        loadStudentLoans(student.getId());

        detailPanel.setVisible(true);
        detailPanel.setManaged(true);
    }

    private void loadStudentLoans(long studentId) {
        loansContainer.getChildren().clear();

        List<Loan> loans = loanService.getActiveLoansByStudent(studentId);

        if (loans.isEmpty()) {
            noLoansLabel.setVisible(true);
            noLoansLabel.setManaged(true);
            return;
        }

        noLoansLabel.setVisible(false);
        noLoansLabel.setManaged(false);

        for (Loan loan : loans) {
            HBox row = buildLoanRow(loan);
            loansContainer.getChildren().add(row);
        }
    }

    private HBox buildLoanRow(Loan loan) {
        // title + date
        Label bookTitle = new Label(loan.getBook().getTitle());
        bookTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        bookTitle.setWrapText(true);
        bookTitle.setMaxWidth(160);

        String dueDateStr = loan.getDueDate() != null ? "Due " + loan.getDueDate().format(DATE_FMT) : "";
        Label dueDate = new Label(dueDateStr);
        dueDate.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaaaaa;");

        VBox info = new VBox(2, bookTitle, dueDate);
        info.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(info, Priority.ALWAYS);

        // status badge
        String statusText = loan.getStatus().toString();
        Label status = new Label(statusText);
        String color = switch (loan.getStatus()) {
            case ONGOING  -> "-fx-text-fill: #2E3D60;";
            case OVERDUE  -> "-fx-text-fill: #b71c1c;";
            case RETURNED -> "-fx-text-fill: #1a7a3c;";
        };
        status.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;" + color);

        HBox row = new HBox(10, info, status);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("""
            -fx-background-color: #f8f8f6;
            -fx-background-radius: 6;
            -fx-padding: 8 12;
        """);
        return row;
    }

    @FXML
    private void closeDetailPanel() {
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
        studentTable.getSelectionModel().clearSelection();
        selectedStudent = null;
    }

    // actions
    @FXML
    private void handleAdd() { openDialog(null); }

    @FXML
    private void handleEdit() {
        if (selectedStudent != null) openDialog(selectedStudent);
    }

    @FXML
    private void handleDelete() {
        if (selectedStudent == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete student");
        confirm.setHeaderText("Delete \""
                + selectedStudent.getFirstName() + " "
                + selectedStudent.getLastName() + "\" ?");
        confirm.setContentText("This action cannot be undone.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    studentService.deleteStudent(selectedStudent.getId());
                    closeDetailPanel();
                    loadStudents();
                } catch (RuntimeException e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    private void openDialog(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bibliomanager/fxml/student-dialog-view.fxml"));
            Parent root = loader.load();

            StudentDialogController ctrl = loader.getController();
            ctrl.setStudent(student);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(studentTable.getScene().getWindow());
            dialog.setTitle(student == null ? "Add student" : "Edit student");
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.showAndWait();

            loadStudents();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // helpers
    private String getActiveLoansBadgeText(Student student) {
        try {
            List<Loan> loans = loanService.getActiveLoansByStudent(student.getId());
            if (loans.isEmpty()) return "None";
            long overdue = loans.stream()
                    .filter(l -> l.getStatus() == com.bibliomanager.model.LoanStatus.OVERDUE)
                    .count();
            if (overdue > 0) return overdue + " overdue";
            return loans.size() + " active";
        } catch (Exception e) {
            return "—";
        }
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}