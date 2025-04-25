package com.reparalo.tutorialservice.controller;

import com.google.firebase.auth.FirebaseToken;
import com.reparalo.tutorialservice.dto.TutorialDTO;
import com.reparalo.tutorialservice.model.Tutorial;
import com.reparalo.tutorialservice.service.TutorialService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tutoriales")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Tutoriales", description = "API para gestión de tutoriales")
public class TutorialController {

    private final TutorialService tutorialService;

    @GetMapping("/public")
    @Operation(summary = "Obtener todos los tutoriales activos")
    public ResponseEntity<List<Tutorial>> obtenerTodosTutoriales() {
        List<Tutorial> tutoriales = tutorialService.obtenerTodosTutoriales();
        return ResponseEntity.ok(tutoriales);
    }

    @GetMapping("/public/categoria/{categoria}")
    @Operation(summary = "Obtener tutoriales por categoría")
    public ResponseEntity<List<Tutorial>> obtenerTutorialesPorCategoria(@PathVariable String categoria) {
        List<Tutorial> tutoriales = tutorialService.obtenerTutorialesPorCategoria(categoria);
        return ResponseEntity.ok(tutoriales);
    }

    @GetMapping("/public/dificultad/{dificultad}")
    @Operation(summary = "Obtener tutoriales por nivel de dificultad")
    public ResponseEntity<List<Tutorial>> obtenerTutorialesPorDificultad(@PathVariable String dificultad) {
        List<Tutorial> tutoriales = tutorialService.obtenerTutorialesPorDificultad(dificultad);
        return ResponseEntity.ok(tutoriales);
    }

    @GetMapping("/public/{id}")
    @Operation(summary = "Obtener un tutorial por ID")
    public ResponseEntity<Tutorial> obtenerTutorial(@PathVariable String id) {
        Tutorial tutorial = tutorialService.obtenerTutorial(id);
        if (tutorial != null) {
            return ResponseEntity.ok(tutorial);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/public/{id}/like")
    @Operation(summary = "Dar like a un tutorial")
    public ResponseEntity<Void> darLikeATutorial(@PathVariable String id) {
        boolean resultado = tutorialService.incrementarLikes(id);
        if (resultado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/tecnico")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @Operation(summary = "Crear un nuevo tutorial", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Tutorial> crearTutorial(
            @AuthenticationPrincipal Object principal,
            @Valid @RequestBody TutorialDTO tutorialDTO) {
        
        if (principal instanceof FirebaseToken) {
            FirebaseToken token = (FirebaseToken) principal;
            String uid = token.getUid();
            String nombre = token.getName();
            if (nombre == null) {
                nombre = (String) token.getClaims().getOrDefault("name", "Usuario");
            }
            
            Tutorial tutorial = mapearDtoAEntidad(tutorialDTO);
            Tutorial nuevoTutorial = tutorialService.crearTutorial(tutorial, uid, nombre);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTutorial);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PutMapping("/tecnico/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @Operation(summary = "Actualizar un tutorial existente", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Tutorial> actualizarTutorial(
            @PathVariable String id,
            @Valid @RequestBody TutorialDTO tutorialDTO) {
        
        Tutorial tutorial = mapearDtoAEntidad(tutorialDTO);
        Tutorial tutorialActualizado = tutorialService.actualizarTutorial(id, tutorial);
        
        if (tutorialActualizado != null) {
            return ResponseEntity.ok(tutorialActualizado);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/tecnico/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    @Operation(summary = "Eliminar un tutorial", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminarTutorial(@PathVariable String id) {
        boolean resultado = tutorialService.eliminarTutorial(id);
        if (resultado) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private Tutorial mapearDtoAEntidad(TutorialDTO dto) {
        return Tutorial.builder()
                .id(dto.getId())
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .contenido(dto.getContenido())
                .categoria(dto.getCategoria())
                .dificultad(dto.getDificultad())
                .imagenes(dto.getImagenes())
                .videoUrl(dto.getVideoUrl())
                .metadatos(dto.getMetadatos())
                .build();
    }
}