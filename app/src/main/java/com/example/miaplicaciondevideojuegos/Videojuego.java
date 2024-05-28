package com.example.miaplicaciondevideojuegos;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import java.util.ArrayList;

public class Videojuego {
    private String nombre;
    private String desarrollador;
    private String imagen;
    private List<String> obtencion;
    private List<String> progreso;
    private List<String> plataformaIds;

    public Videojuego() {
        this.obtencion = new ArrayList<>();
        this.progreso = new ArrayList<>();
        this.plataformaIds = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDesarrollador() {
        return desarrollador;
    }

    public void setDesarrollador(String desarrollador) {
        this.desarrollador = desarrollador;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<String> getObtencion() {
        return obtencion;
    }

    public void setObtencion(List<String> obtencion) {
        this.obtencion = obtencion;
    }

    public void addObtencion(String item) {
        this.obtencion.add(item);
    }

    public List<String> getProgreso() {
        return progreso;
    }

    public void setProgreso(List<String> progreso) {
        this.progreso = progreso;
    }

    public void addProgreso(String item) {
        this.progreso.add(item);
    }

    public List<String> getPlataformas() {
        return plataformaIds;
    }

    public void setPlataformas(List<String> plataformas) {
        this.plataformaIds = plataformas;
    }

    public void addPlataforma(String plataforma) {
        this.plataformaIds.add(plataforma);
    }

    public List<String> getPlataformasNombres() {
        List<String> plataformasNombres = new ArrayList<>();

        // Obtenemos una referencia a la colección de plataformas
        CollectionReference plataformasRef = FirebaseFirestore.getInstance().collection("plataformas");

        // Obtenemos los nombres de las plataformas para cada ID
        for (String plataformaId : plataformaIds) {
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