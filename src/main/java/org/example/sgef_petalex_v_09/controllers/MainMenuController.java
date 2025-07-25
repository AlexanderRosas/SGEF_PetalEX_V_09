package org.example.sgef_petalex_v_09.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import org.example.sgef_petalex_v_09.util.NavigationHelper;

public class MainMenuController {

    @FXML private AnchorPane root;
    @FXML private Button btnClientes;
    @FXML private Button btnVentas;
    @FXML private Button btnCompras;
    @FXML private Button btnProveedores;
    @FXML private Button btnSistema;

    @FXML
    private void onClientes(ActionEvent event) {
        NavigationHelper.cargarVista(event, "/fxml/Clientes.fxml", "Index Blooms – Clientes");
    }

    @FXML
    private void onVentas(ActionEvent event) {
        NavigationHelper.cargarVista(event, "/fxml/Ventas.fxml", "Index Blooms – Ventas");
    }

    @FXML
    private void onCompras(ActionEvent event) {
        NavigationHelper.cargarVista(event, "/fxml/ComprasPedidos.fxml", "Index Blooms – Compras y Pedidos");
    }

    @FXML
    private void onProveedores(ActionEvent event) {
        NavigationHelper.cargarVista(event, "/fxml/Proveedores.fxml", "Index Blooms – Proveedores");
    }

    @FXML
    private void onSistema(ActionEvent event) {
        NavigationHelper.cargarVista(event, "/fxml/Administracion.fxml", "Index Blooms – Administración del Sistema");
    }
}
