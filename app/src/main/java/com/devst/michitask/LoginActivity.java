package com.devst.michitask;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    // Elementos de la pantalla
    private TextInputLayout tilCorreo, tilContrasena;
    private TextInputEditText etCorreo, etContrasena;
    private Button btnLogin;
    private ProgressBar progressLogin;

    // Objeto de Firebase que maneja las cuentas de usuario
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();

        // Conectamos cada variable con su elemento del XML
        tilCorreo = findViewById(R.id.tilCorreo);
        tilContrasena = findViewById(R.id.tilContrasena);
        etCorreo = findViewById(R.id.etCorreo);
        etContrasena = findViewById(R.id.etContrasena);
        btnLogin = findViewById(R.id.btnLogin);
        progressLogin = findViewById(R.id.progressLogin);
        TextView tvIrRegistro = findViewById(R.id.tvIrRegistro);

        btnLogin.setOnClickListener(v -> iniciarSesion());

        // Abre la pantalla para crear una cuenta nueva
        tvIrRegistro.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));
    }

    private void iniciarSesion() {
        String correo = textoDe(etCorreo).trim();
        String contrasena = textoDe(etContrasena);

        // 1. Si algún campo está mal, mostramos el error y no seguimos
        if (!validarCampos(correo, contrasena)) {
            return;
        }

        // 2. Mostramos la carga para que no presione el botón dos veces
        mostrarCarga(true);

        // 3. Firebase revisa si el correo y la contraseña son correctos
        auth.signInWithEmailAndPassword(correo, contrasena)
                .addOnCompleteListener(this, tarea -> {
                    mostrarCarga(false);

                    if (tarea.isSuccessful()) {
                        Toast.makeText(this, R.string.msg_login_ok, Toast.LENGTH_SHORT).show();
                        irAPantallaPrincipal();
                    } else {
                        mostrarErrorLogin(tarea.getException());
                    }
                });
    }

    // En el login solo revisamos que los campos no estén vacíos y el formato del correo.
    // Si la contraseña es correcta o no, eso lo decide Firebase.
    private boolean validarCampos(String correo, String contrasena) {
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

        if (contrasena.isEmpty()) {
            tilContrasena.setError(getString(R.string.error_contrasena_vacia));
            esValido = false;
        } else {
            tilContrasena.setError(null);
        }

        return esValido;
    }

    // Traduce el error de Firebase a un mensaje en español
    private void mostrarErrorLogin(Exception error) {
        int mensaje;

        if (error instanceof FirebaseAuthInvalidUserException) {
            // La cuenta no existe o fue deshabilitada desde la consola de Firebase
            String codigo = ((FirebaseAuthInvalidUserException) error).getErrorCode();
            mensaje = "ERROR_USER_DISABLED".equals(codigo)
                    ? R.string.error_cuenta_deshabilitada
                    : R.string.error_credenciales;
        } else if (error instanceof FirebaseAuthInvalidCredentialsException) {
            // Contraseña incorrecta. Por seguridad no decimos si falló el correo o la contraseña.
            mensaje = R.string.error_credenciales;
        } else if (error instanceof FirebaseTooManyRequestsException) {
            mensaje = R.string.error_demasiados_intentos;
        } else if (error instanceof FirebaseNetworkException) {
            mensaje = R.string.error_sin_conexion;
        } else {
            mensaje = R.string.error_login_general;
        }

        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }

    private void mostrarCarga(boolean cargando) {
        progressLogin.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!cargando);
    }

    private void irAPantallaPrincipal() {
        Intent intent = new Intent(this, MainActivity.class);
        // Borra las pantallas anteriores: con "atrás" no se vuelve al login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // Evita errores si el campo viene vacío (null)
    private String textoDe(TextInputEditText campo) {
        return campo.getText() == null ? "" : campo.getText().toString();
    }
}
