package com.bibliomanager.controller;

import com.bibliomanager.DatabaseHandler;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.sql.*;
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
    private AnchorPane availableBooksFont;
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
    private Button halfNav_takeBtn;
    @FXML
    private Circle smallCircle_image;

    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;

    public ObservableList<availableBook> dataList () {

        ObservableList<availableBook> booksList = FXCollections.observableArrayList();
        String sql = " SELECT * FROM book";
        connect = DatabaseHandler.connectDB();

        try {

            availableBook aBooks;
            prepare = connect.prepareStatement(sql);
            result = prepare.executeQuery();

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
    }
}
