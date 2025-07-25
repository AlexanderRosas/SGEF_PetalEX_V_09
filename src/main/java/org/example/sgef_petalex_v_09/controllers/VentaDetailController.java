package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.ItemVenta;
import org.example.sgef_petalex_v_09.models.Venta;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class VentaDetailController implements Initializable {

    private Venta currentVenta;
    private boolean ventaAceptada = false;
    private final ObservableList<ItemVenta> items = FXCollections.observableArrayList();

    // —— FXML controls ——
    @FXML private Label lblCliente;
    @FXML private Label lblDireccion;
    @FXML private Label lblTelefono;
    @FXML private Label lblCorreo;
    @FXML private Label lblFecha;
    @FXML private Label lblTipoDestino;
    @FXML private Label lblEstadoCliente;
    @FXML private Label lblTotal;

    @FXML private TableView<ItemVenta> tableItems;
    @FXML private TableColumn<ItemVenta, Integer> colItem;
    @FXML private TableColumn<ItemVenta, String>  colVariedad;
    @FXML private TableColumn<ItemVenta, String>  colPaquete;
    @FXML private TableColumn<ItemVenta, Integer> colCantidad;
    @FXML private TableColumn<ItemVenta, Double>  colPrecioU;
    @FXML private TableColumn<ItemVenta, Double>  colPrecioT;

    @FXML private Button btnAddItem;
    @FXML private Button btnEliminarItem;
    @FXML private Button btnCancelVenta;
    @FXML private Button btnAcceptVenta;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Si no se llamó a initData, creamos una venta vacía
        if (currentVenta == null) {
            currentVenta = new Venta();
            currentVenta.setFecha(LocalDate.now());
        }

        // Configurar columnas con PropertyValueFactory
        colItem    .setCellValueFactory(new PropertyValueFactory<>("item"));
        colVariedad.setCellValueFactory(new PropertyValueFactory<>("variedad"));
        colPaquete .setCellValueFactory(new PropertyValueFactory<>("paquete"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colPrecioU .setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecioT .setCellValueFactory(new PropertyValueFactory<>("precioTotal"));

        // Carga la lista de ítems en la tabla
        tableItems.setItems(items);

        // Configurar handlers de botones
        btnAddItem      .setOnAction(this::onAddItem);
        btnEliminarItem .setOnAction(this::onEliminarItem);
        btnCancelVenta  .setOnAction(this::onCancelVenta);
        btnAcceptVenta  .setOnAction(this::onAcceptVenta);

        // Inicialmente deshabilitar Eliminar y Aceptar
        btnEliminarItem.setDisable(true);
        btnAcceptVenta .setDisable(true);

        // Activar Eliminar cuando haya una selección
        tableItems.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSel, newSel) -> btnEliminarItem.setDisable(newSel == null)
        );

        // Cada vez que cambian los ítems, recálculo totales y habilito/deshabilito Aceptar
        items.addListener((ListChangeListener<ItemVenta>) change -> {
            updateTotal();
            btnAcceptVenta.setDisable(items.isEmpty());
        });
    }

    /**
     * Inicializa este controller con una venta ya existente,
     * mostrando sus datos en pantalla.
     */
    public void initData(Venta venta) {
        this.currentVenta = venta;
        setupVentaInfo();
        items.setAll(venta.getItems());
        updateTotal();
        btnAcceptVenta.setDisable(items.isEmpty());
    }

    /** Rellena los labels de información del cliente y la venta. */
    private void setupVentaInfo() {
        lblCliente     .setText("Cliente: " + currentVenta.getId());
        lblDireccion   .setText("Dirección: " + currentVenta.getDireccion());
        lblTelefono    .setText(""); // o info si la tienes
        lblCorreo      .setText(""); // o info si la tienes
        lblFecha       .setText("Fecha: " +
                currentVenta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblTipoDestino .setText("Tipo: " + currentVenta.getTipoDestino());
        lblEstadoCliente.setText("Estado: " + currentVenta.getEstado());
    }

    /** Recalcula subtotal, IVA quemado y total, y actualiza el label. */
    private void updateTotal() {
        double subtotal = items.stream()
                .mapToDouble(ItemVenta::getPrecioTotal)
                .sum();
        double iva   = 0.0;       // IVA siempre cero
        double total = subtotal;  // total = subtotal + iva

        currentVenta.setPrecio(subtotal);
        currentVenta.setIva(iva);
        currentVenta.setTotal(total);

        lblTotal.setText(String.format(
                "Subtotal: $%.2f   IVA: $%.2f   Total: $%.2f",
                subtotal, iva, total));
    }

    /** Validaciones antes de aceptar la venta. */
    private boolean validarVenta() {
        if (items.isEmpty()) {
            DialogHelper.showWarning(getWindow(), "Debe agregar al menos un ítem.");
            return false;
        }
        if (currentVenta.getCliente() == null || currentVenta.getCliente().isEmpty()) {
            DialogHelper.showWarning(getWindow(), "Debe seleccionar un cliente.");
            return false;
        }
        return true;
    }

    /** Abre el diálogo/modo de agregar un nuevo ItemVenta. */
    @FXML
    private void onAddItem(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RoseSelection.fxml"));
            Parent pop = loader.load(); // Carga nuevo nodo raíz cada vez

            Stage dlg = new Stage();
            dlg.initOwner(lblCliente.getScene().getWindow());
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setTitle("Elegir variedad");

            Scene scene = new Scene(pop, 600, 600); // Tamaño más grande
            dlg.setScene(scene);

            dlg.showAndWait();

            String variedad = ((RoseSelectionController) loader.getController()).getSelectedRose();
            if (variedad == null)
                return;

            ItemVenta newItem = showPaqueteCantidadDialog(variedad);
            if (newItem != null) {
                newItem.setItem(items.size() + 1);
                items.add(newItem);
                updateTotal();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private ItemVenta showPaqueteCantidadDialog(String variedad) {
        Dialog<ItemVenta> dlg = new Dialog<>();
        dlg.setTitle("Configurar ítem");

        ButtonType cancelType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType acceptType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(cancelType, acceptType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label lblVar = new Label("Variedad: " + variedad);
        ComboBox<String> cbPack = new ComboBox<>(FXCollections.observableArrayList(
                "Caja Tabaco", "Caja Full", "Cuartos"));
        cbPack.setPromptText("Paquete");
        Spinner<Integer> spQty = new Spinner<>(1, 999, 1);
        spQty.setEditable(true);

        grid.add(lblVar, 0, 0, 2, 1);
        grid.add(new Label("Paquete:"), 0, 1);
        grid.add(cbPack, 1, 1);
        grid.add(new Label("Cantidad:"), 0, 2);
        grid.add(spQty, 1, 2);

        dlg.getDialogPane().setContent(grid);

        // Después de obtener okBtn:
        Button okBtn = (Button) dlg.getDialogPane().lookupButton(acceptType);
        okBtn.setDisable(true);

        javafx.beans.value.ChangeListener<Object> validar = (obs, oldV, newV) -> {
            boolean paqueteSeleccionado = cbPack.getValue() != null;
            boolean cantidadValida = spQty.getValue() != null && spQty.getValue() > 0;
            okBtn.setDisable(!(paqueteSeleccionado && cantidadValida));
        };
        cbPack.valueProperty().addListener(validar);
        spQty.valueProperty().addListener(validar);

        dlg.setResultConverter(btn -> {
            if (btn == acceptType) {
                ItemVenta it = new ItemVenta();
                it.setVariedad(variedad);
                it.setPaquete(cbPack.getValue());
                it.setCantidad(spQty.getValue());
                // define precios de ejemplo
                double pu = switch (cbPack.getValue()) {
                    case "Caja Tabaco" -> 50.0;
                    case "Caja Full" -> 120.0;
                    default -> 30.0;
                };
                it.setPrecioUnit(pu);
                it.setPrecioTotal(pu * spQty.getValue());
                return it;
            }
            return null;
        });

        Optional<ItemVenta> res = dlg.showAndWait();
        if (res.isPresent() && DialogHelper.confirm(
                lblCliente.getScene().getWindow(),
                "¿Está seguro que desea agregar este ítem?")) {
            return res.get();
        }
        return null;
    }

    /** Elimina el item seleccionado de la lista. */
    @FXML
    private void onEliminarItem(ActionEvent event) {
        ItemVenta sel = tableItems.getSelectionModel().getSelectedItem();
        if (sel != null) {
            items.remove(sel);
        }
    }

    /** Cancela la venta y cierra la ventana sin guardar. */
    @FXML
    private void onCancelVenta(ActionEvent event) {
        ventaAceptada = false;
        closeWindow();
    }

    /** Acepta la venta (si pasa validación) y cierra la ventana. */
    @FXML
    private void onAcceptVenta(ActionEvent event) {
        if (validarVenta()) {
            ventaAceptada = true;
            closeWindow();
        }
    }

    /**
     * Devuelve Optional.of(currentVenta) si el usuario aceptó,
     * o Optional.empty() si canceló.
     */
    public Optional<Venta> getVentaCreated() {
        return ventaAceptada
                ? Optional.of(currentVenta)
                : Optional.empty();
    }

    /** Cierra esta ventana modal. */
    private void closeWindow() {
        Stage stage = (Stage) lblCliente.getScene().getWindow();
        stage.close();
    }

    /** Para centrar diálogos. */
    private Window getWindow() {
        return lblCliente.getScene().getWindow();
    }
}
