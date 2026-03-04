module com.bibliomanager {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.bibliomanager to javafx.fxml;
    exports com.bibliomanager;
}