package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.util.Optional;

public class RolesPermisosController {

        @FXML
        private Button btnActualizarRoles;
        @FXML
        private Button btnActualizarPermisos;
        @FXML
        private Button btnExportar;

        @FXML
        private TextField txtBuscar;
        @FXML
        private ComboBox<String> cbFiltroRol;
        @FXML
        private ComboBox<String> cbFiltroEstado;

        @FXML
        private TableView<Usuario> tableUsuariosRolesPermisos;
        @FXML
        private TableColumn<Usuario, String> colNombre;
        @FXML
        private TableColumn<Usuario, String> colUsuario;
        @FXML
        private TableColumn<Usuario, String> colRol;
        @FXML
        private TableColumn<Usuario, String> colPermisos;
        @FXML
        private TableColumn<Usuario, String> colEstado;

        private final ObservableList<Usuario> masterData = FXCollections.observableArrayList();
        private FilteredList<Usuario> filteredData;

        @FXML
        public void initialize() {
                // Configurar columnas
                colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
                colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
                colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
                colPermisos.setCellValueFactory(new PropertyValueFactory<>("permisos")); // Debes agregar propiedad
                                                                                         // permisos en
                                                                                         // Usuario
                colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

                // Datos de ejemplo (agregar permisos)
                masterData.addAll(
                                new Usuario("Laura Gomez", "laura@example.com", "lgomez", "Administrador", "Activo",
                                                "Gestionar Compras, Gestionar Pedidos, Cambiar Estados de Lotes,  , Administrar Usuarios, Modificar Parámetros"),

                                new Usuario("Carlos Pérez", "carlos@example.com", "cperez", "Ventas", "Activo",
                                                "Gestionar Pedidos,  "),

                                new Usuario("María Rodríguez", "maria.rod@example.com", "mrod", "Gerente", "Activo",
                                                "Gestionar Compras, Gestionar Pedidos,  , Cambiar Estados de Lotes"),

                                new Usuario("Jorge Martínez", "jorge.m@example.com", "jmart", "Ventas", "Activo",
                                                "Gestionar Pedidos"),

                                new Usuario("Ana Fernández", "ana.f@example.com", "afdez", "Ventas", "Activo",
                                                "Gestionar Pedidos"),

                                new Usuario("Pedro Sánchez", "pedro.s@example.com", "psanchez", "Administrador",
                                                "Activo",
                                                "Administrar Usuarios, Gestionar Compras, Modificar Parámetros"),

                                new Usuario("Lucía Moreno", "lucia.m@example.com", "lmoreno", "Finanzas", "Inactivo",
                                                " Administrar Usuarios, Gestionar Compras"),

                                new Usuario("Diego Torres", "diego.t@example.com", "dtorres", "Finanzas", "Activo",
                                                " , Gestionar Compras"),

                                new Usuario("Sofía Herrera", "sofia.h@example.com", "sherrera", "Gerente", "Inactivo",
                                                "Gestionar Pedidos"),

                                new Usuario("Fernando Castillo", "fcastillo@example.com", "fcastillo", "Administrador",
                                                "Inactivo",
                                                "Administrar Usuarios")

                );

                filteredData = new FilteredList<>(masterData, p -> true);
                tableUsuariosRolesPermisos.setItems(filteredData);

                // Opciones filtro Rol y Estado
                cbFiltroRol.getItems().addAll("Todos", "Administrador", "Finanzas", "Gerente", "Ventas");
                cbFiltroRol.getSelectionModel().selectFirst();

                cbFiltroEstado.getItems().addAll("Todos", "Activo", "Inactivo");
                cbFiltroEstado.getSelectionModel().selectFirst();

                tableUsuariosRolesPermisos.setEditable(true);

                // Filtros de búsqueda
                txtBuscar.textProperty().addListener((obs, old, val) -> filtrarUsuarios());
                cbFiltroRol.getSelectionModel().selectedItemProperty()
                                .addListener((obs, old, val) -> filtrarUsuarios());
                cbFiltroEstado.getSelectionModel().selectedItemProperty()
                                .addListener((obs, old, val) -> filtrarUsuarios());
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
                Usuario sel = tableUsuariosRolesPermisos.getSelectionModel().getSelectedItem();

                if (sel == null) {
                        DialogHelper.showWarning(owner, "Selecciona un usuario primero");
                        return;
                }

                Dialog<String> dlg = new Dialog<>();
                dlg.setTitle("Actualizar Rol");
                dlg.initOwner(owner);
                dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                ComboBox<String> cbRol = new ComboBox<>(FXCollections.observableArrayList(
                                "Administrador", "Finanzas", "Gerente", "Ventas"));
                cbRol.setValue(sel.getRol());
                cbRol.setMaxWidth(Double.MAX_VALUE); // Permite que crezca horizontalmente si el contenedor lo permite

                // Contenedor para controlar tamaño y agregar padding
                VBox content = new VBox(10);
                content.setPrefWidth(250); // Limita el ancho del diálogo
                content.setStyle("-fx-padding: 10;"); // Padding interno para que no quede pegado al borde
                content.getChildren().add(cbRol);

                dlg.getDialogPane().setContent(content);

                dlg.setResultConverter(bt -> bt == ButtonType.OK ? cbRol.getValue() : null);
                Optional<String> result = dlg.showAndWait();

                result.ifPresent(nuevoRol -> {
                        if (!nuevoRol.equals(sel.getRol())
                                        && DialogHelper.confirm(owner, "¿Deseas actualizar el rol?")) {
                                sel.setRol(nuevoRol);
                                tableUsuariosRolesPermisos.refresh();
                                DialogHelper.showSuccess(owner, "actualizado rol");
                        }
                });
        }

