package org.example.sgef_petalex_v_09.controllers;

import javafx.beans.binding.BooleanBinding;
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

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class GestionUsuariosController implements Initializable {

    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML
    private TableColumn<Usuario, String> colId, colNombre, colCorreo, colUsuario, colRol, colEstado, colSucursal, colRuc, colPermisos;

    @FXML
    private Button btnNuevo, btnEditar, btnEstado, btnReactivar;
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
        colId.setCellValueFactory(c -> c.getValue().idProperty());
        colNombre.setCellValueFactory(c -> c.getValue().nombreProperty());
        colCorreo.setCellValueFactory(c -> c.getValue().correoProperty());
        colUsuario.setCellValueFactory(c -> c.getValue().usuarioProperty());
        colRol.setCellValueFactory(c -> c.getValue().rolProperty());
        colEstado.setCellValueFactory(c -> c.getValue().estadoProperty());
        colSucursal.setCellValueFactory(c -> c.getValue().sucursalProperty());
        colRuc.setCellValueFactory(c -> c.getValue().rucProperty());
        colPermisos.setCellValueFactory(c -> c.getValue().permisosProperty());

        btnEditar.setDisable(true);
        btnEstado.setDisable(true);
        btnReactivar.setDisable(true);

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, nuevo) -> {
            boolean seleccionado = nuevo != null;
            btnEditar.setDisable(!seleccionado);
            btnEstado.setDisable(!seleccionado);
            btnReactivar.setDisable(!seleccionado || !"Inactivo".equalsIgnoreCase(nuevo.getEstado()));
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
            filteredData.setPredicate(u -> filtro.isEmpty()
                    || u.getNombre().toLowerCase().contains(filtro)
                    || u.getUsuario().toLowerCase().contains(filtro));
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
                data.add(u);
                CSVUtil.guardarUsuarios(data);
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

        Optional<Usuario> resultado = mostrarFormulario("Editar usuario", seleccionado);
        resultado.ifPresent(u -> {
            if (DialogHelper.confirm(w, "¿Actualizar usuario?")) {
                seleccionado.setNombre(u.getNombre());
                seleccionado.setCorreo(u.getCorreo());
                seleccionado.setUsuario(u.getUsuario());
                seleccionado.setRol(u.getRol());
                seleccionado.setSucursal(u.getSucursal());
                seleccionado.setRuc(u.getRuc());
                seleccionado.setPermisos(u.getPermisos());
                CSVUtil.guardarUsuarios(data);
                DialogHelper.showSuccess(w, "Usuario actualizado");
            }
        });
    }

    @FXML
    private void onEstado(ActionEvent ev) {
        Window w = getWindow(ev);
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        String nuevo = "Activo".equals(seleccionado.getEstado()) ? "Inactivo" : "Activo";
        if (DialogHelper.confirm(w, "¿Cambiar estado a " + nuevo + "?")) {
            seleccionado.setEstado(nuevo);
            CSVUtil.guardarUsuarios(data);
            DialogHelper.showSuccess(w, "Estado cambiado a " + nuevo);
        }
    }

    @FXML
    private void onReactivar(ActionEvent ev) {
        Window w = getWindow(ev);
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null || !"Inactivo".equalsIgnoreCase(seleccionado.getEstado())) {
            DialogHelper.showWarning(w, "Selecciona un usuario inactivo para reactivar.");
            return;
        }

        seleccionado.setEstado("Activo");
        CSVUtil.guardarUsuarios(data);
        tablaUsuarios.refresh();
        DialogHelper.showSuccess(w, "Usuario reactivado correctamente.");
    }

    // --- Formulario de usuario ---
    private Optional<Usuario> mostrarFormulario(String titulo, Usuario usuarioExistente) {
        Dialog<Usuario> dialog = new Dialog<>();
        dialog.setTitle(titulo);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNombre = new TextField(usuarioExistente != null ? usuarioExistente.getNombre() : "");
        TextField txtCorreo = new TextField(usuarioExistente != null ? usuarioExistente.getCorreo() : "");
        TextField txtUsuario = new TextField(usuarioExistente != null ? usuarioExistente.getUsuario() : "");
        TextField txtRuc = new TextField(usuarioExistente != null ? usuarioExistente.getRuc() : "");
        PasswordField txtPassword = new PasswordField();
        ComboBox<String> cbRol = new ComboBox<>(
                FXCollections.observableArrayList("Administrador", "Finanzas", "Gerente", "Ventas", "Logistica"));
        ComboBox<String> cbSucursal = new ComboBox<>(
                FXCollections.observableArrayList("Guaytacama", "Latacunga", "Quito"));
        Label lblPermisos = new Label();

        if (usuarioExistente != null) {
            cbRol.setValue(usuarioExistente.getRol());
            cbSucursal.setValue(usuarioExistente.getSucursal());
            lblPermisos.setText(usuarioExistente.getPermisos());
        }

        cbRol.valueProperty().addListener((obs, oldR, newR) -> {
            List<Permiso> permisos = PermisosUtil.getPermisosPorRol(newR);
            String permisosStr = permisos.stream()
                    .map(Permiso::getCodigo)
                    .collect(Collectors.joining(","));
            lblPermisos.setText(permisosStr);
        });

        grid.addRow(0, new Label("Nombre:"), txtNombre);
        grid.addRow(1, new Label("Correo:"), txtCorreo);
        grid.addRow(2, new Label("Usuario:"), txtUsuario);
        grid.addRow(3, new Label("Contraseña:"), txtPassword);
        grid.addRow(4, new Label("Rol:"), cbRol);
        grid.addRow(5, new Label("Sucursal:"), cbSucursal);
        grid.addRow(6, new Label("RUC:"), txtRuc);
        grid.addRow(7, new Label("Permisos:"), lblPermisos);

        dialog.getDialogPane().setContent(grid);

        Node okButton = dialog.getDialogPane().lookupButton(ButtonType.OK);
        BooleanBinding invalid = txtNombre.textProperty().isEmpty()
                .or(txtCorreo.textProperty().isEmpty())
                .or(txtUsuario.textProperty().isEmpty())
                .or(cbRol.valueProperty().isNull())
                .or(cbSucursal.valueProperty().isNull())
                .or(txtRuc.textProperty().isEmpty());
        okButton.disableProperty().bind(invalid);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                Usuario u = usuarioExistente != null ? usuarioExistente : new Usuario();
                u.setNombre(txtNombre.getText().trim());
                u.setCorreo(txtCorreo.getText().trim());
                u.setUsuario(txtUsuario.getText().trim());
                u.setRol(cbRol.getValue());
                u.setSucursal(cbSucursal.getValue());
                u.setRuc(txtRuc.getText().trim());
                List<Permiso> permisos = PermisosUtil.getPermisosPorRol(u.getRol());
                u.setPermisos(permisos.stream()
                        .map(Permiso::getCodigo)
                        .collect(Collectors.joining(",")));
                if (!txtPassword.getText().isEmpty()) {
                    u.setPassword(txtPassword.getText());
                }
                return u;
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Window getWindow(ActionEvent ev) {
        return ((Node) ev.getSource()).getScene().getWindow();
    }
}