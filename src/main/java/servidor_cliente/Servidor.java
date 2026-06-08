    package servidor_cliente;

    import java.net.ServerSocket;
    import java.net.Socket;

    public class Servidor {

        private static final int PUERTO = 5000;//TOMO EL PUERTO

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
