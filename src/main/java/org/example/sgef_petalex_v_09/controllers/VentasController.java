package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;

// Agrega imports para escuchar cambios

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.models.Venta;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.util.NavigationHelper;

import java.io.IOException;
import java.time.LocalDate;

public class VentasController {

    @FXML
    private AnchorPane root;
    @FXML
    private Button btnBack;
    @FXML
    private Button btnAddVenta;
    @FXML
    private Button btnActualizarVenta;
    @FXML
    private Button btnAnularVenta;
    @FXML
    private TableView<Venta> tableVentas;
    @FXML
    private TableColumn<Venta, String> colCliente;
    @FXML
    private TableColumn<Venta, String> colFecha;
    @FXML
    private TableColumn<Venta, Number> colTotal;
    @FXML
    private TableColumn<Venta, String> colEstado;
    @FXML
    private TextField txtBuscar; // <--- Nuevo
    @FXML
    private ComboBox<String> cbEstado; // <--- Nuevo

    private final ObservableList<Venta> allVentas = FXCollections.observableArrayList();
    private FilteredList<Venta> filteredVentas;

    @FXML
    public void initialize() {
        filteredVentas = new FilteredList<>(allVentas, p -> true);

        // Columnas
        colCliente.setCellValueFactory(v -> v.getValue().clienteProperty());
        colFecha.setCellValueFactory(v -> v.getValue().fechaProperty().asString());
        colTotal.setCellValueFactory(v -> v.getValue().totalProperty());
        colEstado.setCellValueFactory(v -> v.getValue().estadoProperty()); // Vinculación
        // Inicializa ComboBox de estado (ejemplo)
        cbEstado.getItems().addAll("Todos", "Activo", "Anulado");
        cbEstado.getSelectionModel().selectFirst(); // seleccionar "Todos" por defecto

        // Inicializa FilteredList con todos los datos
        filteredVentas = new FilteredList<>(allVentas, p -> true);

        // Escuchar cambios en txtBuscar y cbEstado para filtrar
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrar());
        cbEstado.valueProperty().addListener((obs, oldVal, newVal) -> filtrar());

        // Conecta FilteredList a la tabla con SortedList para ordenar si quieres
        SortedList<Venta> sortedList = new SortedList<>(filteredVentas);
        sortedList.comparatorProperty().bind(tableVentas.comparatorProperty());
        tableVentas.setItems(sortedList);

