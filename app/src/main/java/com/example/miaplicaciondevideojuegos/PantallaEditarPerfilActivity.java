package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

public class PantallaEditarPerfilActivity extends AppCompatActivity {

    private static final int SELECCIONAR_IMAGEN_REQUEST = 1;
    private static final int TOMAR_FOTO_REQUEST = 2;
    private ImageView imagenPerfil;
    private EditText editTextNombre, editTextBiografia;
    private Uri imagenUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String rutaArchivoTemporal;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaeditarperfil);

        imagenPerfil = findViewById(R.id.imagenPerfil);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextBiografia = findViewById(R.id.editTextBiografia);
        Button botonGuardar = findViewById(R.id.botonGuardarCambios);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            cargarDatosUsuario();
        }

        imagenPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarImagen();
            }
        });

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();
                // Cerrar la pantalla de perfil
                Intent intentCerrarPerfil = new Intent(PantallaEditarPerfilActivity.this, PantallaPerfilActivity.class);
                intentCerrarPerfil.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Cierra todas las actividades encima de la actividad de perfil
                startActivity(intentCerrarPerfil);
                // Abrir la actividad de perfil
                Intent intentAbrirPerfil = new Intent(PantallaEditarPerfilActivity.this, PantallaPerfilActivity.class);
                startActivity(intentAbrirPerfil);
            }
        });
    }

    private void cargarDatosUsuario() {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String name = documentSnapshot.getString("name");
                String bio = documentSnapshot.getString("bio");
                String imageUrl = documentSnapshot.getString("profileImageUrl");

                editTextNombre.setText(name);
                editTextBiografia.setText(bio);

                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(imagenPerfil);
                }
            }
        });
    }

    private void seleccionarImagen() {
        // Intent para seleccionar una imagen de la galería
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentGaleria.setType("image/*");

        // Intent para capturar una nueva imagen con la cámara
        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentCamara.resolveActivity(getPackageManager()) != null) {
            // Crear un archivo temporal para almacenar la imagen capturada por la cámara
            File archivoFoto = null;
            try {
                archivoFoto = crearArchivoTemporal();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (archivoFoto != null) {
                // Obtener la URI del archivo temporal para la imagen capturada
                imagenUri = FileProvider.getUriForFile(this, "com.example.miaplicaciondevideojuegos.fileprovider", archivoFoto);
                // Agregar la URI del archivo temporal al intent de la cámara
                intentCamara.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri);
            }
        }

        // Crear un Intent chooser que combine ambos intents (galería y cámara)
        Intent intentSeleccionarImagen = Intent.createChooser(intentGaleria, "Selecciona una imagen");
        intentSeleccionarImagen.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentCamara});

        // Iniciar la actividad para seleccionar la imagen
        startActivityForResult(intentSeleccionarImagen, SELECCIONAR_IMAGEN_REQUEST);
    }

    private File crearArchivoTemporal() throws IOException {
        String nombreArchivo = "imagen_perfil";
        File directorioAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File archivoImagen = File.createTempFile(nombreArchivo, ".jpg", directorioAlmacenamiento);
        rutaArchivoTemporal = archivoImagen.getAbsolutePath();
        return archivoImagen;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECCIONAR_IMAGEN_REQUEST) {
                if (data != null && data.getData() != null) {
                    imagenUri = data.getData();
                    imagenPerfil.setImageURI(imagenUri);
                } else {
                    // No se seleccionó ninguna imagen de la galería, por lo que puede ser una foto de la cámara
                    imagenPerfil.setImageURI(imagenUri);
                }
            } else if (requestCode == TOMAR_FOTO_REQUEST && resultCode == RESULT_OK) {
                // La foto fue capturada correctamente con la cámara
                imagenPerfil.setImageURI(imagenUri);
            }
        }
    }

    private void guardarCambios() {
        String nuevoNombre = editTextNombre.getText().toString();
        String nuevaBiografia = editTextBiografia.getText().toString();

        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("name", nuevoNombre, "bio", nuevaBiografia)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if (imagenUri != null) {
                            subirImagenAStorage(imagenUri);
                        } else {
                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PantallaEditarPerfilActivity.this, "Error al guardar los cambios", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void subirImagenAStorage(Uri imagenUri) {
        StorageReference imageRef = storageRef.child("user_images").child(userId);

        imageRef.putFile(imagenUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                guardarURLenFirestore(downloadUri.toString());
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PantallaEditarPerfilActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void guardarURLenFirestore(String downloadUrl) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("profileImageUrl", downloadUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Forzar la carga de la nueva imagen
                        cargarImagenPerfilNueva(downloadUrl);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PantallaEditarPerfilActivity.this, "Error al guardar la URL de la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void cargarImagenPerfilNueva(String imageUrl) {
        // Limpiar la caché de Picasso
        Picasso.get().invalidate(imageUrl);

        // Cargar la nueva imagen
        Picasso.get().load(imageUrl).into(imagenPerfil);
    }
}