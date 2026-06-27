/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controlador;

import DAO.SalaDAO;
import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import Modelo.Usuario;
import java.util.ArrayList;
import java.util.Iterator;
import servidor_cliente.Servidor;

/**
 *
 * @author sronn
 */
public class GestorSala {

    private SalaDAO salaDao;

    public GestorSala() {
        this.salaDao = new SalaDAO();
    }

    public GestorSala(SalaDAO salaDao) {
        this.salaDao = salaDao;
    }

    public boolean guardarSala(Sala sala, String nombreUsuario) {

        return salaDao.guardarSala(
                String.valueOf(sala.getCodigoSala()),
                sala.getNombreSala(),
                sala.getCantidadJugadores(),
                nombreUsuario
        );
    }

    public boolean presentarSala(int codigoSala) {
        return salaDao.presentarSala(codigoSala);
    }

    public ArrayList<Sala> consultarSalasDeUsuario(
            String nombreUsuario) {

        return salaDao.consultarSalasDeUsuario(
                nombreUsuario
        );
    }

    public Sala buscarSala(int codigoSala) {
        // Obtenemos la lista de todas las salas en memoria
        ArrayList<Sala> salas = Servidor.juego.getArrayDeSalas();

        // Recorremos cada sala comparando su código
        for (int i = 0; i < salas.size(); i++) {
            Sala s = salas.get(i);
            if (s.getCodigoSala() == codigoSala) {
                return s; // Encontramos la sala, la devolvemos
            }
        }

        // Si llegamos aquí, no se encontró la sala
        return null;
    }


    public void agregarOActualizarSala(Sala sala) {
        ArrayList<Sala> salas = Servidor.juego.getArrayDeSalas();
        boolean existe = false;

        // Buscamos si la sala ya existe en memoria
        for (int i = 0; i < salas.size(); i++) {
            Sala s = salas.get(i);
            if (s.getCodigoSala() == sala.getCodigoSala()) {
                // La sala ya existe: actualizamos sus datos
                s.setListaPreguntas(sala.getListaPreguntas());
                s.setNombreSala(sala.getNombreSala());
                s.setCantidadJugadores(sala.getCantidadJugadores());
                s.setArrayDeUsuarios(sala.getArrayDeUsuarios());
                s.setPartidaIniciada(sala.isPartidaIniciada());
                existe = true;
                System.out.println("Sala " + sala.getCodigoSala() + " actualizada en memoria");
                break; // Salimos del bucle, ya encontramos la sala
            }
        }

        // Si la sala no existía, la agregamos como nueva
        if (!existe) {
            salas.add(sala);
            System.out.println("Sala agregada a memoria: " + sala.getCodigoSala());
        }
    }


    public boolean agregarJugadorASala(int codigoSala, String nombreJugador) {
        // Primero buscamos la sala
        Sala sala = buscarSala(codigoSala);
        if (sala == null) {
            return false; // La sala no existe
        }

        // Verificamos si el jugador ya está en la sala
        ArrayList<Usuario> jugadores = sala.getArrayDeUsuarios();
        for (int i = 0; i < jugadores.size(); i++) {
            Usuario u = jugadores.get(i);
            if (u.getNombreUsuario().equalsIgnoreCase(nombreJugador)) {
                return false; // El jugador ya existe en esta sala
            }
        }

        // El jugador no existe, lo creamos y agregamos
        // Usuario(id, nombre, correo, contraseña, puntajeAcumulado)
        Usuario jugador = new Usuario(0, nombreJugador, "", "", 0.0);
        sala.agregarJugador(jugador);
        System.out.println("Jugador " + nombreJugador + " agregado a sala " + codigoSala);
        return true;
    }


    public void removerJugadorDeSala(int codigoSala, String nombreJugador) {
        Sala sala = buscarSala(codigoSala);
        if (sala == null) {
            return; // La sala no existe, no hay nada que hacer
        }

        // Obtenemos la lista de jugadores
        ArrayList<Usuario> jugadores = sala.getArrayDeUsuarios();

        // Recorremos la lista con un iterador para poder eliminar mientras iteramos
        Iterator<Usuario> iterador = jugadores.iterator();
        while (iterador.hasNext()) {
            Usuario u = iterador.next();
            if (u.getNombreUsuario().equals(nombreJugador)) {
                iterador.remove(); // Eliminamos al jugador de forma segura
                System.out.println("Jugador " + nombreJugador + " removido de sala " + codigoSala);
                break; // Solo removemos la primera coincidencia
            }
        }
    }


