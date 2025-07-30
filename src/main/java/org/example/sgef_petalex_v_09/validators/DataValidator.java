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
    private static final Pattern EMPRESA_NOMBRE_PATTERN = Pattern.compile("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s\\.\\&\\-]{1,60}$");

    private static final Pattern DIRECCION_PATTERN = Pattern.compile("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\\s\\.\\,\\-]{1,100}$");

    // Teléfono E.164: “+” opcional y hasta 15 dígitos
    private static final Pattern PHONE_E164_PATTERN = Pattern.compile("^\\+?[0-9]{1,15}$");
    /* ============== Validaciones específicas ============== */

    /** Nombre de la empresa. */
    public static ValidationResult validateEmpresaNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return ValidationResult.error("Nombre", "El nombre de la empresa es obligatorio");
        }
        if (!EMPRESA_NOMBRE_PATTERN.matcher(nombre).matches()) {
            return ValidationResult.error("Nombre",
                    "Debe tener 1-60 letras, tildes, ñ o espacios");
        }
        return ValidationResult.success();
    }

    /** Identificador empresarial según país. */
    public static ValidationResult validateIdentificador(String identificador, String pais) {
        if (identificador == null || identificador.isBlank()) {
            return ValidationResult.error("Identificador", "El identificador es obligatorio");
        }

        String id = identificador.trim();

        switch (pais.toUpperCase()) {
            case "ECUADOR":
                // Solo RUC válido: 13 dígitos terminados en 001 y que los primeros 10 dígitos
                // formen una cédula válida
                if (id.length() == 13 && id.endsWith("001")) {
                    return validateRUC(id, "RUC");
                } else {
                    return ValidationResult.error("Identificador",
                            "El RUC debe tener 13 dígitos terminando en 001");
                }

            case "ESTADOS UNIDOS":
                if (!id.matches("^\\d{2}-\\d{7}$")) {
                    return ValidationResult.error("Identificador",
                            "El EIN debe tener formato XX-XXXXXXX");
                }
                break;

            case "CANADÁ":
            case "CANADA":
                if (!id.matches("^\\d{9}$")) {
                    return ValidationResult.error("Identificador",
                            "El Business Number debe tener 9 dígitos");
                }
                break;

            case "ALEMANIA":
            case "FRANCIA":
            case "ESPAÑA":
            case "PAÍSES BAJOS":
            case "PAISES BAJOS":
                if (!id.matches("^[A-Z]{2}[A-Z0-9]{2,12}$")) {
                    return ValidationResult.error("Identificador",
                            "El VAT debe empezar por código ISO (2 letras) y seguir formato local");
                }
                break;

            default:
                return ValidationResult.error("País", "País no soportado para validación");
        }
        return ValidationResult.success();
    }

    /** Dirección de la empresa. */
    public static ValidationResult validateDireccion(String direccion) {
        if (direccion == null || direccion.isBlank()) {
            return ValidationResult.error("Dirección", "La dirección es obligatoria");
        }
        if (!DIRECCION_PATTERN.matcher(direccion.trim()).matches()) {
            return ValidationResult.error("Dirección",
                    "Máx. 100 caracteres: letras, números, punto, guion o espacio");
        }
        return ValidationResult.success();
    }

    /** Teléfono internacional E.164. */
    public static ValidationResult validateTelefonoE164(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            return ValidationResult.error("Teléfono", "El teléfono es obligatorio");
        }
        if (!PHONE_E164_PATTERN.matcher(telefono.trim()).matches()) {
            return ValidationResult.error("Teléfono",
                    "Formato E.164 inválido para teléfono(ej. +593991234567)");
        }
        return ValidationResult.success();
    }

    /** Correo electrónico RFC 5322. */
    public static ValidationResult validateCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return ValidationResult.error("Correo", "El correo es obligatorio");
        }
        if (!EMAIL_PATTERN.matcher(correo.trim()).matches()) {
            return ValidationResult.error("Correo", "Formato de correo inválido (ej. nombre@dominio.com)");
        }
        return ValidationResult.success();
    }

    /** País (solo verifica que no esté vacío). */
    public static ValidationResult validatePais(String pais) {
        if (pais == null || pais.isBlank()) {
            return ValidationResult.error("País", "Debe seleccionar un país");
        }
        return ValidationResult.success();
    }

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
                    "La " + fieldName.toLowerCase() + " no válida.");
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

    // Valida nombre de usuario: alfanumérico 5-10 caracteres
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.error("Nombre de usuario", "El nombre de usuario es obligatorio");
        }
        String u = username.trim();
        if (u.length() < 5 || u.length() > 10) {
            return ValidationResult.error("Nombre de usuario", "Debe tener entre 5 y 10 caracteres");
        }
        if (!u.matches("^[a-zA-Z0-9]+$")) {
            return ValidationResult.error("Nombre de usuario", "Solo puede contener letras y números");
        }
        return ValidationResult.success();
    }

    // Valida contraseña: 5-20 caracteres, incluye símbolos especiales permitidos
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.error("Contraseña", "La contraseña es obligatoria");
        }
        if (password.length() < 5 || password.length() > 20) {
            return ValidationResult.error("Contraseña", "Debe tener entre 5 y 20 caracteres");
        }
        if (!password.matches("^[a-zA-Z0-9!@#$%^&*()_+\\-=\\[\\]{}|\\\\:;\"'<>,.?/~]+$")) {
            return ValidationResult.error("Contraseña", "Contiene caracteres inválidos");
        }
        return ValidationResult.success();
    }

    // Valida nombre natural: solo letras (incluye vocales tildadas), espacios, 8-60
    // caracteres
    public static ValidationResult validateNaturalName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return ValidationResult.error("Nombre natural", "El nombre natural es obligatorio");
        }
        String n = name.trim();
        if (n.length() < 8 || n.length() > 60) {
            return ValidationResult.error("Nombre natural", "Debe tener entre 8 y 60 caracteres");
        }
        if (!n.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            return ValidationResult.error("Nombre natural", "Solo puede contener letras y espacios");
        }
        return ValidationResult.success();
    }

}
