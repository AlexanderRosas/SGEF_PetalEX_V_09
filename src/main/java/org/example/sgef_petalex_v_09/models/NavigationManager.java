package org.example.sgef_petalex_v_09.models;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationManager {

    public static void showMainMenu(Scene currentScene) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource("/fxml/MainMenu.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) currentScene.getWindow();
        stage.setScene(scene);
        stage.setTitle("SGEF - Sistema de Gestión de Exportación de Flores");
        stage.show();
    }

    public static void showProveedores(Scene currentScene) throws IOException {
        FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource("/fxml/Proveedores.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) currentScene.getWindow();
        stage.setScene(scene);
        stage.setTitle("SGEF - Gestión de Proveedores y Productos");
        stage.show();
    }
}