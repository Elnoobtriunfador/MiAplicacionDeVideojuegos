package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.EditText;
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

public class PantallaTodosLosJuegosActivity extends AppCompatActivity {

    private RecyclerView recyclerViewJuegos;
    private RecyclerViewVideojuegosAdapter videojuegoAdapter;
    private List<Videojuego> listaVideojuegos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallatodoslosjuegos);

        cargarDatos();

        ImageButton imagenVolver = findViewById(R.id.imagenVolver);
        imagenVolver.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaPrincipalActivity.class);
            startActivity(intent);
            finish();
        });

        EditText editTextBuscarJuego = findViewById(R.id.editTextBuscarJuego);
        editTextBuscarJuego.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    editTextBuscarJuego.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                } else {
                    editTextBuscarJuego.setGravity(Gravity.CENTER);
                }
                filtrarJuegos(s.toString());
            }
        });

        recyclerViewJuegos = findViewById(R.id.recyclerViewJuegos);
        recyclerViewJuegos.setLayoutManager(new LinearLayoutManager(this));
        listaVideojuegos = new ArrayList<>();
        videojuegoAdapter = new RecyclerViewVideojuegosAdapter(listaVideojuegos);
        recyclerViewJuegos.setAdapter(videojuegoAdapter);

    }

    private void cargarDatos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference juegosRef = db.collection("juegos");

        juegosRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot value, @NonNull FirebaseFirestoreException error) {
                if (error != null) {
                    Toast.makeText(PantallaTodosLosJuegosActivity.this, "Error al cargar los datos", Toast.LENGTH_SHORT).show();
                    return;
                }

                listaVideojuegos.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Videojuego videojuego = doc.toObject(Videojuego.class);
                    // Obtener los nombres de las plataformas desde la base de datos
                    videojuego.setPlataformas(videojuego.getPlataformas());
                    listaVideojuegos.add(videojuego);
                }

                videojuegoAdapter.notifyDataSetChanged(); // Actualizar el RecyclerView
            }
        });
    }

    private void filtrarJuegos(String texto) {
        List<Videojuego> listaFiltrada = new ArrayList<>();
        for (Videojuego videojuego : listaVideojuegos) {
            if (videojuego.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(videojuego);
            }
        }
        videojuegoAdapter.filtrarJuegos(listaFiltrada);
    }
}