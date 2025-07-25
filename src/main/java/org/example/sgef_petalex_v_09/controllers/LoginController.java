package org.example.sgef_petalex_v_09.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.util.ScreenManager;
import org.example.sgef_petalex_v_09.util.UserSession;
import org.example.sgef_petalex_v_09.util.UserUtil;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.net.URL;

public class LoginController {
    
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnIngresar;
    @FXML private Button btnSalir;

    private static final int MAX_INTENTOS = 3;
    private int intentos = 0;

    @FXML
    private void initialize() {
        // Limpiar cualquier sesión previa
        UserSession.cerrarSesion();
        
        // Configurar listeners
        txtUsuario.textProperty().addListener((obs, old, val) -> 
            validarCampos());
        txtPassword.textProperty().addListener((obs, old, val) -> 
            validarCampos());
    }

    private void validarCampos() {
        boolean camposVacios = txtUsuario.getText().trim().isEmpty() || 
                             txtPassword.getText().trim().isEmpty();
        btnIngresar.setDisable(camposVacios);
    }

    @FXML
    private void onIngresar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (intentos >= MAX_INTENTOS) {
            mostrarError("Demasiados intentos fallidos", 
                "Por favor, contacte al administrador del sistema.");
            System.exit(0);
            return;
        }

        Usuario usuarioEncontrado = UserUtil.buscarUsuario(usuario, password);

        if (usuarioEncontrado != null) {
            if ("Inactivo".equals(usuarioEncontrado.getEstado())) {
                mostrarError("Usuario inactivo", 
                    "Este usuario está desactivado. Contacte al administrador.");
                return;
            }

            // Iniciar sesión
            UserSession.iniciarSesion(usuarioEncontrado);

            // Mostrar mensaje de bienvenida
            DialogHelper.showSuccess(btnIngresar.getScene().getWindow(),
                "Bienvenido " + usuarioEncontrado.getNombre());

            // Cargar menú principal usando el mismo método que MainMenuController
            try {
                Stage stage = (Stage) btnIngresar.getScene().getWindow();
                URL resource = getClass().getResource("/fxml/MainMenu.fxml");
                Parent rootNode = FXMLLoader.load(resource);
                Scene scene = new Scene(rootNode);
                scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
                );
                stage.setScene(scene);
                stage.setTitle("Index Blooms - Menú Principal");
                stage.centerOnScreen();
            } catch (IOException e) {
                e.printStackTrace();
                mostrarError("Error", "No se pudo cargar el menú principal");
            }

        } else {
            intentos++;
            int restantes = MAX_INTENTOS - intentos;
            
            mostrarError("Credenciales inválidas",
                "Usuario o contraseña incorrectos.\nIntentos restantes: " + restantes);
            
            txtPassword.clear();
            txtPassword.requestFocus();
        }
    }

    @FXML
    private void onSalir(ActionEvent event) {
        System.exit(0);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}