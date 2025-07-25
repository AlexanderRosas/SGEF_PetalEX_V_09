package org.example.sgef_petalex_v_09.util;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Helper para navegación entre vistas en JavaFX.
 */
public class NavigationHelper {

    /**
     * Carga una vista FXML en la misma ventana, manteniendo tamaño y estado maximizado.
     * @param event   evento de acción que dispara la navegación
     * @param fxmlPath ruta al recurso FXML (por ejemplo "/fxml/Clientes.fxml")
     * @param title   título de la ventana
     */
    public static void cargarVista(ActionEvent event, String fxmlPath, String title) {
        try {
            URL resource = NavigationHelper.class.getResource(fxmlPath);
            if (resource == null) {
                System.err.println("ERROR: FXML no encontrado en " + fxmlPath);
                return;
            }
            Parent root = FXMLLoader.load(resource);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    NavigationHelper.class.getResource("/css/styles.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setTitle(title);

            Platform.runLater(() -> {
                if (wasMaximized) {
                    stage.setMaximized(true);
                } else {
                    stage.setWidth(width);
                    stage.setHeight(height);
                    stage.centerOnScreen();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vuelve al menú principal en la misma ventana.
     */
    public static void volverAlMenuPrincipal(ActionEvent event) {
        cargarVista(event, "/fxml/MainMenu.fxml", "Index Blooms – Menú Principal");
    }

    /**
     * Reinicia la aplicación abriendo un nuevo Stage con el menú principal, cerrando el actual.
     */
    public static void restartAtMenu(ActionEvent event) {
        try {
            // Cerrar ventana actual
            Stage current = (Stage) ((Node) event.getSource()).getScene().getWindow();
            current.close();

            // Cargar MainMenu.fxml en un nuevo Stage
            URL resource = NavigationHelper.class.getResource("/fxml/MainMenu.fxml");
            if (resource == null) {
                System.err.println("ERROR: No se encontró MainMenu.fxml");
                return;
            }
            Parent root = FXMLLoader.load(resource);

            Stage menuStage = new Stage();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    NavigationHelper.class.getResource("/css/styles.css").toExternalForm()
            );
            menuStage.setScene(scene);
            menuStage.setTitle("Index Blooms – Menú Principal");
            menuStage.setMaximized(true);
            menuStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
