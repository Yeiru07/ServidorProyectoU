package servidor_cliente;

/**
 * Clase encargada de validar todas las tramas recibidas por el servidor.
 *
 * Esta clase centraliza la logica de validacion para cada tipo de comando,
 * asegurando que las tramas tengan el formato correcto antes de procesarlas.
 *
 * Cada metodo de validacion retorna un objeto ResultadoValidacion que indica: -
 * Si la trama es valida o no - El mensaje de error correspondiente en caso de
 * ser invalida
 *
 * Ventajas de tener la validacion separada: - Codigo mas limpio en
 * ManejadorDeUsuarios - Las reglas de validacion se pueden modificar en un solo
 * lugar - Facil de extender con nuevas validaciones
 */
public class ValidadorTramas {

    /**
     * Valida la trama de registro de usuario.
     *
     * Formato esperado: REGISTRO|nombre|correo|contrasena
     *
     * Reglas de validacion: - Debe tener al menos 4 partes - El nombre de
     * usuario no puede estar vacio - El correo no puede estar vacio y debe
     * contener el simbolo @ - La contrasena debe tener al menos 4 caracteres
     *
     * partes Array con las partes de la trama return ResultadoValidacion con el
     * resultado de la validacion
     */
    public ResultadoValidacion validarRegistro(String[] partes) {
        // Verificamos que la trama tenga suficientes partes
        if (partes.length < 4) {
            return new ResultadoValidacion(false, "ERROR|Formato: REGISTRO|nombre|correo|contrasena");
        }

        // Validamos que el nombre no este vacio (despues de quitar espacios)
        if (partes[1].trim().isEmpty()) {
            return new ResultadoValidacion(false, "ERROR|El nombre de usuario no puede estar vacio");
        }

        // Validamos que el correo no este vacio y contenga el simbolo @
        if (partes[2].trim().isEmpty() || !partes[2].contains("@")) {
            return new ResultadoValidacion(false, "ERROR|El correo no es valido");
        }

        // Validamos que la contrasena tenga al menos 4 caracteres
        if (partes[3].trim().length() < 4) {
            return new ResultadoValidacion(false, "ERROR|La contrasena debe tener al menos 4 caracteres");
        }

        // Todas las validaciones pasaron
        return new ResultadoValidacion(true, null);
    }

    /**
     * Valida la trama de inicio de sesion.
     *
     * Formato esperado: LOGIN|nombre|contrasena
     *
     * Reglas de validacion: - Debe tener al menos 3 partes - El nombre de
     * usuario y la contrasena no pueden estar vacios
     *
     * partes Array con las partes de la trama return ResultadoValidacion con el
     * resultado de la validacion
     */
    public ResultadoValidacion validarLogin(String[] partes) {
        // Verificamos que la trama tenga suficientes partes
        if (partes.length < 3) {
            return new ResultadoValidacion(false, "ERROR|Formato: LOGIN|nombre|contrasena");
        }

        // Validamos que ambos campos no esten vacios
        if (partes[1].trim().isEmpty() || partes[2].trim().isEmpty()) {
            return new ResultadoValidacion(false, "ERROR|Usuario y contrasena son requeridos");
        }

        // Validacion exitosa
        return new ResultadoValidacion(true, null);
    }

    /**
     * Valida la trama de creacion de pregunta.
     *
     * Formato esperado:
     * Pregunta|enunciado|respuesta1|respuesta2|respuesta3|respuesta4|codigoSala
     *
     * Reglas de validacion: - Debe tener al menos 7 partes - El enunciado no
     * puede estar vacio
     *
     * Nota: Las respuestas pueden ser cadenas vacias en algunos tipos de
     * pregunta
     *
     * partes Array con las partes de la trama return ResultadoValidacion con el
     * resultado de la validacion
     */
    public ResultadoValidacion validarPregunta(String[] partes) {
        // Verificamos que la trama tenga el numero minimo de partes
        if (partes.length < 7) {
            return new ResultadoValidacion(false, "ERROR|Formato de pregunta incompleto");
        }

        // Validamos que el enunciado no este vacio
        if (partes[1].trim().isEmpty()) {
            return new ResultadoValidacion(false, "ERROR|El enunciado no puede estar vacio");
        }

        // Validacion exitosa
        return new ResultadoValidacion(true, null);
    }

