package org.example.sgef_petalex_v_09.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdministracionController {

    @FXML
    private Button btnBack;
    @FXML
    private Button btnGestionUsuarios;
    @FXML
    private Button btnRolesPermisos;
    @FXML
    private AnchorPane contentPane;

    @FXML
    public void initialize() {
        cargarSeccion("GestionUsuarios.fxml");
        resaltarBoton(btnGestionUsuarios);
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            // Carga el FXML del menú principal
            Parent mainRoot = FXMLLoader.load(
                    getClass().getResource("/fxml/MainMenu.fxml"));
            // Obtiene el Stage y reutiliza la Scene actual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            // Reemplaza la raíz sin crear nueva Scene
            scene.setRoot(mainRoot);

            // Reaplica tu CSS
            scene.getStylesheets().clear();
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm());

            // Asegura que siga maximizado
            //stage.setMaximized(true);

            // Actualiza el título
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
    private void onRolesPermisos(ActionEvent event) {
        cargarSeccion("RolesPermisos.fxml");
        resaltarBoton(btnRolesPermisos);
    }

    private void cargarSeccion(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFile));
            AnchorPane pane = loader.load();
            contentPane.getChildren().setAll(pane);

            // Ajustar el panel cargado para que ocupe todo el espacio disponible
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resaltarBoton(Button activo) {
        // Remover la clase active de todos los botones
        btnGestionUsuarios.getStyleClass().remove("active");
        btnRolesPermisos.getStyleClass().remove("active");

        // Agregar la clase active al botón seleccionado
        activo.getStyleClass().add("active");
    }
}