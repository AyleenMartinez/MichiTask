
package com.devst.michitask;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.devst.michitask.data.TrabajoRepository;
import com.devst.michitask.modelo.Trabajo;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Locale;

public class TrabajoActivity extends AppCompatActivity {

    private TextInputEditText etTitulo;
    private TextInputEditText etAsignatura;
    private TextInputEditText etDescripcion;

    private TextInputLayout tilTitulo;
    private TextInputLayout tilAsignatura;
    private TextInputLayout tilDescripcion;

    private TextView tvTituloFormulario;

    private Spinner spPrioridad;
    private Spinner spEstado;

    private Button btnFecha;
    private Button btnGuardar;
    private Button btnCancelar;

    private String fechaEntrega = "";
    private String idTrabajo;

    private boolean modoEdicion = false;

    private TrabajoRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SessionManager.protegerPantalla(this)) {
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_trabajo);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.formularioRoot),
                (v, insets) -> {

                    Insets barras = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars());

                    v.setPadding(
                            barras.left,
                            barras.top,
                            barras.right,
                            barras.bottom
                    );

                    return insets;
                });

        repository = new TrabajoRepository();

        inicializarComponentes();
        cargarOpciones();

        // Detectar si estamos creando o editando
        idTrabajo = getIntent().getStringExtra("idTrabajo");

        modoEdicion = idTrabajo != null && !idTrabajo.isEmpty();

        if (modoEdicion) {

            tvTituloFormulario.setText("Editar trabajo");
            btnGuardar.setText("Guardar cambios");

            if (savedInstanceState == null) {
                cargarDatosTrabajo();
            }
        }

        if (savedInstanceState != null) {

            fechaEntrega = savedInstanceState.getString(
                    "fechaEntrega", "");

            if (!fechaEntrega.isEmpty()) {
                btnFecha.setText(fechaEntrega);
            }
        }

        btnFecha.setOnClickListener(v -> seleccionarFecha());

        btnGuardar.setOnClickListener(v -> guardarTrabajo());

        btnCancelar.setOnClickListener(v -> finish());
    }

    private void inicializarComponentes() {

        tvTituloFormulario = findViewById(R.id.tvTituloFormulario);

        etTitulo = findViewById(R.id.etTitulo);
        etAsignatura = findViewById(R.id.etAsignatura);
        etDescripcion = findViewById(R.id.etDescripcion);

        tilTitulo = findViewById(R.id.tilTitulo);
        tilAsignatura = findViewById(R.id.tilAsignatura);
        tilDescripcion = findViewById(R.id.tilDescripcion);

        spPrioridad = findViewById(R.id.spPrioridad);
        spEstado = findViewById(R.id.spEstado);

        btnFecha = findViewById(R.id.btnFecha);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);
    }

    private void cargarOpciones() {

        String[] prioridades = {
                "Baja", "Media", "Alta"
        };

        String[] estados = {
                "Pendiente", "En progreso", "Completado"
        };

        ArrayAdapter<String> adapterPrioridad =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        prioridades);

        adapterPrioridad.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spPrioridad.setAdapter(adapterPrioridad);
        spPrioridad.setSelection(1);

        ArrayAdapter<String> adapterEstado =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_item,
                        estados);

        adapterEstado.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);

        spEstado.setAdapter(adapterEstado);
    }

    // Recuperar los datos del trabajo seleccionado
    private void cargarDatosTrabajo() {

        etTitulo.setText(
                getIntent().getStringExtra("titulo"));

        etAsignatura.setText(
                getIntent().getStringExtra("asignatura"));

        etDescripcion.setText(
                getIntent().getStringExtra("descripcion"));

        String fecha = getIntent().getStringExtra("fechaEntrega");

        if (fecha != null) {
            fechaEntrega = fecha;
            btnFecha.setText(fechaEntrega);
        }

        seleccionarOpcion(
                spPrioridad,
                getIntent().getStringExtra("prioridad"));

        seleccionarOpcion(
                spEstado,
                getIntent().getStringExtra("estado"));
    }

    private void seleccionarOpcion(Spinner spinner, String valor) {

        for (int i = 0; i < spinner.getCount(); i++) {

            if (spinner.getItemAtPosition(i)
                    .toString().equals(valor)) {

                spinner.setSelection(i);
                break;
            }
        }
    }

    private void seleccionarFecha() {

        Calendar calendario = Calendar.getInstance();

        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        if (!fechaEntrega.isEmpty()) {

            String[] partes = fechaEntrega.split("-");

            if (partes.length == 3) {
                anio = Integer.parseInt(partes[0]);
                mes = Integer.parseInt(partes[1]) - 1;
                dia = Integer.parseInt(partes[2]);
            }
        }

        DatePickerDialog dialogo = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    fechaEntrega = String.format(
                            Locale.US,
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            dayOfMonth
                    );

                    btnFecha.setText(fechaEntrega);
                },
                anio, mes, dia
        );

        dialogo.show();
    }

    private void guardarTrabajo() {

        String titulo = etTitulo.getText().toString().trim();
        String asignatura = etAsignatura.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        String prioridad = spPrioridad.getSelectedItem().toString();
        String estado = spEstado.getSelectedItem().toString();

        tilTitulo.setError(null);
        tilAsignatura.setError(null);
        tilDescripcion.setError(null);

        if (titulo.isEmpty()) {
            tilTitulo.setError("Ingresa el nombre del trabajo");
            etTitulo.requestFocus();
            return;
        }

        if (asignatura.isEmpty()) {
            tilAsignatura.setError("Ingresa la asignatura");
            etAsignatura.requestFocus();
            return;
        }

        if (descripcion.isEmpty()) {
            tilDescripcion.setError("Ingresa una descripción");
            etDescripcion.requestFocus();
            return;
        }

        if (fechaEntrega.isEmpty()) {

            Toast.makeText(this,
                    "Selecciona una fecha de entrega",
                    Toast.LENGTH_SHORT).show();

            return;
        }

        Trabajo trabajo = new Trabajo(
                titulo,
                asignatura,
                descripcion,
                fechaEntrega,
                prioridad,
                estado
        );

        btnGuardar.setEnabled(false);
        btnCancelar.setEnabled(false);
        btnGuardar.setText("Guardando...");

        Task<?> operacion;

        // Decidir si crear o actualizar
        if (modoEdicion) {

            operacion = repository.actualizarTrabajo(
                    idTrabajo, trabajo);

        } else {

            operacion = repository.crearTrabajo(trabajo);
        }

        operacion.addOnSuccessListener(resultado -> {

            String mensaje = modoEdicion
                    ? "Trabajo actualizado correctamente"
                    : "Trabajo guardado correctamente";

            Toast.makeText(this,
                    mensaje,
                    Toast.LENGTH_SHORT).show();

            finish();

        }).addOnFailureListener(e -> {

            btnGuardar.setEnabled(true);
            btnCancelar.setEnabled(true);

            btnGuardar.setText(modoEdicion
                    ? "Guardar cambios"
                    : "Guardar trabajo");

            Toast.makeText(this,
                    "Error al guardar: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putString("fechaEntrega", fechaEntrega);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isFinishing()) {
            SessionManager.protegerPantalla(this);
        }
    }
}
