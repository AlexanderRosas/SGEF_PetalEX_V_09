package org.example.sgef_petalex_v_09.util;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public class DialogHelper {

    public static void showSuccess(Window owner, String action) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(owner);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText("Se ha " + action + " correctamente.");
        alert.showAndWait();
    }

    public static void showError(Window owner, String action) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(owner);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText("No se ha podido " + action + ".");
        alert.showAndWait();
    }

    public static void showWarning(Window owner, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(owner);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean confirm(Window owner, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(owner);
        alert.setTitle("Confirmación");
        return true;
    };
}
