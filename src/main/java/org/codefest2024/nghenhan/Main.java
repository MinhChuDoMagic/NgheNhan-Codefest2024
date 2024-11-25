package org.codefest2024.nghenhan;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    @SuppressWarnings("unknown enum constant DeprecationLevel.ERROR")
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("socketlayout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 690, 650);
        stage.setTitle("Nghe Nhan - Codefest 2024");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}