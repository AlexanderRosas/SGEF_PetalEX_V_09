package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Proveedor;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.models.NavigationManager; // ✅ CORREGIDO: Movido a util

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class ProveedoresController {

    @FXML
    private Button btnBack;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab tabProveedores;
    @FXML
    private Tab tabProductos;

    // Componentes de la pestaña Proveedores
    @FXML
    private TableView<Proveedor> tableProveedores;
    @FXML
    private TableColumn<Proveedor, String> colRuc;
    @FXML
    private TableColumn<Proveedor, String> colNombre;
    @FXML
    private TableColumn<Proveedor, String> colRazonSocial;
    @FXML
    private TableColumn<Proveedor, String> colTelefono;
    @FXML
    private TableColumn<Proveedor, String> colCorreo;
    @FXML
    private TableColumn<Proveedor, String> colEstado;
    @FXML
    private TextField txtBuscarProveedor;
    @FXML
    private ComboBox<String> cbEstadoProveedor;
    @FXML
    private Button btnNuevoProveedor;
    @FXML
    private Button btnEditarProveedor;
    @FXML
    private Button btnCambiarEstadoProveedor;
    @FXML
    private Button btnExportarProveedor;

    private final ObservableList<Proveedor> masterDataProveedores = FXCollections.observableArrayList();
    private FilteredList<Proveedor> filteredDataProveedores;

    // Controlador de productos
    private ProductosController productosController;

    @FXML
    public void initialize() {
        initializeProveedoresTab();
        initializeProductosTab();
    }

    private void initializeProveedoresTab() {
        // Configurar columnas de proveedores
        colRuc.setCellValueFactory(new PropertyValueFactory<>("ruc"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colRazonSocial.setCellValueFactory(new PropertyValueFactory<>("razon_social"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Configurar ComboBox de estado
        cbEstadoProveedor.getItems().addAll("Todos", "Activo", "Inactivo");
        cbEstadoProveedor.getSelectionModel().selectFirst();

        // Configurar botones inicialmente
        btnEditarProveedor.setDisable(true);
        btnCambiarEstadoProveedor.setDisable(true);

        // Listener para habilitar botones
        tableProveedores.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    boolean hasSelection = newSel != null;
                    btnEditarProveedor.setDisable(!hasSelection);
                    btnCambiarEstadoProveedor.setDisable(!hasSelection);
                });

        // Cargar datos de ejemplo
        cargarDatosProveedores();

        // Configurar filtrado
        filteredDataProveedores = new FilteredList<>(masterDataProveedores, p -> true);
        tableProveedores.setItems(filteredDataProveedores);

        // Listeners para filtrado
        txtBuscarProveedor.textProperty().addListener((obs, oldVal, newVal) -> filtrarProveedores());
        cbEstadoProveedor.valueProperty().addListener((obs, oldVal, newVal) -> filtrarProveedores());
    }

    private void initializeProductosTab() {
        try {
            // ✅ CORREGIR: Cargar el FXML correcto
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ProductosTab.fxml"));
            AnchorPane productosContent = loader.load();

            // Configurar el contenido de la pestaña
            tabProductos.setContent(productosContent);

            // Obtener el controlador
            productosController = loader.getController();

            // Pasar la lista de proveedores al controlador de productos
            if (productosController != null) {
                productosController.setProveedoresDisponibles(masterDataProveedores);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar ProductosTab.fxml: " + e.getMessage());

            // ✅ FALLBACK: Si no se puede cargar el FXML, crear contenido simple
            Label errorLabel = new Label("Error al cargar la pestaña de productos: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");
            AnchorPane errorPane = new AnchorPane(errorLabel);
            AnchorPane.setTopAnchor(errorLabel, 20.0);
            AnchorPane.setLeftAnchor(errorLabel, 20.0);
            tabProductos.setContent(errorPane);
        }
    }

    private void filtrarProveedores() {
        String filtroTexto = txtBuscarProveedor.getText().toLowerCase().trim();
        String estadoSeleccionado = cbEstadoProveedor.getValue();

        filteredDataProveedores.setPredicate(proveedor -> {
            boolean coincideTexto = filtroTexto.isEmpty()
                    || proveedor.getRuc().toLowerCase().contains(filtroTexto)
                    || proveedor.getNombre().toLowerCase().contains(filtroTexto)
                    || proveedor.getRazon_social().toLowerCase().contains(filtroTexto)
                    || proveedor.getTelefono().toLowerCase().contains(filtroTexto)
                    || proveedor.getCorreo().toLowerCase().contains(filtroTexto)
                    || proveedor.getDireccion().toLowerCase().contains(filtroTexto);

            boolean coincideEstado = estadoSeleccionado.equals("Todos")
                    || proveedor.getEstado().equals(estadoSeleccionado);

            return coincideTexto && coincideEstado;
        });
    }

    @FXML
    private void onNuevoProveedor(ActionEvent event) {
        Optional<Proveedor> resultado = mostrarFormularioProveedor("Nuevo Proveedor", null);

        if (resultado.isPresent()) {
            Proveedor nuevoProveedor = resultado.get();
            if (validarProveedor(nuevoProveedor)) {
                if (DialogHelper.confirm(getWindow(), "¿Está seguro que desea agregar este proveedor?")) {
                    masterDataProveedores.add(nuevoProveedor);
                    // Actualizar lista en productos
                    if (productosController != null) {
                        productosController.setProveedoresDisponibles(masterDataProveedores);
                    }
                    DialogHelper.showSuccess(getWindow(), "Proveedor agregado exitosamente");
                }
            }
        }
    }

    @FXML
    private void onEditarProveedor(ActionEvent event) {
        Proveedor seleccionado = tableProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            DialogHelper.showWarning(getWindow(), "Debe seleccionar un proveedor para editar");
            return;
        }

        Optional<Proveedor> resultado = mostrarFormularioProveedor("Editar Proveedor", seleccionado);

        if (resultado.isPresent()) {
            if (validarProveedor(seleccionado)) {
                if (DialogHelper.confirm(getWindow(), "¿Está seguro que desea actualizar este proveedor?")) {
                    tableProveedores.refresh();
                    // Actualizar lista en productos
                    if (productosController != null) {
                        productosController.setProveedoresDisponibles(masterDataProveedores);
                    }
                    DialogHelper.showSuccess(getWindow(), "Proveedor actualizado exitosamente");
                }
            }
        }
    }

    @FXML
    private void onCambiarEstadoProveedor(ActionEvent event) {
        Proveedor seleccionado = tableProveedores.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            DialogHelper.showWarning(getWindow(), "Debe seleccionar un proveedor para cambiar su estado");
            return;
        }

        String estadoActual = seleccionado.getEstado();
        String nuevoEstado = estadoActual.equals("Activo") ? "Inactivo" : "Activo";
        String accion = nuevoEstado.equals("Activo") ? "activar" : "inactivar";

        String mensaje = String.format("¿Está seguro que desea %s al proveedor '%s'?",
                accion, seleccionado.getNombre());

        if (DialogHelper.confirm(getWindow(), mensaje)) {
            seleccionado.setEstado(nuevoEstado);
            tableProveedores.refresh();
            DialogHelper.showSuccess(getWindow(),
                    String.format("Proveedor %s exitosamente",
                            nuevoEstado.equals("Activo") ? "activado" : "inactivado"));
        }
    }

    @FXML
    private void onExportarProveedor(ActionEvent event) {
        DialogHelper.showSuccess(getWindow(), "Funcionalidad de exportación en desarrollo");
    }

    @FXML
    private void onBack(ActionEvent event) {
        try {
            NavigationManager.showMainMenu(btnBack.getScene());
        } catch (IOException e) {
            e.printStackTrace();
            DialogHelper.showError(getWindow(), "Error al volver al menú principal");
        }
    }

    private Optional<Proveedor> mostrarFormularioProveedor(String titulo, Proveedor proveedorExistente) {
        Dialog<Proveedor> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.getDialogPane().getButtonTypes().addAll(
                new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE),
                new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos del formulario
        TextField txtRuc = new TextField();
        txtRuc.setPromptText("1790123456001");
        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre comercial");
        TextField txtRazonSocial = new TextField();
        txtRazonSocial.setPromptText("Razón social");
        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("02-1234567");
        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Dirección completa");
        TextField txtCuentaBancaria = new TextField();
        txtCuentaBancaria.setPromptText("1234567890");
        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("empresa@email.com");
        DatePicker dpFechaRegistro = new DatePicker();

        if (proveedorExistente != null) {
            txtRuc.setText(proveedorExistente.getRuc());
            txtNombre.setText(proveedorExistente.getNombre());
            txtRazonSocial.setText(proveedorExistente.getRazon_social());
            txtTelefono.setText(proveedorExistente.getTelefono());
            txtDireccion.setText(proveedorExistente.getDireccion());
            txtCuentaBancaria.setText(proveedorExistente.getCuenta_bancaria());
            txtCorreo.setText(proveedorExistente.getCorreo());
            dpFechaRegistro.setValue(proveedorExistente.getFecha_registro());
        } else {
            dpFechaRegistro.setValue(LocalDate.now());
        }

        // Agregar campos al grid
        grid.add(new Label("RUC:"), 0, 0);
        grid.add(txtRuc, 1, 0);
        grid.add(new Label("Nombre:"), 0, 1);
        grid.add(txtNombre, 1, 1);
        grid.add(new Label("Razón Social:"), 0, 2);
        grid.add(txtRazonSocial, 1, 2);
        grid.add(new Label("Teléfono:"), 0, 3);
        grid.add(txtTelefono, 1, 3);
        grid.add(new Label("Dirección:"), 0, 4);
        grid.add(txtDireccion, 1, 4);
        grid.add(new Label("Cuenta Bancaria:"), 0, 5);
        grid.add(txtCuentaBancaria, 1, 5);
        grid.add(new Label("Correo:"), 0, 6);
        grid.add(txtCorreo, 1, 6);
        grid.add(new Label("Fecha Registro:"), 0, 7);
        grid.add(dpFechaRegistro, 1, 7);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                if (proveedorExistente != null) {
                    // Actualizar proveedor existente
                    proveedorExistente.setRuc(txtRuc.getText());
                    proveedorExistente.setNombre(txtNombre.getText());
                    proveedorExistente.setRazon_social(txtRazonSocial.getText());
                    proveedorExistente.setTelefono(txtTelefono.getText());
                    proveedorExistente.setDireccion(txtDireccion.getText());
                    proveedorExistente.setCuenta_bancaria(txtCuentaBancaria.getText());
                    proveedorExistente.setCorreo(txtCorreo.getText());
                    proveedorExistente.setFecha_registro(dpFechaRegistro.getValue());
                    return proveedorExistente;
                } else {
                    // Crear nuevo proveedor
                    return new Proveedor(
                            txtRuc.getText(),
                            txtNombre.getText(),
                            txtRazonSocial.getText(),
                            txtTelefono.getText(),
                            txtDireccion.getText(),
                            txtCuentaBancaria.getText(),
                            txtCorreo.getText(),
                            dpFechaRegistro.getValue(),
                            "Activo");
                }
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private boolean validarProveedor(Proveedor proveedor) {
        // Validar campos obligatorios
        if (proveedor.getRuc() == null || proveedor.getRuc().trim().isEmpty()) {
            DialogHelper.showError(getWindow(), "El RUC es obligatorio");
            return false;
        }

        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            DialogHelper.showError(getWindow(), "El nombre es obligatorio");
            return false;
        }

        if (proveedor.getRazon_social() == null || proveedor.getRazon_social().trim().isEmpty()) {
            DialogHelper.showError(getWindow(), "La razón social es obligatoria");
            return false;
        }

        // Validar RUC ecuatoriano
        if (!proveedor.isValidRuc()) {
            DialogHelper.showError(getWindow(), "El RUC debe tener 13 dígitos y terminar en 001 o 002");
            return false;
        }

        // Validar correo si está presente
        if (proveedor.getCorreo() != null && !proveedor.getCorreo().trim().isEmpty()) {
            if (!proveedor.isValidEmail()) {
                DialogHelper.showError(getWindow(), "El formato del correo electrónico no es válido");
                return false;
            }
        }

        // Validar RUC único
        for (Proveedor p : masterDataProveedores) {
            if (!p.equals(proveedor) && p.getRuc().equals(proveedor.getRuc())) {
                DialogHelper.showError(getWindow(), "Ya existe un proveedor con este RUC");
                return false;
            }
        }

        return true;
    }

    private void cargarDatosProveedores() {
        masterDataProveedores.addAll(
                new Proveedor("1790123456001", "FloriPetals", "FloriPetals S.A.", "02-2345678",
                        "Av. de las Rosas 123, Quito", "1234567890", "contacto@floripetalsa.com",
                        LocalDate.now().minusMonths(6), "Activo"),
                new Proveedor("1790234567001", "Roses Export", "Roses Export Ecuador Cia. Ltda.", "02-3456789",
                        "Calle Flores 456, Cayambe", "2345678901", "ventas@rosesexport.com",
                        LocalDate.now().minusMonths(4), "Activo"),
                new Proveedor("1790345678001", "Garden Flowers", "Garden Flowers Cia. Ltda.", "02-4567890",
                        "Av. Floresta 789, Tabacundo", "3456789012", "info@gardenflowers.ec",
                        LocalDate.now().minusMonths(8), "Activo"),
                new Proveedor("1790456789001", "EcuaFlores", "EcuaFlores Internacional S.A.", "02-5678901",
                        "Km 25 Vía Cayambe, Cayambe", "4567890123", "admin@ecuaflores.com",
                        LocalDate.now().minusMonths(12), "Inactivo"),
                new Proveedor("1790567890001", "Premium Blooms", "Premium Blooms del Ecuador S.A.", "02-6789012",
                        "Sector La Esperanza, Pedro Moncayo", "5678901234", "premium@blooms.ec",
                        LocalDate.now().minusMonths(2), "Activo"));
    }

    private Window getWindow() {
        return btnBack.getScene().getWindow();
    }

    // Métodos para acceso desde otros controladores
    public ObservableList<Proveedor> getProveedores() {
        return masterDataProveedores;
    }
}
