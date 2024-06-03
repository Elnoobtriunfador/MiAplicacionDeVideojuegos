package com.example.miaplicaciondevideojuegos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class PantallaRecuperarContraActivity extends AppCompatActivity {

    private TextView textoIniciarSesion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallarecuperarcontra);

        if (!NetworkUtil.isConnectedToInternet(this)) {
            showNoInternetDialog();
        } else {
            setContentView(R.layout.activity_pantallarecuperarcontra);
        }

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
        textoIniciarSesion = findViewById(R.id.textoIniciarSesion);

        textoIniciarSesion.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaLoginActivity.class);
            startActivity(intent);
        });
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
        textoIniciarSesion.performClick();

    }
}
