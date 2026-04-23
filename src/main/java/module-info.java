module com.bibliomanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io;
    requires kernel;
    requires layout;

    exports com.bibliomanager;
    opens com.bibliomanager to javafx.fxml;
    exports com.bibliomanager.model;
    opens com.bibliomanager.model to javafx.fxml;
    opens com.bibliomanager.controller to javafx.fxml;
}
