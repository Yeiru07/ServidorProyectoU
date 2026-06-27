@echo off
setlocal

cd /d "%~dp0"

if not exist target\server-classes mkdir target\server-classes

echo Compilando servidor...
javac -d target\server-classes ^
  src\main\java\Interfaces\*.java ^
  src\main\java\Modelo\*.java ^
  src\main\java\Controlador\*.java ^
  src\main\java\DAO\*.java ^
  src\main\java\MySQL\*.java ^
  src\main\java\servidor_cliente\*.java

if errorlevel 1 (
  echo.
  echo No se pudo compilar el servidor.
  pause
  exit /b 1
)

set "MYSQL_JAR=%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\9.3.0\mysql-connector-j-9.3.0.jar"
set "PROTOBUF_JAR=%USERPROFILE%\.m2\repository\com\google\protobuf\protobuf-java\4.29.0\protobuf-java-4.29.0.jar"

echo.
echo Iniciando servidor en puerto 5000...
java -cp "target\server-classes;%MYSQL_JAR%;%PROTOBUF_JAR%" servidor_cliente.Servidor

pause
