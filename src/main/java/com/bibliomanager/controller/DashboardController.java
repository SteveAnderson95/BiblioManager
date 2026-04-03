package com.bibliomanager.controller;

import com.bibliomanager.DatabaseHandler;
import com.bibliomanager.dao.GetData;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

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

        String sql = "INSERT INTO takenBooks VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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
                prepare.setString(6, GetData.path);
                prepare.setDate(7, sqlDate);
                String check = "Not returned";
                prepare.setString(8, check);

                prepare.executeUpdate();

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

        }
    }

    public void setStudentNumberLabel () {
        studentNumberLabel.setText(GetData.studentNumber);
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
        showAvailableBooks();
        setStudentNumberLabel();
        displayDate();
        StudentNumberLabel();
        gender();
    }
}
