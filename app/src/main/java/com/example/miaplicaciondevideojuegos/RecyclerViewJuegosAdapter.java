package com.example.miaplicaciondevideojuegos;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RecyclerViewJuegosAdapter extends RecyclerView.Adapter<RecyclerViewJuegosAdapter.JuegoViewHolder> {

    private List<Videojuego> juegos;
    private List<Videojuego> listaJuegosAgregados;
    private Context context;

    public RecyclerViewJuegosAdapter(List<Videojuego> juegos, Context context) {
        this.juegos = juegos;
        this.listaJuegosAgregados = new ArrayList<>(juegos);
        this.context = context;
    }

    @NonNull
    @Override
    public JuegoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewslot, parent, false);
        return new JuegoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JuegoViewHolder holder, int position) {
        Videojuego videojuego = listaJuegosAgregados.get(position);

        // Cargar la imagen usando Glide
        Glide.with(holder.itemView.getContext())
                .load(videojuego.getImagen())
                .into(holder.imageViewPortada);

        holder.textViewTitulo.setText(videojuego.getNombre());

        // Obtener los nombres de las plataformas
        List<String> plataformasNombres = videojuego.getPlataformasNombres(videojuego.getPlataformas());

        // Mostrar las plataformas en el TextView
        holder.textViewPlataformasJuego.setText(TextUtils.join(", ", plataformasNombres));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PantallaJuegosActivity.class);
                intent.putExtra("Videojuego", videojuego);
                intent.putExtra("clave_int", 1);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaJuegosAgregados.size();
    }

    public static class JuegoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPortada;
        TextView textViewTitulo;
        TextView textViewPlataformasJuego;

        public JuegoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPortada = itemView.findViewById(R.id.imageViewPortada);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewPlataformasJuego = itemView.findViewById(R.id.textViewPlataformasJuego);
        }
    }
    public void filtrarJuegosAgregados(List<Videojuego> juegos) {

        List<Videojuego> listaFiltradaTemp = new ArrayList<>();
        for (Videojuego juego : juegos) {
            if (juego.getLoTengo()) {
                listaFiltradaTemp.add(juego);
            }
        }
        listaJuegosAgregados = listaFiltradaTemp;
        notifyDataSetChanged();
    }

    public void actualizarLista(List<Videojuego> nuevaLista) {
        juegos.clear();
        juegos.addAll(nuevaLista);
        notifyDataSetChanged();
    }
}
