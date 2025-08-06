package org.example.sgef_petalex_v_09.controllers;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.sgef_petalex_v_09.models.Venta;
import org.example.sgef_petalex_v_09.util.CSVUtil;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.util.UserSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.example.sgef_petalex_v_09.util.CSVUtil.VENTAS_CSV;

public class VentasController implements Initializable {

    @FXML
    private Button btnBack;

    @FXML
    private TextField txtPuntoEmision;
    @FXML
    private TextField txtFecha;
    @FXML
    private TextField txtSucursal;

    @FXML
    private Button btnExportarFactura;
    @FXML
    private Button btnAnularVenta;
    @FXML
    private Button btnRecaudar;
    @FXML
    private Button btnEliminar;

    @FXML
    private DatePicker dpFechaDesde;
    @FXML
    private DatePicker dpFechaHasta;

    @FXML
    private TableView<Venta> tablaVentas;
    @FXML
    private TableColumn<Venta, String> colId;
    @FXML
    private TableColumn<Venta, String> colDestino;
    @FXML
    private TableColumn<Venta, String> colServicio;
    @FXML
    private TableColumn<Venta, String> colCliente;
    @FXML
    private TableColumn<Venta, String> colDetalle;
    @FXML
    private TableColumn<Venta, Number> colPrecio;
    @FXML
    private TableColumn<Venta, Number> colIva;
    @FXML
    private TableColumn<Venta, Number> colTotal;
    @FXML
    private TableColumn<Venta, String> colEstado;
    @FXML
    private TableColumn<Venta, String> colFecha;
    @FXML
    private TableColumn<Venta, String> colUsuarioResponsable;

    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarDatos();
        configurarCamposFijos();
        configurarListeners();

        Platform.runLater(() -> {
            Stage stage = (Stage) tablaVentas.getScene().getWindow();
            stage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (isFocused) {
                    cargarDatos();
                }
            });
        });
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colDestino.setCellValueFactory(c -> c.getValue().tipoDestinoProperty());
        colServicio.setCellValueFactory(c -> c.getValue().servicioProperty());
        colCliente.setCellValueFactory(c -> c.getValue().clienteNombreProperty());
        colDetalle.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDetalleProductos()));

        colPrecio.setCellValueFactory(c -> c.getValue().precioProperty());
        colIva.setCellValueFactory(c -> c.getValue().ivaProperty());
        colTotal.setCellValueFactory(c -> c.getValue().totalProperty());

        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFecha().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        colUsuarioResponsable.setCellValueFactory(c -> c.getValue().usuarioResponsableProperty());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "EC"));

        formatoMoneda(colPrecio, currencyFormat);
        formatoMoneda(colIva, currencyFormat);
        formatoMoneda(colTotal, currencyFormat);
    }

    private void formatoMoneda(TableColumn<Venta, Number> col, NumberFormat format) {
        col.setCellFactory(tc -> new TableCell<Venta, Number>() {
            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : format.format(value.doubleValue()));
            }
        });
    }

    private void cargarDatos() {
        listaVentas.setAll(CSVUtil.leerVentas());
        tablaVentas.setItems(listaVentas);
    }

    private void configurarCamposFijos() {
        txtPuntoEmision.setText(UserSession.getPuntoEmision());
        txtFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        txtSucursal.setText(UserSession.getSucursal());

        txtPuntoEmision.setEditable(false);
        txtFecha.setEditable(false);
        txtSucursal.setEditable(false);
    }

    private void configurarListeners() {
        tablaVentas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> {
                    boolean sel = newSel != null;
                    btnRecaudar.setDisable(!sel);
                    btnEliminar.setDisable(!sel);
                    btnAnularVenta.setDisable(!sel);
                    btnExportarFactura.setDisable(false); // Siempre habilitado para emitir facturas
                });
    }

    @FXML
    private void onExportarFactura(ActionEvent event) {
        LocalDate desde = dpFechaDesde.getValue();
        LocalDate hasta = dpFechaHasta.getValue();

        if (desde == null || hasta == null) {
            showWarning("Por favor seleccione el rango de fechas para emitir facturas.");
            return;
        }
        if (hasta.isBefore(desde)) {
            showWarning("La fecha 'Hasta' no puede ser anterior a la fecha 'Desde'.");
            return;
        }

        List<Venta> ventasFiltradas = listaVentas.stream()
                .filter(v -> !v.getFecha().isBefore(desde) && !v.getFecha().isAfter(hasta))
                .toList();

        // Aquí deberías implementar la lógica para exportar esas facturas

        showInfo("Facturas emitidas para ventas del " + desde + " al " + hasta);
    }

    @FXML
    private void onAnularVenta(ActionEvent event) {
        Venta ventaSel = tablaVentas.getSelectionModel().getSelectedItem();
        if (ventaSel == null) {
            showWarning("Seleccione una venta para anular.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de anular la venta " + ventaSel.getId() + "?",
                ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.initOwner(tablaVentas.getScene().getWindow());

        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            ventaSel.setEstado("Anulada");
            CSVUtil.guardarVentas(listaVentas, VENTAS_CSV);
            tablaVentas.refresh();
            showInfo("Venta anulada correctamente.");
        }
    }

    @FXML
    private void onRecaudar(ActionEvent event) {
        Venta sel = tablaVentas.getSelectionModel().getSelectedItem();
        if (sel != null) {
            UserSession.setVentaSeleccionada(sel);
            // Aquí la navegación al módulo de recaudación si está implementado
            // NavigationHelper.cargarVista(event, "/fxml/Recaudacion.fxml", "Recaudación");
        }
    }

    @FXML
    private void onEliminar(ActionEvent event) {
        Venta sel = tablaVentas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showWarning("Seleccione una venta para eliminar.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar venta " + sel.getId() + "?", ButtonType.OK, ButtonType.CANCEL);
        confirm.setHeaderText(null);
        confirm.initOwner(tablaVentas.getScene().getWindow());
        if (confirm.showAndWait().filter(bt -> bt == ButtonType.OK).isPresent()) {
            listaVentas.remove(sel);
            CSVUtil.guardarVentas(listaVentas, VENTAS_CSV);
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            boolean wasMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));

            Scene scene = stage.getScene();
            scene.setRoot(root);

            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            stage.setResizable(false);
            if (wasMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setMaximized(false);
                stage.setWidth(width);
                stage.setHeight(height);
                stage.centerOnScreen();
            }

            stage.setTitle("Index Blooms – Menú Principal");

        } catch (IOException e) {
            e.printStackTrace();
            DialogHelper.showError(null, "No se pudo cargar el menú principal.");
        }
    }

    /* — Utilitarios de alerta — */
    private void showWarning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg);
        a.initOwner(tablaVentas.getScene().getWindow());
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.initOwner(tablaVentas.getScene().getWindow());
        a.setHeaderText(null);
        a.showAndWait();
    }
}
