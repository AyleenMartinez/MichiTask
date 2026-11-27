package com.devst.michitask;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Reúne todo lo relacionado con la sesión del usuario en un solo lugar.
 *
 * Firebase guarda la sesión en el teléfono automáticamente: aunque se cierre la app,
 * el usuario sigue conectado hasta que llame a cerrarSesion().
 *
 * Los métodos son "static", así que se usan sin crear un objeto:
 *     SessionManager.haySesionActiva()
 */
public class SessionManager {

    // ¿Hay alguien con la sesión iniciada en este teléfono?
    public static boolean haySesionActiva() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    // UID del usuario conectado (o null si no hay sesión).
    // Es el identificador que usa Firestore: usuarios/{uid}/trabajos/{trabajoId}
    public static String obtenerUid() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        return usuario == null ? null : usuario.getUid();
    }

    // Correo del usuario conectado (o null si no hay sesión)
    public static String obtenerCorreo() {
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        return usuario == null ? null : usuario.getEmail();
    }

    /**
     * Protege una pantalla: si no hay sesión, manda al login y cierra la pantalla actual.
     * Se usa al inicio de onCreate:
     *
     *     if (!SessionManager.protegerPantalla(this)) return;
     *
     * Devuelve true si el usuario puede quedarse en la pantalla.
     */
    public static boolean protegerPantalla(Activity pantalla) {
        if (haySesionActiva()) {
            return true;
        }
        irAlLogin(pantalla);
        return false;
    }

    // Cierra la sesión en Firebase y vuelve al login
    public static void cerrarSesion(Activity pantalla) {
        FirebaseAuth.getInstance().signOut();
        irAlLogin(pantalla);
    }

    // Abre el login y borra las pantallas anteriores,
    // así el botón "atrás" no puede volver a una pantalla protegida
    private static void irAlLogin(Activity pantalla) {
        Intent intent = new Intent(pantalla, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        pantalla.startActivity(intent);
        pantalla.finish();
    }
}
