package org.example.sgef_petalex_v_09.util;

import org.example.sgef_petalex_v_09.models.Usuario;
import java.io.*;
import java.nio.file.*;
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

    public static Usuario buscarUsuario(String correo, String usuario) {
        try {
            Path path = Paths.get("data/usuarios.csv");
            if (!Files.exists(path))
                return null;

            List<String> lines = Files.readAllLines(path);
            for (int i = 1; i < lines.size(); i++) { // Saltar encabezado
                String[] parts = lines.get(i).split(";", -1);
                if (parts.length >= 10) {
                    String correoCsv = parts[3].trim();
                    String usuarioCsv = parts[4].trim();
                    String estadoCsv = parts[6].trim();

                    if (correoCsv.equalsIgnoreCase(correo) && usuarioCsv.equals(usuario)) {
                        Usuario u = new Usuario();
                        u.setId(parts[0]);
                        u.setNombre(parts[1]);
                        u.setCedula(parts[2]);
                        u.setCorreo(correoCsv);
                        u.setUsuario(usuarioCsv);
                        u.setRol(parts[5]);
                        u.setEstado(estadoCsv);
                        u.setPermisos(parts[7]);
                        u.setUsuarioModificacion(parts[8]);
                        return u;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}