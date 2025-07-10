package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import org.example.sgef_petalex_v_09.models.Usuario;

public class GestionUsuariosController {
    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnReactivar;
    @FXML private Button btnResetPass;
    @FXML private Button btnBloquear;
    @FXML private Button btnAsignarRol;
    @FXML private Button btnExportar;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colEstado;

    private ObservableList<Usuario> usuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Datos de ejemplo (reemplazar con datos reales)
        usuarios.addAll(
                new Usuario("Laura Gomez", "laura@example.com", "lgomez", "Administrador", "Activo"),
                new Usuario("Carlos Pérez", "carlos@example.com", "cperez", "Vendedor", "Activo")
        );
        tableUsuarios.setItems(usuarios);

        // Inicializar filtros
        cbEstado.getItems().addAll("Activo", "Inactivo");
    }

    @FXML private void onNuevo() {
        // TODO: abrir diálogo de nuevo usuario
    }
    @FXML private void onEditar() {
        // TODO: editar usuario seleccionado
    }
    @FXML private void onEliminar() {
        // TODO: desactivar usuario
    }
    @FXML private void onReactivar() {
        // TODO: reactivar usuario
    }
    @FXML private void onResetPassword() {
        // TODO: restablecer contraseña
    }
    @FXML private void onBloquear() {
        // TODO: bloquear/desbloquear usuario
    }
    @FXML private void onAsignarRol() {
        // TODO: asignar rol a usuario
    }
    @FXML private void onExportar() {
        // TODO: exportar lista a CSV/PDF
    }
}