package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
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
                            if (task.isSuccessful()) {

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                                Toast.makeText(this, "Inicio de sesion exitoso", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(this, PantallaPrincipalActivity.class);
                                startActivity(intent);
                            } else {

                                Toast.makeText(this, "Error al iniciar sesion", Toast.LENGTH_SHORT).show();
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
