package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PantallaJuegosActivity extends AppCompatActivity {

    private ImageView imageViewJuego;
    private TextView textViewNombreJuego;
    private TextView textViewPlataformas;
    private TextView textViewDesarrollador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallajuegos);

        Videojuego videojuego = (Videojuego) getIntent().getSerializableExtra("Videojuego");

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

        ImageButton imagenVolver = findViewById(R.id.imagenVolver);
        imagenVolver.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaTodosLosJuegosActivity.class);
            startActivity(intent);
            finish();
        });
    }
}