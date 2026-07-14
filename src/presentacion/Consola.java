package presentacion;

import modelos.Libro;
import modelos.Usuario;
import modelos.Prestamo;
import servicio.SistemaBiblioteca;

import java.util.Scanner;

/**
 * ============================================================
 *  PRESENTACIÓN: Consola
 * ============================================================
 *  Toda la interacción con el usuario vive aquí.
 *  Esta clase NO conoce las estructuras de datos internas:
 *  solo usa el SistemaBiblioteca como intermediario.
 *
 *  Responsabilidades:
 *    - Colores ANSI para una interfaz linda en terminal
 *    - Menús interactivos (principal, libros, usuarios, préstamos)
 *    - Formateo de objetos para mostrar en pantalla
 *    - Validación básica de entrada del usuario
 * ============================================================
 */
public class Consola {

    // ── códigos ANSI ───────────────────────────────────────
    static final String RESET   = "\033[0m";
    static final String NEGRITA = "\033[1m";
    static final String ROJO    = "\033[91m";
    static final String VERDE   = "\033[92m";
    static final String AMARILLO= "\033[93m";
    static final String AZUL    = "\033[94m";
    static final String MAGENTA = "\033[95m";
    static final String CIAN    = "\033[96m";
    static final String GRIS    = "\033[90m";
    static final String BLANCO  = "\033[97m";

    // ── atributos ──────────────────────────────────────────
    private final SistemaBiblioteca sistema;
    private final Scanner           scanner;

