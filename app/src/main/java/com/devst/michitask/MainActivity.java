package com.devst.michitask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

        // Abrir formulario para crear un trabajo
        btnNuevoTrabajo.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    TrabajoActivity.class
            );

            startActivity(intent);
        });
    }

    // Vincular los componentes del XML
    private void inicializarComponentes() {

        btnNuevoTrabajo = findViewById(R.id.btnNuevoTrabajo);
        rvTrabajos = findViewById(R.id.rvTrabajos);
        tvContador = findViewById(R.id.tvContador);
        tvVacio = findViewById(R.id.tvVacio);
        progressTrabajos = findViewById(R.id.progressTrabajos);
    }

    // Configurar el listado de trabajos
    private void configurarRecyclerView() {

        adapter = new TrabajoAdapter();

        rvTrabajos.setLayoutManager(
                new LinearLayoutManager(this));

        rvTrabajos.setAdapter(adapter);
    }

    // Actualizar los trabajos al regresar a la pantalla
    @Override
    protected void onResume() {
        super.onResume();

        cargarTrabajos();
    }

    // READ: Consultar trabajos desde Firestore
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
}
