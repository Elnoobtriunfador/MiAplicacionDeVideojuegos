package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class PantallaTusJuegosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewConsolasAdapter adapter;
    private List<Plataforma> plataformas;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallatusjuegos);

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
                        String nombre = document.getString("Nombre");
                        if (nombre != null) {
                            plataformas.add(new Plataforma(nombre));
                        } else {
                        }
                    }
                    adapter = new RecyclerViewConsolasAdapter(plataformas, this);
                    recyclerView.setAdapter(adapter);
                    adapter.selectDefaultItem(0);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PantallaTusJuegosActivity.this, "Error al obtener plataformas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
