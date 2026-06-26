package servidor_cliente;

import Modelo.Sala;
import Modelo.Usuario;
import DAO.SalaDAO;
import DAO.UsuarioDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Clase que maneja la comunicacion con un cliente conectado al servidor.
 *
 * Cada instancia de esta clase se ejecuta en un hilo separado y gestiona todas
 * las peticiones que un cliente envia a traves del socket.
 *
 * Responsabilidades: - Recibir y procesar tramas del cliente - Delegar
 * operaciones de base de datos a los DAOs - Delegar gestion de salas en memoria
 * al GestorSalas - Validar tramas antes de procesarlas - Mantener el estado del
 * cliente (sala actual, nombre de usuario)
 */
public class ManejadorDeUsuarios extends Thread {

    // Socket para la comunicacion con el cliente
    private Socket socketCliente;

    // DAOs para acceso a base de datos
    private SalaDAO salaDAO;
    private UsuarioDAO usuarioDAO;

    // Gestores de logica de negocio
    private GestorSalas gestorSalas;
    private ValidadorTramas validador;

    // Estado actual del cliente
    private int codigoSalaActual = 0;     // 0 significa que no esta en ninguna sala
    private String nombreUsuario = "";     // Nombre del usuario autenticado

    // Canal de salida para enviar respuestas al cliente
    private PrintWriter escritor;

    /**
     * Constructor que inicializa el manejador con el socket del cliente. Crea
     * las instancias necesarias de DAOs y gestores.
     *
     * socketCliente Socket conectado al cliente
     */
    public ManejadorDeUsuarios(Socket socketCliente) {
        this.socketCliente = socketCliente;
        this.salaDAO = new SalaDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.gestorSalas = new GestorSalas();
        this.validador = new ValidadorTramas();
    }

    /**
     * Metodo principal del hilo que se ejecuta cuando el cliente se conecta.
     *
     * Flujo: 1. Configura los canales de entrada/salida 2. Registra al cliente
     * en la lista global 3. Entra en un bucle infinito leyendo tramas del
     * cliente 4. Procesa cada trama recibida 5. Cuando el cliente se
     * desconecta, limpia los recursos
     */
    public void run() {
        BufferedReader lector = null;

        try {
            // Configuramos el canal de lectura desde el socket
            lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));

            // Configuramos el canal de escritura hacia el socket (autoflush activado)
            escritor = new PrintWriter(socketCliente.getOutputStream(), true);

            // Agregamos este escritor a la lista global de clientes
            Servidor.clientes.add(escritor);
            System.out.println("CLIENTE AGREGADO. TOTAL = " + Servidor.clientes.size());

