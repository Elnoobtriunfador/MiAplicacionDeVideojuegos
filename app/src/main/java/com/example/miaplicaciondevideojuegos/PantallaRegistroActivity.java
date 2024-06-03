package com.example.miaplicaciondevideojuegos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PantallaRegistroActivity extends AppCompatActivity{

    private TextView textoIniciarSesion;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaregistro);

        if (!NetworkUtil.isConnectedToInternet(this)) {
            showNoInternetDialog();
        } else {
            setContentView(R.layout.activity_pantallaregistro);
        }

        EditText editTextContraseña = findViewById(R.id.textoContraseñaRegistro);
        ImageView togglePasswordVisibility = findViewById(R.id.imageTogglePasswordVisibility);

        EditText editTextConfirmarContraseña = findViewById(R.id.textoConfirmarContraseñaRegistro);
        ImageView toggleConfirmPasswordVisibility = findViewById(R.id.imageToggleConfirmPasswordVisibility);

        togglePasswordVisibility.setOnClickListener(view -> {
            if (isPasswordVisible) {
                editTextContraseña.setInputType(129);
                togglePasswordVisibility.setImageResource(R.drawable.ojooculto);
            } else {
                editTextContraseña.setInputType(1);
                togglePasswordVisibility.setImageResource(R.drawable.ojo);
            }
            isPasswordVisible = !isPasswordVisible;
            editTextContraseña.setSelection(editTextContraseña.getText().length());
        });

        toggleConfirmPasswordVisibility.setOnClickListener(view -> {
            if (isConfirmPasswordVisible) {
                editTextConfirmarContraseña.setInputType(129);
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.ojooculto);
            } else {
                editTextConfirmarContraseña.setInputType(1);
                toggleConfirmPasswordVisibility.setImageResource(R.drawable.ojo);
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
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

                                    userRef.set(userData)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    } else {
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
