package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PantallaJuegosActivity extends AppCompatActivity {

    private ImageView imageViewJuego;
    private TextView textViewNombreJuego;
    private TextView textViewPlataformas;
    private TextView textViewDesarrollador;
    private Videojuego videojuego;
    private RadioButton radioButtonEsperando;
    private RadioButton radioButtonJugando;
    private RadioButton radioButtonCompletado;
    private RadioButton radioButtonAbandonado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallajuegos);

        videojuego = (Videojuego) getIntent().getSerializableExtra("Videojuego");

        imageViewJuego = findViewById(R.id.imageViewJuego);
        textViewNombreJuego = findViewById(R.id.textViewNombreJuego);
        textViewPlataformas = findViewById(R.id.textViewPlataformas);
        textViewDesarrollador = findViewById(R.id.textViewDesarrollador);
        radioButtonEsperando = findViewById(R.id.radioButtonEsperando);
        radioButtonJugando = findViewById(R.id.radioButtonJugando);
        radioButtonCompletado = findViewById(R.id.radioButtonCompletado);
        radioButtonAbandonado = findViewById(R.id.radioButtonAbandonado);

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




            // ALERTA ESTO NO VA A QUI, ES SOLO PARA PROBAR
            // IR EN UN BOTON DE GUARDAR CAMBIOS QUE TENDRAS SOLO CUANDO EL JUEGO ESTA EN TU COLECCION






            guardarCambiosEnFirestore();








            Intent intent = new Intent(this, PantallaTodosLosJuegosActivity.class);
            startActivity(intent);
            finish();
        });






        // Listeners para RadioGroupProgreso (RadioButton)
        ToggleableRadioGroup radioGroupProgreso = findViewById(R.id.RadioGroupProgreso);
        radioGroupProgreso.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case radioButtonEsperando:
                    videojuego.setEsperandoParaJugar(true);
                    videojuego.setJugando(false);
                    videojuego.setCompletado(false);
                    videojuego.setAbandonado(false);
                    break;
                case radioButtonJugando:
                    videojuego.setEsperandoParaJugar(false);
                    videojuego.setJugando(true);
                    videojuego.setCompletado(false);
                    videojuego.setAbandonado(false);
                    break;
                case radioButtonCompletado:
                    videojuego.setEsperandoParaJugar(false);
                    videojuego.setJugando(false);
                    videojuego.setCompletado(true);
                    videojuego.setAbandonado(false);
                    break;
                case radioButtonAbandonado:
                    videojuego.setEsperandoParaJugar(false);
                    videojuego.setJugando(false);
                    videojuego.setCompletado(false);
                    videojuego.setAbandonado(true);
                    break;
            }
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

    private void guardarCambiosEnFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference juegosRef = db.collection("juegos");

        // Obtén el ID del documento desde el objeto Videojuego
        String juegoId = videojuego.getId();

        // Crea un mapa con los datos del videojuego
        Map<String, Object> data = new HashMap<>();
        data.put("esperandoParaJugar", videojuego.isEsperandoParaJugar());
        data.put("jugando", videojuego.isJugando());
        data.put("completado", videojuego.isCompletado());
        data.put("abandonado", videojuego.isAbandonado());
        data.put("caratulaObtenida", videojuego.isCaratulaObtenida());
        data.put("manualObtenido", videojuego.isManualObtenido());
        data.put("juegoObtenido", videojuego.isJuegoObtenido());
        data.put("extrasObtenido", videojuego.isExtrasObtenidos());

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
                        videojuego.setEsperandoParaJugar(documentSnapshot.getBoolean("esperandoParaJugar"));
                        videojuego.setJugando(documentSnapshot.getBoolean("jugando"));
                        videojuego.setCompletado(documentSnapshot.getBoolean("completado"));
                        videojuego.setAbandonado(documentSnapshot.getBoolean("abandonado"));
                        videojuego.setCaratulaObtenida(documentSnapshot.getBoolean("caratulaObtenida"));
                        videojuego.setManualObtenido(documentSnapshot.getBoolean("manualObtenido"));
                        videojuego.setJuegoObtenido(documentSnapshot.getBoolean("juegoObtenido"));
                        videojuego.setExtrasObtenidos(documentSnapshot.getBoolean("extrasObtenido"));

                        // Actualiza los widgets con los datos recuperados
                        radioButtonEsperando.setChecked(videojuego.isEsperandoParaJugar());
                        radioButtonJugando.setChecked(videojuego.isJugando());
                        radioButtonCompletado.setChecked(videojuego.isCompletado());
                        radioButtonAbandonado.setChecked(videojuego.isAbandonado());

                        CheckBox checkBoxCaratula = findViewById(R.id.checkBoxCaratula);
                        CheckBox checkBoxManual = findViewById(R.id.checkBoxManual);
                        CheckBox checkBoxJuego = findViewById(R.id.checkBoxJuego);
                        CheckBox checkBoxExtras = findViewById(R.id.checkBoxExtras);

                        // CheckBoxes
                        checkBoxCaratula.setChecked(videojuego.isCaratulaObtenida());
                        checkBoxManual.setChecked(videojuego.isManualObtenido());
                        checkBoxJuego.setChecked(videojuego.isJuegoObtenido());
                        checkBoxExtras.setChecked(videojuego.isExtrasObtenidos());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("FIREBASE", "Error al cargar los datos del juego:", e);
                });
    }
}