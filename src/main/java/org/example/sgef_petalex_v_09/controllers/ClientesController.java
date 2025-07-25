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
import java.util.Random;

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
    private FilteredList<Cliente> filteredData; // <-- NUEVO filtered list para filtro dinámico
    private final Random rnd = new Random();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        // Inicializar ComboBox de Estado
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "Activo", "Inactivo"));
        cbEstado.setValue("Todos"); // por defecto mostrar todos

        btnEditar.setDisable(true);
        btnEstado.setDisable(true);

        tablaClientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    boolean sel = newSel != null;
                    btnEditar.setDisable(!sel);
                    btnEstado.setDisable(!sel);
                });

        cargarDatosPrototipo();

        // Envuelve el ObservableList en FilteredList para filtro dinámico
        filteredData = new FilteredList<>(data, p -> true);
        tablaClientes.setItems(filteredData);

        // Listener combinado para texto y estado
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
                new Cliente(randId(), "RoseFlower INC.", "Av. Petalos 123, Ecuador", "+593991234567",
                        "ventas@roseflower.ec", "Activo"),
                new Cliente(randId(), "SweetMoment Ltd.", "123 Tulip St., Netherlands", "+31205551234",
                        "contact@sweetmoment.nl", "Activo"),
                new Cliente(randId(), "GlobalRoses GmbH", "Rosengasse 5, Germany", "+4930123456",
                        "info@globalroses.de", "Activo"),
                new Cliente(randId(), "FlorAmor Co.", "789 Orchid Ave., Colombia", "+57123456789",
                        "ventas@floramor.co", "Activo"),
                new Cliente(randId(), "PetalWorld SA", "456 Blossom Rd., USA", "+12025550123",
                        "sales@petalworld.com", "Activo"));
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

    /**
     * Muestra un Dialog personalizado para Crear/Actualizar un Cliente.
     * Si 'existing' != null, carga sus datos para edición.
     */
    private Optional<Cliente> showClientFormDialog(String action, Cliente existing) {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle(action + " cliente");
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE),
                new ButtonType("Aceptar", ButtonData.OK_DONE));

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

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Dirección:"), 0, 1);
        grid.add(txtDireccion, 1, 1);
        grid.add(new Label("Teléfono:"), 0, 2);
        grid.add(txtTelefono, 1, 2);
        grid.add(new Label("Correo:"), 0, 3);
        grid.add(txtCorreo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // Convertir resultado
        dialog.setResultConverter(btn -> {
            if (btn.getButtonData() == ButtonData.OK_DONE) {
                Cliente c = new Cliente();
                if (existing != null)
                    c.setId(Integer.parseInt(existing.getId()));
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
            // Carga el FXML del menú principal
            Parent mainRoot = FXMLLoader.load(
                    getClass().getResource("/fxml/MainMenu.fxml"));
            // Obtiene el Stage y reutiliza la Scene actual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = stage.getScene();

            // Reemplaza la raíz sin crear nueva Scene
            scene.setRoot(mainRoot);

            // Reaplica tu CSS
            scene.getStylesheets().clear();
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm());

            // Asegura que siga maximizado
            // stage.setMaximized(true);

            // Actualiza el título
            stage.setTitle("Index Blooms – Menú Principal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
