package org.example.sgef_petalex_v_09.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.example.sgef_petalex_v_09.models.Compra;
import org.example.sgef_petalex_v_09.models.Estados;
import org.example.sgef_petalex_v_09.models.Proveedor;
import org.example.sgef_petalex_v_09.models.Rosa;
import org.example.sgef_petalex_v_09.services.InventarioService;
import org.example.sgef_petalex_v_09.util.CSVUtil;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.util.UserSession;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

public class ComprasController {

    @FXML
    private Button btnNuevo, btnActualizar, btnInactivar, btnExportar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private TableView<Compra> tableCompras;
    @FXML
    private TableColumn<Compra, Integer> colId;
    @FXML
    private TableColumn<Compra, String> colProveedor, colRuc, colTipoRosa, colTipoCorte, colEstado;
    @FXML
    private TableColumn<Compra, Integer> colCantidad;
    @FXML
    private TableColumn<Compra, Double> colCostoUnitario, colTotal;
    @FXML
    private TableColumn<Compra, LocalDate> colFechaCompra;
    @FXML
    private TableColumn<Compra, LocalDate> colFechaActualizacion;
    @FXML
    private TableColumn<Compra, String> colUsuarioResponsable;

    @FXML
    private DatePicker dpFechaDesde, dpFechaHasta;
    private ObservableList<Compra> masterData;
    private FilteredList<Compra> filteredData;
    private ObservableList<Proveedor> proveedoresReales = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        proveedoresReales.setAll(CSVUtil.leerProveedores());
        masterData = cargarComprasDesdeCSV("data/compras.csv");
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colProveedor.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getProveedor() != null ? c.getValue().getProveedor().getNombre() : ""));
        colRuc.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getProveedor() != null ? c.getValue().getProveedor().getRuc() : ""));

        colTipoRosa.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTipoRosa()));
        colTipoCorte.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTipoCorte()));
        colCantidad.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCantidad()));
        colCostoUnitario.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCostoUnitario()));
        colTotal.setCellValueFactory(
                c -> new ReadOnlyObjectWrapper<>(c.getValue().getCantidad() * c.getValue().getCostoUnitario()));
        colFechaCompra.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFechaCompra()));
        colFechaActualizacion
                .setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFechaUltimaActualizacion()));
        colUsuarioResponsable.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getUsuarioResponsable() != null ? c.getValue().getUsuarioResponsable() : ""));
        colEstado.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                c.getValue().getEstadoActual() != null ? c.getValue().getEstadoActual() : ""));

        colTotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                setText(empty || total == null ? null : String.format("$ %.2f", total));
            }
        });

        cbEstado.getItems().add("Todos");
        cbEstado.getItems().addAll(Estados.ESTADOS_COMPRA);
        cbEstado.getSelectionModel().selectFirst();

        dpFechaDesde.valueProperty().addListener((obs, old, val) -> filtrar());
        dpFechaHasta.valueProperty().addListener((obs, old, val) -> filtrar());

        filteredData = new FilteredList<>(masterData, p -> true);

        tableCompras.setItems(filteredData);
        btnExportar.disableProperty().bind(
                Bindings.isEmpty(filteredData));

        tableCompras.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, sel) -> actualizarEstadoBotones(sel));
        btnActualizar.setDisable(true);
        btnInactivar.setDisable(true);

        txtBuscar.textProperty().addListener((obs, old, val) -> filtrar());
        cbEstado.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> filtrar());

        filtrar();
    }

    private void actualizarEstadoBotones(Compra compra) {
        if (compra == null) {
            btnActualizar.setDisable(true);
            btnInactivar.setDisable(true);
            return;
        }
        String estado = compra.getEstadoActual() != null ? compra.getEstadoActual().trim() : "";
        btnActualizar.setDisable(estado.equalsIgnoreCase("Devuelta") || estado.equalsIgnoreCase("En Cuarto Frío"));
        btnInactivar.setDisable(estado.equalsIgnoreCase("Devuelta"));
    }

    private void filtrar() {
        String texto = txtBuscar.getText().toLowerCase().trim();
        String estadoFiltro = cbEstado.getValue();
        LocalDate desde = dpFechaDesde.getValue();
        LocalDate hasta = dpFechaHasta.getValue();

        filteredData.setPredicate(c -> {
            boolean matchTexto = texto.isEmpty()
                    || (c.getProveedor() != null && (c.getProveedor().getNombre().toLowerCase().contains(texto) ||
                            c.getProveedor().getRuc().toLowerCase().contains(texto)))
                    || c.getTipoRosa().toLowerCase().contains(texto);

            boolean matchEstado = estadoFiltro.equals("Todos") || c.getEstadoActual().equals(estadoFiltro);
            boolean matchFecha = true;
            if (desde != null && hasta != null) {
                matchFecha = (!c.getFechaCompra().isBefore(desde)) && (!c.getFechaCompra().isAfter(hasta));
            } else if (desde != null) {
                matchFecha = !c.getFechaCompra().isBefore(desde);
            } else if (hasta != null) {
                matchFecha = !c.getFechaCompra().isAfter(hasta);
            }
            return matchTexto && matchEstado && matchFecha;
        });

        tableCompras.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Compra item, boolean empty) {
                super.updateItem(item, empty);
                setStyle((item != null && "Devuelta".equalsIgnoreCase(item.getEstadoActual()))
                        ? "-fx-background-color: lightgray;"
                        : "");
            }
        });
    }

    @FXML
    private void onNuevo() {
        Window owner = btnNuevo.getScene().getWindow();
        Optional<Compra> resultado = showCompraFormDialog("Registrar Compra", null);
        resultado.ifPresent(c -> {
            if (c.getCostoUnitario() <= 0) {
                DialogHelper.showWarning(owner, "El costo unitario debe ser mayor que cero.");
                return;
            }
            if (confirm(owner, "¿Deseas registrar esta compra?")) {
                c.setId(masterData.stream().mapToInt(Compra::getId).max().orElse(0) + 1);
                c.setEstadoActual("Recibida");
                c.setFechaUltimaActualizacion(LocalDate.now());
                c.setUsuarioResponsable(UserSession.getUsuarioActual().getUsuario());

                masterData.add(c);
                CSVUtil.guardarCompras(masterData); // ✅ Guardar en CSV

                try {
                    InventarioService.agregarUnidades(c.getTipoRosa(), c.getTipoCorte(), c.getCantidad());
                } catch (IOException e) {
                    DialogHelper.showError(owner, "Error al actualizar inventario: " + e.getMessage());
                }

                DialogHelper.showSuccess(owner, "registrado la compra y actualizado inventario");
            }
        });
    }

    @FXML
    private void onActualizar() {
        Window owner = btnActualizar.getScene().getWindow();
        Compra seleccionado = tableCompras.getSelectionModel().getSelectedItem();
        if (seleccionado == null)
            return;
        String estado = seleccionado.getEstadoActual().trim();
        if (estado.equalsIgnoreCase("Devuelta") || estado.equalsIgnoreCase("En Cuarto Frío"))
            return;

        ObservableList<String> estados = Estados.ESTADOS_COMPRA;
        int index = estados.indexOf(estado);
        if (index >= 0 && index < estados.size() - 1) {
            String nuevoEstado = estados.get(index + 1);
            seleccionado.setFechaUltimaActualizacion(LocalDate.now());
            seleccionado.setUsuarioResponsable(UserSession.getUsuarioActual().getUsuario());
            CSVUtil.guardarCompras(masterData);
            seleccionado.setEstadoActual(nuevoEstado);
            tableCompras.refresh();
            DialogHelper.showSuccess(owner, "actualizado el estado de la compra a \"" + nuevoEstado + "\"");
            actualizarEstadoBotones(seleccionado);
        }
    }

    @FXML
    private void onInactivar() {
        Window owner = btnInactivar.getScene().getWindow();
        Compra seleccionado = tableCompras.getSelectionModel().getSelectedItem();
        if (seleccionado != null && confirm(owner, "¿Está seguro/a de devolver esta compra?")) {
            seleccionado.setEstadoActual("Devuelta");

            try {
                boolean exito = InventarioService.consumirUnidades(
                        seleccionado.getTipoRosa(),
                        seleccionado.getTipoCorte(),
                        seleccionado.getCantidad());

                if (!exito) {
                    DialogHelper.showWarning(owner,
                            "No hay suficientes unidades en inventario para devolver esta compra.");
                }
            } catch (IOException e) {
                DialogHelper.showError(owner, "Error al actualizar inventario: " + e.getMessage());
            }
            seleccionado.setFechaUltimaActualizacion(LocalDate.now());
            seleccionado.setUsuarioResponsable(UserSession.getUsuarioActual().getUsuario());
            CSVUtil.guardarCompras(masterData);
            tableCompras.refresh();
            DialogHelper.showSuccess(owner, "inactivado la compra y actualizado inventario");
            actualizarEstadoBotones(seleccionado);
            tableCompras.getSelectionModel().clearSelection();
            tableCompras.getSelectionModel().select(seleccionado);
        }
    }

    @FXML
    private void onExportar() {
        Window owner = btnExportar.getScene().getWindow();
        DialogHelper.showSuccess(owner, "exportado la lista de compras");
    }

    private boolean confirm(Window owner, String msg) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(owner);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.setTitle("Confirmación");
        Optional<ButtonType> respuesta = alert.showAndWait();
        return respuesta.isPresent() && respuesta.get().getButtonData() == ButtonData.OK_DONE;
    };

    private Optional<Compra> showCompraFormDialog(String title, Compra existing) {
        boolean esEdicion = existing != null;

        Dialog<Compra> dlg = new Dialog<>();
        dlg.setTitle(title);
        dlg.setHeaderText(null);

        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(btnCancelar, btnAceptar);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        ObservableList<Proveedor> proveedoresActivos = proveedoresReales.filtered(p -> "Activo".equals(p.getEstado()));

        ComboBox<Proveedor> cbProveedor = new ComboBox<>(proveedoresActivos);
        cbProveedor.setCellFactory(lv -> new ListCell<Proveedor>() {
            @Override
            protected void updateItem(Proveedor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cbProveedor.setButtonCell(new ListCell<Proveedor>() {
            @Override
            protected void updateItem(Proveedor item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNombre());
            }
        });
        cbProveedor.setPromptText("Selecciona proveedor");

        ComboBox<Rosa.TipoRosa> cbTipoRosa = new ComboBox<>(FXCollections.observableArrayList(Rosa.TipoRosa.values()));
        cbTipoRosa.setPromptText("Selecciona tipo rosa");

        TextField tfPrecioUnitario = new TextField();
        tfPrecioUnitario.setPromptText("Precio unitario (c/IVA)");
        tfPrecioUnitario.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                tfPrecioUnitario.setText(oldVal);
            }
        });

        ComboBox<Rosa.TipoCorte> cbTipoCorte = new ComboBox<>(
                FXCollections.observableArrayList(Rosa.TipoCorte.values()));
        cbTipoCorte.setPromptText("Selecciona tipo corte");

        TextField tfCantidad = new TextField();
        tfCantidad.setPromptText("Cantidad");
        tfCantidad.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                tfCantidad.setText(oldVal);
            }
        });

        DatePicker dpFecha = new DatePicker(LocalDate.now());

        // Campos que se vuelven visibles solo cuando se selecciona tipo rosa
        List<Node> camposDependientes = List.of(
                new Label("Precio Unitario (c/IVA):"), tfPrecioUnitario,
                new Label("Tipo Corte:"), cbTipoCorte,
                new Label("Cantidad:"), tfCantidad,
                new Label("Fecha Compra:"), dpFecha);

        camposDependientes.forEach(n -> n.setVisible(false));

        Runnable actualizarPrecioPorProveedorYTipoRosa = () -> {
            Proveedor proveedor = cbProveedor.getValue();
            Rosa.TipoRosa tipoRosa = cbTipoRosa.getValue();
            if (proveedor != null && tipoRosa != null) {
                Double precioBase = proveedor.getPrecioTipoRosa(tipoRosa);
                if (precioBase != null) {
                    double precioConIVA = Math.round(precioBase * 1.15 * 100.0) / 100.0;
                    tfPrecioUnitario.setText(String.valueOf(precioConIVA));
                } else {
                    tfPrecioUnitario.setText("0.00");
                }
            } else {
                tfPrecioUnitario.setText("0.00");
            }
        };

        cbTipoRosa.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean mostrar = newVal != null;
            camposDependientes.forEach(n -> n.setVisible(mostrar));
            actualizarPrecioPorProveedorYTipoRosa.run();
        });

        cbProveedor.valueProperty().addListener((obs, oldVal, newVal) -> actualizarPrecioPorProveedorYTipoRosa.run());

        if (esEdicion) {
            proveedoresActivos.stream()
                    .filter(p -> p.getNombre().equals(existing.getProveedor()))
                    .findFirst()
                    .ifPresent(cbProveedor::setValue);
            cbTipoRosa.setValue(Rosa.TipoRosa.valueOf(existing.getTipoRosa().toUpperCase()));
            cbTipoCorte.setValue(Rosa.TipoCorte.valueOf(existing.getTipoCorte().toUpperCase()));
            tfCantidad.setText(String.valueOf(existing.getCantidad()));
            tfPrecioUnitario.setText(String.valueOf(existing.getCostoUnitario()));
            dpFecha.setValue(existing.getFechaCompra());
            camposDependientes.forEach(n -> n.setVisible(true));
        }

        grid.addRow(0, new Label("Proveedor:"), cbProveedor);
        grid.addRow(1, new Label("Tipo Rosa:"), cbTipoRosa);
        grid.addRow(2, new Label("Precio Unitario (c/IVA):"), tfPrecioUnitario);
        grid.addRow(3, new Label("Tipo Corte:"), cbTipoCorte);
        grid.addRow(4, new Label("Cantidad:"), tfCantidad);
        grid.addRow(5, new Label("Fecha Compra:"), dpFecha);
        dlg.getDialogPane().setContent(grid);

        // Obtener botones
        Button btnAceptarButton = (Button) dlg.getDialogPane().lookupButton(btnAceptar);
        Button btnCancelarButton = (Button) dlg.getDialogPane().lookupButton(btnCancelar);

        // Binding para habilitar/deshabilitar el botón Aceptar según campos
        // obligatorios
        btnAceptarButton.disableProperty().bind(
                cbProveedor.valueProperty().isNull());
        btnAceptarButton.addEventFilter(ActionEvent.ACTION, event -> {
            if (cbProveedor.getValue() == null ||
                    cbTipoRosa.getValue() == null ||
                    cbTipoCorte.getValue() == null ||
                    tfCantidad.getText().isEmpty() ||
                    tfPrecioUnitario.getText().isEmpty() ||
                    dpFecha.getValue() == null) {

                DialogHelper.showWarning(dlg.getOwner(), "Por favor, complete todos los campos obligatorios.");
                event.consume();
            }
        });
        // Listener para mostrar confirmación al cancelar
        btnCancelarButton.addEventFilter(ActionEvent.ACTION, event -> {
            event.consume(); // Cancelamos el cierre para mostrar confirmación primero

            Window owner = dlg.getDialogPane().getScene().getWindow();
            boolean confirmar = DialogHelper.confirm(owner,
                    "¿Está seguro/a de cancelar el registro de esta compra?");

            if (confirmar) {
                dlg.setResult(null); // Cierra el diálogo sin resultado (equivale a cancelar)
                dlg.close();
            }
            // si no confirma, no se cierra el diálogo y se queda abierto
        });

        dlg.setResultConverter(button -> {
            if (button == btnAceptar) {
                Compra c = new Compra();
                c.setId(0);
                c.setProveedor(cbProveedor.getValue());
                c.setRuc(cbProveedor.getValue().getRuc());
                c.setTipoRosa(cbTipoRosa.getValue().name());
                c.setTipoCorte(cbTipoCorte.getValue().name());
                c.setLargoTallo(0.0); // Campo faltante
                c.setCantidad(Integer.parseInt(tfCantidad.getText()));
                c.setCantidadDisponible(Integer.parseInt(tfCantidad.getText())); // Campo faltante
                c.setCostoUnitario(Double.parseDouble(tfPrecioUnitario.getText()));
                c.setPrecioUnitario(Double.parseDouble(tfPrecioUnitario.getText())); // Campo faltante
                c.setPrecioTotal(c.getCantidad() * c.getPrecioUnitario()); // Campo faltante
                c.setFechaCompra(dpFecha.getValue());
                c.setEstadoActual("Recibida");
                c.setFechaHidratacion(null); // Campo faltante
                c.setFechaCuartoFrio(null); // Campo faltante
                c.setFechaEmpaque(null); // Campo faltante
                c.setFechaExportacion(null); // Campo faltante
                c.setObservaciones(""); // Campo faltante
                c.setFechaUltimaActualizacion(LocalDate.now());
                c.setUsuarioResponsable(UserSession.getUsuarioActual().getUsuario());
                return c;
            }
            return null;
        });

        return dlg.showAndWait();
    }

    private ObservableList<Compra> cargarComprasDesdeCSV(String ruta) {
        return FXCollections.observableArrayList(CSVUtil.leerCompras());
    }

}
