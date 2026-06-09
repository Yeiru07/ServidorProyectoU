/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author sronn
 */
import Modelo.Usuario;
import Modelo.Sala;
import MySQL.ConexionBaseDeDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;// Agregado para el procedimiento almacenado
import java.sql.ResultSet;
import java.util.ArrayList;
import servidor_cliente.Servidor;

public class GestorUsuarios {

    // Lista en memoria para saber quienes estan jugando/conectados actualmente
    private ArrayList<Usuario> usuariosConectados = new ArrayList<>();

    //MÉTODO PARA REGISTRAR USUARIO EN LA BASE DE DATOS
    public boolean registrarUsuarioEnBD(String nombre, String correo, String contra) {
        String sql = "INSERT INTO usuarios (nombreUsuario, correo, contraseña, puntuajeAcumulado) VALUES (?, ?, ?, 0.0)";

        try (Connection conexion = ConexionBaseDeDatos.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, correo);
            ps.setString(3, contra);

            int filas = ps.executeUpdate();
            return filas > 0; // Si afectó filas, el registro fue exitoso

        } catch (Exception e) {
            System.out.println("Error SQL al registrar: " + e.getMessage());
            return false; // Devuelve false si el usuario o correo ya existen (duplicados)
        }
    }

    //MÉTODO PARA VERIFICAR LOGIN EN LA BASE DE DATOS
    public boolean verificarLoginEnBD(String nombre, String contra) {
        String sql = "SELECT * FROM usuarios WHERE nombreUsuario = ? AND contraseña = ?";

        try (Connection conexion = ConexionBaseDeDatos.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, contra);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // True si encontró al usuario con esa contraseña
            }

        } catch (Exception e) {
            System.out.println("Error SQL en login: " + e.getMessage());
            return false;
        }
    }

    //MÉTODO PARA LLAMAR AL PROCEDIMIENTO ALMACENADO CON INNER JOIN
    public ArrayList<Sala> consultarSalasDeUsuario(String nombreUsuario) {
        ArrayList<Sala> listaSalas = new ArrayList<>();

        //Se llama el procedimiento almacenado
        String sql = "{call ObtenerSalasDeUsuario(?)}";

        try (Connection conexion = ConexionBaseDeDatos.conectar(); CallableStatement cs = conexion.prepareCall(sql)) {

            // Asignamos el parámetro de entrada (IN) con el nombre que viene de JavaFX
            cs.setString(1, nombreUsuario);

            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    int codigo = rs.getInt("codigoSala");
                    String nombre = rs.getString("nombreSala");
                    int jugadores = rs.getInt("cantidadJugadore");
                    // Reconstruimos la instancia del modelo Sala en memoria.
                    Sala sala = new Sala(codigo, nombre, true, jugadores);
                    listaSalas.add(sala);
                }
            }
        } catch (Exception e) {
            System.out.println("Error SQL al ejecutar el procedimiento ObtenerSalasDeUsuario: " + e.getMessage());
        }

        return listaSalas;
    }

    public Sala buscarSalaPorCodigo(int codigoSala) {
        String sql = "SELECT * FROM sala WHERE codigoSala = ?";
        try (
                Connection conexion = ConexionBaseDeDatos.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, codigoSala);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Sala(
                        rs.getInt("codigoSala"),
                        rs.getString("nombreSala"),
                        rs.getBoolean("estado"),
                        rs.getInt("cantidadJugadore")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Sala buscarSalaMemoria(int codigoSala) {

        for (Sala s : Servidor.juego.getArrayDeSalas()) {
            System.out.println("Sala en memoria =" + s.getCodigoSala());
        }
        for (Sala sala : Servidor.juego.getArrayDeSalas()) {
            if (sala.getCodigoSala() == codigoSala) {
                return sala;
            }
        }

        return null;
    }

    // 4. MÉTODOS PARA CONTROLAR LOGUEADOS EN VIVO (Para las salas del juego)
    public void agregarUsuarioActivo(Usuario usuario) {
        usuariosConectados.add(usuario);
    }

    public void removerUsuarioActivo(Usuario usuario) {
        usuariosConectados.remove(usuario);
    }
}
