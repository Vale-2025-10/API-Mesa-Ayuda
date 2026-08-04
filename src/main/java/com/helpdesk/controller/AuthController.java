package com.helpdesk.controller;

import com.helpdesk.dto.*;
import com.helpdesk.model.*;
import com.helpdesk.repository.UsuarioRepository;
import com.helpdesk.security.JwtUtils;
import com.helpdesk.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UsuarioRepository usuarioRepository, PasswordEncoder encoder,
                          JwtUtils jwtUtils, RefreshTokenService refreshTokenService) {
        this.usuarioRepository = usuarioRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Error: El email ya esta registrado.");
        }

        Usuario usuario = new Usuario(
                request.getNombre(),
                request.getEmail(),
                encoder.encode(request.getPassword()),
                Rol.USUARIO
        );

        usuarioRepository.save(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente.");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail()).orElse(null);

        if (usuario == null || !encoder.matches(request.getPassword(), usuario.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales invalidas.");
        }

        String jwt = jwtUtils.generateJwtToken(usuario.getEmail(), usuario.getRol().name());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(usuario.getId());

        return ResponseEntity.ok(new AuthResponse(jwt, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUsuario)
                .map(usuario -> {
                    String token = jwtUtils.generateJwtToken(usuario.getEmail(), usuario.getRol().name());
                    return ResponseEntity.ok(new AuthResponse(token, requestRefreshToken));
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh Token invalido o revocado."));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Valid @RequestBody RefreshRequest request) {
        refreshTokenService.revocarToken(request.getRefreshToken());
        return ResponseEntity.ok("Sesion cerrada y Refresh Token revocado exitosamente.");
    }
}
