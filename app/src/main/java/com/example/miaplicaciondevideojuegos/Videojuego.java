package com.example.miaplicaciondevideojuegos;

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
    private List<String> PlataformaIds;

    public Videojuego() {
        this.Obtencion = new ArrayList<>();
        this.Progreso = new ArrayList<>();
        this.PlataformaIds = new ArrayList<>();
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
        return PlataformaIds;
    }

    public void setPlataformas(List<String> plataformas) {
        this.PlataformaIds = plataformas;
    }

    public void addPlataforma(String plataforma) {
        this.PlataformaIds.add(plataforma);
    }

    public List<String> getPlataformasNombres() {
        List<String> plataformasNombres = new ArrayList<>();

        // Obtenemos una referencia a la colección de plataformas
        CollectionReference plataformasRef = FirebaseFirestore.getInstance().collection("plataformas");

        // Obtenemos los nombres de las plataformas para cada ID
        for (String plataformaId : PlataformaIds) {
            DocumentReference plataformaRef = plataformasRef.document(plataformaId);
            plataformaRef.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String nombrePlataforma = documentSnapshot.getString("nombre"); // Suponiendo que el campo de nombre se llama "nombre"
                            plataformasNombres.add(nombrePlataforma);
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error
                    });
        }

        return plataformasNombres;
    }
}