package org.example.sgef_petalex_v_09.controllers;

import javafx.application.Platform;
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

public class MainMenuController {
    @FXML private AnchorPane root;
    @FXML private Button btnClientes;
    @FXML private Button btnVentas;
    @FXML private Button btnCompras;
    @FXML private Button btnProveedores;
    @FXML private Button btnSistema;

    @FXML
    private Node anyNodeInsideRoot;

    @FXML
    private void onClientes(ActionEvent event) {
        // TODO: navegar a Clientes.fxml
    }

    @FXML
    private void onVentas(ActionEvent event) {
        // TODO: navegar a Ventas.fxml
    }

    @FXML
    private void onCompras(ActionEvent event) {
        // TODO: navegar a ComprasPedidos.fxml
    }

    @FXML
    private void onProveedores(ActionEvent event) {
        // TODO: navegar a Proveedores.fxml
    }


    @FXML
    private void onSistema(ActionEvent event) {
        try {
            // Obtiene la ventana actual y guarda sus dimensiones
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            double width = stage.getWidth();
            double height = stage.getHeight();
            boolean wasMaximized = stage.isMaximized();

            // Carga la vista de Administración
            Parent adminRoot = FXMLLoader.load(
                    getClass().getResource("/fxml/Administracion.fxml")
            );

            // Crea una nueva escena (y reaplica el CSS)
            Scene scene = new Scene(adminRoot);
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
            );

            // Ajusta la escena y el título
            stage.setScene(scene);
            stage.setTitle("Index Blooms – Administración del Sistema");

            // Restaura las dimensiones originales
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

}