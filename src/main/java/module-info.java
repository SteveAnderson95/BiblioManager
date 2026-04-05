module com.bibliomanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.bibliomanager to javafx.fxml;
    exports com.bibliomanager;
    exports com.bibliomanager.controller;
    opens com.bibliomanager.controller to javafx.fxml, java.base;
    exports com.bibliomanager.dao;
    opens com.bibliomanager.dao to java.base, javafx.base, javafx.fxml;
}