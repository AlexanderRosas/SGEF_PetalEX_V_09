package org.example.sgef_petalex_v_09.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.*;
import org.example.sgef_petalex_v_09.validators.DataValidator;
import org.example.sgef_petalex_v_09.validators.ValidationResult;

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

    private void setErrorStyle(TextField field, String message) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        Tooltip tooltip = new Tooltip(message);
        tooltip.setStyle("-fx-background-color: #ffdddd; -fx-text-fill: red;");
        field.setTooltip(tooltip);
    }

    private void clearErrorStyle(TextField field) {
        field.setStyle(null);
        field.setTooltip(null);
    }

    @FXML
    private void onIngresar(ActionEvent event) {
        String correo = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        clearErrorStyle(txtUsuario);
        clearErrorStyle(txtPassword);

        boolean valid = true;

        // Validar correo electrónico
        ValidationResult vCorreo = DataValidator.validateCorreo(correo);
        if (!vCorreo.isValid()) {
            setErrorStyle(txtUsuario, vCorreo.getErrorMessage());
            valid = false;
        }

        // Validar contraseña
        ValidationResult vPassword = DataValidator.validatePassword(password);
        if (!vPassword.isValid()) {
            setErrorStyle(txtPassword, vPassword.getErrorMessage());
            valid = false;
        }

        if (!valid) {
            DialogHelper.showError(null, "Error al Iniciar Sesión. Corrige los campos resaltados (mantén el cursor sobre el campo para más información)");
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
            setErrorStyle(txtUsuario, "Correo electrónico o contraseña incorrectos.");
            setErrorStyle(txtPassword, "Correo electrónico o contraseña incorrectos.");
            DialogHelper.showError(null, "Error al Iniciar Sesión. Corrige los campos resaltados (mantén el cursor sobre el campo para más información)");
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    @FXML
    private void onSalir(ActionEvent event) {
        System.exit(0);
    }
}