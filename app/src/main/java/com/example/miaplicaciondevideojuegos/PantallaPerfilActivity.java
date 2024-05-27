package com.example.miaplicaciondevideojuegos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;

public class PantallaPerfilActivity extends AppCompatActivity {

    private static final int PERMISO_CAMARA_REQUEST = 1;
    private static final int SELECCIONAR_IMAGEN_REQUEST = 2;
    private String rutaArchivoTemporal;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ImageView imagenPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaperfil);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        TextView textoNombreUsuario = findViewById(R.id.textoNombreUsuario);
        TextView textoMiembroDesdeUsuario = findViewById(R.id.textoMiembroDesdeUsuario);

        if (user != null) {
            String userId = user.getUid();
            DocumentReference userRef = db.collection("users").document(userId);
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    String name = documentSnapshot.getString("name");
                    textoNombreUsuario.setText(name);
                    int registrationYear = documentSnapshot.getLong("registrationYear").intValue();
                    int registrationMonth = documentSnapshot.getLong("registrationMonth").intValue();
                    int registrationDay = documentSnapshot.getLong("registrationDay").intValue();
                    textoMiembroDesdeUsuario.setText(registrationDay + " - " + registrationMonth + " - " + registrationYear);
                }
            });
        }

        ImageButton botonVolver = findViewById(R.id.botonVolver);
        botonVolver.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaPrincipalActivity.class);
            startActivity(intent);
            finish();
        });
        imagenPerfil = findViewById(R.id.imagenPerfil);
        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirSelectorDeImagen();
            }
        });

        Button botonCerrarSesion = findViewById(R.id.botonCerrarSesion);
        botonCerrarSesion.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("recuerdame", false);
            editor.apply();

            mAuth.signOut();

            Intent intent = new Intent(this, PantallaLoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void abrirSelectorDeImagen() {
        // Verificar si se tienen los permisos necesarios para la cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tienen permisos, solicitarlos al usuario
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISO_CAMARA_REQUEST);
        } else {
            // Si se tienen permisos, proceder a abrir el selector de imagen
            abrirSelector();
        }
    }

    // Método para abrir el selector de imagen
    private void abrirSelector() {
        // Crear un Intent para seleccionar una imagen de la galería
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Definir el tipo de contenido
        intentGaleria.setType("image/*");

        // Crear un Intent para abrir la cámara
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Verificar si hay una aplicación de cámara disponible
        if (intentCamara.resolveActivity(getPackageManager()) != null) {
            // Crear un archivo temporal para guardar la imagen capturada por la cámara
            File archivoFoto = null;
            try {
                archivoFoto = crearArchivoTemporal();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (archivoFoto != null) {
                // Obtener la URI del archivo temporal
                Uri uriArchivoFoto = FileProvider.getUriForFile(this,
                        "com.example.miaplicaciondevideojuegos.fileprovider", archivoFoto);
                // Pasar la URI como extra al Intent de la cámara
                intentCamara.putExtra(MediaStore.EXTRA_OUTPUT, uriArchivoFoto);
            }
        }

        // Crear un Intent para el selector de imágenes que ofrece al usuario las opciones de galería o cámara
        Intent intentSeleccionarImagen = Intent.createChooser(intentGaleria, "Selecciona una imagen");
        intentSeleccionarImagen.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentCamara});

        // Iniciar el Intent
        startActivityForResult(intentSeleccionarImagen, SELECCIONAR_IMAGEN_REQUEST);
    }

    // Método para manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_CAMARA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Si el permiso de cámara fue concedido, abrir el selector de imagen
                abrirSelector();
            } else {
                // Si el permiso de cámara fue denegado, mostrar un mensaje de que la funcionalidad no está disponible
                Toast.makeText(this, "La función de cámara no está disponible debido a la falta de permisos", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Método para manejar el resultado de la selección de la imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECCIONAR_IMAGEN_REQUEST) {
                if (data != null && data.getData() != null) {
                    // Obtiene la URI de la imagen seleccionada
                    Uri imagenUri = data.getData();
                    // Establece la imagen seleccionada en tu ImageView
                    imagenPerfil.setImageURI(imagenUri);
                } else {
                    // La imagen proviene de la cámara, carga la imagen desde la ruta del archivo temporal
                    Bitmap bitmap = BitmapFactory.decodeFile(rutaArchivoTemporal);
                    if (bitmap != null) {
                        imagenPerfil.setImageBitmap(bitmap);
                    } else {
                        // Handle bitmap loading failure
                    }
                }
            }
        }
    }

    // Método para crear un archivo temporal para la imagen capturada por la cámara
    private File crearArchivoTemporal() throws IOException {
        String nombreArchivo = "imagen_perfil";
        File directorioAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File archivoImagen = File.createTempFile(nombreArchivo, ".jpg", directorioAlmacenamiento);
        rutaArchivoTemporal = archivoImagen.getAbsolutePath(); // Guardar la ruta del archivo temporal
        return archivoImagen;
    }
}