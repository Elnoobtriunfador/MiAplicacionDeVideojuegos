package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PantallaPrincipalActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageButton imagenPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallaprincipal);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            cargarImagenPerfil(user.getUid());
        }

        imagenPerfil = findViewById(R.id.imagenPerfil);
        imagenPerfil.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaPerfilActivity.class);
            startActivity(intent);
        });

        ImageView imagenTusJuegos = findViewById(R.id.imagenTusJuegos);
        imagenTusJuegos.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaTusJuegosActivity.class);
            startActivity(intent);
        });
        TextView textoMisJuegos = findViewById(R.id.textoMisJuegos);
        textoMisJuegos.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaTusJuegosActivity.class);
            startActivity(intent);
        });

        ImageView imagenTodosJuegos = findViewById(R.id.imagenTodosJuegos);
        imagenTodosJuegos.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaTodosLosJuegosActivity.class);
            startActivity(intent);
        });

        TextView textoTodosJuegos = findViewById(R.id.textoTodosJuegos);
        textoTodosJuegos.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaTodosLosJuegosActivity.class);
            startActivity(intent);
        });
    }

    private void cargarImagenPerfil(String userId) {
        DocumentReference userRef = db.collection("users").document(userId);
        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String imageUrl = documentSnapshot.getString("profileImageUrl");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    Picasso.get().load(imageUrl).into(imagenPerfil);
                }
            }
        });
    }
}