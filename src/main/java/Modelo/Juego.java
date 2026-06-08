package Modelo;

import Controlador.GestorUsuarios;
import java.util.ArrayList;

public class Juego {//singleton

    private ArrayList<Sala> arrayDeSalas;
    GestorUsuarios gestor;

    public Juego() {
        this.arrayDeSalas = new ArrayList<>();
    }

    public ArrayList<Sala> getArrayDeSalas() {
        return arrayDeSalas;
    }

    public void setArrayDeSalas(ArrayList<Sala> arrayDeSalas) {
        this.arrayDeSalas = arrayDeSalas;
    }

    public GestorUsuarios getGestor() {
        return gestor;
    }

    public void setGestor(GestorUsuarios gestor) {
        this.gestor = gestor;
    }

    public int obtenerCantidadUsuariosSala(int codigoSala) {
        for (Sala sala : arrayDeSalas) {
            if (sala.getCodigoSala() == codigoSala) {
                return sala.getCantidadUsuarios();
            }
        }
        return 0;
    }

    public int obtenerCodigoSala() {
        for (Sala sala : arrayDeSalas) {
            return sala.getCodigoSala();

        }
        return 0;
    }

    public Sala buscarSala(int codigo) {
        for (Sala sala : arrayDeSalas) {
            if (sala.getCodigoSala() == codigo) {
                return sala;
            }
        }
        return null;
    }
}
