package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PantallaTusJuegosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewConsolas;
    private RecyclerView recyclerViewJuegos;
    private RecyclerViewConsolasAdapter consolasAdapter;
    private RecyclerViewJuegosAdapter juegosAdapter;
    private List<Plataforma> plataformas;
    private List<Videojuego> juegos;
    private FirebaseFirestore db;
    private String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallatusjuegos);

        cargarDatos();

        ImageButton imagenVolver = findViewById(R.id.imagenVolver);
        imagenVolver.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaPrincipalActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerViewConsolas = findViewById(R.id.recyclerViewConsolas);
        recyclerViewConsolas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        plataformas = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Obtener datos de Firestore
        db.collection("plataformas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        nombre = document.getString("Nombre");
                        if (nombre != null) {
                            plataformas.add(new Plataforma(nombre));
                        }
                    }
                    consolasAdapter = new RecyclerViewConsolasAdapter(plataformas, this);
                    recyclerViewConsolas.setAdapter(consolasAdapter);
                    consolasAdapter.selectDefaultItem(0);
                    //adapter.setOnItemSelectedListener(this::filtrarJuegosPorPlataforma);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PantallaTusJuegosActivity.this, "Error al obtener plataformas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        recyclerViewJuegos = findViewById(R.id.recyclerViewJuegos);
        recyclerViewJuegos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        juegos = new ArrayList<>();
        juegosAdapter = new RecyclerViewJuegosAdapter(juegos, this);
        recyclerViewJuegos.setAdapter(juegosAdapter);
    }

    private void cargarDatos() {
        db = FirebaseFirestore.getInstance();
        CollectionReference juegosRef = db.collection("juegos");

        juegosRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot value, @NonNull FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(PantallaTusJuegosActivity.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                    return;
                }

                juegos.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Videojuego videojuego = doc.toObject(Videojuego.class);
                    // Asigna el ID del documento al Videojuego
                    videojuego.setId(doc.getId());
                    // Actualiza los booleans
                    videojuego.cargarBooleans(doc);
                    juegos.add(videojuego);
                }
                juegosAdapter.filtrarJuegosAgregados(juegos);
            }
        });
    }

    /*private void filtrarJuegosPorPlataforma(Plataforma plataformaSeleccionada) {
        juegosFiltrados.clear();
        for (Videojuego juego : juegos) {
            if (juego.getPlataformas().contains(plataformaSeleccionada.getNombre())) {
                juegosFiltrados.add(juego);
            }
        }
        juegosAdapter.notifyDataSetChanged();
    }*/
}