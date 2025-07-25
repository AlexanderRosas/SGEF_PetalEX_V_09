package org.example.sgef_petalex_v_09.controllers;

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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.models.Venta;
import org.example.sgef_petalex_v_09.util.CSVUtil;
import org.example.sgef_petalex_v_09.util.NavigationHelper;
import org.example.sgef_petalex_v_09.util.UserSession;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static org.example.sgef_petalex_v_09.util.CSVUtil.VENTAS_CSV;

public class VentasController implements Initializable {

    @FXML private Button btnBack;
    @FXML private TextField txtPuntoEmision;
    @FXML private TextField txtFecha;
    @FXML private TextField txtSucursal;
    @FXML private Button btnNuevo;
    @FXML private Button btnRecaudar;
    @FXML private Button btnEliminar;

    @FXML private TableView<Venta> tablaVentas;
    @FXML private TableColumn<Venta, String>  colId;
    @FXML private TableColumn<Venta, String>  colDestino;
    @FXML private TableColumn<Venta, String>  colServicio;
    @FXML private TableColumn<Venta, String>  colCliente;
    @FXML private TableColumn<Venta, String>  colDetalle;
    @FXML private TableColumn<Venta, Double>  colPrecio;
    @FXML private TableColumn<Venta, Double>  colIva;
    @FXML private TableColumn<Venta, Double>  colTotal;

    private final ObservableList<Venta> listaVentas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColumnas();
        cargarDatos();
        configurarCamposFijos();
        configurarListeners();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colDestino.setCellValueFactory(c -> c.getValue().tipoDestinoProperty());
        colServicio.setCellValueFactory(c -> c.getValue().servicioProperty());
        colCliente.setCellValueFactory(c -> c.getValue().clienteProperty());
        colDetalle.setCellValueFactory(c -> new SimpleStringProperty(
                (String) c.getValue().getDetalleResumen()));
        colPrecio.setCellValueFactory(c -> c.getValue().precioProperty().asObject());
        colIva.setCellValueFactory(c -> c.getValue().ivaProperty());
        colTotal.setCellValueFactory(c -> c.getValue().totalProperty().asObject());

        // Formateo monetario
        formatoMoneda(colPrecio);
        formatoMoneda(colIva);
        formatoMoneda(colTotal);
    }

    private void formatoMoneda(TableColumn<Venta, Double> col) {
        col.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                setText(empty || val == null ? null : String.format("$%.2f", val));
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
                }
        );
    }

    @FXML
    private void onNuevo(ActionEvent event) {
        // 1) Tipo de Destino
        List<String> opciones = Arrays.asList("Nacional", "Internacional");
        ChoiceDialog<String> tipoDialog = new ChoiceDialog<>(opciones.get(0), opciones);
        tipoDialog.setTitle("Nueva Venta – Tipo de Destino");
        tipoDialog.setHeaderText(null);
        tipoDialog.setContentText("Selecciona el Tipo de Destino:");

        // Deshabilitar OK si no hay selección
        Button okBtn = (Button) tipoDialog.getDialogPane().lookupButton(ButtonType.OK);
        okBtn.disableProperty().bind(tipoDialog.selectedItemProperty().isNull());

        Optional<String> tipoOpt = tipoDialog.showAndWait();
        if (!tipoOpt.isPresent()) return;
        String tipoDestino = tipoOpt.get();

        // 2) Selección de Cliente
        try {
            FXMLLoader clLoader = new FXMLLoader(
                    getClass().getResource("/fxml/ClienteSelection.fxml"));
            Parent clRoot = clLoader.load();
            Stage clStage = new Stage();
            clStage.initModality(Modality.WINDOW_MODAL);
            clStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            clStage.setTitle("Seleccionar Cliente");
            clStage.setScene(new Scene(clRoot));
            clStage.showAndWait();

            ClienteSelectionController clCtrl = clLoader.getController();
            Optional<Cliente> clienteOpt = clCtrl.getClienteSeleccionado();
            if (!clienteOpt.isPresent()) return;
            Cliente cliente = clienteOpt.get();

            // 3) Detalle de Venta
            FXMLLoader vdLoader = new FXMLLoader(
                    getClass().getResource("/fxml/VentaDetail.fxml"));
            Parent vdRoot = vdLoader.load();
            Stage vdStage = new Stage();
            vdStage.initModality(Modality.WINDOW_MODAL);
            vdStage.initOwner(clStage);
            vdStage.setTitle("Nueva Venta – " + tipoDestino);
            Scene vdScene = new Scene(vdRoot);
            vdScene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm());
            vdStage.setScene(vdScene);

            // Inicializar datos
            VentaDetailController vdCtrl = vdLoader.getController();
            Venta nuevaVenta = new Venta();
            nuevaVenta.setTipoDestino(tipoDestino);
            nuevaVenta.setCliente(cliente.getId());
            nuevaVenta.setFecha(LocalDate.now());
            vdCtrl.initData(nuevaVenta);

            vdStage.showAndWait();

            // 4) Recuperar y guardar
            vdCtrl.getVentaCreated().ifPresent(venta -> {
                // Generar ID
                String id = String.format("V%03d", listaVentas.size() + 1);
                venta.setId(id);
                venta.setServicio("Ventas");
                listaVentas.add(venta);
                CSVUtil.guardarVentas(listaVentas, VENTAS_CSV);
            });

        } catch (IOException e) {
            showError("Error al crear nueva venta", e);
        }
    }

    @FXML
    private void onRecaudar(ActionEvent event) {
        Venta sel = tablaVentas.getSelectionModel().getSelectedItem();
        if (sel != null) {
            UserSession.setVentaSeleccionada(sel);
            //NavigationHelper.cargarVista(event, "/fxml/Recaudacion.fxml", "Recaudación");
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
        NavigationHelper.volverAlMenuPrincipal(event);
    }

    /*— Utilitarios de alerta —*/
    private void showError(String msg, Exception e) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg + "\n" + e.getMessage());
        a.initOwner(tablaVentas.getScene().getWindow());
        a.setHeaderText(null);
        a.showAndWait();
    }
    private void showWarning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg);
        a.initOwner(tablaVentas.getScene().getWindow());
        a.setHeaderText(null);
        a.showAndWait();
    }
}
