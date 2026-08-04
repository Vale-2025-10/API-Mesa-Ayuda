package com.helpdesk.repository;

import com.helpdesk.model.RefreshToken;
import com.helpdesk.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUsuario(Usuario usuario);
}
