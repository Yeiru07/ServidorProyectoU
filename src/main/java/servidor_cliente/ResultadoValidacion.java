package servidor_cliente;

/**
 * Clase que representa el resultado de una validacion de trama.
 *
 * Esta clase es usada por ValidadorTramas para devolver el resultado de cada
 * validacion de forma estructurada.
 *
 * Contiene dos campos: - esValido: true si la trama paso todas las validaciones
 * - mensajeError: el mensaje de error si la trama no es valida (null si es
 * valida)
 */
public class ResultadoValidacion {

    // Indica si la validacion fue exitosa
    public boolean esValido;

    // Mensaje de error para enviar al cliente (null si es valido)
    public String mensajeError;

    /**
     * Constructor del resultado de validacion.
     *
     * esValido true si la trama es valida, false si no
     * mensajeError mensaje de error (puede ser null si es valido)
     */
    public ResultadoValidacion(boolean esValido, String mensajeError) {
        this.esValido = esValido;
        this.mensajeError = mensajeError;
    }
}
