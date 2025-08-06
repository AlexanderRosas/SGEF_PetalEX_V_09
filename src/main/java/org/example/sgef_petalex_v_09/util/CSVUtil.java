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
import java.util.Locale;
import java.util.stream.Collectors;

public class CSVUtil {
    public static final String VENTAS_CSV = "data/ventas.csv";
    public static final String CLIENTES_CSV = "data/clientes.csv";
    public static final String USUARIOS_CSV = "data/usuarios.csv";
    public static final String PEDIDOS_CSV = "data/pedidos.csv";
    public static final String IVA_CSV = "data/iva.csv";
    public static final String PROVEEDORES_CSV = "data/proveedores.csv";

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
                    pw.println(
                            "ID;TipoDestino;Servicio;Cliente;Direccion;Fecha;Pais;Precio;IVA;Total;Estado;UsuarioResponsable;Items");

                } else if (rutaArchivo.equals(USUARIOS_CSV)) {
                    // Header sin Sucursal ni RUC
                    pw.println(
                            "ID;Nombre;Cedula;Correo;Contraseña; Usuario;Rol;Estado;Permisos;UsuarioModificacion;FechaModificacio");
                } else if (rutaArchivo.equals(PEDIDOS_CSV)) {
                    pw.println(
                            "ID;ClienteID;FechaPedido;FechaEnvio;Estado;GuiaAerea;PrecioTotal;EmpresaTransporte;FechaCreacion;FechaModificacion;UsuarioResponsable;Items");
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

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea = br.readLine(); // Leer encabezado
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(";", -1);

                if (campos.length < 11)
                    continue; // Validar tamaño mínimo

                Venta venta = new Venta();

                venta.setId(campos[0]);
                venta.setTipoDestino(campos[1]);
                venta.setServicio(campos[2]);

                Cliente cliente = new Cliente();
                cliente.setNombre(campos[3].replace("\"", ""));
                cliente.setDireccion(campos[4].replace("\"", ""));
                cliente.setPais(campos[6].replace("\"", ""));
                venta.setCliente(cliente);

                venta.setFecha(LocalDate.parse(campos[5], formatter));
                venta.setPrecio(Double.parseDouble(campos[7].replace(",", ".")));
                venta.setIva(Double.parseDouble(campos[8].replace(",", ".")));
                venta.setTotal(Double.parseDouble(campos[9].replace(",", ".")));
                venta.setEstado(campos[10]);

                // Leer items (formato esperado: Variedad xCantidad - $Precio)
                if (campos.length > 11) {
                    venta.setUsuarioResponsable(campos[11]);

                    // Leer items (posibles desde campo[12] en adelante)
                    if (campos.length > 12) {
                        String itemsStr = campos[12].replace("\"", "");
                        String[] itemsArr = itemsStr.split(", ");

                        for (String itemStr : itemsArr) {
                            String[] parts = itemStr.split(" - \\$");
                            if (parts.length == 2) {
                                String desc = parts[0]; // "Variedad xCantidad"
                                double precioTotal = Double.parseDouble(parts[1].replace(",", "."));

                                String[] subParts = desc.split(" x");
                                String variedad = subParts[0].trim();
                                int cantidad = subParts.length > 1 ? Integer.parseInt(subParts[1].trim()) : 1;

                                ItemVenta item = new ItemVenta();
                                item.setVariedad(variedad);
                                item.setCantidad(cantidad);
                                item.setPrecioTotal(precioTotal);

                                venta.addItem(item);
                            }
                        }
                    }
                }
                ventas.add(venta);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ventas;
    }

    public static void guardarVentas(List<Venta> ventas, String rutaArchivo) {
        try {
            crearArchivoSiNoExiste(rutaArchivo);

            try (PrintWriter pw = new PrintWriter(new FileWriter(rutaArchivo))) {
                pw.println("ID;TipoDestino;Servicio;Cliente;Direccion;Fecha;Pais;Precio;IVA;Total;Estado;Items");

                for (Venta venta : ventas) {
                    StringBuilder sb = new StringBuilder();

                    sb.append(String.format("%s;%s;%s;\"%s\";\"%s\";%s;\"%s\";%.2f;%.2f;%.2f;%s;%s;",
                            venta.getId(),
                            venta.getTipoDestino(),
                            venta.getServicio(),
                            venta.getCliente().getNombre(),
                            venta.getCliente().getDireccion(),
                            venta.getFecha().format(formatter),
                            venta.getCliente().getPais(),
                            venta.getPrecio(),
                            venta.getIva(),
                            venta.getTotal(),
                            venta.getEstado(),
                            venta.getUsuarioResponsable()));
                    List<String> itemsStr = new ArrayList<>();
                    for (ItemVenta item : venta.getItems()) {
                        itemsStr.add(String.format("%s x%d - $%.2f",
                                item.getVariedad(),
                                item.getCantidad(),
                                item.getPrecioTotal()));
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
        Path path = Paths.get(PEDIDOS_CSV);

        try {
            if (!Files.exists(path)) {
                Files.createFile(path);
                try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                    pw.println(
                            "ID;ClienteID;ClienteNombre;ClienteIdentificadorEmpresarial;ClienteDireccion;ClienteTelefono;FechaPedido;FechaEnvio;Estado;GuiaAerea;PrecioTotal;EmpresaTransporte;FechaCreacion;FechaModificacion;UsuarioResponsable;Items");
                }
                return pedidos;
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
                    if (campos.length < 16) {
                        System.err.println("Línea ignorada (columnas insuficientes): " + linea);
                        continue;
                    }

                    try {
                        int id = Integer.parseInt(campos[0].trim());
                        String clienteId = campos[1].trim();
                        String clienteNombre = campos[2].trim();
                        String identificador = campos[3].trim();
                        String direccion = campos[4].trim();
                        String telefono = campos[5].trim();
                        LocalDate fechaPedido = LocalDate.parse(campos[6].trim());
                        LocalDate fechaEnvio = LocalDate.parse(campos[7].trim());
                        String estado = campos[8].trim();
                        String guiaAerea = campos[9].trim();
                        double precioTotal = Double.parseDouble(campos[10].trim().replace(",", "."));
                        String empresaTransporte = campos[11].trim();
                        LocalDateTime fechaCreacion = LocalDateTime.parse(campos[12].trim(),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        LocalDateTime fechaModificacion = LocalDateTime.parse(campos[13].trim(),
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                        String usuarioResponsable = campos[14].trim();

                        // Items
                        List<ItemVenta> itemsVenta = new ArrayList<>();
                        String itemsStr = campos[15].trim();
                        if (!itemsStr.isEmpty()) {
                            for (String itemData : itemsStr.split("\\|")) {
                                String[] part = itemData.split("-");
                                if (part.length == 3) {
                                    ItemVenta item = new ItemVenta();
                                    item.setVariedad(part[0].trim());
                                    item.setCantidad(Integer.parseInt(part[1].trim()));
                                    item.setPrecioTotal(Double.parseDouble(part[2].trim().replace(",", ".")));
                                    itemsVenta.add(item);
                                }
                            }
                        }

                        Cliente cliente = new Cliente();
                        cliente.setId(clienteId);
                        cliente.setNombre(clienteNombre);
                        cliente.setIdentificadorEmpresarial(identificador);
                        cliente.setDireccion(direccion);
                        cliente.setTelefono(telefono);

                        Pedido pedido = new Pedido();
                        pedido.setId(id);
                        pedido.setCliente(cliente);
                        pedido.setFechaPedido(fechaPedido);
                        pedido.setFechaEstimadaEnvio(fechaEnvio);
                        pedido.setEstadoActual(estado);
                        pedido.setCodigoGuiaAerea(guiaAerea);
                        pedido.setPrecioTotal(precioTotal);
                        pedido.setEmpresaTransporte(empresaTransporte);
                        pedido.setFechaCreacion(fechaCreacion);
                        pedido.setFechaModificacion(fechaModificacion);
                        pedido.setUsuarioResponsable(usuarioResponsable);
                        pedido.setItemsVenta(itemsVenta);

                        pedidos.add(pedido);
                    } catch (Exception e) {
                        System.err.println("Error en línea: " + linea + " -> " + e.getMessage());
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

                // Nueva cabecera extendida
                bw.write(
                        "ID;ClienteID;ClienteNombre;ClienteIdentificadorEmpresarial;ClienteDireccion;ClienteTelefono;FechaPedido;FechaEnvio;Estado;GuiaAerea;PrecioTotal;EmpresaTransporte;FechaCreacion;FechaModificacion;UsuarioResponsable;Items\n");

                for (Pedido p : pedidos) {
                    Cliente c = p.getCliente();

                    String itemsStr = p.getItemsVenta().stream()
                            .map(i -> i.getVariedad() + "-" + i.getCantidad() + "-"
                                    + String.format(Locale.US, "%.2f", i.getPrecioTotal()))
                            .collect(Collectors.joining("|"));

                    bw.write(String.format(Locale.US,
                            "%d;%s;%s;%s;%s;%s;%s;%s;%s;%s;%.2f;%s;%s;%s;%s;%s\n",
                            p.getId(),
                            c.getId(),
                            c.getNombre(),
                            c.getIdentificadorEmpresarial(),
                            c.getDireccion(),
                            c.getTelefono(),
                            p.getFechaPedido(),
                            p.getFechaEstimadaEnvio(),
                            p.getEstadoActual(),
                            p.getCodigoGuiaAerea(),
                            p.getPrecioTotal(),
                            p.getEmpresaTransporte(),
                            p.getFechaCreacion().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            p.getFechaModificacion().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            p.getUsuarioResponsable(),
                            itemsStr));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Proveedor> leerProveedores() {
        List<Proveedor> proveedores = new ArrayList<>();
        try {
            Path path = Paths.get("data/proveedores.csv");
            if (!Files.exists(path)) {
                Files.createFile(path);
                try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                    pw.println(
                            "RUC;Nombre;Razón Social;Teléfono;Dirección;Correo;Estado;Fecha Registro;Fecha Modificación;Usuario Responsable");
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
                        Proveedor proveedor = new Proveedor();
                        proveedor.setRuc(campos[0].trim());
                        proveedor.setNombre(campos[1].trim());
                        proveedor.setRazon_social(campos[2].trim());
                        proveedor.setTelefono(campos[3].trim());
                        proveedor.setDireccion(campos[4].trim());
                        proveedor.setCorreo(campos[5].trim());
                        proveedor.setEstado(campos[6].trim());
                        proveedor.setFechaRegistro(LocalDate.parse(campos[7].trim()));
                        proveedor.setFechaModificacion(LocalDate.parse(campos[8].trim()));
                        proveedor.setUsuarioResponsable(campos[9].trim());

                        proveedores.add(proveedor);
                    } catch (Exception e) {
                        System.err.println("Error en línea: " + linea + ", motivo: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return proveedores;
    }

    public static void guardarProveedores(List<Proveedor> proveedores) {
        try {
            Path path = Paths.get("data/proveedores.csv");
            Files.createDirectories(path.getParent());
            try (BufferedWriter bw = Files.newBufferedWriter(path,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING)) {

                bw.write(
                        "RUC;Nombre;Razón Social;Teléfono;Dirección;Correo;Estado;Fecha Registro;Fecha Modificación;Usuario Responsable\n");

                for (Proveedor p : proveedores) {
                    bw.write(String.format("%s;%s;%s;%s;%s;%s;%s;%s;%s;%s\n",
                            p.getRuc(),
                            p.getNombre(),
                            p.getRazon_social(),
                            p.getTelefono(),
                            p.getDireccion(),
                            p.getCorreo(),
                            p.getEstado(),
                            p.getFechaRegistro().toString(),
                            p.getFechaModificacion().toString(),
                            p.getUsuarioResponsable()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}