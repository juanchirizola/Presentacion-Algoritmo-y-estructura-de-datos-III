#!/bin/bash
echo ""
echo " Compilando..."
mkdir -p out

javac -encoding UTF-8 -sourcepath src -d out \
  src/modelos/Libro.java \
  src/modelos/Usuario.java \
  src/modelos/Prestamo.java \
  src/estructuras/NodoUsuario.java \
  src/estructuras/ListaSimple.java \
  src/estructuras/Pila.java \
  src/datos/DatosJSON.java \
  src/servicio/SistemaBiblioteca.java \
  src/presentacion/Consola.java \
  Main.java

if [ $? -ne 0 ]; then
  echo " ERROR en la compilacion."
  exit 1
fi

echo " Compilacion exitosa!"
echo ""
java -cp out Main
