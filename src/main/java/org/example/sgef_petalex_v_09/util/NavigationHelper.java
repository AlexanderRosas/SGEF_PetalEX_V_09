package org.example.sgef_petalex_v_09.util;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

public class NavigationHelper {


    // Carga genérica de vistas con título, manteniendo tamaño
    public static void cargarVista(javafx.event.ActionEvent event, String fxmlPath, String title) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            URL resource = NavigationHelper.class.getResource(fxmlPath);
            if (resource == null) {
                System.err.println("ERROR: FXML no encontrado en " + fxmlPath);
                return;
            }

            Parent rootNode = FXMLLoader.load(resource);
            Scene scene = new Scene(rootNode);
            scene.getStylesheets().add(NavigationHelper.class.getResource("/css/styles.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle(title);

            if (wasMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setWidth(width);
                stage.setHeight(height);
                stage.centerOnScreen();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void volverAlMenuPrincipal(javafx.event.ActionEvent event) {
        try {
            // 1) Cerrar la ventana actual
            Stage current = (Stage) ((Node) event.getSource()).getScene().getWindow();
            current.close();

            // 2) Cargar MainMenu.fxml
            URL menuUrl = NavigationHelper.class.getResource("/fxml/MainMenu.fxml");
            if (menuUrl == null) {
                System.err.println("ERROR: No se encontró MainMenu.fxml");
                return;
            }
            Parent menuRoot = FXMLLoader.load(menuUrl);

            // 3) Crear un nuevo Stage para el menú
            Stage menuStage = new Stage();
            Scene menuScene = new Scene(menuRoot);
            menuScene.getStylesheets().add(
                    NavigationHelper.class.getResource("/css/styles.css").toExternalForm()
            );
            menuStage.setScene(menuScene);
            menuStage.setTitle("Index Blooms – Menú Principal");
            //menuStage.setMaximized(true);
            menuStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
