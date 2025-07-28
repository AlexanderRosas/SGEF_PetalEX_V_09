package org.example.sgef_petalex_v_09;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Index Blooms – Login");
        stage.setMaximized(false); // Login NO maximizado
        stage.setResizable(false); // No redimensionable
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}