        @FXML
        private void onActualizarPermisos(ActionEvent event) {
                Window owner = btnActualizarPermisos.getScene().getWindow();
                Usuario sel = tableUsuariosRolesPermisos.getSelectionModel().getSelectedItem();

                if (sel == null) {
                        DialogHelper.showWarning(owner, "Selecciona un usuario primero");
                        return;
                }

                Dialog<ObservableList<String>> dlg = new Dialog<>();
                dlg.setTitle("Actualizar Permisos");
                dlg.initOwner(owner);
                dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                ObservableList<String> opciones = FXCollections.observableArrayList(
                                "Gestionar Compras", "Gestionar Pedidos", "Cambiar Estados de Lotes",
                                "Administrar Usuarios", "Modificar Parámetros");

                VBox content = new VBox(10);
                ObservableList<CheckBox> checkboxes = FXCollections.observableArrayList();

                String[] permisosActuales = sel.getPermisos().split(",");
                for (String permiso : opciones) {
                        CheckBox cb = new CheckBox(permiso);
                        for (String actual : permisosActuales) {
                                if (permiso.trim().equalsIgnoreCase(actual.trim())) {
                                        cb.setSelected(true);
                                        break;
                                }
                        }
                        checkboxes.add(cb);
                }

                content.getChildren().addAll(checkboxes);
                dlg.getDialogPane().setContent(content);

                dlg.setResultConverter(bt -> {
                        if (bt == ButtonType.OK) {
                                ObservableList<String> seleccionados = FXCollections.observableArrayList();
                                for (CheckBox cb : checkboxes) {
                                        if (cb.isSelected()) {
                                                seleccionados.add(cb.getText());
                                        }
                                }
                                return seleccionados;
                        }
                        return null;
                });

                Optional<ObservableList<String>> res = dlg.showAndWait();

                res.ifPresent(lista -> {
                        String nuevosPermisos = String.join(", ", lista);
                        if (!nuevosPermisos.equals(sel.getPermisos()) &&
                                        DialogHelper.confirm(owner, "¿Deseas actualizar los permisos?")) {
                                sel.setPermisos(nuevosPermisos);
                                tableUsuariosRolesPermisos.refresh();
                                DialogHelper.showSuccess(owner, "actualizado permisos");
                        }
                });
        }

        @FXML
        private void onExportar(ActionEvent event) {
                Window owner = btnExportar.getScene().getWindow();
                DialogHelper.showSuccess(owner, "exportado datos correctamente");
        }

}
