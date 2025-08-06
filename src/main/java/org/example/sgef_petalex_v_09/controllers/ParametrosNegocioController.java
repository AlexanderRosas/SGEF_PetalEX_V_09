package org.example.sgef_petalex_v_09.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.sgef_petalex_v_09.util.CSVUtil;
import org.example.sgef_petalex_v_09.util.DialogHelper;

public class ParametrosNegocioController {

    @FXML private TextField tfPorcentajeIVA;
    @FXML private Label lblHiddenFocus; // Añade un Label oculto en el FXML

    @FXML
    public void initialize() {
        // Cargar el valor actual de IVA desde el archivo CSV
        double ivaActual = CSVUtil.leerIva();
        tfPorcentajeIVA.setText(String.valueOf(ivaActual));

        // Transferir el foco a un Label oculto para evitar el resaltado del panel
        lblHiddenFocus.requestFocus();
    }

    @FXML
    private void onGuardar() {
        try {
            double iva = Double.parseDouble(tfPorcentajeIVA.getText().trim());
            if (iva < 0 || iva > 100) {
                throw new NumberFormatException();
            }

            // Guardar el nuevo valor de IVA en el archivo CSV
            CSVUtil.guardarIva(iva);

            DialogHelper.showSuccess(
                    tfPorcentajeIVA.getScene().getWindow(),
                    "IVA actualizado a " + iva + "%"
            );

        } catch (NumberFormatException e) {
            DialogHelper.showValidationError(
                    tfPorcentajeIVA.getScene().getWindow(),
                    "Porcentaje de IVA",
                    "Introduce un número válido entre 0 y 100."
            );
        }
    }
}