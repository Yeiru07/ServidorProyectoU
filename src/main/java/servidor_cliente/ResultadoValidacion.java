package servidor_cliente;

public class ResultadoValidacion {

    public final boolean esValido;
    public final String mensajeError;

    public ResultadoValidacion(boolean esValido, String mensajeError) {
        this.esValido = esValido;
        this.mensajeError = mensajeError;
    }
}
