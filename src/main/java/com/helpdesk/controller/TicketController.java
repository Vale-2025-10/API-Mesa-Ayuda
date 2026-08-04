package com.helpdesk.controller;

import com.helpdesk.dto.EstadoRequest;
import com.helpdesk.dto.TicketRequest;
import com.helpdesk.model.*;
import com.helpdesk.repository.TicketRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;

    public TicketController(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @PostMapping
    public ResponseEntity<?> crearTicket(@Valid @RequestBody TicketRequest request, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();

        Ticket ticket = new Ticket();
        ticket.setTitulo(request.getTitulo());
        ticket.setDescripcion(request.getDescripcion());
        ticket.setPrioridad(request.getPrioridad());
        ticket.setCreadoPor(usuario);

        Ticket guardado = ticketRepository.save(ticket);
        return ResponseEntity.status(HttpStatus.CREATED).body(guardado);
    }

    @GetMapping("/mios")
    public ResponseEntity<List<Ticket>> misTickets(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        return ResponseEntity.ok(ticketRepository.findByCreadoPor(usuario));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerTicket(@PathVariable Long id, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        Ticket ticket = ticketRepository.findById(id).orElse(null);

        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket no encontrado.");
        }

        if (usuario.getRol() == Rol.USUARIO && !ticket.getCreadoPor().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Acceso denegado.");
        }

        return ResponseEntity.ok(ticket);
    }

    @GetMapping
    public ResponseEntity<?> listarTodos(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        if (usuario.getRol() == Rol.USUARIO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Rol insuficiente.");
        }
        return ResponseEntity.ok(ticketRepository.findAll());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @Valid @RequestBody EstadoRequest request, Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        if (usuario.getRol() == Rol.USUARIO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Rol insuficiente.");
        }

        Ticket ticket = ticketRepository.findById(id).orElse(null);
        if (ticket == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket no encontrado.");
        }

        ticket.setEstado(request.getEstado());
        ticketRepository.save(ticket);
        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/vencidos")
    public ResponseEntity<?> ticketsVencidos(Authentication auth) {
        Usuario usuario = (Usuario) auth.getPrincipal();
        if (usuario.getRol() == Rol.USUARIO) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Rol insuficiente.");
        }

        List<Ticket> vencidos = ticketRepository.findBySlaVenceEnBeforeAndEstadoNot(LocalDateTime.now(), Estado.RESUELTO);
        return ResponseEntity.ok(vencidos);
    }
}
