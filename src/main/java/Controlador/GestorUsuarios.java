/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

/**
 *
 * @author sronn
 */
import DAO.UsuarioDAO;
import Modelo.Usuario;
import Modelo.Sala;
import MySQL.ConexionBaseDeDatos;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;// Agregado para el procedimiento almacenado
import java.sql.ResultSet;
import java.util.ArrayList;
import Modelo.Preguntas;
import Modelo.Respuestas;
import servidor_cliente.Servidor;

public class GestorUsuarios {

    // Lista en memoria para saber quienes estan jugando/conectados actualmente
    UsuarioDAO usuarioDAO;
    private ArrayList<Usuario> usuariosConectados;

    public GestorUsuarios() {
        this.usuarioDAO = new UsuarioDAO();
        this.usuariosConectados = new ArrayList<>();
    }

    public boolean registrarUsuario(Usuario usuario) {

        return usuarioDAO.registrarUsuario(usuario);

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

    public ArrayList<Preguntas> obtenerPreguntasSala(int codigoSala) {

        ArrayList<Preguntas> preguntas = new ArrayList<>();

        String sql = "SELECT * FROM preguntas WHERE codigoSala=?";

        try (Connection conexion = ConexionBaseDeDatos.conectar(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, codigoSala);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Preguntas p = new Preguntas();

                p.setEnunciado(rs.getString("enunciado"));

                ArrayList<Respuestas> respuestas = new ArrayList<>();

                respuestas.add(new Respuestas(1, rs.getString("respuesta1"), false));

                respuestas.add(new Respuestas(2, rs.getString("respuesta2"), false));

                respuestas.add(new Respuestas(3, rs.getString("respuesta3"), false));

                respuestas.add(new Respuestas(4, rs.getString("respuesta4"), false));

                p.setArregloDeRespuestasParaPreguntas(respuestas);
                preguntas.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return preguntas;
    }

    // 4. MÉTODOS PARA CONTROLAR LOGUEADOS EN VIVO (Para las salas del juego)
    public void agregarUsuarioActivo(Usuario usuario) {
        usuariosConectados.add(usuario);
    }

    public void removerUsuarioActivo(Usuario usuario) {
        usuariosConectados.remove(usuario);
    }
}
