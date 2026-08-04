package com.helpdesk.dto;

import com.helpdesk.model.Estado;
import jakarta.validation.constraints.NotNull;

public class EstadoRequest {
    @NotNull
    private Estado estado;

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}
