package com.example.miaplicaciondevideojuegos;

import android.util.Log;

import java.util.List;

import java.util.ArrayList;

import java.io.Serializable;

public class Videojuego implements Serializable{
    private String Nombre;
    private String Desarrollador;
    private String Imagen;
    private List<String> Plataformas;
    private boolean esperandoParaJugar;
    private boolean jugando;
    private boolean completado;
    private boolean abandonado;
    private boolean caratulaObtenida;
    private boolean manualObtenido;
    private boolean juegoObtenido;
    private boolean extrasObtenidos;
    private String id; // Campo para el ID del documento
    private String userId;

    public Videojuego() {
        this.Plataformas = new ArrayList<>();
    }

    public String getNombre() {
        return Nombre;
    }

    public String getDesarrollador() {
        return Desarrollador;
    }

    public String getImagen() {
        return Imagen;
    }

    public List<String> getPlataformas() {
        return Plataformas;
    }

    public boolean isEsperandoParaJugar() {
        return esperandoParaJugar;
    }
    public void setEsperandoParaJugar(boolean esperandoParaJugar) {
        this.esperandoParaJugar = esperandoParaJugar;
    }
    public boolean isJugando() {
        return jugando;
    }
    public void setJugando(boolean jugando) {
        this.jugando = jugando;
    }
    public boolean isCompletado() {
        return completado;
    }
    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
    public boolean isAbandonado() {
        return abandonado;
    }
    public void setAbandonado(boolean abandonado) {
        this.abandonado = abandonado;
    }
    public boolean isCaratulaObtenida() {
        return caratulaObtenida;
    }
    public void setCaratulaObtenida(boolean caratulaObtenida) {
        this.caratulaObtenida = caratulaObtenida;
    }
    public boolean isManualObtenido() {
        return manualObtenido;
    }
    public void setManualObtenido(boolean manualObtenido) {
        this.manualObtenido = manualObtenido;
    }
    public boolean isJuegoObtenido() {
        return juegoObtenido;
    }
    public void setJuegoObtenido(boolean juegoObtenido) {
        this.juegoObtenido = juegoObtenido;
    }
    public boolean isExtrasObtenidos() {
        return extrasObtenidos;
    }
    public void setExtrasObtenidos(boolean extrasObtenidos) {
        this.extrasObtenidos = extrasObtenidos;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public List<String> getPlataformasNombres(List<String> listaPlataformas) {
        List<String> nombres = new ArrayList<>();

        for (String nombre : listaPlataformas) {
            Log.d("ADAPTER", "Nombre: " + nombre);
            nombres.add(nombre.substring(3));
        }

        return nombres;
    }
}