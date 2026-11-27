package com.devst.michitask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegistroActivity extends AppCompatActivity {

    // Elementos de la pantalla
    private TextInputLayout tilCorreo, tilContrasena, tilConfirmar;
    private TextInputEditText etCorreo, etContrasena, etConfirmar;
    private Button btnRegistrar;
    private ProgressBar progressRegistro;

    // Objeto de Firebase que maneja las cuentas de usuario
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        // Conectamos cada variable con su elemento del XML
        tilCorreo = findViewById(R.id.tilCorreo);
        tilContrasena = findViewById(R.id.tilContrasena);
        tilConfirmar = findViewById(R.id.tilConfirmar);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        etConfirmar = findViewById(R.id.etConfirmar);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        progressRegistro = findViewById(R.id.progressRegistro);

        btnRegistrar.setOnClickListener(v -> registrarUsuario());
    }

    private void registrarUsuario() {
        // Leemos lo que escribió el usuario (trim quita espacios al inicio y final)
        String correo = textoDe(etCorreo).trim();
        String contrasena = textoDe(etContrasena);
        String confirmar = textoDe(etConfirmar);

        // 1. Si algún campo está mal, mostramos el error y no seguimos
        if (!validarCampos(correo, contrasena, confirmar)) {
            return;
        }

        // 2. Mostramos la carga para que no presione el botón dos veces
        mostrarCarga(true);

        // 3. Firebase crea la cuenta. La contraseña la guarda Firebase de forma segura,
        //    nosotros NO la guardamos en ninguna parte.
        auth.createUserWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, tarea -> {
                    mostrarCarga(false);

                    if (tarea.isSuccessful()) {
                        // Al crear la cuenta, Firebase deja la sesión iniciada
                        Toast.makeText(this, R.string.msg_registro_ok, Toast.LENGTH_SHORT).show();
                        irAPantallaPrincipal();
                    } else {
                        mostrarErrorRegistro(tarea.getException());
                    }
                });
    }

    // Revisa los campos uno por uno y marca en rojo los que estén mal
    private boolean validarCampos(String correo, String contrasena, String confirmar) {
        boolean esValido = true;

        if (correo.isEmpty()) {
            tilCorreo.setError(getString(R.string.error_correo_vacio));
            esValido = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            tilCorreo.setError(getString(R.string.error_correo_invalido));
            esValido = false;
        } else {
            tilCorreo.setError(null);
        }

        // Firebase exige mínimo 6 caracteres
        if (contrasena.isEmpty()) {
            tilContrasena.setError(getString(R.string.error_contrasena_vacia));
            esValido = false;
        } else if (contrasena.length() < 6) {
            tilContrasena.setError(getString(R.string.error_contrasena_corta));
            esValido = false;
        } else {
            tilContrasena.setError(null);
        }

        if (confirmar.isEmpty()) {
            tilConfirmar.setError(getString(R.string.error_confirmar_vacia));
            esValido = false;
        } else if (!confirmar.equals(contrasena)) {
            tilConfirmar.setError(getString(R.string.error_contrasenas_distintas));
            esValido = false;
        } else {
            tilConfirmar.setError(null);
        }

        return esValido;
    }

    // Traduce el error de Firebase a un mensaje en español
    private void mostrarErrorRegistro(Exception error) {
        if (error instanceof FirebaseAuthUserCollisionException) {
            tilCorreo.setError(getString(R.string.error_correo_en_uso));
        } else if (error instanceof FirebaseAuthWeakPasswordException) {
            tilContrasena.setError(getString(R.string.error_contrasena_debil));
        } else if (error instanceof FirebaseAuthInvalidCredentialsException) {
            tilCorreo.setError(getString(R.string.error_correo_invalido));
        } else if (error instanceof FirebaseNetworkException) {
            Toast.makeText(this, R.string.error_sin_conexion, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.error_registro_general, Toast.LENGTH_LONG).show();
        }
    }

    private void mostrarCarga(boolean cargando) {
        progressRegistro.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnRegistrar.setEnabled(!cargando);
    }

    private void irAPantallaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        // Borra las pantallas anteriores: con "atrás" no se vuelve al registro
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Evita errores si el campo viene vacío (null)
    private String textoDe(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString();
    }
}
