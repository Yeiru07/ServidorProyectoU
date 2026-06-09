package servidor_cliente;

import Modelo.Juego;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servidor {

    private static final int PUERTO = 5000;//TOMO EL PUERTO
    public static Juego juego = new Juego();
    public static ArrayList<PrintWriter> clientes = new ArrayList<>();

    public static void enviarATodos(String mensaje) {

        System.out.println("CLIENTES CONECTADOS: "
                + clientes.size());

        for (PrintWriter cliente : clientes) {

            System.out.println("ENVIANDO: " + mensaje);

            cliente.println(mensaje);
        }
    }

    public static void main(String[] args) {

        try {

            ServerSocket servidor = new ServerSocket(PUERTO);//CREO UN SERVER CON EL PUERTO
            System.out.println("SERVIDOR INICIADO");

            while (true) {

                System.out.println("Esperando clientes...");

                Socket socketCliente = servidor.accept();

                System.out.println("Cliente conectado");

                ManejadorDeUsuarios cliente = new ManejadorDeUsuarios(socketCliente);

                cliente.start();
            }

        } catch (Exception e) {
            System.out.println("Error al iniciar servidor: " + e.getMessage());
        }
    }
}
