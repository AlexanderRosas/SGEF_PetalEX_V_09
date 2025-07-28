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
    private TextField txtBuscar;

    private final ObservableList<Usuario> data = FXCollections.observableArrayList();
    private FilteredList<Usuario> filteredData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        cargarUsuarios();
        configurarFiltros();
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
            btnEstado.setText(
                    (seleccionado && "Activo".equalsIgnoreCase(nuevo.getEstado())) ? "Inactivar" : "Reactivar");
        });
    }

    private void cargarUsuarios() {
        List<Usuario> usuarios = CSVUtil.leerUsuarios();
        data.setAll(usuarios);
        filteredData = new FilteredList<>(data, u -> true);
        tablaUsuarios.setItems(filteredData);
    }

    private void configurarFiltros() {
        txtBuscar.textProperty().addListener((obs, o, n) -> {
            String filtro = n.toLowerCase().trim();

            filteredData.setPredicate(u -> {
                if (filtro.isEmpty())
                    return true;

                return u.getNombre().toLowerCase().contains(filtro) ||
                        u.getCedula().toLowerCase().contains(filtro) ||
                        u.getCorreo().toLowerCase().contains(filtro) ||
                        u.getEstado().toLowerCase().contains(filtro);
            });
        });
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
    TextInputDialog pwdDialog = new TextInputDialog();
    pwdDialog.initOwner(w);
    pwdDialog.setTitle("Verificación");
    pwdDialog.setHeaderText("Contraseña de Administrador");
    pwdDialog.setContentText("Ingrese su contraseña:");
    Optional<String> pwd = pwdDialog.showAndWait();

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
        TextInputDialog pwdDialog = new TextInputDialog();
        pwdDialog.initOwner(w);
        pwdDialog.setTitle("Verificación");
        pwdDialog.setHeaderText("Contraseña de Administrador");
        pwdDialog.setContentText("Ingrese su contraseña:");
        Optional<String> pwd = pwdDialog.showAndWait();

        if (pwd.isEmpty())
            return;

        Usuario admin = UserUtil.buscarUsuario(UserSession.getUsuarioActual().getCorreo(), pwd.get());
        if (admin == null || !"Administrador".equalsIgnoreCase(admin.getRol())) {
            DialogHelper.showError(w, "Contraseña incorrecta o sin privilegios.");
            return;
        }

        if (!DialogHelper.confirm(w, "¿Estás seguro/a de anular este usuario?"))
            return;

        String nuevoEstado = "Activo".equalsIgnoreCase(seleccionado.getEstado()) ? "Inactivo" : "Activo";
        seleccionado.setEstado(nuevoEstado);
        seleccionado.setUsuarioModificacion(admin.getUsuario());
        seleccionado.setFechaModificacion(LocalDateTime.now());
        CSVUtil.guardarUsuarios(data);
        tablaUsuarios.refresh();
        DialogHelper.showSuccess(w, "Usuario " + nuevoEstado.toLowerCase() + " correctamente.");
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

        ComboBox<String> cbRol = new ComboBox<>(
                FXCollections.observableArrayList(PermisosUtil.getRolesDisponibles()));

        if (usuarioExistente != null) {
            cbRol.setValue(usuarioExistente.getRol());
        }

        // Layout compacto
        grid.addRow(0, new Label("Nombre natural:"), txtNombre);
        grid.addRow(1, new Label("Correo:"), txtCorreo);
        grid.addRow(2, new Label("Usuario:"), txtUsuario);
        grid.addRow(3, new Label("Contraseña:"), txtPassword);
        grid.addRow(4, new Label("Rol:"), cbRol);
        grid.addRow(5, new Label("Cédula:"), txtCedula);

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
                    ? "¿Cancelar edición?"
                    : "¿Cancelar registro?";
            if (!DialogHelper.confirm(dialog.getDialogPane().getScene().getWindow(), mensaje)) {
                event.consume();
            }
        });

        // Resultado
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Usuario u = usuarioExistente != null ? usuarioExistente : new Usuario();
                u.setNombre(txtNombre.getText().trim());
                u.setCorreo(txtCorreo.getText().trim());
                u.setUsuario(txtUsuario.getText().trim());
                u.setRol(cbRol.getValue());
                u.setCedula(txtCedula.getText().trim());
                u.setPassword(txtPassword.getText().trim());

                // ✅ Actualizar fecha y usuario modificador
                u.setUsuarioModificacion(getUsuarioActual());
                u.setFechaModificacion(LocalDateTime.now());

                // Asignar permisos según rol
                List<Permiso> permisos = PermisosUtil.getPermisosPorRol(u.getRol());
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

    // Campos del formulario
    TextField txtNombre = new TextField(usuarioExistente.getNombre());
    TextField txtCorreo = new TextField(usuarioExistente.getCorreo());
    PasswordField txtPassword = new PasswordField();
    txtPassword.setPromptText("Nueva contraseña");
    ComboBox<String> cbRol = new ComboBox<>(FXCollections.observableArrayList(
            "Administrador", "Finanzas", "Gerente", "Ventas"));
    cbRol.setValue(usuarioExistente.getRol());

    // Agregar campos al GridPane
    grid.addRow(0, new Label("Nombre Natural:"), txtNombre);
    grid.addRow(1, new Label("Correo Electrónico:"), txtCorreo);
    grid.addRow(2, new Label("Contraseña:"), txtPassword);
    grid.addRow(3, new Label("Rol:"), cbRol);

    // Configurar el contenido del diálogo
    dialog.getDialogPane().setContent(grid);

    // Configurar el resultado del diálogo
    dialog.setResultConverter(btn -> {
        if (btn == ButtonType.OK) {
            usuarioExistente.setNombre(txtNombre.getText().trim());
            usuarioExistente.setCorreo(txtCorreo.getText().trim());
            usuarioExistente.setPassword(txtPassword.getText().trim());
            usuarioExistente.setRol(cbRol.getValue());
            return usuarioExistente;
        }
        return null;
    });

    // Mostrar el diálogo y esperar la respuesta
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
        return "admin"; // Simulación
    }
}
