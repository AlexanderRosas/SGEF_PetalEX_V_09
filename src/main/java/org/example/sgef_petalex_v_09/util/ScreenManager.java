package org.example.sgef_petalex_v_09.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Node;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ScreenManager {
    private static final Map<String, Scene> scenes = new HashMap<>();
    private static Stage mainStage;

    public static void initialize(Stage stage) {
        mainStage = stage;
    }

    public static void loadScreen(ActionEvent event, String name, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scenes.put(name, scene);
        } catch (IOException e) {
            throw new RuntimeException("Error cargando pantalla: " + name, e);
        }
    }

    public static void switchScreen(String name) {
        Scene scene = scenes.get(name);
        if (scene == null) {
            throw new IllegalArgumentException("Pantalla no encontrada: " + name);
        }
        mainStage.setScene(scene);
    }

    public static void switchScreen(ActionEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = getStageFromEvent(event);
            stage.setTitle(title);
            stage.setScene(scene);
        } catch (IOException e) {
            throw new RuntimeException("Error cambiando a pantalla: " + fxmlPath, e);
        }
    }

    public static Window getCurrentWindow(ActionEvent event) {
        return ((Node) event.getSource()).getScene().getWindow();
    }

    private static Stage getStageFromEvent(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void closeCurrentScreen(ActionEvent event) {
        getStageFromEvent(event).close();
    }

    public static <T> T loadFXML(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(ScreenManager.class.getResource(fxmlPath));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            throw new RuntimeException("Error cargando FXML: " + fxmlPath, e);
        }
    }

    public static void showError(String message) {
        // Aquí puedes implementar la lógica para mostrar errores
        System.err.println("Error: " + message);
    }

    public static void clearCache() {
        scenes.clear();
    }
}