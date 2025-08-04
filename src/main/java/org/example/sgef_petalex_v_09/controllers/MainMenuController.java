package org.example.sgef_petalex_v_09.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.util.PermisosUtil;
import org.example.sgef_petalex_v_09.util.UserSession;

import java.io.IOException;
import java.net.URL;

public class MainMenuController {

    @FXML
    private AnchorPane root;

    @FXML
    private Button btnClientes, btnVentas, btnCompras, btnProveedores, btnSistema;

    @FXML
    private Label lblBienvenida;

    @FXML
    public void initialize() {
        Usuario usuario = UserSession.getUsuarioActual();

        if (usuario != null && usuario.getUsuario() != null) {
            lblBienvenida.setText("Bienvenido: " + usuario.getUsuario());
        } else {
            lblBienvenida.setText("Bienvenido: Invitado");
        }

        // Por defecto todos los botones están habilitados, excepto "Sistema"
        btnClientes.setDisable(false);
        btnProveedores.setDisable(false);
        btnCompras.setDisable(false);
        btnVentas.setDisable(false);

        // Solo ADMINISTRADOR o GERENTE pueden acceder a la administración
        String rol = usuario != null ? usuario.getRol() : "";
        boolean accesoSistema = rol.equalsIgnoreCase("ADMINISTRADOR") || rol.equalsIgnoreCase("GERENTE");
        btnSistema.setDisable(!accesoSistema);
    }

    @FXML
    private void onClientes(ActionEvent event) {
        cargarVista(event, "/fxml/Clientes.fxml", "Index Blooms – Clientes");
    }

    @FXML
    private void onVentas(ActionEvent event) {
        cargarVista(event, "/fxml/Ventas.fxml", "Index Blooms – Ventas");
    }

    @FXML
    private void onCompras(ActionEvent event) {
        cargarVista(event, "/fxml/ComprasPedidos.fxml", "Index Blooms – Compras y Pedidos");
    }

    @FXML
    private void onProveedores(ActionEvent event) {
        cargarVista(event, "/fxml/Proveedores.fxml", "Index Blooms – Proveedores");
    }

    @FXML
    private void onSistema(ActionEvent event) {
        cargarVista(event, "/fxml/Administracion.fxml", "Index Blooms – Administración del Sistema");
    }

    @FXML
    private void onLogout(ActionEvent event) {
        Window window = ((Node) event.getSource()).getScene().getWindow();
        if (DialogHelper.confirm(window, "¿Estás seguro/a de cerrar sesión?")) {
            UserSession.cerrarSesion();
            DialogHelper.showSuccess(window, "Sesión cerrada");
            try {
                Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
                Stage stage = (Stage) window;

                Scene scene = new Scene(loginRoot);
                scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

                stage.setScene(scene);
                stage.setTitle("Index Blooms – Login");

                // Ajusta el tamaño de la ventana al contenido de la escena
                stage.sizeToScene();

                // Deshabilita redimensionar para evitar que se modifique el tamaño
                stage.setResizable(false);

                // Centra la ventana en la pantalla
                stage.centerOnScreen();

            } catch (IOException e) {
                e.printStackTrace();
                DialogHelper.showError(window, "No se pudo cargar la pantalla de inicio de sesión.");
            }
        }
    }

    private void cargarVista(ActionEvent event, String fxmlPath, String title) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                System.err.println("ERROR: FXML no encontrado en " + fxmlPath);
                return;
            }

            Parent rootNode = FXMLLoader.load(resource);

            scene.setRoot(rootNode);
            stage.setTitle(title);
            stage.setMaximized(true);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}