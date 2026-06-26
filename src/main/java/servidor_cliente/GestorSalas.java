package servidor_cliente;

import Modelo.Preguntas;
import Modelo.Respuestas;
import Modelo.Sala;
import Modelo.Usuario;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Clase responsable de gestionar las salas en memoria.
 *
 * Esta clase centraliza toda la lógica de negocio relacionada con las salas: -
 * Búsqueda de salas - Agregar/actualizar salas - Gestión de jugadores dentro de
 * las salas - Formateo de datos para envío por socket - Control del ciclo de
 * vida de las salas (creación, inicio, cierre)
 */
public class GestorSalas {

    /**
     * Busca una sala por su código en la lista de salas del juego.
     *
     * Recorre todas las salas almacenadas en memoria
     * (Servidor.juego.getArrayDeSalas()) comparando el código de cada una con
     * el código buscado.
     *
     * codigoSala El código único de la sala a buscar
     *  La sala encontrada, o null si no existe ninguna sala con ese
     * código
     */
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

    /**
     * Agrega una sala a la memoria si no existe, o actualiza sus datos si ya
     * existe.
     *
     * Este método es útil cuando se presenta una sala: si es la primera vez, se
     * agrega a la lista; si ya estaba, se actualizan sus preguntas y datos.
     *
     * sala La sala a agregar o actualizar
     */
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

    /**
     * Agrega un jugador a una sala si no existe previamente.
     *
     * Verifica que la sala exista y que el jugador no esté ya registrado en esa
     * sala antes de agregarlo.
     *
     * codigoSala El código de la sala donde se agregará el jugador
     * nombreJugador El nombre del jugador a agregar
     *  true si el jugador fue agregado exitosamente, false si ya existía
     * o la sala no existe
     */
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

    /**
     * Remueve un jugador de una sala por su nombre.
     *
     * Busca la sala por código y elimina al jugador que coincida con el nombre.
     * Se usa cuando un jugador se desconecta o abandona la sala.
     *
     * El código de la sala de donde se removerá el jugador
     * nombreJugador El nombre del jugador a remover
     */
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

    /**
     * Cierra y elimina completamente una sala del sistema.
     *
     * Este método: 1. Envía un mensaje "SALA_CERRADA" a todos los clientes en
     * la sala 2. Elimina la sala del mapa de clientes por sala 3. Elimina la
     * sala de la lista de salas del juego
     *
     * codigoSala El código de la sala a cerrar
     */
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

    /**
     * Obtiene la lista de jugadores de una sala formateada para envío por
     * socket.
     *
     * Genera un string con el formato: "JUGADORES|nombre1,nombre2,nombre3" Si
     * no hay jugadores, devuelve: "JUGADORES|"
     *
     *  codigosala El código de la sala
     *  String formateado con la lista de jugadores
     */
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

    /**
     * Formatea las preguntas de una sala para enviarlas por socket.
     *
     * Genera un string con el formato:
     * "PREGUNTAS|enunciado1,resp1,resp2,resp3,resp4;enunciado2,resp1,resp2,..."
     *
     * Cada pregunta se separa con ";" y las respuestas de cada pregunta con ","
     *
     * codigoSala El código de la sala
     * String formateado con las preguntas, o null si no hay preguntas
     */
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

            // Agregamos el enunciado de la pregunta
            respuesta.append(p.getEnunciado());

            // Agregamos las respuestas si existen
            if (p.getArregloDeRespuestasParaPreguntas() != null) {
                ArrayList<Respuestas> respuestas = p.getArregloDeRespuestasParaPreguntas();
                for (int j = 0; j < respuestas.size(); j++) {
                    Respuestas r = respuestas.get(j);
                    respuesta.append(",").append(r.getRespuestas());
                }
            }

            // Separador entre preguntas (excepto después de la última)
            if (i < preguntas.size() - 1) {
                respuesta.append(";");
            }
        }

        return respuesta.toString();
    }

    /**
     * Inicia el juego en una sala específica.
     *
     * Marca la sala como "partida iniciada" y envía el mensaje "INICIO_PARTIDA"
     * a todos los clientes conectados a esa sala.
     *
     * codigoSala El código de la sala donde se iniciará el juego
     */
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
