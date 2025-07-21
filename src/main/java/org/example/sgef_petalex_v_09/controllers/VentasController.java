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
<<<<<<< HEAD
    private Button btnViewVenta;
=======
    private Button btnAnularVenta;
>>>>>>> c83e3e5c64f096d3e9bcd33b21bd6f6bcc86a3e2
    @FXML
    private TableView<Venta> tableVentas;
    @FXML
    private TableColumn<Venta, String> colCliente;
    @FXML
    private TableColumn<Venta, String> colFecha;
    @FXML
    private TableColumn<Venta, Number> colTotal;
    @FXML
<<<<<<< HEAD
    private Button btnInactivarVenta;
=======
    private TableColumn<Venta, String> colEstado;
    @FXML
    private TextField txtBuscar; // <--- Nuevo
    @FXML
    private ComboBox<String> cbEstado; // <--- Nuevo
>>>>>>> c83e3e5c64f096d3e9bcd33b21bd6f6bcc86a3e2

    private final ObservableList<Venta> allVentas = FXCollections.observableArrayList();
    private FilteredList<Venta> filteredVentas;

    @FXML
    public void initialize() {
<<<<<<< HEAD
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
    private void onBack(ActionEvent event) {
        try {
            Parent mainRoot = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            scene.setRoot(mainRoot);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm());

            try {
                Thread.sleep(500);
                stage.setHeight(600);
                stage.setWidth(1000);
                System.out.println("Ajustando tamaño...");
                stage.setMaximized(true);
                stage.centerOnScreen();

            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            stage.setTitle("Index Blooms – Menú Principal");
        } catch (IOException e) {
            e.printStackTrace();
        }
=======
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
    private void onBack(ActionEvent e) throws IOException {
        Parent main = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
        Stage st = (Stage) btnBack.getScene().getWindow();
        st.getScene().setRoot(main);
        st.setTitle("Index Blooms – Menú Principal");
        //st.setResizable(false);
        //st.sizeToScene();
        st.setMaximized(true);
        st.centerOnScreen();
>>>>>>> c83e3e5c64f096d3e9bcd33b21bd6f6bcc86a3e2
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
<<<<<<< HEAD
        if (c == null) {
            // El usuario canceló la selección
            return;
        }
=======
>>>>>>> c83e3e5c64f096d3e9bcd33b21bd6f6bcc86a3e2

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
<<<<<<< HEAD
=======
                nuevaVenta.setEstado("Activo");
>>>>>>> c83e3e5c64f096d3e9bcd33b21bd6f6bcc86a3e2

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

<<<<<<< HEAD
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
=======
   @FXML
private void onAnularVenta(ActionEvent event) {
    Venta ventaSeleccionada = tableVentas.getSelectionModel().getSelectedItem();
    if (ventaSeleccionada == null) {
        DialogHelper.showWarning(
                btnAnularVenta.getScene().getWindow(),
                "Seleccione una venta para anular.");
        return;
>>>>>>> c83e3e5c64f096d3e9bcd33b21bd6f6bcc86a3e2
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
