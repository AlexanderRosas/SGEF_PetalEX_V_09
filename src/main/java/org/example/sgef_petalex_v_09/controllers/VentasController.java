package org.example.sgef_petalex_v_09.controllers;

import javafx.beans.property.SimpleDoubleProperty;
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
import org.example.sgef_petalex_v_09.models.Venta;
import org.example.sgef_petalex_v_09.util.CSVUtil;
import org.example.sgef_petalex_v_09.util.NavigationHelper;
import org.example.sgef_petalex_v_09.util.ScreenManager;
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
    @FXML private TableColumn<Venta, String> colId;
    @FXML private TableColumn<Venta, String> colDestino;
    @FXML private TableColumn<Venta, String> colServicio;
    @FXML private TableColumn<Venta, String> colCliente;
    @FXML private TableColumn<Venta, String> colDetalle;
    @FXML private TableColumn<Venta, Double> colPrecio;
    @FXML private TableColumn<Venta, Double> colIva;
    @FXML private TableColumn<Venta, Double> colTotal;

    private ObservableList<Venta> listaVentas = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configurar columnas
        configurarColumnas();
        
        // Cargar datos
        cargarDatos();
        
        // Configurar campos fijos
        configurarCamposFijos();
        
        // Configurar listeners
        configurarListeners();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colDestino.setCellValueFactory(c -> c.getValue().tipoDestinoProperty());
        colServicio.setCellValueFactory(c -> c.getValue().servicioProperty());
        colCliente.setCellValueFactory(c -> c.getValue().clienteProperty());
        colDetalle.setCellValueFactory(c -> new SimpleStringProperty(
                String.valueOf(c.getValue().getDetalleResumen())));
        colPrecio.setCellValueFactory(c -> c.getValue().precioProperty().asObject());
        colIva.setCellValueFactory(c -> c.getValue().ivaProperty().asObject());
        colTotal.setCellValueFactory(c -> c.getValue().totalProperty().asObject());

        // Formato para columnas numéricas
        configurarFormatoColumnas();
    }

    private void configurarFormatoColumnas() {
        colPrecio.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", precio));
                }
            }
        });

        colIva.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double iva, boolean empty) {
                super.updateItem(iva, empty);
                if (empty || iva == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", iva));
                }
            }
        });

        colTotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total));
                }
            }
        });
    }

    private void cargarDatos() {
        listaVentas.clear();
        listaVentas.addAll(CSVUtil.leerVentas());
        tablaVentas.setItems(listaVentas);
    }

    private void configurarCamposFijos() {
        txtPuntoEmision.setText(UserSession.getPuntoEmision());
        txtFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yy")));
        txtSucursal.setText(UserSession.getSucursal());
        
        txtPuntoEmision.setEditable(false);
        txtFecha.setEditable(false);
        txtSucursal.setEditable(false);
    }

    private void configurarListeners() {
        // Listener para selección en tabla
        tablaVentas.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSel, newSel) -> {
                boolean haySeleccion = (newSel != null);
                btnRecaudar.setDisable(!haySeleccion);
                btnEliminar.setDisable(!haySeleccion);
            }
        );
    }

    @FXML
    private void onNuevo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VentaDetail.fxml"));
            Parent root = loader.load();

            Stage ventaDetailStage = new Stage();
            ventaDetailStage.initModality(Modality.WINDOW_MODAL);
            ventaDetailStage.initOwner(((Node)event.getSource()).getScene().getWindow());
            ventaDetailStage.setTitle("Nueva Venta");
            ventaDetailStage.setScene(new Scene(root));

            VentaDetailController controller = loader.getController();
            
            ventaDetailStage.showAndWait();
            
            controller.getVentaCreated().ifPresent(venta -> {
                String id = String.format("V%03d", listaVentas.size() + 1);
                venta.setId(id);
                venta.setFecha(LocalDate.now());
                
                listaVentas.add(venta);
                CSVUtil.guardarVentas(listaVentas);
            });
            
        } catch (IOException e) {
            mostrarError("Error al crear nueva venta", e);
        }
    }

    @FXML
    private void onRecaudar(ActionEvent event) {
        Venta seleccionada = tablaVentas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            UserSession.setVentaSeleccionada(seleccionada);
            ScreenManager.loadScreen(event, "/fxml/Recaudacion.fxml", "Recaudación");
        }
    }

    @FXML
    private void onEliminar(ActionEvent event) {
        Venta seleccionada = tablaVentas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAdvertencia("Seleccione una venta para eliminar.");
            return;
        }

        if (confirmarEliminacion(seleccionada)) {
            listaVentas.remove(seleccionada);
            CSVUtil.guardarVentas(listaVentas);
        }
    }

    @FXML
    private void onBack(ActionEvent event) {
        NavigationHelper.volverAlMenuPrincipal(event);
    }

    private boolean confirmarEliminacion(Venta venta) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Está seguro que desea eliminar la venta " + venta.getId() + "?");
        return confirm.showAndWait().filter(bt -> bt == ButtonType.OK).isPresent();
    }

    private void mostrarError(String mensaje, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje + "\n" + e.getMessage());
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}