        // Configurar botones según selección de tabla
        tableVentas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnActualizarVenta.setDisable(newSel == null);
            btnAnularVenta.setDisable(newSel == null || "Anulado".equals(newSel.getEstado()));
        });

        // Inicialmente deshabilitar botones
        btnActualizarVenta.setDisable(true);
        btnAnularVenta.setDisable(true);

        // Agregar datos de prueba
        cargarDatosPrueba();
    }

    private void cargarDatosPrueba() {
        // Datos de ejemplo para testing
        Venta v1 = new Venta("Juan Pérez", "Calle 123", LocalDate.now().minusDays(2));
        v1.setEstado("Activo");
        v1.setTotal(150.50);

        Venta v2 = new Venta("María González", "Av. Principal 456", LocalDate.now().minusDays(1));
        v2.setEstado("Activo");
        v2.setTotal(320.75);

        Venta v3 = new Venta("Carlos López", "Plaza Central 789", LocalDate.now());
        v3.setEstado("Anulado");
        v3.setTotal(89.25);

        allVentas.addAll(v1, v2, v3);
    }

    private void filtrar() {
        String texto = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase() : "";
        String estadoSeleccionado = cbEstado.getValue();

        filteredVentas.setPredicate(venta -> {
            boolean coincideTexto = texto.isEmpty() ||
                    venta.getCliente().toLowerCase().contains(texto) ||
                    venta.getFecha().toString().contains(texto) ||
                    String.valueOf(venta.getTotal()).contains(texto);

            boolean coincideEstado = estadoSeleccionado == null || estadoSeleccionado.equals("Todos") ||
                    venta.getEstado().equalsIgnoreCase(estadoSeleccionado);

            return coincideTexto && coincideEstado;
        });
    }

    @FXML
    private void onBack(ActionEvent event) {
        NavigationHelper.volverAlMenuPrincipal(event);
    }


    @FXML
    private void onAddVenta(ActionEvent event) throws IOException {
        // 1) Selección de cliente
        FXMLLoader selLoader = new FXMLLoader(getClass().getResource("/fxml/ClienteSelection.fxml"));
        Parent selRoot = selLoader.load();
        ClienteSelectionController selCtrl = selLoader.getController();

        Stage selStage = new Stage();
        selStage.initOwner(btnAddVenta.getScene().getWindow());
        selStage.initModality(Modality.APPLICATION_MODAL);
        selStage.setScene(new Scene(selRoot));
        selStage.setTitle("Seleccionar Cliente");
        selStage.showAndWait();

        Cliente c = selCtrl.getSelectedCliente();

        // Validar que se seleccionó un cliente
        if (c == null) {
            DialogHelper.showWarning(
                    btnAddVenta.getScene().getWindow(),
                    "Debe seleccionar un cliente para continuar.");
            return;
        }

        // 2) Confirmar selección
        boolean confirmado = DialogHelper.confirm(
                btnAddVenta.getScene().getWindow(),
                "¿Está seguro que desea Seleccionar al " + c.getNombre() + "?");
        if (!confirmado)
            return;

        // 3) Crear objeto Venta con nombre, dirección, fecha
        Venta nuevaVenta = new Venta(
                c.getNombre(),
                c.getDireccion(),
                LocalDate.now());
        nuevaVenta.setEstado("Activo");

        // 4) Abrir detalle de venta
        FXMLLoader detLoader = new FXMLLoader(getClass().getResource("/fxml/VentaDetail.fxml"));
        Parent detRoot = detLoader.load();
        VentaDetailController detCtrl = detLoader.getController();
        detCtrl.initData(nuevaVenta);

        Stage detStage = new Stage();
        detStage.initOwner(btnAddVenta.getScene().getWindow());
        detStage.initModality(Modality.APPLICATION_MODAL);
        detStage.setScene(new Scene(detRoot));
        detStage.setTitle("Nueva Venta");
        detStage.showAndWait();

        // 5) Recuperar venta si fue aceptada
        detCtrl.getVentaCreated().ifPresent(v -> allVentas.add(v));
    }

    @FXML
    private void onActualizarVenta(ActionEvent event) throws IOException {
        Venta ventaSeleccionada = tableVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada == null) {
            DialogHelper.showWarning(
                    btnActualizarVenta.getScene().getWindow(),
                    "Seleccione una venta para actualizar.");
            return;
        }

        if ("Anulado".equals(ventaSeleccionada.getEstado())) {
            DialogHelper.showWarning(
                    btnActualizarVenta.getScene().getWindow(),
                    "No se puede actualizar una venta anulada.");
            return;
        }

        // Abrir VentaDetail para editar
        FXMLLoader detLoader = new FXMLLoader(getClass().getResource("/fxml/VentaDetail.fxml"));
        Parent detRoot = detLoader.load();
        VentaDetailController detCtrl = detLoader.getController();
        detCtrl.initData(ventaSeleccionada);

        Stage detStage = new Stage();
        detStage.initOwner(btnActualizarVenta.getScene().getWindow());
        detStage.initModality(Modality.APPLICATION_MODAL);
        detStage.setScene(new Scene(detRoot));
        detStage.setTitle("Actualizar Venta");
        detStage.showAndWait();

        // La tabla se actualiza automáticamente por las properties
        DialogHelper.showSuccess(
                btnActualizarVenta.getScene().getWindow(),
                "Venta actualizada correctamente.");
    }

    @FXML
    private void onAnularVenta(ActionEvent event) {
        Venta ventaSeleccionada = tableVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada == null) {
            DialogHelper.showWarning(
                    btnAnularVenta.getScene().getWindow(),
                    "Seleccione una venta para anular.");
            return;
        }

        boolean confirmado = DialogHelper.confirm(
                btnAnularVenta.getScene().getWindow(),
                "¿Está seguro que desea anular la venta seleccionada?");
        if (!confirmado)
            return;

        // Solo cambiamos el estado, no se elimina
        ventaSeleccionada.setEstado("Anulado");

        DialogHelper.showSuccess(
                btnAnularVenta.getScene().getWindow(),
                "anulado la venta");
    }

}
