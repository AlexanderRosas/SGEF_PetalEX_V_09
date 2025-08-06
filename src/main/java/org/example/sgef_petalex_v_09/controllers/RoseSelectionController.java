package org.example.sgef_petalex_v_09.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.Control;
import javafx.scene.control.Tooltip;
import org.example.sgef_petalex_v_09.models.Estados;
import org.example.sgef_petalex_v_09.models.ItemVenta;
import org.example.sgef_petalex_v_09.models.Pedido;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import org.example.sgef_petalex_v_09.validators.DataValidator;
import org.example.sgef_petalex_v_09.validators.ValidationResult;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.sgef_petalex_v_09.models.ItemVenta;
import org.example.sgef_petalex_v_09.models.Rosa;
import org.example.sgef_petalex_v_09.util.DialogHelper;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import org.example.sgef_petalex_v_09.validators.DataValidator;
import org.example.sgef_petalex_v_09.validators.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

public class RoseSelectionController implements Initializable {

    @FXML
    private GridPane gridRoses;
    @FXML
    private Button btnOk;
    @FXML
    private Button btnCancel;
    @FXML
    private Button btnEliminar;

    private String selectedRose = null;

    private final List<String> roseNames = Arrays.asList(
            "FREEDOM", "EXPLORER", "MONDIAL", "PINKMONDIAL",
            "GOTCHA", "QUEEN_SAND", "NINA", "PLAYA_BLANCA",
            "MOMENTUM", "PINK_FLOYD", "VENDELA", "HERMOSA");

    public enum ModoOperacion {
        AGREGAR_PRODUCTO,
        AGREGAR_A_PEDIDO
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        int cols = 4, row = 0, col = 0;
        for (String name : roseNames) {
            ImageView iv = new ImageView(new Image(
                    getClass().getResourceAsStream("/images/" + name + "_FOTO.PNG")));
            iv.setFitWidth(100);
            iv.setPreserveRatio(true);

            Label lbl = new Label(name.replace('_', ' '));
            lbl.setStyle("-fx-font-size:12px;-fx-text-fill:#333;");

            VBox cell = new VBox(5, iv, lbl);
            cell.setStyle("-fx-alignment:center;-fx-cursor:hand;-fx-padding:5;");
            cell.setOnMouseClicked(evt -> selectRose(cell, name));

            gridRoses.add(cell, col, row);
            if (++col >= cols) {
                col = 0;
                row++;
            }
        }
        btnOk.setDisable(false);
        btnEliminar.setVisible(false);
        btnEliminar.setManaged(false);
    }

    private ModoOperacion modoOperacion;

    public void setModoOperacion(ModoOperacion modoOperacion) {
        this.modoOperacion = modoOperacion;

        if (modoOperacion == ModoOperacion.AGREGAR_PRODUCTO) {
            btnOk.setText("Añadir producto");
            btnEliminar.setVisible(true);
            btnEliminar.setManaged(true); // importante para que ocupe espacio
        } else {
            btnOk.setText("Aceptar");
            btnEliminar.setVisible(false);
            btnEliminar.setManaged(false); // para que no ocupe espacio
        }
    }

    private void selectRose(VBox cell, String name) {
        gridRoses.getChildren().forEach(n -> n.setEffect(null));
        cell.setEffect(new DropShadow(10, Color.GREEN));
        selectedRose = name;
        btnOk.setDisable(false);
    }

