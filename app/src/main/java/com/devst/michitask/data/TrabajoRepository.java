
package com.devst.michitask.data;

import com.devst.michitask.modelo.Trabajo;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class TrabajoRepository {

    private final FirebaseFirestore db =
            FirebaseFirestore.getInstance();

    private final FirebaseAuth auth =
            FirebaseAuth.getInstance();

    // CREATE: Crear trabajo
    public Task<DocumentReference> crearTrabajo(Trabajo trabajo) {

        FirebaseUser usuario = auth.getCurrentUser();

        if (usuario == null) {
            return Tasks.forException(
                    new IllegalStateException("No hay una sesión activa")
            );
        }

        return db.collection("usuarios")
                .document(usuario.getUid())
                .collection("trabajos")
                .add(trabajo);
    }

    // READ: Consultar trabajos
    public Task<QuerySnapshot> obtenerTrabajos() {

        FirebaseUser usuario = auth.getCurrentUser();

        if (usuario == null) {
            return Tasks.forException(
                    new IllegalStateException("No hay una sesión activa")
            );
        }

        return db.collection("usuarios")
                .document(usuario.getUid())
                .collection("trabajos")
                .orderBy("fechaEntrega")
                .get();
    }

    // UPDATE: Actualizar un trabajo existente
    public Task<Void> actualizarTrabajo(String id, Trabajo trabajo) {

        FirebaseUser usuario = auth.getCurrentUser();

        if (usuario == null) {
            return Tasks.forException(
                    new IllegalStateException("No hay una sesión activa")
            );
        }

        if (id == null || id.isEmpty()) {
            return Tasks.forException(
                    new IllegalArgumentException("ID de trabajo inválido")
            );
        }

        return db.collection("usuarios")
                .document(usuario.getUid())
                .collection("trabajos")
                .document(id)
                .update(
                        "titulo", trabajo.getTitulo(),
                        "asignatura", trabajo.getAsignatura(),
                        "descripcion", trabajo.getDescripcion(),
                        "fechaEntrega", trabajo.getFechaEntrega(),
                        "prioridad", trabajo.getPrioridad(),
                        "estado", trabajo.getEstado()
                );
    }

    // DELETE: Eliminar un trabajo
    public Task<Void> eliminarTrabajo(String id) {

        FirebaseUser usuario = auth.getCurrentUser();

        if (usuario == null) {
            return Tasks.forException(
                    new IllegalStateException("No hay una sesión activa")
            );
        }

        if (id == null || id.isEmpty()) {
            return Tasks.forException(
                    new IllegalArgumentException("ID de trabajo inválido")
            );
        }

        return db.collection("usuarios")
                .document(usuario.getUid())
                .collection("trabajos")
                .document(id)
                .delete();
    }
}
