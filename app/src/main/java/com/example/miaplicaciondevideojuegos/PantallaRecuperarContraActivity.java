package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class PantallaRecuperarContraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallarecuperarcontra);

        EditText editEmail = findViewById(R.id.textoRecuperarCorreo);
        Button botonEnviar = findViewById(R.id.botonEnviarCorreo);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        botonEnviar.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, escribe un correo electronico", Toast.LENGTH_SHORT).show();
                return;
            } else {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Email enviado", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, PantallaLoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "Email no encontrado", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        TextView textoIniciarSesion = findViewById(R.id.textoIniciarSesion);

        textoIniciarSesion.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaLoginActivity.class);
            startActivity(intent);
        });
    }
}
