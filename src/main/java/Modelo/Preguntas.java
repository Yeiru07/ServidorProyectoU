package Modelo;

import java.util.ArrayList;

public class Preguntas {

    private String enunciado;
    private ArrayList<Respuestas> arregloDeRespuestasParaPreguntas;
    private String tipoDePregunta;
    private int tiempoParaLasPreguntas;
    private int valorPuntosPreguntas;
    private int codigoSala;

    public Preguntas(String enunciado, ArrayList<Respuestas> arregloDeRespuestasParaPreguntas, String tipoDePregunta, int codigoSala) {
        this.enunciado = enunciado;
        this.arregloDeRespuestasParaPreguntas = new ArrayList<>();
    }

    public Preguntas() {
        this.enunciado = "";
        this.arregloDeRespuestasParaPreguntas = new ArrayList<>();
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public ArrayList<Respuestas> getArregloDeRespuestasParaPreguntas() {
        return arregloDeRespuestasParaPreguntas;
    }

    public void setArregloDeRespuestasParaPreguntas(ArrayList<Respuestas> arregloDeRespuestasParaPreguntas) {
        this.arregloDeRespuestasParaPreguntas = arregloDeRespuestasParaPreguntas;
    }

    public int getTiempoParaLasPreguntas() {
        return tiempoParaLasPreguntas;
    }

    public void setTiempoParaLasPreguntas(int tiempoParaLasPreguntas) {
        this.tiempoParaLasPreguntas = tiempoParaLasPreguntas;
    }

    public int getValorPuntosPreguntas() {
        return valorPuntosPreguntas;
    }

    public void setValorPuntosPreguntas(int valorPuntosPreguntas) {
        this.valorPuntosPreguntas = valorPuntosPreguntas;
    }

    public String getTipoDePregunta() {
        return tipoDePregunta;
    }

    public void setTipoDePregunta(String tipoDePregunta) {
        this.tipoDePregunta = tipoDePregunta;
    }

    public int getCodigoSala() {
        return codigoSala;
    }

    public void setCodigoSala(int codigoSala) {
        this.codigoSala = codigoSala;
    }
    

}
