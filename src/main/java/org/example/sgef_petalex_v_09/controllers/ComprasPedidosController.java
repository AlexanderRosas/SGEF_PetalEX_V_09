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
    @FXML private Button btnCompras;
    @FXML private Button btnPedidos;
    @FXML private AnchorPane contentPane;

    @FXML
    public void initialize() {
        cargarSeccion("GestionCompras.fxml");
        resaltarBoton(btnCompras);
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Parent mainRoot = FXMLLoader.load(
                    getClass().getResource("/fxml/MainMenu.fxml")
            );
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
            );
            stage.setMaximized(true);
            stage.setTitle("Index Blooms – Menú Principal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onCompras(ActionEvent event) {
        cargarSeccion("GestionCompras.fxml");
        resaltarBoton(btnCompras);
    }

    @FXML
    private void onPedidos(ActionEvent event) {
        cargarSeccion("GestionPedidos.fxml");
        resaltarBoton(btnPedidos);
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
        btnCompras.getStyleClass().remove("active");
        btnPedidos.getStyleClass().remove("active");

        if (!activo.getStyleClass().contains("active")) {
            activo.getStyleClass().add("active");
        }
    }
}
