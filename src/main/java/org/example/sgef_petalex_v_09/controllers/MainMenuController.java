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
import java.net.URL;

public class MainMenuController {

    @FXML private AnchorPane root;
    @FXML private Button btnClientes;
    @FXML private Button btnVentas;
    @FXML private Button btnCompras;
    @FXML private Button btnProveedores;
    @FXML private Button btnSistema;

    @FXML
    private void onClientes(ActionEvent event) {
        cargarVista(event, "/fxml/Clientes.fxml",       "Index Blooms – Clientes");
    }

    @FXML
    private void onVentas(ActionEvent event) {
        cargarVista(event, "/fxml/Ventas.fxml",         "Index Blooms – Ventas");
    }

    @FXML
    private void onCompras(ActionEvent event) {
        cargarVista(event, "/fxml/ComprasPedidos.fxml", "Index Blooms – Compras y Pedidos");
    }

    @FXML
    private void onProveedores(ActionEvent event) {
        cargarVista(event, "/fxml/Proveedores.fxml",    "Index Blooms – Proveedores");
    }

    @FXML
    private void onSistema(ActionEvent event) {
        cargarVista(event, "/fxml/Administracion.fxml","Index Blooms – Administración del Sistema");
    }

    /**
     * Carga un FXML en una nueva Scene para el Stage actual,
     * reaplica CSS y restaura el tamaño o maximizado.
     *
     * @param event     el evento de clic
     * @param fxmlPath  ruta al recurso FXML (empieza con '/')
     * @param title     título de la ventana
     */
    private void cargarVista(ActionEvent event, String fxmlPath, String title) {
        // 1) Obtengo el Stage actual y guardo su estado
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        double prevW = stage.getWidth();
        double prevH = stage.getHeight();
        boolean wasMaximized = stage.isMaximized();

        try {
            // 2) Cargo el FXML con FXMLLoader para poder obtener el Controller si quisiera
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // 3) Preparo la nueva Scene y aplico el CSS
            Scene scene = new Scene(root);
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
            );

            // 4) Cambio la escena y el título
            stage.setScene(scene);
            stage.setTitle(title);

            // 5) Restauro maximizado o tamaño anterior
            if (wasMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setWidth(prevW);
                stage.setHeight(prevH);
                stage.centerOnScreen();
            }

        } catch (IOException e) {
            System.err.println("No se pudo cargar FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

}
