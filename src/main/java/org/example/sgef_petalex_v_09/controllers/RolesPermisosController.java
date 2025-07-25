package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import org.example.sgef_petalex_v_09.models.Permiso;
import org.example.sgef_petalex_v_09.models.Usuario;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.util.PermisosUtil;
import org.example.sgef_petalex_v_09.util.UserUtil;

import java.util.*;

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
                configurarColumnas();
                cargarDatos();
                //configurarFiltros();
                //configurarListeners();
        }

        private void configurarColumnas() {
                colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
                colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
                colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
                colPermisos.setCellValueFactory(new PropertyValueFactory<>("permisos"));
                colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        }

        private void cargarDatos() {
                masterData.clear();
                masterData.addAll(UserUtil.leerUsuarios());
                filteredData = new FilteredList<>(masterData, p -> true);
                tableUsuariosRolesPermisos.setItems(filteredData);
        }

        @FXML
        private void onActualizarRoles(ActionEvent event) {
                Usuario sel = tableUsuariosRolesPermisos.getSelectionModel().getSelectedItem();
                if (sel == null) {
                        DialogHelper.showWarning(btnActualizarRoles.getScene().getWindow(),
                                "Seleccione un usuario");
                        return;
                }

                Dialog<String> dlg = new Dialog<>();
                dlg.setTitle("Actualizar Rol");

                ComboBox<String> cbRol = new ComboBox<>(FXCollections.observableArrayList(
                        "Administrador", "Finanzas", "Gerente", "Ventas"
                ));
                cbRol.setValue(sel.getRol());

                dlg.getDialogPane().setContent(cbRol);
                dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                dlg.setResultConverter(bt -> {
                        if (bt == ButtonType.OK) {
                                String nuevoRol = cbRol.getValue();
                                // Actualizar permisos según el rol
                                sel.setPermisos(String.join(",",
                                        PermisosUtil.getPermisosPorRol(nuevoRol).stream()
                                                .map(Permiso::getCodigo)
                                                .toList()
                                ));
                                sel.setRol(nuevoRol);
                                UserUtil.guardarUsuarios(masterData);
                                return nuevoRol;
                        }
                        return null;
                });

                dlg.showAndWait();
                tableUsuariosRolesPermisos.refresh();
        }

        @FXML
        private void onActualizarPermisos(ActionEvent event) {
                Usuario sel = tableUsuariosRolesPermisos.getSelectionModel().getSelectedItem();
                if (sel == null) {
                        DialogHelper.showWarning(btnActualizarPermisos.getScene().getWindow(),
                                "Seleccione un usuario");
                        return;
                }

                Dialog<List<String>> dlg = new Dialog<>();
                dlg.setTitle("Actualizar Permisos");

                VBox content = new VBox(10);
                List<CheckBox> checks = new ArrayList<>();
                Set<String> permisosActuales = new HashSet<>(
                        Arrays.asList(sel.getPermisos().split(","))
                );

                for (Permiso p : PermisosUtil.getTodosLosPermisos()) {
                        CheckBox cb = new CheckBox(p.getNombre());
                        cb.setSelected(permisosActuales.contains(p.getCodigo()));
                        cb.setUserData(p);
                        checks.add(cb);
                }

                content.getChildren().addAll(checks);
                dlg.getDialogPane().setContent(new ScrollPane(content));
                dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                dlg.setResultConverter(bt -> {
                        if (bt == ButtonType.OK) {
                                return checks.stream()
                                        .filter(CheckBox::isSelected)
                                        .map(cb -> ((Permiso)cb.getUserData()).getCodigo())
                                        .toList();
                        }
                        return null;
                });

                dlg.showAndWait().ifPresent(permisos -> {
                        sel.setPermisos(String.join(",", permisos));
                        UserUtil.guardarUsuarios(masterData);
                        tableUsuariosRolesPermisos.refresh();
                });
        }
        @FXML
        private void onExportar(ActionEvent event) {
                // Aquí podrías abrir un FileChooser y volcar la tabla a CSV/Excel.
                // Ejemplo mínimo: un simple mensaje de “no implementado aún”.
                DialogHelper.showInformation(
                        btnExportar.getScene().getWindow(),
                        "Exportar",
                        "La funcionalidad de exportar se implementará próximamente."
                );
        }
}

