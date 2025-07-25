package org.example.sgef_petalex_v_09.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.util.CSVUtil;

import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ClienteSelectionController implements Initializable {

    @FXML private TextField txtFilterNombre;
    @FXML private TableView<Cliente> tableClientes;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;
    @FXML private TableColumn<Cliente, String> colEstado;
    @FXML private Button btnCancel;
    @FXML private Button btnAceptar;

    private final ObservableList<Cliente> masterData = FXCollections.observableArrayList();
    private Cliente clienteSeleccionado;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) Configurar las columnas del TableView
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colDireccion.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDireccion()));
        colTelefono.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelefono()));
        colCorreo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCorreo()));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado()));

        // 2) Cargar los clientes desde el CSV
        List<Cliente> clientes = CSVUtil.leerClientes();
        masterData.setAll(clientes);

        // 3) Envuelve en un FilteredList para el filtrado
        FilteredList<Cliente> filteredData = new FilteredList<>(masterData, p -> true);
        txtFilterNombre.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtro = newVal.toLowerCase();
            if (filtro.isEmpty()) {
                filteredData.setPredicate(c -> true);
            } else {
                filteredData.setPredicate(c ->
                        c.getNombre().toLowerCase().contains(filtro)
                );
            }
        });

        // 4) Envuelve en un SortedList para que respete la ordenación de la tabla
        SortedList<Cliente> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableClientes.comparatorProperty());

        // 5) Asigna los datos a la tabla
        tableClientes.setItems(sortedData);

        // 6) Listener de selección para activar el botón Aceptar
        tableClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            clienteSeleccionado = newSel;
            btnAceptar.setDisable(newSel == null);
        });

        // Inicialmente, sin cliente seleccionado
        btnAceptar.setDisable(true);
    }

    @FXML
    private void onCancel() {
        clienteSeleccionado = null;
        closeWindow();
    }

    @FXML
    private void onAccept() {
        if (clienteSeleccionado != null) {
            closeWindow();
        }
    }

    /**
     * Devuelve null si el usuario canceló, o el Cliente seleccionado.
     */
    public Optional<Cliente> getClienteSeleccionado() {
        return Optional.ofNullable(clienteSeleccionado);
    }

    /** Cierra esta ventana modal. */
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
}
