package com.helpdesk.service;

import com.helpdesk.model.RefreshToken;
import com.helpdesk.model.Usuario;
import com.helpdesk.repository.RefreshTokenRepository;
import com.helpdesk.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UsuarioRepository usuarioRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long usuarioId) {
        RefreshToken refreshToken = new RefreshToken();
        Usuario usuario = usuarioRepository.findById(usuarioId).orElseThrow();

        refreshToken.setUsuario(usuario);
        refreshToken.setExpiraEn(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setRevocado(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiraEn().isBefore(Instant.now()) || token.isRevocado()) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("El refresh token ha expirado o esta revocado.");
        }
        return token;
    }

    @Transactional
    public void revocarToken(String tokenStr) {
        findByToken(tokenStr).ifPresent(token -> {
            token.setRevocado(true);
            refreshTokenRepository.save(token);
        });
    }
}
