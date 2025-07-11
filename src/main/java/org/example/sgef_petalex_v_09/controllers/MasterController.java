package org.example.sgef_petalex_v_09.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class MasterController {
    @FXML
    private AnchorPane contentPane;
    @FXML private Button btnBack;

    @FXML
    private void onBack() {
        // lógica de volver al menú principal...
    }

    /** Carga y muestra un fxml dentro del contentPane */
    public void loadModule(String fxmlPath) throws IOException {
        AnchorPane module = FXMLLoader.load(getClass().getResource(fxmlPath));
        contentPane.getChildren().setAll(module);
        AnchorPane.setTopAnchor(module, 0.0);
        AnchorPane.setRightAnchor(module, 0.0);
        AnchorPane.setBottomAnchor(module, 0.0);
        AnchorPane.setLeftAnchor(module, 0.0);
    }
}