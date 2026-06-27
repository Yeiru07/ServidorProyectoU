package Modelo;

import java.util.ArrayList;

public class Sala {

    private int codigoSala;//se guarda en base de datos
    private String nombreSala;//se guarda en base de datos
    private boolean estado;//se guarda en base de datos
    private int cantidadJugadores;//se guarda en base de datos
    private ArrayList<Usuario> arrayDeUsuarios;
    private ArrayList<Preguntas> listaPreguntas;
    private ArrayList<Integer> listaDeCodigos;
    Usuario propietario;
    private boolean partidaIniciada = false;

    public Sala(int codigoSala, String nombreSala, boolean estado, int cantidadJugadores, Usuario propietario) {
        this.codigoSala = codigoSala;
        this.nombreSala = nombreSala;
        this.estado = estado;
        this.cantidadJugadores = cantidadJugadores;
        this.propietario = propietario;
        this.arrayDeUsuarios = new ArrayList<>();//AGREGACION DE USUARIO A LA SALA
        this.listaPreguntas = new ArrayList<>();
        this.listaDeCodigos = new ArrayList<>();
    }

    // OPTION B: Constructor Reducido (¡El que ocupamos para JDBC/Consultas SQL!)
    public Sala(int codigoSala, String nombreSala, boolean estado, int cantidadJugadores) {
        this.codigoSala = codigoSala;
        this.nombreSala = nombreSala;
        this.estado = estado;
        this.cantidadJugadores = cantidadJugadores;
        this.arrayDeUsuarios = new ArrayList<>();
        this.listaPreguntas = new ArrayList<>();
        this.listaDeCodigos = new ArrayList<>();
        this.propietario = null; // Queda vacío inicialmente hasta que se requiera
    }

    public int getCodigoSala() {
        return codigoSala;
    }

    public void setCodigoSala(int codigoSala) {
        this.codigoSala = codigoSala;
    }

    public String getNombreSala() {
        return nombreSala;
    }

    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getCantidadJugadores() {
        return cantidadJugadores;
    }

    public void setCantidadJugadores(int cantidadJugadores) {
        this.cantidadJugadores = cantidadJugadores;
    }

    public ArrayList<Usuario> getArrayDeUsuarios() {
        return arrayDeUsuarios;
    }

    public void setArrayDeUsuarios(ArrayList<Usuario> arrayDeUsuarios) {
        this.arrayDeUsuarios = arrayDeUsuarios;
    }

    public int getCantidadUsuarios() {
        return arrayDeUsuarios.size();
    }

    public ArrayList<Preguntas> getListaPreguntas() {
        return listaPreguntas;
    }

    public void setListaPreguntas(ArrayList<Preguntas> listaPreguntas) {
        this.listaPreguntas = listaPreguntas;
    }

    public ArrayList<Integer> getListaDeCodigos() {
        return listaDeCodigos;
    }

    public void setListaDeCodigos(ArrayList<Integer> listaDeCodigos) {
        this.listaDeCodigos = listaDeCodigos;
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public void agregarJugador(Usuario jugador) {
        arrayDeUsuarios.add(jugador);
    }

    public boolean isPartidaIniciada() {
        return partidaIniciada;
    }

    public void setPartidaIniciada(boolean partidaIniciada) {
        this.partidaIniciada = partidaIniciada;
    }

}
