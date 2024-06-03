package com.example.miaplicaciondevideojuegos;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PantallaLoginActivity extends AppCompatActivity{

    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallalogin);

        if (!NetworkUtil.isConnectedToInternet(this)) {
            showNoInternetDialog();
        } else {
            setContentView(R.layout.activity_pantallalogin);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        CheckBox checkboxRecuerdame = findViewById(R.id.checkboxRecuerdame);
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        boolean recuerdame = sharedPreferences.getBoolean("recuerdame", false);
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (recuerdame && currentUser != null) {
            if (currentUser != null) {
                CustomToast.showToastShorter(this, "Inicio de sesion exitoso", 1000);
                Intent intent = new Intent(this, PantallaPrincipalActivity.class);
                startActivity(intent);
                finish();
            }
        }

        EditText editTextContraseña = findViewById(R.id.textoContraseña);
        ImageView togglePasswordVisibility = findViewById(R.id.imageTogglePasswordVisibility);

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

        Button botonIniciarSesion = findViewById(R.id.botonInicioSesion);
        botonIniciarSesion.setOnClickListener(view -> {

            EditText EditTextCorreo = findViewById(R.id.textoUsuarioInicioSesion);
            EditText EditTextContra = findViewById(R.id.textoContraseña);


            String correo = EditTextCorreo.getText().toString();
            String contra = EditTextContra.getText().toString();

            if (correo.isEmpty() || contra.isEmpty()) {

                CustomToast.showToastShorter(this, "Por favor, complete todos los campos", 1000);

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
                                    CustomToast.showToastShorter(this, "Inicio de sesion exitoso", 1000);
                                    Intent intent = new Intent(this, PantallaPrincipalActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                user = null;
                                if (user != null) {
                                    CustomToast.showToastShorter(this, "Inicio de sesion exitoso", 1000);
                                    Intent intent = new Intent(this, PantallaPrincipalActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                CustomToast.showToastShorter(this, "Correo o contraseña incorrecto", 1000);
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
        finishAffinity();
        System.exit(0);
    }
}
