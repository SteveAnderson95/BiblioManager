package com.bibliomanager.controller;

import com.bibliomanager.UserSession;
import com.bibliomanager.model.AppSettings;
import com.bibliomanager.model.Category;
import com.bibliomanager.model.Librarian;
import com.bibliomanager.service.*;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class SettingsController {

    @FXML private Button btnProfile, btnCategories, btnLibrary, btnDefaults, btnDanger;
    @FXML private VBox profileSection, categoriesSection, librarySection, defaultsSection, dangerSection;
    @FXML private Label avatarLabel, profileName, profileUsername;
    @FXML private Label statLoans, statToday, statReturns;
    @FXML private TextField firstNameField, lastNameField;
    @FXML private TextField usernameField, emailField;
    @FXML private PasswordField newPasswordField, confirmPasswordField;
    @FXML private Label profileErrorLabel;
    @FXML private VBox categoriesContainer;
    @FXML private TextField newCatNameField, newCatDescField;
    @FXML private TextField libraryNameField, libraryAddressField;
    @FXML private TextField defaultDurationField, maxLoansField;

    private final LibrarianService librarianService = new LibrarianService();
    private final CategoryService categoryService = new CategoryService();
    private final AppSettingsService settingsService = new AppSettingsService();
    private final LoanService loanService = new LoanService();

    @FXML
    public void initialize() {
        showProfile();
    }

    private void showSection(VBox section) {
        for (VBox s : List.of(profileSection, categoriesSection, librarySection, defaultsSection, dangerSection)) {
            s.setVisible(false);
            s.setManaged(false);
        }
        section.setVisible(true);
        section.setManaged(true);
    }

    private void setActiveBtn(Button active) {
        for (Button b : List.of(btnProfile, btnCategories, btnLibrary, btnDefaults, btnDanger)) {
            b.getStyleClass().remove("nav-item-selected");
        }
        active.getStyleClass().add("nav-item-selected");
    }

    @FXML public void showProfile() {
        showSection(profileSection);
        setActiveBtn(btnProfile);
        loadProfile();
    }

    @FXML public void showCategories() {
        showSection(categoriesSection);
        setActiveBtn(btnCategories);
        loadCategories();
    }

    @FXML public void showLibrary() {
        showSection(librarySection);
        setActiveBtn(btnLibrary);
        loadLibraryInfo();
    }

    @FXML public void showDefaults() {
        showSection(defaultsSection);
        setActiveBtn(btnDefaults);
        loadDefaults();
    }

    @FXML public void showDanger() {
        showSection(dangerSection);
        setActiveBtn(btnDanger);
    }

    private void loadProfile() {
        Librarian lib = UserSession.getInstance().getCurrentUser();
        String initials = lib.getFirstName().charAt(0) + "" + lib.getLastName().charAt(0);
        avatarLabel.setText(initials.toUpperCase());
        profileName.setText(lib.getFirstName() + " " + lib.getLastName());
        profileUsername.setText(lib.getUsername() + " · Librarian");

        firstNameField.setText(lib.getFirstName());
        lastNameField.setText(lib.getLastName());
        usernameField.setText(lib.getUsername());
        emailField.setText(lib.getEmail());

        // Stats
        statLoans.setText(String.valueOf(loanService.getActiveLoansCount()));
        statToday.setText(String.valueOf(loanService.getReturnedTodayCount()));
        statReturns.setText(String.valueOf(loanService.getOverdueAlerts().size()));

        profileErrorLabel.setText("");
    }

    @FXML
    private void saveProfile() {
        profileErrorLabel.setText("");
        try {
            Librarian current = UserSession.getInstance().getCurrentUser();

            Librarian updated = new Librarian(
                    current.getId(),
                    firstNameField.getText().trim(),
                    lastNameField.getText().trim(),
                    usernameField.getText().trim(),
                    emailField.getText().trim(),
                    current.getPassword()
            );
            librarianService.updateProfile(updated);

            //Password if filled
            String newPwd = newPasswordField.getText();
            String confirmPwd = confirmPasswordField.getText();
            if (!newPwd.isBlank()) {
                librarianService.updatePassword(
                        current.getId(),
                        current.getPassword(),
                        newPwd, confirmPwd);
            }

            // Session update
            UserSession.getInstance().setCurrentUser(updated);
            newPasswordField.clear();
            confirmPasswordField.clear();
            loadProfile();
            showSuccess("Profile updated successfully.");

        } catch (RuntimeException e) {
            profileErrorLabel.setText(e.getMessage());
            profileErrorLabel.setStyle("-fx-text-fill: #b71c1c; -fx-font-size: 12px;");
        }
    }

    @FXML
    private void cancelProfile() {
        loadProfile();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private void loadCategories() {
        categoriesContainer.getChildren().clear();
        List<Category> cats = categoryService.getAllCategories();
        for (Category cat : cats) {
            categoriesContainer.getChildren().add(buildCategoryRow(cat));
        }
    }

    private HBox buildCategoryRow(Category cat) {
        Label name = new Label(cat.getName());
        name.setStyle("-fx-font-size: 14px; -fx-text-fill: #1E1F20;");

        // Number of books
        int bookCount = categoryService.getBookCountByCategory(cat.getId());
        Label count = new Label(bookCount + " books");
        count.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaaaaa;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("✎");
        editBtn.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e0e0e0;
            -fx-border-width: 0.5;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-cursor: hand;
            -fx-padding: 4 10;
            -fx-font-size: 13px;
        """);
        editBtn.setOnAction(e -> handleEditCategory(cat));

        Button delBtn = new Button("✕");
        delBtn.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #f5b5b5;
            -fx-border-width: 0.5;
            -fx-border-radius: 6;
            -fx-background-radius: 6;
            -fx-text-fill: #b71c1c;
            -fx-cursor: hand;
            -fx-padding: 4 10;
            -fx-font-size: 13px;
        """);
        delBtn.setOnAction(e -> handleDeleteCategory(cat));

        HBox row = new HBox(10, name, count, spacer, editBtn, delBtn);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setStyle("""
            -fx-background-color: #f5f5f3;
            -fx-background-radius: 8;
            -fx-padding: 10 14;
            -fx-border-radius: 8;
        """);
        VBox.setMargin(row, new javafx.geometry.Insets(0, 0, 6, 0));
        return row;
    }

    @FXML
    private void handleAddCategory() {
        String name = newCatNameField.getText().trim();
        String desc = newCatDescField.getText().trim();
        if (name.isBlank()) {
            showError("Category name is required.");
            return;
        }
        try {
            categoryService.addCategory(new Category(name, desc));
            newCatNameField.clear();
            newCatDescField.clear();
            loadCategories();
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    private void handleEditCategory(Category cat) {
        TextInputDialog dialog = new TextInputDialog(cat.getName());
        dialog.setTitle("Edit category");
        dialog.setHeaderText("Rename category");
        dialog.setContentText("New name:");
        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.isBlank()) {
                try {
                    categoryService.updateCategory(new Category(cat.getId(), newName, cat.getDescription()));
                    loadCategories();
                } catch (RuntimeException e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    private void handleDeleteCategory(Category cat) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete category");
        confirm.setHeaderText("Delete \"" + cat.getName() + "\" ?");
        confirm.setContentText("Books in this category will lose their category.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    categoryService.deleteCategory(cat.getId());
                    loadCategories();
                } catch (RuntimeException e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    private void loadLibraryInfo() {
        AppSettings s = settingsService.getSettings();
        libraryNameField.setText(s.getLibraryName());
        libraryAddressField.setText(s.getLibraryAddress());
    }

    @FXML
    private void saveLibraryInfo() {
        try {
            AppSettings s = settingsService.getSettings();
            s.setLibraryName(libraryNameField.getText().trim());
            s.setLibraryAddress(libraryAddressField.getText().trim());
            settingsService.saveSettings(s);
            showSuccess("Library info saved.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    private void loadDefaults() {
        AppSettings s = settingsService.getSettings();
        defaultDurationField.setText(String.valueOf(s.getDefaultLoanDuration()));
        maxLoansField.setText(String.valueOf(s.getMaxLoansPerStudent()));
    }

    @FXML
    private void saveDefaults() {
        try {
            int duration = Integer.parseInt(defaultDurationField.getText().trim());
            int maxLoans = Integer.parseInt(maxLoansField.getText().trim());
            AppSettings s = settingsService.getSettings();
            s.setDefaultLoanDuration(duration);
            s.setMaxLoansPerStudent(maxLoans);
            settingsService.saveSettings(s);
            showSuccess("Defaults saved.");
        } catch (NumberFormatException e) {
            showError("Values must be numbers.");
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleResetLoans() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset loans");
        confirm.setHeaderText("Reset all loan history?");
        confirm.setContentText("This will permanently delete all loan records.\n" +
                "Books and students will be kept. This cannot be undone.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                settingsService.resetLoans();
                showSuccess("All loans have been reset.");
            }
        });
    }

    @FXML
    private void handleFullReset() {
        // Double confirmation for such a destructive action
        Alert first = new Alert(Alert.AlertType.CONFIRMATION);
        first.setTitle("Full reset");
        first.setHeaderText("Are you sure?");
        first.setContentText("This will wipe ALL data including books, students and loans.");
        first.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                Alert second = new Alert(Alert.AlertType.CONFIRMATION);
                second.setTitle("Final confirmation");
                second.setHeaderText("This cannot be undone.");
                second.setContentText("Type OK to confirm complete database reset.");
                second.showAndWait().ifPresent(btn2 -> {
                    if (btn2 == ButtonType.OK) {
                        settingsService.resetAll();
                        showSuccess("Database has been fully reset.");
                    }
                });
            }
        });
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