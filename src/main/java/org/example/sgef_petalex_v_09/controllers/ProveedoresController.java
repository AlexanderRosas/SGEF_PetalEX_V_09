package org.example.sgef_petalex_v_09.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Proveedor;
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.CSVUtil;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.util.UserSession;
import org.example.sgef_petalex_v_09.validators.DataValidator;
import org.example.sgef_petalex_v_09.validators.TextFieldValidator;
import org.example.sgef_petalex_v_09.validators.ValidationResult;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProveedoresController implements Initializable {

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
    private TableColumn<Proveedor, LocalDate> colFechaRegistro;
    @FXML
    private TableColumn<Proveedor, String> colDireccion;
    @FXML
    private TableColumn<Proveedor, LocalDate> colFechaModificacion;
    @FXML
    private TableColumn<Proveedor, String> colUsuarioResponsable;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeProveedoresTab();
        initializeProductosTab();
    }

    private void initializeProductosTab() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RoseSelection.fxml"));
            Parent root = loader.load();

            RoseSelectionController controller = loader.getController();
            controller.setModoOperacion(RoseSelectionController.ModoOperacion.AGREGAR_PRODUCTO);

            tabProductos.setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
            // Opcional: muestra alerta o loguea error
        }
    }

    private void initializeProveedoresTab() {
        // Configurar columnas de proveedores
        colRuc.setCellValueFactory(new PropertyValueFactory<>("ruc"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colRazonSocial.setCellValueFactory(new PropertyValueFactory<>("razon_social"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFechaRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));
        colFechaModificacion.setCellValueFactory(new PropertyValueFactory<>("fechaModificacion"));
        colUsuarioResponsable.setCellValueFactory(new PropertyValueFactory<>("usuarioResponsable"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
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

    private void filtrarProveedores() {
        String ruc = txtBuscarProveedor.getText().toLowerCase().trim();
        String estadoSeleccionado = cbEstadoProveedor.getValue();

        filteredDataProveedores.setPredicate(proveedor -> {
            boolean coincideRuc = ruc.isEmpty() || proveedor.getRuc().toLowerCase().startsWith(ruc);
            boolean coincideEstado = estadoSeleccionado.equals("Todos")
                    || proveedor.getEstado().equals(estadoSeleccionado);
            return coincideRuc && coincideEstado;
        });
    }

    private boolean yaSeAbrioEnEstaSesion = false;

    @FXML
    private void onTabProductosSeleccionado(Event event) {
        Tab tab = (Tab) event.getSource();
        if (tab.isSelected() && !yaSeAbrioEnEstaSesion) {
            yaSeAbrioEnEstaSesion = true;
            abrirSelectorRosaParaAgregarProducto();
        }
    }

    private void abrirSelectorRosaParaAgregarProducto() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/sgef_petalex_v_09/views/RoseSelection.fxml"));
            Parent root = loader.load();

            RoseSelectionController controller = loader.getController();
            controller.setModoOperacion(RoseSelectionController.ModoOperacion.AGREGAR_PRODUCTO);

            Stage stage = new Stage();
            stage.setTitle("Seleccionar tipo de rosa");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            String rosaSeleccionada = controller.getSelectedRose();
            if (rosaSeleccionada != null) {
                System.out.println("Producto nuevo con rosa: " + rosaSeleccionada);
            }

        } catch (IOException e) {
            e.printStackTrace();
            DialogHelper.showError(null, "Error al abrir el selector de rosas.");
        }
    }

    @FXML
    private void onNuevoProveedor(ActionEvent event) {
        Optional<Proveedor> resultado = mostrarFormularioProveedor("Nuevo Proveedor", null);

        if (resultado.isPresent()) {
            Proveedor nuevoProveedor = resultado.get();
            if (validarProveedor(nuevoProveedor)) {
                if (DialogHelper.confirm(getWindow(), "¿Está seguro que desea agregar este proveedor?")) {
                    masterDataProveedores.add(nuevoProveedor);
                    CSVUtil.guardarProveedores(masterDataProveedores);
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
                    CSVUtil.guardarProveedores(masterDataProveedores);
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
            CSVUtil.guardarProveedores(masterDataProveedores);
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
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Guardar estado ventana actual para restaurar si quieres (opcional)
            boolean wasMaximized = stage.isMaximized();
            double width = stage.getWidth();
            double height = stage.getHeight();

            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainMenu.fxml"));

            Scene scene = stage.getScene();
            scene.setRoot(root);

            // Reaplicar CSS si usas hojas externas
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());

            stage.setResizable(false);
            if (wasMaximized) {
                stage.setMaximized(true);
            } else {
                stage.setMaximized(false);
                stage.setWidth(width);
                stage.setHeight(height);
                stage.centerOnScreen();
            }

            stage.setTitle("Index Blooms – Menú Principal");

        } catch (IOException e) {
            e.printStackTrace();
            DialogHelper.showError(null, "No se pudo cargar el menú principal.");
        }
    }

    private Optional<Proveedor> mostrarFormularioProveedor(String titulo, Proveedor proveedorExistente) {
        Dialog<Proveedor> dialog = new Dialog<>();
        dialog.setTitle(titulo);

        // Botones del diálogo
        ButtonType btnAceptar = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnAceptar, btnCancelar);

        // Crear el Grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Campos del formulario
        TextField txtRuc = new TextField();
        txtRuc.setPromptText("1790123456001");

        TextField txtNombre = new TextField();
        txtNombre.setPromptText("Nombre Natural");

        TextField txtRazonSocial = new TextField();
        txtRazonSocial.setPromptText("Razón social");

        TextField txtTelefono = new TextField();
        txtTelefono.setPromptText("0987654321");

        TextField txtDireccion = new TextField();
        txtDireccion.setPromptText("Dirección completa");

        TextField txtCuentaBancaria = new TextField();
        txtCuentaBancaria.setPromptText("1234567890");

        TextField txtCorreo = new TextField();
        txtCorreo.setPromptText("empresa@email.com");

        DatePicker dpFechaRegistro = new DatePicker();

        // Validaciones automáticas
        TextFieldValidator.setupValidation(txtRuc, TextFieldValidator.ValidationType.RUC);
        TextFieldValidator.setupValidation(txtNombre, TextFieldValidator.ValidationType.NAME_ONLY);
        TextFieldValidator.setupValidation(txtTelefono, TextFieldValidator.ValidationType.PHONE);
        TextFieldValidator.setupValidation(txtCorreo, TextFieldValidator.ValidationType.EMAIL);
        TextFieldValidator.setupValidation(txtCuentaBancaria, TextFieldValidator.ValidationType.NUMERIC);

        // Validaciones personalizadas
        txtRazonSocial.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 100)
                txtRazonSocial.setText(oldVal);
        });

        txtDireccion.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 200)
                txtDireccion.setText(oldVal);
        });

        // Si hay un proveedor existente, llenar campos
        if (proveedorExistente != null) {
            txtRuc.setText(proveedorExistente.getRuc());
            txtNombre.setText(proveedorExistente.getNombre());
            txtRazonSocial.setText(proveedorExistente.getRazon_social());
            txtTelefono.setText(proveedorExistente.getTelefono());
            txtDireccion.setText(proveedorExistente.getDireccion());
            txtCuentaBancaria.setText(proveedorExistente.getCuenta_bancaria());
            txtCorreo.setText(proveedorExistente.getCorreo());
            dpFechaRegistro.setValue(proveedorExistente.getFechaRegistro());
        } else {
            dpFechaRegistro.setValue(LocalDate.now());
        }

        // Agregar campos al grid
        grid.addRow(0, new Label("RUC:"), txtRuc);
        grid.addRow(1, new Label("Nombre:"), txtNombre);
        grid.addRow(2, new Label("Razón Social:"), txtRazonSocial);
        grid.addRow(3, new Label("Teléfono:"), txtTelefono);
        grid.addRow(4, new Label("Dirección:"), txtDireccion);
        grid.addRow(5, new Label("Cuenta Bancaria:"), txtCuentaBancaria);
        grid.addRow(6, new Label("Correo:"), txtCorreo);
        grid.addRow(7, new Label("Fecha Registro:"), dpFechaRegistro);

        dialog.getDialogPane().setContent(grid);

        // Obtener botón aceptar
        Button okButton = (Button) dialog.getDialogPane().lookupButton(btnAceptar);
        okButton.setDisable(true);

        // Habilitar botón solo si todos los campos son válidos
        Runnable validarTodo = () -> {
            boolean valido = DataValidator.validateRUC(txtRuc.getText().trim(), "RUC").isValid() &&
                    DataValidator.validateName(txtNombre.getText().trim(), "Nombre").isValid() &&
                    DataValidator.validateTelefonoE164(txtTelefono.getText().trim()).isValid() &&
                    DataValidator.validateCorreo(txtCorreo.getText().trim()).isValid() &&
                    DataValidator.validateNumeric(txtCuentaBancaria.getText().trim(), "Cuenta bancaria").isValid();

            okButton.setDisable(!valido);
        };

        txtRuc.textProperty().addListener((obs, oldVal, newVal) -> {
            ValidationResult result = DataValidator.validateRUC(newVal.trim(), "RUC");
            setStyle(txtRuc, result);
            validarTodo.run();
        });

        txtNombre.textProperty().addListener((obs, oldVal, newVal) -> {
            ValidationResult result = DataValidator.validateName(newVal.trim(), "Nombre");
            setStyle(txtNombre, result);
            validarTodo.run();
        });

        txtTelefono.textProperty().addListener((obs, oldVal, newVal) -> {
            ValidationResult result = DataValidator.validateTelefonoE164(newVal.trim());
            setStyle(txtTelefono, result);
            validarTodo.run();
        });

        txtCorreo.textProperty().addListener((obs, oldVal, newVal) -> {
            ValidationResult result = DataValidator.validateCorreo(newVal.trim());
            setStyle(txtCorreo, result);
            validarTodo.run();
        });

        txtCuentaBancaria.textProperty().addListener((obs, oldVal, newVal) -> {
            ValidationResult result = DataValidator.validateNumeric(newVal.trim(), "Cuenta bancaria");
            setStyle(txtCuentaBancaria, result);
            validarTodo.run();
        });
        Usuario usuarioActual = UserSession.getUsuarioActual();

        // Resultado del diálogo
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnAceptar) {
                Proveedor proveedor = new Proveedor(
                        txtRuc.getText(),
                        txtNombre.getText(),
                        txtRazonSocial.getText(),
                        txtTelefono.getText(),
                        txtDireccion.getText(),
                        txtCuentaBancaria.getText(),
                        txtCorreo.getText(),
                        dpFechaRegistro.getValue(),
                        proveedorExistente != null ? proveedorExistente.getEstado() : "Activo",
                        LocalDate.now(),
                        usuarioActual != null ? usuarioActual.getUsuario() : "Sistema");
                ;

                if (!validarProveedorEnDialogo(proveedor)) {
                    return null;
                }

                if (proveedorExistente != null) {
                    proveedorExistente.setRuc(proveedor.getRuc());
                    proveedorExistente.setNombre(proveedor.getNombre());
                    proveedorExistente.setRazon_social(proveedor.getRazon_social());
                    proveedorExistente.setTelefono(proveedor.getTelefono());
                    proveedorExistente.setDireccion(proveedor.getDireccion());
                    proveedorExistente.setCuenta_bancaria(proveedor.getCuenta_bancaria());
                    proveedorExistente.setCorreo(proveedor.getCorreo());
                    proveedorExistente.setFechaRegistro(proveedor.getFechaRegistro());
                    proveedorExistente.setFechaModificacion(proveedor.getFechaModificacion());
                    proveedorExistente.setUsuarioResponsable(UserSession.getUsuarioActual().getUsuario());
                    return proveedorExistente;
                }

                return proveedor;
            }
            return null;
        });

        validarTodo.run(); // Primera validación
        return dialog.showAndWait();
    }

    // Métodos auxiliares
    private void setStyle(TextField field, ValidationResult result) {
        if (!result.isValid()) {
            setErrorStyle(field, result.getErrorMessage());
        } else {
            clearErrorStyle(field);
        }
    }

    private boolean validarProveedorEnDialogo(Proveedor proveedor) {
        // Validaciones básicas de formato
        if (proveedor.getRuc() == null || proveedor.getRuc().trim().isEmpty()) {
            DialogHelper.showValidationError(getWindow(), "RUC", "El RUC es obligatorio");
            return false;
        }

        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            DialogHelper.showValidationError(getWindow(), "Nombre", "El nombre es obligatorio");
            return false;
        }

        if (proveedor.getRazon_social() == null || proveedor.getRazon_social().trim().isEmpty()) {
            DialogHelper.showValidationError(getWindow(), "Razón Social", "La razón social es obligatoria");
            return false;
        }

        if (proveedor.getTelefono() == null || proveedor.getTelefono().trim().isEmpty()) {
            DialogHelper.showValidationError(getWindow(), "Teléfono", "El teléfono es obligatorio");
            return false;
        }

        if (proveedor.getCorreo() == null || proveedor.getCorreo().trim().isEmpty()) {
            DialogHelper.showValidationError(getWindow(), "Correo", "El correo electrónico es obligatorio");
            return false;
        }

        if (proveedor.getDireccion() == null || proveedor.getDireccion().trim().isEmpty()) {
            DialogHelper.showValidationError(getWindow(), "Dirección", "La dirección es obligatoria");
            return false;
        }

        if (proveedor.getFecha_registro() == null) {
            DialogHelper.showValidationError(getWindow(), "Fecha de registro", "La fecha de registro es obligatoria");
            return false;
        }

        // Validaciones de formato usando DataValidator
        ValidationResult rucResult = DataValidator.validateRUC(proveedor.getRuc(), "RUC");
        if (!rucResult.isValid()) {
            rucResult.showErrorIfInvalid(getWindow());
            return false;
        }

        ValidationResult nameResult = DataValidator.validateName(proveedor.getNombre(), "Nombre");
        if (!nameResult.isValid()) {
            nameResult.showErrorIfInvalid(getWindow());
            return false;
        }

        ValidationResult phoneResult = DataValidator.validateTelefonoE164(proveedor.getTelefono());
        if (!phoneResult.isValid()) {
            phoneResult.showErrorIfInvalid(getWindow());
            return false;
        }

        ValidationResult emailResult = DataValidator.validateCorreo(proveedor.getCorreo());
        if (!emailResult.isValid()) {
            emailResult.showErrorIfInvalid(getWindow());
            return false;
        }

        // Validaciones de cuenta bancaria si está presente
        if (proveedor.getCuenta_bancaria() != null && !proveedor.getCuenta_bancaria().trim().isEmpty()) {
            ValidationResult cuentaResult = DataValidator.validateNumeric(proveedor.getCuenta_bancaria(),
                    "Cuenta bancaria");
            if (!cuentaResult.isValid()) {
                cuentaResult.showErrorIfInvalid(getWindow());
                return false;
            }
        }

        return true;
    }

    private boolean validarProveedor(Proveedor proveedor) {
        // Validar RUC
        ValidationResult rucResult = DataValidator.validateRUC(proveedor.getRuc(), "RUC");
        if (!rucResult.isValid()) {
            rucResult.showErrorIfInvalid(getWindow());
            return false;
        }

        // Validar nombre
        ValidationResult nameResult = DataValidator.validateName(proveedor.getNombre(), "Nombre");
        if (!nameResult.isValid()) {
            nameResult.showErrorIfInvalid(getWindow());
            return false;
        }

        // Validar razón social (obligatorio)
        if (proveedor.getRazon_social() == null || proveedor.getRazon_social().trim().isEmpty()) {
            DialogHelper.showValidationError(getWindow(), "Razón Social", "La razón social es obligatoria");
            return false;
        }

        if (proveedor.getRazon_social().trim().length() < 3) {
            DialogHelper.showValidationError(getWindow(), "Razón Social",
                    "La razón social debe tener al menos 3 caracteres");
            return false;
        }

        // Validar teléfono
        ValidationResult phoneResult = DataValidator.validateTelefonoE164(proveedor.getTelefono());
        if (!phoneResult.isValid()) {
            phoneResult.showErrorIfInvalid(getWindow());
            return false;
        }

        // Validar correo electrónico
        ValidationResult emailResult = DataValidator.validateCorreo(proveedor.getCorreo());
        if (!emailResult.isValid()) {
            emailResult.showErrorIfInvalid(getWindow());
            return false;
        }

        // Validar dirección (obligatorio)
        if (proveedor.getDireccion() == null || proveedor.getDireccion().trim().isEmpty()) {
            DialogHelper.showValidationError(getWindow(), "Dirección", "La dirección es obligatoria");
            return false;
        }

        if (proveedor.getDireccion().trim().length() < 10) {
            DialogHelper.showValidationError(getWindow(), "Dirección",
                    "La dirección debe tener al menos 10 caracteres");
            return false;
        }

        // Validar cuenta bancaria (numérico, opcional)
        if (proveedor.getCuenta_bancaria() != null && !proveedor.getCuenta_bancaria().trim().isEmpty()) {
            ValidationResult cuentaResult = DataValidator.validateNumeric(proveedor.getCuenta_bancaria(),
                    "Cuenta bancaria");
            if (!cuentaResult.isValid()) {
                cuentaResult.showErrorIfInvalid(getWindow());
                return false;
            }

            if (proveedor.getCuenta_bancaria().trim().length() < 10) {
                DialogHelper.showValidationError(getWindow(), "Cuenta bancaria",
                        "La cuenta bancaria debe tener al menos 10 dígitos");
                return false;
            }
        }

        // Validar fecha de registro
        if (proveedor.getFecha_registro() == null) {
            DialogHelper.showValidationError(getWindow(), "Fecha de registro", "La fecha de registro es obligatoria");
            return false;
        }

        if (proveedor.getFecha_registro().isAfter(LocalDate.now())) {
            DialogHelper.showValidationError(getWindow(), "Fecha de registro",
                    "La fecha de registro no puede ser futura");
            return false;
        }

        // Validar RUC único (no duplicado)
        for (Proveedor p : masterDataProveedores) {
            if (!p.equals(proveedor) && p.getRuc().equals(proveedor.getRuc())) {
                DialogHelper.showValidationError(getWindow(), "RUC",
                        "Ya existe un proveedor con este RUC: " + proveedor.getRuc());
                return false;
            }
        }

        // Validar correo único (no duplicado)
        for (Proveedor p : masterDataProveedores) {
            if (!p.equals(proveedor) && p.getCorreo().equalsIgnoreCase(proveedor.getCorreo())) {
                DialogHelper.showValidationError(getWindow(), "Correo electrónico",
                        "Ya existe un proveedor con este correo: " + proveedor.getCorreo());
                return false;
            }
        }

        return true;
    }

    /**
     * Validación específica para el diálogo de proveedor
     * Muestra errores inmediatamente sin cerrar el diálogo
     */
    private void cargarDatosProveedores() {
        masterDataProveedores.clear();
        masterDataProveedores.addAll(CSVUtil.leerProveedores());
    }

    private Window getWindow() {
        return btnBack.getScene().getWindow();
    }

    // Métodos para acceso desde otros controladores
    public ObservableList<Proveedor> getProveedores() {
        return masterDataProveedores;
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