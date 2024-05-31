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

        CheckBox checkBoxEsperando = findViewById(R.id.checkBoxEsperando);
        CheckBox checkBoxJugando = findViewById(R.id.checkBoxJugando);
        CheckBox checkBoxCompletado = findViewById(R.id.checkBoxCompletado);
        CheckBox checkBoxAbandonado = findViewById(R.id.checkBoxAbandonado);
        CheckBox checkBoxCaratula = findViewById(R.id.checkBoxCaratula);
        CheckBox checkBoxManual = findViewById(R.id.checkBoxManual);
        CheckBox checkBoxJuego = findViewById(R.id.checkBoxJuego);
        CheckBox checkBoxExtras = findViewById(R.id.checkBoxExtras);

        Button guardarCambios = findViewById(R.id.buttonGuardarCambios);


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
            DocumentReference juegoRef = db.collection("juegos").document(juegoId);

            if(videojuego.getLoTengo()){

                juegoRef.update("Lo tengo", false)
                        .addOnSuccessListener(aVoid -> {
                            agregarColeccion.setText("Añadir a tu colección");
                            checkBoxEsperando.setVisibility(View.INVISIBLE);
                            checkBoxJugando.setVisibility(View.INVISIBLE);
                            checkBoxCompletado.setVisibility(View.INVISIBLE);
                            checkBoxAbandonado.setVisibility(View.INVISIBLE);
                            checkBoxCaratula.setVisibility(View.INVISIBLE);
                            checkBoxManual.setVisibility(View.INVISIBLE);
                            checkBoxJuego.setVisibility(View.INVISIBLE);
                            checkBoxExtras.setVisibility(View.INVISIBLE);
                            guardarCambios.setVisibility(View.INVISIBLE);
                            checkBoxEsperando.setChecked(false);
                            checkBoxJugando.setChecked(false);
                            checkBoxCompletado.setChecked(false);
                            checkBoxAbandonado.setChecked(false);
                            checkBoxCaratula.setChecked(false);
                            checkBoxManual.setChecked(false);
                            checkBoxJuego.setChecked(false);
                            checkBoxExtras.setChecked(false);
                            CustomToast.showToastShorter(this, "Juego eliminado de la colección", 1000);
                            videojuego.setLoTengo(false);
                            guardarCambiosEnFirestore();
                        })
                        .addOnFailureListener(e -> {
                            CustomToast.showToastShorter(this, "Error al agregar el juego", 1000);
                        });
            } else {

                juegoRef.update("Lo tengo", true)
                        .addOnSuccessListener(aVoid -> {
                            agregarColeccion.setText("Eliminar de la coleccion");
                            checkBoxEsperando.setChecked(false);
                            checkBoxJugando.setChecked(false);
                            checkBoxCompletado.setChecked(false);
                            checkBoxAbandonado.setChecked(false);
                            checkBoxCaratula.setChecked(false);
                            checkBoxManual.setChecked(false);
                            checkBoxJuego.setChecked(false);
                            checkBoxExtras.setChecked(false);
                            checkBoxEsperando.setVisibility(View.VISIBLE);
                            checkBoxJugando.setVisibility(View.VISIBLE);
                            checkBoxCompletado.setVisibility(View.VISIBLE);
                            checkBoxAbandonado.setVisibility(View.VISIBLE);
                            checkBoxCaratula.setVisibility(View.VISIBLE);
                            checkBoxManual.setVisibility(View.VISIBLE);
                            checkBoxJuego.setVisibility(View.VISIBLE);
                            checkBoxExtras.setVisibility(View.VISIBLE);
                            guardarCambios.setVisibility(View.VISIBLE);
                            CustomToast.showToastShorter(this, "Juego agregado a la colección", 1000);
                            videojuego.setLoTengo(true);
                            guardarCambiosEnFirestore();
                        })
                        .addOnFailureListener(e -> {
                            CustomToast.showToastShorter(this, "Error al agregar el juego", 1000);
                        });
            }
        });

        guardarCambios.setOnClickListener(view -> {


            guardarCambiosEnFirestore();
            CustomToast.showToastShorter(this, "Cambios guardados", 1000);
        });


        checkBoxEsperando.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean prevState = checkBoxEsperando.isChecked();
            videojuego.setEsperandoParaJugar(isChecked);
            gestionarCheckBoxes(checkBoxEsperando);
            checkBoxEsperando.setChecked(prevState);
        });

        checkBoxJugando.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean prevState = checkBoxJugando.isChecked();
            videojuego.setJugando(isChecked);
            gestionarCheckBoxes(checkBoxJugando);
            checkBoxJugando.setChecked(prevState);
        });

        checkBoxCompletado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean prevState = checkBoxCompletado.isChecked();
            videojuego.setCompletado(isChecked);
            gestionarCheckBoxes(checkBoxCompletado);
            checkBoxCompletado.setChecked(prevState);
        });


        checkBoxAbandonado.setOnCheckedChangeListener((buttonView, isChecked) -> {
            boolean prevState = checkBoxAbandonado.isChecked();
            videojuego.setAbandonado(isChecked);
            gestionarCheckBoxes(checkBoxAbandonado);
            checkBoxAbandonado.setChecked(prevState);
        });

        // Listeners para CheckBox

        checkBoxCaratula.setOnCheckedChangeListener((buttonView, isChecked) -> videojuego.setCaratulaObtenida(isChecked));

        checkBoxManual.setOnCheckedChangeListener((buttonView, isChecked) -> videojuego.setManualObtenido(isChecked));

        checkBoxJuego.setOnCheckedChangeListener((buttonView, isChecked) -> videojuego.setJuegoObtenido(isChecked));

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
                        videojuego.setLoTengo(documentSnapshot.getBoolean("Lo tengo"));



                        CheckBox checkBoxCaratula = findViewById(R.id.checkBoxCaratula);
                        CheckBox checkBoxManual = findViewById(R.id.checkBoxManual);
                        CheckBox checkBoxJuego = findViewById(R.id.checkBoxJuego);
                        CheckBox checkBoxExtras = findViewById(R.id.checkBoxExtras);
                        CheckBox checkBoxEsperando = findViewById(R.id.checkBoxEsperando);
                        CheckBox checkBoxJugando = findViewById(R.id.checkBoxJugando);
                        CheckBox checkBoxCompletado = findViewById(R.id.checkBoxCompletado);
                        CheckBox checkBoxAbandonado = findViewById(R.id.checkBoxAbandonado);
                        Button agregarColeccion = findViewById(R.id.buttonAgregarColeccion);
                        Button guardarCambios = findViewById(R.id.buttonGuardarCambios);
                        agregarColeccion.setText(videojuego.getLoTengo() ? "Eleminiar de tu colección" : " Añadir a tu colección");
                        checkBoxCaratula.setVisibility(videojuego.getLoTengo() ? View.VISIBLE : View.INVISIBLE);
                        checkBoxManual.setVisibility(videojuego.getLoTengo() ? View.VISIBLE : View.INVISIBLE);
                        checkBoxJuego.setVisibility(videojuego.getLoTengo() ? View.VISIBLE : View.INVISIBLE);
                        checkBoxExtras.setVisibility(videojuego.getLoTengo() ? View.VISIBLE : View.INVISIBLE);
                        checkBoxEsperando.setVisibility(videojuego.getLoTengo() ? View.VISIBLE : View.INVISIBLE);
                        checkBoxJugando.setVisibility(videojuego.getLoTengo() ? View.VISIBLE : View.INVISIBLE);
                        checkBoxCompletado.setVisibility(videojuego.getLoTengo() ? View.VISIBLE : View.INVISIBLE);
                        checkBoxAbandonado.setVisibility(videojuego.getLoTengo() ? View.VISIBLE : View.INVISIBLE);
                        guardarCambios.setVisibility(videojuego.getLoTengo() ? View.VISIBLE : View.INVISIBLE);


                        // CheckBoxes
                        checkBoxCaratula.setChecked(videojuego.isCaratulaObtenida());
                        checkBoxManual.setChecked(videojuego.isManualObtenido());
                        checkBoxJuego.setChecked(videojuego.isJuegoObtenido());
                        checkBoxExtras.setChecked(videojuego.isExtrasObtenidos());
                        checkBoxEsperando.setChecked(videojuego.isEsperandoParaJugar());
                        checkBoxJugando.setChecked(videojuego.isJugando());
                        checkBoxCompletado.setChecked(videojuego.isCompletado());
                        checkBoxAbandonado.setChecked(videojuego.isAbandonado());
                        agregarColeccion.setText(documentSnapshot.getBoolean("Lo tengo") ? "Eliminar de tu colección" : "Añadir a tu colección");
                        checkBoxCaratula.setVisibility(documentSnapshot.getBoolean("Lo tengo") ? View.VISIBLE : View.INVISIBLE);
                        checkBoxManual.setVisibility(documentSnapshot.getBoolean("Lo tengo") ? View.VISIBLE : View.INVISIBLE);
                        checkBoxJuego.setVisibility(documentSnapshot.getBoolean("Lo tengo") ? View.VISIBLE : View.INVISIBLE);
                        checkBoxExtras.setVisibility(documentSnapshot.getBoolean("Lo tengo") ? View.VISIBLE : View.INVISIBLE);
                        checkBoxEsperando.setVisibility(documentSnapshot.getBoolean("Lo tengo") ? View.VISIBLE : View.INVISIBLE);
                        checkBoxJugando.setVisibility(documentSnapshot.getBoolean("Lo tengo") ? View.VISIBLE : View.INVISIBLE);
                        checkBoxCompletado.setVisibility(documentSnapshot.getBoolean("Lo tengo") ? View.VISIBLE : View.INVISIBLE);
                        checkBoxAbandonado.setVisibility(documentSnapshot.getBoolean("Lo tengo") ? View.VISIBLE : View.INVISIBLE);
                        guardarCambios.setVisibility(documentSnapshot.getBoolean("Lo tengo") ? View.VISIBLE : View.INVISIBLE);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FIREBASE", "Error al cargar los datos del juego:", e);
                });
    }
}