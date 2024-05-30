package com.example.miaplicaciondevideojuegos;

import android.content.Context;
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
}