    // ── constructor ────────────────────────────────────────
    public Consola(SistemaBiblioteca sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in, "UTF-8");
        // Habilitar colores ANSI en Windows
        try { new ProcessBuilder("").inheritIO().start(); } catch (Exception ignored) {}
    }

    // ══════════════════════════════════════════════════════════
    //  MENÚ PRINCIPAL
    // ══════════════════════════════════════════════════════════

    public void iniciar() {
        while (true) {
            limpiar();
            encabezado("SISTEMA DE GESTIÓN DE BIBLIOTECA", CIAN);
            System.out.println();
            opcion("1", "Gestionar Libros",    CIAN    + "(Lista Simple)" + RESET);
            opcion("2", "Gestionar Usuarios",  VERDE   + "(Lista Simple)" + RESET);
            opcion("3", "Gestionar Préstamos", MAGENTA + "(Pila / LIFO)"  + RESET);
            opcion("4", "Ver Estadísticas",    "");
            opcion("0", "Salir",               "");
            System.out.println();

            String op = pedir("Selecciona una opción");
            switch (op) {
                case "1" -> menuLibros();
                case "2" -> menuUsuarios();
                case "3" -> menuPrestamos();
                case "4" -> mostrarEstadisticas();
                case "0" -> {
                    limpiar();
                    System.out.println(VERDE + NEGRITA + "  ¡Hasta luego!\n" + RESET);
                    return;
                }
                default  -> error("Opción inválida.");
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  MENÚ LIBROS
    // ══════════════════════════════════════════════════════════

    private void menuLibros() {
        while (true) {
            limpiar();
            encabezado("CATÁLOGO DE LIBROS — Lista Simple", CIAN);
            System.out.println();
            opcion("1", "Agregar libro",   "");
            opcion("2", "Listar todos",    "");
            opcion("3", "Buscar libro",    "por título o autor");
            opcion("4", "Editar libro",    "");
            opcion("5", "Eliminar libro",  "");
            opcion("0", "Volver",          "");
            System.out.println();

            String op = pedir("Selecciona una opción");
            switch (op) {
                case "1" -> {
                    System.out.println();
                    String titulo = pedir("Título");
                    String autor  = pedir("Autor");
                    String genero = pedir("Género");
                    int    anio   = pedirInt("Año de publicación");
                    mostrarMensaje(sistema.agregarLibro(titulo, autor, genero, anio));
                }
                case "2" -> mostrarLibros(sistema.listarLibros(), "Catálogo completo");
                case "3" -> {
                    String termino = pedir("Buscar (título o autor)");
                    mostrarLibros(sistema.buscarLibros(termino), "Resultados");
                }
                case "4" -> {
                    int id = pedirInt("ID del libro a editar");
                    Libro l = sistema.buscarLibroPorId(id);
                    if (l == null) { error("Libro no encontrado."); pausar(); break; }
                    mostrarLibro(l);
                    System.out.println(GRIS + "  (dejá en blanco para no cambiar el campo)" + RESET);
                    String titulo = pedir("Nuevo título");
                    String autor  = pedir("Nuevo autor");
                    String genero = pedir("Nuevo género");
                    String anioStr= pedir("Nuevo año (0 = sin cambio)");
                    int    anio   = anioStr.isBlank() ? 0 : Integer.parseInt(anioStr);
                    mostrarMensaje(sistema.editarLibro(id, titulo, autor, genero, anio));
                }
                case "5" -> {
                    int id = pedirInt("ID del libro a eliminar");
                    Libro l = sistema.buscarLibroPorId(id);
                    if (l == null) { error("Libro no encontrado."); pausar(); break; }
                    mostrarLibro(l);
                    if (confirmar("¿Confirmas la eliminación?"))
                        mostrarMensaje(sistema.eliminarLibro(id));
                    else
                        info("Operación cancelada.");
                }
                case "0" -> { return; }
                default  -> error("Opción inválida.");
            }
            pausar();
        }
    }

    // ══════════════════════════════════════════════════════════
    //  MENÚ USUARIOS
    // ══════════════════════════════════════════════════════════

    private void menuUsuarios() {
        while (true) {
            limpiar();
            encabezado("GESTIÓN DE USUARIOS — Lista Simple", VERDE);
            System.out.println();
            opcion("1", "Registrar usuario", "");
            opcion("2", "Listar todos",      "");
            opcion("3", "Editar usuario",    "");
            opcion("4", "Eliminar usuario",  "");
            opcion("0", "Volver",            "");
            System.out.println();

            String op = pedir("Selecciona una opción");
            switch (op) {
                case "1" -> {
                    System.out.println();
                    String nombre   = pedir("Nombre completo");
                    String dni      = pedir("DNI");
                    String email    = pedir("Email");
                    String telefono = pedir("Teléfono");
                    mostrarMensaje(sistema.agregarUsuario(nombre, dni, email, telefono));
                }
                case "2" -> mostrarUsuarios(sistema.listarUsuarios(), "Usuarios registrados");
                case "3" -> {
                    int id = pedirInt("ID del usuario a editar");
                    Usuario u = sistema.buscarUsuarioPorId(id);
                    if (u == null) { error("Usuario no encontrado."); pausar(); break; }
                    mostrarUsuario(u);
                    System.out.println(GRIS + "  (dejá en blanco para no cambiar el campo)" + RESET);
                    String nombre   = pedir("Nuevo nombre");
                    String email    = pedir("Nuevo email");
                    String telefono = pedir("Nuevo teléfono");
                    mostrarMensaje(sistema.editarUsuario(id, nombre, email, telefono));
                }
                case "4" -> {
                    int id = pedirInt("ID del usuario a eliminar");
                    Usuario u = sistema.buscarUsuarioPorId(id);
                    if (u == null) { error("Usuario no encontrado."); pausar(); break; }
                    mostrarUsuario(u);
                    if (confirmar("¿Confirmas la eliminación?"))
                        mostrarMensaje(sistema.eliminarUsuario(id));
                    else
                        info("Operación cancelada.");
                }
                case "0" -> { return; }
                default  -> error("Opción inválida.");
            }
            pausar();
        }
    }

    // ══════════════════════════════════════════════════════════
    //  MENÚ PRÉSTAMOS
    // ══════════════════════════════════════════════════════════

    private void menuPrestamos() {
        while (true) {
            limpiar();
            encabezado("PRÉSTAMOS Y DEVOLUCIONES — Pila LIFO", MAGENTA);
            System.out.println();
            opcion("1", "Registrar préstamo",    "");
            opcion("2", "Registrar devolución",  "");
            opcion("3", "Ver último préstamo",   "tope de la pila");
            opcion("4", "Ver historial completo","más reciente primero");
            opcion("5", "Ver pendientes",        "libros no devueltos");
            opcion("0", "Volver",                "");
            System.out.println();

            String op = pedir("Selecciona una opción");
            switch (op) {
                case "1" -> {
                    listarResumenUsuarios();
                    int idU = pedirInt("ID del usuario");
                    listarResumenLibrosDisponibles();
                    int idL = pedirInt("ID del libro");
                    mostrarMensaje(sistema.registrarPrestamo(idU, idL));
                }
                case "2" -> {
                    listarResumenLibrosPrestados();
                    int idL = pedirInt("ID del libro a devolver");
                    mostrarMensaje(sistema.registrarDevolucion(idL));
                }
                case "3" -> {
                    Prestamo p = sistema.verUltimoPrestamo();
                    if (p == null) info("La pila está vacía.");
                    else {
                        encabezado("Tope de la Pila (último préstamo)", AZUL);
                        System.out.println("  " + formatearPrestamo(p));
                    }
                }
                case "4" -> mostrarPrestamos(sistema.obtenerHistorialCompleto(), "Historial completo");
                case "5" -> mostrarPrestamos(sistema.obtenerPrestamosPendientes(), "Préstamos pendientes");
                case "0" -> { return; }
                default  -> error("Opción inválida.");
            }
            pausar();
        }
    }

    // ══════════════════════════════════════════════════════════
    //  ESTADÍSTICAS
    // ══════════════════════════════════════════════════════════

    private void mostrarEstadisticas() {
        limpiar();
        int[] s = sistema.estadisticas();
        encabezado("ESTADÍSTICAS GENERALES", AMARILLO);
        System.out.println();
        System.out.printf("  Total de libros       : %s%d%s%n", NEGRITA, s[0], RESET);
        System.out.printf("  " + VERDE  + "Disponibles%s           : %d%n", RESET, s[1]);
        System.out.printf("  " + ROJO   + "Prestados%s             : %d%n", RESET, s[2]);
        separador();
        System.out.printf("  Total de usuarios     : %s%d%s%n", NEGRITA, s[3], RESET);
        separador();
        System.out.printf("  Total de préstamos    : %s%d%s%n", NEGRITA, s[4], RESET);
        System.out.printf("  " + AMARILLO + "Pendientes%s            : %d%n", RESET, s[5]);
        System.out.printf("  " + VERDE    + "Devueltos%s             : %d%n", RESET, s[6]);
        pausar();
    }

    // ══════════════════════════════════════════════════════════
    //  FORMATEO DE OBJETOS
    // ══════════════════════════════════════════════════════════

    private String formatearLibro(Libro l) {
        String color = l.isDisponible() ? VERDE : ROJO;
        String estado = l.isDisponible() ? "Disponible" : "Prestado";
        return String.format("%s[%02d]%s \"%s%s%s\" — %s  %s(%s)%s  %s(%s, %d)%s",
            GRIS, l.getId(), RESET,
            BLANCO, l.getTitulo(), RESET,
            l.getAutor(),
            color, estado, RESET,
            GRIS, l.getGenero(), l.getAnio(), RESET);
    }

    private String formatearUsuario(Usuario u) {
        String color = u.isActivo() ? VERDE : ROJO;
        String estado = u.isActivo() ? "Activo" : "Inactivo";
        return String.format("%s[%02d]%s %s%s%s | DNI: %s | %s | Tel: %s  %s(%s)%s",
            GRIS, u.getId(), RESET,
            BLANCO, u.getNombre(), RESET,
            u.getDni(), u.getEmail(), u.getTelefono(),
            color, estado, RESET);
    }

    private String formatearPrestamo(Prestamo p) {
        if (p.isDevuelto()) {
            return String.format(
                "%s[%03d]%s \"%s%s%s\" → %s  %sPrestado:%s %s  %sDevuelto:%s %s  %s✔%s",
                GRIS, p.getId(), RESET,
                BLANCO, p.getTituloLibro(), RESET,
                p.getNombreUsuario(),
                GRIS, RESET, p.getFechaPrestamo(),
                GRIS, RESET, p.getFechaDevolucion(),
                VERDE, RESET);
        } else {
            return String.format(
                "%s[%03d]%s \"%s%s%s\" → %s  %sPrestado:%s %s  %sPENDIENTE%s",
                GRIS, p.getId(), RESET,
                BLANCO, p.getTituloLibro(), RESET,
                p.getNombreUsuario(),
                GRIS, RESET, p.getFechaPrestamo(),
                AMARILLO, RESET);
        }
    }

    // ── mostrar listas ─────────────────────────────────────

    private void mostrarLibro(Libro l) {
        System.out.println("  " + formatearLibro(l));
    }

    private void mostrarUsuario(Usuario u) {
        System.out.println("  " + formatearUsuario(u));
    }

    private void mostrarLibros(Libro[] libros, String titulo) {
        System.out.println();
        encabezado(titulo + " (" + libros.length + ")", CIAN);
        if (libros.length == 0) { info("No hay libros para mostrar."); return; }
        for (int i = 0; i < libros.length; i++)
            System.out.printf("  %s%d.%s %s%n", GRIS, i + 1, RESET, formatearLibro(libros[i]));
    }

    private void mostrarUsuarios(Usuario[] usuarios, String titulo) {
        System.out.println();
        encabezado(titulo + " (" + usuarios.length + ")", VERDE);
        if (usuarios.length == 0) { info("No hay usuarios registrados."); return; }
        for (int i = 0; i < usuarios.length; i++)
            System.out.printf("  %s%d.%s %s%n", GRIS, i + 1, RESET, formatearUsuario(usuarios[i]));
    }

    private void mostrarPrestamos(Prestamo[] prestamos, String titulo) {
        System.out.println();
        encabezado(titulo + " (" + prestamos.length + ")", MAGENTA);
        if (prestamos.length == 0) { info("No hay préstamos registrados."); return; }
        for (int i = 0; i < prestamos.length; i++)
            System.out.printf("  %s%d.%s %s%n", GRIS, i + 1, RESET, formatearPrestamo(prestamos[i]));
    }

    // ── atajos para menú de préstamos ─────────────────────

    private void listarResumenUsuarios() {
        Usuario[] usuarios = sistema.listarUsuarios();
        System.out.println();
        separador();
        for (Usuario u : usuarios)
            System.out.printf("  %s[%02d]%s %s%n", GRIS, u.getId(), RESET, u.getNombre());
        separador();
    }

    private void listarResumenLibrosDisponibles() {
        System.out.println();
        separador();
        for (Libro l : sistema.listarLibros())
            if (l.isDisponible())
                System.out.printf("  %s[%02d]%s \"%s\"%n", GRIS, l.getId(), RESET, l.getTitulo());
        separador();
    }

    private void listarResumenLibrosPrestados() {
        System.out.println();
        separador();
        for (Libro l : sistema.listarLibros())
            if (!l.isDisponible())
                System.out.printf("  %s[%02d]%s \"%s\"%n", GRIS, l.getId(), RESET, l.getTitulo());
        separador();
    }

    // ══════════════════════════════════════════════════════════
    //  UTILIDADES DE PRESENTACIÓN
    // ══════════════════════════════════════════════════════════

    private void encabezado(String titulo, String color) {
        int ancho = titulo.length() + 4;
        System.out.println(color + "╔" + "═".repeat(ancho) + "╗" + RESET);
        System.out.println(color + "║  " + NEGRITA + titulo + RESET + color + "  ║" + RESET);
        System.out.println(color + "╚" + "═".repeat(ancho) + "╝" + RESET);
    }

    private void separador() {
        System.out.println(GRIS + "  " + "─".repeat(58) + RESET);
    }

    private void opcion(String num, String texto, String extra) {
        System.out.printf("  %s%s.%s %-30s %s%s%n",
            BLANCO, num, RESET, texto, GRIS, extra.isEmpty() ? "" : extra + RESET);
    }

    private void mostrarMensaje(String msg) {
        System.out.println();
        if (msg.startsWith("OK"))
            System.out.println(VERDE + "  ✔ " + msg.substring(3).trim() + RESET);
        else
            System.out.println(ROJO  + "  ✘ " + msg.substring(6).trim() + RESET);
    }

    private void info(String msg) {
        System.out.println(AMARILLO + "  ℹ " + msg + RESET);
    }

    private void error(String msg) {
        System.out.println(ROJO + "  ✘ " + msg + RESET);
    }

    private String pedir(String campo) {
        System.out.printf("%s  ▶ %s%s: %s", AMARILLO, campo, RESET, "");
        return scanner.nextLine().trim();
    }

    private int pedirInt(String campo) {
        while (true) {
            String s = pedir(campo);
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { error("Ingresá un número entero válido."); }
        }
    }

    private boolean confirmar(String msg) {
        System.out.printf("%s  ? %s (s/n): %s", AMARILLO, msg, RESET);
        String r = scanner.nextLine().trim().toLowerCase();
        return r.equals("s") || r.equals("si") || r.equals("sí") || r.equals("y");
    }

    private void pausar() {
        System.out.printf("%n%s  Presioná ENTER para continuar...%s", GRIS, RESET);
        scanner.nextLine();
    }

    private void limpiar() {
        System.out.print("\033[2J\033[H");
        System.out.flush();
    }
}
