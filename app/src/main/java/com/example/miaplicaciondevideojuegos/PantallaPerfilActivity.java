package com.example.miaplicaciondevideojuegos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import androidx.appcompat.app.AppCompatActivity;

public class PantallaPerfilActivity extends AppCompatActivity {

    private Button botonCerrarSesion;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaperfil);

        ImageButton botonVolver = findViewById(R.id.botonVolver);
        botonVolver.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaPrincipalActivity.class);
            startActivity(intent);
            finish();
        });

        botonCerrarSesion = findViewById(R.id.botonCerrarSesion);
        botonCerrarSesion.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("recuerdame", false);
            editor.apply();

            mAuth.signOut();

            Intent intent = new Intent(this, PantallaLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}