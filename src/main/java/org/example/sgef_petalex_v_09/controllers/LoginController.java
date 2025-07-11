package org.example.sgef_petalex_v_09.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;

public class LoginController {
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Button btnIngresar;

    @FXML
    private void onIngresar(ActionEvent event) {
        // Intentamos autenticar
        boolean autentico = authenticate(txtUser.getText(), txtPass.getText());

        if (autentico) {
            try {
                // Carga del menú principal
                Parent mainRoot = FXMLLoader.load(
                        getClass().getResource("/fxml/MainMenu.fxml")
                );
                Scene mainScene = new Scene(mainRoot);
                mainScene.getStylesheets().add(
                        getClass().getResource("/css/styles.css").toExternalForm()
                );
                Stage stage = (Stage) btnIngresar.getScene().getWindow();
                stage.setScene(mainScene);
                //stage.setFullScreen(true);
                stage.centerOnScreen();
                stage.setTitle("Index Blooms – Menú Principal");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Credenciales incorrectas: mostramos una alerta de error
            Stage stage = (Stage) btnIngresar.getScene().getWindow();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error de inicio de sesión");
            alert.setHeaderText(null);
            alert.setContentText("Usuario o contraseña incorrectos");
            alert.initOwner(stage);
            alert.showAndWait();
            txtUser.clear();
            txtPass.clear();
            txtUser.requestFocus();
        }
    }
    private boolean authenticate(String user, String pass) {
        return "admin".equals(user) && "admin".equals(pass);
    }
}