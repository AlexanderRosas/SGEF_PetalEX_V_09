package org.example.sgef_petalex_v_09.util;

import java.util.Map;

public class PaisUtil {

    // Prefijos telefónicos E.164
    public static final Map<String, String> PREFIJOS = Map.of(
        "ECUADOR", "+593",
        "ESTADOS UNIDOS", "+1",
        "CANADÁ", "+1",
        "ESPAÑA", "+34",
        "ALEMANIA", "+49",
        "FRANCIA", "+33",
        "PAÍSES BAJOS", "+31"
    );

    // Formatos de identificador empresarial
    public static final Map<String, String> FORMATOS_IDENTIFICADOR = Map.of(
        "ECUADOR", "CI (10 dígitos) o RUC (13 dígitos)",
        "ESTADOS UNIDOS", "EIN: XX-XXXXXXX",
        "CANADÁ", "Business Number: 9 dígitos",
        "ESPAÑA", "VAT: ES + formato local",
        "ALEMANIA", "VAT: DE + formato local",
        "FRANCIA", "VAT: FR + formato local",
        "PAÍSES BAJOS", "VAT: NL + formato local"
    );

    // Códigos ISO 2 letras
    public static final Map<String, String> ISO2 = Map.of(
        "ECUADOR", "EC",
        "ESTADOS UNIDOS", "US",
        "CANADÁ", "CA",
        "ESPAÑA", "ES",
        "ALEMANIA", "DE",
        "FRANCIA", "FR",
        "PAÍSES BAJOS", "NL"
    );

    // Método auxiliar
    public static String getPrefijo(String pais) {
        return PREFIJOS.getOrDefault(pais.toUpperCase(), "");
    }

    public static String getFormatoIdentificador(String pais) {
        return FORMATOS_IDENTIFICADOR.getOrDefault(pais.toUpperCase(), "Formato no definido");
    }

    public static String getISO2(String pais) {
        return ISO2.getOrDefault(pais.toUpperCase(), "");
    }
}