package org.example.sgef_petalex_v_09.util;

import org.example.sgef_petalex_v_09.models.Venta;
import org.example.sgef_petalex_v_09.models.ItemVenta;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CSVUtil {
    public static final String VENTAS_CSV = "ventas.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static void crearArchivoSiNoExiste(String rutaArchivo) throws IOException {
        Path path = Paths.get(rutaArchivo);
        if (!Files.exists(path)) {
            Files.createFile(path);
            // Escribir encabezado
            try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) {
                pw.println("ID,TipoDestino,Servicio,Cliente,Direccion,Fecha,Precio,IVA,Total,Estado,Items");
            }
        }
    }

    public static List<Venta> leerVentas() {
        return leerVentas(VENTAS_CSV);
    }

    public static List<Venta> leerVentas(String rutaArchivo) {
        List<Venta> ventas = new ArrayList<>();
        
        try {
            crearArchivoSiNoExiste(rutaArchivo);
            
            try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
                String linea;
                boolean primeraLinea = true;
                
                while ((linea = br.readLine()) != null) {
                    if (primeraLinea) {
                        primeraLinea = false;
                        continue;
                    }
                    if (linea.trim().isEmpty()) continue;

                    try {
                        String[] campos = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                        if (campos.length >= 10) {
                            Venta venta = new Venta();
                            venta.setId(campos[0].trim());
                            venta.setTipoDestino(campos[1].trim());
                            venta.setServicio(campos[2].trim());
                            venta.setCliente(campos[3].replaceAll("\"", "").trim());
                            venta.setDireccion(campos[4].replaceAll("\"", "").trim());
                            venta.setFecha(LocalDate.parse(campos[5].trim()));
                            venta.setTotal(Double.parseDouble(campos[8].trim()));
                            venta.setEstado(campos[9].trim());

                            if (campos.length > 10) {
                                String itemsStr = campos[10].replaceAll("\"", "").trim();
                                for (String itemData : itemsStr.split(",")) {
                                    String[] itemParts = itemData.trim().split("-");
                                    if (itemParts.length >= 2) {
                                        ItemVenta item = new ItemVenta();
                                        item.setVariedad(itemParts[0].trim());
                                        String precioStr = itemParts[1].trim().replace("$", "").trim();
                                        double precio = Double.parseDouble(precioStr);
                                        item.setPrecioTotal(precio);
                                        venta.addItem(item);
                                    }
                                }
                            }
                            ventas.add(venta);
                        }
                    } catch (DateTimeParseException | NumberFormatException e) {
                        System.err.println("Error procesando línea: " + linea + "\n" + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error con archivo CSV: " + e.getMessage());
            // Crear archivo vacío si hay error
            try {
                crearArchivoSiNoExiste(rutaArchivo);
            } catch (IOException ex) {
                System.err.println("No se pudo crear el archivo: " + ex.getMessage());
            }
        }
        return ventas;
    }

    public static void guardarVentas(List<Venta> ventas) {
        guardarVentas(ventas, VENTAS_CSV);
    }

    public static void guardarVentas(List<Venta> ventas, String rutaArchivo) {
        try {
            crearArchivoSiNoExiste(rutaArchivo);
            
            try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) {
                // Escribir encabezado
                pw.println("ID,TipoDestino,Servicio,Cliente,Direccion,Fecha,Precio,IVA,Total,Estado,Items");
                
                // Escribir datos
                for (Venta venta : ventas) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("%s,%s,%s,\"%s\",\"%s\",%s,%.2f,%.2f,%.2f,%s,\"",
                        venta.getId(),
                        venta.getTipoDestino(),
                        venta.getServicio(),
                        venta.getCliente(),
                        venta.getDireccion(),
                        venta.getFecha().format(formatter),
                        venta.getPrecio(),
                        venta.getIva(),
                        venta.getTotal(),
                        venta.getEstado()
                    ));

                    // Agregar items
                    List<String> itemsStr = new ArrayList<>();
                    for (ItemVenta item : venta.getItems()) {
                        itemsStr.add(String.format("%s - $%.2f",
                            item.getVariedad(),
                            item.getPrecioTotal()
                        ));
                    }
                    sb.append(String.join(", ", itemsStr)).append("\"");

                    pw.println(sb.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando ventas en CSV: " + e.getMessage());
        }
    }
}