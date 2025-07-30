package org.example.sgef_petalex_v_09.util;

import org.example.sgef_petalex_v_09.models.Usuario;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserUtil {
    public static final String USERS_CSV = "data/user.csv";

    private static void crearArchivoSiNoExiste() throws IOException {
        Path path = Paths.get(USERS_CSV);
        if (!Files.exists(path)) {
            Files.createFile(path);
            try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_CSV))) {
                pw.println("ID,Nombre,Correo,Usuario,Rol,Estado,Sucursal,RUC");
                // Agregar usuario admin por defecto
                pw.println("U001,Administrador,admin@example.com,admin,Administrador,Activo,Principal,0123456789001");
            }
        }
    }

    public static List<Usuario> leerUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();

        try {
            crearArchivoSiNoExiste();

            try (BufferedReader br = new BufferedReader(new FileReader(USERS_CSV))) {
                String linea;
                boolean primeraLinea = true;

                while ((linea = br.readLine()) != null) {
                    if (primeraLinea) {
                        primeraLinea = false;
                        continue;
                    }
                    if (linea.trim().isEmpty())
                        continue;

                    String[] campos = linea.split(",");
                    if (campos.length >= 8) {
                        Usuario usuario = new Usuario();
                        usuario.setId(campos[0].trim());
                        usuario.setNombre(campos[1].trim());
                        usuario.setCorreo(campos[2].trim());
                        usuario.setUsuario(campos[3].trim());
                        usuario.setRol(campos[4].trim());
                        usuario.setEstado(campos[5].trim());
                        usuario.setSucursal(campos[6].trim());
                        usuario.setRuc(campos[7].trim());
                        usuarios.add(usuario);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error con archivo de usuarios: " + e.getMessage());
            try {
                crearArchivoSiNoExiste();
            } catch (IOException ex) {
                System.err.println("No se pudo crear el archivo de usuarios: " + ex.getMessage());
            }
        }
        return usuarios;
    }

    public static void guardarUsuarios(List<Usuario> usuarios) {

        try {
            crearArchivoSiNoExiste();

            try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_CSV))) {
                pw.println("ID,Nombre,Correo,Usuario,Rol,Estado,Sucursal,RUC");

                for (Usuario u : usuarios) {
                    pw.printf("%s,%s,%s,%s,%s,%s,%s,%s%n",
                            u.getId(),
                            u.getNombre(),
                            u.getCorreo(),
                            u.getUsuario(),
                            u.getRol(),
                            u.getEstado(),
                            u.getSucursal(),
                            u.getRuc());
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }

    public static Usuario buscarUsuario(String correo, String contrasena) {
        correo = correo.trim();
        contrasena = contrasena.trim();

        Path path = Paths.get("data/usuarios.csv");
        if (!Files.exists(path)) {
            System.err.println("Archivo usuarios.csv no encontrado.");
            return null;
        }

        System.out.println("Buscando usuario con correo: '" + correo + "' y contraseña: '" + contrasena + "'");

        try {
            List<String> lines = Files.readAllLines(path);
            for (int i = 1; i < lines.size(); i++) { // Saltar encabezado
                String line = lines.get(i);
                String[] parts = line.split(";", -1);

                if (parts.length < 11) {
                    System.err.println("Línea " + (i + 1) + " incompleta en CSV: " + line);
                    continue;
                }

                String correoCsv = parts[3].trim();
                String contrasenaCsv = parts[4].trim();
                String estadoCsv = parts[7].trim();

                // Mostrar para debug línea y datos de usuario
                System.out.println(
                        "Leyendo línea " + (i + 1) + ": correo=" + correoCsv + ", contraseña=" + contrasenaCsv);

                if (correoCsv.equalsIgnoreCase(correo) && contrasenaCsv.equals(contrasena)) {
                    try {
                        Usuario u = new Usuario();
                        u.setId(parts[0]);
                        u.setNombre(parts[1]);
                        u.setCedula(parts[2]);
                        u.setCorreo(correoCsv);
                        u.setUsuario(parts[5]);
                        u.setRol(parts[6]);
                        u.setEstado(estadoCsv);
                        u.setPermisos(parts[8]);
                        u.setUsuarioModificacion(parts[9]);
                        u.setFechaModificacion(LocalDateTime.parse(parts[10], DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                        System.out.println("Usuario encontrado: " + u.getNombre());
                        return u;
                    } catch (Exception e) {
                        System.err.println("Error al parsear datos del usuario en línea " + (i + 1));
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error leyendo archivo usuarios.csv");
            e.printStackTrace();
        }
        System.out.println("No se encontró usuario con ese correo y contraseña.");
        return null;
    }

}