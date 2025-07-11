package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

public class ClientesController {

    @FXML private TableView<Cliente> tablaClientes;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNombre;
    @FXML private TableColumn<Cliente, String> colDireccion;
    @FXML private TableColumn<Cliente, String> colTelefono;
    @FXML private TableColumn<Cliente, String> colCorreo;

    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnRefrescar;
    @FXML private Button btnBuscar;

    private final ObservableList<Cliente> data = FXCollections.observableArrayList();
    private final Random rnd = new Random();

    @FXML
    public void initialize() {
        colId       .setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre   .setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono .setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo   .setCellValueFactory(new PropertyValueFactory<>("correo"));

        btnEditar.setDisable(true);
        btnEliminar.setDisable(true);

        tablaClientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    boolean sel = newSel != null;
                    btnEditar .setDisable(!sel);
                    btnEliminar.setDisable(!sel);
                });

        cargarDatosPrototipo();
    }

    private void cargarDatosPrototipo() {
        data.clear();
        data.addAll(
                new Cliente(randId(), "RoseFlower INC.",   "Av. Petalos 123, Ecuador",     "+593991234567", "ventas@roseflower.ec"),
                new Cliente(randId(), "SweetMoment Ltd.",   "123 Tulip St., Netherlands",    "+31205551234",  "contact@sweetmoment.nl"),
                new Cliente(randId(), "GlobalRoses GmbH",   "Rosengasse 5, Germany",         "+4930123456",   "info@globalroses.de"),
                new Cliente(randId(), "FlorAmor Co.",       "789 Orchid Ave., Colombia",     "+57123456789",  "ventas@floramor.co"),
                new Cliente(randId(), "PetalWorld SA",      "456 Blossom Rd., USA",          "+12025550123",  "sales@petalworld.com")
        );
        tablaClientes.setItems(data);
    }

    private int randId() {
        return 100000 + rnd.nextInt(900000);
    }

    @FXML
    private void onNuevo(ActionEvent event) {
        Window owner = btnNuevo.getScene().getWindow();

        // 1) Form dialog
        Optional<Cliente> formResult = showClientFormDialog("Crear", null);
        formResult.ifPresent(c -> {
            // 2) Confirmation
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.initOwner(owner);
            confirm.setTitle("Confirmar creación");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Está seguro que desea Crear?");
            Optional<ButtonType> ok = confirm.showAndWait();

            if (ok.isPresent() && ok.get().getButtonData() == ButtonData.OK_DONE) {
                c.setId(randId());
                data.add(c);
                tablaClientes.refresh();
                DialogHelper.showSuccess(owner, "creado");
            }
        });
    }

    @FXML
    private void onEditar(ActionEvent event) {
        Window owner = btnEditar.getScene().getWindow();
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogHelper.showWarning(owner, "Selecciona un cliente primero");
            return;
        }

        Optional<Cliente> formResult = showClientFormDialog("Actualizar", sel);
        formResult.ifPresent(c -> {
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.initOwner(owner);
            confirm.setTitle("Confirmar actualización");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Está seguro que desea Actualizar?");
            Optional<ButtonType> ok = confirm.showAndWait();

            if (ok.isPresent() && ok.get().getButtonData() == ButtonData.OK_DONE) {
                sel.setNombre(c.getNombre());
                sel.setDireccion(c.getDireccion());
                sel.setTelefono(c.getTelefono());
                sel.setCorreo(c.getCorreo());
                tablaClientes.refresh();
                DialogHelper.showSuccess(owner, "actualizado");
            }
        });
    }

    @FXML
    private void onEliminar(ActionEvent event) {
        Window owner = btnEliminar.getScene().getWindow();
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogHelper.showWarning(owner, "Selecciona un cliente primero");
            return;
        }
        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.initOwner(owner);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Está seguro que desea Eliminar?");
        Optional<ButtonType> ok = confirm.showAndWait();
        if (ok.isPresent() && ok.get().getButtonData() == ButtonData.OK_DONE) {
            data.remove(sel);
            tablaClientes.refresh();
            DialogHelper.showSuccess(owner, "eliminado");
        }
    }

    @FXML
    private void onRefrescar(ActionEvent event) {
        Window owner = btnRefrescar.getScene().getWindow();
        cargarDatosPrototipo();
        DialogHelper.showSuccess(owner, "refrescado");
    }

    @FXML
    private void onBuscar(ActionEvent event) {
        Window owner = btnBuscar.getScene().getWindow();
        TextInputDialog input = new TextInputDialog();
        input.initOwner(owner);
        input.setTitle("Buscar cliente");
        input.setHeaderText(null);
        input.setContentText("Ingresa nombre o ID:");
        Optional<String> res = input.showAndWait();
        if (res.isPresent()) {
            String q = res.get().trim().toLowerCase();
            boolean found = data.stream().anyMatch(c ->
                    String.valueOf(c.getId()).equals(q) ||
                            c.getNombre().toLowerCase().contains(q)
            );
            if (!found) {
                DialogHelper.showWarning(owner, "Cliente no encontrado");
            } else {
                DialogHelper.showSuccess(owner, "encontrado");
            }
        }
    }

    /**
     * Muestra un Dialog personalizado para Crear/Actualizar un Cliente.
     * Si 'existing' != null, carga sus datos para edición.
     */
    private Optional<Cliente> showClientFormDialog(String action, Cliente existing) {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle(action + " cliente");
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE),
                new ButtonType("Aceptar", ButtonData.OK_DONE)
        );

        // Formulario
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre");
        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Dirección");
        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Teléfono");
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("Correo");

        if (existing != null) {
            txtNombre.setText(existing.getNombre());
            txtDireccion.setText(existing.getDireccion());
            txtTelefono.setText(existing.getTelefono());
            txtCorreo.setText(existing.getCorreo());
        }

        grid.add(new Label("Nombre:"),    0, 0);
        grid.add(txtNombre,               1, 0);
        grid.add(new Label("Dirección:"), 0, 1);
        grid.add(txtDireccion,            1, 1);
        grid.add(new Label("Teléfono:"),  0, 2);
        grid.add(txtTelefono,             1, 2);
        grid.add(new Label("Correo:"),    0, 3);
        grid.add(txtCorreo,               1, 3);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(btn -> {
            if (btn.getButtonData() == ButtonData.OK_DONE) {
                Cliente c = new Cliente();
                if (existing != null) c.setId(existing.getId());
                c.setNombre(txtNombre.getText());
                c.setDireccion(txtDireccion.getText());
                c.setTelefono(txtTelefono.getText());
                c.setCorreo(txtCorreo.getText());
                return c;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            Parent mainRoot = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            scene.setRoot(mainRoot);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
            );

            try {
                Thread.sleep(500);
                stage.setHeight(600);
                stage.setWidth(1000);
                System.out.println("Ajustando tamaño...");
                stage.setMaximized(true);
                stage.centerOnScreen();

            }
            catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
            stage.setTitle("Index Blooms – Menú Principal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
