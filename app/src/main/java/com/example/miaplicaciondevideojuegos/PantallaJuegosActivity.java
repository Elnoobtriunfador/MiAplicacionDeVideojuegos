package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantallaJuegosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallajuegos);

        Videojuego videojuego = (Videojuego) getIntent().getSerializableExtra("Videojuego");

        ImageButton imagenVolver = findViewById(R.id.imagenVolver);
        imagenVolver.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaTodosLosJuegosActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
