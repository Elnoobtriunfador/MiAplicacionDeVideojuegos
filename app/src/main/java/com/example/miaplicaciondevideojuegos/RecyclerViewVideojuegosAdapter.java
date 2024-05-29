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

public class RecyclerViewVideojuegosAdapter extends RecyclerView.Adapter<RecyclerViewVideojuegosAdapter.VideojuegoViewHolder> {

    private List<Videojuego> listaVideojuegos;
    private List<Videojuego> listaFiltrada;
    private Context context;

    public RecyclerViewVideojuegosAdapter(List<Videojuego> listaVideojuegos, Context context) {
        this.listaVideojuegos = listaVideojuegos;
        this.listaFiltrada = new ArrayList<>(listaVideojuegos);
        this.context = context;
    }

    @NonNull
    @Override
    public VideojuegoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewslot, parent, false);
        return new VideojuegoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideojuegoViewHolder holder, int position) {
        Videojuego videojuego = listaFiltrada.get(position);

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
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaFiltrada.size();
    }

    public static class VideojuegoViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewPortada;
        TextView textViewTitulo;
        TextView textViewPlataformasJuego;

        public VideojuegoViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPortada = itemView.findViewById(R.id.imageViewPortada);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewPlataformasJuego = itemView.findViewById(R.id.textViewPlataformasJuego);
        }
    }

    public void filtrarJuegos(String texto) {
        if (texto.isEmpty()) {
            listaFiltrada = new ArrayList<>(listaVideojuegos);
        } else {
            List<Videojuego> listaFiltradaTemp = new ArrayList<>();
            for (Videojuego videojuego : listaVideojuegos) {
                if (videojuego.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltradaTemp.add(videojuego);
                }
            }
            listaFiltrada = listaFiltradaTemp;
        }
        notifyDataSetChanged();
    }
}