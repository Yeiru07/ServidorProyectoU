package DAO;

import Modelo.Usuario;
import MySQL.ConexionBaseDeDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UsuarioDAO {

    private Connection conexion;

    public UsuarioDAO() {
        this.conexion = ConexionBaseDeDatos.conectar();
    }

    /**
     * Registra un nuevo usuario en la base de datos USANDO TU CONSTRUCTOR:
     * Usuario(int idUsuario, String nombreUsuario, String correo, String
     * contraseña, double puntuajeAcumulado)
     */
    public boolean registrarUsuario(String nombreUsuario, String correo, String contraseña) {
        // Primero verificamos si el usuario o correo ya existen
        if (existeUsuario(nombreUsuario) || existeCorreo(correo)) {
            System.out.println("El usuario o correo ya existe");
            return false;
        }

        String sql = "INSERT INTO usuarios (nombreUsuario, correo, contraseña) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, correo);
            ps.setString(3, contraseña); // Idealmente deberías hashear la contraseña

            int filas = ps.executeUpdate();
            System.out.println("Usuario registrado. Filas afectadas: " + filas);
            return filas > 0;

        } catch (Exception e) {
            System.out.println("Error al registrar usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica las credenciales de login
     */
    public boolean verificarLogin(String nombreUsuario, String contraseña) {
        String sql = "SELECT idusuarios FROM usuarios WHERE nombreUsuario = ? AND contraseña = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contraseña);
            ResultSet rs = ps.executeQuery();

            boolean loginCorrecto = rs.next();
            if (loginCorrecto) {
                System.out.println("Login exitoso para: " + nombreUsuario);
            } else {
                System.out.println("Login fallido para: " + nombreUsuario);
            }
            return loginCorrecto;

        } catch (Exception e) {
            System.out.println("Error al verificar login: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un usuario por su nombre USANDO TU CONSTRUCTOR: Usuario(int
     * idUsuario, String nombreUsuario, String correo, String contraseña, double
     * puntuajeAcumulado)
     */
    public Usuario buscarUsuarioPorNombre(String nombreUsuario) {
        String sql = "SELECT idusuarios, nombreUsuario, correo, contraseña FROM usuarios WHERE nombreUsuario = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Usando tu constructor exactamente
                Usuario usuario = new Usuario(
                        rs.getInt("idusuarios"),
                        rs.getString("nombreUsuario"),
                        rs.getString("correo"),
                        rs.getString("contraseña"),
                        0.0 // puntuajeAcumulado inicia en 0
                );
                return usuario;
            }
        } catch (Exception e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca un usuario por su ID USANDO TU CONSTRUCTOR: Usuario(int idUsuario,
     * String nombreUsuario, String correo, String contraseña, double
     * puntuajeAcumulado)
     */
    public Usuario buscarUsuarioPorId(int idUsuario) {
        String sql = "SELECT idusuarios, nombreUsuario, correo, contraseña FROM usuarios WHERE idusuarios = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Usando tu constructor exactamente
                Usuario usuario = new Usuario(
                        rs.getInt("idusuarios"),
                        rs.getString("nombreUsuario"),
                        rs.getString("correo"),
                        rs.getString("contraseña"),
                        0.0
                );
                return usuario;
            }
        } catch (Exception e) {
            System.out.println("Error al buscar usuario por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza el puntaje acumulado de un usuario
     */
    public boolean actualizarPuntaje(String nombreUsuario, double puntaje) {
        String sql = "UPDATE usuarios SET puntuajeAcumulado = puntuajeAcumulado + ? WHERE nombreUsuario = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setDouble(1, puntaje);
            ps.setString(2, nombreUsuario);

            int filas = ps.executeUpdate();
            System.out.println("Puntaje actualizado para " + nombreUsuario + ". Filas: " + filas);
            return filas > 0;

        } catch (Exception e) {
            System.out.println("Error al actualizar puntaje: " + e.getMessage());
            return false;
        }
    }

    // ==================== MÉTODOS PRIVADOS DE VALIDACIÓN ====================
    private boolean existeUsuario(String nombreUsuario) {
        String sql = "SELECT idusuarios FROM usuarios WHERE nombreUsuario = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error al verificar usuario: " + e.getMessage());
            return true; // Por seguridad, asumimos que existe
        }
    }

    private boolean existeCorreo(String correo) {
        String sql = "SELECT idusuarios FROM usuarios WHERE correo = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, correo);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error al verificar correo: " + e.getMessage());
            return true; // Por seguridad, asumimos que existe
        }
    }

    /**
     * Cierra la conexión a la base de datos
     */
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión de UsuarioDAO cerrada");
            }
        } catch (Exception e) {
            System.out.println("Error al cerrar conexión de UsuarioDAO: " + e.getMessage());
        }
    }
}
