package servidor_cliente;

import Controlador.GestorUsuarios; // Importamos el gestor que creaste
import Modelo.Sala;
import Modelo.Usuario;
import MySQL.ConexionBaseDeDatos;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.PreparedStatement;

public class ManejadorDeUsuarios extends Thread {

    private Socket socketCliente;
    // Creamos una instancia del gestor para delegarle el peso de la base de datos
    private GestorUsuarios gestor = new GestorUsuarios();

    public ManejadorDeUsuarios(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    @Override
    public void run() {
        try {
            BufferedReader lector = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            PrintWriter escritor = new PrintWriter(socketCliente.getOutputStream(), true);
            Servidor.clientes.add(escritor);//Se traen todos los usuarios conectados

            System.out.println("CLIENTE AGREGADO. TOTAL = " + Servidor.clientes.size());
            String datosRecibidos;
            while ((datosRecibidos = lector.readLine()) != null) {
                System.out.println("TRAMA RECIBIDA EN EL SERVIDOR: " + datosRecibidos);
                String[] partes = datosRecibidos.split("\\|", -1);
                String comando = partes[0]; // Primer elemento antes del primer "|"

                // Evaluamos que quiere hacer el cliente usando un bloque switch básico
                switch (comando) {

                    case "REGISTRO":
                        // Formato esperado desde la UI: REGISTRO|nombreUsuario|correo|contraseña
                        String nombreReg = partes[1];
                        String correoReg = partes[2];
                        String contraReg = partes[3];

                        // Le quitamos el peso al hilo y se lo delegamos al GestorUsuarios
                        boolean registrado = gestor.registrarUsuarioEnBD(nombreReg, correoReg, contraReg);

                        if (registrado) {
                            escritor.println("OK|Usuario registrado con exito");
                        } else {
                            escritor.println("ERROR|El usuario o correo ya existe");
                        }
                        break;

                    case "LOGIN":
                        // Formato esperado desde la UI: LOGIN|nombreUsuario|contraseña
                        String nombreLog = partes[1];
                        String contraLog = partes[2];

                        // Le delegamos la consulta de validación al Gestor
                        boolean loginCorrecto = gestor.verificarLoginEnBD(nombreLog, contraLog);

                        if (loginCorrecto) {
                            escritor.println("OK|Login correcto");
                        } else {
                            escritor.println("ERROR|Usuario o contraseña incorrectos");
                        }
                        break;

                    case "Pregunta":
                        try {
                            // Si el arreglo no tiene los 6 componentes esperados, rechaza la operación de forma segura
                            if (partes.length < 6) {
                                System.out.println("Servidor rechazó trama inválida: Tamaño del arreglo es " + partes.length);
                                escritor.println("ERROR|Formato de pregunta incompleto");
                                break;
                            }

                            guardarPreguntaNuevobd(partes);
                            escritor.println("GUARDADO_OK");
                        } catch (Exception e) {
                            System.out.println("Error al guardar en BD: " + e.getMessage());
                            escritor.println("Error: No se pudo guardar la pregunta");
                        }
                        break;
                    case "Sala":
                        try {
                            String nombreSala = partes[1];
                            int codigoSala = Integer.parseInt(partes[2]);
                            int cantidadJugadores = Integer.parseInt(partes[3]);
                            guardarSalaNuevobd(partes);
                            escritor.println("GUARDADO_OK");

                            Sala salaNueva = new Sala(codigoSala, nombreSala, true, cantidadJugadores);

                            Servidor.juego.getArrayDeSalas().add(salaNueva);

                            System.out.println("Sala agregada al servidor: " + nombreSala);

                            escritor.println("GUARDADO_OK");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    case "PRESENTAR":
                        try {

                            int codigoSala = Integer.parseInt(partes[1]);

                            presentarSala(codigoSala);
                            Sala sala = gestor.buscarSalaPorCodigo(codigoSala);

                            if (sala != null) {

                                boolean existe = false;

                                for (Sala s : Servidor.juego.getArrayDeSalas()) {
                                    if (s.getCodigoSala() == codigoSala) {
                                        existe = true;
                                        break;
                                    }
                                }

                                if (!existe) {
                                    Servidor.juego.getArrayDeSalas().add(sala);
                                    System.out.println("Sala cargada a memoria: " + sala.getCodigoSala());
                                }
                            }
                            escritor.println("OK|Sala presentada");
                        } catch (Exception e) {

                            escritor.println("ERROR|No se pudo presentar la sala");

                            e.printStackTrace();
                        }
                        break;
                    default:
                        escritor.println("ERROR|Comando no reconocido por el servidor");
                        break;
                    case "CONSULTAR_SALAS":
                        try {
                            String nombreUsuario = partes[1];
                            System.out.println("Servidor consultando salas para: " + nombreUsuario);

                            //Con esto llamamos en procedimiento almacenado del gestor
                            java.util.ArrayList<Modelo.Sala> lista = gestor.consultarSalasDeUsuario(nombreUsuario);

                            //Construimos la respuesta en texto plano para el cliente
                            StringBuilder respuesta = new StringBuilder("RESPUESTA_SALAS|");
                            if (lista.isEmpty()) {
                                respuesta.append("VACIO");
                            } else {
                                for (int i = 0; i < lista.size(); i++) {
                                    Modelo.Sala s = lista.get(i);
                                    respuesta.append(s.getCodigoSala()).append(",")
                                            .append(s.getNombreSala()).append(",")
                                            .append(s.getCantidadJugadores());

                                    if (i < lista.size() - 1) {
                                        respuesta.append(";"); // Separador entre salas
                                    }
                                }
                            }

                            //Enviamos de vuelta al cliente
                            escritor.println(respuesta.toString());
                            /*ESTO HACE QUE EL PROGRAMA SE CAIGA Y NO FUNCIONE Servidor.enviarATodos(respuesta.toString());*/
                            System.out.println("Servidor envió: " + respuesta.toString());

                        } catch (Exception e) {
                            System.out.println("Error al consultar salas en el servidor: " + e.getMessage());
                            escritor.println("RESPUESTA_SALAS|VACIO");
                        }
                        break;
                    case "UNIR_SALA":
                        int codigoSala = Integer.parseInt(partes[1]);
                        String nombreJugador = partes[2];
                        System.out.println("LLEGO PETICION DE UNIR SALA");
                        System.out.println("======== UNIR SALA ========");
                        System.out.println("Codigo: " + codigoSala);
                        System.out.println("Jugador: " + nombreJugador);

                        Sala sala = gestor.buscarSalaMemoria(codigoSala);

                        //ESTO ES UNICAMENTE PARA LAS PRUEBAS DE FLUJO Y VER SI LLEGA ALGO
                        System.out.println("Cantidad de salas en memoria: " + Servidor.juego.getArrayDeSalas().size());

                        for (Sala s : Servidor.juego.getArrayDeSalas()) {
                            System.out.println("Sala encontrada en memoria -> Codigo: "
                                    + s.getCodigoSala()
                                    + " Nombre: "
                                    + s.getNombreSala()
                            );
                        }

                        if (sala == null) {
                            System.out.println("NO SE ENCONTRO LA SALA");
                            escritor.println("ERROR");
                        } else {
                            System.out.println("SI SE ENCONTRO LA SALA");

                            boolean existe = false;

                            /*En esta seccion se valida que el jugadar ya haya entrado a la sala, asi su nombre no se repite*/
                            for (Usuario u : sala.getArrayDeUsuarios()) {
                                if (u.getNombreUsuario().equalsIgnoreCase(nombreJugador)) {
                                    existe = true;
                                    break;
                                }
                            }
                            if (!existe) {
                                Usuario jugador = new Usuario(0, nombreJugador, "", "", 0);
                                sala.agregarJugador(jugador);
                            }

                            StringBuilder respuesta = new StringBuilder("JUGADORES|");

                            for (int i = 0; i < sala.getArrayDeUsuarios().size(); i++) {

                                Usuario u = sala.getArrayDeUsuarios().get(i);

                                respuesta.append(u.getNombreUsuario());

                                if (i < sala.getArrayDeUsuarios().size() - 1) {
                                    respuesta.append(",");
                                }
                            }
                            escritor.println(respuesta.toString());
                            System.out.println("CLIENTES CONECTADOS: " + Servidor.clientes.size());
                            Servidor.enviarATodos(respuesta.toString());
                            System.out.println("Jugadores actuales:");

                            for (Usuario u : sala.getArrayDeUsuarios()) {
                                System.out.println("- " + u.getNombreUsuario());
                            }
                        }
                }
            }
        } catch (Exception e) {
            System.out.println("El cliente se desconectó o hubo un error: " + e.getMessage());
        } finally {
            try {
                if (socketCliente != null) {
                    socketCliente.close();
                }
            } catch (Exception ex) {
                System.out.println("Error al cerrar el socket: " + ex.getMessage());
            }
        }
    }

    //Metodo original para insertar preguntas
    private void guardarPreguntaNuevobd(String[] partes) throws Exception {
        java.sql.Connection conexion = ConexionBaseDeDatos.conectar();
        String sql = "INSERT INTO preguntas (enunciado, respuesta1, respuesta2, respuesta3, respuesta4, codigoSala) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, partes[1]);
            ps.setString(2, partes[2]);
            ps.setString(3, partes[3]);
            ps.setString(4, partes[4]);
            ps.setString(5, partes[5]);
            ps.setInt(6, Integer.parseInt(partes[6]));
            ps.executeUpdate();
            System.out.println("Pregunta guardada correctamente");
        } finally {
            conexion.close();
        }
    }

    private void guardarSalaNuevobd(String[] partes) throws Exception {
        java.sql.Connection conexion = ConexionBaseDeDatos.conectar();

        // Modificamos el SQL para que incluya la columna de la llave foránea 'fk_idUsuario'
        // El SELECT interno busca dinámicamente el id del usuario basándose en su nombre
        String sql = "INSERT INTO sala (codigoSala, nombreSala, cantidadJugadore, fk_idUsuario) "
                + "VALUES (?, ?, ?, (SELECT idusuarios FROM usuarios WHERE nombreUsuario = ? LIMIT 1))";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, partes[2]); // codigoSala
            ps.setString(2, partes[1]); // nombreSala
            ps.setInt(3, Integer.parseInt(partes[3])); // cantidadJugadores

            // Pasamos el nombre del usuario actual que está creando la sala (asegúrate de enviarlo en la trama)
            // Por ejemplo, si tu trama al crear la sala es: Sala|nombreSala|codigo|cantidad|nombreUsuario
            ps.setString(4, partes[4]);

            int filas = ps.executeUpdate();
            System.out.println("SALA guardada correctamente con su llave foránea vinculada.");
        } finally {
            conexion.close();
        }
    }

    private void presentarSala(int codigoSala) throws Exception {

        java.sql.Connection conexion = ConexionBaseDeDatos.conectar();

        String sql = "UPDATE sala SET estado = 1 WHERE codigoSala = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, codigoSala);

            int filas = ps.executeUpdate();

            if (filas > 0) {
                System.out.println("Sala " + codigoSala + " presentada correctamente");
            } else {
                System.out.println("No existe la sala " + codigoSala);
            }
        } finally {
            conexion.close();
        }
    }
}
