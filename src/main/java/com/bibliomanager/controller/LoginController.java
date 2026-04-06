package com.bibliomanager.controller;

import com.bibliomanager.DatabaseHandler;
import com.bibliomanager.dao.GetData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginController {
    @FXML
    private Button minimize;
    @FXML
    private Button loginBtn;
    @FXML
    private AnchorPane root;
    @FXML
    private TextField studentPassword;
    @FXML
    private TextField studentNumber;

    @FXML
    public void exit () {
        System.exit(0);
    }
    @FXML
    private void minimize () {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    private double x = 0;
    private double y = 0;

    //NUMBERS ONLY ALLOWED
    public void numbersOnly (KeyEvent event) {
        String character = event.getCharacter();

        if (character == null || character.isEmpty()) {
            return;
        }

        // Allow control keys such as Enter/Backspace so default actions still work.
        if (Character.isISOControl(character.charAt(0))) {
            return;
        }

        if (!character.matches("\\d")) {
            event.consume();
            studentNumber.setStyle("-fx-border-color: #e04040");
        }
        else {
            studentNumber.setStyle("-fx-border-color: #fff");
        }
    }

    //Database tools
    private Connection connection;
    private PreparedStatement pstmt;
    private Statement stmt;
    private ResultSet resultSet;

    public void login () {

        String sql = "SELECT * FROM students WHERE studentNumber = ? AND password = ?";
//        connection = DatabaseHandler.connectDB();

        try (Connection connection = DatabaseHandler.connectDB();
        PreparedStatement pstmt = connection.prepareStatement(sql)){

            pstmt.setString(1, studentNumber.getText());
            pstmt.setString(2, studentPassword.getText());
            Alert alert;

            try (ResultSet resultSet = pstmt.executeQuery()) {
                if (studentNumber.getText().isBlank() || studentPassword.getText().isBlank()) {
                    alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Admin Message");
                    alert.setHeaderText(null);
                    alert.setContentText("Please, fill all blank fields.");
                    alert.showAndWait();
                }
                else {
                    if (resultSet.next()) {
                        GetData.path = resultSet.getString("image");
                        GetData.studentNumber = studentNumber.getText();

                        alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Admin Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Successfully login !");
                        alert.showAndWait();

                        //TO HIDE THE LOGIN FORM
                        loginBtn.getScene().getWindow().hide();

                        //FOR DASHBOARD
                        Parent root = FXMLLoader.load(getClass().getResource("/com/bibliomanager/fxml/dashboard-view.fxml"));
                        Stage stage = new Stage();

                        root.setOnMousePressed((MouseEvent e) -> {
                            x = e.getSceneX();
                            y = e.getSceneY();
                        });
                        root.setOnMouseDragged((MouseEvent e) -> {
                            stage.setX(e.getScreenX() - x);
                            stage.setY(e.getScreenY() - y);
                        });

                        Scene scene = new Scene(root, 986, 600);
                        stage.setScene(scene);
                        stage.initStyle(StageStyle.TRANSPARENT);
                        stage.setMinHeight(600);
                        stage.setMinWidth(986);
                        stage.show();
                    }
                    else {
                        alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Admin Message");
                        alert.setHeaderText(null);
                        alert.setContentText("Wrong username or password");
                        alert.showAndWait();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
