package com.helpdesk.dto;

import com.helpdesk.model.Prioridad;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TicketRequest {
    @NotBlank
    private String titulo;

    @NotBlank
    private String descripcion;

    @NotNull
    private Prioridad prioridad;

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }
}
