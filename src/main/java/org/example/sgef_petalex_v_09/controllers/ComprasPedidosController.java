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

public class ComprasPedidosController {

    @FXML private Button btnBack;
    @FXML private Button btnSubmoduloCompras;
    @FXML private Button btnSubmoduloPedidos;
    //@FXML private Button btnSubmoduloReportes;
    @FXML private AnchorPane contentPane;

    @FXML
    public void initialize() {
        cargarSeccion("Compras.fxml");
        resaltarBoton(btnSubmoduloCompras);
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Parent mainRoot = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            scene.setRoot(mainRoot);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
            );

            try {
                Thread.sleep(500);
                stage.setHeight(600);
                stage.setWidth(1000);
                System.out.println("Ajustando tamaño...");
                stage.setMaximized(true);
                stage.centerOnScreen();


            }
            catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            stage.setTitle("Index Blooms – Menú Principal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onSubmoduloCompras(ActionEvent event) {
        cargarSeccion("Compras.fxml");
        resaltarBoton(btnSubmoduloCompras);
    }

    @FXML
    private void onSubmoduloPedidos(ActionEvent event) {
        cargarSeccion("Pedidos.fxml");
        resaltarBoton(btnSubmoduloPedidos);
    }
    // Sección no utilizada actualmente
    /*
    @FXML
    private void onSubmoduloReportes(ActionEvent event) {
        cargarSeccion("Reportes.fxml");
        resaltarBoton(btnSubmoduloReportes);
    }
    */

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
        btnSubmoduloCompras.getStyleClass().remove("active");
        btnSubmoduloPedidos.getStyleClass().remove("active");
        activo.getStyleClass().add("active");
    }
}
