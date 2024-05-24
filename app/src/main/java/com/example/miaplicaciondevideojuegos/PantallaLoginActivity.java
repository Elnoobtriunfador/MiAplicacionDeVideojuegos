package com.example.miaplicaciondevideojuegos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PantallaLoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallalogin);

        TextView textoRegistrarse = findViewById(R.id.textoRegistrarse);

        textoRegistrarse.setOnClickListener(view -> {
            Intent intent = new Intent(this, PantallaRegistroActivity.class);
            startActivity(intent);
        });
    }
}
