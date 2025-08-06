package org.example.sgef_petalex_v_09.util;

import org.example.sgef_petalex_v_09.models.*;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {
    public static final String VENTAS_CSV = "data/ventas.csv";
    public static final String CLIENTES_CSV = "data/clientes.csv";
    public static final String USUARIOS_CSV = "data/usuarios.csv";
    public static final String PEDIDOS_CSV = "data/pedidos.csv";
    public static final String IVA_CSV = "data/iva.csv";

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Método para leer el IVA
    public static double leerIva() {
        List<Iva> ivas = new ArrayList<>();
        try {
            crearArchivoSiNoExiste(IVA_CSV);
            try (BufferedReader br = new BufferedReader(new FileReader(IVA_CSV))) {
                String line;
                boolean header = true;
                while ((line = br.readLine()) != null) {
                    if (header) {
                        header = false;
                        continue;
                    }
                    String[] campos = line.split(";", -1);
                    if (campos.length == 1) {
                        double porcentaje = Double.parseDouble(campos[0].trim());
                        ivas.add(new Iva(porcentaje));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo IVA: " + e.getMessage());
            e.printStackTrace();
        }
        return ivas.isEmpty() ? 15.0 : ivas.get(0).getPorcentaje();
    }

    // Método para guardar el IVA
    public static void guardarIva(double porcentaje) {
        try {
            crearArchivoSiNoExiste(IVA_CSV);
            try (PrintWriter pw = new PrintWriter(new FileWriter(IVA_CSV))) {
                pw.println("Porcentaje");
                pw.println(porcentaje);
            }
        } catch (IOException e) {
            System.err.println("Error guardando IVA: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Otros métodos existentes...

    private static void crearArchivoSiNoExiste(String rutaArchivo) throws IOException {
        Path path = Paths.get(rutaArchivo);
        if (!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) {
                if (rutaArchivo.equals(CLIENTES_CSV)) {
                    pw.println(
                            "ID;Nombre;IdentificadorEmpresarial;Pais;Direccion;Telefono;Correo;Estado;UsuarioModificacion;FechaModificacion");
                } else if (rutaArchivo.equals(VENTAS_CSV)) {
                    pw.println("ID;TipoDestino;Servicio;Cliente;Direccion;Fecha;Precio;IVA;Total;Estado;Items");
                } else if (rutaArchivo.equals(USUARIOS_CSV)) {
                    // Header sin Sucursal ni RUC
                    pw.println(
                            "ID;Nombre;Cedula;Correo;Contraseña; Usuario;Rol;Estado;Permisos;UsuarioModificacion;FechaModificacio");
                } else if (rutaArchivo.equals(PEDIDOS_CSV)) {
                    pw.println("ID;ClienteID;FechaPedido;FechaEnvio;Estado;GuiaAerea;PrecioTotal");

                } else if (rutaArchivo.equals(IVA_CSV)) {
                    pw.println("Porcentaje");
                }
            }
        }
    }

    public static Cliente buscarClientePorId(String id) {
        return leerClientes().stream()
                .filter(c -> id.equals(c.getId()))
                .findFirst()
                .orElse(null);
    }

    public static List<Venta> leerVentas() {
        return leerVentas(VENTAS_CSV);
    }

    public static List<Cliente> leerClientes() {
        List<Cliente> clientes = new ArrayList<>();
        Path path = Paths.get(CLIENTES_CSV);

        if (!Files.exists(path)) {
            System.err.println("El archivo de clientes no existe: " + CLIENTES_CSV);
            return clientes;
        }

        try (BufferedReader br = Files.newBufferedReader(path)) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) {
                    header = false;
                    continue;
                }
                String[] campos = line.split(";", -1);
                if (campos.length < 10)
                    continue;

                Cliente cliente = new Cliente();
                cliente.setId(campos[0].trim());
                cliente.setNombre(campos[1].trim());
                cliente.setIdentificadorEmpresarial(campos[2].trim());
                cliente.setPais(campos[3].trim());
                cliente.setDireccion(campos[4].trim());
                cliente.setTelefono(campos[5].trim());
                cliente.setCorreo(campos[6].trim());
                cliente.setEstado(campos[7].trim());
                cliente.setUsuarioModificacion(campos[8].trim());
                cliente.setFechaModificacion(
                        LocalDateTime.parse(campos[9].trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));

                clientes.add(cliente);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo clientes: " + e.getMessage());
            e.printStackTrace();
        }

        return clientes;
    }

    public static void guardarClientes(List<Cliente> clientes) {
        try {
            Files.createDirectories(Paths.get("data"));
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CLIENTES_CSV))) {
                writer.write(
                        "ID;Nombre;IdentificadorEmpresarial;Pais;Direccion;Telefono;Correo;Estado;UsuarioModificacion;FechaModificacion\n");
                for (Cliente cliente : clientes) {
                    writer.write(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s\n",
                            cliente.getId(),
                            cliente.getNombre(),
                            cliente.getIdentificadorEmpresarial(),
                            cliente.getPais(),
                            cliente.getDireccion(),
                            cliente.getTelefono(),
                            cliente.getCorreo(),
                            cliente.getEstado(),
                            cliente.getUsuarioModificacion(),
                            cliente.getFechaModificacion().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<Usuario> leerUsuarios() {
        List<Usuario> list = new ArrayList<>();
        Path path = Paths.get(USUARIOS_CSV);
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                // Header actualizado sin Sucursal ni RUC
                Files.write(path,
                        List.of("ID;Nombre;Correo;Usuario;Rol;Estado;Permisos;UsuarioModificacion;FechaModificacion"),
                        StandardOpenOption.CREATE);
            }
            try (BufferedReader br = Files.newBufferedReader(path)) {
                String line;
                boolean primera = true;
                while ((line = br.readLine()) != null) {
                    if (primera) {
                        primera = false;
                        continue;
                    }
                    String[] f = line.split(";", -1);
                    if (f.length < 11)
                        continue;

                    Usuario u = new Usuario();
                    u.setId(f[0].trim());
                    u.setNombre(f[1].trim());
                    u.setCedula(f[2].trim());
                    u.setCorreo(f[3].trim());
                    u.setPassword(f[4].trim()); // <- nuevo campo Contraseña
                    u.setUsuario(f[5].trim()); // <- campo Usuario
                    u.setRol(f[6].trim());
                    u.setEstado(f[7].trim());
                    u.setPermisos(f[8].trim());
                    u.setUsuarioModificacion(f[9].trim());

                    String fechaModStr = f[10].trim();
                    if (!fechaModStr.isEmpty()) {
                        try {
                            u.setFechaModificacion(
                                    LocalDateTime.parse(fechaModStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        } catch (Exception e) {
                            u.setFechaModificacion(null);
                        }
                    }
                    list.add(u);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void guardarUsuarios(List<Usuario> usuarios) {
        Path path = Paths.get(USUARIOS_CSV);
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {
                // Header sin Sucursal ni RUC
                bw.write(
                        "ID;Nombre;Cedula;Correo;Contraseña;Usuario;Rol;Estado;Permisos;UsuarioModificacion;FechaModificacion\n");
                for (Usuario u : usuarios) {
                    String fechaModStr = "";
                    if (u.getFechaModificacion() != null) {
                        fechaModStr = u.getFechaModificacion().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                    }
                    bw.write(String.join(";",
                            u.getId(),
                            u.getNombre(),
                            u.getCedula(),
                            u.getCorreo(),
                            u.getPassword(), // <- nuevo
                            u.getUsuario(), // <- nuevo
                            u.getRol(),
                            u.getEstado(),
                            u.getPermisos(),
                            u.getUsuarioModificacion() != null ? u.getUsuarioModificacion() : "",
                            fechaModStr));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    if (linea.trim().isEmpty())
                        continue;

                    String[] campos = linea.split(";", -1);
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
                                    double precio = Double.parseDouble(itemParts[1].trim().replace("$", "").trim());
                                    item.setPrecioTotal(precio);
                                    venta.addItem(item);
                                }
                            }
                        }
                        ventas.add(venta);
                    }
                }
            }
        } catch (IOException | DateTimeParseException | NumberFormatException e) {
            System.err.println("Error leyendo ventas: " + e.getMessage());
            try {
                crearArchivoSiNoExiste(rutaArchivo);
            } catch (IOException ex) {
                System.err.println("No se pudo crear archivo: " + ex.getMessage());
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
                pw.println("ID;TipoDestino;Servicio;Cliente;Direccion;Fecha;Precio;IVA;Total;Estado;Items");
                for (Venta venta : ventas) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("%s;%s;%s;\"%s\";\"%s\";%s;%.2f;%.2f;%.2f;%s;\"",
                            venta.getId(),
                            venta.getTipoDestino(),
                            venta.getServicio(),
                            venta.getCliente(),
                            venta.getDireccion(),
                            venta.getFecha().format(formatter),
                            venta.getPrecio(),
                            venta.getIva(),
                            venta.getTotal(),
                            venta.getEstado()));

                    List<String> itemsStr = new ArrayList<>();
                    for (ItemVenta item : venta.getItems()) {
                        itemsStr.add(String.format("%s - $%.2f", item.getVariedad(), item.getPrecioTotal()));
                    }
                    sb.append(String.join(", ", itemsStr)).append("\"");
                    pw.println(sb.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando ventas: " + e.getMessage());
        }
    }

    public static List<Pedido> leerPedidos() {
        List<Pedido> pedidos = new ArrayList<>();
        try {
            Path path = Paths.get(PEDIDOS_CSV);
            if (!Files.exists(path)) {
                Files.createFile(path);
                try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                    pw.println(
                            "ID;ClienteID;FechaPedido;FechaEnvio;Estado;GuiaAerea;PrecioTotal;EmpresaTransporte;FechaModificacion;UsuarioModificacion");
                }
            }

            try (BufferedReader br = Files.newBufferedReader(path)) {
                String linea;
                boolean primeraLinea = true;
                while ((linea = br.readLine()) != null) {
                    if (primeraLinea) {
                        primeraLinea = false;
                        continue;
                    }
                    String[] campos = linea.split(";", -1);
                    if (campos.length < 10)
                        continue;

                    try {
                        int id = Integer.parseInt(campos[0].trim());
                        String clienteId = campos[1].trim();
                        LocalDate fechaPedido = LocalDate.parse(campos[2].trim());
                        LocalDate fechaEnvio = LocalDate.parse(campos[3].trim());
                        String estado = campos[4].trim();
                        String guiaAerea = campos[5].trim();
                        double precioTotal = Double.parseDouble(campos[6].trim().replace(",", "."));
                        String empresaTransporte = campos[7].trim();
                        LocalDateTime fechaModificacion = LocalDateTime.parse(campos[8].trim(),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        String usuarioModificacion = campos[9].trim();

                        // Crear cliente “ligero” solo con el ID
                        Cliente cliente = new Cliente();
                        cliente.setId(clienteId);

                        Pedido pedido = new Pedido();
                        pedido.setId(id);
                        pedido.setCliente(cliente); // ✅
                        pedido.setFechaPedido(fechaPedido);
                        pedido.setFechaEstimadaEnvio(fechaEnvio);
                        pedido.setEstadoActual(estado);
                        pedido.setCodigoGuiaAerea(guiaAerea);
                        pedido.setPrecioTotal(precioTotal);
                        pedido.setEmpresaTransporte(empresaTransporte);
                        pedido.setFechaModificacion(fechaModificacion);
                        pedido.setUsuarioModificacion(usuarioModificacion);

                        pedidos.add(pedido);
                    } catch (Exception e) {
                        System.err.println("Error en línea: " + linea + ", motivo: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    public static void guardarPedidos(List<Pedido> pedidos) {
        try {
            Path path = Paths.get(PEDIDOS_CSV);
            Files.createDirectories(path.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                bw.write(
                        "ID;ClienteID;FechaPedido;FechaEnvio;Estado;GuiaAerea;PrecioTotal;EmpresaTransporte;FechaModificacion;UsuarioModificacion\n");

                for (Pedido p : pedidos) {
                    bw.write(String.format("%d;%s;%s;%s;%s;%s;%.2f;%s;%s;%s\n",
                            p.getId(),
                            p.getCliente().getId(), // 👈 id del cliente
                            p.getFechaPedido(),
                            p.getFechaEstimadaEnvio(),
                            p.getEstadoActual(),
                            p.getCodigoGuiaAerea(),
                            p.getPrecioTotal(),
                            p.getEmpresaTransporte(),
                            p.getFechaModificacion().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            p.getUsuarioModificacion()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}