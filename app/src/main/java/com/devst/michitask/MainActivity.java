
package com.devst.michitask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devst.michitask.data.TrabajoRepository;
import com.devst.michitask.modelo.Trabajo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnNuevoTrabajo;
    private RecyclerView rvTrabajos;
    private TextView tvContador;
    private TextView tvVacio;
    private ProgressBar progressTrabajos;

    private TrabajoRepository repository;
    private TrabajoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {

                    Insets barras = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    int margen = (int) (24 *
                            getResources().getDisplayMetrics().density);

                    v.setPadding(
                            barras.left + margen,
                            barras.top + margen,
                            barras.right + margen,
                            barras.bottom + margen
                    );

                    return insets;
                });

        repository = new TrabajoRepository();

        inicializarComponentes();
        configurarRecyclerView();

        // CREATE: Abrir formulario vacío
        btnNuevoTrabajo.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    TrabajoActivity.class
            );

            startActivity(intent);
        });
    }

    private void inicializarComponentes() {

        btnNuevoTrabajo = findViewById(R.id.btnNuevoTrabajo);
        rvTrabajos = findViewById(R.id.rvTrabajos);
        tvContador = findViewById(R.id.tvContador);
        tvVacio = findViewById(R.id.tvVacio);
        progressTrabajos = findViewById(R.id.progressTrabajos);
    }

    private void configurarRecyclerView() {

        adapter = new TrabajoAdapter();

        rvTrabajos.setLayoutManager(
                new LinearLayoutManager(this));

        rvTrabajos.setAdapter(adapter);

        // Conectar las acciones de cada tarjeta
        adapter.setOnTrabajoListener(
                new TrabajoAdapter.OnTrabajoListener() {

                    @Override
                    public void onEditar(Trabajo trabajo) {
                        editarTrabajo(trabajo);
                    }

                    @Override
                    public void onEliminar(Trabajo trabajo) {
                        confirmarEliminacion(trabajo);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        cargarTrabajos();
    }

    // READ: Consultar trabajos
    private void cargarTrabajos() {

        progressTrabajos.setVisibility(View.VISIBLE);
        tvVacio.setVisibility(View.GONE);

        repository.obtenerTrabajos()
                .addOnSuccessListener(this, resultado -> {

                    List<Trabajo> trabajos =
                            resultado.toObjects(Trabajo.class);

                    adapter.actualizarTrabajos(trabajos);

                    int cantidad = trabajos.size();

                    tvContador.setText(
                            cantidad + (cantidad == 1
                                    ? " trabajo" : " trabajos"));

                    progressTrabajos.setVisibility(View.GONE);

                    if (trabajos.isEmpty()) {

                        tvVacio.setText(
                                "Todavía no tienes trabajos.\n" +
                                        "¡Agrega tu primer pendiente!");

                        tvVacio.setVisibility(View.VISIBLE);

                    } else {

                        tvVacio.setVisibility(View.GONE);
                    }
                })

                .addOnFailureListener(this, e -> {

                    progressTrabajos.setVisibility(View.GONE);

                    adapter.actualizarTrabajos(new ArrayList<>());
                    tvContador.setText("0 trabajos");

                    tvVacio.setText(
                            "No fue posible cargar los trabajos.");

                    tvVacio.setVisibility(View.VISIBLE);

                    Toast.makeText(this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }

    // UPDATE: Abrir formulario con datos existentes
    private void editarTrabajo(Trabajo trabajo) {

        if (trabajo.getId() == null ||
                trabajo.getId().isEmpty()) {

            Toast.makeText(this,
                    "No se encontró el ID del trabajo",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        Intent intent = new Intent(
                MainActivity.this,
                TrabajoActivity.class
        );

        intent.putExtra("idTrabajo", trabajo.getId());
        intent.putExtra("titulo", trabajo.getTitulo());
        intent.putExtra("asignatura", trabajo.getAsignatura());
        intent.putExtra("descripcion", trabajo.getDescripcion());
        intent.putExtra("fechaEntrega", trabajo.getFechaEntrega());
        intent.putExtra("prioridad", trabajo.getPrioridad());
        intent.putExtra("estado", trabajo.getEstado());

        startActivity(intent);
    }

    // Solicitar confirmación antes de eliminar
    private void confirmarEliminacion(Trabajo trabajo) {

        new AlertDialog.Builder(this)
                .setTitle("Eliminar trabajo")
                .setMessage(
                        "¿Estás segura de eliminar \"" +
                                trabajo.getTitulo() +
                                "\"?\n\nEsta acción no se puede deshacer.")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Eliminar", (dialog, which) -> {

                    eliminarTrabajo(trabajo);
                })
                .show();
    }

    // DELETE: Eliminar documento de Firestore
    private void eliminarTrabajo(Trabajo trabajo) {

        progressTrabajos.setVisibility(View.VISIBLE);

        repository.eliminarTrabajo(trabajo.getId())

                .addOnSuccessListener(this, resultado -> {

                    Toast.makeText(this,
                            "Trabajo eliminado correctamente",
                            Toast.LENGTH_SHORT).show();

                    cargarTrabajos();
                })

                .addOnFailureListener(this, e -> {

                    progressTrabajos.setVisibility(View.GONE);

                    Toast.makeText(this,
                            "Error al eliminar: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
    }
}
