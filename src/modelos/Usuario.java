package modelos;

/**
 * ============================================================
 *  MODELO: Usuario
 * ============================================================
 *  Representa un usuario registrado en la biblioteca.
 *
 *  Atributos:
 *    - id       → identificador único
 *    - nombre   → nombre completo
 *    - dni      → documento de identidad
 *    - email    → correo electrónico
 *    - telefono → número de contacto
 *    - activo   → true = habilitado para pedir libros
 *
 *  Un usuario puede tener préstamos asociados (ver Prestamo.java).
 *  Los usuarios se almacenan en la ListaSimple de usuarios
 *  mediante NodoUsuario.
 * ============================================================
 */
public class Usuario {

    // ── atributos ──────────────────────────────────────────
    private int    id;
    private String nombre;
    private String dni;
    private String email;
    private String telefono;
    private boolean activo;

    // ── constructor ────────────────────────────────────────
    public Usuario(int id, String nombre, String dni, String email, String telefono) {
        this.id       = id;
        this.nombre   = nombre;
        this.dni      = dni;
        this.email    = email;
        this.telefono = telefono;
        this.activo   = true;    // todo usuario comienza activo
    }

    // ── getters y setters ──────────────────────────────────
    public int     getId()        { return id;       }
    public String  getNombre()    { return nombre;   }
    public String  getDni()       { return dni;      }
    public String  getEmail()     { return email;    }
    public String  getTelefono()  { return telefono; }
    public boolean isActivo()     { return activo;   }

    public void setNombre(String nombre)     { this.nombre   = nombre;   }
    public void setDni(String dni)           { this.dni      = dni;      }
    public void setEmail(String email)       { this.email    = email;    }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setActivo(boolean activo)    { this.activo   = activo;   }

    // ── serialización JSON manual ──────────────────────────
    public String toJson() {
        return "{"
            + "\"id\":"         + id                    + ","
            + "\"nombre\":\""   + escapar(nombre)       + "\","
            + "\"dni\":\""      + escapar(dni)          + "\","
            + "\"email\":\""    + escapar(email)        + "\","
            + "\"telefono\":\"" + escapar(telefono)     + "\","
            + "\"activo\":"     + activo
            + "}";
    }

    public static Usuario fromJson(String json) {
        int    id       = Integer.parseInt(Libro.extraerValor(json, "id"));
        String nombre   = Libro.extraerValor(json, "nombre");
        String dni      = Libro.extraerValor(json, "dni");
        String email    = Libro.extraerValor(json, "email");
        String telefono = Libro.extraerValor(json, "telefono");
        boolean activo  = Boolean.parseBoolean(Libro.extraerValor(json, "activo"));

        Usuario u = new Usuario(id, nombre, dni, email, telefono);
        u.setActivo(activo);
        return u;
    }

    // ── utilidades privadas ────────────────────────────────
    private static String escapar(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    // ── toString ───────────────────────────────────────────
    @Override
    public String toString() {
        String estado = activo ? "Activo" : "Inactivo";
        return String.format("[%02d] %s | DNI: %s | %s | Tel: %s [%s]",
            id, nombre, dni, email, telefono, estado);
    }
}
