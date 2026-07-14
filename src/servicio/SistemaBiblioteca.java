package servicio;

import modelos.Libro;
import modelos.Usuario;
import modelos.Prestamo;
import estructuras.ListaSimple;
import estructuras.Pila;
import datos.DatosJSON;

/**
 * ============================================================
 *  SERVICIO: SistemaBiblioteca
 * ============================================================
 *  Coordina las tres estructuras de datos y aplica las
 *  REGLAS DE NEGOCIO del sistema.
 *
 *  Contiene:
 *    - catalogo  → ListaSimple con Libros y Usuarios
 *    - historial → Pila con Préstamos (LIFO)
 *
 *  Responsabilidades:
 *    ✔ CRUD de Libros   (agregar, listar, buscar, editar, eliminar)
 *    ✔ CRUD de Usuarios (agregar, listar, buscar, editar, eliminar)
 *    ✔ Préstamos        (registrar, devolver)
 *    ✔ Validaciones de negocio
 *    ✔ Persistencia (llama a DatosJSON para guardar/cargar)
 *
 *  Esta capa NO imprime nada en pantalla: solo retorna
 *  Strings con mensajes OK/ERROR que la presentación muestra.
 * ============================================================
 */
public class SistemaBiblioteca {

    // ── estructuras ────────────────────────────────────────
    private final ListaSimple catalogo;
    private final Pila        historial;

    // ── datos precargados ──────────────────────────────────
    private static final Object[][] LIBROS_INICIALES = {
        {"Don Quijote de la Mancha",           "Miguel de Cervantes",        "Clásico",   1605},
        {"Cien años de soledad",               "Gabriel García Márquez",     "Realismo",  1967},
        {"El Principito",                      "Antoine de Saint-Exupéry",   "Fábula",    1943},
        {"1984",                               "George Orwell",              "Distopía",  1949},
        {"Crónica de una muerte anunciada",    "Gabriel García Márquez",     "Novela",    1981},
        {"La Odisea",                          "Homero",                     "Épica",     -800},
        {"Orgullo y Prejuicio",                "Jane Austen",                "Romance",   1813},
        {"Harry Potter y la Piedra Filosofal", "J. K. Rowling",             "Fantasía",  1997},
        {"El Señor de los Anillos",            "J. R. R. Tolkien",          "Fantasía",  1954},
        {"Los Juegos del Hambre",              "Suzanne Collins",            "Aventura",  2008},
    };

    private static final Object[][] USUARIOS_INICIALES = {
        {"Ana García",     "28456123", "ana@mail.com",    "351-1234"},
        {"Carlos López",   "31987654", "carlos@mail.com", "351-5678"},
        {"María Rodríguez","25678901", "maria@mail.com",  "351-9012"},
    };

    // ── constructor ────────────────────────────────────────
    public SistemaBiblioteca() {
        catalogo  = new ListaSimple();
        historial = new Pila();
        cargarDatos();
    }

    // ── inicialización: carga datos o siembra iniciales ───
    private void cargarDatos() {
        DatosJSON.cargarLibros(catalogo);
        DatosJSON.cargarUsuarios(catalogo);
        DatosJSON.cargarPrestamos(historial);

        // si no hay datos guardados, cargamos los ejemplos
        if (catalogo.contarLibros() == 0) {
            for (Object[] l : LIBROS_INICIALES)
                catalogo.agregarLibro((String)l[0], (String)l[1], (String)l[2], (int)l[3]);
            guardarTodo();
        }
        if (catalogo.contarUsuarios() == 0) {
            for (Object[] u : USUARIOS_INICIALES)
                catalogo.agregarUsuario((String)u[0], (String)u[1], (String)u[2], (String)u[3]);
            guardarTodo();
        }
    }

    // ── persistencia ──────────────────────────────────────
    public void guardarTodo() {
        DatosJSON.guardarLibros(catalogo);
        DatosJSON.guardarUsuarios(catalogo);
        DatosJSON.guardarPrestamos(historial);
    }

    // ══════════════════════════════════════════════════════════
    //  CRUD LIBROS
    // ══════════════════════════════════════════════════════════

    public String agregarLibro(String titulo, String autor, String genero, int anio) {
        String msg = catalogo.agregarLibro(titulo, autor, genero, anio);
        if (msg.startsWith("OK")) guardarTodo();
        return msg;
    }

    public Libro[] listarLibros() {
        return catalogo.obtenerTodosLosLibros();
    }

    public Libro[] buscarLibros(String termino) {
        return catalogo.buscarLibros(termino);
    }

    public Libro buscarLibroPorId(int id) {
        return catalogo.buscarLibroPorId(id);
    }

    public String editarLibro(int id, String titulo, String autor, String genero, int anio) {
        Libro libro = catalogo.buscarLibroPorId(id);
        if (libro == null)
            return "ERROR: No existe ningún libro con ID " + id + ".";
        if (!titulo.isBlank())  libro.setTitulo(titulo);
        if (!autor.isBlank())   libro.setAutor(autor);
        if (!genero.isBlank())  libro.setGenero(genero);
        if (anio > 0)           libro.setAnio(anio);
        guardarTodo();
        return "OK: Libro [" + id + "] actualizado correctamente.";
    }

