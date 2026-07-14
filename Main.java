/**
 * ============================================================
 *  SISTEMA DE GESTIÓN DE BIBLIOTECA
 * ============================================================
 *  Proyecto académico — Análisis de Sistemas
 *  Estructuras de datos: Lista Simple + Pila (Stack)
 *
 *  Cómo compilar:
 *    Windows:
 *      javac -encoding UTF-8 -sourcepath src -d out
 *            src/modelos/*.java src/estructuras/*.java
 *            src/datos/*.java src/servicio/*.java
 *            src/presentacion/*.java Main.java
 *
 *    Linux / macOS:
 *      javac -encoding UTF-8 -sourcepath src -d out \
 *            src/modelos/*.java src/estructuras/*.java \
 *            src/datos/*.java src/servicio/*.java \
 *            src/presentacion/*.java Main.java
 *
 *  Cómo ejecutar (desde la carpeta del proyecto):
 *    java -cp out Main
 *
 *  O con el script:
 *    Windows → ejecutar.bat
 *    Linux   → bash ejecutar.sh
 *
 * ============================================================
 *  ARQUITECTURA
 * ============================================================
 *
 *   Main
 *    └──▶ Consola          (presentación: menús, colores, I/O)
 *          └──▶ SistemaBiblioteca  (servicio: lógica de negocio)
 *                ├──▶ ListaSimple  (estructura: lista enlazada)
 *                │     ├── NodoLibro   → Libro
 *                │     └── NodoUsuario → Usuario
 *                ├──▶ Pila         (estructura: stack LIFO)
 *                │     └── NodoPrestamo → Prestamo
 *                └──▶ DatosJSON    (persistencia: archivos .json)
 *
 * ============================================================
 */
public class Main {

    public static void main(String[] args) {
        // habilitamos UTF-8 en la salida estándar
        System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));

        // creamos el sistema (carga datos del JSON o siembra los iniciales)
        servicio.SistemaBiblioteca sistema = new servicio.SistemaBiblioteca();

        // lanzamos la interfaz de consola
        presentacion.Consola consola = new presentacion.Consola(sistema);
        consola.iniciar();
    }
}
