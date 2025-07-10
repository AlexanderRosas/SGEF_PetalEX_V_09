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

    @FXML private Button btnBack;
    @FXML private Button btnGestionUsuarios;
    @FXML private Button btnRolesPermisos;
    @FXML private Button btnParametrosNegocio;
    @FXML private AnchorPane contentPane;

    @FXML
    public void initialize() {
        //cargarSeccion("GestionUsuarios.fxml");
        resaltarBoton(btnGestionUsuarios);
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Parent mainRoot = FXMLLoader.load(
                    getClass().getResource("/fxml/MainMenu.fxml")
            );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(mainRoot);
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
            );
            stage.setScene(scene);
            stage.setTitle("Index Blooms – Menú Principal");
            stage.centerOnScreen();
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
        //cargarSeccion("RolesPermisos.fxml");
        resaltarBoton(btnRolesPermisos);
    }

    @FXML
    private void onParametrosNegocio(ActionEvent event) {
        //cargarSeccion("ParametrosNegocio.fxml");
        resaltarBoton(btnParametrosNegocio);
    }

    private void cargarSeccion(String fxmlFile) {
        try {
            AnchorPane pane = FXMLLoader.load(
                    getClass().getResource("/fxml/" + fxmlFile)
            );
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
        btnGestionUsuarios.getStyleClass().remove("active");
        btnRolesPermisos.getStyleClass().remove("active");
        btnParametrosNegocio.getStyleClass().remove("active");
        activo.getStyleClass().add("active");
    }
}