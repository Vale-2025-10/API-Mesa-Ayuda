package com.helpdesk.controller;

import com.helpdesk.model.Rol;
import com.helpdesk.model.Usuario;
import com.helpdesk.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UsuarioRepository usuarioRepository;

    public AdminController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/soporte")
    public ResponseEntity<?> ascenderASoporte(@RequestParam String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

        usuario.setRol(Rol.SOPORTE);
        usuarioRepository.save(usuario);
        return ResponseEntity.ok("Usuario ascendido a rol SOPORTE exitosamente.");
    }
}
