package com.bibliomanager;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Controller {
    @FXML
    private Button minimize;
    @FXML
    private Button loginBtn;

    @FXML
    private AnchorPane root;

    @FXML
    private TextField studentPassword;

    @FXML
    private TextField studentUsername;

    @FXML
    public void exit () {
        System.exit(0);
    }
    @FXML
    private void minimize () {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    };
}
