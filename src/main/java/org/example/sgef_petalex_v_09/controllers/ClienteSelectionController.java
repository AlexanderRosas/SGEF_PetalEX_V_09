package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.util.DialogHelper;

public class ClienteSelectionController {

    @FXML
    private TextField txtFilterNombre;
    @FXML
    private TableView<Cliente> tableClientes;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colDireccion;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, String> colCorreo;
    @FXML
    private TableColumn<Cliente, String> colEstado;

    @FXML
    private Button btnAceptar;
    @FXML
    private Button btnCancel;

    private final ObservableList<Cliente> master = FXCollections.observableArrayList();
    private FilteredList<Cliente> filtered;
    private Cliente selectedCliente = null;

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Datos de prueba con ID corregido (int en lugar de String)
        master.addAll(
                new Cliente(1234567890, "RoseFlower INC.", "Av. Pétalos 123", "0991234567", "rosa@rose.com", "Activo"),
                new Cliente(987654321, "SweetMoment Ltd.", "Calle Tulipán 45", "0987654321", "sm@moment.com", "Inactivo")
        );

        filtered = new FilteredList<>(master, c -> true);
        tableClientes.setItems(filtered);

        txtFilterNombre.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtro = newVal.toLowerCase().trim();
            filtered.setPredicate(cliente ->
                    filtro.isEmpty() || cliente.getNombre().toLowerCase().contains(filtro));
        });

        tableClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnAceptar.setDisable(newSel == null);
        });

        btnAceptar.setDisable(true);
    }

    @FXML
    private void onAccept() {
        Cliente c = tableClientes.getSelectionModel().getSelectedItem();
        if (c == null)
            return;

        Stage stage = (Stage) btnAceptar.getScene().getWindow();
        boolean ok = DialogHelper.confirm(
                stage,
                "¿Está seguro que desea seleccionar al cliente " + c.getNombre() + "?");
        if (ok) {
            selectedCliente = c;
            stage.close();
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        boolean ok = DialogHelper.confirm(
                stage,
                "¿Está seguro/a de cancelar la selección del cliente?");
        if (ok) {
            selectedCliente = null;
            stage.close();
        }
    }

    public Cliente getSelectedCliente() {
        return selectedCliente;
    }
}
