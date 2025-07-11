package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import org.example.sgef_petalex_v_09.models.Compra;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.time.LocalDate;
import java.util.Optional;

public class ComprasController {

    @FXML private Button btnNuevo, btnEditar, btnEliminar, btnExportar;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Compra> tableCompras;
    @FXML private TableColumn<Compra, Integer> colId;
    @FXML private TableColumn<Compra, String> colProveedor, colRuc, colTipoRosa, colTipoCorte, colUnidad, colEstado;
    @FXML private TableColumn<Compra, Integer> colCantidad;
    @FXML private TableColumn<Compra, Double> colCostoUnitario;
    @FXML private TableColumn<Compra, LocalDate> colFechaCompra;

    private final ObservableList<Compra> masterData = FXCollections.observableArrayList();
    private FilteredList<Compra> filteredData;

    @FXML
    public void initialize() {
        // Configurar columnas
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colProveedor.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getProveedor()));
        colRuc.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getRuc()));
        colTipoRosa.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTipoRosa()));
        colTipoCorte.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTipoCorte()));
        colCantidad.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCantidad()));
        colUnidad.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getUnidad()));
        colCostoUnitario.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getCostoUnitario()));
        colFechaCompra.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getFechaCompra()));
        colEstado.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getEstadoActual()));

        cbEstado.getItems().addAll("Todos", "Recibido", "Hidratado", "En Cuarto Frío", "Empacado", "Exportado");
        cbEstado.getSelectionModel().selectFirst(); // "Todos"

        // Datos de prueba
        masterData.addAll(
                new Compra(1, "Rosas del Azuay", "1791512345001", "Mondial", "Americano", 60.0, 100, "Tallos", 0.45, LocalDate.now(), "Recibido"),
                new Compra(2, "Florícolas Galápagos", "1791512345002", "Freedom", "Ruso", 55.0, 80, "Tallos", 0.5, LocalDate.now().minusDays(1), "Hidratado")
        );

        filteredData = new FilteredList<>(masterData, p -> true);
        tableCompras.setItems(filteredData);

        // Activar botones si hay selección
        tableCompras.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean has = sel != null;
            btnEditar.setDisable(!has);
            btnEliminar.setDisable(!has);
        });

        // Filtros
        txtBuscar.textProperty().addListener((obs, old, val) -> filtrar());
        cbEstado.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> filtrar());
    }

    private void filtrar() {
        String texto = txtBuscar.getText().toLowerCase().trim();
        String estado = cbEstado.getValue();
        filteredData.setPredicate(c -> {
            boolean matchTexto = texto.isEmpty()
                    || c.getProveedor().toLowerCase().contains(texto)
                    || c.getTipoRosa().toLowerCase().contains(texto);
            boolean matchEstado = estado.equals("Todos") || c.getEstadoActual().equals(estado);
            return matchTexto && matchEstado;
        });
    }

    @FXML
    private void onNuevo() {
        Window owner = btnNuevo.getScene().getWindow();
        Optional<Compra> res = showCompraFormDialog("Registrar Compra", null);
        res.ifPresent(c -> {
            if (confirm(owner, "¿Deseas registrar esta compra?")) {
                c.setId(generarId());
                masterData.add(c);
                DialogHelper.showSuccess(owner, "registrado");
            }
        });
    }

    @FXML
    private void onEditar() {
        Window owner = btnEditar.getScene().getWindow();
        Compra sel = tableCompras.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        Optional<Compra> res = showCompraFormDialog("Editar Compra", sel);
        res.ifPresent(c -> {
            if (confirm(owner, "¿Deseas guardar los cambios?")) {
                sel.setProveedor(c.getProveedor());
                sel.setRuc(c.getRuc());
                sel.setTipoRosa(c.getTipoRosa());
                sel.setTipoCorte(c.getTipoCorte());
                sel.setLargoTallo(c.getLargoTallo());
                sel.setCantidad(c.getCantidad());
                sel.setUnidad(c.getUnidad());
                sel.setCostoUnitario(c.getCostoUnitario());
                sel.setFechaCompra(c.getFechaCompra());
                sel.setEstadoActual(c.getEstadoActual());
                tableCompras.refresh();
                DialogHelper.showSuccess(owner, "actualizado");
            }
        });
    }

    @FXML
    private void onEliminar() {
        Window owner = btnEliminar.getScene().getWindow();
        Compra sel = tableCompras.getSelectionModel().getSelectedItem();
        if (sel == null) return;
        if (confirm(owner, "¿Deseas eliminar esta compra?")) {
            masterData.remove(sel);
            DialogHelper.showSuccess(owner, "eliminado");
        }
    }

    @FXML
    private void onExportar() {
        Window owner = btnExportar.getScene().getWindow();
        DialogHelper.showSuccess(owner, "exportado los datos");
    }

    private boolean confirm(Window owner, String msg) {
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.initOwner(owner);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.setTitle("Confirmación");
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get().getButtonData() == ButtonData.OK_DONE;
    }

    /** Dialog para registrar o editar compra */
    private Optional<Compra> showCompraFormDialog(String title, Compra existing) {
        Dialog<Compra> dlg = new Dialog<>();
        dlg.setTitle(title);
        dlg.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE),
                new ButtonType("Aceptar", ButtonData.OK_DONE)
        );

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtProveedor = new TextField();
        TextField txtRuc = new TextField();
        TextField txtRosa = new TextField();
        TextField txtCorte = new TextField();
        Spinner<Integer> spCantidad = new Spinner<>(1, 9999, 100);
        TextField txtUnidad = new TextField("Tallos");
        Spinner<Double> spCosto = new Spinner<>(0.01, 100, 0.5, 0.01);
        DatePicker dpFecha = new DatePicker(LocalDate.now());
        ComboBox<String> cbEstado = new ComboBox<>(FXCollections.observableArrayList("Recibido", "Hidratado", "En Cuarto Frío", "Empacado", "Exportado"));

        if (existing != null) {
            txtProveedor.setText(existing.getProveedor());
            txtRuc.setText(existing.getRuc());
            txtRosa.setText(existing.getTipoRosa());
            txtCorte.setText(existing.getTipoCorte());
            spCantidad.getValueFactory().setValue(existing.getCantidad());
            txtUnidad.setText(existing.getUnidad());
            spCosto.getValueFactory().setValue(existing.getCostoUnitario());
            dpFecha.setValue(existing.getFechaCompra());
            cbEstado.setValue(existing.getEstadoActual());
        }

        grid.addRow(0, new Label("Proveedor:"), txtProveedor);
        grid.addRow(1, new Label("RUC:"), txtRuc);
        grid.addRow(2, new Label("Tipo Rosa:"), txtRosa);
        grid.addRow(3, new Label("Tipo Corte:"), txtCorte);
        grid.addRow(4, new Label("Cantidad:"), spCantidad);
        grid.addRow(5, new Label("Unidad:"), txtUnidad);
        grid.addRow(6, new Label("Costo Unitario:"), spCosto);
        grid.addRow(7, new Label("Fecha Compra:"), dpFecha);
        grid.addRow(8, new Label("Estado:"), cbEstado);

        dlg.getDialogPane().setContent(grid);

        dlg.setResultConverter(bt -> {
            if (bt.getButtonData() == ButtonData.OK_DONE) {
                Compra c = new Compra();
                c.setProveedor(txtProveedor.getText());
                c.setRuc(txtRuc.getText());
                c.setTipoRosa(txtRosa.getText());
                c.setTipoCorte(txtCorte.getText());
                c.setCantidad(spCantidad.getValue());
                c.setUnidad(txtUnidad.getText());
                c.setCostoUnitario(spCosto.getValue());
                c.setFechaCompra(dpFecha.getValue());
                c.setEstadoActual(cbEstado.getValue());
                return c;
            }
            return null;
        });

        return dlg.showAndWait();
    }

    private int generarId() {
        return masterData.stream().mapToInt(Compra::getId).max().orElse(0) + 1;
    }
}
