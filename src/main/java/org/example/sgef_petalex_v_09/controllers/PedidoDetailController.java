package org.example.sgef_petalex_v_09.controllers;

import org.example.sgef_petalex_v_09.util.UserSession;
import org.example.sgef_petalex_v_09.models.Usuario;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.example.sgef_petalex_v_09.models.Estados;
import org.example.sgef_petalex_v_09.models.ItemVenta;
import org.example.sgef_petalex_v_09.models.Pedido;
import org.example.sgef_petalex_v_09.services.InventarioService;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.validators.DataValidator;
import org.example.sgef_petalex_v_09.validators.ValidationResult;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class PedidoDetailController {

    @FXML
    private Label lblCliente;
    @FXML
    private Label lblTotal;
    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnRemoveItem;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnAccept;

    @FXML
    private TableView<ItemVenta> tableItems;
    @FXML
    private TableColumn<ItemVenta, Integer> colItem;
    @FXML
    private TableColumn<ItemVenta, String> colVariedad;
    @FXML
    private TableColumn<ItemVenta, String> colLargo;
    @FXML
    private TableColumn<ItemVenta, String> colPaquete;
    @FXML
    private TableColumn<ItemVenta, Integer> colCantidad;
    @FXML
    private TableColumn<ItemVenta, Double> colPrecioU;
    @FXML
    private TableColumn<ItemVenta, Double> colPrecioT;

    @FXML
    private DatePicker dpFechaPedido;
    @FXML
    private DatePicker dpFechaExport;
    @FXML
    private ComboBox<String> cbEstadoPedido;
    @FXML
    private TextField txtGuiaAerea;
    @FXML
    private TextField txtEmpresaTransporte; // Nuevo campo

    private boolean pedidoAceptado = false;
    private Pedido currentPedido;
    private final ObservableList<ItemVenta> items = FXCollections.observableArrayList();

    public void initData(Pedido pedido) {
        this.currentPedido = pedido;

        // Asignar fecha actual si es nuevo
        if (pedido.getFechaPedido() == null) {
            pedido.setFechaPedido(LocalDate.now());
        }

        setup();
        items.setAll(pedido.getItemsVenta());
        actualizarTotalLabel();
    }

    private void setup() {
        dpFechaPedido.setDisable(true);
        btnAccept.setDisable(items.isEmpty());
        tableItems.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            btnRemoveItem.setDisable(newSel == null);
        });
        btnRemoveItem.setDisable(true); // Inicia deshabilitado

        items.addListener((javafx.collections.ListChangeListener.Change<? extends ItemVenta> c) -> {
            btnAccept.setDisable(items.isEmpty());
            actualizarTotalLabel();
        });

        colItem.setCellValueFactory(i -> i.getValue().itemProperty().asObject());
        colVariedad.setCellValueFactory(i -> i.getValue().variedadProperty());
        colLargo.setCellValueFactory(i -> i.getValue().largoProperty());
        colPaquete.setCellValueFactory(i -> i.getValue().paqueteProperty());
        colCantidad.setCellValueFactory(i -> i.getValue().cantidadProperty().asObject());
        colPrecioU.setCellValueFactory(i -> i.getValue().precioUnitProperty().asObject());
        colPrecioT.setCellValueFactory(i -> i.getValue().precioTotalProperty().asObject());

        tableItems.setItems(items);

        btnAddItem.setOnAction(this::onAddItem);
        btnRemoveItem.setOnAction(this::onRemoveItem);
        btnCancel.setOnAction(this::onCancel);
        btnAccept.setOnAction(this::onAccept);

        lblCliente.setText("Cliente: " + currentPedido.getCliente().getNombre());

        cbEstadoPedido.setItems(Estados.ESTADOS_PEDIDO.filtered(e -> !e.equalsIgnoreCase("Anulado")));
        cbEstadoPedido.getSelectionModel().select(currentPedido.getEstadoActual());

        txtGuiaAerea.setText(currentPedido.getCodigoGuiaAerea());
        dpFechaPedido.setValue(currentPedido.getFechaPedido());
        dpFechaExport.setValue(currentPedido.getFechaEstimadaEnvio());

        txtEmpresaTransporte
                .setText(currentPedido.getEmpresaTransporte() != null ? currentPedido.getEmpresaTransporte() : "");
    }

    private void actualizarTotalLabel() {
        double total = items.stream().mapToDouble(ItemVenta::getPrecioTotal).sum();
        lblTotal.setText(String.format("Total: $ %.2f", total));
    }

    private void closeWindow() {
        ((Stage) lblCliente.getScene().getWindow()).close();
    }

    public Optional<Pedido> getPedidoResult() {
        if (pedidoAceptado) {
            currentPedido.getItemsVenta().clear();
            currentPedido.getItemsVenta().addAll(items);
            currentPedido.setCodigoGuiaAerea(txtGuiaAerea.getText());
            currentPedido.setEstadoActual(cbEstadoPedido.getValue());
            currentPedido.setFechaPedido(LocalDate.now());
            currentPedido.setFechaEstimadaEnvio(dpFechaExport.getValue());
            currentPedido.setEmpresaTransporte(txtEmpresaTransporte.getText().trim());
            return Optional.of(currentPedido);
        }
        return Optional.empty();
    }

    @FXML
    private void onAddItem(ActionEvent e) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RoseSelection.fxml"));
            Parent root = loader.load();

            RoseSelectionController controller = loader.getController();
            controller.setModoOperacion(RoseSelectionController.ModoOperacion.AGREGAR_A_PEDIDO);

            Stage dialog = new Stage();
            dialog.initOwner(lblCliente.getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Seleccionar variedad de rosa");
            dialog.setScene(new Scene(root, 600, 600));
            dialog.showAndWait();

            ItemVenta nuevoItem = controller.getItemSeleccionado();
            if (nuevoItem != null) {
                nuevoItem.setItem(items.size() + 1);
                items.add(nuevoItem);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            DialogHelper.showError(lblCliente.getScene().getWindow(), "Error al cargar selección de rosas.");
        }
    }

    public ItemVenta showPaqueteCantidadDialog(String variedad) {
        Dialog<ItemVenta> dlg = new Dialog<>();
        dlg.setTitle("Configurar ítem");

        ButtonType cancelType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType acceptType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(cancelType, acceptType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label lblVar = new Label("Variedad: " + variedad);
        ComboBox<String> cbLargo = new ComboBox<>(FXCollections.observableArrayList("AMERICANO", "RUSO"));
        cbLargo.setPromptText("Selecciona tipo de corte");

        ComboBox<String> cbPack = new ComboBox<>(FXCollections.observableArrayList(
                "Caja Tabaco", "Caja Full", "Cuartos"));
        cbPack.setPromptText("Selecciona paquete");

        Spinner<Integer> spQty = new Spinner<>(1, 999, 1);
        spQty.setEditable(true);

        grid.add(lblVar, 0, 0, 2, 1);
        grid.add(new Label("Tipo de Corte:"), 0, 1);
        grid.add(cbLargo, 1, 1);
        grid.add(new Label("Paquete:"), 0, 2);
        grid.add(cbPack, 1, 2);
        grid.add(new Label("Cantidad:"), 0, 3);
        grid.add(spQty, 1, 3);

        dlg.getDialogPane().setContent(grid);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(acceptType);
        okBtn.setDisable(true);

        javafx.beans.value.ChangeListener<Object> validador = (obs, oldV, newV) -> {
            boolean esValido = cbLargo.getValue() != null &&
                    cbPack.getValue() != null &&
                    spQty.getValue() != null && spQty.getValue() > 0;
            okBtn.setDisable(!esValido);
        };

        cbLargo.valueProperty().addListener(validador);
        cbPack.valueProperty().addListener(validador);
        spQty.valueProperty().addListener(validador);

        dlg.setResultConverter(btn -> {
            if (btn == acceptType) {
                ItemVenta it = new ItemVenta();
                it.setVariedad(variedad);
                it.setLargo(cbLargo.getValue());
                it.setPaquete(cbPack.getValue());
                it.setCantidad(spQty.getValue());

                double base = switch (cbPack.getValue()) {
                    case "Caja Tabaco" -> 50.0;
                    case "Caja Full" -> 120.0;
                    default -> 30.0;
                };
                if ("RUSO".equals(cbLargo.getValue()))
                    base += 10.0;

                it.setPrecioUnit(base);
                it.setPrecioTotal(base * spQty.getValue());
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

    @FXML
    private void onRemoveItem(ActionEvent e) {
        ItemVenta seleccionado = tableItems.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            DialogHelper.showWarning(lblCliente.getScene().getWindow(),
                    "Debe seleccionar un ítem para eliminar.");
            return;
        }
        boolean confirmar = DialogHelper.confirm(lblCliente.getScene().getWindow(),
                "¿Está seguro que desea eliminar el ítem seleccionado?");
        if (confirmar) {
            items.remove(seleccionado);
            for (int i = 0; i < items.size(); i++) {
                items.get(i).setItem(i + 1);
            }
            actualizarTotalLabel();
        }
    }

    @FXML
    private void onCancel(ActionEvent e) {
        boolean cancelar = DialogHelper.confirm(btnCancel.getScene().getWindow(),
                "¿Está seguro/a de cancelar el registro de este pedido?");
        if (cancelar) {
            pedidoAceptado = false;
            closeWindow();
        }
    }

    @FXML
    private void onAccept(ActionEvent e) {
        boolean hasErrors = false;

        // Validaciones existentes...
        ValidationResult waybillResult = DataValidator.validateIATAWaybill(txtGuiaAerea.getText().trim());
        if (!waybillResult.isValid()) {
            setErrorStyle(txtGuiaAerea, waybillResult.getErrorMessage());
            hasErrors = true;
        } else {
            clearErrorStyle(txtGuiaAerea);
        }

        ValidationResult result = DataValidator.validateDireccion(txtEmpresaTransporte.getText().trim());
        if (!result.isValid()) {
            setErrorStyle(txtEmpresaTransporte, result.getErrorMessage());
            hasErrors = true;
        } else {
            clearErrorStyle(txtEmpresaTransporte);
        }

        if (dpFechaPedido.getValue() == null) {
            setErrorStyle(dpFechaPedido, "La fecha de pedido es obligatoria.");
            hasErrors = true;
        } else {
            clearErrorStyle(dpFechaPedido);
        }

        if (dpFechaExport.getValue() == null) {
            setErrorStyle(dpFechaExport, "La fecha de exportación es obligatoria.");
            hasErrors = true;
        } else {
            clearErrorStyle(dpFechaExport);
        }

        if (cbEstadoPedido.getValue() == null || cbEstadoPedido.getValue().isEmpty()) {
            setErrorStyle(cbEstadoPedido, "El estado del pedido es obligatorio.");
            hasErrors = true;
        } else {
            clearErrorStyle(cbEstadoPedido);
        }

        if (hasErrors) {
            DialogHelper.showError(lblCliente.getScene().getWindow(),
                    "Error al registrar pedido, corrige los campos resaltados (mantén el cursor sobre el campo para más información)");
            return;
        }

        // Intentar descontar unidades del stock
        try {
            descontarUnidadesDelStock(currentPedido);
        } catch (IOException ex) {
            DialogHelper.showError(lblCliente.getScene().getWindow(),
                    "Error al descontar stock: " + ex.getMessage());
            return;
        }

        // Si todo bien, actualizar pedido
        double total = items.stream().mapToDouble(ItemVenta::getPrecioTotal).sum();
        currentPedido.setCodigoGuiaAerea(txtGuiaAerea.getText());
        currentPedido.setEstadoActual(cbEstadoPedido.getValue());
        currentPedido.setFechaPedido(LocalDate.now());
        currentPedido.setFechaEstimadaEnvio(dpFechaExport.getValue());
        currentPedido.setPrecioTotal(total);
        currentPedido.setEmpresaTransporte(txtEmpresaTransporte.getText().trim());
        Usuario usuarioActual = UserSession.getUsuarioActual();
        currentPedido.setUsuarioResponsable(usuarioActual != null ? usuarioActual.getUsuario() : "Sistema");

        actualizarTotalLabel();
        pedidoAceptado = true;
        closeWindow();
    }

    private void descontarUnidadesDelStock(Pedido pedido) throws IOException {
        for (ItemVenta item : pedido.getItemsVenta()) {
            boolean ok = InventarioService.consumirUnidades(item.getVariedad(), item.getLargo(), item.getCantidad());
            if (!ok) {
                throw new IOException("No hay suficiente stock para " + item.getVariedad() + " " + item.getLargo());
            }
        }
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