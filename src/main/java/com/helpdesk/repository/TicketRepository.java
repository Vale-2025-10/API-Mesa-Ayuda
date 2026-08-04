package com.helpdesk.repository;

import com.helpdesk.model.Estado;
import com.helpdesk.model.Ticket;
import com.helpdesk.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreadoPor(Usuario usuario);
    List<Ticket> findBySlaVenceEnBeforeAndEstadoNot(LocalDateTime ahora, Estado estado);
}
