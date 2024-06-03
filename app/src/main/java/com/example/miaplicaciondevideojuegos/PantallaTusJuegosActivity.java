package com.example.miaplicaciondevideojuegos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class PantallaTusJuegosActivity extends AppCompatActivity implements RecyclerViewConsolasAdapter.OnItemSelectedListener {

    private RecyclerView recyclerViewConsolas;
    private RecyclerView recyclerViewJuegos;
    private RecyclerViewConsolasAdapter consolasAdapter;
    private RecyclerViewJuegosAdapter juegosAdapter;
    private List<Plataforma> plataformas;
    private List<Videojuego> juegos;
    private FirebaseFirestore db;
    private String nombre;
    private ImageButton imagenVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallatusjuegos);

        if (!NetworkUtil.isConnectedToInternet(this)) {
            showNoInternetDialog();
        } else {
            setContentView(R.layout.activity_pantallatusjuegos);
        }

        cargarDatos();

        imagenVolver = findViewById(R.id.imagenVolver);
        imagenVolver.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaPrincipalActivity.class);
            startActivity(intent);
            finish();
        });

        recyclerViewConsolas = findViewById(R.id.recyclerViewConsolas);
        recyclerViewConsolas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        plataformas = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

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
                    consolasAdapter.setOnItemSelectedListener(this);
                })
                .addOnFailureListener(e -> {
                    CustomToast.showToastShorter(this, "Error al obtener plataformas: ", 1000);
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
                    CustomToast.showToastShorter(PantallaTusJuegosActivity.this, "Error al cargar los datos", 1000);
                    return;
                }

                juegos.clear();
                for (QueryDocumentSnapshot doc : value) {
                    Videojuego videojuego = doc.toObject(Videojuego.class);
                    videojuego.setId(doc.getId());
                    videojuego.cargarBooleans(doc);
                    juegos.add(videojuego);
                }

                juegosAdapter.filtrarJuegosAgregados(juegos);
                filtrarJuegos("Nintendo 3DS");
            }
        });
    }

    @Override
    public void onItemSelected(Plataforma plataforma) {
        filtrarJuegos(plataforma.getNombre());
    }

    public void filtrarJuegos(String plataforma){
        List<Videojuego> listaFinal = new ArrayList<>();
        for (Videojuego juego : juegos){
            List<String> plataformaJuego = juego.getPlataformasNombres(juego.getPlataformas());
            for (String nombrePlataforma : plataformaJuego){
                if (nombrePlataforma.equals(plataforma)){
                    listaFinal.add(juego);
                }
            }
        }
        juegosAdapter.filtrarJuegosAgregados(listaFinal);
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No hay conexión a Internet");
        builder.setMessage("Por favor, conecte su dispositivo a Internet para continuar.");
        builder.setCancelable(false);
        builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                System.exit(0);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public void onBackPressed() {
        imagenVolver.performClick();

    }
}
