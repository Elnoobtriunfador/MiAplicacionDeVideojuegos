package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PantallaJuegosActivity extends AppCompatActivity {

    private ImageView imageViewJuego;
    private TextView textViewNombreJuego;
    private TextView textViewPlataformas;
    private TextView textViewDesarrollador;
    private Videojuego videojuego;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallajuegos);

        videojuego = (Videojuego) getIntent().getSerializableExtra("Videojuego");
        Intent intent = getIntent();

        // Recuperar el valor int pasado en el Intent
        int claveInt = intent.getIntExtra("clave_int", 0);

        imageViewJuego = findViewById(R.id.imageViewJuego);
        textViewNombreJuego = findViewById(R.id.textViewNombreJuego);
        textViewPlataformas = findViewById(R.id.textViewPlataformas);
        textViewDesarrollador = findViewById(R.id.textViewDesarrollador);

        if (videojuego != null) {
            Glide.with(this).load(videojuego.getImagen()).into(imageViewJuego);
            textViewNombreJuego.setText(videojuego.getNombre());
            textViewDesarrollador.setText(videojuego.getDesarrollador());

            // Mostrar las plataformas en el TextView
            String plataformasTexto = TextUtils.join(", ", videojuego.getPlataformasNombres(videojuego.getPlataformas()));
            textViewPlataformas.setText(plataformasTexto);
        }

        cargarDatosDeFirestore();

        ImageButton imagenVolver = findViewById(R.id.imagenVolver);
        imagenVolver.setOnClickListener(view -> {
            if(claveInt == 1){
                Intent intent2 = new Intent(this, PantallaTusJuegosActivity.class);
                startActivity(intent2);
                finish();
            }else{
                Intent intent3 = new Intent(this, PantallaTodosLosJuegosActivity.class);
                startActivity(intent3);
                finish();
            }
        });

        Button agregarColeccion = findViewById(R.id.buttonAgregarColeccion);
        agregarColeccion.setOnClickListener(view -> {

            // Obtén el ID del juego que se quiere agregar
            String juegoId = videojuego.getId(); // Implementa esta función para obtener el ID

            // Actualiza el campo "tiene" a "true" en Firestore
            DocumentReference juegoRef = db.collection("juegos").document(juegoId);
            juegoRef.update("Lo tengo", true)
                    .addOnSuccessListener(aVoid -> {
                        // El juego se agregó correctamente a la colección
                        Toast.makeText(this, "Juego agregado a la colección", Toast.LENGTH_SHORT).show();
                        videojuego.setLoTengo(true);
                        agregarColeccion.setVisibility(View.INVISIBLE);
                        guardarCambiosEnFirestore();
                    })
                    .addOnFailureListener(e -> {
                        // Error al agregar el juego
                        Toast.makeText(this, "Error al agregar el juego", Toast.LENGTH_SHORT).show();
                    });
        });

        // Listeners para CheckBox
        CheckBox checkBoxEsperando = findViewById(R.id.checkBoxEsperando);
        checkBoxEsperando.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean prevState = checkBoxEsperando.isChecked();
            videojuego.setEsperandoParaJugar(isChecked);
            gestionarCheckBoxes(checkBoxEsperando);
            checkBoxEsperando.setChecked(prevState);
        });
        CheckBox checkBoxJugando = findViewById(R.id.checkBoxJugando);
        checkBoxJugando.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean prevState = checkBoxJugando.isChecked();
            videojuego.setJugando(isChecked);
            gestionarCheckBoxes(checkBoxJugando);
            checkBoxJugando.setChecked(prevState);
        });
        CheckBox checkBoxCompletado = findViewById(R.id.checkBoxCompletado);
        checkBoxCompletado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean prevState = checkBoxCompletado.isChecked();
            videojuego.setCompletado(isChecked);
            gestionarCheckBoxes(checkBoxCompletado);
            checkBoxCompletado.setChecked(prevState);
        });

        CheckBox checkBoxAbandonado = findViewById(R.id.checkBoxAbandonado);
        checkBoxAbandonado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean prevState = checkBoxAbandonado.isChecked();
            videojuego.setAbandonado(isChecked);
            gestionarCheckBoxes(checkBoxAbandonado);
            checkBoxAbandonado.setChecked(prevState);
        });

        // Listeners para CheckBox
        CheckBox checkBoxCaratula = findViewById(R.id.checkBoxCaratula);
        checkBoxCaratula.setOnCheckedChangeListener((buttonView, isChecked) -> videojuego.setCaratulaObtenida(isChecked));
        CheckBox checkBoxManual = findViewById(R.id.checkBoxManual);
        checkBoxManual.setOnCheckedChangeListener((buttonView, isChecked) -> videojuego.setManualObtenido(isChecked));
        CheckBox checkBoxJuego = findViewById(R.id.checkBoxJuego);
        checkBoxJuego.setOnCheckedChangeListener((buttonView, isChecked) -> videojuego.setJuegoObtenido(isChecked));
        CheckBox checkBoxExtras = findViewById(R.id.checkBoxExtras);
        checkBoxExtras.setOnCheckedChangeListener((buttonView, isChecked) -> videojuego.setExtrasObtenidos(isChecked));
    }

    private void gestionarCheckBoxes(CheckBox checkBox) {
        CheckBox checkBoxEsperando = findViewById(R.id.checkBoxEsperando);
        CheckBox checkBoxJugando = findViewById(R.id.checkBoxJugando);
        CheckBox checkBoxCompletado = findViewById(R.id.checkBoxCompletado);
        CheckBox checkBoxAbandonado = findViewById(R.id.checkBoxAbandonado);

        // Desmarcar todos los CheckBox excepto el que se ha marcado
        if (checkBox != checkBoxEsperando) {
            checkBoxEsperando.setChecked(false);
        }
        if (checkBox != checkBoxJugando) {
            checkBoxJugando.setChecked(false);
        }
        if (checkBox != checkBoxCompletado) {
            checkBoxCompletado.setChecked(false);
        }
        if (checkBox != checkBoxAbandonado) {
            checkBoxAbandonado.setChecked(false);
        }
    }

    private void guardarCambiosEnFirestore() {
        CollectionReference juegosRef = db.collection("juegos");

        // Obtén el ID del documento desde el objeto Videojuego
        String juegoId = videojuego.getId();

        // Crea un mapa con los datos del videojuego
        Map<String, Object> data = new HashMap<>();
        data.put("Esperando a jugar", videojuego.isEsperandoParaJugar());
        data.put("Jugando", videojuego.isJugando());
        data.put("Completado", videojuego.isCompletado());
        data.put("Abandonado", videojuego.isAbandonado());
        data.put("Caratula", videojuego.isCaratulaObtenida());
        data.put("Manual", videojuego.isManualObtenido());
        data.put("Juego", videojuego.isJuegoObtenido());
        data.put("Extras", videojuego.isExtrasObtenidos());
        data.put("Lo tengo", videojuego.getLoTengo());

        // Actualiza el documento del juego en Firestore
        juegosRef.document(juegoId).update(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FIREBASE", "Datos del juego actualizados correctamente.");
                })
                .addOnFailureListener(e -> {
                    Log.w("FIREBASE", "Error al actualizar los datos del juego:", e);
                });
    }

    // En la clase PantallaJuegosActivity, dentro del método onCreate
    private void cargarDatosDeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference juegosRef = db.collection("juegos");

        // Suponiendo que tienes un ID único para el juego
        String juegoId = videojuego.getId();

        juegosRef.document(juegoId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        videojuego.setEsperandoParaJugar(documentSnapshot.getBoolean("Esperando a jugar"));
                        videojuego.setJugando(documentSnapshot.getBoolean("Jugando"));
                        videojuego.setCompletado(documentSnapshot.getBoolean("Completado"));
                        videojuego.setAbandonado(documentSnapshot.getBoolean("Abandonado"));
                        videojuego.setCaratulaObtenida(documentSnapshot.getBoolean("Caratula"));
                        videojuego.setManualObtenido(documentSnapshot.getBoolean("Manual"));
                        videojuego.setJuegoObtenido(documentSnapshot.getBoolean("Juego"));
                        videojuego.setExtrasObtenidos(documentSnapshot.getBoolean("Extras"));


                        CheckBox checkBoxCaratula = findViewById(R.id.checkBoxCaratula);
                        CheckBox checkBoxManual = findViewById(R.id.checkBoxManual);
                        CheckBox checkBoxJuego = findViewById(R.id.checkBoxJuego);
                        CheckBox checkBoxExtras = findViewById(R.id.checkBoxExtras);
                        CheckBox checkBoxEsperando = findViewById(R.id.checkBoxEsperando);
                        CheckBox checkBoxJugando = findViewById(R.id.checkBoxJugando);
                        CheckBox checkBoxCompletado = findViewById(R.id.checkBoxCompletado);
                        CheckBox checkBoxAbandonado = findViewById(R.id.checkBoxAbandonado);
                        Button agregarColeccion = findViewById(R.id.buttonAgregarColeccion);
                        agregarColeccion.setVisibility(videojuego.getLoTengo() ? View.GONE : View.VISIBLE);


                        // CheckBoxes
                        checkBoxCaratula.setChecked(videojuego.isCaratulaObtenida());
                        checkBoxManual.setChecked(videojuego.isManualObtenido());
                        checkBoxJuego.setChecked(videojuego.isJuegoObtenido());
                        checkBoxExtras.setChecked(videojuego.isExtrasObtenidos());
                        checkBoxEsperando.setChecked(videojuego.isEsperandoParaJugar());
                        checkBoxJugando.setChecked(videojuego.isJugando());
                        checkBoxCompletado.setChecked(videojuego.isCompletado());
                        checkBoxAbandonado.setChecked(videojuego.isAbandonado());
                        agregarColeccion.setVisibility(documentSnapshot.getBoolean("Lo tengo") ? View.GONE : View.VISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FIREBASE", "Error al cargar los datos del juego:", e);
                });
    }
}