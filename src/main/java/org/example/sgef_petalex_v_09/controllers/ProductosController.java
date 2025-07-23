package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Producto;
import org.example.sgef_petalex_v_09.models.Proveedor;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.time.LocalDate;
import java.util.Optional;

public class ProductosController {

    @FXML
    private TableView<Producto> tableProductos;
    @FXML
    private TableColumn<Producto, String> colId;
    @FXML
    private TableColumn<Producto, String> colNombre;
    @FXML
    private TableColumn<Producto, String> colColor;
    @FXML
    private TableColumn<Producto, String> colUnidadMedida;
    @FXML
    private TableColumn<Producto, String> colEstado;
    @FXML
    private TableColumn<Producto, String> colProveedor;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnCambiarEstado;

    private final ObservableList<Producto> masterData = FXCollections.observableArrayList();
    private FilteredList<Producto> filteredData;
    private final ObservableList<Proveedor> proveedoresDisponibles = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colUnidadMedida.setCellValueFactory(new PropertyValueFactory<>("unidad_medida"));
        colEstado.setCellValueFactory(
                cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstadoTexto()));
        colProveedor.setCellValueFactory(new PropertyValueFactory<>("proveedorRuc"));

        // Configurar ComboBox de estado
        cbEstado.getItems().addAll("Todos", "Activo", "Inactivo");
        cbEstado.getSelectionModel().selectFirst();

        // Configurar botones inicialmente deshabilitados
        btnActualizar.setDisable(true);
        btnCambiarEstado.setDisable(true);

        // Listener para habilitar botones cuando se selecciona un producto
        tableProductos.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    boolean hasSelection = newSel != null;
                    btnActualizar.setDisable(!hasSelection);
                    btnCambiarEstado.setDisable(!hasSelection);
                });

        // Cargar datos de ejemplo
        cargarProveedoresEjemplo();
        cargarDatosEjemplo();

        // Configurar filtrado
        filteredData = new FilteredList<>(masterData, p -> true);
        tableProductos.setItems(filteredData);

        // Listeners para filtrado dinámico
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
        cbEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        String filtroTexto = txtBuscar.getText() != null ? txtBuscar.getText().toLowerCase().trim() : "";
        String estadoSeleccionado = cbEstado.getValue();

        filteredData.setPredicate(producto -> {
            if (producto == null)
                return false;

            boolean coincideTexto = filtroTexto.isEmpty()
                    || (producto.getId() != null && producto.getId().toLowerCase().contains(filtroTexto))
                    || (producto.getNombre() != null && producto.getNombre().toLowerCase().contains(filtroTexto))
                    || (producto.getColor() != null && producto.getColor().toLowerCase().contains(filtroTexto))
                    || (producto.getUnidad_medida() != null
                            && producto.getUnidad_medida().toLowerCase().contains(filtroTexto))
                    || (producto.getProveedorRuc() != null
                            && producto.getProveedorRuc().toLowerCase().contains(filtroTexto));

            boolean coincideEstado = estadoSeleccionado == null || estadoSeleccionado.equals("Todos")
                    || producto.getEstadoTexto().equals(estadoSeleccionado);

            return coincideTexto && coincideEstado;
        });
    }

    @FXML
    private void onNuevo(ActionEvent event) {
        Optional<Producto> resultado = mostrarFormularioProducto("Nuevo Producto", null);

        if (resultado.isPresent()) {
            Producto nuevoProducto = resultado.get();
            if (DialogHelper.confirm(getWindow(), "¿Está seguro que desea agregar este producto?")) {
                masterData.add(nuevoProducto);
                DialogHelper.showSuccess(getWindow(), "Producto agregado exitosamente");
            }
        }
    }

    @FXML
    private void onActualizar(ActionEvent event) {
        Producto seleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            DialogHelper.showWarning(getWindow(), "Debe seleccionar un producto para actualizar");
            return;
        }

        Optional<Producto> resultado = mostrarFormularioProducto("Actualizar Producto", seleccionado);

        if (resultado.isPresent()) {
            if (DialogHelper.confirm(getWindow(), "¿Está seguro que desea actualizar este producto?")) {
                tableProductos.refresh();
                DialogHelper.showSuccess(getWindow(), "Producto actualizado exitosamente");
            }
        }
    }

    @FXML
    private void onCambiarEstado(ActionEvent event) {
        Producto seleccionado = tableProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            DialogHelper.showWarning(getWindow(), "Debe seleccionar un producto para cambiar su estado");
            return;
        }

        String estadoActual = seleccionado.getEstadoTexto();
        String nuevoEstado = estadoActual.equals("Activo") ? "Inactivo" : "Activo";
        String accion = nuevoEstado.equals("Activo") ? "activar" : "inactivar";

        String mensaje = String.format("¿Está seguro que desea %s el producto '%s'?",
                accion, seleccionado.getNombre());

        if (DialogHelper.confirm(getWindow(), mensaje)) {
            seleccionado.setEstadoTexto(nuevoEstado);
            tableProductos.refresh();
            DialogHelper.showSuccess(getWindow(),
                    String.format("Producto %s exitosamente",
                            nuevoEstado.equals("Activo") ? "activado" : "inactivado"));
        }
    }

    private Optional<Producto> mostrarFormularioProducto(String titulo, Producto productoExistente) {
        Dialog<Producto> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE),
                new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE));

        // Crear formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos del formulario
        TextField txtId = new TextField();
        txtId.setPromptText("ID del producto (ej: P001)");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del producto");
        ComboBox<String> cbUnidadMedida = new ComboBox<>();
        cbUnidadMedida.getItems().addAll("Caja", "Ramo", "Tallo", "Paquete");
        cbUnidadMedida.setPromptText("Seleccione unidad");
        TextField txtColor = new TextField();
        txtColor.setPromptText("Color del producto");
        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción del producto");
        txtDescripcion.setPrefRowCount(3);
        TextField txtCantidad = new TextField();
        txtCantidad.setPromptText("Ej: 12 tallos, 1 ramo");
        TextField txtCostoCompra = new TextField();
        txtCostoCompra.setPromptText("0.00");
        TextField txtCostoFlete = new TextField();
        txtCostoFlete.setPromptText("0.00");
        TextField txtTasaIva = new TextField();
        txtTasaIva.setPromptText("12.0");
        DatePicker dpFechaCompra = new DatePicker();
        dpFechaCompra.setPromptText("Fecha de compra");
        DatePicker dpFechaVencimiento = new DatePicker();
        dpFechaVencimiento.setPromptText("Fecha de vencimiento");
        ComboBox<String> cbProveedor = new ComboBox<>();
        cbProveedor.setPromptText("Seleccione proveedor");

        // Llenar ComboBox de proveedores
        cbProveedor.getItems().clear();
        for (Proveedor proveedor : proveedoresDisponibles) {
            cbProveedor.getItems().add(proveedor.getRuc() + " - " + proveedor.getNombre());
        }

        // Si es edición, llenar campos
        if (productoExistente != null) {
            txtId.setText(productoExistente.getId());
            txtId.setDisable(true); // No permitir cambiar ID en edición
            txtNombre.setText(productoExistente.getNombre());
            cbUnidadMedida.setValue(productoExistente.getUnidad_medida());
            txtColor.setText(productoExistente.getColor());
            txtDescripcion.setText(productoExistente.getDescripcion());
            txtCantidad.setText(productoExistente.getCantidad());
            txtCostoCompra.setText(String.valueOf(productoExistente.getCosto_unitario_compra()));
            txtCostoFlete.setText(String.valueOf(productoExistente.getCosto_flete_unitario()));
            txtTasaIva.setText(String.valueOf(productoExistente.getTasa_iva()));
            dpFechaCompra.setValue(productoExistente.getFecha_compra());
            dpFechaVencimiento.setValue(productoExistente.getFecha_vencimiento());

            // Seleccionar proveedor
            String proveedorRuc = productoExistente.getProveedorRuc();
            for (String item : cbProveedor.getItems()) {
                if (item.startsWith(proveedorRuc)) {
                    cbProveedor.setValue(item);
                    break;
                }
            }
        } else {
            // Valores por defecto para nuevo producto
            txtTasaIva.setText("12.0");
            dpFechaCompra.setValue(LocalDate.now());
            dpFechaVencimiento.setValue(LocalDate.now().plusDays(30));
        }

        // Agregar campos al grid
        grid.add(new Label("ID:"), 0, 0);
        grid.add(txtId, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(new Label("Unidad de medida:"), 0, 2);
        grid.add(cbUnidadMedida, 1, 2);
        grid.add(new Label("Color:"), 0, 3);
        grid.add(txtColor, 1, 3);
        grid.add(new Label("Descripción:"), 0, 4);
        grid.add(txtDescripcion, 1, 4);
        grid.add(new Label("Cantidad por paquete:"), 0, 5);
        grid.add(txtCantidad, 1, 5);
        grid.add(new Label("Costo unitario compra ($):"), 0, 6);
        grid.add(txtCostoCompra, 1, 6);
        grid.add(new Label("Costo flete unitario ($):"), 0, 7);
        grid.add(txtCostoFlete, 1, 7);
        grid.add(new Label("Tasa IVA (%):"), 0, 8);
        grid.add(txtTasaIva, 1, 8);
        grid.add(new Label("Fecha compra:"), 0, 9);
        grid.add(dpFechaCompra, 1, 9);
        grid.add(new Label("Fecha vencimiento:"), 0, 10);
        grid.add(dpFechaVencimiento, 1, 10);
        grid.add(new Label("Proveedor:"), 0, 11);
        grid.add(cbProveedor, 1, 11);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado con validaciones mejoradas
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                try {
                    // ✅ Validaciones mejoradas
                    String idProducto = txtId.getText() != null ? txtId.getText().trim() : "";
                    if (idProducto.isEmpty()) {
                        DialogHelper.showError(getWindow(), "El ID del producto es obligatorio");
                        return null;
                    }

                    // Validar ID único solo para productos nuevos
                    if (productoExistente == null) {
                        for (Producto p : masterData) {
                            if (p.getId().equals(idProducto)) {
                                DialogHelper.showError(getWindow(), "Ya existe un producto con este ID");
                                return null;
                            }
                        }
                    }

                    String nombreProducto = txtNombre.getText() != null ? txtNombre.getText().trim() : "";
                    if (nombreProducto.isEmpty()) {
                        DialogHelper.showError(getWindow(), "El nombre del producto es obligatorio");
                        return null;
                    }

                    if (cbUnidadMedida.getValue() == null) {
                        DialogHelper.showError(getWindow(), "Debe seleccionar una unidad de medida");
                        return null;
                    }

                    if (cbProveedor.getValue() == null) {
                        DialogHelper.showError(getWindow(), "Debe seleccionar un proveedor");
                        return null;
                    }

                    if (dpFechaCompra.getValue() == null) {
                        DialogHelper.showError(getWindow(), "La fecha de compra es obligatoria");
                        return null;
                    }

                    if (dpFechaVencimiento.getValue() == null) {
                        DialogHelper.showError(getWindow(), "La fecha de vencimiento es obligatoria");
                        return null;
                    }

                    // ✅ Validación de fechas mejorada
                    LocalDate fechaCompra = dpFechaCompra.getValue();
                    LocalDate fechaVencimiento = dpFechaVencimiento.getValue();

                    if (fechaVencimiento.isBefore(fechaCompra)) {
                        DialogHelper.showError(getWindow(),
                                "La fecha de vencimiento debe ser posterior a la fecha de compra");
                        return null;
                    }

                    if (fechaCompra.isAfter(LocalDate.now())) {
                        DialogHelper.showError(getWindow(),
                                "La fecha de compra no puede ser futura");
                        return null;
                    }

                    // ✅ Validar que el proveedor existe
                    String proveedorSeleccionado = cbProveedor.getValue();
                    String proveedorRuc = proveedorSeleccionado.split(" - ")[0];

                    boolean proveedorExiste = proveedoresDisponibles.stream()
                            .anyMatch(p -> p.getRuc().equals(proveedorRuc));
                    if (!proveedorExiste) {
                        DialogHelper.showError(getWindow(), "El proveedor seleccionado no es válido");
                        return null;
                    }

                    // ✅ Validar campos numéricos con mejor manejo
                    double costoCompra, costoFlete, tasaIva;

                    try {
                        String costoCompraText = txtCostoCompra.getText().trim();
                        if (costoCompraText.isEmpty())
                            costoCompraText = "0.0";
                        costoCompra = Double.parseDouble(costoCompraText);

                        String costoFleteText = txtCostoFlete.getText().trim();
                        if (costoFleteText.isEmpty())
                            costoFleteText = "0.0";
                        costoFlete = Double.parseDouble(costoFleteText);

                        String tasaIvaText = txtTasaIva.getText().trim();
                        if (tasaIvaText.isEmpty())
                            tasaIvaText = "12.0";
                        tasaIva = Double.parseDouble(tasaIvaText);

                    } catch (NumberFormatException e) {
                        DialogHelper.showError(getWindow(),
                                "Los valores de costos y tasa de IVA deben ser números válidos");
                        return null;
                    }

                    if (costoCompra < 0 || costoFlete < 0) {
                        DialogHelper.showError(getWindow(),
                                "Los costos no pueden ser negativos");
                        return null;
                    }

                    if (tasaIva < 0 || tasaIva > 100) {
                        DialogHelper.showError(getWindow(),
                                "La tasa de IVA debe estar entre 0 y 100");
                        return null;
                    }

                    // ✅ Crear o actualizar producto
                    if (productoExistente != null) {
                        // Actualizar producto existente
                        productoExistente.setNombre(nombreProducto);
                        productoExistente.setUnidad_medida(cbUnidadMedida.getValue());
                        productoExistente.setColor(txtColor.getText() != null ? txtColor.getText().trim() : "");
                        productoExistente.setDescripcion(
                                txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "");
                        productoExistente
                                .setCantidad(txtCantidad.getText() != null ? txtCantidad.getText().trim() : "");
                        productoExistente.setCosto_unitario_compra(costoCompra);
                        productoExistente.setCosto_flete_unitario(costoFlete);
                        productoExistente.setTasa_iva(tasaIva);
                        productoExistente.setFecha_compra(fechaCompra);
                        productoExistente.setFecha_vencimiento(fechaVencimiento);
                        productoExistente.setProveedorRuc(proveedorRuc);
                        return productoExistente;
                    } else {
                        // Crear nuevo producto
                        return new Producto(
                                idProducto,
                                nombreProducto,
                                cbUnidadMedida.getValue(),
                                txtColor.getText() != null ? txtColor.getText().trim() : "",
                                txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "",
                                txtCantidad.getText() != null ? txtCantidad.getText().trim() : "",
                                costoCompra,
                                costoFlete,
                                tasaIva,
                                0, // Estado activo por defecto
                                fechaCompra,
                                fechaVencimiento,
                                proveedorRuc);
                    }
                } catch (Exception e) {
                    DialogHelper.showError(getWindow(), "Error inesperado: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private void cargarDatosEjemplo() {
        masterData.addAll(
                new Producto("P001", "Rosa Roja Premium", "Tallo", "Rojo", "Rosa roja de exportación premium",
                        "12 tallos",
                        1.50, 0.25, 12.0, 0, LocalDate.now().minusDays(5),
                        LocalDate.now().plusDays(10), "1790123456001"),
                new Producto("P002", "Rosa Blanca Freedom", "Tallo", "Blanco", "Rosa blanca variedad Freedom",
                        "12 tallos",
                        1.75, 0.25, 12.0, 0, LocalDate.now().minusDays(3),
                        LocalDate.now().plusDays(12), "1790123456001"),
                new Producto("P003", "Gypsophila Million Star", "Ramo", "Blanco",
                        "Gypsophila para complementar arreglos", "1 ramo",
                        0.80, 0.15, 12.0, 0, LocalDate.now().minusDays(2),
                        LocalDate.now().plusDays(8), "1790234567001"),
                new Producto("P004", "Alstroemeria Variada", "Caja", "Multicolor", "Alstroemeria en colores variados",
                        "20 tallos",
                        2.20, 0.30, 12.0, 0, LocalDate.now().minusDays(1),
                        LocalDate.now().plusDays(15), "1790345678001"),
                new Producto("P005", "Clavel Standard", "Paquete", "Rosa", "Clavel rosa producción nacional",
                        "15 tallos",
                        1.20, 0.20, 12.0, 1, LocalDate.now().minusDays(10),
                        LocalDate.now().minusDays(2), "1790456789001"));
    }

    private void cargarProveedoresEjemplo() {
        proveedoresDisponibles.addAll(
                new Proveedor("1790123456001", "FloriPetals", "FloriPetals S.A.", "02-2345678",
                        "Av. de las Rosas 123, Quito", "1234567890", "contacto@floripetalsa.com",
                        LocalDate.now().minusMonths(6), "Activo"),
                new Proveedor("1790234567001", "Roses Export", "Roses Export Ecuador Cia. Ltda.", "02-3456789",
                        "Calle Flores 456, Cayambe", "2345678901", "ventas@rosesexport.com",
                        LocalDate.now().minusMonths(4), "Activo"),
                new Proveedor("1790345678001", "Garden Flowers", "Garden Flowers Cia. Ltda.", "02-4567890",
                        "Av. Floresta 789, Tabacundo", "3456789012", "info@gardenflowers.ec",
                        LocalDate.now().minusMonths(8), "Activo"),
                new Proveedor("1790456789001", "EcuaFlores", "EcuaFlores Internacional S.A.", "02-5678901",
                        "Km 25 Vía Cayambe, Cayambe", "4567890123", "admin@ecuaflores.com",
                        LocalDate.now().minusMonths(12), "Activo"),
                new Proveedor("1790567890001", "Premium Blooms", "Premium Blooms del Ecuador S.A.", "02-6789012",
                        "Sector La Esperanza, Pedro Moncayo", "5678901234", "premium@blooms.ec",
                        LocalDate.now().minusMonths(2), "Activo"));
    }

    private Window getWindow() {
        return tableProductos != null && tableProductos.getScene() != null ? tableProductos.getScene().getWindow()
                : null;
    }

    public ObservableList<Producto> getProductos() {
        return masterData;
    }

    public void setProveedoresDisponibles(ObservableList<Proveedor> proveedores) {
        this.proveedoresDisponibles.clear();
        this.proveedoresDisponibles.addAll(proveedores);
    }
}