package MySQL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class ConexionBaseDeDatos {


private static final String URL = "jdbc:mysql://proyecto-u-mysql-vpnaprueba121-b9f7.d.aivencloud.com:25848/batallas_preguntas";//con esto le digo que base usar

    private static final String USUARIO = "avnadmin";

    private static final String PASSWORD = "AVNS_0gUHrNaURYyhMDxJYwF";

    public static Connection conectar() {

        try {

            Connection conexion =
                    DriverManager.getConnection(
                            URL,
                            USUARIO,
                            PASSWORD);

            System.out.println("Conectado a MySQL");

            return conexion;

        } catch (Exception e) {

            System.out.println("Error en conexión");

            e.printStackTrace();

            return null;
        }
    }
}

/*CREATE DATABASE IF NOT EXISTS nombreDeLaTabla;
USE nombreDeLaTabla;

CREATE TABLE IF NOT EXISTS clientes o nombre de lo que sea (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cedula INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    monto_inicial DOUBLE NOT NULL,
    tipo_cuenta VARCHAR(50) NOT NULL
);*/