package org.example.sgef_petalex_v_09.controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.*;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import org.example.sgef_petalex_v_09.models.Cliente;
import org.example.sgef_petalex_v_09.util.*;
import org.example.sgef_petalex_v_09.validators.DataValidator;
import org.example.sgef_petalex_v_09.validators.ValidationResult;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class ClientesController {

    @FXML
    private TableView<Cliente> tablaClientes;
    @FXML
    private TableColumn<Cliente, String> colId;
    @FXML
    private TableColumn<Cliente, String> colNombre;
    @FXML
    private TableColumn<Cliente, String> colIdentificador;
    @FXML
    private TableColumn<Cliente, String> colPais;
    @FXML
    private TableColumn<Cliente, String> colDireccion;
    @FXML
    private TableColumn<Cliente, String> colTelefono;
    @FXML
    private TableColumn<Cliente, String> colCorreo;
    @FXML
    private TableColumn<Cliente, String> colEstado;
    @FXML
    private TableColumn<Cliente, String> colUsuarioModificacion;
    @FXML
    private TableColumn<Cliente, String> colFechaModificacion;

    @FXML
    private TextField txtBuscarNombre;
    @FXML
    private TextField txtBuscarIdentificador;
    @FXML
    private ComboBox<String> cbPais;
    @FXML
    private ComboBox<String> cbEstado;

    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEstado;
    @FXML
    private Button btnBack;

    private final ObservableList<Cliente> data = FXCollections.observableArrayList();
    private FilteredList<Cliente> filteredData;

    @FXML
    public void initialize() {
        configurarColumnas();
        configurarFiltros();
        cargarDatosDesdeCSV();
        tablaClientes.setItems(filteredData);
        btnEstado.setText("Cambiar estado");

    }

    private String getUsuarioActual() {
        var usuario = UserSession.getUsuarioActual();
        return (usuario != null) ? usuario.getUsuario() : "desconocido";
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colIdentificador.setCellValueFactory(new PropertyValueFactory<>("identificadorEmpresarial"));
        colPais.setCellValueFactory(new PropertyValueFactory<>("pais"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colUsuarioModificacion.setCellValueFactory(new PropertyValueFactory<>("usuarioModificacion"));
        colFechaModificacion.setCellValueFactory(cellData -> {
            LocalDateTime fecha = cellData.getValue().getFechaModificacion();
            String texto = fecha != null ? fecha.toString().replace("T", " ") : "";
            return new ReadOnlyStringWrapper(texto);
        });

        cbPais.setItems(FXCollections.observableArrayList(PaisUtil.PREFIJOS.keySet()));
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "Activa", "Inactiva"));
        cbEstado.setValue("Todos");

        tablaClientes.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldSel, newSel) -> {
                    boolean sel = newSel != null;
                    btnEditar.setDisable(!sel);
                    btnEstado.setDisable(!sel);
                });
    }

    private void configurarFiltros() {
        filteredData = new FilteredList<>(data, p -> true);
        txtBuscarNombre.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
        txtBuscarIdentificador.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
        cbPais.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
        cbEstado.valueProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        String nombre = txtBuscarNombre.getText().toLowerCase();
        String identificador = txtBuscarIdentificador.getText().toLowerCase();
        String pais = cbPais.getValue();
        String estado = cbEstado.getValue();

        filteredData.setPredicate(cliente -> (nombre.isEmpty() || cliente.getNombre().toLowerCase().contains(nombre)) &&
                (identificador.isEmpty() || cliente.getIdentificadorEmpresarial().toLowerCase().contains(identificador))
                &&
                (pais == null || pais.equals("Todos") || cliente.getPais().equalsIgnoreCase(pais)) &&
                (estado.equals("Todos") || cliente.getEstado().equalsIgnoreCase(estado)));
    }

    private void cargarDatosDesdeCSV() {
        data.clear();
        data.addAll(CSVUtil.leerClientes());
    }

    @FXML
    private void onNuevo(ActionEvent event) {
        Optional<Cliente> result = showClientForm("Crear", null);
        result.ifPresent(cliente -> {
            if (existeIdentificador(cliente.getIdentificadorEmpresarial())) {
                DialogHelper.showError(btnNuevo.getScene().getWindow(), "El identificador Empresarial ya existe");
                return;
            }

            cliente.setEstado("Activa");
            cliente.setUsuarioModificacion(getUsuarioActual());
            cliente.setFechaModificacion(LocalDateTime.now());

            data.add(cliente);
            System.out.println("Empresas Clientes a guardar: " + data.size());
            for (Cliente c : data) {
                System.out.println(" -> " + c.getNombre() + " [" + c.getIdentificadorEmpresarial() + "]");
            }

            CSVUtil.guardarClientes(data);
            DialogHelper.showSuccess(btnNuevo.getScene().getWindow(), "Empresa Cliente registrada exitosamente");
        });
    }

    @FXML
    private void onEditar(ActionEvent event) {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;

        Optional<Cliente> result = showClientForm("Editar", sel);
        result.ifPresent(cliente -> {
            sel.setNombre(cliente.getNombre());
            sel.setDireccion(cliente.getDireccion());
            sel.setTelefono(cliente.getTelefono());
            sel.setCorreo(cliente.getCorreo());
            sel.setUsuarioModificacion(getUsuarioActual());
            sel.setFechaModificacion(LocalDateTime.now());
            System.out.println("Antes de guardar:");
            data.forEach(c -> System.out.println(c.getNombre() + " - " + c.getDireccion()));

            CSVUtil.guardarClientes(data);

            System.out.println("Guardado completado.");

            List<Cliente> clientesDesdeArchivo = CSVUtil.leerClientes();
            System.out.println("Leídos desde archivo:");
            clientesDesdeArchivo.forEach(c -> System.out.println(c.getNombre() + " - " + c.getDireccion()));
            tablaClientes.refresh();

            DialogHelper.showSuccess(btnEditar.getScene().getWindow(), "Empresa Cliente actualizada");

        });
    }

    @FXML
    private void onEstado(ActionEvent event) {
        Cliente sel = tablaClientes.getSelectionModel().getSelectedItem();
        if (sel == null)
            return;

        String nuevoEstado = sel.getEstado().equalsIgnoreCase("Activa") ? "Inactiva" : "Activa";
        String mensajeConfirmacion = "¿Está seguro/a de cambiar el estado de la Empresa Cliente?'" + nuevoEstado + "'?";

        boolean confirmed = DialogHelper.confirm(btnEstado.getScene().getWindow(), mensajeConfirmacion);
        if (confirmed) {
            sel.setEstado(nuevoEstado);
            sel.setUsuarioModificacion(getUsuarioActual());
            sel.setFechaModificacion(LocalDateTime.now());

            CSVUtil.guardarClientes(data);
            tablaClientes.refresh();
            DialogHelper.showSuccess(btnEstado.getScene().getWindow(), "Estado cambiado a '" + nuevoEstado + "'");
        }
    }

    private Optional<Cliente> showClientForm(String title, Cliente existing) {
        Dialog<Cliente> dialog = new Dialog<>();
        dialog.setTitle(title + " cliente");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        ComboBox<String> cbPais = new ComboBox<>(FXCollections.observableArrayList(PaisUtil.PREFIJOS.keySet()));
        TextField txtIdentificador = new TextField();
        TextField txtNombre = new TextField();
        TextField txtDireccion = new TextField();
        TextField txtTelefono = new TextField();
        TextField txtCorreo = new TextField();

        // Placeholders
        txtIdentificador.setPromptText("Ej: 1790012345001");
        txtNombre.setPromptText("Ej: GlobalRoses S.A.");
        txtDireccion.setPromptText("Ej: Av. Amazonas 123 y Naciones Unidas");
        txtCorreo.setPromptText("Ej: contacto@globalroses.com");

        cbPais.getSelectionModel().selectedItemProperty().addListener((obs, oldPais, newPais) -> {
            if (newPais != null) {
                String prefijo = PaisUtil.getPrefijo(newPais);
                txtTelefono.setPromptText(prefijo + " Ej: 9912345678");
                txtTelefono.setText(prefijo);
                txtIdentificador.setPromptText("Ej: " + obtenerEjemploIdentificador(newPais));
            }
        });

        if (existing != null) {
            cbPais.setValue(existing.getPais());
            txtIdentificador.setText(existing.getIdentificadorEmpresarial());
            txtIdentificador.setDisable(true);
            cbPais.setDisable(true);
            txtNombre.setText(existing.getNombre());
            txtDireccion.setText(existing.getDireccion());
            txtTelefono.setText(existing.getTelefono());
            txtCorreo.setText(existing.getCorreo());
        } else {
            cbPais.getSelectionModel().selectFirst();
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        grid.addRow(0, new Label("País:"), cbPais);
        grid.addRow(1, new Label("Identificador:"), txtIdentificador);
        grid.addRow(2, new Label("Nombre:"), txtNombre);
        grid.addRow(3, new Label("Dirección:"), txtDireccion);
        grid.addRow(4, new Label("Teléfono:"), txtTelefono);
        grid.addRow(5, new Label("Correo:"), txtCorreo);

        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            ValidationResult vIdentificador = DataValidator.validateIdentificador(txtIdentificador.getText(),
                    cbPais.getValue());
            ValidationResult vNombre = DataValidator.validateEmpresaNombre(txtNombre.getText());
            ValidationResult vDireccion = DataValidator.validateDireccion(txtDireccion.getText());
            ValidationResult vTelefono = DataValidator.validateTelefonoE164(txtTelefono.getText());
            ValidationResult vCorreo = DataValidator.validateCorreo(txtCorreo.getText());

            clearErrorStyle(txtIdentificador);
            clearErrorStyle(txtNombre);
            clearErrorStyle(txtDireccion);
            clearErrorStyle(txtTelefono);
            clearErrorStyle(txtCorreo);

            boolean valid = true;

            if (!vIdentificador.isValid()) {
                setErrorStyle(txtIdentificador, vIdentificador.getErrorMessage());
                valid = false;
            }
            if (!vNombre.isValid()) {
                setErrorStyle(txtNombre, vNombre.getErrorMessage());
                valid = false;
            }
            if (!vDireccion.isValid()) {
                setErrorStyle(txtDireccion, vDireccion.getErrorMessage());
                valid = false;
            }
            if (!vTelefono.isValid()) {
                setErrorStyle(txtTelefono, vTelefono.getErrorMessage());
                valid = false;
            }
            if (!vCorreo.isValid()) {
                setErrorStyle(txtCorreo, vCorreo.getErrorMessage());
                valid = false;
            }

            if (!valid) {
                event.consume();
                DialogHelper.showError(dialog.getDialogPane().getScene().getWindow(),
                        "Por favor corrige los campos resaltados con errores antes de continuar.");
            }
        });

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.addEventFilter(ActionEvent.ACTION, event -> {
            String mensaje = (existing != null)
                    ? "¿Está seguro/a de cancelar el proceso de actualización de información de la Empresa Cliente?"
                    : "¿Está seguro/a de cancelar el registro de la Empresa Cliente?";

            boolean confirmed = DialogHelper.confirm(dialog.getDialogPane().getScene().getWindow(), mensaje);
            if (!confirmed) {
                event.consume();
            }
        });
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Cliente c = new Cliente();
                c.setId(existing != null ? existing.getId() : generarNuevoIdIncremental());
                c.setNombre(txtNombre.getText());
                c.setIdentificadorEmpresarial(txtIdentificador.getText());
                c.setPais(cbPais.getValue());
                c.setDireccion(txtDireccion.getText());
                c.setTelefono(txtTelefono.getText());
                c.setCorreo(txtCorreo.getText());
                c.setEstado(existing != null ? existing.getEstado() : "Activa");
                c.setUsuarioModificacion(getUsuarioActual());
                c.setFechaModificacion(LocalDateTime.now());
                return c;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    // Métodos auxiliares de la clase (no dentro de showClientForm)
    private void setErrorStyle(TextField field, String message) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
        Tooltip tooltip = new Tooltip(message);
        tooltip.setStyle("-fx-background-color: #ffdddd; -fx-text-fill: red;");
        field.setTooltip(tooltip);
    }

    private void clearErrorStyle(TextField field) {
        field.setStyle(null);
        field.setTooltip(null);
    }

    // Método auxiliar para ejemplos de identificador según país
    private String obtenerEjemploIdentificador(String pais) {
        return switch (pais.toUpperCase()) {
            case "ECUADOR" -> "1790012345001";
            case "ESTADOS UNIDOS" -> "12-3456789";
            case "CANADÁ" -> "123456789";
            case "ESPAÑA" -> "ES12345678X";
            case "ALEMANIA" -> "DE123456789";
            case "FRANCIA" -> "FR12345678901";
            case "PAÍSES BAJOS" -> "NL123456789B01";
            default -> "";
        };
    }

    private boolean existeIdentificador(String identificador) {
        return data.stream().anyMatch(c -> c.getIdentificadorEmpresarial().equalsIgnoreCase(identificador));
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

    private String generarNuevoIdIncremental() {
        if (data.isEmpty()) {
            return "1";
        }
        int maxId = data.stream()
                .map(Cliente::getId)
                .filter(id -> id.matches("\\d+")) // solo IDs numéricos válidos
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);
        return String.valueOf(maxId + 1);
    }

}