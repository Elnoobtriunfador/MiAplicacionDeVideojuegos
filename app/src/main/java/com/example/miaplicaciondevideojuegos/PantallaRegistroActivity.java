package com.example.miaplicaciondevideojuegos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.Timestamp;

public class PantallaRegistroActivity extends AppCompatActivity{

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaregistro);

        EditText editTextContraseña = findViewById(R.id.textoContraseñaRegistro);
        ImageView togglePasswordVisibility = findViewById(R.id.imageTogglePasswordVisibility);

        EditText editTextConfirmarContraseña = findViewById(R.id.textoConfirmarContraseñaRegistro);
        ImageView toggleConfirmPasswordVisibility = findViewById(R.id.imageToggleConfirmPasswordVisibility);

        togglePasswordVisibility.setOnClickListener(view -> {
            if (isPasswordVisible) {
                // Ocultar contraseña
                editTextContraseña.setInputType(129); // 129 es el valor para "textPassword"
                togglePasswordVisibility.setImageResource(R.drawable.ojooculto);
            } else {
                // Mostrar contraseña
                editTextContraseña.setInputType(1); // 1 es el valor para "text"
                togglePasswordVisibility.setImageResource(R.drawable.ojo);
            }
            isPasswordVisible = !isPasswordVisible;
            // Colocar el cursor al final del texto
            editTextContraseña.setSelection(editTextContraseña.getText().length());
        });

        toggleConfirmPasswordVisibility.setOnClickListener(view -> {
            if (isConfirmPasswordVisible) {
                // Ocultar contraseña
                editTextConfirmarContraseña.setInputType(129); // 129 es el valor para "textPassword"
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.ojooculto);
            } else {
                // Mostrar contraseña
                editTextConfirmarContraseña.setInputType(1); // 1 es el valor para "text"
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.ojo);
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            // Colocar el cursor al final del texto
            editTextConfirmarContraseña.setSelection(editTextConfirmarContraseña.getText().length());
        });

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

                CustomToast.showToastShorter(this, "Por favor, complete todos los campos", 1000);

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
                                    Calendar calendar = Calendar.getInstance();
                                    int year = calendar.get(Calendar.YEAR);
                                    int month = calendar.get(Calendar.MONTH) + 1;
                                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                                    userData.put("registrationYear", year);
                                    userData.put("registrationMonth", month);
                                    userData.put("registrationDay", day);

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

                                    CustomToast.showToastShorter(this, "Registro exitoso", 1000);
                                    Intent intent = new Intent(this, PantallaLoginActivity.class);
                                    startActivity(intent);
                                } else {

                                    CustomToast.showToastShorter(this, "Correo ya registrado", 1000);
                                }
                            });
                } else {
                    CustomToast.showToastShorter(this, "Las contraseñas deben coincidir", 1000);
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
