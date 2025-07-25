package org.example.sgef_petalex_v_09.controllers;

import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.models.ItemVenta;
import org.example.sgef_petalex_v_09.models.Venta;
import org.example.sgef_petalex_v_09.util.CSVUtil;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class VentaDetailController {
    private Venta currentVenta;
    private boolean ventaAceptada = false;
    private final ObservableList<ItemVenta> items = FXCollections.observableArrayList();

    // Controles de la UI
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

    @FXML
    public void initialize() {
        // Inicializa la venta si no se pasó via initData
        if (currentVenta == null) {
            currentVenta = new Venta();
            currentVenta.setFecha(LocalDate.now());
        }

        setupTableColumns();
        setupEventHandlers();

        tableItems.setItems(items);

        // Si no hay ítems, no se puede aceptar la venta
        btnAcceptVenta.setDisable(true);
        items.addListener((ListChangeListener<ItemVenta>) change -> {
            btnAcceptVenta.setDisable(items.isEmpty());
            updateTotal();
        });
    }

    /**
     * Llamar DESPUÉS de FXMLLoader.load() para inicializar destino y cliente.
     */
    public void initData(String tipoDestino, Cliente cliente) {
        currentVenta = new Venta();
        currentVenta.setTipoDestino(tipoDestino);
        currentVenta.setCliente(cliente.getId());     // asume getId()
        currentVenta.setDireccion(cliente.getDireccion());
        currentVenta.setFecha(LocalDate.now());

        setupVentaInfo();
    }

    private void setupTableColumns() {
        colItem.setCellValueFactory(data -> data.getValue().itemProperty().asObject());
        colVariedad.setCellValueFactory(data -> data.getValue().variedadProperty());
        colPaquete.setCellValueFactory(data -> data.getValue().paqueteProperty());
        colCantidad.setCellValueFactory(data -> data.getValue().cantidadProperty().asObject());
        colPrecioU.setCellValueFactory(data -> data.getValue().precioUnitProperty().asObject());
        colPrecioT.setCellValueFactory(data -> data.getValue().precioTotalProperty().asObject());
    }

    private void setupEventHandlers() {
        // Añadir item
        btnAddItem.setOnAction(evt -> {
            // Lógica para agregar, p.ej. abrir RoseSelection.fxml
            // Al cerrar, retrieve ItemVenta y hacer items.add(nuevoItem);
        });

        // Eliminar item
        btnEliminarItem.setOnAction(evt -> {
            ItemVenta sel = tableItems.getSelectionModel().getSelectedItem();
            if (sel != null) {
                items.remove(sel);
            }
        });

        // Al seleccionar fila, habilitar/eliminar botón
        tableItems.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    btnEliminarItem.setDisable(newSel == null);
                });

        // Cancelar venta = cerrar ventana
        btnCancelVenta.setOnAction(evt -> {
            ventaAceptada = false;
            closeWindow();
        });

        // Aceptar venta = validar y cerrar
        btnAcceptVenta.setOnAction(evt -> {
            if (validarVenta()) {
                ventaAceptada = true;
                closeWindow();
            }
        });
    }

    private void setupVentaInfo() {
        // Carga datos del cliente
        String clienteId = currentVenta.getCliente();
        Cliente cliente = CSVUtil.buscarClientePorId(clienteId);
        if (cliente != null) {
            lblCliente.setText("Cliente: " + cliente.getNombre());
            lblDireccion.setText("Dirección: " + cliente.getDireccion());
            lblTelefono.setText("Teléfono: " + cliente.getTelefono());
            lblCorreo.setText("Correo: " + cliente.getCorreo());
            lblEstadoCliente.getStyleClass().setAll("estado-label",
                    "estado-" + cliente.getEstado().toLowerCase());
            lblEstadoCliente.setText("Estado: " + cliente.getEstado());
        }

        // Fecha y tipo
        lblFecha.setText("Fecha: " +
                currentVenta.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        lblTipoDestino.setText("Tipo: " + currentVenta.getTipoDestino());

        updateTotal();
    }

    private void updateTotal() {
        double subtotal = items.stream()
                .mapToDouble(ItemVenta::getPrecioTotal)
                .sum();
        double iva      = subtotal * 0.12;
        double total    = subtotal + iva;

        // Guarda en la venta
        currentVenta.setSubtotal(subtotal);
        currentVenta.setIva(iva);
        currentVenta.setTotal(total);

        // Actualiza etiqueta
        lblTotal.setText(String.format("Subtotal: $%.2f   IVA: $%.2f   Total: $%.2f",
                subtotal, iva, total));
    }

    private boolean validarVenta() {
        if (items.isEmpty()) {
            DialogHelper.showWarning(getWindow(), "Debe agregar al menos un ítem.");
            return false;
        }
        if (currentVenta.getCliente() == null || currentVenta.getCliente().isEmpty()) {
            DialogHelper.showWarning(getWindow(), "Debe seleccionar un cliente.");
            return false;
        }
        Cliente c = CSVUtil.buscarClientePorId(currentVenta.getCliente());
        if (c == null || "inactivo".equalsIgnoreCase(c.getEstado())) {
            DialogHelper.showWarning(getWindow(),
                    "El cliente no existe o está inactivo.");
            return false;
        }
        return true;
    }

    /**
     * @return Optional.of(currentVenta) si aceptada, Optional.empty() si cancelada.
     */
    public Optional<Venta> getVentaCreated() {
        return ventaAceptada
                ? Optional.of(currentVenta)
                : Optional.empty();
    }

    private void closeWindow() {
        Stage stage = (Stage) lblCliente.getScene().getWindow();
        stage.close();
    }

    private Window getWindow() {
        return lblCliente.getScene().getWindow();
    }

    public void initData(Venta nuevaVenta) {
        currentVenta = nuevaVenta;
        setupVentaInfo();
        items.addAll(currentVenta.getItems());
        updateTotal();
        btnAcceptVenta.setDisable(false);
        ventaAceptada = false;

    }
}
