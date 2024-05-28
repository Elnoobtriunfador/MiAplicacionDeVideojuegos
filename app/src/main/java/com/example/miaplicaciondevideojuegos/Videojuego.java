package com.example.miaplicaciondevideojuegos;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import java.util.ArrayList;

public class Videojuego {
    private String Nombre;
    private String Desarrollador;
    private String Imagen;
    private List<String> Obtencion;
    private List<String> Progreso;
    private List<String> Plataformas;

    public Videojuego() {
        this.Obtencion = new ArrayList<>();
        this.Progreso = new ArrayList<>();
        this.Plataformas = new ArrayList<>();
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        this.Nombre = nombre;
    }

    public String getDesarrollador() {
        return Desarrollador;
    }

    public void setDesarrollador(String desarrollador) {
        this.Desarrollador = desarrollador;
    }

    public String getImagen() {
        return Imagen;
    }

    public void setImagen(String imagen) {
        this.Imagen = imagen;
    }

    public List<String> getObtencion() {
        return Obtencion;
    }

    public void setObtencion(List<String> obtencion) {
        this.Obtencion = obtencion;
    }

    public void addObtencion(String item) {
        this.Obtencion.add(item);
    }

    public List<String> getProgreso() {
        return Progreso;
    }

    public void setProgreso(List<String> progreso) {
        this.Progreso = progreso;
    }

    public void addProgreso(String item) {
        this.Progreso.add(item);
    }

    public List<String> getPlataformas() {
        return Plataformas;
    }

    public void setPlataformas(List<String> plataformas) {
        this.Plataformas = plataformas;
    }

    public void addPlataforma(String plataforma) {
        this.Plataformas.add(plataforma);
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