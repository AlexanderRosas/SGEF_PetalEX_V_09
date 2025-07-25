package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.sgef_petalex_v_09.models.Cliente;

public class ClienteSelectionController {
    @FXML private TableView<Cliente> tableClientes;
    @FXML private TextField txtFilterNombre;
    @FXML private Button btnAceptar;
    @FXML private Button btnCancel;
    
    private Cliente clienteSeleccionado;
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();
    
    @FXML
    public void initialize() {
        // Configurar tabla y cargar datos
        setupTable();
        loadClientes();
        
        // Habilitar botón Aceptar solo cuando hay selección
        btnAceptar.disableProperty().bind(
            tableClientes.getSelectionModel().selectedItemProperty().isNull()
        );
        
        // Filtro de búsqueda
        txtFilterNombre.textProperty().addListener((obs, old, text) -> {
            filterClientes(text);
        });
    }
    
    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }
    
    @FXML
    private void onAccept() {
        clienteSeleccionado = tableClientes.getSelectionModel().getSelectedItem();
        closeWindow();
    }
    
    @FXML
    private void onCancel() {
        clienteSeleccionado = null;
        closeWindow();
    }
    
    private void closeWindow() {
        ((Stage)btnCancel.getScene().getWindow()).close();
    }
    
    private void filterClientes(String filtro) {
        // Implementar filtrado de clientes
    }
    
    private void setupTable() {
        // Configurar columnas de la tabla
    }
    
    private void loadClientes() {
        // Cargar lista de clientes
    }
}