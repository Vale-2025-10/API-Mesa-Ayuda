package com.helpdesk.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridad prioridad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ABIERTO;

    private LocalDateTime creadoEn;
    private LocalDateTime slaVenceEn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario creadoPor;

    public Ticket() {}

    @PrePersist
    public void alCrear() {
        this.creadoEn = LocalDateTime.now();
        this.calcularSla();
    }

    public void calcularSla() {
        if (this.prioridad == null) return;
        switch (this.prioridad) {
            case ALTA -> this.slaVenceEn = this.creadoEn.plusHours(4);
            case MEDIA -> this.slaVenceEn = this.creadoEn.plusHours(24);
            case BAJA -> this.slaVenceEn = this.creadoEn.plusHours(72);
        }
    }

    public boolean isVencido() {
        return this.estado != Estado.RESUELTO && LocalDateTime.now().isAfter(this.slaVenceEn);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getSlaVenceEn() { return slaVenceEn; }
    public void setSlaVenceEn(LocalDateTime slaVenceEn) { this.slaVenceEn = slaVenceEn; }

    public Usuario getCreadoPor() { return creadoPor; }
    public void setCreadoPor(Usuario creadoPor) { this.creadoPor = creadoPor; }
}
