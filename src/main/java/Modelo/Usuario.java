package Modelo;

import java.util.ArrayList;

public class Usuario implements Interfaces.ItipoUsuario {

    private int idUsuario;
    private String nombreUsuario;
    private String correo;
    private String contraseña;
    private double puntuajeAcumulado;
    private ArrayList<Sala> salasAdministradas = new ArrayList<>();

    public Usuario(int idUsuario, String nombreUsuario, String correo, String contraseña, double puntuajeAcumulado) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.contraseña = contraseña;
        this.puntuajeAcumulado = 0.0;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public double getPuntuajeAcumulado() {
        return puntuajeAcumulado;
    }

    public void setPuntuajeAcumulado(double puntuajeAcumulado) {
        this.puntuajeAcumulado = puntuajeAcumulado;
    }

    public ArrayList<Sala> getSalasAdministradas() {
        return salasAdministradas;
    }

    public void setSalasAdministradas(ArrayList<Sala> salasAdministradas) {
        this.salasAdministradas = salasAdministradas;
    }

    @Override
    public String getTipo() {

        if (!salasAdministradas.isEmpty()) {
            return "Administrador";
        }

        return "Normal";
    }
}
