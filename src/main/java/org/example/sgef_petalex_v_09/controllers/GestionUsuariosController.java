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
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.CSVUtil;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.net.URL;
import java.util.*;

public class GestionUsuariosController implements Initializable {

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colEstado;
    @FXML private TableColumn<Usuario, String> colSucursal;
    @FXML private TableColumn<Usuario, String> colRuc;
    @FXML private TableColumn<Usuario, String> colPermisos;

    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEstado;
    @FXML private TextField txtBuscar;

    private final ObservableList<Usuario> data = FXCollections.observableArrayList();
    private FilteredList<Usuario> filteredData;
    private final Random rnd = new Random();

    @FXML
    private void onReactivar(ActionEvent event) {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogHelper.showWarning(getWindow(), "Selecciona un usuario inactivo para reactivar.");
            return;
        }
        // Solo reactivamos si está inactivo
        if (!"Inactivo".equalsIgnoreCase(sel.getEstado())) {
            DialogHelper.showWarning(getWindow(), "El usuario ya está activo.");
            return;
        }
        sel.setEstado("Activo");
        CSVUtil.guardarUsuarios(data);
        tablaUsuarios.refresh();
        DialogHelper.showSuccess(getWindow(), "Usuario reactivado correctamente.");
    }

    private Window getWindow() {
        return tablaUsuarios.getScene().getWindow();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1) Configurar columnas
        colId      .setCellValueFactory(c -> c.getValue().idProperty());
        colNombre  .setCellValueFactory(c -> c.getValue().nombreProperty());
        colCorreo  .setCellValueFactory(c -> c.getValue().correoProperty());
        colUsuario .setCellValueFactory(c -> c.getValue().usuarioProperty());
        colRol     .setCellValueFactory(c -> c.getValue().rolProperty());
        colEstado  .setCellValueFactory(c -> c.getValue().estadoProperty());
        colSucursal.setCellValueFactory(c -> c.getValue().sucursalProperty());
        colRuc     .setCellValueFactory(c -> c.getValue().rucProperty());
        colPermisos.setCellValueFactory(c -> c.getValue().permisosProperty());

        // 2) Control de selección
        btnEditar.setDisable(true);
        btnEstado.setDisable(true);
        tablaUsuarios.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldV, newV) -> {
                    boolean sel = newV != null;
                    btnEditar.setDisable(!sel);
                    btnEstado.setDisable(!sel);
                });

        // 3) Cargar datos del CSV
        List<Usuario> list = CSVUtil.leerUsuarios();
        data.setAll(list);

        // 4) Filtro dinámico
        filteredData = new FilteredList<>(data, u -> true);
        tablaUsuarios.setItems(filteredData);
        txtBuscar.textProperty().addListener((obs, o, n) -> {
            String f = n.toLowerCase().trim();
            filteredData.setPredicate(u ->
                    f.isEmpty()
                            || u.getNombre().toLowerCase().contains(f)
                            || u.getUsuario().toLowerCase().contains(f)
            );
        });
    }

    @FXML
    private void onNuevo(ActionEvent ev) {
        Window w = ((Node)ev.getSource()).getScene().getWindow();
        showUserForm("Crear usuario", null).ifPresent(u -> {
            if (!DialogHelper.confirm(w, "¿Crear usuario?")) return;
            u.setId("U" + String.format("%03d", rnd.nextInt(1000)));
            u.setEstado("Activo");
            data.add(u);
            CSVUtil.guardarUsuarios(data);
            DialogHelper.showSuccess(w, "Usuario creado");
        });
    }

    @FXML
    private void onEditar(ActionEvent ev) {
        Window w = ((Node)ev.getSource()).getScene().getWindow();
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogHelper.showWarning(w, "Seleccione un usuario");
            return;
        }
        showUserForm("Editar usuario", sel).ifPresent(u -> {
            if (!DialogHelper.confirm(w, "¿Actualizar usuario?")) return;
            sel.setNombre(u.getNombre());
            sel.setCorreo(u.getCorreo());
            sel.setUsuario(u.getUsuario());
            sel.setRol(u.getRol());
            sel.setSucursal(u.getSucursal());
            sel.setRuc(u.getRuc());
            sel.setPermisos(u.getPermisos());
            CSVUtil.guardarUsuarios(data);
            DialogHelper.showSuccess(w, "Usuario actualizado");
        });
    }

    @FXML
    private void onEstado(ActionEvent ev) {
        Window w = ((Node)ev.getSource()).getScene().getWindow();
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogHelper.showWarning(w, "Seleccione un usuario");
            return;
        }
        String nuevo = sel.getEstado().equals("Activo") ? "Inactivo" : "Activo";
        if (!DialogHelper.confirm(w, "¿Cambiar estado a " + nuevo + "?")) return;
        sel.setEstado(nuevo);
        CSVUtil.guardarUsuarios(data);
        DialogHelper.showSuccess(w, "Estado cambiado a " + nuevo);
    }

    /**
     * Muestra un diálogo para crear/editar usuario.
     */
    private Optional<Usuario> showUserForm(String title, Usuario existing) {
        Dialog<Usuario> dlg = new Dialog<>();
        dlg.setTitle(title);
        ButtonType cancelType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType okType     = new ButtonType("Aceptar",  ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().setAll(cancelType, okType);

        // 1) Construir formulario con ComboBox para rol
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNombre   = new TextField();
        TextField txtCorreo   = new TextField();
        TextField txtUsuario  = new TextField();
        ComboBox<String> cbRol = new ComboBox<>();
        Label lblPermisos     = new Label(); // Sólo para mostrar

        txtNombre .setPromptText("Nombre completo");
        txtCorreo .setPromptText("correo@dominio.com");
        txtUsuario.setPromptText("login");

        // Roles fijos
        cbRol.setItems(FXCollections.observableArrayList(
                "Administrador", "Contador", "Gerente", "Logistica"
        ));
        cbRol.setPromptText("Selecciona rol");

        // Mapa de permisos según rol
        Map<String,String> permisosMap = Map.of(
                "Administrador", "ALL",
                "Contador",      "FINANZAS,REPORTES",
                "Gerente",       "VENTAS,COMPRAS,REPORTES",
                "Logistica",     "INVENTARIO,ENTREGAS"
        );

        // Si estamos editando, precargar
        if (existing != null) {
            txtNombre.setText(existing.getNombre());
            txtCorreo.setText(existing.getCorreo());
            txtUsuario.setText(existing.getUsuario());
            cbRol.setValue(existing.getRol());
            lblPermisos.setText(existing.getPermisos());
        }

        // Al cambiar el rol, actualizamos permisos automáticos
        cbRol.valueProperty().addListener((obs, oldR, newR) -> {
            lblPermisos.setText(permisosMap.getOrDefault(newR, ""));
        });

        // Layout
        grid.addRow(0, new Label("Nombre:"),   txtNombre);
        grid.addRow(1, new Label("Correo:"),   txtCorreo);
        grid.addRow(2, new Label("Usuario:"),  txtUsuario);
        grid.addRow(3, new Label("Rol:"),      cbRol);
        grid.addRow(4, new Label("Permisos:"), lblPermisos);

        dlg.getDialogPane().setContent(grid);

        // 2) Habilitar OK solo si todo está lleno
        Node okButton = dlg.getDialogPane().lookupButton(okType);
        BooleanBinding invalid = txtNombre.textProperty().isEmpty()
                .or(txtCorreo.textProperty().isEmpty())
                .or(txtUsuario.textProperty().isEmpty())
                .or(cbRol.valueProperty().isNull());
        okButton.disableProperty().bind(invalid);

        // 3) Resultado usa permisosMap
        dlg.setResultConverter(btn -> {
            if (btn == okType) {
                Usuario u = existing == null ? new Usuario() : existing;
                u.setNombre(txtNombre.getText().trim());
                u.setCorreo(txtCorreo.getText().trim());
                u.setUsuario(txtUsuario.getText().trim());
                String rol = cbRol.getValue();
                u.setRol(rol);
                u.setPermisos(permisosMap.getOrDefault(rol, ""));
                return u;
            }
            return null;
        });

        return dlg.showAndWait();
    }
}
