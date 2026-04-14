package com.bibliomanager.controller;

import com.bibliomanager.model.Student;
import com.bibliomanager.service.StudentService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.util.List;

public class StudentDialogController {

    @FXML private Label dialogTitle;
    @FXML private TextField firstnameField, lastnameField;
    @FXML private TextField numberField, emailField;
    @FXML private ComboBox<String> classCombo;
    @FXML private Label errorLabel;

    private final StudentService studentService = new StudentService();
    private Student editingStudent = null;

    @FXML
    public void initialize() {
        List<String> classes = studentService.getAllClasses();

        classCombo.setItems(FXCollections.observableArrayList(classes));

        firstnameField.textProperty().addListener((o, old, v) -> errorLabel.setText(""));
        lastnameField.textProperty().addListener((o, old, v)  -> errorLabel.setText(""));
        numberField.textProperty().addListener((o, old, v)    -> errorLabel.setText(""));
        emailField.textProperty().addListener((o, old, v)     -> errorLabel.setText(""));
        classCombo.valueProperty().addListener((o, old, v)    -> errorLabel.setText(""));
    }

    public void setStudent(Student student) {
        this.editingStudent = student;
        if (student == null) {
            dialogTitle.setText("Add Student");
            return;
        }
        dialogTitle.setText("Edit Student");
        firstnameField.setText(student.getFirstName());
        lastnameField.setText(student.getLastName());
        numberField.setText(student.getStudentNumber());
        emailField.setText(student.getEmail() != null ? student.getEmail() : "");
        classCombo.setValue(student.getSchoolClass());
    }

    @FXML
    private void handleSave() {
        errorLabel.setText("");
        try {
            String firstName = firstnameField.getText().trim();
            String lastName = lastnameField.getText().trim();
            String number = numberField.getText().trim();
            String email = emailField.getText().trim();
            String cls = classCombo.getValue();

            Student student = editingStudent != null
                    ? new Student(editingStudent.getId(), number,
                    lastName, firstName, cls,
                    email.isEmpty() ? null : email)
                    : new Student(number, lastName, firstName, cls,
                    email.isEmpty() ? null : email);

            if (editingStudent != null) studentService.updateStudent(student);
            else studentService.addStudent(student);

            closeDialog();

        } catch (RuntimeException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleClose() { closeDialog(); }

    private void closeDialog() {
        ((Stage) firstnameField.getScene().getWindow()).close();
    }
}