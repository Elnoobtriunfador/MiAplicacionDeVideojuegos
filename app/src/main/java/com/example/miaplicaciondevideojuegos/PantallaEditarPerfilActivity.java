package com.example.miaplicaciondevideojuegos;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.io.FileOutputStream;
import java.io.IOException;

public class PantallaEditarPerfilActivity extends AppCompatActivity {

    private static final int PERMISO_CAMARA_REQUEST = 1;
    private static final int SELECCIONAR_IMAGEN_REQUEST = 2;
    private ImageView imagenPerfil;
    private EditText editTextNombre, editTextBiografia;
    private Uri imagenUri;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private String rutaArchivoTemporal;
    private String userId;
    private Button botonGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaeditarperfil);

        if (!NetworkUtil.isConnectedToInternet(this)) {
            showNoInternetDialog();
        } else {
            setContentView(R.layout.activity_pantallaeditarperfil);
        }

        imagenPerfil = findViewById(R.id.imagenPerfil);
        editTextNombre = findViewById(R.id.editTextNombre);
        editTextBiografia = findViewById(R.id.editTextBiografia);
        botonGuardar = findViewById(R.id.botonGuardarCambios);

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
                abrirSelectorDeImagen();
            }
        });

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();
            }
        });
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
                        CustomToast.showToastShorter(PantallaEditarPerfilActivity.this, "Error al guardar los cambios", 1000);
                    }
                });

        // Abrir la actividad de perfil
        Intent intentAbrirPerfil = new Intent(PantallaEditarPerfilActivity.this, PantallaPerfilActivity.class);
        startActivity(intentAbrirPerfil);
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

    private void abrirSelectorDeImagen() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISO_CAMARA_REQUEST);
        } else {
            abrirSelector();
        }
    }

    private void abrirSelector() {
        Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intentGaleria.setType("image/*");

        Intent intentCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (intentCamara.resolveActivity(getPackageManager()) != null) {
            File archivoFoto = null;
            try {
                archivoFoto = crearArchivoTemporal();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (archivoFoto != null) {
                Uri uriArchivoFoto = FileProvider.getUriForFile(this, "com.example.miaplicaciondevideojuegos.fileprovider", archivoFoto);
                intentCamara.putExtra(MediaStore.EXTRA_OUTPUT, uriArchivoFoto);
            }
        }

        Intent intentSeleccionarImagen = Intent.createChooser(intentGaleria, "Selecciona una imagen");
        intentSeleccionarImagen.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intentCamara});

        startActivityForResult(intentSeleccionarImagen, SELECCIONAR_IMAGEN_REQUEST);
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_CAMARA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirSelector();
            } else {
                CustomToast.showToastShorter(this, "La función de cámara no está disponible debido a la falta de permisos", 1000);
            }
        }
    }

    private void procesarImagenSeleccionada(Uri imagenUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenUri);
            bitmap = corregirOrientacion(bitmap, imagenUri.getPath());
            imagenPerfil.setImageBitmap(bitmap);

            subirImagenAStorage(imagenUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECCIONAR_IMAGEN_REQUEST) {
                if (data != null && data.getData() != null) {
                    Uri imagenUri = data.getData();
                    procesarImagenSeleccionada(imagenUri);
                } else {
                    Uri imagenUri = Uri.fromFile(new File(rutaArchivoTemporal));
                    procesarImagenSeleccionada(imagenUri);
                }
            }
        }
    }

    private void subirImagenAStorage(Uri imagenUri) {
        StorageReference imageRef = storageRef.child("user_images").child(userId);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenUri);
            bitmap = corregirOrientacion(bitmap, imagenUri.getPath());

            File tempFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "temp_image.jpg");
            FileOutputStream outputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            Uri tempUri = Uri.fromFile(tempFile);
            UploadTask uploadTask = imageRef.putFile(tempUri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrlTask = taskSnapshot.getStorage().getDownloadUrl();
                    downloadUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri downloadUri) {
                            guardarURLenFirestore(downloadUri.toString());
                        }
                    });
                }}).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    CustomToast.showToastShorter(PantallaEditarPerfilActivity.this, "Error al subir la imagen", 1000);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            CustomToast.showToastShorter(this, "Error al procesar la imagen", 1000);
        }
    }

    private void guardarURLenFirestore(String downloadUrl) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("profileImageUrl", downloadUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Forzar la carga de la nueva imagen
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        CustomToast.showToastShorter(PantallaEditarPerfilActivity.this, "Error al guardar la URL de la imagen", 1000);
                    }
                });
    }

    private void cargarImagenPerfilNueva(String imageUrl) {
        // Limpiar la caché de Picasso
        Picasso.get().invalidate(imageUrl);

        // Cargar la nueva imagen
        Picasso.get().load(imageUrl).into(imagenPerfil);
    }

    private Bitmap corregirOrientacion(Bitmap bitmap, String imagePath) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateBitmap(bitmap, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateBitmap(bitmap, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateBitmap(bitmap, 270);
                default:
                    return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private File crearArchivoTemporal() throws IOException {
        String nombreArchivo = "imagen_perfil";
        File directorioAlmacenamiento = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File archivoImagen = File.createTempFile(nombreArchivo, ".jpg", directorioAlmacenamiento);
        rutaArchivoTemporal = archivoImagen.getAbsolutePath();
        return archivoImagen;
    }

    private void showNoInternetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No hay conexión a Internet");
        builder.setMessage("Por favor, conecte su dispositivo a Internet para continuar.");
        builder.setCancelable(false);
        builder.setPositiveButton("Salir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity(); // Cierra todas las actividades relacionadas con esta aplicación
                System.exit(0);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void onBackPressed() {
        botonGuardar.performClick();

    }
}