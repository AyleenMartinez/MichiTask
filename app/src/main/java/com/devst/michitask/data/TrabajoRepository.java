
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

    // CREATE: Crear un trabajo
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

    // READ: Consultar los trabajos del usuario
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
}
