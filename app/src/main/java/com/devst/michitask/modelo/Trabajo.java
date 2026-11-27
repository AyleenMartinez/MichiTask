
package com.devst.michitask.modelo;

import com.google.firebase.firestore.DocumentId;

public class Trabajo {

    private String id;
    private String titulo;
    private String asignatura;
    private String descripcion;
    private String fechaEntrega;
    private String prioridad;
    private String estado;

    public Trabajo() {
    }

    public Trabajo(String titulo, String asignatura,
                   String descripcion, String fechaEntrega,
                   String prioridad, String estado) {

        this.titulo = titulo;
        this.asignatura = asignatura;
        this.descripcion = descripcion;
        this.fechaEntrega = fechaEntrega;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    @DocumentId
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAsignatura() {
        return asignatura;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getFechaEntrega() {
        return fechaEntrega;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public String getEstado() {
        return estado;
    }
}
