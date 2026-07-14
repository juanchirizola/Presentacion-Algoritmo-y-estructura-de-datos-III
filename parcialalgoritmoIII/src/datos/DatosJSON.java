package datos;

import modelos.Libro;
import modelos.Usuario;
import modelos.Prestamo;
import estructuras.ListaSimple;
import estructuras.Pila;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 *  CAPA DE DATOS: DatosJSON
 * ============================================================
 *  Responsable de GUARDAR y CARGAR los datos en archivos JSON.
 *  No usa librerías externas: el JSON se construye y parsea
 *  manualmente con StringBuilder y búsqueda de cadenas.
 *
 *  Archivos generados:
 *    data/libros.json    → catálogo de libros
 *    data/usuarios.json  → usuarios registrados
 *    data/prestamos.json → historial de préstamos (pila)
 *
 *  Formato JSON de cada archivo:
 *    {
 *      "datos": [
 *        { ...objeto1... },
 *        { ...objeto2... }
 *      ]
 *    }
 * ============================================================
 */
public class DatosJSON {

    // ── rutas de los archivos ──────────────────────────────
    private static final String DIR        = "data/";
    private static final String LIBROS     = DIR + "libros.json";
    private static final String USUARIOS   = DIR + "usuarios.json";
    private static final String PRESTAMOS  = DIR + "prestamos.json";

    // ── GUARDAR ────────────────────────────────────────────

    /** Guarda todos los libros de la lista en libros.json */
    public static void guardarLibros(ListaSimple lista) {
        Libro[] libros = lista.obtenerTodosLosLibros();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"datos\": [\n");
        for (int i = 0; i < libros.length; i++) {
            sb.append("    ").append(libros[i].toJson());
            if (i < libros.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}");
        escribirArchivo(LIBROS, sb.toString());
    }

    /** Guarda todos los usuarios en usuarios.json */
    public static void guardarUsuarios(ListaSimple lista) {
        Usuario[] usuarios = lista.obtenerTodosLosUsuarios();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"datos\": [\n");
        for (int i = 0; i < usuarios.length; i++) {
            sb.append("    ").append(usuarios[i].toJson());
            if (i < usuarios.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}");
        escribirArchivo(USUARIOS, sb.toString());
    }

    /** Guarda todo el historial de préstamos en prestamos.json */
    public static void guardarPrestamos(Pila pila) {
        // obtenerHistorial() devuelve de más reciente a más antiguo;
        // al cargar, invertimos para que el orden de apilado sea correcto
        Prestamo[] prestamos = pila.obtenerHistorial();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"datos\": [\n");
        for (int i = 0; i < prestamos.length; i++) {
            sb.append("    ").append(prestamos[i].toJson());
            if (i < prestamos.length - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("  ]\n}");
        escribirArchivo(PRESTAMOS, sb.toString());
    }

    // ── CARGAR ─────────────────────────────────────────────

    /** Carga los libros desde libros.json y los agrega a la lista. */
    public static void cargarLibros(ListaSimple lista) {
        String contenido = leerArchivo(LIBROS);
        if (contenido == null) return;
        List<String> objetos = extraerObjetos(contenido);
        for (String obj : objetos) {
            try {
                Libro libro = Libro.fromJson(obj);
                lista.agregarLibroDirecto(libro);
            } catch (Exception e) {
                System.out.println("Advertencia: no se pudo cargar un libro del JSON.");
            }
        }
    }

    /** Carga los usuarios desde usuarios.json. */
    public static void cargarUsuarios(ListaSimple lista) {
        String contenido = leerArchivo(USUARIOS);
        if (contenido == null) return;
        List<String> objetos = extraerObjetos(contenido);
        for (String obj : objetos) {
            try {
                Usuario usuario = Usuario.fromJson(obj);
                lista.agregarUsuarioDirecto(usuario);
            } catch (Exception e) {
                System.out.println("Advertencia: no se pudo cargar un usuario del JSON.");
            }
        }
    }

    /**
     * Carga los préstamos desde prestamos.json.
     * Los apila en orden INVERSO para que el tope de la pila
     * corresponda al préstamo más reciente (índice 0 del JSON).
     */
    public static void cargarPrestamos(Pila pila) {
        String contenido = leerArchivo(PRESTAMOS);
        if (contenido == null) return;
        List<String> objetos = extraerObjetos(contenido);
        // apilamos del último al primero para mantener el orden LIFO correcto
        for (int i = objetos.size() - 1; i >= 0; i--) {
            try {
                Prestamo prestamo = Prestamo.fromJson(objetos.get(i));
                pila.apilarDirecto(prestamo);
            } catch (Exception e) {
                System.out.println("Advertencia: no se pudo cargar un préstamo del JSON.");
            }
        }
    }

    // ── utilidades privadas de I/O ─────────────────────────

    private static void escribirArchivo(String ruta, String contenido) {
        try {
            File f = new File(ruta);
            f.getParentFile().mkdirs();  // crea el directorio si no existe
            try (OutputStreamWriter writer = new OutputStreamWriter(
                    new FileOutputStream(f), StandardCharsets.UTF_8)) {
                writer.write(contenido);
            }
        } catch (IOException e) {
            System.out.println("Error al guardar " + ruta + ": " + e.getMessage());
        }
    }

    private static String leerArchivo(String ruta) {
        File f = new File(ruta);
        if (!f.exists()) return null;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null)
                sb.append(linea).append("\n");
            return sb.toString();
        } catch (IOException e) {
            System.out.println("Error al leer " + ruta + ": " + e.getMessage());
            return null;
        }
    }

    // ── parser de JSON: extrae objetos del array "datos" ──
    /**
     * Extrae cada objeto JSON { ... } del array "datos".
     * Funciona contando llaves { y } para encontrar los límites
     * de cada objeto, aunque el JSON tenga objetos anidados.
     *
     * Ejemplo de entrada:
     *   { "datos": [ {"id":1,...}, {"id":2,...} ] }
     *
     * Devuelve: [ "{\"id\":1,...}", "{\"id\":2,...}" ]
     */
    private static List<String> extraerObjetos(String json) {
        List<String> resultado = new ArrayList<>();
        int inicio = json.indexOf("[");
        if (inicio == -1) return resultado;

        int i = inicio + 1;
        while (i < json.length()) {
            if (json.charAt(i) == '{') {
                // encontramos el inicio de un objeto: buscamos su cierre
                int profundidad = 0;
                int j = i;
                while (j < json.length()) {
                    char c = json.charAt(j);
                    if      (c == '{') profundidad++;
                    else if (c == '}') {
                        profundidad--;
                        if (profundidad == 0) {
                            resultado.add(json.substring(i, j + 1));
                            i = j + 1;
                            break;
                        }
                    }
                    j++;
                }
            } else {
                i++;
            }
        }
        return resultado;
    }
}
