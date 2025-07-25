package org.example.sgef_petalex_v_09.validators;

import java.util.regex.Pattern;

public class DataValidator {

    // Patrones de validación
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{2,50}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^09[0-9]{8}$");
    private static final Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("^[0-9]+(\\.[0-9]{1,2})?$");
    private static final Pattern RUC_PATTERN = Pattern.compile("^[0-9]{13}$");

    // ✅ Validación de nombres (solo letras, espacios y acentos)
    public static ValidationResult validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " es obligatorio");
        }

        String trimmedName = name.trim();

        if (trimmedName.length() < 2) {
            return ValidationResult.error(fieldName,
                    "El " + fieldName.toLowerCase() + " debe tener al menos 2 caracteres");
        }

        if (trimmedName.length() > 50) {
            return ValidationResult.error(fieldName,
                    "El " + fieldName.toLowerCase() + " no puede tener más de 50 caracteres");
        }

        if (!NAME_PATTERN.matcher(trimmedName).matches()) {
            return ValidationResult.error(fieldName,
                    "El " + fieldName.toLowerCase() + " solo puede contener letras, espacios y acentos");
        }

        return ValidationResult.success();
    }

    // ✅ Validación de email
    public static ValidationResult validateEmail(String email, String fieldName) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " es obligatorio");
        }

        String trimmedEmail = email.trim();

        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            return ValidationResult.error(fieldName,
                    "El formato del " + fieldName.toLowerCase() + " es inválido. Debe ser: usuario@dominio.com");
        }

        return ValidationResult.success();
    }

    // ✅ Validación de teléfono celular ecuatoriano
    public static ValidationResult validatePhone(String phone, String fieldName) {
        if (phone == null || phone.trim().isEmpty()) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " es obligatorio");
        }

        String trimmedPhone = phone.trim().replaceAll("[\\s-]", ""); // Remover espacios y guiones

        if (!PHONE_PATTERN.matcher(trimmedPhone).matches()) {
            return ValidationResult.error(fieldName,
                    "El " + fieldName.toLowerCase() + " debe tener formato 09XXXXXXXX (10 dígitos comenzando con 09)");
        }

        return ValidationResult.success();
    }

    // ✅ Validación de números enteros
    public static ValidationResult validateNumeric(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " es obligatorio");
        }

        String trimmedValue = value.trim();

        if (!NUMERIC_PATTERN.matcher(trimmedValue).matches()) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " solo puede contener números");
        }

        return ValidationResult.success();
    }

    // ✅ Validación de números decimales (precio, costos)
    public static ValidationResult validateDecimal(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " es obligatorio");
        }

        String trimmedValue = value.trim();

        if (!DECIMAL_PATTERN.matcher(trimmedValue).matches()) {
            return ValidationResult.error(fieldName,
                    "El " + fieldName.toLowerCase() + " debe ser un número válido (ej: 123.45)");
        }

        try {
            double numericValue = Double.parseDouble(trimmedValue);
            if (numericValue < 0) {
                return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " no puede ser negativo");
            }
            if (numericValue > 999999.99) {
                return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " es demasiado grande");
            }
        } catch (NumberFormatException e) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " no es un número válido");
        }

        return ValidationResult.success();
    }

    // ✅ Validación de cédula ecuatoriana
    public static ValidationResult validateEcuadorianID(String cedula, String fieldName) {
        if (cedula == null || cedula.trim().isEmpty()) {
            return ValidationResult.error(fieldName, "La " + fieldName.toLowerCase() + " es obligatoria");
        }

        String trimmedCedula = cedula.trim();

        if (trimmedCedula.length() != 10) {
            return ValidationResult.error(fieldName,
                    "La " + fieldName.toLowerCase() + " debe tener exactamente 10 dígitos");
        }

        if (!NUMERIC_PATTERN.matcher(trimmedCedula).matches()) {
            return ValidationResult.error(fieldName, "La " + fieldName.toLowerCase() + " solo puede contener números");
        }

        // Algoritmo de validación de cédula ecuatoriana
        if (!isValidEcuadorianID(trimmedCedula)) {
            return ValidationResult.error(fieldName,
                    "La " + fieldName.toLowerCase() + " no es válida según el algoritmo ecuatoriano");
        }

        return ValidationResult.success();
    }

    // ✅ Validación de RUC ecuatoriano
    public static ValidationResult validateRUC(String ruc, String fieldName) {
        if (ruc == null || ruc.trim().isEmpty()) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " es obligatorio");
        }

        String trimmedRuc = ruc.trim();

        if (trimmedRuc.length() != 13) {
            return ValidationResult.error(fieldName,
                    "El " + fieldName.toLowerCase() + " debe tener exactamente 13 dígitos");
        }

        if (!RUC_PATTERN.matcher(trimmedRuc).matches()) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " solo puede contener números");
        }

        // Los primeros 10 dígitos deben ser una cédula válida
        String cedulaPart = trimmedRuc.substring(0, 10);
        if (!isValidEcuadorianID(cedulaPart)) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase()
                    + " no es válido (los primeros 10 dígitos deben formar una cédula válida)");
        }

        // Los últimos 3 dígitos deben ser 001
        if (!trimmedRuc.endsWith("001")) {
            return ValidationResult.error(fieldName, "El " + fieldName.toLowerCase() + " debe terminar en 001");
        }

        return ValidationResult.success();
    }

    // ✅ Validación de rangos numéricos
    public static ValidationResult validateRange(String value, String fieldName, double min, double max) {
        ValidationResult numericValidation = validateDecimal(value, fieldName);
        if (!numericValidation.isValid()) {
            return numericValidation;
        }

        double numericValue = Double.parseDouble(value.trim());
        if (numericValue < min || numericValue > max) {
            return ValidationResult.error(fieldName,
                    "El " + fieldName.toLowerCase() + " debe estar entre " + min + " y " + max);
        }

        return ValidationResult.success();
    }

    // ✅ Algoritmo de validación de cédula ecuatoriana
    private static boolean isValidEcuadorianID(String cedula) {
        try {
            int[] digits = new int[10];
            for (int i = 0; i < 10; i++) {
                digits[i] = Integer.parseInt(String.valueOf(cedula.charAt(i)));
            }

            // Verificar que el tercer dígito sea menor a 6 (persona natural)
            if (digits[2] >= 6) {
                return false;
            }

            // Algoritmo de validación
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                int digit = digits[i];
                if (i % 2 == 0) {
                    digit *= 2;
                    if (digit > 9) {
                        digit -= 9;
                    }
                }
                sum += digit;
            }

            int checkDigit = (10 - (sum % 10)) % 10;
            return checkDigit == digits[9];

        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ✅ Validación combinada para formularios
    public static ValidationResult validatePersonData(String name, String email, String phone) {
        ValidationResult nameResult = validateName(name, "Nombre");
        if (!nameResult.isValid())
            return nameResult;

        ValidationResult emailResult = validateEmail(email, "Correo electrónico");
        if (!emailResult.isValid())
            return emailResult;

        ValidationResult phoneResult = validatePhone(phone, "Teléfono");
        if (!phoneResult.isValid())
            return phoneResult;

        return ValidationResult.success();
    }

    // ✅ Validación para proveedores
    public static ValidationResult validateProviderData(String ruc, String name, String email, String phone) {
        ValidationResult rucResult = validateRUC(ruc, "RUC");
        if (!rucResult.isValid())
            return rucResult;

        return validatePersonData(name, email, phone);
    }
}