    public ItemVenta showPaqueteCantidadDialog(String variedad) {
        Dialog<ItemVenta> dlg = new Dialog<>();
        dlg.setTitle("Configurar ítem");

        ButtonType cancelType = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType acceptType = new ButtonType("Aceptar", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(cancelType, acceptType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        Label lblVar = new Label("Variedad: " + variedad);
        ComboBox<String> cbLargo = new ComboBox<>(FXCollections.observableArrayList("AMERICANO", "RUSO"));
        cbLargo.setPromptText("Selecciona tipo de corte");

        ComboBox<String> cbPack = new ComboBox<>(FXCollections.observableArrayList(
                "Caja Tabaco", "Caja Full", "Cuartos"));
        cbPack.setPromptText("Selecciona paquete");

        Spinner<Integer> spQty = new Spinner<>(1, 999, 1);
        spQty.setEditable(true);

        grid.add(lblVar, 0, 0, 2, 1);
        grid.add(new Label("Tipo de Corte:"), 0, 1);
        grid.add(cbLargo, 1, 1);
        grid.add(new Label("Paquete:"), 0, 2);
        grid.add(cbPack, 1, 2);
        grid.add(new Label("Cantidad:"), 0, 3);
        grid.add(spQty, 1, 3);

        dlg.getDialogPane().setContent(grid);

        Button okBtn = (Button) dlg.getDialogPane().lookupButton(acceptType);
        okBtn.setDisable(true);

        javafx.beans.value.ChangeListener<Object> validador = (obs, oldV, newV) -> {
            boolean esValido = cbLargo.getValue() != null &&
                    cbPack.getValue() != null &&
                    spQty.getValue() != null && spQty.getValue() > 0;
            okBtn.setDisable(!esValido);
        };

        cbLargo.valueProperty().addListener(validador);
        cbPack.valueProperty().addListener(validador);
        spQty.valueProperty().addListener(validador);

        dlg.setResultConverter(btn -> {
            if (btn == acceptType) {
                ItemVenta it = new ItemVenta();
                it.setVariedad(variedad);
                it.setLargo(cbLargo.getValue());
                it.setPaquete(cbPack.getValue());
                it.setCantidad(spQty.getValue());

                double base = switch (cbPack.getValue()) {
                    case "Caja Tabaco" -> 50.0;
                    case "Caja Full" -> 120.0;
                    default -> 30.0;
                };
                if ("RUSO".equals(cbLargo.getValue()))
                    base += 10.0;

                it.setPrecioUnit(base);
                it.setPrecioTotal(base * spQty.getValue());
                return it;
            }
            return null;
        });

        Optional<ItemVenta> res = dlg.showAndWait();
        if (res.isPresent() && DialogHelper.confirm(
                btnOk.getScene().getWindow(), "¿Está seguro que desea agregar este ítem?")) {
            return res.get();
        }
        return null;
    }

    @FXML
    private void onAccept() {
        // Validar que se haya seleccionado una rosa
        if (selectedRose == null) {
            DialogHelper.showWarning(
                    btnOk.getScene().getWindow(),
                    "Debe seleccionar una variedad antes de continuar.");
            return;
        }

        Stage stage = (Stage) btnOk.getScene().getWindow();

        switch (modoOperacion) {
            case AGREGAR_PRODUCTO -> {
                // Abre el formulario para registrar un nuevo producto
                mostrarFormularioAgregarProducto(stage);
            }

            case AGREGAR_A_PEDIDO -> {
                // Abre el formulario de cantidad, largo y paquete
                ItemVenta item = showPaqueteCantidadDialog(selectedRose);
                if (item != null) {
                    itemSeleccionado = item;
                    stage.close(); // Cierra la ventana solo si se obtuvo un ítem válido
                }
            }
        }
    }

    public record ProductoNuevo(String nombre, File foto) {
    }

    private void mostrarFormularioAgregarProducto(Stage parent) {
        Dialog<ProductoNuevo> dialog = new Dialog<>();
        dialog.setTitle("Agregar nuevo producto");
        dialog.initOwner(parent);

        Label lblNombre = new Label("Nombre:");
        TextField txtNombre = new TextField(selectedRose);

        Label lblFoto = new Label("Fotografía:");
        Button btnSeleccionar = new Button("Seleccionar imagen");
        Label lblArchivo = new Label("(ninguno)");
        FileChooser fileChooser = new FileChooser();
        final File[] selectedFile = { null };

        btnSeleccionar.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(dialog.getOwner());
            if (file != null) {
                if (validarImagen(file)) {
                    selectedFile[0] = file;
                    lblArchivo.setText(file.getName());
                } else {
                    DialogHelper.showError(dialog.getOwner(),
                            "La imagen debe ser menor a 3 MB y de máximo 500x500 píxeles.");
                }
            }
        });

        VBox content = new VBox(10, lblNombre, txtNombre, lblFoto, btnSeleccionar, lblArchivo);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Botón OK para validación
        Button btnOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btnOk.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String nombre = txtNombre.getText().trim();

            ValidationResult result = DataValidator.validateNombreRosaProducto(nombre);
            if (!result.isValid()) {
                setErrorStyle(txtNombre, result.getErrorMessage());

                DialogHelper.showError(dialog.getDialogPane().getScene().getWindow(),
                        "Error al agregar producto, corrige los campos resaltados (mantén el cursor sobre el campo para más información)");

                event.consume(); // Evita que se cierre el diálogo
            } else {
                clearErrorStyle(txtNombre);
            }
        });

        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new ProductoNuevo(txtNombre.getText().trim(), selectedFile[0]);
            }
            return null;
        });

        Optional<ProductoNuevo> result = dialog.showAndWait();

        result.ifPresent(productoNuevo -> {
            System.out.println("Producto agregado: " + productoNuevo.nombre() + " con foto: " + productoNuevo.foto());
            // Aquí puedes continuar con el procesamiento, guardar en base, actualizar UI,
            // etc.
        });
    }

    @FXML
    private void onEliminar() {
        if (selectedRose == null) {
            DialogHelper.showError(null, "Seleccione una rosa para eliminar.");
            return;
        }

        boolean confirm = DialogHelper.confirm(null, "¿Está seguro que desea eliminar " + selectedRose + "?");

        if (confirm) {
            System.out.println("Producto eliminado: " + selectedRose);
            selectedRose = null;
            // Deshabilitar botón eliminar porque no hay selección
            btnEliminar.setDisable(true);
            // Quitar efecto selección
            gridRoses.getChildren().forEach(n -> n.setEffect(null));
            // btnOk sigue habilitado siempre
        }
    }

    @FXML
    private void onCancel() {
        ((Stage) btnCancel.getScene().getWindow()).close();
    }

    public String getSelectedRose() {
        return selectedRose;
    }

    private static boolean validarImagen(File file) {
        long maxSize = 3 * 1024 * 1024; // 3 MB

        if (file.length() > maxSize)
            return false;

        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null)
                return false;

            int width = image.getWidth();
            int height = image.getHeight();

            return width <= 500 && height <= 500;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void setErrorStyle(Control control, String message) {
        Platform.runLater(() -> {
            control.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            Tooltip tooltip = new Tooltip(
                    "Error, corrige los campos resaltados (mantén el cursor sobre el campo para más información)\n"
                            + message);
            tooltip.setStyle("-fx-background-color: #ffdddd; -fx-text-fill: red;");
            control.setTooltip(tooltip);
        });
    }

    private void clearErrorStyle(Control control) {
        Platform.runLater(() -> {
            control.setStyle(null);
            control.setTooltip(null);
        });
    }

    // Agrega un campo para el resultado completo
    private ItemVenta itemSeleccionado = null;

    public ItemVenta getItemSeleccionado() {
        return itemSeleccionado;
    }

}
