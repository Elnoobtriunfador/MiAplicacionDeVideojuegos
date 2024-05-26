package com.example.miaplicaciondevideojuegos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PantallaLoginActivity extends AppCompatActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallalogin);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        CheckBox checkboxRecuerdame = findViewById(R.id.checkboxRecuerdame);
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        boolean recuerdame = sharedPreferences.getBoolean("recuerdame", false);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (recuerdame && currentUser != null) {
            if (currentUser != null) {
                Toast.makeText(this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, PantallaPrincipalActivity.class);
                startActivity(intent);
                finish();
            }
        }

        Button botonIniciarSesion = findViewById(R.id.botonInicioSesion);
        botonIniciarSesion.setOnClickListener(view -> {

            EditText EditTextCorreo = findViewById(R.id.textoUsuarioInicioSesion);
            EditText EditTextContra = findViewById(R.id.textoContraseña);


            String correo = EditTextCorreo.getText().toString();
            String contra = EditTextContra.getText().toString();

            if (correo.isEmpty() || contra.isEmpty()) {

                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();

            } else {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, contra)
                        .addOnCompleteListener(task -> {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (task.isSuccessful()) {
                                if (checkboxRecuerdame.isChecked()) {
                                    SharedPreferences sharedPreferences2 = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences2.edit();
                                    editor.putBoolean("recuerdame", true);
                                    editor.apply();
                                } else {
                                    SharedPreferences sharedPreferences2 = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences2.edit();
                                    editor.putBoolean("recuerdame", false);
                                    editor.apply();
                                }
                                if (user != null) {
                                    Toast.makeText(this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, PantallaPrincipalActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                user = null;
                                if (user != null) {
                                    Toast.makeText(this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, PantallaPrincipalActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                Toast.makeText(this, "Correo o contraseña incorrecto", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        TextView textoRegistrarse = findViewById(R.id.textoRegistrarse);

        textoRegistrarse.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaRegistroActivity.class);
            startActivity(intent);
        });

        TextView textoContraseñaOlvidada = findViewById(R.id.textoContraseñaOlvidada);

        textoContraseñaOlvidada.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaRecuperarContraActivity.class);
            startActivity(intent);
        });
    }
}
