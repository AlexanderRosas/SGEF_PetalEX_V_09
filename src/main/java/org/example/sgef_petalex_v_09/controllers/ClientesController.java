package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.transformation.FilteredList;
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

public class ClientesController {

    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, Integer> colId;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colDireccion;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, String> colCorreo;
    @FXML
    private TableColumn<Cliente, String> colEstado;
    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnBuscar;
    @FXML
    private Button btnEstado;

    @FXML
    private TextField txtBuscar;

    private final ObservableList<Cliente> data = FXCollections.observableArrayList();
    private FilteredList<Cliente> filteredData;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        cbEstado.setItems(FXCollections.observableArrayList("Todos", "Activo", "Inactivo"));
        cbEstado.setValue("Todos");

        btnEditar.setDisable(true);
        btnEstado.setDisable(true);

        tablaClientes.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean sel = newSel != null;
            btnEditar.setDisable(!sel);
            btnEstado.setDisable(!sel);
        });

        cargarDatosPrototipo();

        filteredData = new FilteredList<>(data, p -> true);
        tablaClientes.setItems(filteredData);

        txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
        cbEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        String filtroTexto = txtBuscar.getText().toLowerCase().trim();
        String estadoSeleccionado = cbEstado.getValue();

        filteredData.setPredicate(cliente -> {
            boolean coincideTexto = filtroTexto.isEmpty()
                    || String.valueOf(cliente.getId()).contains(filtroTexto)
                    || cliente.getNombre().toLowerCase().contains(filtroTexto)
                    || cliente.getDireccion().toLowerCase().contains(filtroTexto)
                    || cliente.getTelefono().toLowerCase().contains(filtroTexto)
                    || cliente.getCorreo().toLowerCase().contains(filtroTexto);

            boolean coincideEstado = estadoSeleccionado.equals("Todos")
                    || cliente.getEstado().equalsIgnoreCase(estadoSeleccionado);

            return coincideTexto && coincideEstado;
        });
    }

    private void cargarDatosPrototipo() {
        data.clear();
        data.addAll(
                new Cliente(1234567890, "RoseFlower INC.", "Av. Petalos 123, Ecuador", "+593991234567", "ventas@roseflower.ec", "Activo"),
                new Cliente(987654321, "SweetMoment Ltd.", "123 Tulip St., Netherlands", "+31205551234", "contact@sweetmoment.nl", "Activo"),
                new Cliente(567890123, "GlobalRoses GmbH", "Rosengasse 5, Germany", "+4930123456", "info@globalroses.de", "Activo"),
                new Cliente(432109876, "FlorAmor Co.", "789 Orchid Ave., Colombia", "+57123456789", "ventas@floramor.co", "Activo"),
                new Cliente(246801357, "PetalWorld SA", "456 Blossom Rd., USA", "+12025550123", "sales@petalworld.com", "Activo")
        );
    }

    @FXML
    private void onNuevo(ActionEvent event) {
        Window owner = btnNuevo.getScene().getWindow();

        Optional<Cliente> formResult = showClientFormDialog("Crear", null);
        formResult.ifPresent(c -> {
            Alert confirm = new Alert(AlertType.CONFIRMATION);
            confirm.initOwner(owner);
            confirm.setTitle("Confirmar creación");
            confirm.setHeaderText(null);
            confirm.setContentText("¿Está seguro que desea crear al cliente?");
            Optional<ButtonType> ok = confirm.showAndWait();

            if (ok.isPresent() && ok.get().getButtonData() == ButtonData.OK_DONE) {
                data.add(c);
                tablaClientes.refresh();
                DialogHelper.showSuccess(owner, "creado al cliente");
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
    private void onEstado(ActionEvent event) {
        Window owner = btnEstado.getScene().getWindow();
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();

        if (sel == null) {
            DialogHelper.showWarning(owner, "Selecciona un cliente primero");
            return;
        }

        String nuevoEstado = sel.getEstado().equals("Activo") ? "Inactivo" : "Activo";

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.initOwner(owner);
        confirm.setTitle("Confirmar cambio de estado");
        confirm.setHeaderText(null);
        confirm.setContentText("¿Está seguro que desea cambiar el estado a '" + nuevoEstado + "'?");
        Optional<ButtonType> res = confirm.showAndWait();

        if (res.isPresent() && res.get().getButtonData() == ButtonData.OK_DONE) {
            sel.setEstado(nuevoEstado);
            tablaClientes.refresh();
            DialogHelper.showSuccess(owner, "Estado cambiado a " + nuevoEstado);
        }
    }

    private Optional<Cliente> showClientFormDialog(String action, Cliente existing) {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle(action + " cliente");
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE),
                new ButtonType("Aceptar", ButtonData.OK_DONE));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtIdEmpresarial = new TextField();
        txtIdEmpresarial.setPromptText("Identificador Empresarial");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre");
        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Dirección");
        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("Teléfono");
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("Correo");

        if (existing != null) {
            txtIdEmpresarial.setText(String.valueOf(existing.getId()));
            txtIdEmpresarial.setDisable(true);
            txtNombre.setText(existing.getNombre());
            txtDireccion.setText(existing.getDireccion());
            txtTelefono.setText(existing.getTelefono());
            txtCorreo.setText(existing.getCorreo());
        }

        grid.add(new Label("Identificador Empresarial:"), 0, 0);
        grid.add(txtIdEmpresarial, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(new Label("Dirección:"), 0, 2);
        grid.add(txtDireccion, 1, 2);
        grid.add(new Label("Teléfono:"), 0, 3);
        grid.add(txtTelefono, 1, 3);
        grid.add(new Label("Correo:"), 0, 4);
        grid.add(txtCorreo, 1, 4);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(btn -> {
            if (btn.getButtonData() == ButtonData.OK_DONE) {
                try {
                    int id = existing != null ? existing.getId()
                            : Integer.parseInt(txtIdEmpresarial.getText().trim());
                    String nombre = txtNombre.getText();
                    String direccion = txtDireccion.getText();
                    String telefono = txtTelefono.getText();
                    String correo = txtCorreo.getText();
                    String estado = existing != null ? existing.getEstado() : "Activo";
                    return new Cliente(id, nombre, direccion, telefono, correo, estado);
                } catch (NumberFormatException e) {
                    DialogHelper.showError(dialog.getDialogPane().getScene().getWindow(), "ID inválido");
                    return null;
                }
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
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setTitle("Index Blooms – Menú Principal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
