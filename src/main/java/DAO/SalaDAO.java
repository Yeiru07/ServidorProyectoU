package DAO;

import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import Modelo.Usuario;
import MySQL.ConexionBaseDeDatos;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SalaDAO {

    private Connection conexion;

    public SalaDAO() {
        this.conexion = ConexionBaseDeDatos.conectar();
    }

    // ==================== MÉTODOS PARA SALAS ====================
    /**
     * Guarda una nueva sala en la base de datos
     */
    public boolean guardarSala(Sala sala, String nombreUsuario) {

        String sql
                = "INSERT INTO sala "
                + "(codigoSala, nombreSala, cantidadJugadore, fk_idUsuario) "
                + "VALUES (?, ?, ?, "
                + "(SELECT idusuarios "
                + "FROM usuarios "
                + "WHERE nombreUsuario = ? LIMIT 1))";

        try (
                Connection conexion = ConexionBaseDeDatos.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, sala.getCodigoSala());
            ps.setString(2, sala.getNombreSala());
            ps.setInt(3, sala.getCantidadJugadores());
            ps.setString(4, nombreUsuario);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    public boolean presentarSala(int codigoSala) {

        String sql
                = "UPDATE sala "
                + "SET estado = 1 "
                + "WHERE codigoSala = ?";

        try (
                Connection conexion = ConexionBaseDeDatos.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, codigoSala);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<Sala> consultarSalasDeUsuario(String nombreUsuario) {

        ArrayList<Sala> listaSalas = new ArrayList<>();

        String sql = "{call ObtenerSalasDeUsuario(?)}";

        try (
                Connection conexion = ConexionBaseDeDatos.conectar(); CallableStatement cs = conexion.prepareCall(sql)) {

            cs.setString(1, nombreUsuario);

            try (ResultSet rs = cs.executeQuery()) {

                while (rs.next()) {

                    Sala sala = new Sala(
                            rs.getInt("codigoSala"),
                            rs.getString("nombreSala"),
                            true,
                            rs.getInt("cantidadJugadore")
                    );

                    listaSalas.add(sala);
                }
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return listaSalas;
    }

    /**
     * Actualiza el estado de una sala a "presentada" (estado = 1)
     */
    /**
     * Busca una sala por su código y carga sus preguntas USANDO TU CONSTRUCTOR:
     * Sala(int codigoSala, String nombreSala, boolean estado, int
     * cantidadJugadores)
     */
    public Sala buscarSalaPorCodigo(int codigoSala) {
        String sql = "SELECT s.codigoSala, s.nombreSala, s.cantidadJugadore, s.estado, "
                + "u.nombreUsuario, u.idusuarios, u.correo "
                + "FROM sala s "
                + "INNER JOIN usuarios u ON s.fk_idUsuario = u.idusuarios "
                + "WHERE s.codigoSala = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, codigoSala);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Usando tu constructor de 4 parámetros
                Sala sala = new Sala(
                        rs.getInt("codigoSala"),
                        rs.getString("nombreSala"),
                        rs.getBoolean("estado"),
                        rs.getInt("cantidadJugadore")
                );

                // Crear el propietario
                Usuario propietario = new Usuario(
                        rs.getInt("idusuarios"),
                        rs.getString("nombreUsuario"),
                        rs.getString("correo"),
                        "", // contraseña no la cargamos por seguridad
                        0.0
                );
                sala.setPropietario(propietario);

                // Cargar las preguntas de la sala
                ArrayList<Preguntas> preguntas = obtenerPreguntasDeSala(codigoSala);
                sala.setListaPreguntas(preguntas);

                return sala;
            }
        } catch (Exception e) {
            System.out.println("Error al buscar sala por código: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean guardarPregunta(String enunciado, String respuesta1, String respuesta2,
            String respuesta3, String respuesta4, int codigoSala) {
        String sql = "INSERT INTO preguntas (enunciado, respuesta1, respuesta2, respuesta3, respuesta4, codigoSala) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, enunciado);
            ps.setString(2, respuesta1);
            ps.setString(3, respuesta2);
            ps.setString(4, respuesta3);
            ps.setString(5, respuesta4);
            ps.setInt(6, codigoSala);

            int filas = ps.executeUpdate();
            System.out.println("Pregunta guardada en BD. Filas afectadas: " + filas);
            return filas > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar pregunta: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todas las preguntas de una sala específica USANDO TUS MODELOS:
     * Preguntas, Respuestas
     */
    /**
     * Obtiene todas las preguntas de una sala específica USANDO TUS MODELOS:
     * Preguntas, Respuestas
     */
    public ArrayList<Preguntas> obtenerPreguntasDeSala(int codigoSala) {
        ArrayList<Preguntas> preguntas = new ArrayList<>();
        // Solo seleccionamos las columnas que EXISTEN en tu tabla
        String sql = "SELECT idpreguntas, enunciado, respuesta1, respuesta2, respuesta3, respuesta4 "
                + "FROM preguntas WHERE codigoSala = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, codigoSala);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Preguntas pregunta = new Preguntas();
                pregunta.setEnunciado(rs.getString("enunciado"));
                pregunta.setCodigoSala(codigoSala);

                // Crear lista de respuestas usando tu modelo Respuestas
                ArrayList<Respuestas> respuestas = new ArrayList<>();
                respuestas.add(new Respuestas(1, rs.getString("respuesta1"), false));
                respuestas.add(new Respuestas(2, rs.getString("respuesta2"), false));
                respuestas.add(new Respuestas(3, rs.getString("respuesta3"), false));
                respuestas.add(new Respuestas(4, rs.getString("respuesta4"), false));

                pregunta.setArregloDeRespuestasParaPreguntas(respuestas);
                preguntas.add(pregunta);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener preguntas de sala: " + e.getMessage());
            e.printStackTrace();
        }
        return preguntas;
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión de SalaDAO cerrada");
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar conexión de SalaDAO: " + e.getMessage());
        }
    }
}
