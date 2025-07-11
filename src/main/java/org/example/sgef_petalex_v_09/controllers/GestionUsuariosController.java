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
import javafx.scene.layout.GridPane;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.util.Optional;

public class GestionUsuariosController {
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnReactivar;
    @FXML
    private Button btnExportar;
    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private TableView<Usuario> tableUsuarios;
    @FXML
    private TableColumn<Usuario, String> colNombre;
    @FXML
    private TableColumn<Usuario, String> colCorreo;
    @FXML
    private TableColumn<Usuario, String> colUsuario;
    @FXML
    private TableColumn<Usuario, String> colRol;
    @FXML
    private TableColumn<Usuario, String> colEstado;

    private final ObservableList<Usuario> masterData = FXCollections.observableArrayList();
    private FilteredList<Usuario> filteredData;

    @FXML
    public void initialize() {
        // Configurar columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Datos de ejemplo
        masterData.addAll(
                new Usuario("Laura Gomez", "laura@example.com", "lgomez", "Administrador", "Activo"),
                new Usuario("Carlos Pérez", "carlos@example.com", "cperez", "Ventas", "Activo"),
                new Usuario("María Rodríguez", "maria.rod@example.com", "mrod", "Gerente", "Activo"),
                new Usuario("Jorge Martínez", "jorge.m@example.com", "jmart", "Ventas", "Activo"),
                new Usuario("Ana Fernández", "ana.f@example.com", "afdez", "Ventas", "Activo"),
                new Usuario("Pedro Sánchez", "pedro.s@example.com", "psanchez", "Administrador", "Activo"),
                new Usuario("Lucía Moreno", "lucia.m@example.com", "lmoreno", "Finanzas", "Inactivo"),
                new Usuario("Diego Torres", "diego.t@example.com", "dtorres", "Finanzas", "Activo"),
                new Usuario("Sofía Herrera", "sofia.h@example.com", "sherrera", "Gerente", "Inactivo"),
                new Usuario("Fernando Castillo", "fernando.c@example.com", "fcastillo", "Administrador", "Inactivo"));

        // Wrap masterData in FilteredList
        filteredData = new FilteredList<>(masterData, p -> true);
        tableUsuarios.setItems(filteredData);

        // Opciones Estado
        cbEstado.getItems().addAll("Todos", "Activo", "Inactivo");
        cbEstado.getSelectionModel().selectFirst(); // "Todos"

        // Configurar columna Rol como ComboBox editable
        ObservableList<String> roles = FXCollections.observableArrayList(
                "Administrador", "Finanzas", "Gerente", "Ventas");
        tableUsuarios.setEditable(true);
        colRol.setCellFactory(ComboBoxTableCell.forTableColumn(roles));
        colRol.setOnEditCommit(evt -> {
            Usuario u = evt.getRowValue();
            u.setRol(evt.getNewValue());
            DialogHelper.showSuccess(
                    tableUsuarios.getScene().getWindow(),
                    "rol asignado");
        });

        // Listener para habilitar botones
        tableUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean has = sel != null;
            btnEditar.setDisable(!has);
            btnReactivar.setDisable(!has);
        });

        // Listener de búsqueda
        txtBuscar.textProperty().addListener((obs, old, val) -> filtrarUsuarios());
        cbEstado.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> filtrarUsuarios());
    }

    /** Filtra la tabla según txtBuscar y cbEstado. */
    private void filtrarUsuarios() {
        String texto = txtBuscar.getText().toLowerCase().trim();
        String estado = cbEstado.getValue();
        filteredData.setPredicate(u -> {
            boolean matchesTexto = texto.isEmpty()
                    || u.getNombre().toLowerCase().contains(texto)
                    || u.getUsuario().toLowerCase().contains(texto)
                    || u.getCorreo().toLowerCase().contains(texto);
            boolean matchesEstado = estado.equals("Todos") || u.getEstado().equals(estado);
            return matchesTexto && matchesEstado;
        });
    }

    @FXML
    private void onNuevo(ActionEvent event) {
        Window owner = btnNuevo.getScene().getWindow();
        Optional<Usuario> res = showUserFormDialog("Crear usuario", null);
        res.ifPresent(u -> {
            if (confirm(owner, "¿Está seguro que desea Crear un Usuario?")) {
                masterData.add(u);
                DialogHelper.showSuccess(owner, "creado");
            }
        });
    }

    @FXML
    private void onEditar(ActionEvent event) {
        Window owner = btnEditar.getScene().getWindow();
        Usuario sel = tableUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogHelper.showWarning(owner, "Selecciona un usuario primero");
            return;
        }
        Optional<Usuario> res = showUserFormDialog("Actualizar usuario", sel);
        res.ifPresent(u -> {
            if (confirm(owner, "¿Está seguro que desea Actualizar?")) {
                sel.setNombre(u.getNombre());
                sel.setCorreo(u.getCorreo());
                sel.setUsuario(u.getUsuario());
                sel.setRol(u.getRol());
                sel.setEstado(u.getEstado());
                tableUsuarios.refresh();
                DialogHelper.showSuccess(owner, "actualizado");
            }
        });
    }
    @FXML
    private void onReactivar(ActionEvent event) {
        Window owner = btnReactivar.getScene().getWindow();
        Usuario sel = tableUsuarios.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogHelper.showWarning(owner, "Selecciona un usuario primero");
            return;
        }
        if (confirm(owner, "¿Está seguro que desea cambiar el estado del usuario?")) {
            // Alterna entre Activo e Inactivo
            sel.setEstado(sel.getEstado().equals("Activo") ? "Inactivo" : "Activo");
            tableUsuarios.refresh();
            DialogHelper.showSuccess(owner, "estado actualizado");
        }
    }

    @FXML
    private void onExportar(ActionEvent event) {
        Window owner = btnExportar.getScene().getWindow();
        DialogHelper.showSuccess(owner, "exportado los datos");
    }

    /** Muestra un diálogo de confirmación con mensaje. */
    private boolean confirm(Window owner, String msg) {
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.initOwner(owner);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.setTitle("Confirmar");
        Optional<ButtonType> res = a.showAndWait();
        return res.isPresent() && res.get().getButtonData() == ButtonData.OK_DONE;
    }

    /** Diálogo genérico para crear/editar un Usuario */
 private Optional<Usuario> showUserFormDialog(String title, Usuario existing) {
    Dialog<Usuario> dlg = new Dialog<>();
    dlg.setTitle(title);
    dlg.getDialogPane().getButtonTypes()
            .addAll(new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE),
                    new ButtonType("Aceptar", ButtonData.OK_DONE));

    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(10);

    TextField txtNombre = new TextField();
    txtNombre.setPromptText("Nombre");
    TextField txtCorreo = new TextField();
    txtCorreo.setPromptText("Correo");
    TextField txtUsuario = new TextField();
    txtUsuario.setPromptText("Usuario");

    ComboBox<String> cbRol = new ComboBox<>(
            FXCollections.observableArrayList("Administrador", "Finanzas", "Gerente", "Ventas"));
    cbRol.setPromptText("Rol");

    if (existing != null) {
        txtNombre.setText(existing.getNombre());
        txtCorreo.setText(existing.getCorreo());
        txtUsuario.setText(existing.getUsuario());
        // NO cargar ni mostrar rol
    } else {
        cbRol.setValue("Ventas"); // por defecto
    }

    // Campos comunes
    grid.add(new Label("Nombre:"), 0, 0);
    grid.add(txtNombre, 1, 0);
    grid.add(new Label("Correo:"), 0, 1);
    grid.add(txtCorreo, 1, 1);
    grid.add(new Label("Usuario:"), 0, 2);
    grid.add(txtUsuario, 1, 2);

    if (existing == null) {
        // Solo mostrar rol al crear
        grid.add(new Label("Rol:"), 0, 3);
        grid.add(cbRol, 1, 3);
    }

    dlg.getDialogPane().setContent(grid);

    dlg.setResultConverter(bt -> {
        if (bt.getButtonData() == ButtonData.OK_DONE) {
            Usuario u = new Usuario();
            if (existing != null)
                u.setId(existing.getId());
            u.setNombre(txtNombre.getText());
            u.setCorreo(txtCorreo.getText());
            u.setUsuario(txtUsuario.getText());

            // Solo asignar rol al crear
            if (existing == null) {
                u.setRol(cbRol.getValue());
                u.setEstado("Activo");
            } else {
                u.setRol(existing.getRol());
                u.setEstado(existing.getEstado());
            }

            return u;
        }
        return null;
    });

    return dlg.showAndWait();
}

}
