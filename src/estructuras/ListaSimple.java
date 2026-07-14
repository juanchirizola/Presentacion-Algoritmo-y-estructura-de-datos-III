package estructuras;

import modelos.Libro;
import modelos.Usuario;

/**
 * ============================================================
 *  ESTRUCTURA: ListaSimple
 * ============================================================
 *  Lista simplemente enlazada que sirve como CATÁLOGO.
 *  No usa arrays ni ArrayList de Java: el recorrido,
 *  la inserción y la eliminación se hacen moviendo punteros.
 *
 *  Se usa para almacenar tanto LIBROS como USUARIOS.
 *  Para eso tiene dos listas internas:
 *    - cabezaLibros   → primer NodoLibro de la cadena de libros
 *    - cabezaUsuarios → primer NodoUsuario de la cadena de usuarios
 *
 *  Visualización de la lista de libros:
 *
 *   cabezaLibros
 *       │
 *       ▼
 *   ┌──────────┐    ┌──────────┐    ┌──────────┐
 *   │ Libro #1 │───▶│ Libro #2 │───▶│ Libro #3 │───▶ null
 *   └──────────┘    └──────────┘    └──────────┘
 *
 *  Complejidad de las operaciones:
 *    agregar(...)    O(n)  → recorre hasta el final
 *    buscar(...)     O(n)  → recorre nodo por nodo
 *    eliminar(...)   O(n)  → reconecta punteros
 *    obtenerTodos()  O(n)
 *
 *  La eliminación es la operación más delicada:
 *    Antes:          [A] → [B] → [C] → null
 *    Eliminar B:     [A] ──────→ [C] → null
 *    (A.siguiente apunta directamente a C, saltando B)
 * ============================================================
 */
public class ListaSimple {

    // ─────────────────────────────────────────────────────────
    //  INNER CLASS: NodoLibro (solo lo usa ListaSimple)
    // ─────────────────────────────────────────────────────────
    /**
     * Nodo que envuelve un Libro.
     * Análogo a NodoUsuario pero para objetos Libro.
     */
    public static class NodoLibro {
        public Libro     dato;
        public NodoLibro siguiente;

        public NodoLibro(Libro libro) {
            this.dato      = libro;
            this.siguiente = null;
        }
    }

    // ── cabeceras de cada lista ────────────────────────────
    private NodoLibro   cabezaLibros;     // primer nodo de libros
    private NodoUsuario cabezaUsuarios;   // primer nodo de usuarios

    // ── contadores e IDs ───────────────────────────────────
    private int tamanoLibros;
    private int tamanoUsuarios;
    private int siguienteIdLibro;
    private int siguienteIdUsuario;

    // ── constructor ────────────────────────────────────────
    public ListaSimple() {
        cabezaLibros    = null;
        cabezaUsuarios  = null;
        tamanoLibros    = 0;
        tamanoUsuarios  = 0;
        siguienteIdLibro   = 1;
        siguienteIdUsuario = 1;
    }

    // ══════════════════════════════════════════════════════════
    //  OPERACIONES CON LIBROS
    // ══════════════════════════════════════════════════════════

    /**
     * Agrega un libro al FINAL de la lista.
     * Recorre la cadena hasta llegar al último nodo
     * y lo enlaza con el nuevo.
     * Evita títulos duplicados.
     */
    public String agregarLibro(String titulo, String autor, String genero, int anio) {
        titulo = titulo == null ? "" : titulo.trim();
        autor  = autor  == null ? "" : autor.trim();
        genero = genero == null ? "" : genero.trim();

        if (titulo.isEmpty() || autor.isEmpty())
            return "ERROR: Título y autor no pueden estar vacíos.";

        if (buscarLibroPorTituloExacto(titulo) != null)
            return "ERROR: Ya existe un libro con el título \"" + titulo + "\".";

        Libro nuevoLibro = new Libro(siguienteIdLibro++, titulo, autor, genero, anio);
        NodoLibro nuevoNodo = new NodoLibro(nuevoLibro);

        if (cabezaLibros == null) {
            // lista vacía: el nuevo nodo es la cabeza
            cabezaLibros = nuevoNodo;
        } else {
            // recorremos hasta el último nodo
            NodoLibro actual = cabezaLibros;
            while (actual.siguiente != null)
                actual = actual.siguiente;
            actual.siguiente = nuevoNodo;   // enlazamos
        }

        tamanoLibros++;
        return "OK: Libro \"" + titulo + "\" agregado correctamente.";
    }

