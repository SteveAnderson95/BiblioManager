package com.bibliomanager.controller;

import com.bibliomanager.model.Book;
import com.bibliomanager.model.Category;
import com.bibliomanager.service.BookService;
import com.bibliomanager.service.CategoryService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class BookController {

    @FXML
    private TableView<Book> booksTable;
    @FXML
    private ComboBox<String> categoryFilter;
    @FXML
    private TableColumn<Book, String> colAuthor;
    @FXML
    private TableColumn<Book, Integer> colAvailable;
    @FXML
    private TableColumn<Book, String> colCategory;
    @FXML
    private TableColumn<Book, String> colStatus;
    @FXML
    private TableColumn<Book, String > colTitle;
    @FXML
    private TableColumn<Book, Integer> colTotal;
    @FXML
    private Label detailAuthor;
    @FXML
    private Label detailAvailable;
    @FXML
    private Label detailCategory;
    @FXML
    private Label posterPlaceholderLabel;
    @FXML
    private Label detailIsbn;
    @FXML
    private Label detailOnLoan;
    @FXML
    private VBox detailPanel;
    @FXML
    private Label detailTitle;
    @FXML
    private Label detailTotal;
    @FXML
    private Label detailDescription;
    @FXML
    private ImageView posterImageView;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> statusFilter;
    @FXML
    private Label totalBadge;

    private final BookService bookService = new BookService();
    private final CategoryService categoryService = new CategoryService();
    private Book selectedBook;

    @FXML
    public void initialize() {
        setupColumns();
        setupFilters();
        setupSelectionListener();
        loadBooks();
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
    }

    private void setupColumns() {
        colTitle.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTitle()));
        colAuthor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getAuthor()));
        colCategory.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCategory().getName()));
        colTotal.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getTotalQuantity()));
        colAvailable.setCellValueFactory(d ->
                new SimpleObjectProperty<>(d.getValue().getAvailableQuantity()));

        colStatus.setCellValueFactory(d ->
                new SimpleStringProperty(getStatus(d.getValue())));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) { setText(null); setStyle(""); return; }
                setText(status);
                setAlignment(javafx.geometry.Pos.CENTER);
                setStyle("-fx-font-weight: bold; -fx-font-size: 12px;" + switch (status) {
                    case "Available" -> "-fx-text-fill: #1a7a3c;";
                    case "Low stock" -> "-fx-text-fill: #9a5c00;";
                    case "Out of stock" -> "-fx-text-fill: #b71c1c;";
                    default -> "";
                });
            }
        });
    }

    private void setupFilters() {
        //categories
        List<String> categoriesNames = categoryService.getAllCategories()
                .stream()
                .map(Category::getName)
                .toList();
        categoryFilter.setItems(FXCollections.observableArrayList(categoriesNames));

        //status
        statusFilter.setItems(FXCollections.observableArrayList("Available", "Low stock", "Out of stock"));

        //Listeners to restart search with every change
        searchField.textProperty().addListener((o, old, val) -> applyFilters());
        categoryFilter.valueProperty().addListener((o, old, val) -> applyFilters());
        statusFilter.valueProperty().addListener((o, old, val) -> applyFilters());
    }

    private void setupSelectionListener() {
        booksTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, book) -> {
                    if (book != null) showDetailPanel(book);
                });
    }

    private void loadBooks() {
        List<Book> books = bookService.getAllBooks();
        booksTable.setItems(FXCollections.observableArrayList(books));
        totalBadge.setText(books.size() + " total");
    }

    private void applyFilters() {
        String query = searchField.getText();
        String catName = categoryFilter.getValue();
        String status = statusFilter.getValue();

        Long catId = catName == null ? null :
                categoryService.getAllCategories()
                        .stream()
                        .filter(c -> c.getName().equals(catName))
                        .map(Category::getId)
                        .findFirst().orElse(null);

        List<Book> results = bookService.searchBooks(query, catId, status);
        booksTable.setItems(FXCollections.observableArrayList(results));
    }

    private void showDetailPanel(Book book) {
        selectedBook = book;
        detailTitle.setText(book.getTitle());
        detailAuthor.setText(book.getAuthor());
        detailIsbn.setText(book.getIsbn());
        detailCategory.setText(book.getCategory().getName());
        detailDescription.setText(book.getCategory().getDescription());
        detailTotal.setText(String.valueOf(book.getTotalQuantity()));
        detailAvailable.setText(String.valueOf(book.getAvailableQuantity()));
        detailOnLoan.setText(String.valueOf(
                book.getTotalQuantity() - book.getAvailableQuantity()));

        if (book.getCoverImage() != null && !book.getCoverImage().isBlank()) {
            try {
                posterImageView.setImage(new Image(
                        "file:" + book.getCoverImage(), true));
                posterPlaceholderLabel.setText("");

            } catch (Exception e) {
                posterImageView.setImage(null);
            }
        } else {
            posterImageView.setImage(null);
        }

        detailPanel.setVisible(true);
        detailPanel.setManaged(true);
    }

    @FXML
    private void closeDetailPanel() {
        detailPanel.setVisible(false);
        detailPanel.setManaged(false);
        booksTable.getSelectionModel().clearSelection();
        selectedBook = null;
    }

    @FXML
    private void handleAdd() { openDialog(null); }

    @FXML
    private void handleEdit() {
        if (selectedBook != null) openDialog(selectedBook);
    }

    @FXML
    private void handleDelete() {
        if (selectedBook == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete book");
        confirm.setHeaderText("Delete \"" + selectedBook.getTitle() + "\" ?");
        confirm.setContentText("This action cannot be undone.");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    bookService.deleteBook(selectedBook.getId());
                    closeDetailPanel();
                    loadBooks();
                } catch (RuntimeException e) {
                    showError(e.getMessage());
                }
            }
        });
    }

    private void openDialog(Book book) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/bibliomanager/fxml/book-dialog-view.fxml"));
            Parent root = loader.load();

            BookDialogController ctrl = loader.getController();
            ctrl.setBook(book);

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(booksTable.getScene().getWindow());
            dialog.setTitle(book == null ? "Add book" : "Edit book");
            dialog.setScene(new Scene(root));
            dialog.setResizable(false);
            dialog.showAndWait();

            loadBooks();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getStatus(Book b) {
        if (b.getAvailableQuantity() == 0) return "Out of stock";
        if (b.getAvailableQuantity() < b.getTotalQuantity() / 2.0) return "Low stock";
        return "Available";
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
