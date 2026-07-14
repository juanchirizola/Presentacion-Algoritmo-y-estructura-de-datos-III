package modelos;

/**
 * ============================================================
 *  MODELO: Libro
 * ============================================================
 *  Representa un libro del catálogo de la biblioteca.
 *
 *  Atributos:
 *    - id       → identificador único (autoincremental)
 *    - titulo   → título del libro
 *    - autor    → autor del libro
 *    - genero   → género literario
 *    - anio     → año de publicación
 *    - disponible → true = en estantería, false = prestado
 *
 *  Este objeto se almacena dentro de un NodoLibro
 *  en la ListaSimple (catálogo).
 * ============================================================
 */
public class Libro {

    // ── atributos ──────────────────────────────────────────
    private int    id;
    private String titulo;
    private String autor;
    private String genero;
    private int    anio;
    private boolean disponible;

    // ── constructor ────────────────────────────────────────
    public Libro(int id, String titulo, String autor, String genero, int anio) {
        this.id         = id;
        this.titulo     = titulo;
        this.autor      = autor;
        this.genero     = genero;
        this.anio       = anio;
        this.disponible = true;   // todo libro empieza disponible
    }

    // ── getters y setters ──────────────────────────────────
    public int     getId()          { return id; }
    public String  getTitulo()      { return titulo; }
    public String  getAutor()       { return autor; }
    public String  getGenero()      { return genero; }
    public int     getAnio()        { return anio; }
    public boolean isDisponible()   { return disponible; }

    public void setTitulo(String titulo)        { this.titulo = titulo; }
    public void setAutor(String autor)          { this.autor  = autor;  }
    public void setGenero(String genero)        { this.genero = genero; }
    public void setAnio(int anio)               { this.anio   = anio;   }
    public void setDisponible(boolean disponible){ this.disponible = disponible; }

    // ── serialización JSON manual ──────────────────────────
    /**
     * Convierte el objeto a una cadena JSON.
     * No usamos librerías externas: construimos el JSON
     * campo por campo con un StringBuilder.
     */
    public String toJson() {
        return "{"
            + "\"id\":"         + id                          + ","
            + "\"titulo\":\""   + escapar(titulo)             + "\","
            + "\"autor\":\""    + escapar(autor)              + "\","
            + "\"genero\":\""   + escapar(genero)             + "\","
            + "\"anio\":"       + anio                        + ","
            + "\"disponible\":" + disponible
            + "}";
    }

    /**
     * Crea un Libro a partir de un fragmento JSON simple.
     * (Parser básico: busca cada clave y extrae su valor.)
     */
    public static Libro fromJson(String json) {
        int    id         = Integer.parseInt(extraerValor(json, "id"));
        String titulo     = extraerValor(json, "titulo");
        String autor      = extraerValor(json, "autor");
        String genero     = extraerValor(json, "genero");
        int    anio       = Integer.parseInt(extraerValor(json, "anio"));
        boolean disponible= Boolean.parseBoolean(extraerValor(json, "disponible"));

        Libro l = new Libro(id, titulo, autor, genero, anio);
        l.setDisponible(disponible);
        return l;
    }

    // ── utilidades privadas ────────────────────────────────
    /** Escapa comillas dentro de un string para no romper el JSON. */
    private static String escapar(String s) {
        return s == null ? "" : s.replace("\"", "\\\"");
    }

    /**
     * Extrae el valor de una clave en un JSON plano.
     * Busca "clave": y devuelve lo que viene después
     * (string con comillas o número/booleano sin comillas).
     */
    static String extraerValor(String json, String clave) {
        String patron = "\"" + clave + "\":";
        int idx = json.indexOf(patron);
        if (idx == -1) return "";
        int inicio = idx + patron.length();
        char c = json.charAt(inicio);
        if (c == '"') {
            // valor string: buscamos el cierre de comilla
            int fin = json.indexOf('"', inicio + 1);
            return json.substring(inicio + 1, fin);
        } else {
            // valor numérico o booleano: buscamos coma o }
            int fin = inicio;
            while (fin < json.length() && json.charAt(fin) != ',' && json.charAt(fin) != '}')
                fin++;
            return json.substring(inicio, fin).trim();
        }
    }

    // ── toString ───────────────────────────────────────────
    @Override
    public String toString() {
        String estado = disponible ? "Disponible" : "Prestado";
        return String.format("[%02d] \"%s\" — %s  (%s, %d)  [%s]",
            id, titulo, autor, genero, anio, estado);
    }
}
