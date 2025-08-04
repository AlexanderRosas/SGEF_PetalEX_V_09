package org.example.sgef_petalex_v_09.controllers;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Permiso;
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.CSVUtil;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.util.PermisosUtil;
import org.example.sgef_petalex_v_09.util.UserSession;
import org.example.sgef_petalex_v_09.util.UserUtil;
import org.example.sgef_petalex_v_09.validators.DataValidator;
import org.example.sgef_petalex_v_09.validators.ValidationResult;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class GestionUsuariosController implements Initializable {

    @FXML
    private TableView<Usuario> tablaUsuarios;

    @FXML
    private TableColumn<Usuario, String> colNombreNatural, colCedula, colCorreo, colUsuario, colRol, colEstado,
            colPermisos,
            colFechaModificacion, colUsuarioModificador;

    @FXML
    private Button btnNuevo, btnEditar, btnEstado;
    @FXML
    private TextField txtBuscarNombreNatural;
    @FXML
    private TextField txtBuscarNombreUsuario;
    @FXML
    private TextField txtBuscarCedula;
    @FXML
    private TextField txtBuscarCorreo;
    @FXML
    private Label lblNoUsuarios;
    @FXML
    private ComboBox<String> cbEstado;
    private final ObservableList<Usuario> data = FXCollections.observableArrayList();
    private FilteredList<Usuario> filteredData;
    private boolean mostrarErrorSiNoHayResultados = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarUsuarios();
        configurarFiltros();
        cbEstado.setItems(FXCollections.observableArrayList("Todos", "Activo", "Inactivo"));
        cbEstado.setValue("Todos");
    }

    private void configurarTabla() {
        colNombreNatural.setCellValueFactory(c -> c.getValue().nombreProperty());
        colCedula.setCellValueFactory(data -> data.getValue().cedulaProperty());
        colCorreo.setCellValueFactory(c -> c.getValue().correoProperty());
        colUsuario.setCellValueFactory(c -> c.getValue().usuarioProperty());
        colRol.setCellValueFactory(c -> c.getValue().rolProperty());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());
        colPermisos.setCellValueFactory(c -> c.getValue().permisosProperty());
        colFechaModificacion.setCellValueFactory(cellData -> {
            LocalDateTime fecha = cellData.getValue().getFechaModificacion();
            String texto = (fecha != null) ? fecha.toString().replace("T", " ") : "";
            return new ReadOnlyStringWrapper(texto);
        });
        colUsuarioModificador.setCellValueFactory(c -> c.getValue().usuarioModificacionProperty());

        btnEditar.setDisable(true);
        btnEstado.setDisable(true);

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            boolean seleccionado = nuevo != null;
            btnEditar.setDisable(!seleccionado);
            btnEstado.setDisable(!seleccionado);

        });
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = CSVUtil.leerUsuarios();
        data.setAll(usuarios);
        filteredData = new FilteredList<>(data, u -> true);
        tablaUsuarios.setItems(filteredData);
    }

    private void configurarFiltros() {
        txtBuscarNombreNatural.textProperty().addListener((obs, o, n) -> aplicarFiltro());
        txtBuscarNombreUsuario.textProperty().addListener((obs, o, n) -> aplicarFiltro());
        txtBuscarCedula.textProperty().addListener((obs, o, n) -> aplicarFiltro());
        txtBuscarCorreo.textProperty().addListener((obs, o, n) -> aplicarFiltro());
        cbEstado.valueProperty().addListener((obs, o, n) -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        String nombreNatural = txtBuscarNombreNatural == null ? ""
                : txtBuscarNombreNatural.getText().toLowerCase().trim();
        String nombreUsuario = txtBuscarNombreUsuario == null ? ""
                : txtBuscarNombreUsuario.getText().toLowerCase().trim();
        String cedula = txtBuscarCedula.getText().toLowerCase().trim();
        String estado = cbEstado.getValue();

        boolean hayFiltro = !nombreNatural.isEmpty()
                || !nombreUsuario.isEmpty()
                || !cedula.isEmpty()
                || (estado != null && !estado.equals("Todos"));

        filteredData.setPredicate(u -> {
            if (!hayFiltro)
                return true;

            boolean nombreNaturalMatch = nombreNatural.isEmpty()
                    || u.getNombre().toLowerCase().contains(nombreNatural);
            boolean nombreUsuarioMatch = nombreUsuario.isEmpty()
                    || u.getUsuario().toLowerCase().contains(nombreUsuario);
            boolean cedulaMatch = cedula.isEmpty()
                    || u.getCedula().trim().toLowerCase().startsWith(cedula);
            boolean estadoMatch = estado == null || estado.equals("Todos")
                    || u.getEstado().equalsIgnoreCase(estado);

            return nombreNaturalMatch && nombreUsuarioMatch && cedulaMatch && estadoMatch;
        });

        boolean sinResultados = filteredData.isEmpty();

    }

    @FXML
    private void onNuevo(ActionEvent ev) {
        Window w = getWindow(ev);
        Optional<Usuario> resultado = mostrarFormulario("Crear usuario", null);
        resultado.ifPresent(u -> {
            if (DialogHelper.confirm(w, "¿Crear usuario?")) {
                u.setId("U" + String.format("%03d", new Random().nextInt(1000)));
                u.setEstado("Activo");
                u.setUsuarioModificacion(getUsuarioActual());
                u.setFechaModificacion(LocalDateTime.now());
                data.add(u);
                CSVUtil.guardarUsuarios(data);
                tablaUsuarios.refresh();
                DialogHelper.showSuccess(w, "Usuario creado");
            }
        });
    }

    @FXML
    private void onEditar(ActionEvent ev) {
        Window w = getWindow(ev);
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            DialogHelper.showWarning(w, "Seleccione un usuario");
            return;
        }

        // Pedir contraseña del administrador en sesión
        Optional<String> pwd = pedirPasswordRolAutorizado(w);

        if (pwd.isEmpty())
            return;

        Usuario admin = UserUtil.buscarUsuario(UserSession.getUsuarioActual().getCorreo(), pwd.get());
        if (admin == null || !"Administrador".equalsIgnoreCase(admin.getRol())) {
            DialogHelper.showError(w, "Contraseña incorrecta o sin privilegios.");
            return;
        }

        // Formulario de edición
        Optional<Usuario> resultado = mostrarFormularioEdicion(seleccionado);
        resultado.ifPresent(u -> {
            seleccionado.setNombre(u.getNombre());
            seleccionado.setCorreo(u.getCorreo());
            seleccionado.setPassword(u.getPassword());
            seleccionado.setRol(u.getRol());
            seleccionado.setUsuarioModificacion(admin.getUsuario());
            seleccionado.setFechaModificacion(LocalDateTime.now());
            CSVUtil.guardarUsuarios(data);
            tablaUsuarios.refresh();
            DialogHelper.showSuccess(w, "Usuario actualizado");
        });
    }

    @FXML
    private void onEstado(ActionEvent ev) {
        Window w = getWindow(ev);
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null)
            return;

        // Pedir contraseña del administrador en sesión
        Optional<String> pwd = pedirPasswordRolAutorizado(w);
        if ("Administrador".equalsIgnoreCase(seleccionado.getRol())) {
            DialogHelper.showError(w, "No se puede inactivar a un administrador.");
            return;
        }
        if (pwd.isEmpty())
            return;

        Usuario admin = UserUtil.buscarUsuario(UserSession.getUsuarioActual().getCorreo(), pwd.get());
        if (admin == null || !"Administrador".equalsIgnoreCase(admin.getRol())) {
            DialogHelper.showError(w, "Contraseña incorrecta o sin privilegios.");
            return;
        }

        String accion = "Activo".equalsIgnoreCase(seleccionado.getEstado()) ? "inactivar" : "activar";
        if (!DialogHelper.confirm(w, "¿Está seguro/a de " + accion + " al usuario seleccionado?"))
            return;
        if ("Administrador".equalsIgnoreCase(seleccionado.getRol())) {
            DialogHelper.showError(w, "No se puede inactivar a un administrador.");
            return;
        }
        String nuevoEstado = "Activo".equalsIgnoreCase(seleccionado.getEstado()) ? "Inactivo" : "Activo";
        String accionRealizada = "Activo".equalsIgnoreCase(nuevoEstado) ? "activado" : "inactivado";

        seleccionado.setEstado(nuevoEstado);
        seleccionado.setUsuarioModificacion(admin.getUsuario());
        seleccionado.setFechaModificacion(LocalDateTime.now());
        CSVUtil.guardarUsuarios(data);
        tablaUsuarios.refresh();
        DialogHelper.showSuccess(w, "Usuario " + accionRealizada);
        btnEstado.setText("Activo".equalsIgnoreCase(nuevoEstado) ? "Inactivar" : "Reactivar");

    }

    private Optional<Usuario> mostrarFormulario(String titulo, Usuario usuarioExistente) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Campos
        TextField txtNombre = new TextField(usuarioExistente != null ? usuarioExistente.getNombre() : "");
        TextField txtCorreo = new TextField(usuarioExistente != null ? usuarioExistente.getCorreo() : "");
        TextField txtUsuario = new TextField(usuarioExistente != null ? usuarioExistente.getUsuario() : "");
        TextField txtCedula = new TextField(usuarioExistente != null ? usuarioExistente.getCedula() : "");
        PasswordField txtPassword = new PasswordField();

        ComboBox<String> cbRol = new ComboBox<>(FXCollections.observableArrayList(PermisosUtil.getRolesDisponibles()));
        if (usuarioExistente != null)
            cbRol.setValue(usuarioExistente.getRol());

        // Placeholders con ejemplos
        txtNombre.setPromptText("Ej: Juan Pérez");
        txtCorreo.setPromptText("Ej: juan.perez@example.com");
        txtUsuario.setPromptText("Ej: jperez");
        txtCedula.setPromptText("Ej: 1750111211");
        txtPassword.setPromptText("Ej: Pass123");
        // Layout
        grid.addRow(0, new Label("Nombre natural:"), txtNombre);
        grid.addRow(1, new Label("Correo:"), txtCorreo);
        grid.addRow(2, new Label("Usuario:"), txtUsuario);
        grid.addRow(3, new Label("Contraseña:"), txtPassword);
        grid.addRow(4, new Label("Rol:"), cbRol);
        grid.addRow(5, new Label("Cédula:"), txtCedula);
        dialog.setResizable(false);
        dialog.getDialogPane().setPrefSize(340, 280);
        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);

        Runnable clearErrors = () -> {
            clearErrorStyle(txtNombre);
            clearErrorStyle(txtCorreo);
            clearErrorStyle(txtUsuario);
            clearErrorStyle(txtCedula);
            clearErrorStyle(txtPassword);
        };

        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            clearErrors.run();
            boolean valid = true;

            // Validaciones formato
            ValidationResult vNombre = DataValidator.validateNaturalName(txtNombre.getText());
            ValidationResult vCorreo = DataValidator.validateCorreo(txtCorreo.getText());
            ValidationResult vUsuario = DataValidator.validateUsername(txtUsuario.getText());
            ValidationResult vPassword = DataValidator.validatePassword(txtPassword.getText());
            ValidationResult vCedula = DataValidator.validateEcuadorianID(txtCedula.getText(), "Cédula");

            if (!vNombre.isValid()) {
                setErrorStyle(txtNombre, vNombre.getErrorMessage());
                valid = false;
            }
            if (!vCorreo.isValid()) {
                setErrorStyle(txtCorreo, vCorreo.getErrorMessage());
                valid = false;
            }
            if (!vUsuario.isValid()) {
                setErrorStyle(txtUsuario, vUsuario.getErrorMessage());
                valid = false;
            }
            if (!vPassword.isValid()) {
                setErrorStyle(txtPassword, vPassword.getErrorMessage());
                valid = false;
            }
            if (!vCedula.isValid()) {
                setErrorStyle(txtCedula, vCedula.getErrorMessage());
                valid = false;
            }

            // Validaciones duplicados
            Set<String> cedulas = data.stream().map(u -> u.getCedula().toLowerCase()).collect(Collectors.toSet());
            Set<String> usuarios = data.stream().map(u -> u.getUsuario().toLowerCase()).collect(Collectors.toSet());
            Set<String> correos = data.stream().map(u -> u.getCorreo().toLowerCase()).collect(Collectors.toSet());

            String cedulaActual = usuarioExistente != null ? usuarioExistente.getCedula().toLowerCase() : "";
            String usuarioActual = usuarioExistente != null ? usuarioExistente.getUsuario().toLowerCase() : "";
            String correoActual = usuarioExistente != null ? usuarioExistente.getCorreo().toLowerCase() : "";

            ValidationResult vCedulaDup = DataValidator.validarDuplicado(txtCedula.getText(), "Cédula", cedulas);
            ValidationResult vUsuarioDup = DataValidator.validarDuplicado(txtUsuario.getText(), "Usuario", usuarios);
            ValidationResult vCorreoDup = DataValidator.validarDuplicado(txtCorreo.getText(), "Correo", correos);

            if (!vCedulaDup.isValid() && !txtCedula.getText().trim().equalsIgnoreCase(cedulaActual)) {
                setErrorStyle(txtCedula, vCedulaDup.getErrorMessage());
                valid = false;
            }
            if (!vUsuarioDup.isValid() && !txtUsuario.getText().trim().equalsIgnoreCase(usuarioActual)) {
                setErrorStyle(txtUsuario, vUsuarioDup.getErrorMessage());
                valid = false;
            }
            if (!vCorreoDup.isValid() && !txtCorreo.getText().trim().equalsIgnoreCase(correoActual)) {
                setErrorStyle(txtCorreo, vCorreoDup.getErrorMessage());
                valid = false;
            }

            if (!valid) {
                event.consume();
                DialogHelper.showError(dialog.getDialogPane().getScene().getWindow(),
                        "Por favor corrige los campos resaltados antes de continuar.");
            }
        });

        // Cancelar
        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.addEventFilter(ActionEvent.ACTION, event -> {
            String mensaje = usuarioExistente != null
                    ? "¿Está seguro/a de cancelar la edición de este Usuario?"
                    : "¿Está seguro/a de cancelar el registro de este Usuario?";
            if (!DialogHelper.confirm(dialog.getDialogPane().getScene().getWindow(), mensaje)) {
                event.consume();
            }
        });

        // Resultado
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                boolean valid = true;

                // Re-validar por seguridad
                ValidationResult vNombre = DataValidator.validateNaturalName(txtNombre.getText());
                ValidationResult vCorreo = DataValidator.validateCorreo(txtCorreo.getText());
                ValidationResult vUsuario = DataValidator.validateUsername(txtUsuario.getText());
                ValidationResult vPassword = DataValidator.validatePassword(txtPassword.getText());
                ValidationResult vCedula = DataValidator.validateEcuadorianID(txtCedula.getText(), "Cédula");

                if (!vNombre.isValid() || !vCorreo.isValid() || !vUsuario.isValid() || !vPassword.isValid()
                        || !vCedula.isValid()) {
                    return null;
                }

                Usuario u = usuarioExistente != null ? usuarioExistente : new Usuario();
                u.setNombre(txtNombre.getText().trim());
                u.setCorreo(txtCorreo.getText().trim());
                u.setUsuario(txtUsuario.getText().trim());
                u.setRol(cbRol.getValue());
                u.setCedula(txtCedula.getText().trim());
                u.setPassword(txtPassword.getText().trim());
                u.setUsuarioModificacion(getUsuarioActual());
                u.setFechaModificacion(LocalDateTime.now());

                List<Permiso> permisos = PermisosUtil.getModulosPorRol(u.getRol());
                u.setPermisos(permisos.stream().map(Permiso::getCodigo).collect(Collectors.joining(",")));
                return u;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Optional<Usuario> mostrarFormularioEdicion(Usuario usuarioExistente) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle("Editar usuario");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNombre = new TextField(usuarioExistente.getNombre());
        TextField txtCorreo = new TextField(usuarioExistente.getCorreo());
        TextField txtUsuario = new TextField(usuarioExistente.getUsuario());
        PasswordField txtPassword = new PasswordField();
        txtPassword.setPromptText("Dejar vacío para no cambiar");
        txtUsuario.setDisable(true);
        // Crear ComboBox de roles
        ComboBox<String> cbRol = new ComboBox<>(FXCollections.observableArrayList(PermisosUtil.getRolesDisponibles()));
        cbRol.setValue(usuarioExistente.getRol());
        txtNombre.setDisable(true); // No editable
        // Si el usuario es Administrador, deshabilitar el ComboBox de roles
        if ("Administrador".equalsIgnoreCase(usuarioExistente.getRol())) {
            cbRol.setDisable(true); // desactivar ComboBox
            cbRol.setStyle("-fx-opacity: 0.6;"); // aspecto visual
        }

        grid.addRow(0, new Label("Nombre Natural:"), txtNombre);
        grid.addRow(2, new Label("Usuario:"), txtUsuario);
        grid.addRow(1, new Label("Correo Electrónico:"), txtCorreo);
        grid.addRow(3, new Label("Contraseña:"), txtPassword);
        grid.addRow(4, new Label("Rol:"), cbRol);
        dialog.setResizable(false);
        dialog.getDialogPane().setPrefSize(340, 280);
        dialog.getDialogPane().setContent(grid);

        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        Runnable clearErrors = () -> {
            clearErrorStyle(txtNombre);
            clearErrorStyle(txtCorreo);
            clearErrorStyle(txtUsuario);
        };

        okButton.addEventFilter(ActionEvent.ACTION, event -> {
            clearErrors.run();
            boolean valid = true;

            ValidationResult vNombre = DataValidator.validateNaturalName(txtNombre.getText());
            ValidationResult vCorreo = DataValidator.validateCorreo(txtCorreo.getText());
            ValidationResult vUsuario = DataValidator.validateUsername(txtUsuario.getText());

            if (!vNombre.isValid()) {
                setErrorStyle(txtNombre, vNombre.getErrorMessage());
                valid = false;
            }
            if (!vCorreo.isValid()) {
                setErrorStyle(txtCorreo, vCorreo.getErrorMessage());
                valid = false;
            }
            if (!vUsuario.isValid()) {
                setErrorStyle(txtUsuario, vUsuario.getErrorMessage());
                valid = false;
            }

            // Validar duplicados
            Set<String> usuarios = data.stream().map(u -> u.getUsuario().toLowerCase()).collect(Collectors.toSet());
            Set<String> correos = data.stream().map(u -> u.getCorreo().toLowerCase()).collect(Collectors.toSet());

            String usuarioActual = usuarioExistente.getUsuario().toLowerCase();
            String correoActual = usuarioExistente.getCorreo().toLowerCase();

            ValidationResult vUsuarioDup = DataValidator.validarDuplicado(txtUsuario.getText(), "Usuario", usuarios);
            ValidationResult vCorreoDup = DataValidator.validarDuplicado(txtCorreo.getText(), "Correo", correos);

            if (!vUsuarioDup.isValid() && !txtUsuario.getText().trim().equalsIgnoreCase(usuarioActual)) {
                setErrorStyle(txtUsuario, vUsuarioDup.getErrorMessage());
                valid = false;
            }
            if (!vCorreoDup.isValid() && !txtCorreo.getText().trim().equalsIgnoreCase(correoActual)) {
                setErrorStyle(txtCorreo, vCorreoDup.getErrorMessage());
                valid = false;
            }

            if (!valid) {
                event.consume();
                DialogHelper.showError(dialog.getDialogPane().getScene().getWindow(),
                        "Por favor corrige los campos resaltados.");
            }
        });

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                usuarioExistente.setNombre(txtNombre.getText().trim());
                usuarioExistente.setCorreo(txtCorreo.getText().trim());
                usuarioExistente.setUsuario(txtUsuario.getText().trim());
                usuarioExistente.setRol(cbRol.getValue());

                String newPassword = txtPassword.getText().trim();
                if (!newPassword.isEmpty()) {
                    usuarioExistente.setPassword(newPassword);
                }

                usuarioExistente.setUsuarioModificacion(getUsuarioActual());
                usuarioExistente.setFechaModificacion(LocalDateTime.now());

                List<Permiso> permisos = PermisosUtil.getModulosPorRol(usuarioExistente.getRol());
                usuarioExistente
                        .setPermisos(permisos.stream().map(Permiso::getCodigo).collect(Collectors.joining(",")));

                return usuarioExistente;
            }
            return null;
        });

        return dialog.showAndWait();
    }

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

    private Window getWindow(ActionEvent ev) {
        return ((Node) ev.getSource()).getScene().getWindow();
    }

    private String getUsuarioActual() {
        Usuario actual = UserSession.getUsuarioActual();
        return (actual != null) ? actual.getUsuario() : "desconocido";
    }

    private Optional<String> pedirPasswordRolAutorizado(Window owner) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Verificación");
        dialog.setHeaderText("Contraseña de Administrador o Gerente");
        dialog.initOwner(owner);

        ButtonType loginButtonType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Ingrese su contraseña");

        GridPane grid = new GridPane();
        grid.add(new Label("Contraseña:"), 0, 0);
        grid.add(passwordField, 1, 0);
        dialog.getDialogPane().setContent(grid);

        Node okButton = dialog.getDialogPane().lookupButton(loginButtonType);
        okButton.setDisable(true);
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> okButton.setDisable(newVal.trim().isEmpty()));

        dialog.setResultConverter(btn -> {
            if (btn == loginButtonType)
                return passwordField.getText();
            return null;
        });

        return dialog.showAndWait();
    }
}