    public String eliminarLibro(int id) {
        // no podemos eliminar un libro prestado
        if (historial.buscarPrestamoActivoPorLibro(id) != null)
            return "ERROR: El libro [" + id + "] está actualmente prestado. Devuélvelo primero.";
        String msg = catalogo.eliminarLibro(id);
        if (msg.startsWith("OK")) guardarTodo();
        return msg;
    }

    // ══════════════════════════════════════════════════════════
    //  CRUD USUARIOS
    // ══════════════════════════════════════════════════════════

    public String agregarUsuario(String nombre, String dni, String email, String telefono) {
        String msg = catalogo.agregarUsuario(nombre, dni, email, telefono);
        if (msg.startsWith("OK")) guardarTodo();
        return msg;
    }

    public Usuario[] listarUsuarios() {
        return catalogo.obtenerTodosLosUsuarios();
    }

    public Usuario buscarUsuarioPorId(int id) {
        return catalogo.buscarUsuarioPorId(id);
    }

    public String editarUsuario(int id, String nombre, String email, String telefono) {
        Usuario usuario = catalogo.buscarUsuarioPorId(id);
        if (usuario == null)
            return "ERROR: No existe ningún usuario con ID " + id + ".";
        if (!nombre.isBlank())    usuario.setNombre(nombre);
        if (!email.isBlank())     usuario.setEmail(email);
        if (!telefono.isBlank())  usuario.setTelefono(telefono);
        guardarTodo();
        return "OK: Usuario [" + id + "] actualizado correctamente.";
    }

    public String eliminarUsuario(int id) {
        // no podemos eliminar un usuario con préstamos activos
        Prestamo[] activos = historial.buscarPrestamosActivosPorUsuario(id);
        if (activos.length > 0)
            return "ERROR: El usuario [" + id + "] tiene " + activos.length
                 + " préstamo(s) pendiente(s). Debe devolver los libros primero.";
        String msg = catalogo.eliminarUsuario(id);
        if (msg.startsWith("OK")) guardarTodo();
        return msg;
    }

    // ══════════════════════════════════════════════════════════
    //  PRÉSTAMOS
    // ══════════════════════════════════════════════════════════

    /**
     * Registra un préstamo: relaciona Usuario + Libro.
     * Valida que el libro esté disponible y el usuario esté activo.
     */
    public String registrarPrestamo(int idUsuario, int idLibro) {
        Usuario usuario = catalogo.buscarUsuarioPorId(idUsuario);
        if (usuario == null)
            return "ERROR: No existe ningún usuario con ID " + idUsuario + ".";
        if (!usuario.isActivo())
            return "ERROR: El usuario \"" + usuario.getNombre() + "\" está inactivo.";

        Libro libro = catalogo.buscarLibroPorId(idLibro);
        if (libro == null)
            return "ERROR: No existe ningún libro con ID " + idLibro + ".";
        if (!libro.isDisponible())
            return "ERROR: El libro \"" + libro.getTitulo() + "\" ya está prestado.";

        // todo OK: creamos el préstamo
        int idPrestamo = historial.getSiguienteId();
        Prestamo prestamo = new Prestamo(idPrestamo, usuario, libro);

        libro.setDisponible(false);  // marcamos el libro como no disponible
        historial.apilarDirecto(prestamo);
        guardarTodo();

        return "OK: Préstamo registrado.\n"
             + "    Usuario : " + usuario.getNombre() + "\n"
             + "    Libro   : " + libro.getTitulo() + "\n"
             + "    Fecha   : " + prestamo.getFechaPrestamo();
    }

    /**
     * Registra la devolución de un libro prestado.
     * Busca el préstamo activo del libro y lo marca como devuelto.
     */
    public String registrarDevolucion(int idLibro) {
        Libro libro = catalogo.buscarLibroPorId(idLibro);
        if (libro == null)
            return "ERROR: No existe ningún libro con ID " + idLibro + ".";
        if (libro.isDisponible())
            return "ERROR: El libro \"" + libro.getTitulo() + "\" no está prestado actualmente.";

        Prestamo prestamo = historial.buscarPrestamoActivoPorLibro(idLibro);
        if (prestamo == null)
            return "ERROR: No se encontró el préstamo activo para este libro.";

        prestamo.registrarDevolucion();
        libro.setDisponible(true);
        guardarTodo();

        return "OK: Devolución registrada.\n"
             + "    Libro     : " + libro.getTitulo() + "\n"
             + "    Usuario   : " + prestamo.getNombreUsuario() + "\n"
             + "    Prestado  : " + prestamo.getFechaPrestamo() + "\n"
             + "    Devuelto  : " + prestamo.getFechaDevolucion();
    }

    // ── consultas de historial ─────────────────────────────
    public Prestamo[] obtenerHistorialCompleto() {
        return historial.obtenerHistorial(false);
    }

    public Prestamo[] obtenerPrestamosPendientes() {
        return historial.obtenerHistorial(true);
    }

    public Prestamo verUltimoPrestamo() {
        return historial.verUltimo();
    }

    // ── estadísticas ───────────────────────────────────────
    public int[] estadisticas() {
        Libro[] libros = catalogo.obtenerTodosLosLibros();
        int disponibles = 0, prestados = 0;
        for (Libro l : libros) {
            if (l.isDisponible()) disponibles++;
            else prestados++;
        }
        return new int[]{
            catalogo.contarLibros(),
            disponibles,
            prestados,
            catalogo.contarUsuarios(),
            historial.getTamano(),
            historial.contarPendientes(),
            historial.contarDevueltos()
        };
    }
}
