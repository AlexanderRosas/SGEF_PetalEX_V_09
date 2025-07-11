package org.example.sgef_petalex_v_09.controllers;

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
import org.example.sgef_petalex_v_09.models.ItemVenta;
import org.example.sgef_petalex_v_09.models.Venta;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.io.IOException;
import java.util.Optional;

public class VentaDetailController {

    @FXML
    private Label lblCliente;
    @FXML
    private Label lblDireccion;
    @FXML
    private Label lblFecha;
    @FXML
    private Button btnAddItem;
    @FXML
    private Button btnRemoveItem;

    @FXML
    private TableView<ItemVenta> tableItems;
    @FXML
    private TableColumn<ItemVenta, Integer> colItem;
    @FXML
    private TableColumn<ItemVenta, String> colVariedad;
    @FXML
    private TableColumn<ItemVenta, String> colPaquete;
    @FXML
    private TableColumn<ItemVenta, Integer> colCantidad;
    @FXML
    private TableColumn<ItemVenta, Double> colPrecioU;
    @FXML
    private TableColumn<ItemVenta, Double> colPrecioT;
    @FXML
    private Label lblTotal;
    @FXML
    private Button btnCancelVenta;
    @FXML
    private Button btnAcceptVenta;

    private boolean ventaAceptada = false;
    private Venta currentVenta;
    private ObservableList<ItemVenta> items = FXCollections.observableArrayList();

    /** Para ver venta existente */
    public void initData(Venta v) {
        this.currentVenta = v;
        setup();
        items.setAll(v.getItems());
    }

    private void setup() {
        btnAcceptVenta.setDisable(true); // al inicio

        items.addListener((javafx.collections.ListChangeListener.Change<? extends ItemVenta> c) -> {
            btnAcceptVenta.setDisable(items.isEmpty());
        });
        btnRemoveItem.setOnAction(this::onRemoveItem);

        lblCliente.setText("Cliente: " + currentVenta.getCliente());
        lblFecha.setText("Fecha: " + currentVenta.getFecha());
        lblDireccion.setText("Dirección: " + currentVenta.getDireccion());

        colItem.setCellValueFactory(i -> i.getValue().itemProperty().asObject());
        colVariedad.setCellValueFactory(i -> i.getValue().variedadProperty());
        colPaquete.setCellValueFactory(i -> i.getValue().paqueteProperty());
        colCantidad.setCellValueFactory(i -> i.getValue().cantidadProperty().asObject());
        colPrecioU.setCellValueFactory(i -> i.getValue().precioUnitProperty().asObject());
        colPrecioT.setCellValueFactory(i -> i.getValue().precioTotalProperty().asObject());

        tableItems.setItems(items);
        updateTotal();

        btnAddItem.setOnAction(this::onAddItem);
        btnCancelVenta.setOnAction(this::onCancelVenta);
        btnAcceptVenta.setOnAction(this::onAcceptVenta);
    }

    private void closeWindow() {
        Stage st = (Stage) lblCliente.getScene().getWindow();
        st.close();
    }

    /** Para VentasController: devuelve Optional.empty() si canceló */
    public Optional<Venta> getVentaCreated() {
        if (ventaAceptada) {
            currentVenta.getItems().clear(); // Limpia antes de agregar
            items.forEach(currentVenta::addItem);
            return Optional.of(currentVenta);
        } else {
            return Optional.empty();
        }
    }

    private void updateTotal() {
        double sum = items.stream().mapToDouble(ItemVenta::getPrecioTotal).sum();
        lblTotal.setText(String.format("$%,.2f", sum));
        currentVenta.setTotal(sum);
    }

    @FXML
    private void onCancelVenta(ActionEvent e) {
        ventaAceptada = false;
        ((Stage) lblCliente.getScene().getWindow()).close();
    }

    @FXML
    private void onAcceptVenta(ActionEvent e) {
        ventaAceptada = true;
        ((Stage) lblCliente.getScene().getWindow()).close();
    }

    @FXML
    private void onAddItem(ActionEvent e) {
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

    @FXML
    private void onRemoveItem(ActionEvent e) {
        ItemVenta selected = tableItems.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogHelper.showWarning(lblCliente.getScene().getWindow(), "Debe seleccionar un ítem para eliminar.");
            return;
        }
        boolean confirm = DialogHelper.confirm(lblCliente.getScene().getWindow(),
                "¿Está seguro que desea eliminar el ítem seleccionado?");
        if (confirm) {
            items.remove(selected);
            // Reasigna números de ítem para mantener orden
            for (int i = 0; i < items.size(); i++) {
                items.get(i).setItem(i + 1);
            }
            updateTotal();
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

}
