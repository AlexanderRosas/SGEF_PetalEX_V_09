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

import java.io.IOException;

public class LoginController {
    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Button btnIngresar;

    @FXML
    private void onIngresar(ActionEvent event) {
        if (authenticate(txtUser.getText(), txtPass.getText())) {
            try {
                Parent mainRoot = FXMLLoader.load(
                        getClass().getResource("/fxml/MainMenu.fxml")
                );
                Scene mainScene = new Scene(mainRoot);
                mainScene.getStylesheets().add(
                        getClass().getResource("/css/styles.css").toExternalForm()
                );
                Stage stage = (Stage) btnIngresar.getScene().getWindow();
                stage.setScene(mainScene);
                stage.setTitle("Index Blooms – Menú Principal");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // TODO: mostrar alerta
            System.out.println("Usuario o contraseña incorrectos");
        }
    }

    private boolean authenticate(String user, String pass) {
        return "admin".equals(user) && "admin".equals(pass);
    }
}