            // Bucle principal: lee tramas del cliente mientras este conectado
            String datosRecibidos;
            while ((datosRecibidos = lector.readLine()) != null) {
                procesarTrama(datosRecibidos);
            }
        } catch (Exception e) {
            System.out.println("Error en comunicacion: " + e.getMessage());
        } finally {
            // Cuando el cliente se desconecta (readLine devuelve null o hay error)
            desconectarCliente();
        }
    }

    /**
     * Procesa una trama recibida del cliente.
     *
     * Divide la trama por el separador "|" y dirige el comando al metodo
     * manejador correspondiente segun el primer elemento.
     *
     * Formato de trama: COMANDO|parametro1|parametro2|...
     *
     * datosRecibidos La trama completa recibida del cliente
     */
    private void procesarTrama(String datosRecibidos) {
        System.out.println("TRAMA RECIBIDA: " + datosRecibidos);

        // Dividimos la trama usando "|" como separador
        // El -1 asegura que se mantengan los campos vacios al final
        String[] partes = datosRecibidos.split("\\|", -1);

        // Verificamos que la trama no este vacia
        if (partes.length == 0) {
            escritor.println("ERROR|Trama vacia");
            return;
        }

        try {
            // El primer elemento indica el comando a ejecutar
            switch (partes[0]) {
                case "REGISTRO":
                    manejarRegistro(partes);
                    break;
                case "LOGIN":
                    manejarLogin(partes);
                    break;
                case "Pregunta":
                    manejarPregunta(partes);
                    break;
                case "Sala":
                    manejarSala(partes);
                    break;
                case "PRESENTAR":
                    manejarPresentar(partes);
                    break;
                case "CONSULTAR_SALAS":
                    manejarConsultarSalas(partes);
                    break;
                case "OBTENER_PREGUNTAS":
                    manejarObtenerPreguntas(partes);
                    break;
                case "INICIAR_JUEGO":
                    manejarIniciarJuego(partes);
                    break;
                case "UNIR_SALA":
                    manejarUnirSala(partes);
                    break;
                case "CERRAR_SALA":
                    manejarCerrarSala(partes);
                    break;
                default:
                    escritor.println("ERROR|Comando no reconocido: " + partes[0]);
                    break;
            }
        } catch (Exception e) {
            escritor.println("ERROR|Error procesando comando");
            e.printStackTrace();
        }
    }

    // ==================== MANEJADORES DE COMANDOS ====================
    /**
     * Maneja el comando REGISTRO.
     *
     * Formato esperado: REGISTRO|nombreUsuario|correo|contrasena
     *
     * Valida los datos, intenta registrar al usuario en la base de datos y
     * envia la respuesta correspondiente al cliente.
     *
     * partes Array con las partes de la trama
     */
    private void manejarRegistro(String[] partes) {
        // Validamos que la trama tenga el formato correcto
        // AHORA USAMOS ResultadoValidacion directamente (sin ValidadorTramas.)
        ResultadoValidacion validacion = validador.validarRegistro(partes);
        if (!validacion.esValido) {
            escritor.println(validacion.mensajeError);
            return;
        }

        // Intentamos registrar al usuario en la base de datos
        boolean registrado = usuarioDAO.registrarUsuario(partes[1], partes[2], partes[3]);
        if (registrado) {
            escritor.println("OK|Usuario registrado");
        } else {
            escritor.println("ERROR|El usuario o correo ya existe");
        }
    }

    /**
     * Maneja el comando LOGIN.
     *
     * Formato esperado: LOGIN|nombreUsuario|contrasena
     *
     * Verifica las credenciales y si son correctas guarda el nombre de usuario
     * en la variable de instancia para uso posterior.
     *
     * partes Array con las partes de la trama
     */
    private void manejarLogin(String[] partes) {
        // Validamos formato
        ResultadoValidacion validacion = validador.validarLogin(partes);
        if (!validacion.esValido) {
            escritor.println(validacion.mensajeError);
            return;
        }

        // Verificamos credenciales contra la base de datos
        boolean loginCorrecto = usuarioDAO.verificarLogin(partes[1], partes[2]);
        if (loginCorrecto) {
            this.nombreUsuario = partes[1];  // Guardamos el nombre para uso futuro
            escritor.println("OK|Login correcto");
        } else {
            escritor.println("ERROR|Usuario o contrasena incorrectos");
        }
    }

    /**
     * Maneja el comando Pregunta.
     *
     * Formato esperado:
     * Pregunta|enunciado|respuesta1|respuesta2|respuesta3|respuesta4|codigoSala
     *
     * Guarda una pregunta en la base de datos asociandola a una sala.
     *
     * partes Array con las partes de la trama
     */
    private void manejarPregunta(String[] partes) {
        // Validamos el formato de la trama
        ResultadoValidacion validacion = validador.validarPregunta(partes);
        if (!validacion.esValido) {
            escritor.println(validacion.mensajeError);
            return;
        }

        // Guardamos la pregunta en la base de datos
        boolean guardado = salaDAO.guardarPregunta(
                partes[1], partes[2], partes[3], partes[4], partes[5],
                Integer.parseInt(partes[6])
        );
        if (guardado) {
            escritor.println("GUARDADO_OK");
        } else {
            escritor.println("ERROR|No se pudo guardar");
        }
    }

    /**
     * Maneja el comando Sala.
     *
     * Formato esperado:
     * Sala|nombreSala|codigoSala|cantidadJugadores|nombreUsuario
     *
     * Crea una nueva sala, la guarda en base de datos y en memoria.
     *
     * partes Array con las partes de la trama
     */
    private void manejarSala(String[] partes) {
        // Validamos formato
        ResultadoValidacion validacion = validador.validarSala(partes);
        if (!validacion.esValido) {
            escritor.println(validacion.mensajeError);
            return;
        }

        // Guardamos la sala en la base de datos
        boolean guardado = salaDAO.guardarSala(
                partes[2], partes[1], Integer.parseInt(partes[3]), partes[4]
        );

        if (guardado) {
            // Si se guardo en BD, buscamos el usuario propietario
            Usuario propietario = usuarioDAO.buscarUsuarioPorNombre(partes[4]);

            // Creamos el objeto Sala en memoria
            // Constructor: (codigoSala, nombreSala, estado, cantidadJugadores, propietario)
            Sala salaNueva = new Sala(
                    Integer.parseInt(partes[2]),
                    partes[1],
                    true,
                    Integer.parseInt(partes[3]),
                    propietario
            );

            // Agregamos la sala a la memoria del servidor
            gestorSalas.agregarOActualizarSala(salaNueva);
            escritor.println("GUARDADO_OK");
        } else {
            escritor.println("ERROR|No se pudo guardar la sala");
        }
    }

    /**
     * Maneja el comando PRESENTAR.
     *
     * Formato esperado: PRESENTAR|codigoSala
     *
     * "Presenta" una sala: la marca como activa en BD, la carga en memoria con
     * sus preguntas y registra al presentador en la sala para recibir
     * notificaciones cuando jugadores se unan.
     *
     * partes Array con las partes de la trama
     */
    private void manejarPresentar(String[] partes) {
        // Validamos que el codigo de sala este presente y sea valido
        ResultadoValidacion validacion = validador.validarCodigoSala(partes);
        if (!validacion.esValido) {
            escritor.println(validacion.mensajeError);
            return;
        }

        int codigoSala = Integer.parseInt(partes[1]);

        // Actualizamos el estado de la sala en la base de datos (estado = presentada)
        salaDAO.presentarSala(codigoSala);

        // Cargamos la sala completa desde la base de datos (incluye preguntas)
        Sala sala = salaDAO.buscarSalaPorCodigo(codigoSala);

        if (sala != null) {
            // Guardamos la referencia de la sala actual para este cliente
            this.codigoSalaActual = codigoSala;

            // Agregamos o actualizamos la sala en memoria
            gestorSalas.agregarOActualizarSala(sala);

            // IMPORTANTE: Registramos al presentador en la sala
            // Esto permite que reciba mensajes como JUGADORES cuando alguien se une
            registrarClienteEnSala(codigoSala);

            System.out.println("Presentador registrado en sala " + codigoSala);
            escritor.println("OK|Sala presentada");
        } else {
            escritor.println("ERROR|Sala no encontrada");
        }
    }

    /**
     * Maneja el comando CONSULTAR_SALAS.
     *
     * Formato esperado: CONSULTAR_SALAS|nombreUsuario
     *
     * Consulta todas las salas creadas por un usuario y las devuelve en un
     * formato especifico para que el cliente las muestre.
     *
     * partes Array con las partes de la trama
     */
    private void manejarConsultarSalas(String[] partes) {
        // Validamos que el nombre de usuario este presente
        ResultadoValidacion validacion = validador.validarConsultarSalas(partes);
        if (!validacion.esValido) {
            escritor.println(validacion.mensajeError);
            return;
        }

        // Consultamos las salas del usuario en la base de datos
        ArrayList<Sala> lista = salaDAO.consultarSalasDeUsuario(partes[1]);

        // Formateamos la respuesta y la enviamos
        String respuesta = formatearListaSalas(lista);
        escritor.println(respuesta);
    }

    /**
     * Maneja el comando OBTENER_PREGUNTAS.
     *
     * Formato esperado: OBTENER_PREGUNTAS|codigoSala
     *
     * Obtiene las preguntas de una sala desde la memoria y las envia a todos
     * los clientes conectados a esa sala.
     *
     * partes Array con las partes de la trama
     */
    private void manejarObtenerPreguntas(String[] partes) {
        // Validamos codigo de sala
        ResultadoValidacion validacion = validador.validarCodigoSala(partes);
        if (!validacion.esValido) {
            return;
        }

        int codigoSala = Integer.parseInt(partes[1]);

        // Obtenemos las preguntas formateadas desde el gestor de salas
        String preguntasFormateadas = gestorSalas.formatearPreguntasParaEnvio(codigoSala);

        if (preguntasFormateadas != null) {
            // Enviamos las preguntas SOLO a los clientes de esta sala
            Servidor.enviarASala(codigoSala, preguntasFormateadas);
        }
    }

    /**
     * Maneja el comando INICIAR_JUEGO.
     *
     * Formato esperado: INICIAR_JUEGO|codigoSala
     *
     * Inicia el juego en una sala, marcandola como partida iniciada y
     * notificando a todos los clientes en la sala.
     *
     * partes Array con las partes de la trama
     */
    private void manejarIniciarJuego(String[] partes) {
        // Validamos codigo de sala
        ResultadoValidacion validacion = validador.validarCodigoSala(partes);
        if (!validacion.esValido) {
            return;
        }

        int codigoSala = Integer.parseInt(partes[1]);

        // Delegamos al gestor de salas el inicio del juego
        gestorSalas.iniciarJuego(codigoSala);
    }

    /**
     * Maneja el comando UNIR_SALA.
     *
     * Formato esperado: UNIR_SALA|codigoSala|nombreJugador
     *
     * Permite que un jugador se una a una sala existente.
     *
     * Flujo: 1. Busca la sala en memoria (o la carga desde BD si no esta) 2.
     * Registra al cliente en la sala 3. Agrega al jugador a la lista de
     * jugadores de la sala 4. Envia la lista actualizada a TODOS los clientes
     * en la sala
     *
     * partes Array con las partes de la trama
     */
    private void manejarUnirSala(String[] partes) {
        // Validamos formato
        ResultadoValidacion validacion = validador.validarUnirSala(partes);
        if (!validacion.esValido) {
            escritor.println(validacion.mensajeError);
            return;
        }

        int codigoSala = Integer.parseInt(partes[1]);
        String nombreJugador = partes[2];

        // Buscamos la sala en memoria
        Sala sala = gestorSalas.buscarSala(codigoSala);
        if (sala == null) {
            // Si no esta en memoria, intentamos cargarla desde la base de datos
            sala = salaDAO.buscarSalaPorCodigo(codigoSala);
            if (sala != null) {
                gestorSalas.agregarOActualizarSala(sala);
            } else {
                escritor.println("ERROR|Sala no encontrada");
                return;
            }
        }

        // Guardamos el estado del cliente (sala y nombre)
        this.codigoSalaActual = codigoSala;
        this.nombreUsuario = nombreJugador;

        // Registramos al cliente en la sala para recibir mensajes
        registrarClienteEnSala(codigoSala);

        // Intentamos agregar al jugador a la sala
        boolean agregado = gestorSalas.agregarJugadorASala(codigoSala, nombreJugador);

        if (agregado) {
            System.out.println("Jugador " + nombreJugador + " agregado a sala " + codigoSala);
        } else {
            System.out.println("Jugador " + nombreJugador + " ya existe en sala " + codigoSala);
        }

        // Enviamos la lista actualizada de jugadores a TODOS en la sala
        // Esto incluye al presentador y a los demas jugadores
        String listaJugadores = gestorSalas.obtenerListaJugadoresFormateada(codigoSala);
        if (!listaJugadores.isEmpty()) {
            Servidor.enviarASala(codigoSala, listaJugadores);
            System.out.println("Lista de jugadores enviada a sala " + codigoSala + ": " + listaJugadores);
        }
    }

    /**
     * Maneja el comando CERRAR_SALA.
     *
     * Formato esperado: CERRAR_SALA|codigoSala
     *
     * Cierra una sala, notifica a todos los clientes y la elimina de la memoria
     * del servidor.
     *
     * partes Array con las partes de la trama
     */
    private void manejarCerrarSala(String[] partes) {
        // Validamos codigo de sala
        ResultadoValidacion validacion = validador.validarCodigoSala(partes);
        if (!validacion.esValido) {
            return;
        }

        int codigoSala = Integer.parseInt(partes[1]);

        // Delegamos al gestor de salas el cierre completo
        gestorSalas.cerrarSala(codigoSala);
    }

    // ==================== METODOS AUXILIARES ====================
    /**
     * Registra al cliente actual en una sala para que reciba mensajes.
     *
     * Agrega el PrintWriter del cliente a la lista de clientes de esa sala en
     * el mapa Servidor.clientesPorSala. Esto permite que cuando se envie un
     * mensaje a la sala, este cliente lo reciba.
     *
     * codigoSala El codigo de la sala donde registrar al cliente
     */
    private void registrarClienteEnSala(int codigoSala) {
        // Si la sala no tiene entrada en el mapa, la creamos
        if (!Servidor.clientesPorSala.containsKey(codigoSala)) {
            Servidor.clientesPorSala.put(codigoSala, new ArrayList<PrintWriter>());
        }

        // Si el escritor del cliente no esta ya en la lista, lo agregamos
        if (!Servidor.clientesPorSala.get(codigoSala).contains(escritor)) {
            Servidor.clientesPorSala.get(codigoSala).add(escritor);
        }
    }

    /**
     * Formatea una lista de salas para enviarla al cliente.
     *
     * Formato de respuesta: - Con salas:
     * "RESPUESTA_SALAS|codigo1,nombre1,jugadores1;codigo2,nombre2,jugadores2" -
     * Sin salas: "RESPUESTA_SALAS|VACIO"
     *
     * Las salas se separan con ";" y los datos de cada sala con ","
     *
     * lista Lista de salas a formatear return String formateado con la
     * informacion de las salas
     */
    private String formatearListaSalas(ArrayList<Sala> lista) {
        StringBuilder respuesta = new StringBuilder("RESPUESTA_SALAS|");

        if (lista.isEmpty()) {
            respuesta.append("VACIO");
        } else {
            // Recorremos cada sala y agregamos sus datos
            for (int i = 0; i < lista.size(); i++) {
                Sala s = lista.get(i);
                respuesta.append(s.getCodigoSala()).append(",")
                        .append(s.getNombreSala()).append(",")
                        .append(s.getCantidadJugadores());

                // Agregamos separador entre salas, excepto despues de la ultima
                if (i < lista.size() - 1) {
                    respuesta.append(";");
                }
            }
        }
        return respuesta.toString();
    }

    /**
     * Limpia todos los recursos cuando un cliente se desconecta.
     *
     * Realiza las siguientes acciones: 1. Remueve al cliente de la lista global
     * 2. Si estaba en una sala, lo remueve de la sala 3. Remueve al jugador de
     * la lista de jugadores de la sala 4. Notifica a los demas clientes en la
     * sala 5. Cierra el socket 6. Cierra las conexiones a base de datos
     */
    private void desconectarCliente() {
        // Removemos al cliente de la lista global
        Servidor.clientes.remove(escritor);

        // Si el cliente estaba en una sala, limpiamos su estado
        if (codigoSalaActual > 0 && escritor != null) {
            // Removemos al cliente del mapa de clientes por sala
            Servidor.removerClienteDeSala(codigoSalaActual, escritor);

            // Removemos al jugador de la lista de jugadores de la sala
            gestorSalas.removerJugadorDeSala(codigoSalaActual, nombreUsuario);

            // Notificamos a los demas clientes en la sala
            String listaJugadores = gestorSalas.obtenerListaJugadoresFormateada(codigoSalaActual);
            if (!listaJugadores.isEmpty()) {
                Servidor.enviarASala(codigoSalaActual, listaJugadores);
            }
        }

        // Cerramos el socket
        try {
            if (socketCliente != null && !socketCliente.isClosed()) {
                socketCliente.close();
            }
        } catch (IOException e) {
            System.out.println("Error al cerrar socket: " + e.getMessage());
        }

        // Cerramos las conexiones a base de datos
        if (salaDAO != null) {
            salaDAO.cerrarConexion();
        }
        if (usuarioDAO != null) {
            usuarioDAO.cerrarConexion();
        }

        System.out.println("Cliente desconectado y recursos liberados");
    }
}
