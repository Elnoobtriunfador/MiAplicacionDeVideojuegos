package com.example.miaplicaciondevideojuegos;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PantallaJuegosActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewVideojuegosAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantallajuegos); // Asegúrate de que el nombre del layout sea correcto

        /*recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Videojuego> listaVideojuegos = obtenerListaDeVideojuegos(); // Tu método para obtener la lista de videojuegos

        adaptador = new RecyclerViewVideojuegosAdapter(listaVideojuegos, this);
        recyclerView.setAdapter(adaptador);*/
    }

    private List<Videojuego> obtenerListaDeVideojuegos() {
        // Aquí obtienes la lista de videojuegos de tu base de datos o cualquier otra fuente
        // Asegúrate de que no sea nula ni esté vacía
        //return tuListaDeVideojuegos(); // Reemplaza con tu lógica
        return null;
    }
}
