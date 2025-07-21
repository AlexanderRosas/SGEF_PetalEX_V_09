package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.Window;
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

        // ejemplo de clientes (puedes cargar real)
        master.addAll(
                new Cliente(101, "RoseFlower INC.", "Av. Petalos 123", "0991234567", "rosa@rose.com", "Activo"),
                new Cliente(102, "SweetMoment Ltd.", "Calle Tulipán 45", "0987654321", "sm@moment.com", "Inactivo"));

        filtered = new FilteredList<>(master, c -> true);
        tableClientes.setItems(filtered);

        // filtros
        txtFilterNombre.textProperty().addListener((obs, o, v) -> filtered
                .setPredicate(c -> v.isEmpty() || c.getNombre().toLowerCase().contains(v.toLowerCase())));

        // selección
        tableClientes.getSelectionModel().selectedItemProperty().addListener((obs, o, s) -> {
            btnAceptar.setDisable(s == null);
        });

        btnAceptar.setDisable(true);
        btnAceptar.setOnAction(e -> {
            Window w = btnAceptar.getScene().getWindow();
            Cliente c = tableClientes.getSelectionModel().getSelectedItem();
            boolean ok = DialogHelper.confirm(w,
                    "¿Está seguro que desea Seleccionar al " + c.getNombre() + "?");
            if (ok) {
                selectedCliente = c;
                w.hide();
            }
        });

        btnCancel.setOnAction(e -> btnCancel.getScene().getWindow().hide());
    }

    @FXML
    private void onAccept() {
        Cliente c = tableClientes.getSelectionModel().getSelectedItem();
        if (c == null)
            return;

        Stage stage = (Stage) btnAceptar.getScene().getWindow();
        boolean ok = DialogHelper.confirm(
                stage,
                "¿Está seguro que desea Seleccionar al " + c.getNombre() + "?");
        if (ok) {
            selectedCliente = c;
            stage.close();
        }
    }

    @FXML
    private void onCancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    /** Devuelve el cliente seleccionado, o null. */
    public Cliente getSelectedCliente() {
        return selectedCliente;
    }
}
