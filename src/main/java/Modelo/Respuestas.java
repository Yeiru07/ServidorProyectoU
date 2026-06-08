package Modelo;

import java.util.ArrayList;

public class Respuestas {

    private int numeroDeRespuesta;
    private String respuestas;
    private boolean correcta;

    public Respuestas(int numeroDeRespuesta, String respuestas, boolean correcta) {
        this.numeroDeRespuesta = numeroDeRespuesta;
        this.respuestas = respuestas;
        this.correcta=correcta;
    }

    public int getNumeroDeRespuesta() {
        return numeroDeRespuesta;
    }

    public void setNumeroDeRespuesta(int numeroDeRespuesta) {
        this.numeroDeRespuesta = numeroDeRespuesta;
    }

    public String getRespuestas() {
        return respuestas;
    }

    public void setRespuestas(String respuestas) {
        this.respuestas = respuestas;
    }

    public boolean isCorrecta() {
        return correcta;
    }

    public void setCorrecta(boolean correcta) {
        this.correcta = correcta;
    }



}