    /**
     * Valida la trama de creacion de sala.
     *
     * Formato esperado:
     * Sala|nombreSala|codigoSala|cantidadJugadores|nombreUsuario
     *
     * Reglas de validacion: - Debe tener al menos 5 partes - El codigo de sala
     * debe ser un numero valido - La cantidad de jugadores debe ser un numero
     * valido
     *
     * partes Array con las partes de la trama ResultadoValidacion con el
     * resultado de la validacion
     */
    public ResultadoValidacion validarSala(String[] partes) {
        // Verificamos el numero minimo de partes
        if (partes.length < 5) {
            return new ResultadoValidacion(false, "ERROR|Formato incompleto para sala");
        }

        // Intentamos convertir el codigo y la cantidad a numeros
        // Si falla, significa que no son valores numericos validos
        try {
            Integer.parseInt(partes[2]);  // Validar codigo de sala
            Integer.parseInt(partes[3]);  // Validar cantidad de jugadores
        } catch (NumberFormatException e) {
            return new ResultadoValidacion(false, "ERROR|Codigo o cantidad de jugadores invalidos");
        }

        // Validacion exitosa
        return new ResultadoValidacion(true, null);
    }

    /**
     * Valida que una trama contenga un codigo de sala valido.
     *
     * Este metodo es usado por varios comandos que solo necesitan un codigo de
     * sala como parametro (PRESENTAR, INICIAR_JUEGO, etc.)
     *
     * Formato esperado: COMANDO|codigoSala
     *
     * Reglas de validacion: - Debe tener al menos 2 partes - El codigo de sala
     * debe ser un numero valido
     *
     * partes Array con las partes de la trama ResultadoValidacion con el
     * resultado de la validacion
     */
    public ResultadoValidacion validarCodigoSala(String[] partes) {
        // Verificamos que exista el codigo de sala
        if (partes.length < 2) {
            return new ResultadoValidacion(false, "ERROR|Codigo de sala requerido");
        }

        // Validamos que el codigo sea un numero
        try {
            Integer.parseInt(partes[1]);
        } catch (NumberFormatException e) {
            return new ResultadoValidacion(false, "ERROR|Codigo de sala invalido");
        }

        // Validacion exitosa
        return new ResultadoValidacion(true, null);
    }

    /**
     * Valida la trama para unirse a una sala.
     *
     * Formato esperado: UNIR_SALA|codigoSala|nombreJugador
     *
     * Reglas de validacion: - Debe tener al menos 3 partes - El codigo de sala
     * debe ser un numero valido - El nombre del jugador no puede estar vacio
     *
     * partes Array con las partes de la trama ResultadoValidacion con el
     * resultado de la validacion
     */
    public ResultadoValidacion validarUnirSala(String[] partes) {
        // Verificamos el numero minimo de partes
        if (partes.length < 3) {
            return new ResultadoValidacion(false, "ERROR|Formato: UNIR_SALA|codigo|nombre");
        }

        // Validamos que el codigo de sala sea numerico
        try {
            Integer.parseInt(partes[1]);
        } catch (NumberFormatException e) {
            return new ResultadoValidacion(false, "ERROR|Codigo de sala invalido");
        }

        // Validamos que el nombre del jugador no este vacio
        if (partes[2].trim().isEmpty()) {
            return new ResultadoValidacion(false, "ERROR|Nombre de jugador requerido");
        }

        // Validacion exitosa
        return new ResultadoValidacion(true, null);
    }

    /**
     * Valida la trama de consulta de salas.
     *
     * Formato esperado: CONSULTAR_SALAS|nombreUsuario
     *
     * Reglas de validacion: - Debe tener al menos 2 partes - El nombre de
     * usuario no puede estar vacio
     *
     * En caso de error, el mensaje usa el prefijo "RESPUESTA_SALAS|VACIO"
     * porque es lo que el cliente espera recibir en ese formato.
     *
     * partes Array con las partes de la trama ResultadoValidacion con el
     * resultado de la validacion
     */
    public ResultadoValidacion validarConsultarSalas(String[] partes) {
        // Verificamos que el nombre de usuario este presente y no vacio
        if (partes.length < 2 || partes[1].trim().isEmpty()) {
            // Usamos el formato de respuesta esperado por el cliente
            return new ResultadoValidacion(false, "RESPUESTA_SALAS|VACIO");
        }

        // Validacion exitosa
        return new ResultadoValidacion(true, null);
    }
}
