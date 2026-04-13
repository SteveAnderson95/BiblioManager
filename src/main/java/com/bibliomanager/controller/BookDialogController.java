package com.bibliomanager.controller;

import com.bibliomanager.model.Book;
import com.bibliomanager.model.Category;
import com.bibliomanager.service.BookService;
import com.bibliomanager.service.CategoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class BookDialogController {

    @FXML private Label dialogTitle;
    @FXML private TextField titleField, authorField, isbnField;
    @FXML private TextField totalField, availableField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private ImageView coverPreview;
    @FXML private VBox coverPlaceholder;
    @FXML private Label errorLabel;

    private final BookService bookService = new BookService();
    private final CategoryService categoryService = new CategoryService();
    private Book editingBook = null;
    private String coverImagePath = null;

    @FXML
    public void initialize() {
        List<Category> categories = categoryService.getAllCategories();
        categoryCombo.setItems(FXCollections.observableArrayList(categories));

        //display the category name in the combo
        categoryCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Category c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getName());
            }
        });
        categoryCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Category c, boolean empty) {
                super.updateItem(c, empty);
                setText(empty || c == null ? null : c.getName());
            }
        });
    }

    public void setBook(Book book) {
        this.editingBook = book;
        if (book == null) {
            dialogTitle.setText("Add book");
            return;
        }
        // book editing
        dialogTitle.setText("Edit book");
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        isbnField.setText(book.getIsbn());
        totalField.setText(String.valueOf(book.getTotalQuantity()));
        availableField.setText(String.valueOf(book.getAvailableQuantity()));

        categoryCombo.getItems().stream()
                .filter(c -> c.getId() == book.getCategory().getId())
                .findFirst()
                .ifPresent(categoryCombo::setValue);

        if (book.getCoverImage() != null && !book.getCoverImage().isBlank()) {
            coverImagePath = book.getCoverImage();
            coverPreview.setImage(new Image("file:" + coverImagePath, true));
            coverPlaceholder.setVisible(false);
            coverPlaceholder.setManaged(false);
        }
    }

    @FXML
    private void handleChooseCover() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Choose cover image");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File file = fc.showOpenDialog(titleField.getScene().getWindow());
        if (file == null) return;

        // size checking max 2MB
        if (file.length() > 2 * 1024 * 1024) {
            showError("Image exceeds 2MB limit.");
            return;
        }

        try {
            // Copy into covers/ folder
            Path coversDir = Paths.get("covers");
            Files.createDirectories(coversDir);
            Path dest = coversDir.resolve(file.getName());
            Files.copy(file.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            coverImagePath = dest.toString();

            coverPreview.setImage(new Image(dest.toUri().toString(), true));
            coverPlaceholder.setVisible(false);
            coverPlaceholder.setManaged(false);
        } catch (IOException e) {
            showError("Could not copy image.");
        }
    }

    @FXML
    private void handleSave() {
        errorLabel.setText("");
        try {
            String title  = titleField.getText().trim();
            String author = authorField.getText().trim();
            String isbn   = isbnField.getText().trim();
            Category cat  = categoryCombo.getValue();
            int total     = Integer.parseInt(totalField.getText().trim());
            int available = Integer.parseInt(availableField.getText().trim());

            Book book = editingBook != null
                    ? new Book(editingBook.getId(), title, author, isbn,
                    cat, coverImagePath, total, available)
                    : new Book(title, author, isbn,
                    cat, coverImagePath, total, available);

            if (editingBook != null) bookService.updateBook(book);
            else bookService.addBook(book);

            closeDialog();

        } catch (NumberFormatException e) {
            showError("Total and Available must be numbers.");
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleClose() { closeDialog(); }

    private void closeDialog() {
        ((Stage) titleField.getScene().getWindow()).close();
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setStyle("-fx-text-fill: #e53935; -fx-font-size: 12px;");
    }
}