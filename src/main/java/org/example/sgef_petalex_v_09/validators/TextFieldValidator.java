package org.example.sgef_petalex_v_09.validators;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

public class TextFieldValidator {

    public enum ValidationType {
        NAME_ONLY,
        EMAIL,
        PHONE,
        NUMERIC,
        DECIMAL,
        CEDULA,
        RUC
    }

    // ✅ Aplicar validación en tiempo real
    public static void setupValidation(TextField field, ValidationType type) {
        switch (type) {
            case NAME_ONLY:
                applyNameValidation(field);
                break;
            case EMAIL:
                applyEmailValidation(field);
                break;
            case PHONE:
                applyPhoneValidation(field);
                break;
            case NUMERIC:
                applyNumericValidation(field);
                break;
            case DECIMAL:
                applyDecimalValidation(field);
                break;
            case CEDULA:
                applyCedulaValidation(field);
                break;
            case RUC:
                applyRUCValidation(field);
                break;
        }
    }

    // ✅ Validación para nombres (solo letras)
    public static void applyNameValidation(TextField field) {
        field.setTooltip(new Tooltip("Solo letras, espacios y acentos"));

        field.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*")) {
                field.setText(oldText);
            }
            updateFieldStyle(field, newText != null && newText.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$"));
        });
    }

    // ✅ Validación para email
    public static void applyEmailValidation(TextField field) {
        field.setTooltip(new Tooltip("Formato: usuario@dominio.com"));

        field.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Cuando pierde el foco
                String email = field.getText();
                boolean valid = email != null && email.matches("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
                updateFieldStyle(field, valid);
            }
        });
    }

    // ✅ Validación para teléfono
    public static void applyPhoneValidation(TextField field) {
        field.setTooltip(new Tooltip("Formato: 09XXXXXXXX"));

        field.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("[0-9]*")) {
                field.setText(oldText);
            }
            if (newText != null && newText.length() > 10) {
                field.setText(newText.substring(0, 10));
            }
            updateFieldStyle(field, newText != null && newText.matches("^09[0-9]{8}$"));
        });
    }

    // ✅ Validación numérica
    public static void applyNumericValidation(TextField field) {
        field.setTooltip(new Tooltip("Solo números"));

        field.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("[0-9]*")) {
                field.setText(oldText);
            }
            updateFieldStyle(field, newText != null && !newText.isEmpty() && newText.matches("^[0-9]+$"));
        });
    }

    // ✅ Validación decimal
    public static void applyDecimalValidation(TextField field) {
        field.setTooltip(new Tooltip("Números decimales: 123.45"));

        field.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("[0-9]*\\.?[0-9]*")) {
                field.setText(oldText);
            }
            if (newText != null && newText.contains(".")) {
                String[] parts = newText.split("\\.");
                if (parts.length > 2 || (parts.length == 2 && parts[1].length() > 2)) {
                    field.setText(oldText);
                }
            }
            updateFieldStyle(field, newText != null && newText.matches("^[0-9]+(\\.[0-9]{1,2})?$"));
        });
    }

    // ✅ Validación para cédula
    public static void applyCedulaValidation(TextField field) {
        field.setTooltip(new Tooltip("Cédula ecuatoriana: 10 dígitos"));

        field.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("[0-9]*")) {
                field.setText(oldText);
            }
            if (newText != null && newText.length() > 10) {
                field.setText(newText.substring(0, 10));
            }
            updateFieldStyle(field, newText != null && newText.length() == 10);
        });
    }

    // ✅ Validación para RUC
    public static void applyRUCValidation(TextField field) {
        field.setTooltip(new Tooltip("RUC ecuatoriano: 13 dígitos"));

        field.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && !newText.matches("[0-9]*")) {
                field.setText(oldText);
            }
            if (newText != null && newText.length() > 13) {
                field.setText(newText.substring(0, 13));
            }
            updateFieldStyle(field, newText != null && newText.length() == 13);
        });
    }

    // ✅ Actualizar estilo visual del campo
    private static void updateFieldStyle(TextField field, boolean isValid) {
        field.getStyleClass().removeAll("text-field-valid", "text-field-invalid");

        if (field.getText() != null && !field.getText().trim().isEmpty()) {
            if (isValid) {
                field.getStyleClass().add("text-field-valid");
            } else {
                field.getStyleClass().add("text-field-invalid");
            }
        }
    }
}
