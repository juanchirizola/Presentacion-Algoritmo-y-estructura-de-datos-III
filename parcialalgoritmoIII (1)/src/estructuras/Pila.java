package estructuras;

import modelos.Prestamo;

/**
 * ============================================================
 *  ESTRUCTURA: Pila (Stack — LIFO)
 * ============================================================
 *  Almacena el HISTORIAL DE PRÉSTAMOS con comportamiento LIFO:
 *  Último en Entrar → Primero en Salir.
 *
 *  Cada préstamo se apila en el TOPE.
 *  El tope es siempre el préstamo más reciente.
 *
 *  Visualización:
 *
 *            tope ──▶  [Préstamo #5]  ← más reciente
 *                      [Préstamo #4]
 *                      [Préstamo #3]
 *                      [Préstamo #2]
 *                      [Préstamo #1]  ← más antiguo
 *
 *  Cuando recorremos la pila, siempre vemos del más nuevo
 *  al más antiguo (tope → base).
 *
 *  Complejidad:
 *    apilar (push)     O(1) → solo tocamos el tope
 *    verUltimo (peek)  O(1) → solo leemos el tope
 *    obtenerHistorial  O(n) → recorremos todo
 *
 *  Nota académica: usamos la pila como BITÁCORA (no
 *  desapilamos), porque el objetivo es mostrar historial.
 *  El LIFO se demuestra en el ORDEN DE LECTURA.
 * ============================================================
 */
public class Pila {

    // ─────────────────────────────────────────────────────────
    //  INNER CLASS: NodoPrestamo
    // ─────────────────────────────────────────────────────────
    /**
     * Cada nodo de la pila guarda un Préstamo y apunta
     * al nodo que estaba antes en el tope.
     *
     *   push nuevo:  [nuevo] → [anterior tope] → ... → null
     */
    private static class NodoPrestamo {
        Prestamo      dato;
        NodoPrestamo  siguiente;

        NodoPrestamo(Prestamo prestamo) {
            this.dato      = prestamo;
            this.siguiente = null;
        }
    }

    // ── atributos ──────────────────────────────────────────
    private NodoPrestamo tope;    // siempre apunta al más reciente
    private int          tamano;
    private int          siguienteId;

    // ── constructor ────────────────────────────────────────
    public Pila() {
        tope        = null;
        tamano      = 0;
        siguienteId = 1;
    }

    // ── PUSH: apilar un nuevo préstamo ─────────────────────
    /**
     * Inserta un nuevo préstamo en el tope.
     * El nuevo nodo apunta al que antes era el tope,
     * y pasa a ser el nuevo tope.
     *
     *   Antes:   tope → [B] → [A] → null
     *   Push C:  tope → [C] → [B] → [A] → null
     */
    public void apilar(Prestamo prestamo) {
        NodoPrestamo nuevoNodo = new NodoPrestamo(prestamo);
        nuevoNodo.siguiente = tope;   // apunta al ex-tope
        tope                = nuevoNodo; // ahora ES el tope
        tamano++;
    }

    // ── PEEK: ver el tope sin extraerlo ───────────────────
    /**
     * Devuelve el préstamo del tope SIN quitarlo de la pila.
     * Retorna null si la pila está vacía.
     */
    public Prestamo verUltimo() {
        return tope != null ? tope.dato : null;
    }

    // ── recorrido completo ─────────────────────────────────
    /**
     * Devuelve todos los préstamos en orden LIFO
     * (tope → base = más reciente → más antiguo).
     *
     * @param soloActivos  true = solo devuelve préstamos pendientes (no devueltos)
     *                     false = devuelve todos (historial completo)
     */
    public Prestamo[] obtenerHistorial(boolean soloActivos) {
        // primera pasada: contar
        int count = 0;
        NodoPrestamo actual = tope;
        while (actual != null) {
            if (!soloActivos || !actual.dato.isDevuelto()) count++;
            actual = actual.siguiente;
        }
        // segunda pasada: llenar
        Prestamo[] resultado = new Prestamo[count];
        int i = 0;
        actual = tope;
        while (actual != null) {
            if (!soloActivos || !actual.dato.isDevuelto())
                resultado[i++] = actual.dato;
            actual = actual.siguiente;
        }
        return resultado;
    }

    /** Devuelve todos los préstamos sin filtro. */
    public Prestamo[] obtenerHistorial() {
        return obtenerHistorial(false);
    }

    /** Busca un préstamo activo (no devuelto) para un libro específico. */
    public Prestamo buscarPrestamoActivoPorLibro(int idLibro) {
        NodoPrestamo actual = tope;
        while (actual != null) {
            if (actual.dato.getIdLibro() == idLibro && !actual.dato.isDevuelto())
                return actual.dato;
            actual = actual.siguiente;
        }
        return null;
    }

    /** Busca un préstamo activo para un usuario específico. */
    public Prestamo[] buscarPrestamosActivosPorUsuario(int idUsuario) {
        // primera pasada: contar
        int count = 0;
        NodoPrestamo actual = tope;
        while (actual != null) {
            if (actual.dato.getIdUsuario() == idUsuario && !actual.dato.isDevuelto()) count++;
            actual = actual.siguiente;
        }
        // segunda pasada: llenar
        Prestamo[] resultado = new Prestamo[count];
        int i = 0;
        actual = tope;
        while (actual != null) {
            if (actual.dato.getIdUsuario() == idUsuario && !actual.dato.isDevuelto())
                resultado[i++] = actual.dato;
            actual = actual.siguiente;
        }
        return resultado;
    }

    // ── consultas ─────────────────────────────────────────
    public boolean estaVacia()          { return tope == null; }
    public int     getTamano()          { return tamano;       }
    public int     getSiguienteId()     { return siguienteId;  }
    public void    setSiguienteId(int id){ this.siguienteId = id; }

    /** Agrega un préstamo ya construido (para cargar desde JSON). */
    public void apilarDirecto(Prestamo prestamo) {
        apilar(prestamo);
        if (prestamo.getId() >= siguienteId)
            siguienteId = prestamo.getId() + 1;
    }

    public int contarPendientes() {
        return obtenerHistorial(true).length;
    }

    public int contarDevueltos() {
        return tamano - contarPendientes();
    }
}
