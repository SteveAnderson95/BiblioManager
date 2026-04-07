package com.bibliomanager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        HBox hBox = new HBox();
        Scene scene = new Scene(hBox);
        stage.setScene(scene);
        stage.show();
    }
}
