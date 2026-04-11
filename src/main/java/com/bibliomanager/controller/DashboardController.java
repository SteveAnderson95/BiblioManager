package com.bibliomanager.controller;

import com.bibliomanager.UserSession;
import com.bibliomanager.model.Librarian;
import com.bibliomanager.service.AuthService;
import com.bibliomanager.utils.DateUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class DashboardController {

    @FXML
    private Label avatarLabel;
    @FXML
    private HBox btnBooks;
    @FXML
    private HBox btnBorrows;
    @FXML
    private HBox btnDashboard;
    @FXML
    private HBox btnHistory;
    @FXML
    private HBox btnLogout;
    @FXML
    private HBox btnReturns;
    @FXML
    private HBox btnSettings;
    @FXML
    private HBox btnStudents;
    @FXML
    private Label dateLabel;
    @FXML
    private BorderPane mainContainer;
    @FXML
    private VBox sidebar;
    @FXML
    private Label userLoginLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label viewTitleLabel;
    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        if (UserSession.isLoggedIn()) {
            Librarian user = UserSession.getInstance().getCurrentUser();
            userNameLabel.setText(user.getFirstName() + " " + user.getLastName());
            userLoginLabel.setText(user.getUsername());
            String initials = user.getFirstName().substring(0, 1).toUpperCase() +
                    user.getLastName().substring(0, 1).toUpperCase();
            avatarLabel.setText(initials);
            dateLabel.setText(DateUtils.getFormattedDate());
            showDashboardView();
        }
    }

    @FXML
    public void showDashboardView() {
        viewTitleLabel.setText("Dashboard");
        setActiveBtn(btnDashboard);
        loadView("home-view.fxml");
    }

    @FXML
    public void showBooksView() {
        viewTitleLabel.setText("Books Collection");
        setActiveBtn(btnBooks);
        loadView("books-view.fxml");
    }

    @FXML
    public void showStudentsView() {
        viewTitleLabel.setText("Students Registry");
        setActiveBtn(btnStudents);
        loadView("students-view.fxml");
    }

    @FXML
    public void showBorrowsView() {
        viewTitleLabel.setText("Active loans");
        setActiveBtn(btnBorrows);
        loadView("borrows-view.fxml");
    }

    @FXML
    public void showReturnsView() {
        viewTitleLabel.setText("Returns Management");
        setActiveBtn(btnReturns);
        loadView("returns-view.fxml");
    }

    @FXML
    public void showHistoryView() {
        viewTitleLabel.setText("Activity History");
        setActiveBtn(btnHistory);
        loadView("history-view.fxml");
    }

    @FXML
    public void showSettingsView() {
        viewTitleLabel.setText("Settings");
        setActiveBtn(btnSettings);
        loadView("settings-view.fxml");
    }

    public void setActiveBtn(HBox activeBtn) {
        List<HBox> allButtons = Arrays.asList(btnDashboard, btnBooks, btnBorrows, btnStudents, btnReturns, btnHistory, btnSettings);
        for (HBox box : allButtons)
            box.getStyleClass().remove("nav-item-selected");
        activeBtn.getStyleClass().add("nav-item-selected");
    }

    public void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bibliomanager/fxml/" + fxmlFile));
            Parent view = loader.load();
            mainContainer.setCenter(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogout() {
        authService.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bibliomanager/fxml/login-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 500);
            Stage stage = (Stage) mainContainer.getScene().getWindow();
            stage.setMaximized(false);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.setTitle("BiblioManager - Login");
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
