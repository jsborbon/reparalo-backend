package com.reparalo.authservice.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.reparalo.authservice.dto.UsuarioDTO;
import com.reparalo.authservice.model.Usuario;
import com.reparalo.authservice.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Usuarios", description = "API para gestión de usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final FirebaseAuth firebaseAuth;

    @GetMapping("/perfil")
    @Operation(summary = "Obtener perfil del usuario autenticado", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Usuario> obtenerPerfilUsuario(@AuthenticationPrincipal Object principal) {
        if (principal instanceof com.google.firebase.auth.FirebaseToken) {
            String uid = ((com.google.firebase.auth.FirebaseToken) principal).getUid();
            Usuario usuario = usuarioService.obtenerUsuario(uid);
            if (usuario != null) {
                return ResponseEntity.ok(usuario);
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/perfil")
    @Operation(summary = "Crear o actualizar perfil de usuario", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Usuario> crearActualizarPerfil(
            @AuthenticationPrincipal Object principal,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {
        
        if (principal instanceof com.google.firebase.auth.FirebaseToken) {
            String uid = ((com.google.firebase.auth.FirebaseToken) principal).getUid();
            
            // Verificar si el usuario ya existe
            Usuario usuarioExistente = usuarioService.obtenerUsuario(uid);
            
            // Mapear DTO a entidad
            Usuario usuario = mapearDtoAEntidad(usuarioDTO);
            usuario.setUid(uid); // Asegurar que el UID sea el del token
            
            if (usuarioExistente == null) {
                // Crear nuevo usuario
                Usuario nuevoUsuario = usuarioService.crearUsuario(usuario);
                return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
            } else {
                // Actualizar usuario existente
                Usuario usuarioActualizado = usuarioService.actualizarUsuario(uid, usuario);
                return ResponseEntity.ok(usuarioActualizado);
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/perfil")
    @Operation(summary = "Actualizar perfil de usuario", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Usuario> actualizarPerfil(
            @AuthenticationPrincipal Object principal,
            @Valid @RequestBody UsuarioDTO usuarioDTO) {
        
        if (principal instanceof com.google.firebase.auth.FirebaseToken) {
            String uid = ((com.google.firebase.auth.FirebaseToken) principal).getUid();
            
            // Verificar si el usuario existe
            Usuario usuarioExistente = usuarioService.obtenerUsuario(uid);
            if (usuarioExistente == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Mapear DTO a entidad
            Usuario usuario = mapearDtoAEntidad(usuarioDTO);
            usuario.setUid(uid); // Asegurar que el UID sea el del token
            
            // Actualizar usuario
            Usuario usuarioActualizado = usuarioService.actualizarUsuario(uid, usuario);
            return ResponseEntity.ok(usuarioActualizado);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/admin/usuarios/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtener usuario por UID (solo admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Usuario> obtenerUsuarioPorUid(@PathVariable String uid) {
        Usuario usuario = usuarioService.obtenerUsuario(uid);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/admin/usuarios/{uid}/rol")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Actualizar rol de usuario (solo admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, String>> actualizarRolUsuario(
            @PathVariable String uid,
            @RequestParam String nuevoRol) {
        
        if (!nuevoRol.matches("CLIENTE|TECNICO|ADMIN")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no válido. Debe ser CLIENTE, TECNICO o ADMIN");
        }
        
        boolean actualizado = usuarioService.actualizarRol(uid, nuevoRol);
        
        if (actualizado) {
            try {
                // Actualizar custom claims en Firebase Auth
                Map<String, Object> claims = new HashMap<>();
                claims.put("role", nuevoRol);
                firebaseAuth.setCustomUserClaims(uid, claims);
                
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Rol actualizado correctamente");
                return ResponseEntity.ok(response);
            } catch (FirebaseAuthException e) {
                log.error("Error al actualizar claims en Firebase: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al actualizar claims en Firebase");
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/admin/usuarios/{uid}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario (solo admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Map<String, String>> eliminarUsuario(@PathVariable String uid) {
        boolean eliminado = usuarioService.eliminarUsuario(uid);
        
        if (eliminado) {
            try {
                // Eliminar usuario de Firebase Auth
                firebaseAuth.deleteUser(uid);
                
                Map<String, String> response = new HashMap<>();
                response.put("mensaje", "Usuario eliminado correctamente");
                return ResponseEntity.ok(response);
            } catch (FirebaseAuthException e) {
                log.error("Error al eliminar usuario en Firebase: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar usuario en Firebase");
            }
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/public/verificar")
    @Operation(summary = "Verificar estado del servicio de autenticación")
    public ResponseEntity<Map<String, String>> verificarServicio() {
        Map<String, String> response = new HashMap<>();
        response.put("estado", "activo");
        response.put("servicio", "auth-service");
        return ResponseEntity.ok(response);
    }

    private Usuario mapearDtoAEntidad(UsuarioDTO dto) {
        return Usuario.builder()
                .uid(dto.getUid())
                .email(dto.getEmail())
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .telefono(dto.getTelefono())
                .direccion(dto.getDireccion())
                .urlFoto(dto.getUrlFoto())
                .metadatos(dto.getMetadatos())
                .rol(dto.getRol())
                .build();
    }
}