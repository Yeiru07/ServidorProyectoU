package DAO;

import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import Modelo.Usuario;
import MySQL.ConexionBaseDeDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SalaDAO {

    private Connection conexion;

    public SalaDAO() {
        this.conexion = ConexionBaseDeDatos.conectar();
    }

    public SalaDAO(Connection conexion) {
        this.conexion = conexion;
    }

    // ==================== MÉTODOS PARA SALAS ====================
    /**
     * Guarda una nueva sala en la base de datos
     */
    public boolean guardarSala(String codigoSala, String nombreSala, int cantidadJugadores, String nombreUsuario) {
        String sql = "INSERT INTO sala (codigoSala, nombreSala, cantidadJugadore, fk_idUsuario) "
                + "VALUES (?, ?, ?, (SELECT idusuarios FROM usuarios WHERE nombreUsuario = ? LIMIT 1))";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, codigoSala);
            ps.setString(2, nombreSala);
            ps.setInt(3, cantidadJugadores);
            ps.setString(4, nombreUsuario);

            int filas = ps.executeUpdate();
            System.out.println("Sala guardada en BD. Filas afectadas: " + filas);
            return filas > 0;

        } catch (Exception e) {
            System.out.println("Error al guardar sala: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el estado de una sala a "presentada" (estado = 1)
     */
    public boolean presentarSala(int codigoSala) {
        String sql = "UPDATE sala SET estado = 1 WHERE codigoSala = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, codigoSala);
            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Sala " + codigoSala + " presentada correctamente");
                return true;
            } else {
                System.out.println("No existe la sala " + codigoSala + " en la BD");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Error al presentar sala: " + e.getMessage());
            return false;
        }
    }

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

    /**
     * Consulta todas las salas creadas por un usuario específico USANDO TU
     * CONSTRUCTOR: Sala(int codigoSala, String nombreSala, boolean estado, int
     * cantidadJugadores)
     */
    public ArrayList<Sala> consultarSalasDeUsuario(String nombreUsuario) {
        ArrayList<Sala> salas = new ArrayList<>();
        String sql = "SELECT s.codigoSala, s.nombreSala, s.cantidadJugadore, s.estado "
                + "FROM sala s "
                + "INNER JOIN usuarios u ON s.fk_idUsuario = u.idusuarios "
                + "WHERE u.nombreUsuario = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Usando tu constructor de 4 parámetros
                Sala sala = new Sala(
                        rs.getInt("codigoSala"),
                        rs.getString("nombreSala"),
                        rs.getBoolean("estado"),
                        rs.getInt("cantidadJugadore")
                );
                salas.add(sala);
            }
        } catch (Exception e) {
            System.out.println("Error al consultar salas del usuario: " + e.getMessage());
        }
        return salas;
    }

    // ==================== MÉTODOS PARA PREGUNTAS ====================
    /**
     * Guarda una pregunta en la base de datos
     */
    /**
     * Guarda una pregunta en la base de datos, incluyendo cuál es la correcta
     */
    /**
     * Guarda una pregunta en la base de datos con su respuesta correcta.
     */
    public boolean guardarPregunta(String enunciado, String respuesta1, String respuesta2,
            String respuesta3, String respuesta4, int codigoSala, int respuestaCorrecta) {
        String sql = "INSERT INTO preguntas (enunciado, respuesta1, respuesta2, respuesta3, respuesta4, codigoSala, respuestaCorrecta) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, enunciado);
            ps.setString(2, respuesta1);
            ps.setString(3, respuesta2);
            ps.setString(4, respuesta3);
            ps.setString(5, respuesta4);
            ps.setInt(6, codigoSala);
            ps.setInt(7, respuestaCorrecta);

            int filas = ps.executeUpdate();
            System.out.println("Pregunta guardada en BD. Correcta: " + respuestaCorrecta);
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
    /**
     * Obtiene todas las preguntas de una sala especifica con su respuesta
     * correcta.
     */
    public ArrayList<Preguntas> obtenerPreguntasDeSala(int codigoSala) {
        ArrayList<Preguntas> preguntas = new ArrayList<>();
        String sql = "SELECT enunciado, respuesta1, respuesta2, respuesta3, respuesta4, respuestaCorrecta "
                + "FROM preguntas WHERE codigoSala = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, codigoSala);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Preguntas pregunta = new Preguntas();
                pregunta.setEnunciado(rs.getString("enunciado"));
                pregunta.setCodigoSala(codigoSala);
                pregunta.setTiempoParaLasPreguntas(20);
                pregunta.setValorPuntosPreguntas(10);

                // Obtenemos el número de la respuesta correcta (1, 2, 3 o 4)
                int correcta = rs.getInt("respuestaCorrecta");

                // Creamos las respuestas. Solo la que coincide con 'correcta' tendrá true
                ArrayList<Respuestas> respuestas = new ArrayList<>();
                respuestas.add(new Respuestas(1, rs.getString("respuesta1"), (correcta == 1)));
                respuestas.add(new Respuestas(2, rs.getString("respuesta2"), (correcta == 2)));
                respuestas.add(new Respuestas(3, rs.getString("respuesta3"), (correcta == 3)));
                respuestas.add(new Respuestas(4, rs.getString("respuesta4"), (correcta == 4)));

                int respuestasConTexto = 0;
                for (Respuestas respuesta : respuestas) {
                    if (respuesta.getRespuestas() != null
                            && !respuesta.getRespuestas().trim().isEmpty()) {
                        respuestasConTexto++;
                    }
                }
                pregunta.setTipoDePregunta(respuestasConTexto <= 2 ? "Verdadero O Falso" : "Quiz");

                pregunta.setArregloDeRespuestasParaPreguntas(respuestas);
                preguntas.add(pregunta);

                System.out.println("Pregunta cargada de BD: " + rs.getString("enunciado") + " | Correcta: " + correcta);
            }
        } catch (Exception e) {
            System.out.println("Error al obtener preguntas de sala: " + e.getMessage());
            e.printStackTrace();
        }
        return preguntas;
    }

    public boolean actualizarPregunta(int idPregunta, String enunciado, String respuesta1, String respuesta2,
            String respuesta3, String respuesta4, int respuestaCorrecta) {
        String sql = "UPDATE preguntas SET enunciado = ?, respuesta1 = ?, respuesta2 = ?, "
                + "respuesta3 = ?, respuesta4 = ?, respuestaCorrecta = ? WHERE idpreguntas = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, enunciado);
            ps.setString(2, respuesta1);
            ps.setString(3, respuesta2);
            ps.setString(4, respuesta3);
            ps.setString(5, respuesta4);
            ps.setInt(6, respuestaCorrecta);
            ps.setInt(7, idPregunta);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al actualizar pregunta: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarPregunta(int idPregunta) {
        String sql = "DELETE FROM preguntas WHERE idpreguntas = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idPregunta);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al eliminar pregunta: " + e.getMessage());
            return false;
        }
    }

    public int crearPartida(int codigoSala) {
        String sql = "INSERT INTO partidas (fk_codigoSala) VALUES (?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, String.valueOf(codigoSala));
            if (ps.executeUpdate() == 0) {
                return 0;
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al crear partida: " + e.getMessage());
        }
        return 0;
    }

    public boolean guardarRanking(int idPartida, int idUsuario, int posicion, int puntaje) {
        String sqlCompleto = "INSERT INTO ranking (fk_idUsuarios, fk_idPartida, posicion, puntaje) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sqlCompleto)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idPartida);
            ps.setInt(3, posicion);
            ps.setInt(4, puntaje);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Ranking extendido no disponible, usando formato basico: " + e.getMessage());
        }

        String sql = "INSERT INTO ranking (fk_idUsuarios, fk_idPartida) VALUES (?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idPartida);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Error al guardar ranking: " + e.getMessage());
            return false;
        }
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
