package org.example.sgef_petalex_v_09.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.example.sgef_petalex_v_09.models.Proveedor;
import org.example.sgef_petalex_v_09.util.DialogHelper;

import java.io.IOException;
import java.util.Optional;

public class ProveedoresController {

    @FXML private Button btnNuevo;
    @FXML private Button btnEditar;
    @FXML private Button btnReactivar;
    @FXML private Button btnExportar;
    @FXML private TextField txtBuscar;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<Proveedor> tableProveedores;
    @FXML private TableColumn<Proveedor, String> colRuc;
    @FXML private TableColumn<Proveedor, String> colNombre;
    @FXML private TableColumn<Proveedor, String> colTelefono;
    @FXML private TableColumn<Proveedor, String> colDireccion;
    @FXML private TableColumn<Proveedor, String> colEstado;
    @FXML private Button btnBack;

    private final ObservableList<Proveedor> masterData = FXCollections.observableArrayList();
    private FilteredList<Proveedor> filteredData;

    @FXML
    public void initialize() {
        // Configurar columnas
        colRuc      .setCellValueFactory(new PropertyValueFactory<>("ruc"));
        colNombre   .setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTelefono .setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colEstado   .setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Datos de ejemplo: 15 florícolas
        masterData.addAll(
                new Proveedor("1791512345001", "Rosas del Azuay",       "+593998112233", "Cuenca, Azuay",       "Activo"),
                new Proveedor("1791512345002", "Florícolas Galápagos",  "+593998223344", "Puerto Ayora, Galápagos", "Activo"),
                new Proveedor("1791512345003", "RosaAndina",            "+593998334455", "Riobamba, Chimborazo","Inactivo"),
                new Proveedor("1791512345004", "Valle de Rosas",        "+593998445566", "Ambato, Tungurahua",  "Activo"),
                new Proveedor("1791512345005", "Petalos del Oriente",   "+593998556677", "Tena, Napo",          "Activo"),
                new Proveedor("1791512345006", "EcoRoses",              "+593998667788", "Quito, Pichincha",    "Inactivo"),
                new Proveedor("1791512345007", "Rosaflor",              "+593998778899", "Guayaquil, Guayas",   "Activo"),
                new Proveedor("1791512345008", "Rosa Imperial",         "+593998889900", "Machala, El Oro",     "Activo"),
                new Proveedor("1791512345009", "FlorAndina",            "+593998990011", "Loja, Loja",          "Inactivo"),
                new Proveedor("1791512345010", "Rosas del Valle",       "+593991101112", "Santo Domingo, SD",   "Activo"),
                new Proveedor("1791512345011", "RosaNorte",             "+593992212223", "Ibarra, Imbabura",    "Activo"),
                new Proveedor("1791512345012", "Petal Express",         "+593993323334", "Latacunga, Cotopaxi", "Activo"),
                new Proveedor("1791512345013", "Andes Flowers",         "+593994434445", "Otavalo, Imbabura",   "Activo"),
                new Proveedor("1791512345014", "Rosa del Pacífico",     "+593995545556", "Manta, Manabí",       "Activo"),
                new Proveedor("1791512345015", "Flores del Chillogallo","+593996656667", "Quito, Pichincha",    "Activo")
        );

        // Wrap en FilteredList
        filteredData = new FilteredList<>(masterData, p -> true);
        tableProveedores.setItems(filteredData);

        // Opciones Estado
        cbEstado.getItems().addAll("Todos", "Activo", "Inactivo");
        cbEstado.getSelectionModel().selectFirst();

        // Listeners para habilitar botones
        tableProveedores.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            boolean has = sel != null;
            btnEditar   .setDisable(!has);
            btnReactivar.setDisable(!has);
        });

        // Búsqueda y filtro
        txtBuscar.textProperty().addListener((obs, o, v) -> filtrar());
        cbEstado.getSelectionModel().selectedItemProperty().addListener((obs, o, v) -> filtrar());
    }

    private void filtrar() {
        String txt = txtBuscar.getText().toLowerCase().trim();
        String est = cbEstado.getValue();
        filteredData.setPredicate(p -> {
            boolean matchTxt = txt.isEmpty()
                    || p.getRuc().contains(txt)
                    || p.getNombre().toLowerCase().contains(txt)
                    || p.getDireccion().toLowerCase().contains(txt);
            boolean matchEst = est.equals("Todos") || p.getEstado().equals(est);
            return matchTxt && matchEst;
        });
    }

    @FXML
    private void onNuevo(ActionEvent evt) {
        Window w = btnNuevo.getScene().getWindow();
        Optional<Proveedor> res = showForm("Crear proveedor", null);
        res.ifPresent(p -> {
            if (confirm(w, "¿Está seguro que desea Crear?")) {
                masterData.add(p);
                DialogHelper.showSuccess(w, "creado");
            }
        });
    }

    @FXML
    private void onEditar(ActionEvent evt) {
        Window w = btnEditar.getScene().getWindow();
        Proveedor sel = tableProveedores.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogHelper.showWarning(w, "Selecciona un proveedor primero");
            return;
        }
        Optional<Proveedor> res = showForm("Actualizar proveedor", sel);
        res.ifPresent(p -> {
            if (confirm(w, "¿Está seguro que desea Actualizar?")) {
                sel.setRuc(p.getRuc());
                sel.setNombre(p.getNombre());
                sel.setTelefono(p.getTelefono());
                sel.setDireccion(p.getDireccion());
                sel.setEstado(p.getEstado());
                tableProveedores.refresh();
                DialogHelper.showSuccess(w, "actualizado");
            }
        });
    }

    @FXML
    private void onReactivar(ActionEvent evt) {
        Window w = btnReactivar.getScene().getWindow();
        Proveedor sel = tableProveedores.getSelectionModel().getSelectedItem();
        if (sel == null) {
            DialogHelper.showWarning(w, "Selecciona un proveedor primero");
            return;
        }
        if ("Activo".equals(sel.getEstado())) {
            DialogHelper.showWarning(w, "El proveedor ya se encuentra activo");
            return;
        }
        if (confirm(w, "¿Está seguro que desea Reactivar?")) {
            sel.setEstado("Activo");
            tableProveedores.refresh();

            DialogHelper.showSuccess(w, "actualizado");
        }
    }

    @FXML
    private void onExportar(ActionEvent evt) {
        Window w = btnExportar.getScene().getWindow();
        DialogHelper.showSuccess(w, "exportado los datos");
    }

    private boolean confirm(Window owner, String msg) {
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.initOwner(owner);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.setTitle("Confirmar");
        Optional<ButtonType> r = a.showAndWait();
        return r.isPresent() && r.get().getButtonData() == ButtonData.OK_DONE;
    }

    private Optional<Proveedor> showForm(String title, Proveedor existing) {
        Dialog<Proveedor> dlg = new Dialog<>();
        dlg.setTitle(title);
        dlg.getDialogPane().getButtonTypes()
                .addAll(new ButtonType("Cancelar", ButtonData.CANCEL_CLOSE),
                        new ButtonType("Aceptar",   ButtonData.OK_DONE));

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(10);

        TextField fRuc       = new TextField(); fRuc.setPromptText("RUC");
        TextField fNombre    = new TextField(); fNombre.setPromptText("Nombre");
        TextField fTelefono  = new TextField(); fTelefono.setPromptText("Teléfono");
        TextField fDireccion = new TextField(); fDireccion.setPromptText("Dirección");
        ComboBox<String> fEst = new ComboBox<>(FXCollections.observableArrayList("Activo","Inactivo"));
        fEst.setPromptText("Estado");

        if (existing != null) {
            fRuc.setText(existing.getRuc());
            fNombre.setText(existing.getNombre());
            fTelefono.setText(existing.getTelefono());
            fDireccion.setText(existing.getDireccion());
            fEst.setValue(existing.getEstado());
        }

        g.add(new Label("RUC:"),       0, 0); g.add(fRuc,       1, 0);
        g.add(new Label("Nombre:"),    0, 1); g.add(fNombre,    1, 1);
        g.add(new Label("Teléfono:"),  0, 2); g.add(fTelefono,  1, 2);
        g.add(new Label("Dirección:"), 0, 3); g.add(fDireccion, 1, 3);
        g.add(new Label("Estado:"),    0, 4); g.add(fEst,       1, 4);

        dlg.getDialogPane().setContent(g);
        dlg.setResultConverter(bt -> {
            if (bt.getButtonData() == ButtonData.OK_DONE) {
                Proveedor p = new Proveedor();
                if (existing != null) p.setRuc(existing.getRuc());
                p.setRuc(fRuc.getText());
                p.setNombre(fNombre.getText());
                p.setTelefono(fTelefono.getText());
                p.setDireccion(fDireccion.getText());
                p.setEstado(fEst.getValue());
                return p;
            }
            return null;
        });
        return dlg.showAndWait();
    }
    @FXML
    private void onBack(ActionEvent event) {
        try {
            Parent mainRoot = FXMLLoader.load(
                    getClass().getResource("/fxml/MainMenu.fxml")
            );
            Stage stage = (Stage) btnBack.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(mainRoot);
            scene.getStylesheets().clear();
            scene.getStylesheets().add(
                    getClass().getResource("/css/styles.css").toExternalForm()
            );
            // stage.setMaximized(true); // Eliminado para evitar maximizar siempre la ventana
            stage.setTitle("Index Blooms – Menú Principal");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
