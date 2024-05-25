package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PantallaPrincipalActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaprincipal);

        ImageButton imagenPerfil = findViewById(R.id.imagenPerfil);
        imagenPerfil.setOnClickListener(view -> {

                    Intent intent = new Intent(this, PantallaPerfilActivity.class);
                    startActivity(intent);

        });
    }
}