    /**
     * Elimina el libro cuyo ID coincide.
     * Necesitamos recordar el nodo ANTERIOR para
     * "saltar" el nodo a eliminar.
     */
    public String eliminarLibro(int id) {
        NodoLibro anterior = null;
        NodoLibro actual   = cabezaLibros;

        while (actual != null) {
            if (actual.dato.getId() == id) {
                if (anterior == null)
                    cabezaLibros = actual.siguiente;   // era la cabeza
                else
                    anterior.siguiente = actual.siguiente; // salto el nodo
                tamanoLibros--;
                return "OK: Libro [" + id + "] eliminado.";
            }
            anterior = actual;
            actual   = actual.siguiente;
        }
        return "ERROR: No se encontró ningún libro con ID " + id + ".";
    }

    /** Busca por título exacto (sin distinción de mayúsculas). */
    public Libro buscarLibroPorTituloExacto(String titulo) {
        String t = titulo == null ? "" : titulo.toLowerCase().trim();
        NodoLibro actual = cabezaLibros;
        while (actual != null) {
            if (actual.dato.getTitulo().toLowerCase().equals(t))
                return actual.dato;
            actual = actual.siguiente;
        }
        return null;
    }

    /** Busca por ID. */
    public Libro buscarLibroPorId(int id) {
        NodoLibro actual = cabezaLibros;
        while (actual != null) {
            if (actual.dato.getId() == id) return actual.dato;
            actual = actual.siguiente;
        }
        return null;
    }

    /** Búsqueda parcial: coincidencia en título o autor. */
    public Libro[] buscarLibros(String termino) {
        String t = termino == null ? "" : termino.toLowerCase().trim();
        // primera pasada: contamos
        int count = 0;
        NodoLibro actual = cabezaLibros;
        while (actual != null) {
            if (actual.dato.getTitulo().toLowerCase().contains(t)
             || actual.dato.getAutor().toLowerCase().contains(t))
                count++;
            actual = actual.siguiente;
        }
        // segunda pasada: llenamos array
        Libro[] resultado = new Libro[count];
        int i = 0;
        actual = cabezaLibros;
        while (actual != null) {
            if (actual.dato.getTitulo().toLowerCase().contains(t)
             || actual.dato.getAutor().toLowerCase().contains(t))
                resultado[i++] = actual.dato;
            actual = actual.siguiente;
        }
        return resultado;
    }

    /** Devuelve todos los libros en orden de inserción. */
    public Libro[] obtenerTodosLosLibros() {
        Libro[] arr = new Libro[tamanoLibros];
        NodoLibro actual = cabezaLibros;
        int i = 0;
        while (actual != null) {
            arr[i++] = actual.dato;
            actual = actual.siguiente;
        }
        return arr;
    }

    public int contarLibros()          { return tamanoLibros; }
    public int getSiguienteIdLibro()   { return siguienteIdLibro; }
    public void setSiguienteIdLibro(int id) { this.siguienteIdLibro = id; }

    /** Agrega un libro ya construido (se usa al cargar desde JSON). */
    public void agregarLibroDirecto(Libro libro) {
        NodoLibro nuevoNodo = new NodoLibro(libro);
        if (cabezaLibros == null) {
            cabezaLibros = nuevoNodo;
        } else {
            NodoLibro actual = cabezaLibros;
            while (actual.siguiente != null) actual = actual.siguiente;
            actual.siguiente = nuevoNodo;
        }
        tamanoLibros++;
        if (libro.getId() >= siguienteIdLibro)
            siguienteIdLibro = libro.getId() + 1;
    }