    public void cerrarSala(int codigoSala) {
        // Notificamos a todos los clientes en la sala que se va a cerrar
        Servidor.enviarASala(codigoSala, "SALA_CERRADA");

        // Eliminamos la entrada del mapa clientesPorSala
        Servidor.clientesPorSala.remove(codigoSala);

        // Eliminamos la sala de la lista de salas del juego
        ArrayList<Sala> salas = Servidor.juego.getArrayDeSalas();
        Iterator<Sala> iterador = salas.iterator();
        while (iterador.hasNext()) {
            Sala s = iterador.next();
            if (s.getCodigoSala() == codigoSala) {
                iterador.remove(); // Eliminamos la sala de forma segura
                break;
            }
        }

        System.out.println("Sala " + codigoSala + " cerrada y eliminada");
    }


    public String obtenerListaJugadoresFormateada(int codigoSala) {
        Sala sala = buscarSala(codigoSala);
        if (sala == null) {
            return ""; // Sala no encontrada
        }

        ArrayList<Usuario> jugadores = sala.getArrayDeUsuarios();

        // Si no hay jugadores, devolvemos el prefijo vacío
        if (jugadores.isEmpty()) {
            return "JUGADORES|";
        }

        // Construimos la lista de nombres separados por comas
        StringBuilder respuesta = new StringBuilder("JUGADORES|");
        for (int i = 0; i < jugadores.size(); i++) {
            respuesta.append(jugadores.get(i).getNombreUsuario());
            // Agregamos coma entre nombres, pero no después del último
            if (i < jugadores.size() - 1) {
                respuesta.append(",");
            }
        }

        return respuesta.toString();
    }

 
    public String formatearPreguntasParaEnvio(int codigoSala) {
        Sala sala = buscarSala(codigoSala);

        // Verificamos que la sala y sus preguntas existan
        if (sala == null || sala.getListaPreguntas() == null) {
            return null;
        }

        StringBuilder respuesta = new StringBuilder("PREGUNTAS|");
        ArrayList<Preguntas> preguntas = sala.getListaPreguntas();

        // Recorremos cada pregunta
        for (int i = 0; i < preguntas.size(); i++) {
            Preguntas p = preguntas.get(i);
            ArrayList<Respuestas> respuestasValidas = new ArrayList<>();
            int correcta = 0;

            // Agregamos el enunciado de la pregunta
            String tipo = p.getTipoDePregunta();
            if (tipo == null || tipo.trim().isEmpty()) {
                tipo = "Quiz";
            }

            // Agregamos las respuestas si existen
            if (p.getArregloDeRespuestasParaPreguntas() != null) {
                ArrayList<Respuestas> respuestas = p.getArregloDeRespuestasParaPreguntas();
                for (int j = 0; j < respuestas.size(); j++) {
                    Respuestas r = respuestas.get(j);
                    if (r.getRespuestas() != null && !r.getRespuestas().trim().isEmpty()) {
                        respuestasValidas.add(r);
                        if (r.isCorrecta()) {
                            correcta = respuestasValidas.size();
                        }
                    }
                }
            }

            // Separador entre preguntas (excepto después de la última)
            if (tipo.equals("Quiz") && respuestasValidas.size() <= 2) {
                tipo = "Verdadero O Falso";
            }

            respuesta.append(p.getEnunciado()).append(",")
                    .append(tipo).append(",")
                    .append(p.getTiempoParaLasPreguntas() > 0 ? p.getTiempoParaLasPreguntas() : 20).append(",")
                    .append(p.getValorPuntosPreguntas() > 0 ? p.getValorPuntosPreguntas() : 10).append(",")
                    .append(correcta);

            for (Respuestas r : respuestasValidas) {
                respuesta.append(",").append(r.getRespuestas());
            }

            if (i < preguntas.size() - 1) {
                respuesta.append(";");
            }
        }

        return respuesta.toString();
    }


    public void iniciarJuego(int codigoSala) {
        Sala sala = buscarSala(codigoSala);
        if (sala != null) {
            // Marcamos la sala como iniciada
            sala.setPartidaIniciada(true);

            // Notificamos a todos los clientes en la sala que el juego comenzó
            Servidor.enviarASala(codigoSala, "INICIO_PARTIDA");

            System.out.println("Juego iniciado en sala " + codigoSala);
        }
    }
}
