package estructuras;

import modelos.Usuario;

/**
 * ============================================================
 *  ESTRUCTURA: NodoUsuario
 * ============================================================
 *  Un NODO es la "cajita" que vive dentro de la lista enlazada.
 *  Cada nodo guarda DOS cosas:
 *
 *    1. dato      → el objeto Usuario que queremos almacenar
 *    2. siguiente → una FLECHA (referencia) al próximo nodo
 *                   Si es null, no hay nodo siguiente (es el último)
 *
 *  Visualización:
 *
 *   ┌────────────────────┐    ┌────────────────────┐
 *   │  dato: Usuario #1  │───▶│  dato: Usuario #2  │───▶ null
 *   └────────────────────┘    └────────────────────┘
 *        NodoUsuario #1             NodoUsuario #2
 *
 *  El NodoUsuario NO sabe nada del mundo exterior: solo conoce
 *  al usuario que guarda y al nodo que le sigue.
 *  La inteligencia de cómo recorrer o modificar la cadena
 *  vive en ListaSimple.java.
 * ============================================================
 */
public class NodoUsuario {

    // ── atributos ──────────────────────────────────────────
    public Usuario      dato;       // el usuario almacenado en este nodo
    public NodoUsuario  siguiente;  // puntero al próximo nodo (null = fin)

    // ── constructor ────────────────────────────────────────
    public NodoUsuario(Usuario usuario) {
        this.dato      = usuario;
        this.siguiente = null;   // por defecto, ningún siguiente
    }
}
