package com.devst.michitask;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.michitask.modelo.Trabajo;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class TrabajoAdapter extends
        RecyclerView.Adapter<TrabajoAdapter.TrabajoViewHolder> {

    private final List<Trabajo> trabajos = new ArrayList<>();

    public void actualizarTrabajos(List<Trabajo> nuevosTrabajos) {
        trabajos.clear();
        trabajos.addAll(nuevosTrabajos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TrabajoViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trabajo, parent, false);

        return new TrabajoViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TrabajoViewHolder holder, int position) {

        Trabajo trabajo = trabajos.get(position);

        holder.tvTitulo.setText(trabajo.getTitulo());
        holder.tvAsignatura.setText(trabajo.getAsignatura());
        holder.tvDescripcion.setText(trabajo.getDescripcion());

        holder.tvFecha.setText(
                "Entrega: " + trabajo.getFechaEntrega());

        holder.chipPrioridad.setText(trabajo.getPrioridad());
        holder.chipEstado.setText(trabajo.getEstado());

        // Colores según prioridad
        int colorPrioridad;

        if ("Alta".equals(trabajo.getPrioridad())) {
            colorPrioridad = Color.parseColor("#F4C2D7");
        } else if ("Baja".equals(trabajo.getPrioridad())) {
            colorPrioridad = Color.parseColor("#A8D5BA");
        } else {
            colorPrioridad = Color.parseColor("#EDE3FA");
        }

        holder.chipPrioridad.setChipBackgroundColor(
                ColorStateList.valueOf(colorPrioridad));

        // Colores según estado
        int colorEstado;

        if ("Completado".equals(trabajo.getEstado())) {
            colorEstado = Color.parseColor("#A8D5BA");
        } else if ("Pendiente".equals(trabajo.getEstado())) {
            colorEstado = Color.parseColor("#FCE8B5");
        } else {
            colorEstado = Color.parseColor("#EDE3FA");
        }

        holder.chipEstado.setChipBackgroundColor(
                ColorStateList.valueOf(colorEstado));
    }

    @Override
    public int getItemCount() {
        return trabajos.size();
    }

    public static class TrabajoViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvTitulo;
        TextView tvAsignatura;
        TextView tvDescripcion;
        TextView tvFecha;

        Chip chipPrioridad;
        Chip chipEstado;

        public TrabajoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvAsignatura = itemView.findViewById(R.id.tvAsignatura);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcion);
            tvFecha = itemView.findViewById(R.id.tvFecha);

            chipPrioridad = itemView.findViewById(R.id.chipPrioridad);
            chipEstado = itemView.findViewById(R.id.chipEstado);
        }
    }
}
