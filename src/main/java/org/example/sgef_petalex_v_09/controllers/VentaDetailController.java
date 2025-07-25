package org.example.sgef_petalex_v_09.controllers;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
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
import org.example.sgef_petalex_v_09.util.NavigationHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.time.format.DateTimeFormatter;

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

    @FXML
    private void onNuevo(ActionEvent event) {
        // 1) Define las opciones
        List<String> opciones = Arrays.asList("Nacional", "Internacional");
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, opciones);
        dialog.setTitle("Nuevo – Tipo de Destino");
        dialog.setHeaderText(null);
        dialog.setContentText("Selecciona el Tipo de Destino:");

        // 2) Obtén referencia al botón OK y deshabilítalo si no hay selección
        Button okButton = (Button) dialog.getDialogPane()
                .lookupButton(ButtonType.OK);
        // Bind al valor de selectedItem: si es null, OK está deshabilitado
        okButton.disableProperty()
                .bind(dialog.selectedItemProperty().isNull());

        // 3) Muestra el diálogo y procesa el resultado
        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(tipoDestino -> {
            try {
                NavigationHelper.cargarVista(event,
                        "/fxml/VentaDetail.fxml",
                        "Nueva Venta – " + tipoDestino);
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR,
                        "Error al abrir el detalle de venta:\n" + e.getMessage())
                        .showAndWait();
            }
        });
    }
    @FXML
    public void initialize() {
        // Inicializar venta si es nueva
        if (currentVenta == null) {
            currentVenta = new Venta();
        }
        
        setupTableColumns();
        setupEventHandlers();
        tableItems.setItems(items);
        
        // Listener para cambios en items
        items.addListener((ListChangeListener<ItemVenta>) c -> {
            btnAcceptVenta.setDisable(items.isEmpty());
            updateTotal();
        });
        
        // Listener para selección
        tableItems.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSel, newSel) -> btnRemoveItem.setDisable(newSel == null)
        );
        
        // Estado inicial de botones
        btnAcceptVenta.setDisable(true);
        btnRemoveItem.setDisable(true);
    }

    /** Para ver venta existente */
    public void initData(Venta v) {
        this.currentVenta = v;
        setupVentaInfo();
        items.setAll(v.getItems());
        updateTotal();
    }

    private void setupTableColumns() {
        colItem.setCellValueFactory(i -> i.getValue().itemProperty().asObject());
        colVariedad.setCellValueFactory(i -> i.getValue().variedadProperty());
        colPaquete.setCellValueFactory(i -> i.getValue().paqueteProperty());
        colCantidad.setCellValueFactory(i -> i.getValue().cantidadProperty().asObject());
        colPrecioU.setCellValueFactory(i -> i.getValue().precioUnitProperty().asObject());
        colPrecioT.setCellValueFactory(i -> i.getValue().precioTotalProperty().asObject());
        
        // Formato para las columnas de precio
        colPrecioU.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", precio));
                }
            }
        });
        
        colPrecioT.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", precio));
                }
            }
        });
    }

    private void setupEventHandlers() {
        btnAddItem.setOnAction(this::onAddItem);
        btnRemoveItem.setOnAction(this::onRemoveItem);
        btnCancelVenta.setOnAction(this::onCancelVenta);
        btnAcceptVenta.setOnAction(this::onAcceptVenta);
    }

    private void setupVentaInfo() {
        if (currentVenta != null) {
            lblCliente.setText("Cliente: " + 
                (currentVenta.getCliente() != null ? currentVenta.getCliente() : "No especificado"));
            
            lblFecha.setText("Fecha: " + 
                (currentVenta.getFecha() != null ? currentVenta.getFecha().format(
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "No especificada"));
            
            lblDireccion.setText("Dirección: " + 
                (currentVenta.getDireccion() != null ? currentVenta.getDireccion() : "No especificada"));
        }
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
        double sum = items.stream()
            .mapToDouble(ItemVenta::getPrecioTotal)
            .sum();
        lblTotal.setText(String.format("$%.2f", sum));
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

        ChangeListener<Object> validar = (obs, oldV, newV) -> {
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
        return res.orElse(null);
    }

    public void onBack(ActionEvent e) {
        ((Stage) lblCliente.getScene().getWindow()).close();

    }

    public void onEliminarItem(ActionEvent e) {
        ItemVenta selected = tableItems.getSelectionModel().getSelectedItem();
        if (selected == null) {
            DialogHelper.showWarning(lblCliente.getScene().getWindow(), "Debe seleccionar un ítem para eliminar.");
            return;
        }
        boolean confirm = DialogHelper.confirm(lblCliente.getScene().getWindow(),
                "¿Está seguro que desea eliminar el ítem seleccionado?");
        if (confirm) {
            items.remove(selected);
        }
    }

}