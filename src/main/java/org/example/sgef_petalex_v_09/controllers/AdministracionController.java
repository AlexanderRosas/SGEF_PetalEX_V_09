package org.example.sgef_petalex_v_09.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdministracionController {

    @FXML private Button btnBack;
    @FXML private Button btnGestionUsuarios;
    @FXML private Button btnParametrosNegocio; // NUEVO
    @FXML private AnchorPane contentPane;

    @FXML
    public void initialize() {
        cargarSeccion("GestionUsuarios.fxml");
        resaltarBoton(btnGestionUsuarios);
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            boolean wasMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainMenu.fxml"));
            AnchorPane mainRoot = loader.load();

            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);

            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            stage.setResizable(false);
            if (wasMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setMaximized(false);
                stage.setWidth(width);
                stage.setHeight(height);
                stage.centerOnScreen();
            }

            stage.setTitle("Index Blooms – Menú Principal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onGestionUsuarios(ActionEvent event) {
        cargarSeccion("GestionUsuarios.fxml");
        resaltarBoton(btnGestionUsuarios);
    }

    @FXML
    private void onParametrosNegocio(ActionEvent event) { // NUEVO
        cargarSeccion("ParametrosNegocio.fxml");
        resaltarBoton(btnParametrosNegocio);
    }

    private void cargarSeccion(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            AnchorPane pane = loader.load();
            contentPane.getChildren().setAll(pane);

            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resaltarBoton(Button activo) {
        // Remueve "active" de todos los botones
        btnGestionUsuarios.getStyleClass().remove("active");
        btnParametrosNegocio.getStyleClass().remove("active"); // NUEVO

        // Agrega "active" al botón seleccionado
        if (activo != null && !activo.getStyleClass().contains("active")) {
            activo.getStyleClass().add("active");
        }
    }
}