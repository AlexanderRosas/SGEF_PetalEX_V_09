package org.example.sgef_petalex_v_09.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class RoseSelectionController implements Initializable {

    @FXML private GridPane gridRoses;
    @FXML private Button btnOk;
    @FXML private Button btnCancel;

    private String selectedRose = null;

    private final List<String> roseNames = Arrays.asList(
            "FREEDOM", "EXPLORER", "MONDIAL", "PINKMONDIAL",
            "GOTCHA", "QUEEN_SAND", "NINA", "PLAYA_BLANCA",
            "MOMENTUM", "PINK_FLOYD", "VENDELA", "HERMOSA"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int cols = 4, row = 0, col = 0;
        for (String name : roseNames) {
            ImageView iv = new ImageView(new Image(
                    getClass().getResourceAsStream("/images/" + name + "_FOTO.PNG")
            ));
            iv.setFitWidth(100);
            iv.setPreserveRatio(true);

            Label lbl = new Label(name.replace('_', ' '));
            lbl.setStyle("-fx-font-size:12px;-fx-text-fill:#333;");

            VBox cell = new VBox(5, iv, lbl);
            cell.setStyle("-fx-alignment:center;-fx-cursor:hand;-fx-padding:5;");
            cell.setOnMouseClicked(evt -> selectRose(cell, name));

            gridRoses.add(cell, col, row);
            if (++col >= cols) { col = 0; row++; }
        }

        btnOk.setDisable(true);
    }

    private void selectRose(VBox cell, String name) {
        gridRoses.getChildren().forEach(n -> n.setEffect(null));
        cell.setEffect(new DropShadow(10, Color.GREEN));
        selectedRose = name;
        btnOk.setDisable(false);
    }

    @FXML
    private void onAccept() {
        if (selectedRose == null) return;
        Stage stage = (Stage) btnOk.getScene().getWindow();
        boolean ok = DialogHelper.confirm(
                stage,
                "¿Está seguro que desea agregar la variedad " + selectedRose + "?"
        );
        if (ok) {
            stage.close();
        }
    }

    @FXML
    private void onCancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    public String getSelectedRose() {
        return selectedRose;
    }
}
