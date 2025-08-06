package org.example.sgef_petalex_v_09.controllers;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;
import org.example.sgef_petalex_v_09.util.UserSession;

import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.models.Estados;
import org.example.sgef_petalex_v_09.models.ItemVenta;
import org.example.sgef_petalex_v_09.models.Pedido;
import org.example.sgef_petalex_v_09.models.Venta;
import org.example.sgef_petalex_v_09.services.InventarioService;
import org.example.sgef_petalex_v_09.util.CSVUtil;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.validators.DataValidator;
import org.example.sgef_petalex_v_09.validators.ValidationResult;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class PedidosController {
    @FXML
    private TableView<Pedido> tablePedidos;

    @FXML
    private Button btnNuevo, btnActualizarEstado, btnEditar, btnAnular, btnExportar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private TableColumn<Pedido, String> colCliente, colEstado;

    @FXML
    private TableColumn<Pedido, LocalDate> colFechaPedido, colFechaExportacion;

    @FXML
    private TableColumn<Pedido, String> colEmpresaTransporte;

    @FXML
    private TableColumn<Pedido, Integer> colTotalProductos;

    @FXML
    private TableColumn<Pedido, Double> colPrecioTotal;

    @FXML
    private TableColumn<Pedido, String> colFechaModificacion;

    @FXML
    private TableColumn<Pedido, String> colUsuarioModificador;

    private final ObservableList<Pedido> masterData = FXCollections.observableArrayList();
    private FilteredList<Pedido> filteredData;

    @FXML
    public void initialize() {
        configurarColumnas();
        inicializarComboEstado();
        cargarDatosDesdeCSV();
        configurarListeners();
        actualizarEstadoBotones(false);

        tablePedidos.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Pedido item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setStyle("");
                } else if ("Anulado".equalsIgnoreCase(item.getEstadoActual())) {
                    setStyle("-fx-background-color: lightgray;");
                } else {
                    setStyle("");
                }
            }
        });
    }

    private void configurarColumnas() {
        // colId y colIdentificadorEmpresarial no están declaradas en tu controlador,
        // las comento
        // colId.setCellValueFactory(c -> new
        // ReadOnlyObjectWrapper<>(c.getValue().getId()));

        colCliente.setCellValueFactory(c -> {
            Cliente cliente = c.getValue().getCliente();
            return new ReadOnlyStringWrapper(cliente != null ? cliente.getNombre() : "Sin cliente");
        });

        // colIdentificadorEmpresarial.setCellValueFactory(c -> {
        // Cliente cliente = c.getValue().getCliente();
        // return new ReadOnlyStringWrapper(cliente != null ?
        // cliente.getIdentificadorEmpresarial() : "N/A");
        // });

        colEmpresaTransporte.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                Optional.ofNullable(c.getValue().getEmpresaTransporte()).orElse("")));

        colFechaPedido.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFechaPedido()));

        colFechaExportacion.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFechaEstimadaEnvio()));

        colEstado.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEstadoActual()));

        // colGuiaAerea no está declarado, así que se comenta esta línea
        // colGuiaAerea.setCellValueFactory(c -> new ReadOnlyStringWrapper(
        // Optional.ofNullable(c.getValue().getCodigoGuiaAerea()).orElse("")));

        colTotalProductos.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCantidadTotalRosas()));

        colPrecioTotal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPrecioTotal()));

        colFechaModificacion.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                Optional.ofNullable(c.getValue().getFechaModificacion())
                        .map(d -> d.toString().replace("T", " "))
                        .orElse("")));

        colUsuarioModificador.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                Optional.ofNullable(c.getValue().getUsuarioModificacion()).orElse("")));
    }

    private void inicializarComboEstado() {
        cbEstado.getItems().clear();
        cbEstado.getItems().add("Todos");
        cbEstado.getItems().addAll(Estados.ESTADOS_PEDIDO);
        cbEstado.getSelectionModel().selectFirst();
    }

    private void cargarDatosDesdeCSV() {
        masterData.clear();
        List<Pedido> pedidos = CSVUtil.leerPedidos(); // Carga los pedidos del CSV
        masterData.addAll(pedidos);

        filteredData = new FilteredList<>(masterData, p -> true);
        tablePedidos.setItems(filteredData);
    }

    private void configurarListeners() {
        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> filtrarTabla());
        cbEstado.valueProperty().addListener((obs, oldVal, newVal) -> filtrarTabla());

        tablePedidos.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            actualizarEstadoBotones(newSel != null);
        });
    }

    private void actualizarEstadoBotones(boolean haySeleccion) {
        btnEditar.setDisable(!haySeleccion);
        btnAnular.setDisable(!haySeleccion);
        btnExportar.setDisable(!haySeleccion);
        btnActualizarEstado.setDisable(true);

        if (haySeleccion) {
            Pedido seleccionado = tablePedidos.getSelectionModel().getSelectedItem();
            String estado = seleccionado.getEstadoActual();

            boolean puedeActualizar = !estado.equals("Anulado") && !estado.equals("Exportado");
            btnActualizarEstado.setDisable(!puedeActualizar);
            btnExportar.setDisable(!estado.equals("Empacado"));
            btnAnular.setDisable(estado.equals("Anulado"));
        }
    }

    private void filtrarTabla() {
        String filtroTexto = Optional.ofNullable(txtBuscar.getText()).orElse("").toLowerCase().trim();
        String filtroEstado = Optional.ofNullable(cbEstado.getValue()).orElse("Todos");

        filteredData.setPredicate(p -> {
            boolean coincideTexto = (p.getCliente() != null
                    && p.getCliente().getNombre().toLowerCase().contains(filtroTexto)) ||
                    (p.getCodigoGuiaAerea() != null && p.getCodigoGuiaAerea().toLowerCase().contains(filtroTexto));

            boolean coincideEstado = filtroEstado.equals("Todos") || p.getEstadoActual().equals(filtroEstado);
            return coincideTexto && coincideEstado;
        });
    }

    @FXML
    private void onNuevo() {
        try {
            crearPedidoDesdeCliente();
        } catch (IOException e) {
            DialogHelper.showError(btnNuevo.getScene().getWindow(), "Error al crear pedido");
        }
    }

    @FXML
    private void onActualizarEstado() {
        Pedido seleccionado = tablePedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null)
            return;

        String actual = seleccionado.getEstadoActual();
        int index = Estados.ESTADOS_PEDIDO.indexOf(actual);

        if (index < 0 || index >= Estados.ESTADOS_PEDIDO.size() - 1) {
            DialogHelper.showWarning(btnActualizarEstado.getScene().getWindow(),
                    "Este pedido ya está en su estado final.");
            return;
        }

        String nuevo = Estados.ESTADOS_PEDIDO.get(index + 1);
        seleccionado.setEstadoActual(nuevo);
        seleccionado.setUsuarioModificacion(UserSession.getUsuarioActual().getUsuario());
        seleccionado.setFechaModificacion(LocalDateTime.now());

        tablePedidos.refresh();
        actualizarEstadoBotones(true);
        pasarPedidoAVentasSiEsExportado(seleccionado);
        CSVUtil.guardarPedidos(masterData); // <-- Guardar aquí

        DialogHelper.showSuccess(btnActualizarEstado.getScene().getWindow(),
                "Actualizado el estado del pedido a: " + nuevo);
    }

    @FXML
    private void onEditar() {
        Pedido pedido = tablePedidos.getSelectionModel().getSelectedItem();
        if (pedido == null)
            return;

        showPedidoEditSimpleDialog("Editar Pedido", pedido)
                .ifPresent(p -> {
                    pedido.setFechaEstimadaEnvio(p.getFechaEstimadaEnvio());
                    pedido.setCodigoGuiaAerea(p.getCodigoGuiaAerea());
                    pedido.setUsuarioResponsable(UserSession.getUsuarioActual().getUsuario());
                    pedido.setUsuarioModificacion(UserSession.getUsuarioActual().getUsuario());
                    pedido.setFechaModificacion(LocalDateTime.now());
                    tablePedidos.refresh();

                    CSVUtil.guardarPedidos(masterData); // <-- Guardar aquí

                    DialogHelper.showSuccess(btnEditar.getScene().getWindow(), "Actualizado el pedido");
                });
    }

    private Optional<Pedido> showPedidoEditSimpleDialog(String title, Pedido existing) {
        Dialog<Pedido> dlg = new Dialog<>();
        dlg.setTitle(title);
        dlg.setHeaderText(null);

        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(btnCancelar, btnAceptar);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        DatePicker dpFechaExportacion = new DatePicker(existing.getFechaEstimadaEnvio());
        TextField txtGuiaAerea = new TextField(existing.getCodigoGuiaAerea());
        txtGuiaAerea.setPromptText("Ejemplo: AA123456"); // Ejemplo genérico

        grid.addRow(0, new Label("Fecha de Exportación:"), dpFechaExportacion);
        grid.addRow(1, new Label("Código Guía Aérea:"), txtGuiaAerea);

        dlg.getDialogPane().setContent(grid);

        Button okButton = (Button) dlg.getDialogPane().lookupButton(btnAceptar);
        okButton.setDisable(true);

        // Utilizar AtomicBoolean para permitir modificaciones dentro del listener
        AtomicBoolean hasErrors = new AtomicBoolean(false);

        txtGuiaAerea.textProperty().addListener((obs, oldVal, newVal) -> {
            ValidationResult result = DataValidator.validateIATAWaybill(newVal.trim());
            if (!result.isValid()) {
                setErrorStyle(txtGuiaAerea, result.getErrorMessage());
                hasErrors.set(true);
                okButton.setDisable(true);
            } else {
                clearErrorStyle(txtGuiaAerea);
                hasErrors.set(false);
                okButton.setDisable(false);
            }
        });

        dlg.setResultConverter(button -> {
            if (button == btnAceptar) {
                if (hasErrors.get()) {
                    DialogHelper.showError(dlg.getDialogPane().getScene().getWindow(),
                            "Error al actualizar pedido, corrige los campos resaltados (mantén el cursor sobre el campo para más información)");
                    return null;
                }
                existing.setFechaEstimadaEnvio(dpFechaExportacion.getValue());
                existing.setCodigoGuiaAerea(txtGuiaAerea.getText().trim());

                return existing;
            }
            return null;
        });

        return dlg.showAndWait();
    }

    @FXML
    private void onAnular() {
        Pedido pedido = tablePedidos.getSelectionModel().getSelectedItem();
        if (pedido == null)
            return;

        if (DialogHelper.confirm(btnAnular.getScene().getWindow(), "¿Anular el pedido seleccionado?")) {
            pedido.setEstadoActual("Anulado");
            pedido.setUsuarioResponsable(UserSession.getUsuarioActual().getUsuario());
            pedido.setUsuarioModificacion(UserSession.getUsuarioActual().getUsuario());
            pedido.setFechaModificacion(LocalDateTime.now());

            try {
                devolverUnidadesAlStock(pedido);
            } catch (IOException ex) {
                DialogHelper.showError(btnAnular.getScene().getWindow(), "Error al actualizar inventario");
                ex.printStackTrace();
            }

            tablePedidos.refresh();
            actualizarEstadoBotones(true);

            CSVUtil.guardarPedidos(masterData); // <-- Guardar aquí

            DialogHelper.showSuccess(btnAnular.getScene().getWindow(), "Anulado el pedido");
        }
    }

    private void devolverUnidadesAlStock(Pedido pedido) throws IOException {
        for (ItemVenta item : pedido.getItemsVenta()) {
            InventarioService.agregarUnidades(item.getVariedad(), item.getLargo(), item.getCantidad());
        }
    }

    

    @FXML
    private void onExportar() {
        Pedido pedido = tablePedidos.getSelectionModel().getSelectedItem();
        if (pedido == null)
            return;

        if (DialogHelper.confirm(btnExportar.getScene().getWindow(), "¿Confirmar exportación del pedido?")) {
            pedido.setEstadoActual("Exportado");
            tablePedidos.refresh();
            actualizarEstadoBotones(true);

            CSVUtil.guardarPedidos(masterData); // <-- Guardar aquí

            DialogHelper.showSuccess(btnExportar.getScene().getWindow(), "Exportado el pedido");
        }
    }

    private void pasarPedidoAVentasSiEsExportado(Pedido pedido) {
        if (!"Exportado".equalsIgnoreCase(pedido.getEstadoActual())) {
            return;
        }

        List<Venta> ventas = CSVUtil.leerVentas();
        boolean yaExiste = ventas.stream()
                .anyMatch(v -> pedido.getCliente().getNombre().equals(v.getCliente()) &&
                        v.getServicio().equals("Pedido Exportado") &&
                        v.getFecha().equals(LocalDate.now()));
        if (yaExiste) {
            return;
        }
        Venta venta = new Venta();
        venta.setId("V" + String.format("%03d", ventas.size() + 1));
        venta.setTipoDestino("Internacional");
        venta.setCliente(pedido.getCliente());
        venta.setFecha(LocalDate.now());
        venta.servicioProperty().set("Pedido Exportado");

        // ASIGNAMOS EL USUARIO ACTUAL EN LA SESIÓN, NO EL DEL PEDIDO
        venta.setUsuarioResponsable(UserSession.getUsuarioActual().getUsuario());

        for (ItemVenta item : pedido.getItemsVenta()) {
            venta.addItem(item);
        }

        ventas.add(venta);
        CSVUtil.guardarVentas(ventas, CSVUtil.VENTAS_CSV);
    }

    private void crearPedidoDesdeCliente() throws IOException {
        FXMLLoader selLoader = new FXMLLoader(getClass().getResource("/fxml/ClienteSelection.fxml"));
        Parent selRoot = selLoader.load();
        ClienteSelectionController selCtrl = selLoader.getController();

        Stage selStage = new Stage();
        selStage.initOwner(btnNuevo.getScene().getWindow());
        selStage.initModality(Modality.APPLICATION_MODAL);
        selStage.setScene(new Scene(selRoot));
        selStage.setTitle("Seleccionar Cliente");
        selStage.showAndWait();
        Cliente cliente = selCtrl.getClienteSeleccionado().orElse(null);
        if (cliente == null)
            return;
        boolean confirmado = DialogHelper.confirm(btnNuevo.getScene().getWindow(),
                "¿Desea registrar un pedido para " + cliente.getNombre() + "?");
        if (!confirmado)
            return;

        Pedido nuevo = new Pedido();
        nuevo.setId(generarId());
        nuevo.setCliente(cliente);
        nuevo.setFechaPedido(LocalDate.now());
        nuevo.setFechaEstimadaEnvio(LocalDate.now().plusDays(2));
        nuevo.setEstadoActual("En Cuarto Frío");
        nuevo.setCodigoGuiaAerea("");
        nuevo.setFechaCreacion(LocalDateTime.now());
        nuevo.setFechaModificacion(LocalDateTime.now());
        nuevo.setUsuarioResponsable(UserSession.getUsuarioActual().getUsuario());
        nuevo.setUsuarioModificacion(UserSession.getUsuarioActual().getUsuario());
        FXMLLoader detLoader = new FXMLLoader(getClass().getResource("/fxml/PedidoDetail.fxml"));
        Parent detRoot = detLoader.load();
        PedidoDetailController detCtrl = detLoader.getController();
        detCtrl.initData(nuevo);

        Stage detStage = new Stage();
        detStage.initOwner(btnNuevo.getScene().getWindow());
        detStage.initModality(Modality.APPLICATION_MODAL);
        detStage.setScene(new Scene(detRoot));
        detStage.setTitle("Nuevo Pedido");
        detStage.showAndWait();

        detCtrl.getPedidoResult().ifPresent(pedidoGuardado -> {
            masterData.add(pedidoGuardado);
            tablePedidos.refresh();

            CSVUtil.guardarPedidos(masterData); // <-- Guardar aquí

            DialogHelper.showSuccess(btnNuevo.getScene().getWindow(), "Registrado el pedido");
        });
    }

    private int generarId() {
        return masterData.stream().mapToInt(Pedido::getId).max().orElse(0) + 1;
    }

    private void setErrorStyle(Control control, String message) {
        Platform.runLater(() -> {
            control.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            Tooltip tooltip = new Tooltip(
                    "Error, corrige los campos resaltados (mantén el cursor sobre el campo para más información)\n"
                            + message);
            tooltip.setStyle("-fx-background-color: #ffdddd; -fx-text-fill: red;");
            control.setTooltip(tooltip);
        });
    }

    private void clearErrorStyle(Control control) {
        Platform.runLater(() -> {
            control.setStyle(null);
            control.setTooltip(null);
        });
    }

}
