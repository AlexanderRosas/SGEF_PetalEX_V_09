package org.example.sgef_petalex_v_09.services;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class InventarioService {
    private static final String INVENTARIO_CSV = "data/inventario.csv";

    public static void crearArchivoInventarioSiNoExiste() throws IOException {
        Path path = Paths.get(INVENTARIO_CSV);
        if (!Files.exists(path)) {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            Files.createFile(path);
            try (PrintWriter pw = new PrintWriter(new FileWriter(INVENTARIO_CSV))) {
                // Cabecera del CSV de inventario (con coma, no punto y coma)
                pw.println("Variedad,Largo,Unidades");
            }
        }
    }

    static class RegistroInventario {
        String variedad;
        String largo;
        int unidades;

        RegistroInventario(String variedad, String largo, int unidades) {
            this.variedad = variedad;
            this.largo = largo;
            this.unidades = unidades;
        }

        @Override
        public String toString() {
            return variedad + "," + largo + "," + unidades;
        }
    }

    public static List<RegistroInventario> leerInventario() throws IOException {
        Path path = Paths.get(INVENTARIO_CSV);
        if (!Files.exists(path))
            return new ArrayList<>();

        try (var lines = Files.lines(path)) {
            return lines
                    .skip(1)
                    .map(linea -> {
                        String[] partes = linea.split(",");
                        if (partes.length < 3)
                            return null;
                        try {
                            return new RegistroInventario(
                                    partes[0].trim(),
                                    partes[1].trim(),
                                    Integer.parseInt(partes[2].trim()));
                        } catch (NumberFormatException e) {
                            // Podrías loguear o manejar error, pero aquí se ignora la línea corrupta
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
    }

    public static void agregarUnidades(String variedad, String largo, int unidades) throws IOException {
        List<RegistroInventario> registros = leerInventario();
        boolean encontrado = false;

        for (RegistroInventario r : registros) {
            if (r.variedad.equalsIgnoreCase(variedad) && r.largo.equalsIgnoreCase(largo)) {
                r.unidades += unidades;
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            registros.add(new RegistroInventario(variedad, largo, unidades));
        }

        guardarInventario(registros);
    }

    public static boolean consumirUnidades(String variedad, String largo, int unidades) throws IOException {
        List<RegistroInventario> inventario = leerInventario();
        boolean encontrado = false;

        for (RegistroInventario registro : inventario) {
            if (registro.variedad.equalsIgnoreCase(variedad) && registro.largo.equalsIgnoreCase(largo)) {
                if (registro.unidades >= unidades) {
                    registro.unidades -= unidades;
                    encontrado = true;
                    break;
                } else {
                    return false; // No hay suficientes unidades
                }
            }
        }

        if (!encontrado) {
            return false; // No se encontró el registro
        }

        guardarInventario(inventario); // Guardar cambios
        return true;
    }

    private static void guardarInventario(List<RegistroInventario> registros) throws IOException {
        List<String> lineas = new ArrayList<>();
        lineas.add("Variedad,Largo,Unidades");
        for (RegistroInventario r : registros) {
            lineas.add(r.toString());
        }

        Files.write(Paths.get(INVENTARIO_CSV), lineas, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
