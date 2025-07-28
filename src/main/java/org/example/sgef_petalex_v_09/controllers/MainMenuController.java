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

import java.io.IOException;
import java.net.URL;

import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.UserSession;

public class MainMenuController {

    @FXML
    private AnchorPane root;

    @FXML
    private Button btnClientes, btnVentas, btnCompras, btnProveedores, btnSistema;

    @FXML
    private Label lblBienvenida;

    // Método initialize sin ResourceBundle para evitar null pointer
    @FXML
    public void initialize() {
        Usuario usuario = UserSession.getUsuarioActual();
        if (usuario != null && usuario.getUsuario() != null) {
            lblBienvenida.setText("Bienvenido: " + usuario.getUsuario());
        } else {
            lblBienvenida.setText("Bienvenido: Invitado");
        }
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
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(loginRoot);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Index Blooms – Login");
            stage.setMaximized(false);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
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
