package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.util.Optional;

public class RolesPermisosController {

    @FXML private Button btnActualizarRoles;
    @FXML private Button btnActualizarPermisos;
    @FXML private Button btnExportar;

    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbFiltroRol;
    @FXML private ComboBox<String> cbFiltroEstado;

    @FXML private TableView<Usuario> tableUsuariosRolesPermisos;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colPermisos;
    @FXML private TableColumn<Usuario, String> colEstado;

    private final ObservableList<Usuario> masterData = FXCollections.observableArrayList();
    private FilteredList<Usuario> filteredData;

    @FXML
    public void initialize() {
        // Configurar columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colPermisos.setCellValueFactory(new PropertyValueFactory<>("permisos")); // Debes agregar propiedad permisos en Usuario
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Datos de ejemplo (agregar permisos)
        masterData.addAll(
            new Usuario("Laura Gomez", "laura@example.com", "lgomez", "Administrador", "Activo", "Lectura, Escritura"),
            new Usuario("Carlos Pérez", "carlos@example.com", "cperez", "Ventas", "Activo", "Lectura"),
            new Usuario("María Rodríguez", "maria.rod@example.com", "mrod", "Gerente", "Activo", "Lectura, Escritura, Ejecución"),
            new Usuario("Jorge Martínez", "jorge.m@example.com", "jmart", "Ventas", "Activo", "Lectura"),
            new Usuario("Ana Fernández", "ana.f@example.com", "afdez", "Ventas", "Activo", "Lectura"),
            new Usuario("Pedro Sánchez", "pedro.s@example.com", "psanchez", "Administrador", "Activo", "Lectura, Escritura"),
            new Usuario("Lucía Moreno", "lucia.m@example.com", "lmoreno", "Finanzas", "Inactivo", "Lectura"),
            new Usuario("Diego Torres", "diego.t@example.com", "dtorres", "Finanzas", "Activo", "Lectura, Escritura"),
            new Usuario("Sofía Herrera", "sofia.h@example.com", "sherrera", "Gerente", "Inactivo", "Lectura, Escritura"),
            new Usuario("Fernando Castillo", "fernando.c@example.com", "fcastillo", "Administrador", "Inactivo", "Lectura")
        );

        filteredData = new FilteredList<>(masterData, p -> true);
        tableUsuariosRolesPermisos.setItems(filteredData);

        // Opciones filtro Rol y Estado
        cbFiltroRol.getItems().addAll("Todos", "Administrador", "Finanzas", "Gerente", "Ventas");
        cbFiltroRol.getSelectionModel().selectFirst();

        cbFiltroEstado.getItems().addAll("Todos", "Activo", "Inactivo");
        cbFiltroEstado.getSelectionModel().selectFirst();

        // Editable columnas Rol y Permisos
        ObservableList<String> roles = FXCollections.observableArrayList("Administrador", "Finanzas", "Gerente", "Ventas");
        colRol.setCellFactory(ComboBoxTableCell.forTableColumn(roles));
        colRol.setOnEditCommit(evt -> {
            Usuario u = evt.getRowValue();
            u.setRol(evt.getNewValue());
            DialogHelper.showSuccess(tableUsuariosRolesPermisos.getScene().getWindow(), "Rol actualizado");
        });

        // Para permisos puedes hacer un ComboBox editable con opciones ejemplo
        ObservableList<String> permisosOpciones = FXCollections.observableArrayList(
            "Lectura", "Escritura", "Ejecución", "Lectura, Escritura", "Lectura, Escritura, Ejecución"
        );
        colPermisos.setCellFactory(ComboBoxTableCell.forTableColumn(permisosOpciones));
        colPermisos.setOnEditCommit(evt -> {
            Usuario u = evt.getRowValue();
            u.setPermisos(evt.getNewValue());
            DialogHelper.showSuccess(tableUsuariosRolesPermisos.getScene().getWindow(), "Permisos actualizados");
        });

        tableUsuariosRolesPermisos.setEditable(true);

        // Filtros de búsqueda
        txtBuscar.textProperty().addListener((obs, old, val) -> filtrarUsuarios());
        cbFiltroRol.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> filtrarUsuarios());
        cbFiltroEstado.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> filtrarUsuarios());
    }

    /** Filtra la tabla según txtBuscar, cbFiltroRol y cbFiltroEstado */
    private void filtrarUsuarios() {
        String texto = txtBuscar.getText().toLowerCase().trim();
        String rol = cbFiltroRol.getValue();
        String estado = cbFiltroEstado.getValue();

        filteredData.setPredicate(u -> {
            boolean matchesTexto = texto.isEmpty()
                || u.getNombre().toLowerCase().contains(texto)
                || u.getUsuario().toLowerCase().contains(texto);
            boolean matchesRol = rol.equals("Todos") || u.getRol().equals(rol);
            boolean matchesEstado = estado.equals("Todos") || u.getEstado().equals(estado);
            return matchesTexto && matchesRol && matchesEstado;
        });
    }

    @FXML
    private void onActualizarRoles(ActionEvent event) {
        Window owner = btnActualizarRoles.getScene().getWindow();
        if (confirm(owner, "¿Está seguro que desea guardar los cambios en los roles?")) {
            // Aquí iría la lógica para guardar en base de datos
            DialogHelper.showSuccess(owner, "Roles actualizados correctamente");
        }
    }

    @FXML
    private void onActualizarPermisos(ActionEvent event) {
        Window owner = btnActualizarPermisos.getScene().getWindow();
        if (confirm(owner, "¿Está seguro que desea guardar los cambios en los permisos?")) {
            // Aquí iría la lógica para guardar en base de datos
            DialogHelper.showSuccess(owner, "Permisos actualizados correctamente");
        }
    }

    @FXML
    private void onExportar(ActionEvent event) {
        Window owner = btnExportar.getScene().getWindow();
        DialogHelper.showSuccess(owner, "Datos exportados correctamente");
    }

    /** Diálogo de confirmación */
    private boolean confirm(Window owner, String msg) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.initOwner(owner);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.setTitle("Confirmar");
        Optional<ButtonType> res = alert.showAndWait();
        return res.isPresent() && res.get().getButtonData() == ButtonData.OK_DONE;
    }
}
