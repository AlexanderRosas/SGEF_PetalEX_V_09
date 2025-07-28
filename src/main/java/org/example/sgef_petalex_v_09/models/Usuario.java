package org.example.sgef_petalex_v_09.models;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.ReadOnlyStringWrapper;

import java.time.LocalDateTime;

public class Usuario {
    private final StringProperty nombre = new SimpleStringProperty();
    private final StringProperty correo = new SimpleStringProperty();
    private final StringProperty usuario = new SimpleStringProperty();
    private final StringProperty rol = new SimpleStringProperty();
    private final StringProperty estado = new SimpleStringProperty();
    private final StringProperty sucursal = new SimpleStringProperty();
    private final StringProperty ruc = new SimpleStringProperty();
    private final StringProperty permisos = new SimpleStringProperty("");
    private final StringProperty password = new SimpleStringProperty(""); // Si usas password
    
    private final StringProperty usuarioModificacion = new SimpleStringProperty();  // NUEVO campo
    private ObjectProperty<LocalDateTime> fechaModificacion = new SimpleObjectProperty<>(); // NUEVO campo

    private String id;

    public Usuario() {}

    public Usuario(String nombre, String correo, String usuario, String rol, String estado, String sucursal,
                   String ruc, String password, String usuarioModificacion, LocalDateTime fechaModificacion) {
        setNombre(nombre);
        setCorreo(correo);
        setUsuario(usuario);
        setRol(rol);
        setEstado(estado);
        setSucursal(sucursal);
        setRuc(ruc);
        setPassword(password);
        setUsuarioModificacion(usuarioModificacion);
        setFechaModificacion(fechaModificacion);
    }

    // Getters y setters con properties (existentes)
    public String getNombre() { return nombre.get(); }
    public void setNombre(String value) { nombre.set(value); }
    public StringProperty nombreProperty() { return nombre; }

    public String getCorreo() { return correo.get(); }
    public void setCorreo(String value) { correo.set(value); }
    public StringProperty correoProperty() { return correo; }

    public String getUsuario() { return usuario.get(); }
    public void setUsuario(String value) { usuario.set(value); }
    public StringProperty usuarioProperty() { return usuario; }

    public String getRol() { return rol.get(); }
    public void setRol(String value) { rol.set(value); }
    public StringProperty rolProperty() { return rol; }

    public String getEstado() { return estado.get(); }
    public void setEstado(String value) { estado.set(value); }
    public StringProperty estadoProperty() { return estado; }

    public String getSucursal() { return sucursal.get(); }
    public void setSucursal(String value) { sucursal.set(value); }
    public StringProperty sucursalProperty() { return sucursal; }

    public String getRuc() { return ruc.get(); }
    public void setRuc(String value) { ruc.set(value); }
    public StringProperty rucProperty() { return ruc; }

    public String getPermisos() { return permisos.get(); }
    public void setPermisos(String value) { permisos.set(value); }
    public StringProperty permisosProperty() { return permisos; }

    public String getPassword() { return password.get(); }
    public void setPassword(String value) { password.set(value); }
    public StringProperty passwordProperty() { return password; }

    // NUEVOS getters y setters para usuarioModificacion y fechaModificacion

    public String getUsuarioModificacion() { return usuarioModificacion.get(); }
    public void setUsuarioModificacion(String value) { usuarioModificacion.set(value); }
    public StringProperty usuarioModificacionProperty() { return usuarioModificacion; }

    public LocalDateTime getFechaModificacion() { return fechaModificacion.get(); }
    public void setFechaModificacion(LocalDateTime value) { fechaModificacion.set(value); }
    public ObjectProperty<LocalDateTime> fechaModificacionProperty() { return fechaModificacion; }

    // Id simple sin property
    public String getId() { return id; }
    public void setId(String value) { this.id = value; }

    public ObservableValue<String> idProperty() {
        return new ReadOnlyStringWrapper(getId());
    }
}
