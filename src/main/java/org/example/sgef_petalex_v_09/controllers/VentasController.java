package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.models.Venta;
import org.example.sgef_petalex_v_09.util.DialogHelper;

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
    private Button btnViewVenta;
    @FXML
    private TableView<Venta> tableVentas;
    @FXML
    private TableColumn<Venta, String> colCliente;
    @FXML
    private TableColumn<Venta, String> colFecha;
    @FXML
    private TableColumn<Venta, Number> colTotal;
    @FXML
    private Button btnInactivarVenta;

    private final ObservableList<Venta> ventas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Vincula las columnas a las propiedades de Venta
        colCliente.setCellValueFactory(v -> v.getValue().clienteProperty());
        colFecha.setCellValueFactory(v -> v.getValue().fechaProperty().asString());
        colTotal.setCellValueFactory(v -> v.getValue().totalProperty());

        tableVentas.setItems(ventas);
        // Habilita el botón solo si hay selección
        btnInactivarVenta.setDisable(true);
        tableVentas.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnInactivarVenta.setDisable(newSel == null);
        });
    }

    @FXML
    private void onInactivarVenta(ActionEvent event) {
        Venta ventaSeleccionada = tableVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada == null) {
            DialogHelper.showWarning(btnInactivarVenta.getScene().getWindow(), "Selecciona una venta primero");
            return;
        }
        if (!ventaSeleccionada.isActiva()) {
            DialogHelper.showWarning(btnInactivarVenta.getScene().getWindow(), "La venta ya está inactiva.");
            return;
        }
        ventaSeleccionada.setActiva(false);
        DialogHelper.showWarning(btnInactivarVenta.getScene().getWindow(), "La venta ha sido marcada como inactiva.");
        // Si quieres refrescar la tabla, puedes hacer:
        tableVentas.refresh();
    }

    @FXML
    private void onBack(ActionEvent e) throws IOException {
        Parent main = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
        Stage st = (Stage) btnBack.getScene().getWindow();
        st.getScene().setRoot(main);
        st.setTitle("Index Blooms – Menú Principal");
        // st.setMaximized(true); // Eliminado para evitar maximizar siempre la ventana
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
        if (c == null) {
            // El usuario canceló la selección
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
        detCtrl.getVentaCreated().ifPresent(v -> ventas.add(v));
    }

    @FXML
    private void onViewVenta(ActionEvent event) throws IOException {
        Venta ventaSeleccionada = tableVentas.getSelectionModel().getSelectedItem();
        if (ventaSeleccionada == null) {
            DialogHelper.showWarning(
                    btnViewVenta.getScene().getWindow(),
                    "Selecciona una venta primero");
            return;
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/VentaDetail.fxml"));
        Parent detailRoot = loader.load();
        VentaDetailController detailCtrl = loader.getController();

        // Inicializa con la venta existente
        detailCtrl.initData(ventaSeleccionada);

        Stage detailStage = new Stage();
        detailStage.initOwner(btnViewVenta.getScene().getWindow());
        detailStage.initModality(Modality.APPLICATION_MODAL);
        detailStage.setScene(new Scene(detailRoot));
        detailStage.setTitle("Detalle de Venta");
        detailStage.showAndWait();
    }
}
