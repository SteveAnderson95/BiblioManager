module com.bibliomanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.bibliomanager to javafx.fxml;
    exports com.bibliomanager;
}