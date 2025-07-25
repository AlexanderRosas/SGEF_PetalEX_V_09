package org.example.sgef_petalex_v_09.validators;

import javafx.stage.Window;
import org.example.sgef_petalex_v_09.util.DialogHelper;

public class ValidationResult {
    private final boolean valid;
    private final String errorMessage;
    private final String fieldName;

    public ValidationResult(boolean valid, String fieldName, String errorMessage) {
        this.valid = valid;
        this.fieldName = fieldName;
        this.errorMessage = errorMessage;
    }

    // Resultado exitoso
    public static ValidationResult success() {
        return new ValidationResult(true, "", "");
    }

    // Resultado con error
    public static ValidationResult error(String fieldName, String errorMessage) {
        return new ValidationResult(false, fieldName, errorMessage);
    }

    public boolean isValid() {
        return valid;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getFieldName() {
        return fieldName;
    }

    // Mostrar error si no es válido
    public void showErrorIfInvalid(Window owner) {
        if (!valid) {
            DialogHelper.showValidationError(owner, fieldName, errorMessage);
        }
    }

    @Override
    public String toString() {
        return valid ? "Válido" : "Error en " + fieldName + ": " + errorMessage;
    }
}
