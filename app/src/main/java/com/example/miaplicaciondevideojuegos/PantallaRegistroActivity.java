package com.example.miaplicaciondevideojuegos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class PantallaRegistroActivity extends AppCompatActivity{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaregistro);

        Button botonRegistro = findViewById(R.id.botonRegistrarse);
        botonRegistro.setOnClickListener(view -> {

            EditText editTextEmail = findViewById(R.id.textoCorreoRegistro);
            EditText editTextName = findViewById(R.id.textoUsuarioRegistro);
            EditText editTextPassword = findViewById(R.id.textoContraseñaRegistro);
            EditText editTextConfirmPassword = findViewById(R.id.textoConfirmarContraseñaRegistro);

            String email = editTextEmail.getText().toString();
            String name = editTextName.getText().toString();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            if (email.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {

                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();

            } else {

                if (password.equals(confirmPassword)){
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    String userId = user.getUid();
                                    DocumentReference userRef = db.collection("users").document(userId);
                                    Map<String, Object> userData = new HashMap<>();
                                    userData.put("name", name);
                                    userData.put("address", email);

                                    // Establecer los datos del usuario en Firestore
                                    userRef.set(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        // Datos del usuario establecidos correctamente
                                                    } else {
                                                        // Error al establecer los datos del usuario
                                                    }
                                                }
                                            });

                                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(this, PantallaLoginActivity.class);
                                    startActivity(intent);
                                } else {

                                    Toast.makeText(this, "Correo ya registrado", Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    Toast.makeText(this, "Las contraseñas deben coincidir", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView textoIniciarSesion = findViewById(R.id.textoIniciarSesion);

        textoIniciarSesion.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaLoginActivity.class);
            startActivity(intent);
        });
    }
}
