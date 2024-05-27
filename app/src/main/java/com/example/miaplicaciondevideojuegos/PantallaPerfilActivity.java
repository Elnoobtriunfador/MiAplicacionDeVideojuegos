package com.example.miaplicaciondevideojuegos;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
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

public class PantallaPerfilActivity extends AppCompatActivity {

    private static final int PERMISO_CAMARA_REQUEST = 1;
    private static final int SELECCIONAR_IMAGEN_REQUEST = 2;
    private String rutaArchivoTemporal;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private ImageView imagenPerfil;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaperfil);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView textoNombreUsuario = findViewById(R.id.textoNombreUsuario);
        TextView textoMiembroDesdeUsuario = findViewById(R.id.textoMiembroDesdeUsuario);
        imagenPerfil = findViewById(R.id.imagenPerfil);

        if (user != null) {
            userId = user.getUid();
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
                    String imageUrl = documentSnapshot.getString("profileImageUrl");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        Picasso.get()
                                .load(imageUrl) // Carga la URL si está disponible
                                .placeholder(R.drawable.iconoperfil) // Imagen predeterminada
                                .into(imagenPerfil);
                    }
                }
            });
        }

        ImageButton botonVolver = findViewById(R.id.botonVolver);
        botonVolver.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaPrincipalActivity.class);
            startActivity(intent);
            finish();
        });

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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISO_CAMARA_REQUEST);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISO_CAMARA_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirSelector();
            } else {
                Toast.makeText(this, "La función de cámara no está disponible debido a la falta de permisos", Toast.LENGTH_SHORT).show();
            }
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
                    Toast.makeText(PantallaPerfilActivity.this, "Error al subir la imagen", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarURLenFirestore(String downloadUrl) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.update("profileImageUrl", downloadUrl)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PantallaPerfilActivity.this, "Imagen de perfil actualizada", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(PantallaPerfilActivity.this, "Error al actualizar la URL de la imagen", Toast.LENGTH_SHORT).show();
                    }
                });
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
}