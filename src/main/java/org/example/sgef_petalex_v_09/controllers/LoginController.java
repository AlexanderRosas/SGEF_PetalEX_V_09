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
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.*;

import java.io.IOException;
import java.net.URL;

public class LoginController {

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnIngresar;
    @FXML
    private Button btnSalir;

    private static final int MAX_INTENTOS = 3;
    private int intentos = 0;

    @FXML
    private void initialize() {
        UserSession.cerrarSesion();
        validarCampos(); // Deshabilitar botón al inicio si campos vacíos

        // Listener para activar/desactivar botón Ingresar
        txtUsuario.textProperty().addListener((obs, old, val) -> validarCampos());
        txtPassword.textProperty().addListener((obs, old, val) -> validarCampos());
    }

    private void validarCampos() {
        boolean camposVacios = txtUsuario.getText().trim().isEmpty() || txtPassword.getText().trim().isEmpty();
        btnIngresar.setDisable(camposVacios);
    }

    @FXML
    private void onIngresar(ActionEvent event) {
        String correo = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (correo.isEmpty() || password.isEmpty()) {
            DialogHelper.showError(null, "Correo electrónico y contraseña son obligatorios.");
            return;
        }

        // Validación básica correo electrónico
        if (!correo.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            DialogHelper.showError(null, "Formato de correo electrónico inválido.");
            return;
        }

        if (intentos >= MAX_INTENTOS) {
            DialogHelper.showError(null, "Demasiados intentos fallidos. Contacte al administrador.");
            System.exit(0);
            return;
        }

        Usuario usuario = UserUtil.buscarUsuario(correo, password);

        if (usuario != null) {
            if ("Inactivo".equalsIgnoreCase(usuario.getEstado())) {
                DialogHelper.showError(null, "Usuario inactivo. Contacte al administrador.");
                return;
            }

            UserSession.iniciarSesion(usuario);
            try {
                Stage stage = (Stage) btnIngresar.getScene().getWindow();

                URL resource = getClass().getResource("/fxml/MainMenu.fxml");
                Parent root = FXMLLoader.load(resource);

                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

                stage.setScene(scene);

                // Primero desmaximizar y luego maximizar para forzar el cambio
                stage.setMaximized(false);
                stage.setResizable(true);
                stage.setMaximized(true);

                stage.setTitle("Index Blooms - Menú Principal");
                // No es necesario llamar a stage.show() porque ya está visible

            } catch (IOException e) {
                e.printStackTrace(); // Para debug
                DialogHelper.showError(null, "No se pudo cargar el menú principal.");
            }

        } else {
            intentos++;
            DialogHelper.showError(null, "Nombre de usuario o contraseña incorrectos.");
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    @FXML
    private void onSalir(ActionEvent event) {
        System.exit(0);
    }
}
