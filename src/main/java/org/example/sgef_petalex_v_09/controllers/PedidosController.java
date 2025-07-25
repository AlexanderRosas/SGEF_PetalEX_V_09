package org.example.sgef_petalex_v_09.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.models.Estados;
import org.example.sgef_petalex_v_09.models.Pedido;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class PedidosController {

    @FXML
    private Button btnNuevo, btnActualizarEstado, btnEditar, btnAnular, btnExportar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private TableView<Pedido> tablePedidos;
    @FXML
    private TableColumn<Pedido, Integer> colId, colTotalProductos;
    @FXML
    private TableColumn<Pedido, String> colCliente, colEstado, colGuiaAerea;
    @FXML
    private TableColumn<Pedido, LocalDate> colFechaPedido, colFechaExportacion;
    @FXML
    private TableColumn<Pedido, Double> colPrecioTotal;

    private final ObservableList<Pedido> masterData = FXCollections.observableArrayList();
    private FilteredList<Pedido> filteredData;

    @FXML
    public void initialize() {
        configurarColumnas();
        inicializarComboEstado();
        cargarDatosQuemados();  // ahora sin DAO
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
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colCliente.setCellValueFactory(c -> {
            Cliente cliente = c.getValue().getCliente();
            return new ReadOnlyStringWrapper(cliente != null ? cliente.getNombre() : "Sin cliente");
        });
        colFechaPedido.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFechaPedido()));
        colFechaExportacion.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFechaEstimadaEnvio()));
        colEstado.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEstadoActual()));
        colGuiaAerea.setCellValueFactory(c -> new ReadOnlyStringWrapper(
                Optional.ofNullable(c.getValue().getCodigoGuiaAerea()).orElse("")));
        colTotalProductos.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getItems().size()));
        colPrecioTotal.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getPrecioTotal()));
    }

    private void inicializarComboEstado() {
        cbEstado.getItems().clear();
        cbEstado.getItems().add("Todos");
        cbEstado.getItems().addAll(Estados.ESTADOS_PEDIDO);
        cbEstado.getSelectionModel().selectFirst();
    }

    private void cargarDatosQuemados() {
        masterData.clear();
        masterData.addAll(
                new Pedido(1, new Cliente(1234567890, "RoseFlower INC.", "Av. Pétalos 123", "0991234567", "rosa@rose.com", "Activo"),
                        LocalDate.now().minusDays(3), LocalDate.now().plusDays(2), "En Cuarto Frío", ""),
                new Pedido(2, new Cliente(987654321, "SweetMoment Ltd.", "Calle Tulipán 45", "0987654321", "sm@moment.com", "Activo"),
                        LocalDate.now().minusDays(1), LocalDate.now().plusDays(3), "Empacado", "GA-001")
        );
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
        tablePedidos.refresh();
        actualizarEstadoBotones(true);

        DialogHelper.showSuccess(btnActualizarEstado.getScene().getWindow(),
                "actualizado el Estado del pedido a: " + nuevo);
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
                    tablePedidos.refresh();
                    DialogHelper.showSuccess(btnEditar.getScene().getWindow(), "actualizado el pedido");
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

        grid.addRow(0, new Label("Fecha de Exportación:"), dpFechaExportacion);
        grid.addRow(1, new Label("Código Guía Aérea:"), txtGuiaAerea);

        dlg.getDialogPane().setContent(grid);

        dlg.setResultConverter(button -> {
            if (button == btnAceptar) {
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
            tablePedidos.refresh();
            actualizarEstadoBotones(true);
            DialogHelper.showSuccess(btnAnular.getScene().getWindow(), "anulado el pedido");
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
            DialogHelper.showSuccess(btnExportar.getScene().getWindow(), "exportado el pedido");
        }
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

        Cliente cliente = selCtrl.getSelectedCliente();
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
        nuevo.agregarItem("Producto sin definir");

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
            DialogHelper.showSuccess(btnNuevo.getScene().getWindow(), "registrado el pedido");
        });
    }

    private int generarId() {
        return masterData.stream().mapToInt(Pedido::getId).max().orElse(0) + 1;
    }
}
