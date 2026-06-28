package servidor_cliente;

import Modelo.Juego;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase principal del servidor del juego Kahoot.
 *
 * Responsabilidades: - Iniciar el servidor y aceptar conexiones de clientes -
 * Mantener listas de clientes conectados (global y por sala) - Proporcionar
 * metodos para enviar mensajes a todos los clientes o solo a los clientes de
 * una sala especifica - Gestionar el ciclo de vida de las salas
 * (agregar/remover clientes)
 *
 * Esta clase es el punto de entrada del servidor (metodo main).
 */
public class Servidor {

    // Puerto en el que el servidor escucha conexiones entrantes
    private static final int PUERTO = 5000;

    // Instancia global del juego que contiene todas las salas
    public static Juego juego = new Juego();

    // Lista global de todos los clientes conectados (sin importar la sala)
    // Cada elemento es un PrintWriter que permite enviar mensajes a un cliente
    public static java.util.List<PrintWriter> clientes = Collections.synchronizedList(new ArrayList<>());

    // Mapa que agrupa clientes por sala
    // Clave: codigo de la sala (Integer)
    // Valor: lista de PrintWriters de los clientes en esa sala
    public static Map<Integer, ArrayList<PrintWriter>> clientesPorSala = Collections.synchronizedMap(new HashMap<>());

    /**
     * Envia un mensaje a TODOS los clientes conectados al servidor.
     *
     * Recorre la lista global de clientes y envia el mensaje a cada uno. Usar
     * con precaucion: este metodo envia a TODOS, sin importar la sala.
     *
     * mensaje El mensaje a enviar a todos los clientes
     */
    public static void enviarATodos(String mensaje) {
        System.out.println("ENVIANDO A TODOS (" + clientes.size() + " clientes): " + mensaje);

        // Recorremos la lista global de clientes
        synchronized (clientes) {
            for (int i = 0; i < clientes.size(); i++) {
                PrintWriter cliente = clientes.get(i);
                cliente.println(mensaje);  // Enviamos el mensaje por el socket
            }
        }
    }

    /**
     * Envia un mensaje SOLO a los clientes que estan en una sala especifica.
     *
     * Este es el metodo principal para comunicacion dentro de una sala. Los
     * clientes que no estan en esta sala NO reciben el mensaje.
     *
     * codigoSala El codigo de la sala destino mensaje El mensaje a enviar a los
     * clientes de la sala
     */
    public static void enviarASala(int codigoSala, String mensaje) {
        // Obtenemos la lista de clientes de esta sala especifica
        ArrayList<PrintWriter> clientesSala = clientesPorSala.get(codigoSala);

        if (clientesSala != null) {
            System.out.println("ENVIANDO A SALA " + codigoSala + " (" + clientesSala.size() + " clientes): " + mensaje);

            // Recorremos solo los clientes de esta sala
            synchronized (clientesSala) {
                for (int i = 0; i < clientesSala.size(); i++) {
                    PrintWriter cliente = clientesSala.get(i);
                    cliente.println(mensaje);
                }
            }
        } else {
            System.out.println("No hay clientes en la sala " + codigoSala);
        }
    }

    /**
     * Remueve un cliente de una sala especifica.
     *
     * Cuando un cliente se desconecta o cambia de sala, este metodo lo elimina
     * de la lista de clientes de la sala. Si la sala queda sin clientes,
     * tambien se elimina del mapa.
     *
     * odigoSala El codigo de la sala de donde remover al cliente cliente El
     * PrintWriter del cliente a remover
     */
    public static void removerClienteDeSala(int codigoSala, PrintWriter cliente) {
        // Obtenemos la lista de clientes de la sala
        ArrayList<PrintWriter> clientesSala = clientesPorSala.get(codigoSala);

        if (clientesSala != null) {
            // Removemos al cliente de la lista
            synchronized (clientesSala) {
                clientesSala.remove(cliente);
            }
            System.out.println("Cliente removido de sala " + codigoSala + ". Quedan: " + clientesSala.size());

            // Si la sala se quedo sin clientes, la eliminamos del mapa
            if (clientesSala.isEmpty()) {
                clientesPorSala.remove(codigoSala);
                System.out.println("Sala " + codigoSala + " eliminada (sin clientes)");
            }
        }
    }

    /**
     * Imprime el estado actual de todas las salas en consola.
     *
     * Util para depuracion: muestra cuantas salas hay, cuantos clientes tiene
     * cada sala y el total de salas en el juego.
     */
    public static void imprimirEstadoSalas() {
        System.out.println("=== ESTADO DE SALAS ===");

        // Obtenemos todas las entradas del mapa (codigoSala -> lista de clientes)
        // Usamos entrySet() para iterar sobre el mapa
        synchronized (clientesPorSala) {
            for (Map.Entry<Integer, ArrayList<PrintWriter>> entrada : clientesPorSala.entrySet()) {
                int codigoSala = entrada.getKey();
                int cantidadClientes = entrada.getValue().size();
                System.out.println("Sala " + codigoSala + ": " + cantidadClientes + " clientes");
            }
        }

        System.out.println("Total salas en juego: " + juego.getArrayDeSalas().size());
        System.out.println("======================");
    }

    /**
     * Metodo principal que inicia el servidor.
     *
     * Flujo: 1. Crea un ServerSocket en el puerto especificado 2. Entra en un
     * bucle infinito esperando conexiones 3. Por cada conexion entrante: -
     * Acepta el socket del cliente - Crea una instancia de ManejadorDeUsuarios
     * - Inicia el hilo para manejar al cliente 4. Si hay un error, lo imprime y
     * termina
     *
     * args Argumentos de linea de comandos (no se usan)
     */
    public static void main(String[] args) {
        try {
            // Creamos el socket del servidor en el puerto definido
            ServerSocket servidor = new ServerSocket(PUERTO);
            System.out.println("SERVIDOR INICIADO EN PUERTO " + PUERTO);

            // Bucle infinito: el servidor siempre esta escuchando
            while (true) {
                System.out.println("Esperando clientes...");

                // accept() se bloquea hasta que un cliente se conecta
                Socket socketCliente = servidor.accept();
                System.out.println("Cliente conectado desde: " + socketCliente.getInetAddress());

                // Creamos un manejador para este cliente y lo ejecutamos en un hilo
                ManejadorDeUsuarios cliente = new ManejadorDeUsuarios(socketCliente);
                cliente.start();  // Inicia el hilo (llama al metodo run())
            }
        } catch (Exception e) {
            System.out.println("Error al iniciar servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