    // ══════════════════════════════════════════════════════════
    //  OPERACIONES CON USUARIOS
    // ══════════════════════════════════════════════════════════

    public String agregarUsuario(String nombre, String dni, String email, String telefono) {
        nombre   = nombre   == null ? "" : nombre.trim();
        dni      = dni      == null ? "" : dni.trim();
        email    = email    == null ? "" : email.trim();
        telefono = telefono == null ? "" : telefono.trim();

        if (nombre.isEmpty() || dni.isEmpty())
            return "ERROR: Nombre y DNI no pueden estar vacíos.";

        if (buscarUsuarioPorDni(dni) != null)
            return "ERROR: Ya existe un usuario con DNI " + dni + ".";

        Usuario nuevoUsuario = new Usuario(siguienteIdUsuario++, nombre, dni, email, telefono);
        NodoUsuario nuevoNodo = new NodoUsuario(nuevoUsuario);

        if (cabezaUsuarios == null) {
            cabezaUsuarios = nuevoNodo;
        } else {
            NodoUsuario actual = cabezaUsuarios;
            while (actual.siguiente != null) actual = actual.siguiente;
            actual.siguiente = nuevoNodo;
        }

        tamanoUsuarios++;
        return "OK: Usuario \"" + nombre + "\" registrado correctamente.";
    }

    public String eliminarUsuario(int id) {
        NodoUsuario anterior = null;
        NodoUsuario actual   = cabezaUsuarios;

        while (actual != null) {
            if (actual.dato.getId() == id) {
                if (anterior == null)
                    cabezaUsuarios = actual.siguiente;
                else
                    anterior.siguiente = actual.siguiente;
                tamanoUsuarios--;
                return "OK: Usuario [" + id + "] eliminado.";
            }
            anterior = actual;
            actual   = actual.siguiente;
        }
        return "ERROR: No se encontró ningún usuario con ID " + id + ".";
    }

    public Usuario buscarUsuarioPorId(int id) {
        NodoUsuario actual = cabezaUsuarios;
        while (actual != null) {
            if (actual.dato.getId() == id) return actual.dato;
            actual = actual.siguiente;
        }
        return null;
    }

    public Usuario buscarUsuarioPorDni(String dni) {
        String d = dni == null ? "" : dni.trim();
        NodoUsuario actual = cabezaUsuarios;
        while (actual != null) {
            if (actual.dato.getDni().equals(d)) return actual.dato;
            actual = actual.siguiente;
        }
        return null;
    }

    public Usuario[] obtenerTodosLosUsuarios() {
        Usuario[] arr = new Usuario[tamanoUsuarios];
        NodoUsuario actual = cabezaUsuarios;
        int i = 0;
        while (actual != null) {
            arr[i++] = actual.dato;
            actual = actual.siguiente;
        }
        return arr;
    }

    public int contarUsuarios()              { return tamanoUsuarios; }
    public int getSiguienteIdUsuario()       { return siguienteIdUsuario; }
    public void setSiguienteIdUsuario(int id){ this.siguienteIdUsuario = id; }

    /** Agrega un usuario ya construido (para cargar desde JSON). */
    public void agregarUsuarioDirecto(Usuario usuario) {
        NodoUsuario nuevoNodo = new NodoUsuario(usuario);
        if (cabezaUsuarios == null) {
            cabezaUsuarios = nuevoNodo;
        } else {
            NodoUsuario actual = cabezaUsuarios;
            while (actual.siguiente != null) actual = actual.siguiente;
            actual.siguiente = nuevoNodo;
        }
        tamanoUsuarios++;
        if (usuario.getId() >= siguienteIdUsuario)
            siguienteIdUsuario = usuario.getId() + 1;
    }
}
