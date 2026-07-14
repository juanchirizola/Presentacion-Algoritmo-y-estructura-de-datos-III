@echo off
echo.
echo  Compilando...
if not exist out mkdir out

javac -encoding UTF-8 -sourcepath src -d out ^
  src\modelos\Libro.java ^
  src\modelos\Usuario.java ^
  src\modelos\Prestamo.java ^
  src\estructuras\NodoUsuario.java ^
  src\estructuras\ListaSimple.java ^
  src\estructuras\Pila.java ^
  src\datos\DatosJSON.java ^
  src\servicio\SistemaBiblioteca.java ^
  src\presentacion\Consola.java ^
  Main.java

if %errorlevel% neq 0 (
  echo  ERROR en la compilacion.
  pause
  exit /b
)

echo  Compilacion exitosa!
echo.
java -cp out Main
