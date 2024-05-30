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

    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private RecyclerViewConsolasAdapter adapter;
    private RecyclerViewJuegosAdapter juegosAdapter;
    private List<Plataforma> plataformas;
    private List<Videojuego> juegos;
    private List<Videojuego> juegosAgregados;
    private FirebaseFirestore db;
    private String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallatusjuegos);

        juegosAgregados = new ArrayList<>(); // Inicializar la lista aquí

        cargarDatos();

        ImageButton imagenVolver = findViewById(R.id.imagenVolver);
        imagenVolver.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaPrincipalActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerView = findViewById(R.id.recyclerViewConsolas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

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
                    adapter = new RecyclerViewConsolasAdapter(plataformas, this);
                    recyclerView.setAdapter(adapter);
                    adapter.selectDefaultItem(0);
                    //adapter.setOnItemSelectedListener(this::filtrarJuegosPorPlataforma);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PantallaTusJuegosActivity.this, "Error al obtener plataformas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        recyclerView2 = findViewById(R.id.recyclerViewJuegos);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void cargarDatos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference juegosRef = db.collection("juegos");

        juegosRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot value, @NonNull FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(PantallaTusJuegosActivity.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                    return;
                }

                juegos = new ArrayList<>();
                for (QueryDocumentSnapshot doc : value) {
                    Videojuego videojuego = doc.toObject(Videojuego.class);
                    videojuego.setId(doc.getId());
                    juegos.add(videojuego);
                }

                // Filtrar los juegos por el estado "Lo tengo"
                juegosAgregados.clear();
                juegosAgregados.addAll(juegos.stream()
                        .filter(Videojuego::isLoTengo)
                        .collect(Collectors.toList()));

                // Crear o actualizar el adaptador con la lista filtrada
                if (juegosAdapter == null) {
                    juegosAdapter = new RecyclerViewJuegosAdapter(juegosAgregados, PantallaTusJuegosActivity.this);
                    recyclerView2.setAdapter(juegosAdapter);
                } else {
                    juegosAdapter.notifyDataSetChanged();
                }
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