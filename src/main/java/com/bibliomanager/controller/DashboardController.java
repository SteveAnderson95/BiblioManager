package com.bibliomanager.controller;

import com.bibliomanager.DatabaseHandler;
import com.bibliomanager.dao.GetData;
import com.bibliomanager.dao.ReturnBook;
import com.bibliomanager.dao.SavedBook;
import com.bibliomanager.dao.availableBook;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    // abt = Available Books Table
    @FXML
    private TableColumn<availableBook, String> abtBookAuthorCol;
    @FXML
    private TableColumn<availableBook, String> abtBookTitleCol;
    @FXML
    private TableColumn<availableBook, String> abtBookTypeCol;
    @FXML
    private TableColumn<availableBook, Date> abtPublishedDateCol;
    @FXML
    private ImageView availableBookPoster;
    @FXML
    private Label availableBookTitle;
    @FXML
    private Button availableBooksBtn;
    @FXML
    private AnchorPane availableBooksForm;
    @FXML
    private TableView<availableBook> availableBooksTable;
    @FXML
    private Circle circleImage;
    @FXML
    private Button editBtn;
    @FXML
    private Button issueBooksBtn;
    @FXML
    private Button returnBooksBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button savedBooksBtn;
    @FXML
    private Button signOutBtn;
    @FXML
    private Label studentNumberLabel;
    @FXML
    private Button takeBtn;
    @FXML
    private Button minimize;
    @FXML
    private Button close;
    @FXML
    private Button arrowBtn;
    @FXML
    private Button barsBtn;
    @FXML
    private AnchorPane navForm;
    @FXML
    private AnchorPane mainCenterForm;
    @FXML
    private Button halfNav_availableBtn;
    @FXML
    private AnchorPane halfNav_form;
    @FXML
    private Button halfNav_returnBtn;
    @FXML
    private Button halfNav_saveBtn;
    @FXML
    private Button halfNav_issueBtn;
    @FXML
    private Circle smallCircle_image;
    @FXML
    private AnchorPane availableBookForm;
    @FXML
    private AnchorPane issueForm;
    @FXML
    private AnchorPane savedBookForm;
    @FXML
    private AnchorPane returnBookForm;
    @FXML
    private Label currentFormLabel;
    @FXML
    private Label takeBookAuthor;
    @FXML
    private Label takeBookDate;
    @FXML
    private Label takeBookDetailsDate;
    @FXML
    private TextField takeBookDetailsTitle;
    @FXML
    private ImageView takeBookImage;
    @FXML
    private Label takeBookTitle;
    @FXML
    private Label takeBookType;
    @FXML
    private Button takeClearBtn;
    @FXML
    private TextField takeFirstName;
    @FXML
    private ComboBox<String> takeGender;
    @FXML
    private TextField takeLastName;
    @FXML
    private Label takeStudentNumber;
    @FXML
    private Button takeTakeBtn;

    @FXML
    private TableColumn<ReturnBook, String> returnAuthor;
    @FXML
    private TableColumn<ReturnBook, String> returnBookTitle;
    @FXML
    private TableColumn<ReturnBook, String> returnBookType;
    @FXML
    private ImageView returnImageView;
    @FXML
    private TableColumn<ReturnBook, java.util.Date> returnIssuedDate;
    @FXML
    private TableView<ReturnBook> returnTableView;
    @FXML
    private Button returnUnsaveButton;

    @FXML
    private TableColumn<SavedBook, String> saveColAuthor;
    @FXML
    private TableColumn<SavedBook, Date> saveColDate;
    @FXML
    private TableColumn<SavedBook, String> saveColTitle;
    @FXML
    private TableColumn<SavedBook, String> saveColType;
    @FXML
    private ImageView saveImageView;
    @FXML
    private TableView<SavedBook> saveTableView;
    @FXML
    private Button saveUnsaveBtn;


    private Image image;
    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;
    private String comboBox[] = {"Male", "Female"};

    public  void findBooks (ActionEvent e) {

        clearFindData();

        String sql = "SELECT * FROM book WHERE bookTitle = '" + takeBookDetailsTitle.getText() + "'";
//        connect = DatabaseHandler.connectDB();

        try (Connection connect = DatabaseHandler.connectDB();
            PreparedStatement prepare = connect.prepareStatement(sql);
            ResultSet result = prepare.executeQuery()){

            boolean check = false;
            Alert alert;

            if(takeBookDetailsTitle.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Please select the book");
                alert.showAndWait();
            } /*else if (takeFirstName.getText().isEmpty()
                    || takeLastName.getText().isEmpty()
                    || takeGender.getSelectionModel().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Please type student complete details");
                alert.showAndWait();
            }*/else {
                while(result.next()) {
                    takeBookTitle.setText(result.getString("bookTitle"));
                    takeBookAuthor.setText(result.getString("author"));
                    takeBookType.setText(result.getString("bookType"));
                    takeBookDate.setText(result.getString("date"));

                    GetData.path = result.getString("image");
                    String uri = "file:" + GetData.path;
                    image = new Image(uri, 127, 162, false, true);
                    takeBookImage.setImage(image);

                    check = true;
                }
                if (!check) {
                    takeBookTitle.setText("The book is not available !");
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void StudentNumberLabel () {
        takeStudentNumber.setText(GetData.studentNumber);
    }

    public void clearTakeData () {
        takeBookDetailsTitle.setText("");
        takeBookTitle.setText("");
        takeBookAuthor.setText("");
        takeBookType.setText("");
        takeBookDate.setText("");
        takeBookImage.setImage(null);
    }

    public void clearFindData () {
        takeBookTitle.setText("");
        takeBookAuthor.setText("");
        takeBookType.setText("");
        takeBookDate.setText("");
        takeBookImage.setImage(null);
    }

    public void gender () {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(Arrays.asList(comboBox));
        takeGender.setItems(list);

    }

    public void displayDate () {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(new java.util.Date());
        takeBookDetailsDate.setText(date);
    }

    public void takeBook () {

        java.util.Date date = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

        String sql = "INSERT INTO takenBooks (studentNumber, firstName, lastName, gender, bookTitle, " +
                "author, bookType, image, date, checkReturn)\n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connect = DatabaseHandler.connectDB();
            PreparedStatement prepare = connect.prepareStatement(sql)) {
            Alert alert;
            if (takeFirstName.getText().isEmpty()
                    || takeLastName.getText().isEmpty()
                    || takeGender.getSelectionModel().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Please type student complete details");
                alert.showAndWait();
            } else if (takeBookTitle.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Please indicate the book you want to take !");
                alert.showAndWait();
            } else {
//                prepare = connect.prepareStatement(sql);
                prepare.setString(1, takeStudentNumber.getText());
                prepare.setString(2, takeFirstName.getText());
                prepare.setString(3, takeLastName.getText());
                prepare.setString(4, (String) takeGender.getSelectionModel().getSelectedItem());
                prepare.setString(5, takeBookTitle.getText());
                prepare.setString(8, GetData.path);
                prepare.setDate(9, sqlDate);
                String check = "Not returned";
                prepare.setString(10, check);
                prepare.setString(6, takeBookAuthor.getText());
                prepare.setString(7, takeBookType.getText());

                prepare.executeUpdate();
                showReturnBooks();

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully take da book !");
                alert.showAndWait();
                clearTakeData();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // SAVED BOOKS
    public ObservableList<SavedBook> savedBooksData () {

        ObservableList<SavedBook> listSavedData = FXCollections.observableArrayList();

        String sql = "SELECT * FROM savedBooks WHERE studentNumber = '" + GetData.studentNumber + "'";

        try (
                Connection connect = DatabaseHandler.connectDB();
                PreparedStatement pstmt = connect.prepareStatement(sql)
                ) {

            SavedBook sBook;

            ResultSet result = pstmt.executeQuery();

            while (result.next()) {
                sBook = new SavedBook(result.getString("bookTitle"), result.getString("author"),
                        result.getString("bookType"), result.getDate("date"), result.getString("image"));
                listSavedData.add(sBook);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listSavedData;
    }

    private ObservableList<SavedBook> savedBooksList;

    public void showSavedBooks () {
        savedBooksList = savedBooksData();

        saveColTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        saveColAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        saveColType.setCellValueFactory(new PropertyValueFactory<>("genre"));
        saveColDate.setCellValueFactory(new PropertyValueFactory<>("image"));

        saveTableView.setItems(savedBooksList);
    }

    public void selectSavedBook () {
        SavedBook savedBook = saveTableView.getSelectionModel().getSelectedItem();
        int num = saveTableView.getSelectionModel().getFocusedIndex();

        if ((num - 1) < -1)
            return;
        String uri = "file:" + savedBook.getImage();
        image = new Image(uri, 126,149, false, true);
        saveImageView.setImage(image);

        GetData.savedTitle = savedBook.getTitle();
        GetData.savedImage = savedBook.getImage();
    }

    public void saveBooks () {

        String sql = "INSERT INTO savedBooks (bookTitle, author, bookType, image, date, studentNumber) VALUES (?,?,?,?,?,?)";

        
        try (
                Connection connect = DatabaseHandler.connectDB();
                PreparedStatement prepare = connect.prepareStatement(sql)
                ) {

            Alert alert;

            if (availableBookTitle.getText().isEmpty()) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Please select the book");
                alert.showAndWait();
            } else {

                prepare.setString(1, GetData.savedTitle);
                prepare.setString(2, GetData.savedAuthor);
                prepare.setString(3, GetData.savedType);
                prepare.setString(4, GetData.savedImage);
                prepare.setDate(5, GetData.savedDate);
                prepare.setString(6, GetData.studentNumber);

                prepare.executeUpdate();

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully saved");
                alert.showAndWait();

                saveColAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unsaveBooks () {

        String sql = "DELETE FROM savedBooks WHERE bookTitle = '" + GetData.savedTitle +
                "' and studentNumber = '" + GetData.studentNumber + "'";

        try (
               Connection connect = DatabaseHandler.connectDB();
               Statement stmt = connect.createStatement()
                ) {
            Alert alert;

            if (saveImageView.getImage() == null) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Please select the book you want to unsave 😂");
                alert.showAndWait();
            } else  {
                stmt.executeUpdate(sql);

                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully unsaved 😂");
                alert.showAndWait();

                showSavedBooks();
                saveImageView.setImage(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //RETURN FORM
    public ObservableList<ReturnBook> returnBookData () {

        ObservableList<ReturnBook>  listReturnBook = FXCollections.observableArrayList();

        String checkReturn = "Not returned";

        String sql = "SELECT * FROM takenBooks WHERE checkReturn = '" + checkReturn + "'" +
                "and studentNumber = '" + GetData.studentNumber + "'";

        Alert alert;

        try (Connection connect = DatabaseHandler.connectDB();
            PreparedStatement pstmt = connect.prepareStatement(sql);
            ResultSet result = pstmt.executeQuery()
        ) {
            ReturnBook returnBook;
            while (result.next()) {
                returnBook = new ReturnBook(result.getString("bookTitle"),
                        result.getString("author"),
                        result.getString("bookType"),
                        result.getDate("date"),
                        result.getString("image"));
                listReturnBook.add(returnBook);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return listReturnBook;
    }

    public void returnBooks () {

        String sql = "UPDATE takenBooks SET checkReturn = 'Returned' WHERE bookTitle = '" + GetData.takeBookTitle + "'";
        Alert alert;

        try (
                Connection connect = DatabaseHandler.connectDB();
                Statement stmt = connect.createStatement()
        ) {
            if (returnImageView.getImage() == null) {
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Please the book you want to return !");
                alert.showAndWait();
            } else {
                stmt.executeUpdate(sql);
                alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Admin Message");
                alert.setHeaderText(null);
                alert.setContentText("Successfully returned !");
                alert.showAndWait();
                showReturnBooks();
                returnImageView.setImage(null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ObservableList<ReturnBook> returnData;

    public void showReturnBooks() {

        returnData = returnBookData();

        returnBookTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        returnAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        returnBookType.setCellValueFactory(new PropertyValueFactory<>("genre"));
        returnIssuedDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        returnTableView.setItems(returnData);
    }

    public void selectReturnBook () {

        ReturnBook rbook = returnTableView.getSelectionModel().getSelectedItem();
        int num = returnTableView.getSelectionModel().getFocusedIndex();

        if ((num -1) < -1)
            return;

        String uri = "file:" + rbook.getImage();
        image = new Image(uri, 152, 170, false, true);
        returnImageView.setImage(image);
        GetData.takeBookTitle = rbook.getTitle();
    }

    public ObservableList<availableBook> dataList () {

        ObservableList<availableBook> booksList = FXCollections.observableArrayList();
        String sql = " SELECT * FROM book";
//        connect = DatabaseHandler.connectDB();

        try (Connection connect = DatabaseHandler.connectDB();
             PreparedStatement prepare = connect.prepareStatement(sql);
             ResultSet result = prepare.executeQuery()){

            availableBook aBooks;

            while (result.next()) {
                aBooks = new availableBook(
                        result.getString("bookTitle"),
                        result.getString("author"),
                        result.getString("bookType"),
                        result.getString("image"),
                        result.getDate("date")
                );
                booksList.add(aBooks);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return booksList;
    }

    public void selectAvailableBooks () {

        availableBook bookData = availableBooksTable.getSelectionModel().getSelectedItem();
        int num = availableBooksTable.getSelectionModel().getFocusedIndex();

        if ((num - 1) < -1)
            return;

        availableBookTitle.setText(bookData.getTitle());

        String uri = "file:" + bookData.getImage();
        image = new Image(uri, 134, 171, false, true);
        availableBookPoster.setImage(image);
        GetData.takeBookTitle = bookData.getTitle();

        GetData.savedTitle = bookData.getTitle();
        GetData.savedAuthor = bookData.getAuthor();
        GetData.savedType = bookData.getGenre();
        GetData.savedImage = bookData.getImage();
        GetData.savedDate = bookData.getPublishedDate();
    }

    public void takeAvailableBook (ActionEvent e) {

        if (e.getSource() == takeBtn) {
            issueForm.setVisible(true);
            availableBooksForm.setVisible(false);
            savedBookForm.setVisible(false);
            returnBookForm.setVisible(false);
        }
    }

    public void navButtonDesign (ActionEvent e) {

        if (e.getSource() == availableBooksBtn || e.getSource() == halfNav_availableBtn) {

            issueForm.setVisible(false);
            availableBooksForm.setVisible(true);
            savedBookForm.setVisible(false);
            returnBookForm.setVisible(false);

            currentFormLabel.setText("Available Books");

            availableBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #46589a, #4278a7);");
            issueBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            returnBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            savedBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");

            halfNav_availableBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #46589a, #4278a7);");
            halfNav_issueBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_returnBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_saveBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");

        } else if (e.getSource() == issueBooksBtn || e.getSource() == halfNav_issueBtn) {

            issueForm.setVisible(true);
            availableBooksForm.setVisible(false);
            savedBookForm.setVisible(false);
            returnBookForm.setVisible(false);

            currentFormLabel.setText("Issue Books");

            issueBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #46589a, #4278a7);");
            availableBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            returnBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            savedBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");

            halfNav_issueBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #46589a, #4278a7);");
            halfNav_availableBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_returnBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_saveBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");

        } else if (e.getSource() == returnBooksBtn || e.getSource() == halfNav_returnBtn) {

            issueForm.setVisible(false);
            availableBooksForm.setVisible(false);
            savedBookForm.setVisible(false);
            returnBookForm.setVisible(true);

            currentFormLabel.setText("Return Books");

            returnBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #46589a, #4278a7);");
            issueBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            availableBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            savedBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");

            halfNav_returnBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #46589a, #4278a7);");
            halfNav_issueBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_availableBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_saveBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");

            showReturnBooks();


        } else if (e.getSource() == savedBooksBtn || e.getSource() == halfNav_saveBtn) {

            issueForm.setVisible(false);
            availableBooksForm.setVisible(false);
            savedBookForm.setVisible(true);
            returnBookForm.setVisible(false);

            currentFormLabel.setText("Saved Books");

            savedBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #46589a, #4278a7);");
            issueBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            returnBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            availableBooksBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");

            halfNav_saveBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #46589a, #4278a7);");
            halfNav_issueBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_returnBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");
            halfNav_availableBtn.setStyle("-fx-background-color: linear-gradient(to bottom right, #344275, #3a6389);");

            showSavedBooks();

        }
    }

    public void setStudentNumberLabel () {
        studentNumberLabel.setText(GetData.studentNumber);
    }

    public void insertImage () {

        FileChooser open = new FileChooser();
        open.setTitle("Image File");
        open.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image File", "*png", "*jpg", "*jpeg"));
        Stage stage = (Stage) navForm.getScene().getWindow();
        File file = open.showOpenDialog(stage);

        if (file != null) {
            image = new Image(file.toURI().toString(), 100, 63, false, true);
            circleImage.setFill(new ImagePattern(image));
            smallCircle_image.setFill(new ImagePattern(image));

            GetData.path = file.getAbsolutePath();
            changeProfile();
        }

    }

    public void changeProfile () {

        String uri = GetData.path;
        uri = uri.replace("//", "////");

        String sql = "UPDATE students SET image = '" + uri + "' WHERE studentNumber = '" + GetData.studentNumber + "'";

        try (
                Connection connect = DatabaseHandler.connectDB();
                Statement stmt = connect.createStatement()
                ) {

            stmt.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showProfile () {
        if (GetData.path == null || GetData.path.isBlank() || "null".equalsIgnoreCase(GetData.path)) return;
        String uri = "file:" + GetData.path;
        image = new Image(uri, 100, 63, false, true);
        circleImage.setFill(new ImagePattern(image));
        smallCircle_image.setFill(new ImagePattern(image));
    }

    public void designInsertImage () {
        editBtn.setVisible(false);

        circleImage.setOnMouseEntered((MouseEvent e) -> {
            editBtn.setVisible(true);
        });
        circleImage.setOnMouseExited((MouseEvent e) -> {
            editBtn.setVisible(false);
        });
        editBtn.setOnMouseEntered((MouseEvent e) -> {
            editBtn.setVisible(true);
        });
        editBtn.setOnMouseExited((MouseEvent e) -> {
            editBtn.setVisible(false);
        });
        editBtn.setOnMousePressed((MouseEvent e) -> {
            editBtn.setVisible(true);
        });
    }

    private ObservableList<availableBook> booksList;
    public void showAvailableBooks () {

        booksList = dataList();

        abtBookTitleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        abtBookAuthorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        abtBookTypeCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        abtPublishedDateCol.setCellValueFactory(new PropertyValueFactory<>("publishedDate"));

        availableBooksTable.setItems(booksList);
    }

    private double x = 0;
    private double y = 0;


    public void sliderArrow() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(.5));
        slide.setNode(navForm);
        slide.setToX(-200);

        TranslateTransition slide1 = new TranslateTransition();
        slide1.setDuration(Duration.seconds(.5));
        slide1.setNode(mainCenterForm);
//        mainCenterForm.setMinWidth(786 + 200);
        slide1.setToX(-200 + 90);

        TranslateTransition slide2 = new TranslateTransition();
        slide2.setDuration(Duration.seconds(.5));
        slide2.setNode(halfNav_form);
        slide2.setToX(0);

        slide.setOnFinished((ActionEvent e) -> {
            arrowBtn.setVisible(false);
            barsBtn.setVisible(true);
        });
        slide2.play();
        slide1.play();
        slide.play();
    }

    public void sliderBars() {
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(.5));
        slide.setNode(navForm);
        slide.setToX(0);

        TranslateTransition slide1 = new TranslateTransition();
        slide1.setDuration(Duration.seconds(.5));
        slide1.setNode(mainCenterForm);
        slide1.setToX(0);

        TranslateTransition slide2 = new TranslateTransition();
        slide2.setDuration(Duration.seconds(.5));
        slide2.setNode(halfNav_form);
        slide2.setToX(72);

        slide.setOnFinished((ActionEvent e) -> {
            arrowBtn.setVisible(true);
            barsBtn.setVisible(false);
        });
        slide2.play();
        slide1.play();
        slide.play();
    }

    @FXML
    public void close() {
        System.exit(0);
    }

    @FXML
    public void minimize () {
        Stage stage = (Stage) minimize.getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    public void signOut (ActionEvent e) {
        try {
            if (e.getSource() == signOutBtn) {
                // TO SWAP FROM DASHBOARD TO LOGIN FORM
                Parent root = FXMLLoader.load(getClass().getResource("/com/bibliomanager/fxml/login-view.fxml"));
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();

                root.setOnMousePressed((MouseEvent event) -> {
                    x = event.getSceneX();
                    y = event.getSceneY();
                });
                root.setOnMouseDragged((MouseEvent event)-> {
                    stage.setX(event.getScreenX() - x);
                    stage.setY(event.getScreenY() - y);
                });
                signOutBtn.getScene().getWindow().hide();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showProfile();
        designInsertImage();
        showAvailableBooks();
        setStudentNumberLabel();
        displayDate();
        StudentNumberLabel();
        gender();
        showReturnBooks();
        showSavedBooks();
    }
}
