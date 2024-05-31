package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.widget.Button;
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
    private EditText editTextBuscarJuego;

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

        editTextBuscarJuego = findViewById(R.id.editTextBuscarJuego);

        // Botón de búsqueda
        Button botonBuscar = findViewById(R.id.botonBuscar);
        botonBuscar.setOnClickListener(view -> {
            String textoBusqueda = editTextBuscarJuego.getText().toString();
            videojuegoAdapter.filtrarJuegos(textoBusqueda);
        });

        recyclerViewJuegos = findViewById(R.id.recyclerViewJuegos);
        recyclerViewJuegos.setLayoutManager(new LinearLayoutManager(this));
        listaVideojuegos = new ArrayList<>();
        videojuegoAdapter = new RecyclerViewVideojuegosAdapter(listaVideojuegos, this);
        recyclerViewJuegos.setAdapter(videojuegoAdapter);
    }

    private void cargarDatos() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference juegosRef = db.collection("juegos");

        juegosRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot value, @NonNull FirebaseFirestoreException error) {
                if (error != null) {
                    CustomToast.showToastShorter(PantallaTodosLosJuegosActivity.this, "Error al cargar los datos", 1000);
                    return;
                }

                listaVideojuegos.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Videojuego videojuego = doc.toObject(Videojuego.class);
                    // Asigna el ID del documento al Videojuego
                    videojuego.setId(doc.getId());
                    listaVideojuegos.add(videojuego);
                }
                videojuegoAdapter.filtrarJuegos("");
            }
        });
    }
}