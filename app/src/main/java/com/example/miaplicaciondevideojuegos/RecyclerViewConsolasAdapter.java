package com.example.miaplicaciondevideojuegos;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewConsolasAdapter extends RecyclerView.Adapter<RecyclerViewConsolasAdapter.WordViewHolder> {

    private List<Plataforma> plataformas;
    private Context context;
    private int selectedPosition = -1;

    public RecyclerViewConsolasAdapter(List<Plataforma> plataformas, Context context) {
        this.plataformas = plataformas;
        this.context = context;
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewconsolas, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Plataforma plataforma = plataformas.get(position);
        holder.textViewPlataforma.setText(plataforma.getNombre());

        // Cambia el fondo del elemento seleccionado por defecto
        if (selectedPosition == position) {
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Maneja el clic en el elemento
        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition != position) {
                // Actualiza el índice del elemento seleccionado
                int previousSelectedPosition = selectedPosition;
                selectedPosition = position;

                // Notifica al adaptador de los cambios para reflejar la selección
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);

                // Notifica al oyente de la selección del nuevo elemento
                onItemSelected(plataforma);
            }
        });
    }

    @Override
    public int getItemCount() {
        return plataformas.size();
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPlataforma;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPlataforma = itemView.findViewById(R.id.textViewWord);
        }
    }

    // Interfaz para manejar la selección de un elemento
    public interface OnItemSelectedListener {
        void onItemSelected(Plataforma plataforma);
    }

    private OnItemSelectedListener listener;

    // Método para configurar el oyente de la selección del elemento
    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    // Método para notificar al oyente de la selección del elemento
    private void onItemSelected(Plataforma plataforma) {
        if (listener != null) {
            listener.onItemSelected(plataforma);
        }
    }

    // Método para seleccionar un elemento por defecto
    public void selectDefaultItem(int defaultPosition) {
        selectedPosition = defaultPosition;
        notifyDataSetChanged();
        // Notificar al oyente de la selección del elemento por defecto
        if (selectedPosition != -1) {
            onItemSelected(plataformas.get(selectedPosition));
        }
    }
}
