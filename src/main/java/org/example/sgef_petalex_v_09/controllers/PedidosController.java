package org.example.sgef_petalex_v_09.controllers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.models.Pedido;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.time.LocalDate;
import java.util.Optional;

public class PedidosController {

    @FXML private Button btnNuevo, btnEditar, btnEliminar, btnExportar;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Pedido> tablePedidos;
    @FXML private TableColumn<Pedido, Integer> colId, colTotalProductos;
    @FXML private TableColumn<Pedido, String> colCliente, colEstado, colGuiaAerea;
    @FXML private TableColumn<Pedido, LocalDate> colFechaPedido, colFechaExportacion;
    @FXML private TableColumn<Pedido, Double> colPrecioUnitario;

    private final ObservableList<Pedido> masterData = FXCollections.observableArrayList();
    private FilteredList<Pedido> filteredData;

    @FXML
    public void initialize() {
        // Configuración columnas
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colCliente.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCliente().getNombre()));
        colFechaPedido.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFechaPedido()));
        colFechaExportacion.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFechaEstimadaEnvio()));
        colEstado.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEstado()));
        colGuiaAerea.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCodigoGuiaAerea()));
        colTotalProductos.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getItems().size()));
        colPrecioUnitario.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(0.5)); // temporal

        cbEstado.setItems(FXCollections.observableArrayList("Todos", "En preparación", "Listo para exportar", "Exportado"));
        cbEstado.getSelectionModel().select("Todos");

        // Datos iniciales
        Cliente c = new Cliente(1, "RoseFlower INC.", "Av. Petalos 123", "+593991234567", "ventas@roseflower.ec");
        Pedido p = new Pedido(1, c, LocalDate.now(), LocalDate.now().plusDays(1), "En preparación", "GUIA123");
        p.agregarItem("Lote A - 50 tallos");
        masterData.add(p);

        filteredData = new FilteredList<>(masterData, x -> true);
        tablePedidos.setItems(filteredData);

        tablePedidos.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean has = sel != null;
            btnEditar.setDisable(!has);
            btnEliminar.setDisable(!has);
        });

        txtBuscar.textProperty().addListener((obs, old, val) -> filtrarTabla());
        cbEstado.valueProperty().addListener((obs, old, val) -> filtrarTabla());
    }

    private void filtrarTabla() {
        String filtro = txtBuscar.getText().toLowerCase();
        String estado = cbEstado.getValue();
        filteredData.setPredicate(p -> {
            boolean matchTexto = p.getCliente().getNombre().toLowerCase().contains(filtro) ||
                                 (p.getCodigoGuiaAerea() != null && p.getCodigoGuiaAerea().toLowerCase().contains(filtro));
            boolean matchEstado = estado.equals("Todos") || p.getEstado().equals(estado);
            return matchTexto && matchEstado;
        });
    }

    @FXML
    private void onNuevo() {
        Window owner = btnNuevo.getScene().getWindow();
        Optional<Pedido> res = showPedidoDialog("Registrar Pedido", null);
        res.ifPresent(p -> {
            if (confirm(owner, "¿Deseas registrar este pedido?")) {
                p.setId(generarId());
                masterData.add(p);
                DialogHelper.showSuccess(owner, "registrado");
            }
        });
    }

    @FXML
    private void onEditar() {
        Window owner = btnEditar.getScene().getWindow();
        Pedido sel = tablePedidos.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Optional<Pedido> res = showPedidoDialog("Editar Pedido", sel);
        res.ifPresent(p -> {
            if (confirm(owner, "¿Deseas guardar los cambios?")) {
                sel.setCliente(p.getCliente());
                sel.setFechaPedido(p.getFechaPedido());
                sel.setFechaEstimadaEnvio(p.getFechaEstimadaEnvio());
                sel.setEstado(p.getEstado());
                sel.setCodigoGuiaAerea(p.getCodigoGuiaAerea());
                sel.setItems(p.getItems());
                tablePedidos.refresh();
                DialogHelper.showSuccess(owner, "actualizado");
            }
        });
    }

    @FXML
    private void onEliminar() {
        Window owner = btnEliminar.getScene().getWindow();
        Pedido sel = tablePedidos.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (confirm(owner, "¿Deseas eliminar este pedido?")) {
            masterData.remove(sel);
            DialogHelper.showSuccess(owner, "eliminado");
        }
    }

    @FXML
    private void onExportar() {
        DialogHelper.showSuccess(btnExportar.getScene().getWindow(), "exportado");
    }

    private boolean confirm(Window owner, String msg) {
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.initOwner(owner);
        a.setTitle("Confirmación");
        a.setContentText(msg);
        a.getButtonTypes().setAll(
                new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE),
                new ButtonType("Aceptar", ButtonData.OK_DONE)
        );
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get().getButtonData() == ButtonData.OK_DONE;
    }

    private Optional<Pedido> showPedidoDialog(String title, Pedido existing) {
        Dialog<Pedido> dlg = new Dialog<>();
        dlg.setTitle(title);
        dlg.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE),
                new ButtonType("Aceptar", ButtonData.OK_DONE)
        );

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);

        ComboBox<String> cbCliente = new ComboBox<>();
        cbCliente.setItems(FXCollections.observableArrayList(
                "RoseFlower INC.", "SweetMoment Ltd.", "GlobalRoses GmbH",
                "FlorAmor Co.", "PetalWorld SA"
        ));

        DatePicker dpFechaPedido = new DatePicker(LocalDate.now());
        DatePicker dpFechaExport = new DatePicker(LocalDate.now().plusDays(2));

        ComboBox<String> cbEstadoPedido = new ComboBox<>(FXCollections.observableArrayList("En preparación", "Listo para exportar", "Exportado"));
        TextField txtGuia = new TextField();
        TextField txtItems = new TextField();

        if (existing != null) {
            cbCliente.setValue(existing.getCliente().getNombre());
            dpFechaPedido.setValue(existing.getFechaPedido());
            dpFechaExport.setValue(existing.getFechaEstimadaEnvio());
            cbEstadoPedido.setValue(existing.getEstado());
            txtGuia.setText(existing.getCodigoGuiaAerea());
            txtItems.setText(String.join(",", existing.getItems()));
        }

        grid.addRow(0, new Label("Cliente:"), cbCliente);
        grid.addRow(1, new Label("Fecha Pedido:"), dpFechaPedido);
        grid.addRow(2, new Label("Fecha Exportación:"), dpFechaExport);
        grid.addRow(3, new Label("Estado:"), cbEstadoPedido);
        grid.addRow(4, new Label("Guía Aérea:"), txtGuia);
        grid.addRow(5, new Label("Productos (coma):"), txtItems);

        dlg.getDialogPane().setContent(grid);

        dlg.setResultConverter(bt -> {
            if (bt.getButtonData() == ButtonData.OK_DONE) {
                Cliente cliente = new Cliente(0, cbCliente.getValue(), "", "", "");
                Pedido p = new Pedido();
                p.setCliente(cliente);
                p.setFechaPedido(dpFechaPedido.getValue());
                p.setFechaEstimadaEnvio(dpFechaExport.getValue());
                p.setEstado(cbEstadoPedido.getValue());
                p.setCodigoGuiaAerea(txtGuia.getText());
                for (String item : txtItems.getText().split(",")) {
                    p.agregarItem(item.trim());
                }
                return p;
            }
            return null;
        });

        return dlg.showAndWait();
    }

    private int generarId() {
        return masterData.stream().mapToInt(Pedido::getId).max().orElse(0) + 1;
    }
}
