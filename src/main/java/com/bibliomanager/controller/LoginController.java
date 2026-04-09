package com.bibliomanager.controller;

import com.bibliomanager.service.AuthService;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    void handleLogin(ActionEvent event) {

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Complete all fields please !");
            return;
        }

        AuthService authService = new AuthService();
        boolean success = authService.login(username, password);

        if (success) {
            switchToDashboard();
        }
        else {
            passwordField.clear();
            errorLabel.setText("Invalid credentials !");
        }

    }

    @FXML
    public void initialize() {
        ChangeListener<String> clearError = (obs, oldVal, newVal) -> {
            errorLabel.setText("");
        };
        usernameField.textProperty().addListener(clearError);
        passwordField.textProperty().addListener(clearError);
    }

    @FXML
    private void switchToDashboard() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/bibliomanager/fxml/dashboard-view.fxml"));
            Stage stage = (Stage) errorLabel.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("BiblioManager");
            stage.setResizable(true);
            stage.show();

        } catch (IOException e) {
            errorLabel.setText("Dashboard loading error.");
            e.printStackTrace();
        }
    }

}
