package modelos;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 *  MODELO: Prestamo
 * ============================================================
 *  Objeto central del sistema: representa el PRÉSTAMO de un
 *  libro a un usuario.
 *
 *  Un Prestamo es la COMBINACIÓN de:
 *    - Usuario  → quién lleva el libro
 *    - Libro    → qué libro se lleva
 *
 *  Además registra las FECHAS:
 *    - idUsuario      → ID del usuario (referencia)
 *    - idLibro        → ID del libro (referencia)
 *    - fechaPrestamo  → día en que se registró el préstamo
 *    - fechaDevolucion→ día en que el usuario devolvió el libro
 *                       (null si todavía no fue devuelto)
 *    - devuelto       → true cuando el libro ya fue devuelto
 *
 *  Los préstamos se almacenan en la Pila (Stack / LIFO),
 *  por lo que el último préstamo es el primero en mostrarse.
 *
 *  Diagrama de relaciones:
 *
 *      Usuario ──┐
 *                ├──→  Prestamo  ──→  (apilado en Pila)
 *      Libro   ──┘
 *
 * ============================================================
 */
public class Prestamo {

    // ── formato de fecha ───────────────────────────────────
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── atributos ──────────────────────────────────────────
    private int       id;
    private int       idUsuario;
    private int       idLibro;
    private String    nombreUsuario;   // desnormalizado para mostrar sin buscar
    private String    tituloLibro;     // desnormalizado para mostrar sin buscar
    private String    fechaPrestamo;   // formato dd/MM/yyyy
    private String    fechaDevolucion; // null o vacío si aún no se devolvió
    private boolean   devuelto;

    // ── constructor: se usa al REGISTRAR un préstamo nuevo ─
    public Prestamo(int id, Usuario usuario, Libro libro) {
        this.id              = id;
        this.idUsuario       = usuario.getId();
        this.idLibro         = libro.getId();
        this.nombreUsuario   = usuario.getNombre();
        this.tituloLibro     = libro.getTitulo();
        this.fechaPrestamo   = LocalDate.now().format(FMT);
        this.fechaDevolucion = "";       // vacío = aún no devuelto
        this.devuelto        = false;
    }

    // ── constructor privado para fromJson ──────────────────
    private Prestamo() {}

    // ── acción: registrar devolución ───────────────────────
    /**
     * Marca el préstamo como devuelto y registra la fecha actual
     * como fecha de devolución.
     */
    public void registrarDevolucion() {
        this.devuelto        = true;
        this.fechaDevolucion = LocalDate.now().format(FMT);
    }

    // ── getters ────────────────────────────────────────────
    public int     getId()              { return id;              }
    public int     getIdUsuario()       { return idUsuario;       }
    public int     getIdLibro()         { return idLibro;         }
    public String  getNombreUsuario()   { return nombreUsuario;   }
    public String  getTituloLibro()     { return tituloLibro;     }
    public String  getFechaPrestamo()   { return fechaPrestamo;   }
    public String  getFechaDevolucion() { return fechaDevolucion; }
    public boolean isDevuelto()         { return devuelto;        }

    // ── serialización JSON manual ──────────────────────────
    public String toJson() {
        return "{"
            + "\"id\":"                + id                          + ","
            + "\"idUsuario\":"         + idUsuario                   + ","
            + "\"idLibro\":"           + idLibro                     + ","
            + "\"nombreUsuario\":\""   + escapar(nombreUsuario)      + "\","
            + "\"tituloLibro\":\""     + escapar(tituloLibro)        + "\","
            + "\"fechaPrestamo\":\""   + escapar(fechaPrestamo)      + "\","
            + "\"fechaDevolucion\":\"" + escapar(fechaDevolucion)    + "\","
            + "\"devuelto\":"          + devuelto
            + "}";
    }

    public static Prestamo fromJson(String json) {
        Prestamo p = new Prestamo();
        p.id              = Integer.parseInt(Libro.extraerValor(json, "id"));
        p.idUsuario       = Integer.parseInt(Libro.extraerValor(json, "idUsuario"));
        p.idLibro         = Integer.parseInt(Libro.extraerValor(json, "idLibro"));
        p.nombreUsuario   = Libro.extraerValor(json, "nombreUsuario");
        p.tituloLibro     = Libro.extraerValor(json, "tituloLibro");
        p.fechaPrestamo   = Libro.extraerValor(json, "fechaPrestamo");
        p.fechaDevolucion = Libro.extraerValor(json, "fechaDevolucion");
        p.devuelto        = Boolean.parseBoolean(Libro.extraerValor(json, "devuelto"));
        return p;
    }

    // ── utilidades privadas ────────────────────────────────
    private static String escapar(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    // ── toString ───────────────────────────────────────────
    @Override
    public String toString() {
        if (devuelto) {
            return String.format(
                "[%03d] \"%s\" → %s | Prestado: %s | Devuelto: %s  ✔",
                id, tituloLibro, nombreUsuario, fechaPrestamo, fechaDevolucion);
        } else {
            return String.format(
                "[%03d] \"%s\" → %s | Prestado: %s | PENDIENTE",
                id, tituloLibro, nombreUsuario, fechaPrestamo);
        }
    }
}
