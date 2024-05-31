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
                CustomToast.showToastShorter(this, "Por favor, escribe un correo electronico", 1000);
                return;
            } else {
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CustomToast.showToastShorter(this, "Email enviado", 1000);
                        Intent intent = new Intent(this, PantallaLoginActivity.class);
                        startActivity(intent);
                    } else {
                        CustomToast.showToastShorter(this, "Email no encontrado", 1000);
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
